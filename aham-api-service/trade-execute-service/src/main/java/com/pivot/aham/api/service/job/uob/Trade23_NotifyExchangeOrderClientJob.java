package com.pivot.aham.api.service.job.uob;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.UobTradingService;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

//@ElasticJobConf(
//		name = "Trade23_NotifyExchangeOrderClientJob_2",
//		cron = "0 30 18 * * ?",
//		description = "UobTrade_换汇回调_Client",eventTraceRdbDataSource = "dataSource")
@Slf4j
public class Trade23_NotifyExchangeOrderClientJob implements SimpleJob {

	@Autowired
	private UobTradingService uobTradingService;

	@Override
	public void execute(ShardingContext context) {
		log.info("开始执行 =======>>> Trade23_NotifyExchangeOrderClientJob");

		try {
			uobTradingService.notifyExchangeOrderClient();
		} catch (Exception e) {
			ErrorLogAndMailUtil.logErrorForTrade(log, e);
		}
		log.info("执行结束 =======>>> Trade23_NotifyExchangeOrderClientJob");
	}
}