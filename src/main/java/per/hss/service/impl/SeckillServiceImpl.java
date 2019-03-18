package per.hss.service.impl;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import per.hss.dao.SeckillDao;
import per.hss.dao.SuccessKilledDao;
import per.hss.dao.cache.RedisDao;
import per.hss.dto.Exposer;
import per.hss.dto.SeckillExecution;
import per.hss.entity.Seckill;
import per.hss.entity.SuccessKilled;
import per.hss.enums.SeckillStateEnum;
import per.hss.exception.RepeatKillException;
import per.hss.exception.SeckillCloseException;
import per.hss.exception.SeckillException;
import per.hss.service.SeckillService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    //注入service依赖
    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    private final String slat="aksjhdka&)*shd&*^#asd*&@hdaksf";
    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    /**
     * 这个是需要进行优化的
     * 是秒杀地址暴露接口
     * @param seckillId
     * @return
     */
    @Override
    public Exposer exportSeckillUrl(long seckillId) {

        //秒杀时优化这个 redis 缓存这个
        /**
         * get from cache
         * if null
         * get db
         *else
         * put cache
         * locgoin
         * 使用这种方法很容易把这个操作写到业务逻辑中
         * 应该方法哦dao层中
         */
        //使用redis进行缓存优化
        /**
         *
         */
        Seckill seckill=redisDao.getSeckill(seckillId);
        if(seckill==null)
        {
            seckill=seckillDao.queryById(seckillId);
            if (seckill==null)
            {
                return  new Exposer(false,seckillId);
            }else {
                redisDao.putSeckill(seckill);

            }
        }
        if(seckill==null)
        {
            return new Exposer(false,seckillId);
        }
        Date startTime=seckill.getStartTime();
        Date endTime=seckill.getEndTime();
        Date nowTime=new Date();
        if(nowTime.getTime()<startTime.getTime()||nowTime.getTime()>endTime.getTime())
        {
            return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
        }

        //转化特定字符串的过程，不可逆
        String md5=getMD5(seckillId);
        return new Exposer(true,md5,seckillId);
    }

    private String getMD5(long seckillId)
    {
        String base=seckillId+"/"+slat;
        String  MD5= DigestUtils.md5DigestAsHex(base.getBytes());
        return  MD5;
    }
    @Override
    @Transactional
    /**
     * 使用注解控制事物方法的优点；
     * 1.开发团队达成一致约定，明确标注事务方法的编程风格
     * 2.保证事务方法的执行时间尽可能短，不要穿插其他的网络操作HTTP等 或者剥离到事物方法外部
     * 3.不是所有的方法都需要事物，如只有一条修改操作，只读操作不需要事物控制
     *
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillException
    {

       if(md5==null||!getMD5(seckillId).equals(md5))
       {
            throw new SeckillException("秒杀的信息重写");
       }

        try {
                //记录购买行为
                int insertCount=successKilledDao.insertSuccessKilles(seckillId,userPhone);
                if(insertCount<=0)
                {
                    //重复秒杀
                    throw new RepeatKillException("重复进行秒杀");
                }
                else
                {
                    //减库存  热点商品竞争
                    int updateCount=seckillDao.reduceNumber(seckillId,new Date());
                    if(updateCount<=0)
                    {
                        //没有更新到记录
                        throw new SeckillCloseException("秒杀活动已经结束");
                    }
                    else
                    {
                        SuccessKilled successKilled=successKilledDao.queryWithSeckill(seckillId,userPhone);
                        return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS,successKilled);
                    }

                }

            }
            catch (SeckillCloseException e1)
            {
                throw  e1;
            }
            catch (RepeatKillException e2)
            {
                throw e2;
            }
            catch (Exception e)
            {
                logger.error(e.getMessage(),e);
                //将所有的编译器异常转换为运行期异常 spring
                throw  new SeckillException("seckill inner error"+e.getMessage());
            }

    }

    //Java客户端调用存储过程
    @Override
    public SeckillExecution executeSeckillProce(long seckillId, long userPhone, String md5)
    {
        if(md5==null||!getMD5(seckillId).equals(md5))
        {
            return new SeckillExecution(seckillId,SeckillStateEnum.DATA_REWRITE);
        }
        Date killTime=new Date();
        Map<String,Object> map=new HashMap<>();
        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);
        try
        {
            seckillDao.killByProc(map);
            int result=MapUtils.getInteger(map,"result",-2);
            if(result==1)
            {
                //chjenggong
                SuccessKilled successKilled=successKilledDao.queryWithSeckill(seckillId,userPhone);
                return new SeckillExecution(seckillId,SeckillStateEnum.SUCCESS,successKilled);
            }
            else {
                return new SeckillExecution(seckillId,SeckillStateEnum.stateOf(result));
            }
        }catch (Exception e)
        {
            logger.error(e.getMessage(),e);
            return new SeckillExecution(seckillId,SeckillStateEnum.INNER_ERROR);
        }
    }
}
