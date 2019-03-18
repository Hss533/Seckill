-- 秒杀执行存储过程
DELIMITER  $$ -- 改变换行符
CREATE PROCEDURE 'execute_seckill'
  (in v_seckill_id bigint,in v_phone bigint,in v_kill_time timestamp,out r_result int)
BEGIN
  DECLARE  insert_count INT DEFAULT  0;
  START TRANSACTION ;
  INSERT IGNORE  INTO  success_killed (seckill_id, user_phone,create_time) VALUES (v_seckill_id,v_phone,v_kill_time);
  SELECT row_count() INTO insert_count;
  IF (insert_count=0) THEN
    ROLLBACK;
    SET r_result=-1;
  ELSEIF (insert_count<0) THEN
    ROLLBACK ;
    SEt r_result=-2;
  ELSE
    UPDATE seckill set number=number-1 WHERE seckill_id=v_seckill_id AND end_time>v_kill_time  AND start_time<v_kill_time AND number>0;
    SELECT row_count() INTO insert_count;
    IF (insert_count=0)THEN
      ROLLBACK ;
      set r_result=0;
    ELSEIF (insert_count<0) THEN
      ROLLBACK ;
      SET r_result=-2;
    ELSE
      COMMIT ;
      SET r_result=1;
    END IF ;
  END IF ;
END ;
$$

DELIMITER ;
SET @r_result=-3;
CALL execute_seckill(1002,18710921104,now(),@r_result);
SELECT  @r_result;