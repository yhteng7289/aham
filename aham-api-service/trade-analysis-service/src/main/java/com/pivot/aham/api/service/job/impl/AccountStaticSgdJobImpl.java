package com.pivot.aham.api.service.job.impl;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.eventbus.EventBus;
import com.pivot.aham.api.service.job.AccountStaticSgdJob;
import com.pivot.aham.api.service.job.interevent.UserStaticsEvent;
import com.pivot.aham.api.service.mapper.model.AccountInfoPO;
import com.pivot.aham.api.service.mapper.model.AccountPerformanceFee;
import com.pivot.aham.api.service.mapper.model.AccountStaticsPO;
import com.pivot.aham.api.service.mapper.model.ExchangeRatePO;
import com.pivot.aham.api.service.service.AccountInfoService;
import com.pivot.aham.api.service.service.AccountPerformanceFeeService;
import com.pivot.aham.api.service.service.AccountStaticsService;
import com.pivot.aham.api.service.service.ExchangeRateService;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.enums.ExchangeRateTypeEnum;
import com.pivot.aham.common.enums.analysis.InitDayEnum;
import com.pivot.aham.common.enums.analysis.PerformanceFeeStatusEnum;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 重置account过程数据统计状态
 *
 * @author addison
 * @since 2018年12月06日
 */
@ElasticJobConf(name = "AccountStaticSgdJob_2",
        cron = "0 30 20 * * ?",
        shardingItemParameters = "0=1",
        shardingTotalCount = 1,
        description = "交易04_重置account过程数据统计状态")
@Slf4j
@Component
public class AccountStaticSgdJobImpl implements SimpleJob, AccountStaticSgdJob {

    @Resource
    private AccountInfoService accountInfoService;
    @Resource
    private AccountStaticsService accountStaticsService;
    @Resource
    private ExchangeRateService exchangeRateService;
    @Resource
    private EventBus eventBus;
    @Resource
    private AccountPerformanceFeeService accountPerformanceFeeService; //Added WooiTatt

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("AccountStaticSgdJob_2====>开始");
        try {
            updateSgd(null, null);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
        log.info("AccountStaticSgdJob_2====>结束");
    }

    @Override
    public void updateSgd(Long accountId, Date date) {
        Date calDate = null;
        if (date == null) {
            calDate = DateUtils.now();
        } else {
            calDate = date;
        }

        AccountInfoPO accountInfoPO = new AccountInfoPO();
        List<AccountInfoPO> accountInfoPOList = accountInfoService.queryList(accountInfoPO);
        for (AccountInfoPO accountInfo : accountInfoPOList) {
            if (accountId != null && !accountInfo.getId().equals(accountId)) {
                continue;
            }

            ExchangeRatePO exchangeRateParam = new ExchangeRatePO();
            exchangeRateParam.setRateDate(DateUtils.dayStart(calDate));
            exchangeRateParam.setExchangeRateType(ExchangeRateTypeEnum.SAXO_FXRT2);
            //ExchangeRatePO exchangeRatePO = exchangeRateService.getExchangeRate(exchangeRateParam);
            ExchangeRatePO exchangeRatePO = new ExchangeRatePO();
            exchangeRatePO.setUsdToSgd(BigDecimal.ONE);
            AccountStaticsPO accountStaticsQuery = new AccountStaticsPO();
            accountStaticsQuery.setAccountId(accountInfo.getId());
            Date yesterDay = DateUtils.addDateByDay(calDate, -1);
            accountStaticsQuery.setStaticDate(yesterDay);
            AccountStaticsPO accountStaticsPO = accountStaticsService.selectByStaticDate(accountStaticsQuery);

            //Added By WooiTatt
            BigDecimal totalPerformanceFee = null;
            BigDecimal totalPerformanceFeeGst = null;
            //if (!PropertiesUtil.isProd()) {
                AccountPerformanceFee accountPerformanceFee = new AccountPerformanceFee();
                accountPerformanceFee.setAccountId(accountInfo.getId().toString());
                accountPerformanceFee.setStatus(PerformanceFeeStatusEnum.PROCESSING);
                String startDate = DateUtils.formatDate(calDate, DateUtils.DATE_FORMAT);
                String endDate = DateUtils.formatDate(DateUtils.addDateByDay(calDate, 1), DateUtils.DATE_FORMAT);
                accountPerformanceFee.setStartTime(DateUtils.parseDate(startDate));
                accountPerformanceFee.setEndTime(DateUtils.parseDate(endDate));
                totalPerformanceFee = accountPerformanceFeeService.getSumAccPerformanceFee(accountPerformanceFee);
                totalPerformanceFeeGst = accountPerformanceFeeService.getSumAccPerformanceFeeGst(accountPerformanceFee);
            //}

            if (null != exchangeRatePO && accountStaticsPO != null) {
                AccountStaticsPO accountStaticsUpdate = new AccountStaticsPO();
                BigDecimal fxRateUsd = exchangeRatePO.getUsdToSgd();
                BigDecimal navInSgd = accountStaticsPO.getNavInUsd().multiply(fxRateUsd);
                BigDecimal adjFundAssetInSgd = accountStaticsPO.getAdjFundShares().multiply(navInSgd);
                BigDecimal cashWithdrawInSgd = accountStaticsPO.getCashWithdraw().multiply(fxRateUsd);

                accountStaticsUpdate.setId(accountStaticsPO.getId());
                accountStaticsUpdate.setNavInSgd(navInSgd);
                accountStaticsUpdate.setAdjFundAssetInSgd(adjFundAssetInSgd);
                accountStaticsUpdate.setCashWithdrawInSgd(cashWithdrawInSgd);
                accountStaticsUpdate.setFxRateForClearing(fxRateUsd);
                accountStaticsUpdate.setFxRateForFundOut(fxRateUsd);

                //Added By WooiTatt
                if (totalPerformanceFee != null && totalPerformanceFeeGst != null) {
                    accountStaticsUpdate.setPerFee(totalPerformanceFee);
                    accountStaticsUpdate.setGstPerFee(totalPerformanceFeeGst);
                }

                AccountStaticsPO accountStaticsAfterUpdate = accountStaticsService.updateOrInsert(accountStaticsUpdate);

                UserStaticsEvent userStaticsEvent = new UserStaticsEvent();
                userStaticsEvent.setAccountStaticsPO(accountStaticsAfterUpdate);
                eventBus.post(userStaticsEvent);

            } else {
                log.error("账户{},没有查询到T日的T2汇率或昨日statics为空", accountInfo.getId());
            }

            if (accountInfo.getInitDay() == InitDayEnum.INIT_DAY) {
                continue;
            }
        }

    }
}
