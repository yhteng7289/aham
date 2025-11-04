package com.pivot.aham.api.service.job.custstatment.impl;

import cn.hutool.core.util.ReflectUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pivot.aham.api.server.dto.UserGoalInfoDTO;
import com.pivot.aham.api.server.dto.UserGoalInfoResDTO;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.service.mapper.model.*;
import com.pivot.aham.api.service.service.AssetFundNavService;
import com.pivot.aham.api.service.service.UserEtfSharesStaticService;
import com.pivot.aham.api.service.service.UserGoalCashFlowService;
import com.pivot.aham.api.service.service.UserStaticsService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.CalDecimal;
import com.pivot.aham.common.core.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * annex类型月报
 */
@Service
@Slf4j
public class GenAnnex {
    @Autowired
    private UserGoalCashFlowService userGoalCashFlowService;
    @Autowired
    private UserStaticsService userStaticsService;
    @Autowired
    private UserEtfSharesStaticService userEtfSharesStaticService;
    @Autowired
    private AssetFundNavService assetFundNavService;
    @Autowired
    private UserServiceRemoteService userServiceRemoteService;

    public List<AnnexPO> genAnnexReport(AccountUserPO accountUserPO, Date startTime, Date endTime){
//        //获取月报开始时间和结束时间
//        Date lastMonth = DateUtils.addMonths(new Date(),-1);
//        Date startTime = DateUtils.monthStart(lastMonth);
//        Date endTime = DateUtils.monthEnd(lastMonth);
        //获取tpcf和tncf
        UserGoalCashFlowPO userGoalCashFlowPO = new UserGoalCashFlowPO();
        userGoalCashFlowPO.setGoalId(accountUserPO.getGoalId());
        userGoalCashFlowPO.setClientId(accountUserPO.getClientId());
        userGoalCashFlowPO.setAccountId(accountUserPO.getAccountId());
        userGoalCashFlowPO.setStartStaticDate(startTime);
        userGoalCashFlowPO.setEndStaticDate(endTime);
        List<UserGoalCashFlowPO> userGoalCashFlowList = userGoalCashFlowService.queryListByTime(userGoalCashFlowPO);
        //cashflows按年月日分组
        Map<String,UserGoalCashFlowPO> userGoalCashMap = Maps.newHashMap();
        for(UserGoalCashFlowPO userGoalCashFlow:userGoalCashFlowList){
            String date = DateUtils.formatDate(userGoalCashFlow.getStaticDate(),"yyyy-MM-dd");
            userGoalCashMap.put(date,userGoalCashFlow);
        }
        //获取etf资产
        UserEtfSharesStaticPO userEtfSharesStaticPO = new UserEtfSharesStaticPO();
        userEtfSharesStaticPO.setAccountId(accountUserPO.getAccountId());
        userEtfSharesStaticPO.setClientId(accountUserPO.getClientId());
        userEtfSharesStaticPO.setGoalId(accountUserPO.getGoalId());
        userEtfSharesStaticPO.setStartStaticDate(startTime);
        userEtfSharesStaticPO.setEndStaticDate(endTime);
        List<UserEtfSharesStaticPO> userEtfSharesStaticList = userEtfSharesStaticService.queryListByTime(userEtfSharesStaticPO);

        //etf资产按时间分组
        Map<String,UserEtfSharesStaticPO> userEtfSharesStaticMap = Maps.newHashMap();
        for(UserEtfSharesStaticPO userEtfSharesStatic:userEtfSharesStaticList){
            String date = DateUtils.formatDate(userEtfSharesStatic.getStaticDate(),"yyyy-MM-dd");
            userEtfSharesStaticMap.put(date,userEtfSharesStatic);
        }

        //获取过程数据
        UserStaticsPO userStaticsQuery = new UserStaticsPO();
        userStaticsQuery.setGoalId(accountUserPO.getGoalId());
        userStaticsQuery.setAccountId(accountUserPO.getAccountId());
        userStaticsQuery.setClientId(accountUserPO.getClientId());
        userStaticsQuery.setStartStaticDate(startTime);
        userStaticsQuery.setEndStaticDate(endTime);
        List<UserStaticsPO> userStaticsList = userStaticsService.queryListByTime(userStaticsQuery);

        //过程数据按时间分组
        Map<String,UserStaticsPO> userStaticsMap = Maps.newHashMap();
        for(UserStaticsPO userStatics:userStaticsList){
            String date = DateUtils.formatDate(userStatics.getStaticDate(),"yyyy-MM-dd");
            userStaticsMap.put(date,userStatics);
        }

        if(userStaticsList.size()==0 || userEtfSharesStaticList.size()==0){
            return Lists.newArrayList();
        }
        //按天组成报表
        List<AnnexPO> annexList = Lists.newArrayList();
//        int day=0;
//        Date endDate = DateUtils.dayStart(DateUtils.monthEnd(lastMonth));
//        while (true){
        for(UserStaticsPO userStaticsPO:userStaticsList) {
            AnnexPO annexPO = new AnnexPO();
            annexPO.setGoalId(accountUserPO.getGoalId());
            //查询goalName
            UserGoalInfoDTO userGoalInfoDTO = new UserGoalInfoDTO();
            userGoalInfoDTO.setClientId(accountUserPO.getClientId());
            userGoalInfoDTO.setGoalId(accountUserPO.getGoalId());
            RpcMessage<UserGoalInfoResDTO> userGoalInfoRes = userServiceRemoteService.getUserGoalInfo(userGoalInfoDTO);
            if(userGoalInfoRes.isSuccess()) {
                UserGoalInfoResDTO userGoal = userGoalInfoRes.getContent();
                annexPO.setGoalName(userGoal.getGoalName());
            }else{
                annexPO.setGoalName(accountUserPO.getGoalId());
            }
            annexPO.setTotalAsset(userStaticsPO.getAdjFundAsset());
//            Date date = DateUtils.addDateByDay(startTime, day);
            Date etfDate = DateUtils.addDateByDay(userStaticsPO.getStaticDate(),1);
            String etfDateStr = DateUtils.formatDate(etfDate, "yyyy-MM-dd");
            String dateStr = DateUtils.formatDate(userStaticsPO.getStaticDate(), "yyyy-MM-dd");
            annexPO.setStaticDate(DateUtils.parseDate(dateStr));
            UserGoalCashFlowPO userGoalCashFlow = userGoalCashMap.get(dateStr);
            BigDecimal cashFlow = BigDecimal.ZERO;
            if (userGoalCashFlow != null) {
                cashFlow = userGoalCashFlow.getTpcf().subtract(userGoalCashFlow.getTncf());
            }

            annexPO.setCashFlow(cashFlow);
            UserStaticsPO userStatics = userStaticsMap.get(dateStr);
            if (userStatics != null) {
                annexPO.setCashHolding(userStatics.getAdjCashHolding());
                annexPO.setNavInSgd(userStatics.getNavInSgd());
                annexPO.setNavInUsd(userStatics.getNavInUsd());
                BigDecimal transFee = userStatics.getTransactionCostBuy().add(userStatics.getTransactionCostSell());
                annexPO.setTransactionFee(transFee);
                annexPO.setMgtFee(userStatics.getMgtFee());
                annexPO.setCustFee(userStatics.getCustFee());
                annexPO.setGstMgtFee(userStatics.getGstMgtFee());
                BigDecimal totalFee = transFee.add(userStatics.getMgtFee()).add(userStatics.getCustFee());
                annexPO.setTotalFee(totalFee);
                annexPO.setFxRateForFundOut(userStatics.getFxRateForFundOut());
            }

            UserEtfSharesStaticPO userEtfSharesStatic = userEtfSharesStaticMap.get(etfDateStr);
            if (userEtfSharesStatic != null) {
                Date openClosePriceDate = DateUtils.addDateByDay(etfDate, -1);
                Map<String, BigDecimal> etfOpenClosingPriceMap = assetFundNavService.getEtfClosingPrice(openClosePriceDate);

                Field[] openStaticfields = ReflectUtil.getFieldsDirectly(userEtfSharesStatic.getClass(),false);

                for(Field field:openStaticfields) {
                    if (field.getType() != BigDecimal.class) {
                        continue;
                    }
                    BigDecimal etfPrice = etfOpenClosingPriceMap.get(field.getName().toUpperCase());
                    BigDecimal etfShares = (BigDecimal) ReflectUtil.getFieldValue(userEtfSharesStatic,field);
                    BigDecimal etfVaule = BigDecimal.ZERO;
                    if(etfShares != null&&etfPrice != null){
                        etfVaule = etfShares.multiply(etfPrice);
                    }else{
                        log.error("产品:{},没有收市价或价格,{},{}",field.getName().toUpperCase(),etfShares,etfVaule);
                    }

                    ReflectUtil.setFieldValue(annexPO,field.getName(),etfVaule);
                }
            }
            if (userEtfSharesStatic != null || userGoalCashFlow != null || userStatics != null) {
                annexList.add(annexPO);
            }
        }

        //统一处理小数位
        CalDecimal<AnnexPO> calDecimal = new CalDecimal<>();
        for(AnnexPO annexPO : annexList) {
            calDecimal.handleDot(annexPO);
        }


        return  annexList;
    }
}
