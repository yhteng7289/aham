
##备注：
## job_execution_log、job_status_trace_log 两个表是elastic-job使用必须是ID是UUID。IP的字段长度。可能存在多台机器的IP所以目前先保留3台。这量表是框架表,框架生成的目前先不做改动
CREATE TABLE `job_execution_log` (
  `id` varchar(40) NOT NULL default '' comment '主键Id',
  `job_name` varchar(100) NOT NULL default '' comment '任务名称',
  `task_id` varchar(255) NOT NULL default '' comment '任务Id',
  `hostname` varchar(255) NOT NULL default '' comment '主机名',
  `ip` varchar(50) NOT NULL default '' comment 'IP地址',
  `sharding_item` int(11) NOT NULL  default '0' comment 'sharding_item',
  `execution_source` varchar(20) NOT NULL default '' comment 'execution_source',
  `failure_cause` varchar(4000) not null default '' comment '失败原因',
  `is_success` int(11) NOT NULL  default '0' comment '是否成功',
  `start_time` datetime not null DEFAULT CURRENT_TIMESTAMP comment '开始时间',
  `complete_time` datetime not null DEFAULT CURRENT_TIMESTAMP comment '完成时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '任务执行表';


CREATE TABLE `job_status_trace_log` (
  `id` varchar(40) NOT NULL default '' comment '主键Id',
  `job_name` varchar(100) NOT NULL default '' comment '任务名称',
  `original_task_id` varchar(255) NOT NULL default '' comment '原任务Id',
  `task_id` varchar(255) NOT NULL default '' comment '任务Id',
  `slave_id` varchar(50) NOT NULL default '' comment '存机器Id',
  `source` varchar(50) NOT NULL default '' comment '来源',
  `execution_type` varchar(20) NOT NULL default '' comment 'execution_type',
  `sharding_item` varchar(100) NOT NULL default '' comment 'sharding_item',
  `state` varchar(20) NOT NULL default '' comment '状态',
  `message` varchar(4000) not null default '' comment 'message',
  `creation_time` datetime not null DEFAULT CURRENT_TIMESTAMP comment '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_t_id_s` (`task_id`(170),`state`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '任务执行表';

# KEY `TASK_ID_STATE_INDEX` (`task_id`(191),`state`) USING BTREE


CREATE TABLE `t_account_asset` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `product_code` varchar(20) NOT NULL default '' COMMENT '产品code',
  `account_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '账户id',
  `confirm_share` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT '确认份额',
  `confirm_money` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT '确认金额',
  `apply_money` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT '申请金额',
  `product_asset_status` tinyint(2) NOT NULL COMMENT '资产状态',
  `recharge_order_no` bigint(20) NOT NULL DEFAULT '0' COMMENT '订单编号',
  `apply_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '申请时间',
  `confirm_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '确认时间',
  `total_tmp_order_id` bigint(20) not null DEFAULT '0' COMMENT '总临时订单号',
  `create_time` datetime not null DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `tmp_order_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '订单号',
  `dividend_order_id` varchar(20) not null default '' COMMENT '分红订单id',
  `asset_source` tinyint(2) not null default '-1' COMMENT '资产来源',
  PRIMARY KEY (`id`),
  key idx_account_id(`account_id`),
  key idx_r_o_n(`recharge_order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户资产流水记录';


CREATE TABLE `t_account_cost` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint(21) not null default 0 COMMENT '账户id',
  `client_id` bigint(21) not null DEFAULT 0 COMMENT '客户id',
  `trans_cost` decimal(20,2) not null DEFAULT '0' COMMENT '交易手续费',
  `product_code` varchar(255) not null DEFAULT '' COMMENT '产品id',
  `trans_cost_source` tinyint(2) not null DEFAULT '-1' COMMENT '来源',
  `create_time` datetime not NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime not null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `tmp_order_id` bigint(20) not null DEFAULT '0' COMMENT '订单id',
  PRIMARY KEY (`id`),
  key idx_account_id(`account_id`),
  key idx_client_id(`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易手续费记录';


CREATE TABLE `t_account_etf_shares` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint(20) not null default 0 comment '账户Id',
  `product_code` varchar(50) not null DEFAULT '' comment '产品Code',
  `shares` decimal(20,6) not null DEFAULT '0' comment '产品份额',
  `money` decimal(20,6) not null DEFAULT '0' comment '产品金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
  `update_time` datetime not null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
  `static_date` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '统计日期',
  PRIMARY KEY (`id`),
  key idx_account_id(`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '账户ETF份额';


CREATE TABLE `t_account_etf_shares_static` (
  `id` BIGINT(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `static_date` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '统计日期',
  `account_id` bigint(20) not NULL DEFAULT '0' COMMENT '账户id',
  `vt` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品vt',
  `eem` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品eem',
  `bndx` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品bndx',
  `shv` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品shv',
  `emb` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品emb',
  `vwob` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品vwob',
  `bwx` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品bwx',
  `hyg` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品hyg',
  `jnk` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品jnk',
  `mub` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品mub',
  `lqd` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品lqd',
  `vcit` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品vcit',
  `flot` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品flot',
  `ief` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品ief',
  `uup` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品uup',
  `pdbc` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品pdbc',
  `gld` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品gld',
  `vnq` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品vnq',
  `vea` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品vea',
  `vpl` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品vpl',
  `ewa` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品ewa',
  `spy` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品spy',
  `voo` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品voo',
  `vti` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品vti',
  `vgk` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品vgk',
  `ewj` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品ewj',
  `qqq` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品qqq',
  `ews` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品ews',
  `ewz` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品ewz',
  `ashr` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品ashr',
  `vwo` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品vwo',
  `ilf` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品ilf',
  `rsx` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品rsx',
  `aaxj` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品aaxj',
  `asx` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品asx',
  `awc` decimal(20,6) NOT NULL DEFAULT '0.000000' comment '产品awc',
  `create_time` datetime not null DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime not null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  key idx_account_id(`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '账户ETF份额统计表';


CREATE TABLE `t_account_fund_nav` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `account_id` bigint(21) NOT NULL DEFAULT '0' COMMENT '账户id',
  `fund_nav` decimal(25,10) NOT NULL DEFAULT '0.0000000000' COMMENT '自建基金净值',
  `total_share` decimal(25,10) NOT NULL DEFAULT '0.0000000000' COMMENT '自建基金份额',
  `total_asset` decimal(25,10) NOT NULL DEFAULT '0.0000000000' COMMENT '自建基金金额',
  `nav_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '自建基金净值时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `total_cash` decimal(25,10) NOT NULL DEFAULT '0.0000000000' COMMENT '账户总现金',
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自建基金净值表';


CREATE TABLE `t_account_info` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `portfolio_id` varchar(50) NOT NULL DEFAULT '' COMMENT '策略Id',
  `invest_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '账号类型',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `init_day` tinyint(2) NOT NULL DEFAULT '1' COMMENT '是否首单',
  PRIMARY KEY (`id`),
  key idx_portfolio_id(`portfolio_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户账户信息';


CREATE TABLE `t_account_normal_fee` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint(21) not NULL DEFAULT 0 COMMENT '账户id',
  `client_id` bigint(21) not NULL DEFAULT 0 COMMENT '客户id',
  `mgt_fee` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '管理费',
  `cust_fee` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '经营费',
  `mgt_gst` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '管理附加费',
  `reduce_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否扣减',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
  `update_time` datetime not null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
  PRIMARY KEY (`id`),
   KEY `idx_account_id` (`account_id`),
    KEY `idx_client_id` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日常手续费';


CREATE TABLE `t_account_recharge` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `account_id` bigint(21) NOT NULL DEFAULT 0 COMMENT '账户id',
  `client_id` bigint(21) NOT NULL DEFAULT 0 COMMENT '用户id',
  `recharge_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '充值时间',
  `currency` tinyint(2) NOT NULL DEFAULT '1' COMMENT '币种',
  `recharge_amount` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT '充值金额',
  `bank_order_no` varchar(50) NOT NULL DEFAULT '' COMMENT '银行单号',
  `order_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '订单状态',
  `recharge_order_no` varchar(50) NOT NULL DEFAULT '' COMMENT '充值定单号',
  `execute_order_no` varchar(50) NOT NULL DEFAULT '' COMMENT '交易执行订单号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `goal_id` varchar(50) NOT NULL DEFAULT '' COMMENT '投资目标Id',
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_client_id` (`client_id`),
  KEY `idx_recharge_order_no` (`recharge_order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户充值流水表';


CREATE TABLE `t_account_redeem` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `client_id` bigint(21) NOT NULL DEFAULT 0 COMMENT '客户id',
  `account_id` bigint(21) NOT NULL DEFAULT 0 COMMENT '账户id',
  `redeem_apply_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '提现申请时间',
  `redeem_confirm_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '提现确认时间',
  `apply_money` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT '申请金额',
  `confirm_money` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT '确认金额',
  `confirm_shares` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT '确认份额',
  `order_status` tinyint(2) not null DEFAULT '-1' COMMENT '提现状态',
  `total_tmp_order_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'etf订单编号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `goal_id` varchar(50) NOT NULL DEFAULT '' COMMENT '投资目标',
  `redeem_apply_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '提现申请Id',
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_client_id` (`client_id`),
  key `idx_total_tmp_order_id` (`total_tmp_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投资账户提现记录';


CREATE TABLE `t_account_statics` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint(20) not NULL DEFAULT '0' COMMENT '账号id',
  `total_equity` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '总etf价值',
  `cash_residual` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '购买etf剩余金额',
  `cash_by_sell` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT 'etf卖出金额',
  `cash_holding` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '卖出前的现金总额',
  `cash_dividend` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '现金分红',
  `mgt_fee` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '管理费',
  `cust_fee` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '管理费',
  `gst_mgt_fee` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '管理附加费',
  `per_fee` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '提成费',
  `gst_per_fee` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '提成附加费',
  `unbuy_amount` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT 'unbuy金额',
  `excess_cash` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '超额现金',
  `transaction_cost_buy` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '购买时的手续费',
  `transaction_cost_sell` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '卖出时产生的手续费',
  `total_fund_value` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT 'Total Equity Value + Cash Holding',
  `fund_shares` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '自建基金份额',
  `nav_in_usd` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '美元净值',
  `cash_withdraw` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '账户提现总金额',
  `adj_fund_shares` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '剩余份额',
  `adj_fund_asset` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '剩余资产',
  `adj_cash_holding` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '剩余现金',
  `nav_In_sgd` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '新币净值',
  `adj_fund_asset_in_sgd` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '剩余资产-新币',
  `cash_withdraw_in_sgd` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '剩余新币-新币',
  `fx_rate_for_fund_in` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT 'saxo入金汇率',
  `fx_rate_for_fund_out` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT 'saxo出金汇率',
  `fx_rate_for_clearing` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '每天收盘时汇率',
  `static_date` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '统计日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建日期',
  `update_time` datetime not null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新日期',
  PRIMARY KEY (`id`),
    KEY `idx_account_id` (`account_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '中间记录统计表';


CREATE TABLE `t_account_user` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `client_id` bigint(21) NOT NULL DEFAULT 0 COMMENT '客户id',
  `account_id` bigint(21) NOT NULL DEFAULT 0 COMMENT '账户id',
  `reference_code` varchar(50) NOT NULL DEFAULT '' COMMENT '银行转账Code',
  `goal_id` varchar(50) NOT NULL DEFAULT '' COMMENT '投资目标Id',
  `portfolio_id` varchar(50) NOT NULL DEFAULT '' COMMENT '模型Id',
  `risk_level` tinyint(4) NOT NULL DEFAULT '1' COMMENT '风险等级',
  `age_level` tinyint(4) NOT NULL DEFAULT '0' COMMENT '年龄段',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `first_recharge_currency` tinyint(2) NOT NULL DEFAULT '1' COMMENT '首单充值币种类型',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_c_a_r` (`client_id`, `account_id`, `reference_code`),
  KEY `idx_goal_id` (`goal_id`),
  KEY `idx_portfolio_id` (`portfolio_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账户关系表';


CREATE TABLE `t_bank_virtual_account` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `client_id` bigint(21) NOT NULL DEFAULT 0 COMMENT 'client_id',
  `client_name` varchar(50) NOT NULL DEFAULT '' COMMENT 'client_name',
  `virtual_account_no` varchar(50) NOT NULL DEFAULT '' COMMENT '虚拟银行账户',
  `cash_amount` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT '美金账户金额',
  `freeze_amount` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT '新币账户金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `currency` tinyint(4) NOT NULL DEFAULT '1' COMMENT '币种',
  `used_amount` decimal(20,6) not NULL DEFAULT '0.000000' COMMENT '用户名',
  PRIMARY KEY (`id`),
  KEY `idx_client_id` (`client_id`),
  KEY `idx_v_a_id` (`virtual_account_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='银行虚拟账户';


CREATE TABLE `t_bank_virtual_account_order` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `virtual_account_no` varchar(50) NOT NULL DEFAULT '' COMMENT '虚拟银行账户',
  `cash_amount` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT '美金账户金额',
  `currency` tinyint(4) NOT NULL DEFAULT '1' COMMENT '币种类型',
  `order_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '订单状态',
  `operator_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '虚拟账户订单交易类型',
  `need_refend_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否需要退款',
  `bank_order_no` varchar(50) NOT NULL DEFAULT '' COMMENT '银行流水单号',
  `trade_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '交易时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `reference_code` varchar(50) NOT NULL DEFAULT '' COMMENT '投资目标关联标识',
  `redeem_apply_id` bigint(20) not null default 0 COMMENT '提现申请id',
  `action_type` tinyint(4) not null default -1 COMMENT '动作类型',
  PRIMARY KEY (`id`),
  KEY `idx_v_a_n` (`virtual_account_no`),
  KEY `idx_bank_order_no` (`bank_order_no`),
  KEY `idx_reference_code` (`reference_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='银行虚拟账户订单';


CREATE TABLE `t_daily_closing_price` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT comment '主键',
  `etf_code` varchar(20) NOT NULL DEFAULT '' COMMENT 'product code',
  `bsn_dt` date NOT NULL DEFAULT '1970-01-01' COMMENT 'business date key',
  `price` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT 'price',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_dt_code` (`etf_code`,`bsn_dt`) USING BTREE COMMENT '唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日收盘价';


CREATE TABLE `t_daily_exchange_rate` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `bsn_dt` varchar(8) NOT NULL COMMENT 'business date key',
  `usd_to_sgd` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT 'rate of usd to sgd',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_bsnDt` (`bsn_dt`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日汇率';


CREATE TABLE `t_daily_twap_price` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `etf_code` varchar(20) NOT NULL DEFAULT '' COMMENT '产品',
  `bsn_dt` varchar(8) NOT NULL DEFAULT '' COMMENT '日期',
  `avg_ask` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT 'ask价格',
  `avg_bid` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT 'bid价格',
  `avg_count` int(11) NOT NULL DEFAULT '1' COMMENT '计数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_dt_code` (`etf_code`,`bsn_dt`) USING BTREE COMMENT '唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='twap价格';

CREATE TABLE `t_etf_info` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `etf_code` varchar(20) NOT NULL DEFAULT '' COMMENT 'product code',
  `uic` int(11) NOT NULL DEFAULT '0' COMMENT 'saxo`s product id',
  `exchange_code` varchar(10) NOT NULL DEFAULT '' COMMENT 'stock exchange name',
  `use_enable` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'use_enable',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_etf` (`etf_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='etf info';

CREATE TABLE `t_etf_merge_order` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'order type',
  `order_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'order status',
  `product_code` varchar(255) NOT NULL DEFAULT '' COMMENT 'product code',
  `cost_fee` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT 'cost',
  `apply_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT 'apply time',
  `confirm_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT 'confirm time',
  `apply_amount` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT 'apply amount of money(USD)',
  `confirm_amount` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT 'confirm amount of money(USD)',
  `confirm_share` int(11) NOT NULL DEFAULT '0' COMMENT 'confirm share',
  `balance_order` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否为调仓单',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='etf merged order';


CREATE TABLE `t_etf_order` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'username id',
  `order_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'type of order',
  `order_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'status of order',
  `product_code` varchar(50) NOT NULL DEFAULT '' COMMENT 'product code',
  `cost_fee` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT 'cost',
  `apply_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT 'apply time',
  `confirm_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT 'confirm time',
  `apply_amount` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT 'apply amount of money(USD)',
  `confirm_amount` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT 'confirm amount of money(USD)',
  `confirm_share` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT 'confirm share',
  `out_business_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'caller`s business id',
  `merge_order_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'id of merge order',
  `balance_order` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否为调仓单',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='etf order';


CREATE TABLE `t_model_recommend` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `model_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '模型时间',
  `risk_level` tinyint(2) NOT NULL DEFAULT '0' COMMENT '风险等级',
  `age_level` tinyint(2) NOT NULL DEFAULT '0' COMMENT '年龄等级',
  `model_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '模型状态',
  `score` decimal(20,10) NOT NULL DEFAULT '0.0000000000' COMMENT '模型分',
  `currency` decimal(20,10) NOT NULL DEFAULT '0.0000000000' COMMENT '现金配比',
  `product_weight` varchar(2000) NOT NULL DEFAULT '' COMMENT '产品比重配置',
  `classfiy_weight` varchar(500) NOT NULL DEFAULT '' COMMENT '产品一级配置',
  `portfolio_id` varchar(50) NOT NULL DEFAULT '' COMMENT '模型Id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `pool` tinyint(2) not null DEFAULT 1 COMMENT '投资年限',
  PRIMARY KEY (`id`),
  KEY `idx_portfolio_id` (`portfolio_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='模型数据';


CREATE TABLE `t_model_recommend_back_up` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `model_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '模型时间',
  `risk_level` tinyint(2) NOT NULL DEFAULT '0' COMMENT '风险等级',
  `age_level` tinyint(2) NOT NULL DEFAULT '0' COMMENT '年龄等级',
  `model_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '模型状态',
  `score` decimal(20,10) NOT NULL DEFAULT '0.0000000000' COMMENT '模型分',
  `currency` decimal(20,10) NOT NULL DEFAULT '0.0000000000' COMMENT '现金配比',
  `product_weight` varchar(2000) NOT NULL DEFAULT '' COMMENT '产品比重配置',
  `classfiy_weight` varchar(500) NOT NULL DEFAULT '' COMMENT '产品一级配置',
  `portfolio_id` varchar(50) NOT NULL DEFAULT '' COMMENT '模型Id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_portfolio_id` (`portfolio_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='模型备份数据';

##修改了date
CREATE TABLE `t_port_future_level` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `rcmd` decimal(20,10) NOT NULL DEFAULT '0.0000000000' COMMENT '未来总收益的平均值',
  `sixty_eight_low` decimal(20,10) NOT NULL DEFAULT '0.0000000000' COMMENT '总回报范围68%置信区间的下限',
  `sixty_eight_up` decimal(20,10) NOT NULL DEFAULT '0.0000000000' COMMENT '总回报范围68%置信区间的上限',
  `ninety_five_low` decimal(20,10) NOT NULL DEFAULT '0.0000000000' COMMENT '总回报范围95%置信区间的下限',
  `ninety_five_up` decimal(20,10) NOT NULL DEFAULT '0.0000000000' COMMENT '总回报范围95%置信区间的上限',
  `model_date` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '时间',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否可用状态',
  `create_time` datetime not null DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `portfolio_id` varchar(50) NOT NULL DEFAULT '' COMMENT '策略Id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='未来收益表';

##修改了保留字段
CREATE TABLE `t_port_level` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `portfolio_level` decimal(25,15) NOT NULL DEFAULT '0.000000000000000' COMMENT '市场收益数据',
  `max_dd` decimal(25,15) NOT NULL DEFAULT '0.000000000000000' COMMENT '最大回撤',
  `vol` decimal(25,15) NOT NULL DEFAULT '0.000000000000000' COMMENT '波动率',
  `return_vol` decimal(25,15) NOT NULL DEFAULT '0.000000000000000' COMMENT '收益率',
  `rebalance` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否调仓',
  `model_date` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '数据时间',
  `portfolio_id` varchar(50) NOT NULL DEFAULT '' COMMENT '模型策略Id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最新状态更新时间',
  `create_time` datetime not null DEFAULT CURRENT_TIMESTAMP COMMENT '方案生成时间',
  `benchmark_data` decimal(25,15) NOT NULL DEFAULT '0.000000000000000' COMMENT '对标市场收益数据',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_p_d` (`portfolio_id`,`model_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='收益曲线信息';


CREATE TABLE `t_product` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `product_code` varchar(50) NOT NULL DEFAULT '' COMMENT '产品Code',
  `product_name` varchar(50) NOT NULL DEFAULT '' COMMENT '产品名称',
  `product_first_classfiy` tinyint(2) NOT NULL DEFAULT '1' COMMENT '产品一级分类',
  `product_first_classfiy_desc` varchar(50) NOT NULL DEFAULT '' COMMENT '产品一级分类描述',
  `product_desc` varchar(50) NOT NULL DEFAULT '' COMMENT '产品描述',
  `url` varchar(200) NOT NULL DEFAULT '' COMMENT '产品介绍',
  `product_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '产品类型：Main Sub',
  `product_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '产品状态',
  `create_time` datetime not null DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_product_code` (`product_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户goal信息';


CREATE TABLE `t_redeem_apply` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `source_account_type` tinyint(2) NOT NULL COMMENT '源账户的货币类型',
  `apply_money` decimal(20,6) NOT NULL COMMENT '申请金额',
  `target_currency` tinyint(2) NOT NULL COMMENT '目标货币类型',
  `client_id` bigint(21) NOT NULL DEFAULT 0 COMMENT '用户id',
  `account_id` bigint(21) not null default 0 COMMENT '账户id',
  `bank_name` varchar(32) not null DEFAULT '' COMMENT '银行名称',
  `bank_account_no` varchar(32) not null DEFAULT '' COMMENT '银行账号',
  `exchange_amount` decimal(20,6) not null DEFAULT '0' COMMENT '购汇金额',
  `withdrawal_source_type` tinyint(2) not null DEFAULT -1 COMMENT '提现来源类型',
  `withdrawal_target_type` tinyint(2) not null DEFAULT -1 COMMENT '提现目的地类型',
  `withdrawal_target_bank_type` tinyint(2) not null DEFAULT -1 COMMENT '目标银行类型',
  `swift` varchar(20) not null DEFAULT '' COMMENT '银行swift',
  `branch` varchar(20) not null DEFAULT '' COMMENT '银行branch',
  `bank_address` varchar(500) NOT NULL DEFAULT '' COMMENT '银行地址',
  `etf_executed_status` tinyint(2) not null default -1 COMMENT 'etf执行状态',
  `saxo_to_uob_transfer_status` tinyint(2) not null default 1 COMMENT 'SAXOtoUOB转账状态',
  `bank_transfer_status` tinyint(2) not null DEFAULT 5 COMMENT '银行转账状态',
  `bank_transfer_order_Id` varchar(20) not null DEFAULT '' COMMENT '银行转账id',
  `confirm_amount` decimal(20,6) not null DEFAULT '0' COMMENT '提现确认金额',
  `apply_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '申请时间',
  `confirm_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '确认时间',
  `total_tmp_order_id` bigint(20) not null DEFAULT 0 COMMENT '临时订单id',
  `saxo_to_uob_batch_id` varchar(10) not null DEFAULT '' COMMENT 'SAXO到uob的批次id',
  `use_enable` tinyint(2) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  `create_time` datetime  not null DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `redeem_apply_status` tinyint(2) DEFAULT '0' COMMENT '提现状态',
  `goal_id` varchar(32) not null default 0 COMMENT '账户目标id',
  `source_apply_money` decimal(20,6) not null default 0 COMMENT '源币种申请金额',
  PRIMARY KEY (`id`),
  key idx_client_id(`client_id`),
  key idx_account_id(`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提现申请记录';


CREATE TABLE `t_saxo_account_event` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `client_id` bigint(21) NOT NULL DEFAULT 0 COMMENT 'client id',
  `account_id` bigint(21) NOT NULL DEFAULT 0 COMMENT 'username id',
  `sequence_id` varchar(20) NOT NULL DEFAULT '' COMMENT 'sequence',
  `activity_type` varchar(20) NOT NULL DEFAULT '' COMMENT 'only AccountFundings',
  `activity_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT 'UTC',
  `amount` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT 'Amount (e.g. number of securities) ordered.',
  `conversion_rate` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT 'The conversion rate used at the time of the transaction.',
  `currency_code` varchar(10) NOT NULL DEFAULT '' COMMENT 'The currency of the transaction.',
  `funding_event` varchar(20) NOT NULL DEFAULT '' COMMENT 'Describes the funding interevent. （New、Updated）',
  `funding_type` varchar(20) NOT NULL DEFAULT '' COMMENT 'Describes the type of the funding. （Deposit、Withdrawal)',
  `position_id` varchar(20) NOT NULL DEFAULT '' COMMENT 'Identifies the position.',
  `registration_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT 'The time when the transaction was registered by the bank.（UTC）',
  `value_date` date NOT NULL DEFAULT '1970-01-01' COMMENT 'The date when the final transfer of the product of the transaction takes place.',
  `confirmed` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'confirm state',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_sequence_id` (`sequence_id`) USING BTREE COMMENT 'uniq key'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment 'saxo username interevent';


CREATE TABLE `t_saxo_account_order` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `account_id` bigint(21) NOT NULL DEFAULT 0 COMMENT '账户id',
  `client_id` bigint(21) NOT NULL DEFAULT 0 COMMENT '用户id',
  `cash_amount` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT '美金账户金额',
  `currency` tinyint(4) NOT NULL DEFAULT '1' COMMENT '币种类型',
  `order_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '订单状态',
  `operator_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '虚拟账户订单交易类型',
  `exchange_order_no` varchar(50) NOT NULL DEFAULT '' COMMENT '订单号',
  `bank_order_no` varchar(50) NOT NULL DEFAULT '' COMMENT '银行订单号',
  `trade_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '交易时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `action_type` tinyint(2) NOT NULL DEFAULT '-1' COMMENT '交易来源',
  `goal_id` varchar(50) NOT NULL DEFAULT '' COMMENT '目标Id',
  PRIMARY KEY (`id`),
  KEY `idx_exchange_order_no` (`exchange_order_no`),
  KEY `idx_bank_order_no` (`bank_order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SAXO账户币种转化流水表';


CREATE TABLE `t_saxo_order` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT comment '主键',
  `uic` int(20) NOT NULL COMMENT 'saxo`s product id',
  `merge_order_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'id of merge order',
  `saxo_order_code` varchar(30) NOT NULL DEFAULT '' COMMENT 'order id of saxo',
  `order_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'status',
  `order_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'type',
  `apply_share` int(11) NOT NULL DEFAULT '0' COMMENT 'apply share',
  `apply_amount` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT 'apply money amount (USD)',
  `apply_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'apply time',
  `confirm_share` int(11) NOT NULL DEFAULT '0' COMMENT 'deal share',
  `confirm_amount` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT 'deal amout (USD)',
  `confirm_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT 'deal time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='saxo order';


CREATE TABLE `t_saxo_order_activity` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `log_id` varchar(20) NOT NULL DEFAULT '' COMMENT 'log id',
  `account_id` bigint(21) NOT NULL DEFAULT 0 COMMENT 'username id',
  `client_id` bigint(21) NOT NULL DEFAULT 0 COMMENT 'client id',
  `order_id` varchar(20) NOT NULL DEFAULT '' COMMENT 'order id',
  `order_type` varchar(20) NOT NULL DEFAULT '' COMMENT 'order type',
  `position_id` varchar(20) NOT NULL DEFAULT '' COMMENT 'position id',
  `activity_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'activity time',
  `buySell` varchar(10) NOT NULL DEFAULT '' COMMENT 'Buy or Sell',
  `amount` int(11) NOT NULL DEFAULT '0' COMMENT 'Order amount',
  `fill_amount` int(11) NOT NULL DEFAULT '0' COMMENT 'The amount of the current fill',
  `filled_amount` int(11) NOT NULL DEFAULT '0' COMMENT 'Amount currently filled',
  `average_price` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT 'The average price of the FilledAmount',
  `execution_price` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT 'Execution price of this particular fill (if multiple fills)',
  `price` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT 'Order price.',
  `status` varchar(10) NOT NULL DEFAULT '' COMMENT 'OrderStatusType example Placed/Working/Fill/Cancelled',
  `sub_status` varchar(10) NOT NULL DEFAULT '' COMMENT 'The OrderSubStatus identifies the sub status of an order.',
  `uic` int(11) NOT NULL DEFAULT '0' COMMENT 'Uic of instrument traded',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='saxo order activity';







CREATE TABLE `t_saxo_to_uob_record_detail` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `saxo_to_uob_batch_id` varchar(32) NOT NULL DEFAULT '' COMMENT '批次id',
  `transaction_id` varchar(32) NOT NULL DEFAULT '' COMMENT 'uob转账单号',
  `currency` tinyint(2) NOT NULL DEFAULT '-1' COMMENT '币种',
  `amount` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT '确认金额',
  `confirm_date` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '确认日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT  '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_transaction_id` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment 'saxo to uob record detail';


CREATE TABLE `t_saxo_to_uob_total_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `saxo_to_uob_batch_id` varchar(20) NOT NULL DEFAULT '' COMMENT '批次ID',
  `intend_amount` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT '预计金额',
  `confirmed_amount` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT '确认金额',
  `residual_amount` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT '剩余金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `transfer_date` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '转账时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_s_to_u_b_id` (`saxo_to_uob_batch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='saxo到uob记录汇总';


CREATE TABLE `t_tmp_order_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint(20) NOT NULL COMMENT '账户id',
  `tmp_order_id` bigint(20) NOT NULL COMMENT '临时订单id',
  `total_tmp_order_id` bigint(20) NOT NULL COMMENT '临时总订单号',
  `execute_order_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'etf执行单号',
  `product_code` varchar(32) NOT NULL COMMENT '产品code',
  `apply_money` decimal(20,6) NOT NULL COMMENT '申请金额',
  `confirm_money` decimal(20,6) NOT NULL DEFAULT '0' COMMENT '确认金额',
  `confirm_trade_shares` decimal(20,6) NOT NULL DEFAULT '0' COMMENT '确认份额',
  `action_type` tinyint(2) NOT NULL DEFAULT '-1' COMMENT '操作类型',
  `tmp_order_trade_status` tinyint(2) NOT NULL COMMENT '临时订单状态',
  `tmp_order_trade_type` tinyint(2) NOT NULL COMMENT '临时订单交易类型',
  `apply_time` datetime NOT NULL COMMENT '申请时间',
  `confirm_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' comment '确认时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT  '更新时间',
  PRIMARY KEY (`id`),
  key idx_account_id(`account_id`),
  UNIQUE KEY `uniq_tmp_order_id` (`tmp_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='临时订单表';

CREATE TABLE `t_uob_exchange_order` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `out_business_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'caller`s business id',
  `order_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'type or order',
  `order_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'status of order',
  `apply_amount` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '申请金额',
  `apply_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT 'apply time',
  `confirm_amount` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '确认金额',
  `confirm_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT 'confirm time',
  `cost_fee` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT 'cost',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment 't_uob_exchange_order ';


CREATE TABLE `t_uob_recharge_log` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `bank_order_no` varchar(50) NOT NULL DEFAULT '' COMMENT '银行订单号',
  `client_name` varchar(30) NOT NULL DEFAULT '' COMMENT '用户姓名',
  `virtual_account_no` varchar(50) NOT NULL DEFAULT '' COMMENT '虚拟账号',
  `cash_amount` decimal(20,6) NOT NULL DEFAULT '0.000000' COMMENT '充值金额',
  `currency` varchar(10) NOT NULL DEFAULT '' COMMENT '币种',
  `reference_code` varchar(25) NOT NULL DEFAULT '' COMMENT '引用id',
  `trade_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '交易类型',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_orderNo` (`bank_order_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='uob入金记录';


CREATE TABLE `t_uob_tra_exe_order` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `bank_name` varchar(50) NOT NULL DEFAULT '' COMMENT 'bank name',
  `bank_account_number` varchar(50) NOT NULL DEFAULT '' COMMENT '银行账户号',
  `bank_user_name` varchar(50) NOT NULL DEFAULT '' COMMENT '银行用户名',
  `branch_code` varchar(50) NOT NULL DEFAULT '' COMMENT 'branch code',
  `swift_code` varchar(50) NOT NULL DEFAULT '' COMMENT 'swift code',
  `order_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'type or order',
  `order_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态',
  `currency` tinyint(4) NOT NULL DEFAULT '0' COMMENT '货币',
  `amount` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '金额',
  `cost_fee` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT 'cost',
  `apply_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT 'apply time',
  `confirm_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT 'confirm time',
  `relation_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '关联类型',
  `remark` varchar(50) NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment 'uob transfer execution order';


CREATE TABLE `t_uob_tra_order` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `out_business_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'caller`s business id',
  `bank_name` varchar(50) NOT NULL DEFAULT '' COMMENT '银行名称',
  `bank_account_number` varchar(50) NOT NULL DEFAULT '' COMMENT '账户号',
  `bank_user_name` varchar(50) NOT NULL DEFAULT '' COMMENT '用户名',
  `branch_code` varchar(50) NOT NULL DEFAULT '' COMMENT 'branch code',
  `swift_code` varchar(50) NOT NULL DEFAULT '' COMMENT 'swift code',
  `order_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'type or order',
  `order_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'status of order',
  `currency` tinyint(4) NOT NULL DEFAULT '0' COMMENT '货币',
  `amount` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT 'apply amount of money(USD)',
  `cost_fee` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT 'cost',
  `apply_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT 'apply time',
  `confirm_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT 'confirm time',
  `remark` varchar(50) NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment 'uob transfer order';

CREATE TABLE `t_uob_tra_order_rela` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `business_order_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '业务单ID',
  `execution_order_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '执行单ID',
  `relation_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '关联类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关联关系表';







CREATE TABLE `t_user_asset` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `account_id` bigint(21) NOT NULL DEFAULT 0 COMMENT '账户id',
  `client_id` bigint(21) NOT NULL DEFAULT 0 COMMENT '用户id',
  `product_code` varchar(50) NOT NULL DEFAULT '' COMMENT '持有的产品code',
  `share` decimal(20,10) NOT NULL DEFAULT '0.0000000000' COMMENT '持有etf份额',
  `money` decimal(20,10) NOT NULL DEFAULT '0.0000000000' COMMENT '持有etf金额',
  `asset_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '资产统计时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `goal_id` varchar(50) NOT NULL DEFAULT '' COMMENT '投资目标',
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_client_id` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户资产统计表';








CREATE TABLE `t_user_fund_nav` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `client_id` bigint(21) NOT NULL DEFAULT 0 COMMENT '用户id',
  `fund_nav` decimal(25,10) NOT NULL DEFAULT '0.0000000000' COMMENT '自建基金净值',
  `total_share` decimal(25,10) NOT NULL DEFAULT '0.0000000000' COMMENT '自建基金份额',
  `total_asset` decimal(25,10) NOT NULL DEFAULT '0.0000000000' COMMENT '自建基金金额',
  `nav_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '自建基金净值时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `account_id` bigint(21) NOT NULL DEFAULT '0' COMMENT '账户Id',
  `goal_id` varchar(50) DEFAULT '' COMMENT '投资目标Id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_client_id` (`client_id`,`account_id`,`goal_id`,`nav_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户自建基金净值表';

CREATE TABLE `t_user_goal_info` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `client_id` bigint(21) NOT NULL DEFAULT 0 COMMENT 'client_id',
  `goal_id` varchar(50) NOT NULL DEFAULT '' COMMENT '投资目标',
  `portfolio_id` varchar(50) NOT NULL DEFAULT '' COMMENT '方案标识',
  `reference_code` varchar(50) NOT NULL DEFAULT '' COMMENT '银行转账code',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_c_g_p` (`client_id`,`goal_id`,`portfolio_id`),
  KEY `idx_reference_code` (`reference_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='用户goal信息';


CREATE TABLE `t_user_info` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `client_id` bigint(21) NOT NULL DEFAULT 0 COMMENT 'client_id',
  `client_name` varchar(50) NOT NULL DEFAULT '' COMMENT 'client_name',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_client_id` (`client_id`),
  KEY `idx_client_name` (`client_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息';

##修改了保留字段
CREATE TABLE `t_user_profit_info` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `client_id` bigint(21) NOT NULL DEFAULT 0 COMMENT '用户Id',
  `account_id` bigint(21) NOT NULL DEFAULT '0' COMMENT '账户Id',
  `goal_id` varchar(50) NOT NULL DEFAULT '0' COMMENT '目标Id',
  `portfolio_profit` decimal(20,10) NOT NULL DEFAULT '0.0000000000' COMMENT '策略收益',
  `fx_impact` decimal(20,10) NOT NULL DEFAULT '0.0000000000' COMMENT '换汇收益',
  `total_profit` decimal(20,10) NOT NULL DEFAULT '0.0000000000' COMMENT '累计收益',
  `profit_date` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_c_a_g_d` (`account_id`,`client_id`,`goal_id`,`profit_date`),
  KEY `idx_client_id` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收益率表';

CREATE TABLE `task_fire_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `group_name` varchar(50) NOT NULL DEFAULT '' COMMENT '组名称',
  `task_name` varchar(50) NOT NULL DEFAULT '' COMMENT '任务名称',
  `start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
  `end_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '结束时间',
  `status` varchar(1) NOT NULL DEFAULT 'I' COMMENT '执行状态',
  `server_host` varchar(50) NOT NULL DEFAULT '' COMMENT '服务器名',
  `fire_info` VARCHAR(2000) NOT NULL DEFAULT '' COMMENT '日志',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_g_n_t_n_s_t` (`group_name`,`task_name`,`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务日志';

