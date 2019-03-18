package per.hss.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import per.hss.dto.Exposer;
import per.hss.dto.SeckillExecution;
import per.hss.entity.Seckill;
import per.hss.exception.RepeatKillException;
import per.hss.exception.SeckillCloseException;

import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
//代表spring容器启动的时候，要加载哪些配置文件
@ContextConfiguration({"classpath:spring/spring-service.xml","classpath:spring/spring-dao.xml"})
public class SeckillServiceTest
{
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    //测试所有的参加秒杀的商品
    @Test
    public void getSeckillList() throws Exception
    {
        List<Seckill> list=seckillService.getSeckillList();
        logger.info("list={}",list);
    }

    //根据ID测试秒杀的商品
    @Test
    public void getById() throws Exception
    {
        long id=1001;
        Seckill seckill=seckillService.getById(id);
        logger.info("seckill={}",seckill);
    }

    //获取URL
    @Test
    public void exportSeckillUrl() throws Exception
    {
        long id=1000;
        Exposer exposer=seckillService.exportSeckillUrl(id);
        logger.info("exposer={}",exposer);
    }

    @Test
    public void executeSeckill() throws Exception
    {
        long id=1002;
        Exposer exposer=seckillService.exportSeckillUrl(id);
        if(exposer.isExposed())
        {
            logger.info("exposer={}",exposer);
            long phone=18310921106L;
            String md5=exposer.getMd5();

            try
            {
                SeckillExecution execution = seckillService.executeSeckill(id, phone, md5);
                logger.info("结果execution={}",execution);
            }
            catch(RepeatKillException e)
            {
                logger.error(e.getMessage());
            }
            catch(SeckillCloseException e)
            {
                logger.error(e.getMessage());
            }
        }else
        {
            //秒杀未开启
            logger.warn("exposer={}",exposer);
        }

    }

    @Test
    public void executeSeckillProce() throws Exception
    {
        long seckillId=1001;
        long phone=12121212121L;
        Exposer exposer=seckillService.exportSeckillUrl(seckillId);
        if(exposer.isExposed())
        {
            String md5=exposer.getMd5();
            SeckillExecution seckillExecution=seckillService.executeSeckillProce(seckillId,phone,md5);
            logger.info(seckillExecution.getStateInfo());

        }
    }
}