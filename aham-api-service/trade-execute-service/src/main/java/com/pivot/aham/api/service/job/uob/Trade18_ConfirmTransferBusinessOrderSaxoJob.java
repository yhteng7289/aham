package com.pivot.aham.api.service.job.uob;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.UobTradingService;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/*@ElasticJobConf(
		name = "Trade18_ConfirmTransferBusinessOrderSaxoJob_2",
		cron = "0 25 16 * * ?",
		description = "UobTrade_确认UOB到SAXO的转账业务单",eventTraceRdbDataSource = "dataSource")
@Slf4j
public class Trade18_ConfirmTransferBusinessOrderSaxoJob {//implements SimpleJob {

	@Autowired
	private UobTradingService uobTradingService;

	//@Override
	public void execute(ShardingContext context) {
		log.info("开始执行 =======>>> Trade18_ConfirmTransferBusinessOrderSaxoJob");

		try {
			uobTradingService.confirmTransferBusinessOrderSaxo();
		} catch (Exception e) {
			ErrorLogAndMailUtil.logErrorForTrade(log, e);
		}
		log.info("执行结束 =======>>> Trade18_ConfirmTransferBusinessOrderSaxoJob");
	}
}*/