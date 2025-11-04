package com.pivot.aham.api.service.job.impl;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.mapper.model.BankVirtualAccount;
import com.pivot.aham.api.service.mapper.model.BankVirtualAccountDailyRecord;
import com.pivot.aham.api.service.service.BankVirtualAccountDailyRecordService;
import com.pivot.aham.api.service.service.BankVirtualAccountService;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.core.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by luyang.li on 2018/12/24.
 * <p>
 * 同步Uob的充值记录
 */
/*@ElasticJobConf(name = "StaticBankVirtualAccountJob_2",
        cron = "00 00 23 * * ?",
        shardingItemParameters = "0=1",
        shardingTotalCount = 1,
        description = "统计每日虚拟账户余额")
@Slf4j
public class StaticBankVirtualAccountJobImpl {//implements SimpleJob {

    @Autowired
    private BankVirtualAccountDailyRecordService bankVirtualAccountDailyRecordService;
    @Autowired
    private BankVirtualAccountService bankVirtualAccountService;
    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("#######统计每日虚拟账户余额,开始。");
        try {
            BankVirtualAccount bankVirtualAccount = new BankVirtualAccount();
            List<BankVirtualAccount> bankVirtualAccountList = bankVirtualAccountService.queryList(bankVirtualAccount);
            for(BankVirtualAccount bankVirtual:bankVirtualAccountList){
                BankVirtualAccountDailyRecord bankVirtualAccountDailyRecordQuery = new BankVirtualAccountDailyRecord();
                bankVirtualAccountDailyRecordQuery.setClientId(bankVirtual.getClientId());
                bankVirtualAccountDailyRecordQuery.setVirtualAccountNo(bankVirtual.getVirtualAccountNo());
                bankVirtualAccountDailyRecordQuery.setStaticDate(DateUtils.now());
                BankVirtualAccountDailyRecord bankVirtualAccountDaily = bankVirtualAccountDailyRecordService.selectByStaticDate(bankVirtualAccountDailyRecordQuery);

                BankVirtualAccountDailyRecord bankVirtualAccountDailyRecord = new BankVirtualAccountDailyRecord();
                BeanUtils.copyProperties(bankVirtual,bankVirtualAccountDailyRecord);
                bankVirtualAccountDailyRecord.setId(null);
                bankVirtualAccountDailyRecord.setStaticDate(DateUtils.now());
                if(bankVirtualAccountDaily != null) {
                    bankVirtualAccountDailyRecord.setId(bankVirtualAccountDaily.getId());
                }
                bankVirtualAccountDailyRecord.setCreateTime(null);
                bankVirtualAccountDailyRecordService.updateOrInsert(bankVirtualAccountDailyRecord);
            }
        } catch (Exception ex) {
            log.error("#######统计每日虚拟账户余额：", ex);
            ErrorLogAndMailUtil.logError(log, ex);
        }
        log.info("#######统计每日虚拟账户余额,完成。");
    }
}*/