package com.pivot.aham.api.service.job.interevent;

import com.google.common.collect.Maps;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.pivot.aham.api.service.mapper.model.AccountRechargePO;
import com.pivot.aham.api.service.mapper.model.AccountRedeemPO;
import com.pivot.aham.api.service.mapper.model.UserGoalCashFlowPO;
import com.pivot.aham.api.service.service.UserGoalCashFlowService;
import com.pivot.aham.common.core.support.collection.IMultiTable;
import com.pivot.aham.common.core.support.collection.MultiTableFactory;
import com.pivot.aham.common.core.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 月报中间数据-用户每日现金流
 * 统计时机：每日交易分析修改tpcf和tncf之后
 *
 */
@Service
@Slf4j
public class UserGoalCashFlowListener {
    @Autowired
    private UserGoalCashFlowService userGoalCashFlowService;

    @Subscribe
    @AllowConcurrentEvents
    public void staticsUserGoalCashFlow(UserGoalCashFlowEvent userGoalCashFlowEvent){
        Long accountId = userGoalCashFlowEvent.getAccountId();
        //查询充值流水
        List<AccountRechargePO> accountRechargePOs = userGoalCashFlowEvent.getAccountRechargePOS();

        //按用户goal分组
        IMultiTable<String, String, AccountRechargePO> iMultiTableRecharge = MultiTableFactory.arrayListMultiTable();
        for(AccountRechargePO accountRechargePO:accountRechargePOs){
            iMultiTableRecharge.put(accountRechargePO.getClientId(),accountRechargePO.getGoalId(),accountRechargePO);
        }

        Map<String, Map<String, Collection<AccountRechargePO>>> iMultiTableRe = iMultiTableRecharge.rowMap();
        Map<String,UserGoalCashFlowPO> userGoalCashFlowMap = Maps.newHashMap();
        for (Map.Entry<String, Map<String, Collection<AccountRechargePO>>> entry : iMultiTableRe.entrySet()) {
            String clientId = entry.getKey();
            Map<String, Collection<AccountRechargePO>> valueMap = entry.getValue();
            for(Map.Entry<String,Collection<AccountRechargePO>> entry1:valueMap.entrySet()){
                String goalId = entry1.getKey();
                Collection<AccountRechargePO> rechargeCollection = entry1.getValue();
                BigDecimal tpcf = BigDecimal.ZERO;
                for (AccountRechargePO accountRecharge:rechargeCollection) {
                    tpcf = tpcf.add(accountRecharge.getRechargeAmount());
                }

                UserGoalCashFlowPO userGoalCash = getUserGoalCashFlowPO(accountId, clientId, goalId);


                //每个goal的tpcf
                String key = accountId+"_"+clientId+"_"+goalId;
                UserGoalCashFlowPO userGoalCashFlow = new UserGoalCashFlowPO();
                userGoalCashFlow.setAccountId(accountId);
                userGoalCashFlow.setClientId(clientId);
                userGoalCashFlow.setGoalId(goalId);
                userGoalCashFlow.setTpcf(tpcf);
                userGoalCashFlow.setStaticDate(DateUtils.now());
                if(userGoalCash != null){
                    userGoalCashFlow.setId(userGoalCash.getId());
                }
                userGoalCashFlowMap.put(key,userGoalCashFlow);
            }
        }
        //获取TNCF
        List<AccountRedeemPO> accountRedeemPOs = userGoalCashFlowEvent.getAccountRedeemPOs();
        //按用户goal分组
        IMultiTable<String, String, AccountRedeemPO> iMultiTablerRedeem = MultiTableFactory.arrayListMultiTable();
        for(AccountRedeemPO accountRedeemPO :accountRedeemPOs){
            iMultiTablerRedeem.put(accountRedeemPO.getClientId(),accountRedeemPO.getGoalId(),accountRedeemPO);
        }
        Map<String, Map<String, Collection<AccountRedeemPO>>> iMultiTableRedeem = iMultiTablerRedeem.rowMap();

        for (Map.Entry<String, Map<String, Collection<AccountRedeemPO>>> entry : iMultiTableRedeem.entrySet()) {
            String clientId = entry.getKey();
            Map<String, Collection<AccountRedeemPO>> valueMap = entry.getValue();
            for(Map.Entry<String,Collection<AccountRedeemPO>> entry1:valueMap.entrySet()){
                String goalId = entry1.getKey();
                Collection<AccountRedeemPO> redeemCollection = entry1.getValue();
                BigDecimal tncf = BigDecimal.ZERO;
                for (AccountRedeemPO accountRedeem:redeemCollection) {
                    tncf = tncf.add(accountRedeem.getApplyMoney());
                    //每个goal的tncf
                    String key = accountId+"_"+clientId+"_"+goalId;
                    UserGoalCashFlowPO userGoalCashFlowPO = userGoalCashFlowMap.get(key);
                    if(userGoalCashFlowPO != null){
                        userGoalCashFlowPO.setTncf(tncf);
                    }else{

                        UserGoalCashFlowPO userGoalCash = getUserGoalCashFlowPO(accountId, clientId, goalId);

                        UserGoalCashFlowPO userGoalCashFlow = new UserGoalCashFlowPO();
                        userGoalCashFlow.setAccountId(accountId);
                        userGoalCashFlow.setClientId(clientId);
                        userGoalCashFlow.setGoalId(goalId);
                        userGoalCashFlow.setTncf(tncf);
                        userGoalCashFlow.setStaticDate(DateUtils.now());
                        if(userGoalCash != null){
                            userGoalCashFlow.setId(userGoalCash.getId());
                        }
                        userGoalCashFlowMap.put(key,userGoalCashFlow);
                    }
                }
            }
        }
        for(Map.Entry<String,UserGoalCashFlowPO> userGoalCashFlowEntry:userGoalCashFlowMap.entrySet()){
            userGoalCashFlowService.updateOrInsert(userGoalCashFlowEntry.getValue());
        }
    }

    private UserGoalCashFlowPO getUserGoalCashFlowPO(Long accountId, String clientId, String goalId) {
        UserGoalCashFlowPO userGoalCashFlowQuery = new UserGoalCashFlowPO();
        userGoalCashFlowQuery.setAccountId(accountId);
        userGoalCashFlowQuery.setClientId(clientId);
        userGoalCashFlowQuery.setGoalId(goalId);
        userGoalCashFlowQuery.setStaticDate(new Date());
        return userGoalCashFlowService.selectByStaticDate(userGoalCashFlowQuery);
    }

}
