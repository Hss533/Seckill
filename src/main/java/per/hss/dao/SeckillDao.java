package per.hss.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import per.hss.entity.Seckill;
import java.util.List;
import java.util.Date;
import java.util.Map;

@Component
public interface SeckillDao
{
    /**
     * 减库存
     * @param seckillId
     * @param killTime
     * @return
     */
    int reduceNumber(@Param("seckillId") long seckillId,@Param("killTime") Date killTime);

    /**
     * 根据id查找秒杀对象
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     * 根据偏移量查询
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param("offset") int offset,@Param("limit") int limit);
    void killByProc(Map<String,Object> paramMap);
}
