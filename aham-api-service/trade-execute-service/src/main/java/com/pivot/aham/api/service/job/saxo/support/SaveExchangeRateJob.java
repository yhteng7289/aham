package com.pivot.aham.api.service.job.saxo.support;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.TradingSupportService;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/*@ElasticJobConf(name = "SaveExchangeRateJob_2", cron = "0 00 05 * * ?", description = "SaxoSupport_获取汇率", eventTraceRdbDataSource = "dataSource")
@Slf4j
public class SaveExchangeRateJob implements SimpleJob {

    @Autowired
    private TradingSupportService tradingSupportService;

    @Override
    public void execute(ShardingContext context) {
        log.info("开始执行 =======>>> SaveExchangeRateJob");

        try {
            tradingSupportService.saveExchangeRate();
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
        log.info("执行结束 =======>>> SaveExchangeRateJob");
    }
}
*/