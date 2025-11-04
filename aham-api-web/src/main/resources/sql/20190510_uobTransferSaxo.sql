alter table t_saxo_account_order add `exchange_total_order_id`  bigint(21) unsigned NOT NULL default '0' comment '交易转账单号';

-- ##更新历史的交易单号为当时的交易单号
-- update t_saxo_account_order t1 left join t_saxo_account_order t2
-- on t1.id=t2.id
-- set t1.exchange_total_order_id=t2.id
-- where currency=1 and operator_type=1 and action_type=6 and

--全部赎回加标识字段