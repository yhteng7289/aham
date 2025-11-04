alter table t_uob_recharge_log add `recharge_status` tinyint(4) not null default '0' comment '充值状态';
alter table t_account_recharge add `tpcf_status`  tinyint(4) not null default '0' comment 'tpcf状态';
alter table t_account_redeem add `tncf_status`  tinyint(4) not null default '0' comment 'tncf状态', add `nav_date` datetime not null DEFAULT '1970-01-01' comment 'T2汇率时间';