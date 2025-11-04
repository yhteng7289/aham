package com.pivot.aham.api.service.job.uob;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.UobTradingService;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/*@ElasticJobConf(
		name = "Trade19_NotifyTransferBusinessOrderSaxoJob_2",
		cron = "0 40 16 * * ?",
		description = "UobTrade_回调UOB到SAXO的转账业务单",eventTraceRdbDataSource = "dataSource")
@Slf4j
public class Trade19_NotifyTransferBusinessOrderSaxoJob implements SimpleJob {

	@Autowired
	private UobTradingService uobTradingService;

	@Override
	public void execute(ShardingContext context) {
		log.info("开始执行 =======>>> Trade19_NotifyTransferBusinessOrderSaxoJob");

		try {
			uobTradingService.notifyTransferBusinessOrderSaxo();
		} catch (Exception e) {
			ErrorLogAndMailUtil.logErrorForTrade(log, e);
		}
		log.info("执行结束 =======>>> Trade19_NotifyTransferBusinessOrderSaxoJob");
	}
}*/