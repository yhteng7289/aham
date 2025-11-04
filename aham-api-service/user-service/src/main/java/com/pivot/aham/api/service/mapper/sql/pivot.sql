CREATE TABLE `t_user_info` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `client_id` varchar(50) NOT NULL DEFAULT '' COMMENT 'client_id',
  `client_name` varchar(50) NOT NULL DEFAULT '' COMMENT 'client_name',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY idx_client_id (`client_id`),
  KEY idx_client_name (`client_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户信息';

CREATE TABLE `t_bank_virtual_account` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `client_id` varchar(50) NOT NULL DEFAULT '' COMMENT 'client_id',
  `virtual_account_id` varchar(50) NOT NULL DEFAULT '' COMMENT '虚拟银行账户',
  `cash_amount` decimal(20, 6) not NULL DEFAULT 0 COMMENT '可用账户金额',
  `freeze_amount` decimal(20, 6) not NULL DEFAULT 0 COMMENT '账户冻结金额',
  `used_amount` decimal(20, 6) not NULL DEFAULT 0 COMMENT '账户使用金额',
  `account_type` tinyint(2) not NULL DEFAULT 0 COMMENT '账户类型：1:美金账户,2:新币账户',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY idx_client_id (`client_id`),
  UNIQUE KEY uniq_c_a_t (`client_id`, `virtual_account_id`, `account_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='银行虚拟账户';

CREATE TABLE `t_bank_virtual_account_order` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `virtual_account_id` varchar(50) NOT NULL DEFAULT '' COMMENT '虚拟银行账户',
  `cash_amount` decimal(20, 6) not NULL DEFAULT 0 COMMENT '美金账户金额',
  `status` tinyint(2) NOT NULL DEFAULT 0 COMMENT '银行虚拟订单状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY idx_v_a_id (`virtual_account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='银行虚拟账户流水';

CREATE TABLE `t_user_goal_info` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `client_id` varchar(50) NOT NULL DEFAULT '' COMMENT 'client_id',
  `goal_id` varchar(50) NOT NULL DEFAULT '' COMMENT '投资目标',
  `portfolio_id` varchar(50) NOT NULL DEFAULT '' COMMENT '方案标识',
  `risk` tinyint(2) not NULL DEFAULT 0 COMMENT '风险等级',
  `age` tinyint(2) not NULL DEFAULT 0 COMMENT '年龄区间',
  `reference_code` varchar(50) not NULL DEFAULT '' COMMENT '银行转账code',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY idx_client_id (`client_id`),
  KEY idx_reference_code (`reference_code`)
  UNIQUE KEY uniq_c_g_p (`client_id`,`goal_id`,`portfolio_id`);
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户goal信息';

CREATE TABLE `t_product` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `product_code` varchar(50) NOT NULL DEFAULT '' COMMENT '产品Code',
  `product_name` varchar(50) NOT NULL DEFAULT '' COMMENT '产品名称',
  `product_first_classfiy` tinyint(2) NOT NULL DEFAULT '1' COMMENT '产品一级分类',
  `product_first_classfiy_desc` varchar(50) not NULL DEFAULT '' COMMENT '产品一级分类描述',
  `product_desc` varchar(50) not NULL DEFAULT '' COMMENT '产品描述',
  `product_type` tinyint(2) not NULL DEFAULT '' COMMENT '产品类型：Main Sub',
  `product_status` tinyint(2) not NULL DEFAULT '' COMMENT '产品状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY idx_product_code (`product_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户goal信息';