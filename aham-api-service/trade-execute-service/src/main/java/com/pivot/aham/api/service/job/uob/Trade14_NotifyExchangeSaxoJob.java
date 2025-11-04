package com.pivot.aham.api.service.job.uob;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.UobTradingService;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

//@ElasticJobConf(
//		name = "Trade14_NotifyExchangeSaxoJob_2",
//		cron = "0 15 14 * * ?",
//		description = "UobTrade_换汇回调_SAXO",eventTraceRdbDataSource = "dataSource")
@Slf4j
public class Trade14_NotifyExchangeSaxoJob implements SimpleJob {

	@Autowired
	private UobTradingService uobTradingService;

	@Override
	public void execute(ShardingContext context) {
		log.info("开始执行 =======>>> Trade14_NotifyExchangeSaxoJob");

		try {
			uobTradingService.notifyExchangeOrderSaxo();
		} catch (Exception e) {
			ErrorLogAndMailUtil.logErrorForTrade(log, e);
		}
		log.info("执行结束 =======>>> Trade14_NotifyExchangeSaxoJob");
	}
}