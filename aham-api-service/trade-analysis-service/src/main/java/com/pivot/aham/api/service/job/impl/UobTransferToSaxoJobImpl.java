package com.pivot.aham.api.service.job.impl;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.service.RechargeService;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * 处理用户入金
 *
 * @author addison
 * @since 2018年12月06日
 */
/*@ElasticJobConf(name = "uobTransferToSaxo_2",
        cron = "0 20 14 * * ?",
        shardingItemParameters = "0=1",
        shardingTotalCount = 1,
        description = "交易02_交易分析#下uob到saxo的转账单")
@Slf4j
public class UobTransferToSaxoJobImpl implements SimpleJob {

    @Resource
    private RechargeService rechargeService;

    /**
     * 分析UOB的入金下指令转入SAXO，UOB中的处理中的操作类型是从UOB转SAXO的订单进行转账指令
     * <p>
     * <p>
     * * 1、检查是否开市
     * * 2、查询UOB中的处理中的操作类型是从UOB转SAXO的订单，下转账指令
     * * 3、指令下单成功分配账号 (区分Pooling和tailor)
     * * 4、记录转账流水处理中
     * * 5、等待回调
     * <p>
     * 回调、记录充值流水:t_account_recharge
     * 回调、保存投资流水时需要检查账号是否分配:
     */
 /*   @Override
    public void execute(ShardingContext shardingContext) {
        try {
            log.info("=======分析UOB的入金下指令转入SAXO开始");
            rechargeService.handelUobTransferToSaxo();
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
            log.info("=======分析UOB的入金下指令转入SAXO异常：", e);
        }
        log.info("=======分析UOB的入金下指令转入SAXO结束");
    }

}*/
