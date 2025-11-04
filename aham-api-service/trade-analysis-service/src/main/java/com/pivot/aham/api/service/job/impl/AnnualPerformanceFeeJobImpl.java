package com.pivot.aham.api.service.job.impl;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.job.AnnualPerformanceFeeJob;
import com.pivot.aham.api.service.mapper.model.AccountPerformanceFeeDetails;
import com.pivot.aham.api.service.mapper.model.AccountRedeemPO;
import com.pivot.aham.api.service.mapper.model.UserBatchNavPO;
import com.pivot.aham.api.service.mapper.model.UserFundNavPO;
import com.pivot.aham.api.service.service.AccountPerformanceFeeDetailsService;
import com.pivot.aham.api.service.service.AccountRedeemService;
import com.pivot.aham.api.service.service.UserBatchNavService;
import com.pivot.aham.api.service.service.UserFundNavService;
import com.pivot.aham.common.enums.analysis.PerformanceFeeTypeEnum;
import com.pivot.aham.common.enums.analysis.RedeemOrderStatusEnum;
import com.pivot.aham.common.enums.analysis.UserBatchNavEnum;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.enums.recharge.TncfStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * Created by WooiTatt on 18/12/17.
 */
/*@ElasticJobConf(name = "AnnualPerformanceFeesJob_2",
        cron = "0 01 00 01 01 ?",
        shardingItemParameters = "0=1",
        shardingTotalCount = 1,
        description = "交易03_交易分析#计算自建基金净值", eventTraceRdbDataSource = "dataSource")

@Slf4j
public class AnnualPerformanceFeeJobImpl implements SimpleJob, AnnualPerformanceFeeJob {


    @Autowired
    private AccountRedeemService accountRedeemService;
    @Resource
    private UserFundNavService userFundNavService;
    @Resource
    private UserBatchNavService userBatchNavService; //Added By WooiTatt
    @Resource
    private AccountPerformanceFeeDetailsService accountPerformanceFeeDetailsService; //Added By WooiTatt


    public void calculateAnnualPerformanceFee() {
       calculatePerformanceFee(DateUtils.now(), null);
       //calculatePerformanceFee(DateUtils.now(), Long.valueOf("1208995691510280194"));
    }
    
    @Override
    public void calculatePerformanceFee(Date date, Long accountId) {
        
        log.info("accountId:{},CalculatePerformanceFee", accountId);

        try{
            Date yesterday = DateUtils.addDateByDay(date, -1);
            String navDate = DateUtils.formatDate(yesterday, DateUtils.DATE_FORMAT);
            UserFundNavPO userFundNavPO = new UserFundNavPO();
            userFundNavPO.setNavTime(DateUtils.parseDate(navDate));

            List<UserFundNavPO> lUserFundNav = userFundNavService.listUserFundNav(userFundNavPO);

            for(UserFundNavPO usrFundNav : lUserFundNav){
                try{
                log.info("clientId:{},CalculatePerformanceFee", usrFundNav.getClientId());
                UserBatchNavPO usrBatchNavPO = new UserBatchNavPO();
                usrBatchNavPO.setClientId(usrFundNav.getClientId());
                usrBatchNavPO.setGoalId(usrFundNav.getGoalId());
                usrBatchNavPO.setCurrFundNav(usrFundNav.getFundNav());
                usrBatchNavPO.setStatus(UserBatchNavEnum.ACTIVE);

                List<UserBatchNavPO> lUsrBatchNav = userBatchNavService.listUserBatchNavGreateFundNav(usrBatchNavPO);

                for(UserBatchNavPO userBatchNavPO : lUsrBatchNav){
                    try{
                        BigDecimal diffNav = usrFundNav.getFundNav().subtract(userBatchNavPO.getCurrFundNav());
                        BigDecimal performanceFee = userBatchNavPO.getCurrTotalShare().multiply(diffNav).multiply(new BigDecimal("0.1")).setScale(6, BigDecimal.ROUND_DOWN);
                        BigDecimal performanceFeeGst = performanceFee.multiply(new BigDecimal("0.07")).setScale(6, BigDecimal.ROUND_DOWN);

                        //update User Batch NAV
                        UserBatchNavPO usrBatchNav = new UserBatchNavPO();
                        usrBatchNav.setCurrFundNav(usrFundNav.getFundNav());
                        usrBatchNav.setId(userBatchNavPO.getId());
                        userBatchNavService.updateTotalShare(usrBatchNav);


                        AccountPerformanceFeeDetails accountPerformanceFeeDetails = new AccountPerformanceFeeDetails();
                        accountPerformanceFeeDetails.setAccountId(usrFundNav.getAccountId().toString());
                        accountPerformanceFeeDetails.setClientId(usrFundNav.getClientId());
                        accountPerformanceFeeDetails.setFeeType(PerformanceFeeTypeEnum.ANNUAL);
                        accountPerformanceFeeDetails.setGoalId(usrFundNav.getGoalId());
                        accountPerformanceFeeDetails.setUserBatchId(userBatchNavPO.getId().toString());
                        accountPerformanceFeeDetails.setDayNav(usrFundNav.getFundNav());
                        accountPerformanceFeeDetails.setDepositNav(userBatchNavPO.getCurrFundNav());
                        accountPerformanceFeeDetails.setPerformanceFee(performanceFee);
                        accountPerformanceFeeDetails.setPerformanceFeeGst(performanceFeeGst);
                        //accountPerformanceFeeDetails.setShare(confirmShares);
                        accountPerformanceFeeDetailsService.saveAccountPerformanceFeeDetails(accountPerformanceFeeDetails);

                        AccountPerformanceFeeDetails accPerFeeDetails =accountPerformanceFeeDetailsService.getLastAccPerFeeDetails(accountPerformanceFeeDetails);

                        //Insert withdraw 
                        AccountRedeemPO accountRedeemPO = new AccountRedeemPO();
                        accountRedeemPO.setAccountId(usrFundNav.getAccountId());
                        accountRedeemPO.setClientId(usrFundNav.getClientId());
                        accountRedeemPO.setApplyMoney(performanceFee.add(performanceFeeGst).multiply(new BigDecimal("1.1")).setScale(6, BigDecimal.ROUND_DOWN));
                        accountRedeemPO.setGoalId(usrFundNav.getGoalId());
                        accountRedeemPO.setOrderStatus(RedeemOrderStatusEnum.PROCESSING);
                        accountRedeemPO.setTncfStatus(TncfStatusEnum.PROCESSING);
                        accountRedeemPO.setNavDate(DateUtils.now());
                        accountRedeemPO.setIsAnnualPerformanceFee("Y");
                        accountRedeemPO.setNavBatchId(userBatchNavPO.getId());
                        accountRedeemPO.setAccPerformanceFeeDesId(accPerFeeDetails.getId());

                        accountRedeemService.insertAccountRedeem(accountRedeemPO);
                    }catch(Exception e)
                    {
                        ErrorLogAndMailUtil.logErrorForTrade(log, e);
                        continue;
                    }
                }
            }catch(Exception e)
            {
                ErrorLogAndMailUtil.logErrorForTrade(log, e);
                continue;
            }
            }
        }catch(Exception e){ErrorLogAndMailUtil.logErrorForTrade(log, e);}
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            calculateAnnualPerformanceFee();
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
    }
}*/
