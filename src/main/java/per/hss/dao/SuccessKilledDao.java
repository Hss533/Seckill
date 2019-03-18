package per.hss.dao;

import org.apache.ibatis.annotations.Param;
import per.hss.entity.SuccessKilled;

public interface SuccessKilledDao
{
    /**
     * 插入购买明细，可过滤重复
     * @param seckillId
     * @param userPhone
     * @return
     */
    int  insertSuccessKilles(@Param("seckillId")long seckillId,@Param("userPhone")long userPhone);

    /**
     * 根据Id查询successKilled并携带秒杀产品对象实体
     * @param seckillId
     * @return
     */
    SuccessKilled queryWithSeckill(@Param("seckillId") long seckillId,@Param("userPhone") long userPhone);
}
