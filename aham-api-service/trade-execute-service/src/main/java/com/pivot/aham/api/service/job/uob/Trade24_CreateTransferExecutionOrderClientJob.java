package com.pivot.aham.api.service.job.uob;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.UobTradingService;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/*@ElasticJobConf(
		name = "Trade24_CreateTransferExecutionOrderClientJob_2",
		cron = "0 45 16 * * ?",
		description = "UobTrade_创建UOB到Client的执行单",eventTraceRdbDataSource = "dataSource")
@Slf4j
public class Trade24_CreateTransferExecutionOrderClientJob implements SimpleJob {

	@Autowired
	private UobTradingService uobTradingService;

	@Override
	public void execute(ShardingContext context) {
		log.info("开始执行 =======>>> Trade24_CreateTransferExecutionOrderClientJob");

		try {
			uobTradingService.createTransferExecutionOrderClient();
		} catch (Exception e) {
			ErrorLogAndMailUtil.logErrorForTrade(log, e);
		}
		log.info("执行结束 =======>>> Trade24_CreateTransferExecutionOrderClientJob");
	}
}*/