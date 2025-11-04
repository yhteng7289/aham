package com.pivot.aham.api.service.job.uob;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.UobTradingService;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.enums.UobTransferOrderTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

//@ElasticJobConf(
//		name = "Trade29_TestUOBControllerJob_2",
//		cron = "0 1/04 * * * ?",
//		description = "UobTrade_执行UOB到Client的执行单",eventTraceRdbDataSource = "dataSource")
@Slf4j
public class Trade29_TestUOBController implements SimpleJob {

	@Autowired
	private UobTradingService uobTradingService;

	@Override
	public void execute(ShardingContext context) {
		log.info("开始执行 =======>>> Trade25_ExecuteTransferExecutionOrderClientJob");

		try {
			uobTradingService.executeTransferOrderToBank(UobTransferOrderTypeEnum.TRANSFER_TO_BANK);
		} catch (Exception e) {
			ErrorLogAndMailUtil.logErrorForTrade(log, e);
		}
		log.info("执行结束 =======>>> Trade25_ExecuteTransferExecutionOrderClientJob");
	}
}