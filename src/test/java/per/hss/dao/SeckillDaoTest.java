package per.hss.dao;

import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import per.hss.entity.Seckill;

import javax.annotation.Resource;


/**
 * 配置spring和Junit整合
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    @Resource
    private SeckillDao seckillDao;

    @Test
    public void reduceNumber() throws Exception
    {
        Date killTime=new Date();
        int updateCount=seckillDao.reduceNumber(1000L,killTime);
        System.out.println("********updateCount="+updateCount+"******");
    }

    @Test
    public void queryById() throws Exception {
        long id=1000;
        Seckill seckill=seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
    }

    /**
     * java 没有保存形参的记录 queryAll(args0,args1)
     * @throws Exception
     */
    @Test
    public void queryAll() throws Exception
    {
        List<Seckill> seckillList=seckillDao.queryAll(0,100);

        for (Seckill seckill:seckillList)
        {
            System.out.println(seckill);
        }
    }

}