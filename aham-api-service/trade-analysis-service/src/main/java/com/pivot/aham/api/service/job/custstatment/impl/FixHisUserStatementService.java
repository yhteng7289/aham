package com.pivot.aham.api.service.job.custstatment.impl;

import cn.hutool.core.util.ReflectUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.pivot.aham.api.server.dto.UserInfoResDTO;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.service.mapper.model.*;
import com.pivot.aham.api.service.service.*;
import com.pivot.aham.common.core.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 修复历史用户月报数据
 */
@Slf4j
@Service
public class FixHisUserStatementService {
    @Resource
    private AccountRedeemService accountRedeemService;
    @Resource
    private AccountRechargeService accountRechargeService;
    @Resource
    private UserGoalCashFlowService userGoalCashFlowService;
    @Resource
    private UserServiceRemoteService userServiceRemoteService;
    @Resource
    private AccountUserService accountUserService;
    @Resource
    private UserAssetService userAssetService;
    @Resource
    private UserEtfSharesService userEtfSharesService;
    @Resource
    private UserEtfSharesStaticService userEtfSharesStaticService;
    @Resource
    private AccountStaticsService accountStaticsService;
    @Resource
    private UserStaticsService userStaticsService;
    @Resource
    private UserFundNavService userFundNavService;

    private static ExecutorService executorService = new ThreadPoolExecutor(2, 20, 5, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(20), new ThreadPoolExecutor.DiscardOldestPolicy());

