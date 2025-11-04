package com.pivot.aham.api.service.job.saxo.support;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.TradingSupportService;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

//@ElasticJobConf(name = "SaveClosingPriceJob_2", cron = "0 27 9 * * ?", description = "SaxoSupport_获取前收盘价", eventTraceRdbDataSource = "dataSource")
@ElasticJobConf(name = "SaveClosingPriceJob_2", cron = "0 45 17 * * ?", description = "SaxoSupport_获取前收盘价", eventTraceRdbDataSource = "dataSource")
@Slf4j
public class SaveClosingPriceJob implements SimpleJob {

    @Autowired
    private TradingSupportService tradingSupportService;

    @Override
    public void execute(ShardingContext context) {
        log.info("开始执行 =======>>> SaveClosingPriceJob");

        try {
            //tradingSupportService.saveClosingPrice(null);
            tradingSupportService.saveAhamClosingPrice(null);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
        log.info("执行结束 =======>>> SaveClosingPriceJob");
    }
}
