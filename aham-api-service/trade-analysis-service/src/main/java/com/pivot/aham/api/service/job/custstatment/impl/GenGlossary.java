package com.pivot.aham.api.service.job.custstatment.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.pivot.aham.api.server.dto.UserGoalInfoDTO;
import com.pivot.aham.api.server.dto.UserGoalInfoResDTO;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.service.mapper.model.AccountRechargePO;
import com.pivot.aham.api.service.mapper.model.AccountUserPO;
import com.pivot.aham.api.service.mapper.model.GlossaryPO;
import com.pivot.aham.api.service.mapper.model.SaxoAccountOrderPO;
import com.pivot.aham.api.service.mapper.model.UserFundNavPO;
import com.pivot.aham.api.service.mapper.model.UserStaticsPO;
import com.pivot.aham.api.service.service.AccountRechargeService;
import com.pivot.aham.api.service.service.SaxoAccountOrderService;
import com.pivot.aham.api.service.service.UserFundNavService;
import com.pivot.aham.api.service.service.UserStaticsService;
import com.pivot.aham.common.core.base.RpcMessage;
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
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class GenGlossary {
    @Autowired
    private UserStaticsService userStaticsService;
    @Resource
    private SaxoAccountOrderService saxoAccountOrderService;
    @Autowired
    private UserFundNavService userFundNavService;
    @Autowired
    private AccountRechargeService accountRechargeService;
    @Autowired
    private UserServiceRemoteService userServiceRemoteService;

    public List<GlossaryPO> genGlossary(AccountUserPO accountUserPO,Date startTime,Date endTime) {
        List<GlossaryPO> glossaryPOList = Lists.newArrayList();
        //获取开始时间和结束时间
        //Edited By WooiTatt
        Date lastMonth = DateUtils.addMonths(startTime, -1); 
        Date startTimeStatic = DateUtils.monthEnd(lastMonth);
        Date getActualStartStatic = DateUtils.addDateByDay(startTimeStatic, -2);
        Date getStartStaticFilter = DateUtils.addDateByDay(startTimeStatic, -1);
//        Date endTime = DateUtils.monthEnd(lastMonth);

        //获取某个client的所有资产流水
        UserStaticsPO userStaticsQuery = new UserStaticsPO();
        userStaticsQuery.setClientId(accountUserPO.getClientId());
        //userStaticsQuery.setStartStaticDate(startTime);
        userStaticsQuery.setStartStaticDate(getActualStartStatic); // Edited By WooiTatt
        userStaticsQuery.setEndStaticDate(endTime);
        List<UserStaticsPO> userStaticsList = userStaticsService.queryListByTime(userStaticsQuery);
        if(CollectionUtils.isEmpty(userStaticsList)){
            log.error("统计用户资产错误,用户本月无资产记录:{}", JSON.toJSONString(userStaticsQuery));
            return null;
        }
        //按goal分组
        Multimap<String, UserStaticsPO> multimap = ArrayListMultimap.create();
        for (UserStaticsPO userStaticsPO : userStaticsList) {
            multimap.put(userStaticsPO.getGoalId(), userStaticsPO);
        }

        //按account统计oepnvalue和closevalue
        Set<String> goalKeySet = multimap.keySet();
        for (String goalId : goalKeySet) {
            GlossaryPO glossaryPO = new GlossaryPO();
            List<UserStaticsPO> userStaticsGoalList = Lists.newArrayList(multimap.get(goalId));
            //过程数据按时间分组
            Multimap<String,UserStaticsPO> userStaticsMap = ArrayListMultimap.create();
            for(UserStaticsPO userStatics:userStaticsGoalList){
                String date = DateUtils.formatDate(userStatics.getStaticDate(),"yyyy-MM-dd");
                userStaticsMap.put(date,userStatics);
            }

            //按account统计oepnvalue和closevalue
            BigDecimal totalOpenValue = BigDecimal.ZERO;
            BigDecimal totalOpenValueSgd = BigDecimal.ZERO;
            BigDecimal totalCloseValue = BigDecimal.ZERO;
            BigDecimal totalCloseValueSgd = BigDecimal.ZERO;
            UserStaticsPO openStatic = userStaticsGoalList.get(0);
            UserStaticsPO closeStatic = userStaticsGoalList.get(userStaticsGoalList.size()-1);
            Date openDate = openStatic.getStaticDate();
            //Date openDate = getActualStartStatic; //Edited By WooiTatt
            String openDateStr = DateUtils.formatDate(openDate,"yyyy-MM-dd");
//            BigDecimal openFxr = openStatic.getFxRateForFundOut();
            BigDecimal openNavSgd = openStatic.getNavInSgd();
            BigDecimal openNavUsd = openStatic.getNavInUsd();
            //Date closeDate = closeStatic.getStaticDate();
            Date closeDate = endTime; // Edited By WooiTatt
            
            
            String openStaticDateStr = DateUtils.formatDate(openStatic.getStaticDate(),"yyyy-MM-dd");
            String actualStaticDateStr = DateUtils.formatDate(getStartStaticFilter,"yyyy-MM-dd");
            boolean isMonthLastDate = false;
            if(openStaticDateStr.equalsIgnoreCase(actualStaticDateStr)){
                isMonthLastDate = true;
            }
            
//            BigDecimal closeFxr = closeStatic.getFxRateForFundOut();
            BigDecimal closeNavSgd = closeStatic.getNavInSgd();
            BigDecimal closeNavUsd = closeStatic.getNavInUsd();
            String closeDateStr = DateUtils.formatDate(closeDate,"yyyy-MM-dd");

            List<UserStaticsPO> userStaticsOpenList = (List<UserStaticsPO>) userStaticsMap.get(openDateStr);
            List<UserStaticsPO> userStaticsCloseList = (List<UserStaticsPO>) userStaticsMap.get(closeDateStr);


            for(UserStaticsPO userStaticsPO:userStaticsOpenList) {
                UserFundNavPO userFundNavPO = new UserFundNavPO();
                userFundNavPO.setGoalId(userStaticsPO.getGoalId());
                userFundNavPO.setAccountId(userStaticsPO.getAccountId());
                userFundNavPO.setClientId(userStaticsPO.getClientId());
                userFundNavPO.setNavTime(userStaticsPO.getStaticDate());
                UserFundNavPO userFundNav = userFundNavService.selectOneByNavTime(userFundNavPO);
                if(userFundNav == null) {
                    AccountRechargePO accountRecharge = new AccountRechargePO();
                    accountRecharge.setAccountId(userStaticsPO.getAccountId());
                    accountRecharge.setClientId(userStaticsPO.getClientId());
                    accountRecharge.setGoalId(userStaticsPO.getGoalId());
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
                            totalOpenValueSgd = totalOpenValueSgd.add(firstSaxoAccountOrder.getCashAmount());
                        }
                    }
                }else{
                    //totalOpenValueSgd = totalOpenValueSgd.add(userStaticsPO.getAdjFundAssetInSgd());
                }
                //totalOpenValue = totalCloseValue.add(userStaticsPO.getAdjFundAsset());
                //totalOpenValue = totalOpenValue.add(userStaticsPO.getAdjFundAsset()); //Edit By WooiTatt
            }
            //Added By WooiTatt
            if(isMonthLastDate){
                totalOpenValue = openStatic.getAdjFundAsset(); 
                totalOpenValueSgd = openStatic.getAdjFundAssetInSgd();
            }else{
                totalOpenValue =  new BigDecimal("0");
                totalOpenValueSgd = new BigDecimal("0");
            }
            

            for(UserStaticsPO userStaticsPO:userStaticsCloseList) {
                totalCloseValue = totalCloseValue.add(userStaticsPO.getAdjFundAsset());
                totalCloseValueSgd = totalCloseValueSgd.add(userStaticsPO.getAdjFundAssetInSgd());
            }

            //按goal统计入金和出金
            BigDecimal deposit = BigDecimal.ZERO;
            BigDecimal depositSgd = BigDecimal.ZERO;

            BigDecimal withdrawal = BigDecimal.ZERO;
            BigDecimal withdrawalSgd = BigDecimal.ZERO;
            deposit = getUserGoalUsdRechargeMoney(accountUserPO.getClientId(),goalId, getStartStaticFilter, endTime);
            depositSgd = getUserGoalSgdRechargeMoney(accountUserPO.getClientId(),goalId, getStartStaticFilter, endTime);
            withdrawal = getUserGoalUsdRedeemMoney(accountUserPO.getClientId(),goalId, getStartStaticFilter, endTime);
            withdrawalSgd = getUserGoalSgdRedeemMoney(accountUserPO.getClientId(),goalId, getStartStaticFilter, endTime);

            glossaryPO.setDeposit(deposit);
            glossaryPO.setDepositSgd(depositSgd);
            glossaryPO.setWithdrawal(withdrawal);
            glossaryPO.setWithdrawalSgd(withdrawalSgd);
            glossaryPO.setGoalId(goalId);
            //查询goalName
            UserGoalInfoDTO userGoalInfoDTO = new UserGoalInfoDTO();
            userGoalInfoDTO.setClientId(accountUserPO.getClientId());
            userGoalInfoDTO.setGoalId(goalId);
            //RpcMessage<UserGoalInfoResDTO> userGoalInfoRes = userServiceRemoteService.getUserGoalInfo(userGoalInfoDTO);
            RpcMessage<UserGoalInfoResDTO> userGoalInfoRes = userServiceRemoteService.getUserGoalInfoForStatement(userGoalInfoDTO);
            if(userGoalInfoRes.isSuccess()) {
                UserGoalInfoResDTO userGoal = userGoalInfoRes.getContent();
                glossaryPO.setGoalName(userGoal.getGoalName());
            }else{
                glossaryPO.setGoalName(accountUserPO.getGoalId());
            }

            glossaryPO.setPortfolioOpenValue(totalOpenValue);
            glossaryPO.setPortfolioCloseValue(totalCloseValue);

            //获取对应日期的T1和T2
//            BigDecimal totalOpenValueSgd = totalOpenValue.multiply(openFxr).setScale(2,BigDecimal.ROUND_DOWN);
//            totalOpenValueSgd = totalOpenValueSgd.add(totalOpenFirstValue);
//            BigDecimal totalCloseValueSgd = totalCloseValue.multiply(closeFxr).setScale(2,BigDecimal.ROUND_DOWN);
            glossaryPO.setPortfolioOpenValueSgd(totalOpenValueSgd);
            glossaryPO.setPortfolioCloseValueSgd(totalCloseValueSgd);
            BigDecimal portfolioA = BigDecimal.ZERO;
            if(isMonthLastDate){
                portfolioA = totalCloseValue.subtract(withdrawal.negate()).subtract(deposit).divide(totalOpenValue, 6, BigDecimal.ROUND_DOWN).subtract(new BigDecimal("1"));
            }else{
                portfolioA = totalCloseValue.subtract(withdrawal.negate()).subtract(deposit).divide(deposit, 6, BigDecimal.ROUND_DOWN);
            }
            
            //BigDecimal portfolioA = closeNavUsd.divide(openNavUsd,6,BigDecimal.ROUND_DOWN).subtract(new BigDecimal(1));
            glossaryPO.setPortfolioA(portfolioA);
            glossaryPO.setFxImpact(BigDecimal.ZERO);
            glossaryPO.setTotalAb(portfolioA);
            
            BigDecimal portfolioASgd = BigDecimal.ZERO;
            if(isMonthLastDate){
                portfolioASgd = totalCloseValueSgd.subtract(withdrawalSgd.negate()).subtract(depositSgd).divide(totalOpenValueSgd, 6, BigDecimal.ROUND_DOWN).subtract(new BigDecimal("1"));
            }else{
                portfolioASgd = totalCloseValueSgd.subtract(withdrawalSgd.negate()).subtract(depositSgd).divide(depositSgd, 6, BigDecimal.ROUND_DOWN);
            }

            glossaryPO.setPortfolioASgd(portfolioA);
            glossaryPO.setTotalAbSgd(portfolioASgd);
            glossaryPO.setFxImpactSgd(portfolioASgd.subtract(portfolioA));
           /* glossaryPO.setPortfolioASgd(portfolioA);
            BigDecimal totalAb = closeNavSgd.divide(openNavSgd,6,BigDecimal.ROUND_DOWN).subtract(new BigDecimal(1));
            glossaryPO.setTotalAbSgd(totalAb);
            //(1+USD Return) / (1+SGD Return) -1
            BigDecimal fxImpactSgd = portfolioA.add(new BigDecimal(1))
                    .divide(totalAb.add(new BigDecimal(1)),6,BigDecimal.ROUND_DOWN)
                    .subtract(new BigDecimal(1));
            glossaryPO.setFxImpactSgd(fxImpactSgd);
            */
            
            glossaryPOList.add(glossaryPO);
        }


