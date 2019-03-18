package per.hss.service;
import per.hss.dto.Exposer;
import per.hss.dto.SeckillExecution;
import per.hss.entity.Seckill;
import per.hss.exception.RepeatKillException;
import per.hss.exception.SeckillException;

import  java.util.List;
/**
 * 业务接口
 * 站在使用者的角度设计接口、
 * 方法定义粒度，参数，返回类型 （return 类型友好）
 */

public interface SeckillService {

    /**
     * 查询所有的秒杀记录
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     *当秒杀开启时输出秒杀接口地址
     * 否则输出系统时间和秒杀时间
     * @param seckillId
     */
    Exposer exportSeckillUrl (long  seckillId);

    /**
     * 执行秒杀操作
     * 执行秒杀可能会产生异常
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String  md5)
    throws SeckillException,RepeatKillException,SeckillException;

    /**
     * 执行秒杀存储过程的操作
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillException
     */
    SeckillExecution executeSeckillProce(long seckillId, long userPhone, String  md5);

}
