package com.pivot.aham.api.service.job.interevent;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.pivot.aham.api.service.mapper.model.AccountPerformanceFee;
import com.pivot.aham.api.service.mapper.model.AccountStaticsPO;
import com.pivot.aham.api.service.mapper.model.UserFundNavPO;
import com.pivot.aham.api.service.mapper.model.UserStaticsPO;
import com.pivot.aham.api.service.service.AccountPerformanceFeeService;
import com.pivot.aham.api.service.service.UserFundNavService;
import com.pivot.aham.api.service.service.UserStaticsService;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.analysis.PerformanceFeeStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 月报中间数据-用户过程数据 统计时机：获取到t2的fxr，计算sgd资产之后
 * 取数逻辑：获取当前处理的accountstatic,然后根据当前用户用户占比分配
 *
 */
@Service
@Slf4j
public class UserStaticsListener {

    @Autowired
    private UserFundNavService userFundNavService;
    @Autowired
    private UserStaticsService userStaticsService;
    @Autowired
    private AccountPerformanceFeeService accountPerformanceFeeService; //Added WooiTatt

    @Subscribe
    @AllowConcurrentEvents
    public void userStatics(UserStaticsEvent userStaticsEvent) {
        AccountStaticsPO accountStaticsPO = userStaticsEvent.getAccountStaticsPO();
        //获取该account下的所有用户份额
        UserFundNavPO userFundNavQuery = new UserFundNavPO();
        userFundNavQuery.setAccountId(accountStaticsPO.getAccountId());
        Date fundNavDate = DateUtils.addDateByDay(accountStaticsPO.getStaticDate(), 1);
        userFundNavQuery.setNavTime(fundNavDate);
        List<UserFundNavPO> userFundNavList = userFundNavService.queryList(userFundNavQuery);

        //计算该account下的所以fee、trans cost、dividend
        BigDecimal cashDividend = accountStaticsPO.getCashDividend();
        BigDecimal custFee = accountStaticsPO.getCustFee();
        BigDecimal mgtFee = accountStaticsPO.getMgtFee();
        BigDecimal gstMgtFee = accountStaticsPO.getGstMgtFee();
        BigDecimal transactionCostBuy = accountStaticsPO.getTransactionCostBuy();
        BigDecimal transactionCostSell = accountStaticsPO.getTransactionCostSell();
        BigDecimal adjCashHolding = accountStaticsPO.getAdjCashHolding();
        BigDecimal totalFundValue = accountStaticsPO.getTotalFundValue();

        for (UserFundNavPO userFundNavPO : userFundNavList) {
            //计算比例
            BigDecimal precent = userFundNavPO.getTotalShare().divide(accountStaticsPO.getAdjFundShares(), 6, BigDecimal.ROUND_DOWN);
            BigDecimal userCashDividend = cashDividend.multiply(precent);
            BigDecimal userCustfee = custFee.multiply(precent);
            BigDecimal userMgtFee = mgtFee.multiply(precent);
            BigDecimal userGstMgtFee = gstMgtFee.multiply(precent);
            BigDecimal userTransactionCostBuy = transactionCostBuy.multiply(precent);
            BigDecimal userTransactionCostSell = transactionCostSell.multiply(precent);
            BigDecimal adjFundAssetInSgd = userFundNavPO.getTotalShare().multiply(accountStaticsPO.getNavInSgd());
            BigDecimal useradjCashHolding = adjCashHolding.multiply(precent);
            BigDecimal userTotalFundValue = totalFundValue.multiply(precent);

            UserStaticsPO userStatics = new UserStaticsPO();
            userStatics.setAdjCashHolding(useradjCashHolding);
            userStatics.setAccountId(userFundNavPO.getAccountId());
            userStatics.setGoalId(userFundNavPO.getGoalId());
            userStatics.setClientId(userFundNavPO.getClientId());
            userStatics.setAdjFundAsset(userFundNavPO.getTotalAsset());
            userStatics.setAdjFundAssetInSgd(adjFundAssetInSgd);
            userStatics.setAdjFundShares(userFundNavPO.getTotalShare());
            userStatics.setCashDividend(userCashDividend);
            userStatics.setCustFee(userCustfee);
            userStatics.setFxRateForFundIn(accountStaticsPO.getFxRateForFundIn());
            userStatics.setFxRateForFundOut(accountStaticsPO.getFxRateForFundOut());
            userStatics.setGstMgtFee(userGstMgtFee);
            userStatics.setMgtFee(userMgtFee);
            userStatics.setNavInSgd(accountStaticsPO.getNavInSgd());
            userStatics.setNavInUsd(accountStaticsPO.getNavInUsd());
            userStatics.setTransactionCostBuy(userTransactionCostBuy);
            userStatics.setTransactionCostSell(userTransactionCostSell);
            userStatics.setStaticDate(accountStaticsPO.getStaticDate());
            userStatics.setTotalFundValue(userTotalFundValue);

            //Added By WooiTatt
            //if (!PropertiesUtil.isProd()) {
                AccountPerformanceFee accountPerformanceFee = new AccountPerformanceFee();
                accountPerformanceFee.setClientId(userFundNavPO.getClientId());
                accountPerformanceFee.setGoalId(userFundNavPO.getGoalId());
                accountPerformanceFee.setStatus(PerformanceFeeStatusEnum.PROCESSING);
                String startDate = DateUtils.formatDate(fundNavDate, DateUtils.DATE_FORMAT);
                String endDate = DateUtils.formatDate(DateUtils.addDateByDay(fundNavDate, 1), DateUtils.DATE_FORMAT);
                accountPerformanceFee.setStartTime(DateUtils.parseDate(startDate));
                accountPerformanceFee.setEndTime(DateUtils.parseDate(endDate));

                List<AccountPerformanceFee> lAccPerformanceFee = accountPerformanceFeeService.listAccPerformanceFee(accountPerformanceFee);

                if (lAccPerformanceFee != null && lAccPerformanceFee.size() > 0) {
                    BigDecimal totalPerformanceFee = BigDecimal.ZERO;
                    BigDecimal totalPerformanceFeeGst = BigDecimal.ZERO;

                    for (AccountPerformanceFee accPerFee : lAccPerformanceFee) {
                        totalPerformanceFee = totalPerformanceFee.add(accPerFee.getPerformanceFee());
                        totalPerformanceFeeGst = totalPerformanceFeeGst.add(accPerFee.getPerformanceFeeGst());
                    }
                    userStatics.setPerFee(totalPerformanceFee);
                    userStatics.setGstPerFee(totalPerformanceFeeGst);

                    for (AccountPerformanceFee accPerFee : lAccPerformanceFee) {
                        accountPerformanceFee = new AccountPerformanceFee();
                        accountPerformanceFee.setId(accPerFee.getId());
                        accountPerformanceFee.setStatus(PerformanceFeeStatusEnum.COMPLETED);

                        accountPerformanceFeeService.updateAccPerformanceFeeStatus(accountPerformanceFee);
                    }

                }
           // }

            UserStaticsPO userStaticsQuery = new UserStaticsPO();
            userStaticsQuery.setAccountId(userFundNavPO.getAccountId());
            userStaticsQuery.setGoalId(userFundNavPO.getGoalId());
            userStaticsQuery.setClientId(userFundNavPO.getClientId());
            userStaticsQuery.setStaticDate(accountStaticsPO.getStaticDate());
            UserStaticsPO userStaticsPO = userStaticsService.selectByStaticDate(userStaticsQuery);
            if (userStaticsPO != null) {
                userStatics.setId(userStaticsPO.getId());
            }

            userStaticsService.updateOrInsert(userStatics);
        }
    }
}
