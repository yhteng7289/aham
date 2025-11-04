package com.pivot.aham.api.service.job.custstatment.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.*;
import com.pivot.aham.api.server.dto.BankVirtualAccountDTO;
import com.pivot.aham.api.server.dto.BankVirtualAccountDailyRecordDTO;
import com.pivot.aham.api.server.dto.BankVirtualAccountDailyRecordResDTO;
import com.pivot.aham.api.server.dto.BankVirtualAccountResDTO;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.service.mapper.model.*;
import com.pivot.aham.api.service.service.AccountRechargeService;
import com.pivot.aham.api.service.service.SaxoAccountOrderService;
import com.pivot.aham.api.service.service.UserFundNavService;
import com.pivot.aham.api.service.service.UserStaticsService;
import com.pivot.aham.common.core.util.CalDecimal;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderActionTypeEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderTradeStatusEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderTradeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class GenAccountSummary {
    @Resource
    private UserServiceRemoteService userServiceRemoteService;
    @Autowired
    private UserStaticsService userStaticsService;
    @Autowired
    private UserFundNavService userFundNavService;
    @Autowired
    private AccountRechargeService accountRechargeService;
    @Autowired
    private SaxoAccountOrderService saxoAccountOrderService;

    public AccountSummaryPO genAccountSummary(AccountUserPO accountUserPO, Date startTime, Date endTime){
        //获取开始时间和结束时间
//        Date lastMonth = DateUtils.addMonths(new Date(),-1);
//        Date startTime = DateUtils.monthStart(lastMonth);
//        Date endTime = DateUtils.monthEnd(lastMonth);

        UserStaticsPO userStaticsQuery = new UserStaticsPO();
        userStaticsQuery.setClientId(accountUserPO.getClientId());
        userStaticsQuery.setStartStaticDate(startTime);
        userStaticsQuery.setEndStaticDate(endTime);
        List<UserStaticsPO> userStaticsList = userStaticsService.queryListByTime(userStaticsQuery);
        if(CollectionUtils.isEmpty(userStaticsList)){
            log.error("统计用户资产错误,用户本月无资产记录:{}", JSON.toJSONString(userStaticsQuery));
            return null;
        }
        //过程数据按时间分组
        Multimap<String,UserStaticsPO> userStaticsMap = ArrayListMultimap.create();
        for(UserStaticsPO userStatics:userStaticsList){
            String date = DateUtils.formatDate(userStatics.getStaticDate(),"yyyy-MM-dd");
            userStaticsMap.put(date,userStatics);
        }
        AccountSummaryPO accountSummary = new AccountSummaryPO();

        //第一天
        UserStaticsPO openStatic = userStaticsList.get(0);
        //最后一天
        UserStaticsPO closeStatic = userStaticsList.get(userStaticsList.size()-1);
        Date openDate = openStatic.getStaticDate();
        String openDateStr = DateUtils.formatDate(openDate,"yyyy-MM-dd");
        BigDecimal openFxr = openStatic.getFxRateForFundOut();
        Date closeDate = closeStatic.getStaticDate();
        BigDecimal closeFxr = closeStatic.getFxRateForFundOut();
        String closeDateStr = DateUtils.formatDate(closeDate,"yyyy-MM-dd");

        List<UserStaticsPO> userStaticsOpenList = (List<UserStaticsPO>) userStaticsMap.get(openDateStr);
        List<UserStaticsPO> userStaticsCloseList = (List<UserStaticsPO>) userStaticsMap.get(closeDateStr);


        //按account统计oepnvalue和closevalue
        BigDecimal totalPortfolioOpenValue = BigDecimal.ZERO;
        BigDecimal totalPortfolioCloseValue = BigDecimal.ZERO;
        BigDecimal portfolioCloseValueSgd = BigDecimal.ZERO;
        BigDecimal portfolioOpenValueSgd = BigDecimal.ZERO;
        for(UserStaticsPO userStaticsPO:userStaticsOpenList) {
            //查找userStatics对应staticdate的fundnav，如果没有，说明首次入金
            UserFundNavPO userFundNavPO = new UserFundNavPO();
            userFundNavPO.setClientId(userStaticsPO.getClientId());
            userFundNavPO.setGoalId(userStaticsPO.getGoalId());
            userFundNavPO.setAccountId(userStaticsPO.getAccountId());
            userFundNavPO.setNavTime(userStaticsPO.getStaticDate());
            UserFundNavPO userFundNav = userFundNavService.selectOneByNavTime(userFundNavPO);
            if(userFundNav == null){
                AccountRechargePO accountRecharge = new AccountRechargePO();
                accountRecharge.setClientId(userStaticsPO.getClientId());
                accountRecharge.setTpcfTime(userStaticsPO.getStaticDate());
                List<AccountRechargePO> firstTpcfs = accountRechargeService.listAccountRecharge(accountRecharge);
                if(firstTpcfs != null && firstTpcfs.size()>0 ){
                    for(AccountRechargePO accountRecharg:firstTpcfs) {
                        Long executeOrderNo = accountRecharg.getExecuteOrderNo();
                        //根据executeOrderNo获取saxoaccountorder
                        SaxoAccountOrderPO saxoAccountOrderQuery = new SaxoAccountOrderPO();
                        saxoAccountOrderQuery.setActionType(SaxoOrderActionTypeEnum.UOBTOSAXO);
                        saxoAccountOrderQuery.setOperatorType(SaxoOrderTradeTypeEnum.COME_INTO);
                        saxoAccountOrderQuery.setCurrency(CurrencyEnum.SGD);
                        saxoAccountOrderQuery.setAccountId(userStaticsPO.getAccountId());
                        saxoAccountOrderQuery.setClientId(userStaticsPO.getClientId());
                        saxoAccountOrderQuery.setExchangeOrderNo(executeOrderNo);
                        saxoAccountOrderQuery.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
                        SaxoAccountOrderPO firstSaxoAccountOrder = saxoAccountOrderService.selectOne(saxoAccountOrderQuery);
                        portfolioOpenValueSgd = portfolioOpenValueSgd.add(firstSaxoAccountOrder.getCashAmount());
                    }
                }

            }else{
                portfolioOpenValueSgd = portfolioOpenValueSgd.add(userStaticsPO.getAdjFundAssetInSgd());
            }

            totalPortfolioOpenValue = totalPortfolioOpenValue.add(userStaticsPO.getAdjFundAsset());


        }

        for(UserStaticsPO userStaticsPO:userStaticsCloseList){
            totalPortfolioCloseValue = totalPortfolioCloseValue.add(userStaticsPO.getAdjFundAsset());
            portfolioCloseValueSgd = portfolioCloseValueSgd.add(userStaticsPO.getAdjFundAssetInSgd());
        }
        accountSummary.setPortfolioCloseValue(totalPortfolioCloseValue);
        accountSummary.setPortfolioOpenValue(totalPortfolioOpenValue);
        accountSummary.setPortfolioCloseValueSgd(portfolioCloseValueSgd);
        accountSummary.setPortfolioOpenValueSgd(portfolioOpenValueSgd);



        //按virtualaccount统计squirrelsave的open和close
        //按client获取所有虚拟账户
        BankVirtualAccountDTO bankVirtualAccount = new BankVirtualAccountDTO();
        bankVirtualAccount.setClientId(accountUserPO.getClientId());
        List<BankVirtualAccountResDTO> bankVirtualAccountList = userServiceRemoteService.queryListBankVirtualAccount(bankVirtualAccount);

        //按虚拟账户获取银行订单流水
        BigDecimal squirrelCashCloseValueSgd = BigDecimal.ZERO;
        BigDecimal squirrelCashOpenValueSgd = BigDecimal.ZERO;
        BigDecimal squirrelCashCloseValue = BigDecimal.ZERO;
        BigDecimal squirrelCashOpenValue = BigDecimal.ZERO;
        for(BankVirtualAccountResDTO virtualAccount:bankVirtualAccountList){
            BankVirtualAccountDailyRecordDTO bankVirtualAccountDailyRecord = new BankVirtualAccountDailyRecordDTO();
            bankVirtualAccountDailyRecord.setClientId(virtualAccount.getClientId());
            bankVirtualAccountDailyRecord.setVirtualAccountNo(virtualAccount.getVirtualAccountNo());
            bankVirtualAccountDailyRecord.setStartStaticDate(startTime);
            bankVirtualAccountDailyRecord.setEndStaticDate(endTime);
            List<BankVirtualAccountDailyRecordResDTO> bankVirtualAccountDailyRecordResDTOList = userServiceRemoteService
                    .queryBankVirtualAccountDailyRecordList(bankVirtualAccountDailyRecord);

            if(CollectionUtils.isEmpty(bankVirtualAccountDailyRecordResDTOList)){
                continue;
            }
            BigDecimal squirrelOpenValue = bankVirtualAccountDailyRecordResDTOList.get(0).getCashAmount();
            BigDecimal squirrelCloseValue = bankVirtualAccountDailyRecordResDTOList.get(bankVirtualAccountDailyRecordResDTOList.size()-1).getCashAmount();

            if (virtualAccount.getCurrency()== CurrencyEnum.SGD){
                squirrelCashOpenValue = squirrelCashOpenValue.add(squirrelOpenValue.divide(openFxr,2,BigDecimal.ROUND_DOWN));
                squirrelCashCloseValue = squirrelCashCloseValue.add(squirrelCloseValue.divide(closeFxr,2,BigDecimal.ROUND_DOWN));
                squirrelCashOpenValueSgd = squirrelCashOpenValueSgd.add(squirrelOpenValue);
                squirrelCashCloseValueSgd = squirrelCashCloseValueSgd.add(squirrelCloseValue);
            }else{
                squirrelCashOpenValueSgd = squirrelCashOpenValueSgd.add(squirrelOpenValue.multiply(openFxr).setScale(2,BigDecimal.ROUND_DOWN));
                squirrelCashCloseValueSgd = squirrelCashCloseValueSgd.add(squirrelCloseValue.multiply(closeFxr).setScale(2,BigDecimal.ROUND_DOWN));
                squirrelCashOpenValue = squirrelCashOpenValue.add(squirrelOpenValue);
                squirrelCashCloseValue = squirrelCashCloseValue.add(squirrelCloseValue);

            }
        }

        accountSummary.setSquirrelCashOpenValue(squirrelCashOpenValue);
        accountSummary.setSquirrelCashCloseValue(squirrelCashCloseValue);
        accountSummary.setSquirrelCashOpenValueSgd(squirrelCashOpenValueSgd);
        accountSummary.setSquirrelCashCloseValueSgd(squirrelCashCloseValueSgd);


        //统一处理小数位
        CalDecimal<AccountSummaryPO> calDecimal = new CalDecimal<>();
        calDecimal.handleDot(accountSummary);
        //小数处理后再计算total
        BigDecimal totalOpenValue = accountSummary.getPortfolioOpenValue().add(accountSummary.getSquirrelCashOpenValue());
        BigDecimal totalCloseValue = accountSummary.getPortfolioCloseValue().add(accountSummary.getSquirrelCashCloseValue());

        //获取对应日期的T1和T2
//        BigDecimal totalOpenValueSgd = totalOpenValue.multiply(openFxr).setScale(2,BigDecimal.ROUND_DOWN);
//        BigDecimal totalCloseValueSgd = totalCloseValue.multiply(closeFxr).setScale(2,BigDecimal.ROUND_DOWN);

        BigDecimal totalOpenValueSgd = accountSummary.getPortfolioOpenValueSgd().add(accountSummary.getSquirrelCashOpenValueSgd());
        BigDecimal totalCloseValueSgd = accountSummary.getPortfolioCloseValueSgd().add(accountSummary.getSquirrelCashCloseValueSgd());

        accountSummary.setTotalOpenValueSgd(totalOpenValueSgd);
        accountSummary.setTotalCloseValueSgd(totalCloseValueSgd);
        accountSummary.setTotalOpenValue(totalOpenValue);
        accountSummary.setTotalCloseValue(totalCloseValue);


        //返回accountSummary
        return accountSummary;
    }
}