    public void fixUserGoalCashFlow(){
        //开始时间
        Date startTime1 = DateUtils.parseDate("2019-04-29");
        Date endTime = DateUtils.dayEnd(new Date());

        for(Date d=startTime1;d.compareTo(endTime)<=0;d=DateUtils.addDateByDay(d,10)){
            Date finalD = d;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    runCashTask(finalD,DateUtils.addDateByDay(finalD,10));
                }
            });
        }
    }


    private void runCashTask(Date startTime,Date endTime){
        for(Date d = startTime;d.compareTo(endTime)<=0;d=DateUtils.addDateByDay(d,1)){
            AccountRechargePO accountRechargeQuery = new AccountRechargePO();
            accountRechargeQuery.setCreateTime(d);
            List<AccountRechargePO> accountRechargeList = accountRechargeService.listAccountRecharge(accountRechargeQuery);
            //按account,client,goal分组
            Multimap<String,AccountRechargePO> multimapRecharge = ArrayListMultimap.create();
            for(AccountRechargePO accountRecharge:accountRechargeList){
                String key = accountRecharge.getAccountId()+"_"+accountRecharge.getClientId()+"_"+accountRecharge.getGoalId();
                multimapRecharge.put(key,accountRecharge);
            }

            AccountRedeemPO accountRedeemQuery = new AccountRedeemPO();
            accountRedeemQuery.setCreateTime(d);
            List<AccountRedeemPO> accountRedeemList = accountRedeemService.getRedeemListByTime(accountRedeemQuery);
            //按account,client,goal分组
            Multimap<String,AccountRedeemPO> multimapRedeem = ArrayListMultimap.create();
            for(AccountRedeemPO accountRedeem:accountRedeemList){
                String key = accountRedeem.getAccountId()+"_"+accountRedeem.getClientId()+"_"+accountRedeem.getGoalId();
                multimapRedeem.put(key,accountRedeem);
            }

            //求两个map的key的并集
            Set<String> rechargeSet = multimapRecharge.keySet();
            Set<String> redeemSet = multimapRedeem.keySet();
            Set<String> unionSet = Sets.union(rechargeSet,redeemSet);
            for(String key:unionSet){
                List<AccountRechargePO> rechargePOList = Lists.newArrayList();
                List<AccountRedeemPO> redeemPOList = Lists.newArrayList();
                rechargePOList = (List<AccountRechargePO>) multimapRecharge.get(key);
                redeemPOList = (List<AccountRedeemPO>) multimapRedeem.get(key);
                BigDecimal tpcf = BigDecimal.ZERO;
                BigDecimal tncf = BigDecimal.ZERO;
                if(rechargePOList != null) {
                    for (AccountRechargePO accountRechargePO : rechargePOList) {
                        tpcf = tpcf.add(accountRechargePO.getRechargeAmount());
                    }
                }

                if(redeemPOList != null) {
                    for (AccountRedeemPO accountRedeemPO : redeemPOList) {
                        tncf = tncf.add(accountRedeemPO.getApplyMoney());
                    }
                }

                String[] keys = key.split("_");
                UserGoalCashFlowPO userGoalCashFlowPO = new UserGoalCashFlowPO();
                userGoalCashFlowPO.setAccountId(Long.valueOf(keys[0]));
                userGoalCashFlowPO.setClientId(keys[1]);
                userGoalCashFlowPO.setGoalId(keys[2]);
                userGoalCashFlowPO.setTpcf(tpcf);
                userGoalCashFlowPO.setTncf(tncf);
                userGoalCashFlowPO.setStaticDate(d);

                UserGoalCashFlowPO userGoalCashFlowQuery = new UserGoalCashFlowPO();
                userGoalCashFlowQuery.setAccountId(Long.valueOf(keys[0]));
                userGoalCashFlowQuery.setClientId(keys[1]);
                userGoalCashFlowQuery.setGoalId(keys[2]);
                userGoalCashFlowQuery.setStaticDate(d);
                UserGoalCashFlowPO userGoalCashFlow = userGoalCashFlowService.selectByStaticDate(userGoalCashFlowQuery);

                if(userGoalCashFlow != null){
                    userGoalCashFlowPO.setId(userGoalCashFlow.getId());
                }
                userGoalCashFlowService.updateOrInsert(userGoalCashFlowPO);
            }
        }
    }

    public void fixEtfSharesStatics(){
        //获取所有用户
        List<UserInfoResDTO> userInfoResDTOS = userServiceRemoteService.queryUserList();

        //按用户分组
        Multimap<Integer,UserInfoResDTO> multimapGroup = ArrayListMultimap.create();
        for (UserInfoResDTO userInfoResDTO : userInfoResDTOS) {
            Integer groupNo = Math.abs(userInfoResDTO.getClientId().hashCode())%5;
            multimapGroup.put(groupNo,userInfoResDTO);
        }

        Set<Integer> sets = multimapGroup.keySet();
        for(Integer groupNo:sets){
            List<UserInfoResDTO> userInfoResDTOS1 = (List<UserInfoResDTO>) multimapGroup.get(groupNo);
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    runEtfTask(userInfoResDTOS1);
                }
            });
        }
    }

    private void runEtfTask(List<UserInfoResDTO> userInfoResDTOS1){
        for (UserInfoResDTO userInfoResDTO : userInfoResDTOS1) {
            //获取所有用户的goal
            AccountUserPO accountUserQuery = new AccountUserPO();
            accountUserQuery.setClientId(userInfoResDTO.getClientId());
            List<AccountUserPO> accountUserPOList = accountUserService.queryList(accountUserQuery);
            for (AccountUserPO accountUser : accountUserPOList) {
                UserAssetPO userAssetPO = new UserAssetPO();
                userAssetPO.setAccountId(accountUser.getAccountId());
                userAssetPO.setClientId(accountUser.getClientId());
                userAssetPO.setGoalId(accountUser.getGoalId());
//                userAssetPO.setAssetTime(new Date());
                List<UserAssetPO> userAssetPOList = userAssetService.queryListByTime(userAssetPO);
                //按日期分组
                Multimap<Date,UserAssetPO> multimapAsset = ArrayListMultimap.create();
                for(UserAssetPO userAsset:userAssetPOList){
                    multimapAsset.put(userAsset.getAssetTime(),userAsset);
                }
                //遍历日期分组
                Set<Date> dateSets = multimapAsset.keySet();
                for(Date date:dateSets){
                    List<UserAssetPO> userAssetList = (List<UserAssetPO>) multimapAsset.get(date);
                    handleEtf(userAssetList);
                }
            }
        }
    }

    //统计用户的etf持有
    public void handleEtf(List<UserAssetPO> userAssetPOs){
        Long accountId = null;
        String clientId = "";
        String goalId = "";
        Date assetTime = null;
        UserEtfSharesStaticPO userEtfSharesStaticPO = new UserEtfSharesStaticPO();
        if(userAssetPOs != null && userAssetPOs.size()>0){
            UserAssetPO userAssetPO = userAssetPOs.get(0);
            accountId=userAssetPO.getAccountId();
            clientId = userAssetPO.getClientId();
            goalId = userAssetPO.getGoalId();
            assetTime= userAssetPO.getAssetTime();
        }
        for(UserAssetPO userAsset:userAssetPOs) {
            UserEtfSharesPO userEtfSharesPO = new UserEtfSharesPO();
            userEtfSharesPO.setAccountId(accountId);
            userEtfSharesPO.setClientId(clientId);
            userEtfSharesPO.setGoalId(goalId);
            userEtfSharesPO.setProductCode(userAsset.getProductCode());
            userEtfSharesPO.setMoney(userAsset.getMoney());
            userEtfSharesPO.setShares(userAsset.getShare());
            userEtfSharesPO.setStaticDate(userAsset.getAssetTime());

            UserEtfSharesPO userEtfSharesQuery = new UserEtfSharesPO();
            userEtfSharesQuery.setAccountId(accountId);
            userEtfSharesQuery.setClientId(clientId);
            userEtfSharesQuery.setGoalId(goalId);
            userEtfSharesQuery.setProductCode(userAsset.getProductCode());
            userEtfSharesQuery.setStaticDate(userAsset.getAssetTime());
            UserEtfSharesPO userEtfShares = userEtfSharesService.selectByStaticDate(userEtfSharesQuery);
            if(userEtfShares != null){
                userEtfSharesPO.setId(userEtfShares.getId());
            }
            userEtfSharesService.updateOrInsert(userEtfSharesPO);


            userEtfSharesStaticPO.setAccountId(accountId);
            userEtfSharesStaticPO.setClientId(clientId);
            userEtfSharesStaticPO.setGoalId(goalId);
            userEtfSharesStaticPO.setStaticDate(userAsset.getAssetTime());

            Field[] openStaticfields = ReflectUtil.getFieldsDirectly(userEtfSharesStaticPO.getClass(),false);

            for(Field field:openStaticfields){
                String productCode = field.getName().toUpperCase();
                if (productCode.equals(userAsset.getProductCode())) {
                    ReflectUtil.setFieldValue(userEtfSharesStaticPO,field,userAsset.getShare());
                }
            }
        }
        UserEtfSharesStaticPO userEtfSharesStaticQuery = new UserEtfSharesStaticPO();
        userEtfSharesStaticQuery.setStaticDate(assetTime);
        userEtfSharesStaticQuery.setAccountId(accountId);
        userEtfSharesStaticQuery.setClientId(clientId);
        userEtfSharesStaticQuery.setGoalId(goalId);
        UserEtfSharesStaticPO userEtfSharesStatic = userEtfSharesStaticService.selectByStaticDate(userEtfSharesStaticQuery);
        if(userEtfSharesStatic != null){
            userEtfSharesStaticPO.setId(userEtfSharesStatic.getId());
        }
        userEtfSharesStaticService.updateOrInsert(userEtfSharesStaticPO);
    }

    public void fixUserStatics(){
        //开始时间
        Date startTime = DateUtils.parseDate("2019-04-29");
        //结束时间
        Date endTime = DateUtils.dayEnd(new Date());
        for(Date d = startTime;d.compareTo(endTime)<=0;d=DateUtils.addDateByDay(d,1)){
            AccountStaticsPO accountStaticsQuery = new AccountStaticsPO();
            accountStaticsQuery.setStaticDate(d);
            List<AccountStaticsPO> accountStaticsList = accountStaticsService.selectListByStaticDate(accountStaticsQuery);
            for(AccountStaticsPO accountStatics:accountStaticsList){
                handleUserStatics(accountStatics);
            }
        }
    }



    public void handleUserStatics(AccountStaticsPO accountStaticsPO){
        //获取该account下的所有用户份额
        UserFundNavPO userFundNavQuery = new UserFundNavPO();
        userFundNavQuery.setAccountId(accountStaticsPO.getAccountId());
        Date fundNavDate = DateUtils.addDateByDay(accountStaticsPO.getStaticDate(),1);
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

        for(UserFundNavPO userFundNavPO:userFundNavList){
            //计算比例
            BigDecimal precent = userFundNavPO.getTotalShare().divide(accountStaticsPO.getAdjFundShares(),6,BigDecimal.ROUND_DOWN);
            BigDecimal userCashDividend =  cashDividend.multiply(precent);
            BigDecimal userCustfee =  custFee.multiply(precent);
            BigDecimal userMgtFee =  mgtFee.multiply(precent);
            BigDecimal userGstMgtFee =  gstMgtFee.multiply(precent);
            BigDecimal userTransactionCostBuy =  transactionCostBuy.multiply(precent);
            BigDecimal userTransactionCostSell =  transactionCostSell.multiply(precent);
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

            UserStaticsPO userStaticsQuery = new UserStaticsPO();
            userStaticsQuery.setAccountId(userFundNavPO.getAccountId());
            userStaticsQuery.setGoalId(userFundNavPO.getGoalId());
            userStaticsQuery.setClientId(userFundNavPO.getClientId());
            userStaticsQuery.setStaticDate(accountStaticsPO.getStaticDate());
            UserStaticsPO userStaticsPO = userStaticsService.selectByStaticDate(userStaticsQuery);
            if(userStaticsPO != null){
                userStatics.setId(userStaticsPO.getId());
            }
            userStaticsService.updateOrInsert(userStatics);
        }
    }



}
