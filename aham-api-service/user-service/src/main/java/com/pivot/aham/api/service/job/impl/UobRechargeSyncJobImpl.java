package com.pivot.aham.api.service.job.impl;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.pivot.aham.api.service.service.UobRechargeService;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;

import javax.annotation.Resource;

/**
 * Created by luyang.li on 2018/12/24.
 * <p>
 * 同步Uob的充值记录
 */
/*@ElasticJobConf(name = "UobRechargeSynJob_2",
        cron = "0 30 12 * * ?",
        shardingItemParameters = "0=1",
        shardingTotalCount = 1,
        description = "分析01_拉取UOB充值和UOB购汇下单")
@Slf4j
public class UobRechargeSyncJobImpl implements SimpleJob {

    @Resource
    private UobRechargeService uobRechargeService;

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("#######同步UOB线下入金到松鼠虚拟账户,开始。");
        try {
            uobRechargeService.syncUobRechargeToVirtualAccount();
        } catch (Exception ex) {
            log.error("#######同步UOB线下入金到松鼠虚拟账户异常：", ex);
            ErrorLogAndMailUtil.logError(log, ex);
        }
        log.info("#######同步UOB线下入金到松鼠虚拟账户,完成。");
    }
}*/
