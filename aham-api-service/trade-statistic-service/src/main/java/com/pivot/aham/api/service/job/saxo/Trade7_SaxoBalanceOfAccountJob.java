//package com.pivot.aham.api.service.job.saxo;
//
//import com.dangdang.ddframe.job.api.ShardingContext;
//import com.dangdang.ddframe.job.api.simple.SimpleJob;
//import SaxoStatisticService;
//import ElasticJobConf;
//import ErrorLogAndMailUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//
//@ElasticJobConf(name = "Trade6_SaxoBalanceOfAccountJob_2",
//        shardingItemParameters = "0=1",
//        shardingTotalCount = 1,
//        cron = "0 0 15 * * ?", description = "saxo对账任务")
//@Slf4j
//public class Trade7_SaxoBalanceOfAccountJob implements SimpleJob{
//    @Autowired
//    private SaxoStatisticService saxoStatisticService;
//
//    @Override
//    public void execute(ShardingContext shardingContext) {
//        log.info("开始执行 =======>>> SaxoBalanceOfAccountJob");
//
//        try {
//            //TODO 新对账
////            saxoStatisticService.balanceOfAccount();
//        } catch (Exception e) {
//
//            ErrorLogAndMailUtil.logError(log, e);
//        }
//        log.info("执行结束 =======>>> SaxoBalanceOfAccountJob");
//    }
//}
