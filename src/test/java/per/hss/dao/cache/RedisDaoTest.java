package per.hss.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import per.hss.dao.SeckillDao;
import per.hss.entity.Seckill;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest  {

    private long id=1002;

    @Autowired
    private RedisDao redisDao;
    @Autowired
    private SeckillDao seckillDao;

    @Test
    public void sucessed()
    {
       /*
        Jedis jedis=new Jedis("127.0.0.1",6379);
        System.out.println(jedis.ping());
        */
        /*JedisPool jedisPool=new JedisPool("127.0.0.1",6379);
        Jedis jedis=jedisPool.getResource();
        jedis.select(0);
        System.out.println(jedis.get("redis"));
        jedis.close();
        jedisPool.close();*/
        /*
        Seckill seckill=seckillDao.queryById(id);
        System.out.println(redisDao.putSeckill(seckill));*/
        Seckill seckill=redisDao.getSeckill(id);
        if(seckill==null)
        {
            seckill=seckillDao.queryById(id);
            if(seckill!=null)
            {

                String suc=redisDao.putSeckill(seckill);
                System.out.println(suc);
                seckill=redisDao.getSeckill(id);
                System.out.println(seckill);
            }
            else {
                System.out.println("数据库操作失败");
            }
        }
        else {
            seckill=redisDao.getSeckill(id);
            System.out.println(seckill);
        }
    }
    @Test
    public void Success()
    {
        try {
            String host = "127.0.0.1";// 控制台显示访问IP地址
            int port = 6379;
            Jedis jedis = new Jedis(host, port);
            // 鉴权信息
            //jedis.auth("533533");// password
            String key = "redis";
            String value = "aliyun-redis";
            // select db默认为0
            jedis.select(0);
            // set一个key
            jedis.set(key, value);
            System.out.println("Set Key " + key + " Value: " + value);
            // get 设置进去的key
            String getvalue = jedis.get("suc");
            System.out.println("Get Key " + key + " ReturnValue: " + getvalue);
            jedis.quit();
            jedis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}