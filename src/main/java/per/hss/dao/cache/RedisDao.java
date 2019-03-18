package per.hss.dao.cache;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import per.hss.entity.Seckill;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * redis 使用对象
 */
public class RedisDao {

    private final JedisPool  jedisPool;

    public RedisDao(String ip, int port)
    {
        jedisPool=new JedisPool(ip,port);//简单的配置
        System.out.println(jedisPool.getResource().ping());
    }
    private RuntimeSchema<Seckill> schema=RuntimeSchema.createFrom(Seckill.class);
    //通过反射可以拿到字节码有什么属性 有什么方法 这个就是基于SecKill做一个模式  当创建一个对象的时候，会根据相应的模式 赋予
    //相应的值
    private final Logger logger=LoggerFactory.getLogger(this.getClass());


    //通过redis拿到seckill对象
    public Seckill getSeckill(long seckillId)
    {
        try{
                Jedis jedis=jedisPool.getResource();
                try
                {
                    String key="seckill:"+seckillId;
                    //并没有实现内部序列化操作
                    //get ->byte[]二进制数组->反序列化->Object(Seckill)
                    //其实可以采用implements Seralized 但是效率会降低 Java内置序列化有点慢
                    //采用自定义序列化
                    //protostuff : pojo 有get set方法的标准的Java对象
                    byte[] bytes=jedis.get(key.getBytes());//传递字节数组
                    if(bytes!=null)
                    {
                        //空对象 获取到了
                        Seckill seckill=schema.newMessage();
                        ProtobufIOUtil.mergeFrom(bytes,seckill,schema);
                        //这个操作完之后，SecKill就会被反序列化
                        return  seckill;
                    }
                } finally
                {
                    jedis.close();
                }
        }
        catch(Exception e)
        {
            logger.info(e.getMessage(),e);
        }

        return null;
    }

    public void success()
    {
        Jedis jedis=jedisPool.getResource();
        try {
            System.out.println(jedis.get("redis"));
        }finally {
            jedis.close();
        }
    }
    //当发现缓存没有的时候put一个
    // 将seckill对象传递到redis数组中去
    public String putSeckill(Seckill seckill)
    {
        //把对象序列化成字符
        try
        {
            Jedis jedis=jedisPool.getResource();
            try
            {
                String key="seckill:"+seckill.getSeckillId();
                byte[] bytes=ProtobufIOUtil.toByteArray(seckill,schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //linkedBuffer是缓存器
                int timout=60*60;//1 hour
                String result=jedis.setex(key.getBytes(),timout,bytes);
                return  result;
            }finally
            {
                jedis.close();
            }

        }catch(Exception e)
        {
            //打印日志
            logger.error(e.getMessage(),e);
        }
        return null;
    }
}
