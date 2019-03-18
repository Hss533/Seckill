CREATE  DATABASE db_seskill;
use db_seskill;
CREATE  TABLE seckill(
'seckill_id' bigint NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
'name' VARCHAR (120) not NULL  comment '商品名称',
'number' int not null comment '库存数量',
'start_time' TIMESTAMP not null comment '秒杀开启时间',
'end_time' TIMESTAMP  not null comment '秒杀结束时间',
'create_time' TIMESTAMP  not null  DEFAULT CURRENT_TIMESTAMP comment '创建时间',
PRIMARY KEY (seckill_id),
key idx_start_time(start_time) ,
key idx_end_time(end_time) ,
key idx_create_time(create_time)
) ENGINE=InnoDB AUTO_INCREMENT =1000 DEFAULT  CHARSET=utf8 comment='秒杀库存表';



INSERT INTO seckill (name,number,start_time,endTime)
VALUES ("1000元秒杀iPhone 7 Plus",100,'2018-8-1 00:00:00','2018-8-2 00:00:00');

CREATE TABLE  success_killed(

  'seckill_id' BIGINT NOT  NULL COMMENT '秒杀商品id',
  'user_phone' BIGINT NOT NULL  COMMENT '用户手机号',
  'state' TINYINT NOT NULL DEFAULT -1 COMMENT '状态表示-1无效0成功1已付款',
  'create_time' TIMESTAMP NOT NULL COMMENT '创建时间',
  PRIMARY KEY (seckill_id,user_phone)/*联合主键*/,
  KEY idx_create_table(create_time)
)ENGINE =InnoDB DEFAULT CHARSET =utf8 COMMENT ='秒杀成功明细表';

