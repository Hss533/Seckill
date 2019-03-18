package per.hss.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import per.hss.entity.SuccessKilled;

import javax.annotation.Resource;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest
{

    @Resource
    private SuccessKilledDao successKilledDao;
    @Test
    public void insertSuccessKilles() throws Exception
    {
        long id=1000L;
        long phone=18292985971L;
        int insertCount=successKilledDao.insertSuccessKilles(id,phone);
        System.out.println("insertCount"+insertCount);
    }

    @Test
    public void queryWithSeckill() throws Exception
    {

        long id=1000L;
        long phone=18292985971L;
        SuccessKilled successKilled=successKilledDao.queryWithSeckill(id,phone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }

}