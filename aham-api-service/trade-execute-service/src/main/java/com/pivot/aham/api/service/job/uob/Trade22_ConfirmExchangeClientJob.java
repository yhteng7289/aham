package com.pivot.aham.api.service.job.uob;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.UobTradingService;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

//@ElasticJobConf(
//		name = "Trade22_ConfirmExchangeClientJob_2",
//		cron = "0 10 18 * * ?",
//		description = "UobTrade_确认换汇指令Excel_Client",eventTraceRdbDataSource = "dataSource")
@Slf4j
public class Trade22_ConfirmExchangeClientJob implements SimpleJob {

	@Autowired
	private UobTradingService uobTradingService;

	@Override
	public void execute(ShardingContext context) {
		log.info("开始执行 =======>>> Trade22_ConfirmExchangeClientJob");

		try {
			uobTradingService.confirmExchangeOrderClient();
		} catch (Exception e) {
			ErrorLogAndMailUtil.logErrorForTrade(log, e);
		}
		log.info("执行结束 =======>>> Trade22_ConfirmExchangeClientJob");
	}
}