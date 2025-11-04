package com.pivot.aham.api.service.job.uob;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.UobTradingService;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/*@ElasticJobConf(
		name = "Trade27_ConfirmTransferBusinessOrderClientJob_2",
		cron = "0 50 17 * * ?",
		description = "UobTrade_确认UOB到Client的业务单",eventTraceRdbDataSource = "dataSource")
@Slf4j
public class Trade27_ConfirmTransferBusinessOrderClientJob implements SimpleJob {

	@Autowired
	private UobTradingService uobTradingService;

	@Override
	public void execute(ShardingContext context) {
		log.info("开始执行 =======>>> ConfirmTransferBusinessOrderJob");

		try {
			uobTradingService.confirmTransferBusinessOrderClient();
		} catch (Exception e) {
			ErrorLogAndMailUtil.logErrorForTrade(log, e);
		}
		log.info("执行结束 =======>>> ConfirmTransferBusinessOrderJob");
	}
}*/