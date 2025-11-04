
CREATE TABLE `t_account_dividend` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `account_id` bigint(21) NOT NULL DEFAULT '0' COMMENT '账户Id',
  `ex_date` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '除息日:分红产生时间',
  `trade_date` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '股息登记日payDay',
  `dividend_amount` decimal(20,10) NOT NULL DEFAULT '0.0000000000' COMMENT '原始分红金额',
  `nav_dividend_amount` decimal(20,10) NOT NULL DEFAULT '0.0000000000' COMMENT '可用于计算NAV的分红金额',
  `handel_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '处理状态',
  `ca_event_type_id` int(20) NOT NULL DEFAULT '0' COMMENT '分红类型id',
  `ca_event_type_name` varchar(50) NOT NULL DEFAULT '' COMMENT '分红类型名称',
  `product_code` varchar(50) NOT NULL DEFAULT '' COMMENT 'ETF code',
  `dividend_order_id` varchar(50) NOT NULL DEFAULT '' COMMENT '分红订单号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_product_code` (`product_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收益率表';



CREATE TABLE `t_user_dividend` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `dividend_order_id` bigint(21) unsigned NOT NULL default 0 COMMENT '分红ID',
  `client_id` bigint(21) NOT NULL DEFAULT '0' COMMENT '用户Id',
  `account_id` bigint(21) NOT NULL DEFAULT '0' COMMENT '账户Id',
  `goal_id` varchar(50) NOT NULL DEFAULT '0' COMMENT '目标Id',
  `dividend_date` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '分红时间',
  `dividend_amount` decimal(20,10) NOT NULL DEFAULT '0.0000000000' COMMENT '分红金额',
  `handel_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '处理状态',
  `handel_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '分红处理类型',
  `product_code` varchar(50) NOT NULL DEFAULT '' COMMENT 'ETF code',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_a_g` (`account_id`,`goal_id`),
  KEY `idx_client_id` (`client_id`),
  KEY `idx_product_code` (`product_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户分红表';

alter table t_account_user add `effect_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '生效时间';
update t_account_user t1 INNER JOIN t_account_user t2 on t1.id = t2.id set t1.effect_time=t2.create_time;