//        for(GlossaryPO glossaryPO : glossaryPOList){
//            //统一处理小数位
//            CalDecimal<GlossaryPO> calDecimal = new CalDecimal<>();
//            calDecimal.handleDot(glossaryPO);
//        }
        return glossaryPOList;
    }

    private BigDecimal getUserGoalSgdRechargeMoney(String clientId,String goalId, Date startDate, Date endDate) {
        SaxoAccountOrderPO sgdRechargeParam = new SaxoAccountOrderPO();
//        sgdRechargeParam.setAccountId(accountUserPO.getAccountId());
        sgdRechargeParam.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
        //sgdRechargeParam.setOperatorType(SaxoOrderTradeTypeEnum.COME_INTO);
        sgdRechargeParam.setOperatorType(SaxoOrderTradeTypeEnum.COME_OUT); // Edited By WooiTatt
        //sgdRechargeParam.setActionType(SaxoOrderActionTypeEnum.UOBTOSAXO);
        sgdRechargeParam.setActionType(SaxoOrderActionTypeEnum.RECHARGE_EXCHANGE);// Edited By WooiTatt
        sgdRechargeParam.setCurrency(CurrencyEnum.SGD);
        sgdRechargeParam.setClientId(clientId);
        sgdRechargeParam.setGoalId(goalId);
        sgdRechargeParam.setStartTradeTime(startDate);
        sgdRechargeParam.setEndTradeTime(endDate);
        return saxoAccountOrderService.getClientGoalMoney(sgdRechargeParam);
    }
    private BigDecimal getUserGoalSgdRedeemMoney(String clientId,String goalId, Date startDate, Date endDate) {
        SaxoAccountOrderPO sgdRechargeParam = new SaxoAccountOrderPO();
//        sgdRechargeParam.setAccountId(accountUserPO.getAccountId());
        sgdRechargeParam.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
        sgdRechargeParam.setOperatorType(SaxoOrderTradeTypeEnum.COME_OUT);
        sgdRechargeParam.setActionType(SaxoOrderActionTypeEnum.SAXOTOUOB);
        sgdRechargeParam.setCurrency(CurrencyEnum.SGD);
        sgdRechargeParam.setClientId(clientId);
        sgdRechargeParam.setGoalId(goalId);
        sgdRechargeParam.setStartTradeTime(startDate);
        sgdRechargeParam.setEndTradeTime(endDate);
        return saxoAccountOrderService.getClientGoalMoney(sgdRechargeParam);
    }

    private BigDecimal getUserGoalUsdRechargeMoney(String clientId,String goalId, Date startDate, Date endDate) {
        SaxoAccountOrderPO sgdRechargeParam = new SaxoAccountOrderPO();
//        sgdRechargeParam.setAccountId(accountUserPO.getAccountId());
        sgdRechargeParam.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
        sgdRechargeParam.setOperatorType(SaxoOrderTradeTypeEnum.COME_INTO);
        sgdRechargeParam.setActionType(SaxoOrderActionTypeEnum.RECHARGE_EXCHANGE);
        sgdRechargeParam.setCurrency(CurrencyEnum.USD);
        sgdRechargeParam.setClientId(clientId);
        sgdRechargeParam.setGoalId(goalId);
        sgdRechargeParam.setStartTradeTime(startDate);
        sgdRechargeParam.setEndTradeTime(endDate);
        return saxoAccountOrderService.getClientGoalMoney(sgdRechargeParam);
    }


    private BigDecimal getUserGoalUsdRedeemMoney(String clientId,String goalId, Date startDate, Date endDate) {
        SaxoAccountOrderPO sgdRedeemParam = new SaxoAccountOrderPO();
//        sgdRedeemParam.setAccountId(accountUserPO.getAccountId());
        sgdRedeemParam.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
        sgdRedeemParam.setOperatorType(SaxoOrderTradeTypeEnum.COME_OUT);
        sgdRedeemParam.setActionType(SaxoOrderActionTypeEnum.REDEEM_EXCHANGE);
        sgdRedeemParam.setCurrency(CurrencyEnum.USD);
        sgdRedeemParam.setGoalId(goalId);
        sgdRedeemParam.setClientId(clientId);
        sgdRedeemParam.setStartTradeTime(startDate);
        sgdRedeemParam.setEndTradeTime(endDate);
        return saxoAccountOrderService.getClientGoalMoney(sgdRedeemParam);
    }


}
