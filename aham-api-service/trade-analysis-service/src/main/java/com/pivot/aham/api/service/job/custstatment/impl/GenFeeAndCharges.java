package com.pivot.aham.api.service.job.custstatment.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.pivot.aham.api.server.dto.UserGoalInfoDTO;
import com.pivot.aham.api.server.dto.UserGoalInfoResDTO;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.service.mapper.model.AccountUserPO;
import com.pivot.aham.api.service.mapper.model.FeeAndChargesPO;
import com.pivot.aham.api.service.mapper.model.UserStaticsPO;
import com.pivot.aham.api.service.service.UserStaticsService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.CalDecimal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class GenFeeAndCharges {
    @Autowired
    private UserStaticsService userStaticsService;
    @Autowired
    private UserServiceRemoteService userServiceRemoteService;
    public List<FeeAndChargesPO> genFeeAndCharges(AccountUserPO accountUserPO,Date startTime,Date endTime){
        List<FeeAndChargesPO> feeAndChargesList = Lists.newArrayList();
        //获取开始时间和结束时间
//        Date lastMonth = DateUtils.addMonths(new Date(),-1);
//        Date startTime = DateUtils.monthStart(lastMonth);
//        Date endTime = DateUtils.monthEnd(lastMonth);

        //获取某个client的所有资产流水
        UserStaticsPO userStaticsQuery = new UserStaticsPO();
        userStaticsQuery.setClientId(accountUserPO.getClientId());
//        userStaticsQuery.setGoalId(accountUserPO.getGoalId());
        userStaticsQuery.setStartStaticDate(startTime);
        userStaticsQuery.setEndStaticDate(endTime);
        List<UserStaticsPO> userStaticsList = userStaticsService.queryListByTime(userStaticsQuery);
        if(CollectionUtils.isEmpty(userStaticsList)){
            log.error("统计用户资产错误,用户本月无资产记录:{}", JSON.toJSONString(userStaticsQuery));
            return feeAndChargesList;
        }
        //按goal分组
        Multimap<String, UserStaticsPO> multimap = ArrayListMultimap.create();
        for (UserStaticsPO userStaticsPO : userStaticsList) {
            multimap.put(userStaticsPO.getGoalId(), userStaticsPO);
        }

        //按account统计oepnvalue和closevalue
        Set<String> goalKeySet = multimap.keySet();
        for (String goalId : goalKeySet) {
            Collection<UserStaticsPO> userStaticsPOS = multimap.get(goalId);
            BigDecimal totalMgtFee = BigDecimal.ZERO;
            BigDecimal totalCustFee = BigDecimal.ZERO;
            BigDecimal totalGstMgtFee = BigDecimal.ZERO;

            BigDecimal totalMgtFeeSgd = BigDecimal.ZERO;
            BigDecimal totalCustFeeSgd = BigDecimal.ZERO;
            BigDecimal totalGstMgtFeeSgd = BigDecimal.ZERO;

//            BigDecimal fxr = BigDecimal.ZERO;
            BigDecimal totalAsset = BigDecimal.ZERO;
            BigDecimal totalAssetSgd = BigDecimal.ZERO;
            int day = 0;
            for(UserStaticsPO userStaticsPO:userStaticsPOS) {
                BigDecimal fxr = userStaticsPO.getFxRateForFundOut();
                totalAsset = totalAsset.add(userStaticsPO.getAdjFundAsset());
                totalMgtFee = totalMgtFee.add(userStaticsPO.getMgtFee());
                totalCustFee = totalCustFee.add(userStaticsPO.getCustFee());
                totalGstMgtFee = totalGstMgtFee.add(userStaticsPO.getGstMgtFee());

                totalAssetSgd = totalAssetSgd.add(userStaticsPO.getAdjFundAssetInSgd());
                totalMgtFeeSgd = totalMgtFeeSgd.add(userStaticsPO.getMgtFee().multiply(fxr));
                totalCustFeeSgd = totalCustFeeSgd.add(userStaticsPO.getCustFee().multiply(fxr));
                totalGstMgtFeeSgd = totalGstMgtFeeSgd.add(userStaticsPO.getGstMgtFee().multiply(fxr));
                day++;
            }
            FeeAndChargesPO feeAndCharges = new FeeAndChargesPO();
            if(totalAsset.compareTo(BigDecimal.ZERO)>0) {
                BigDecimal monthlyAvgAsset = totalAsset.divide(new BigDecimal(day), 6, BigDecimal.ROUND_DOWN);
                BigDecimal monthlyAvgAssetSgd = totalAssetSgd.divide(new BigDecimal(day), 6, BigDecimal.ROUND_DOWN);
                feeAndCharges.setMonthlyAvgAsset(monthlyAvgAsset);
                feeAndCharges.setMonthlyAvgAssetSgd(monthlyAvgAssetSgd);
            }

            feeAndCharges.setGoalId(goalId);
            //查询goalName
            UserGoalInfoDTO userGoalInfoDTO = new UserGoalInfoDTO();
            userGoalInfoDTO.setClientId(accountUserPO.getClientId());
            userGoalInfoDTO.setGoalId(goalId);
            RpcMessage<UserGoalInfoResDTO> userGoalInfoRes = userServiceRemoteService.getUserGoalInfo(userGoalInfoDTO);
            if(userGoalInfoRes.isSuccess()) {
                UserGoalInfoResDTO userGoal = userGoalInfoRes.getContent();
                feeAndCharges.setGoalName(userGoal.getGoalName());
            }else{
                feeAndCharges.setGoalName(accountUserPO.getGoalId());
            }


            feeAndCharges.setCustFee(totalCustFee);
            feeAndCharges.setGstMgtFee(totalGstMgtFee);
            feeAndCharges.setMgtFee(totalMgtFee);

            feeAndCharges.setMgtFeeSgd(totalMgtFeeSgd);
            feeAndCharges.setCustFeeSgd(totalCustFeeSgd);
            feeAndCharges.setGstMgtFeeSgd(totalGstMgtFeeSgd);

            feeAndChargesList.add(feeAndCharges);
        }

        //统一处理小数位
        for(FeeAndChargesPO feeAndChargesPO:feeAndChargesList){
            CalDecimal<FeeAndChargesPO> calDecimal = new CalDecimal<>();
            calDecimal.handleDot(feeAndChargesPO);
        }


        return feeAndChargesList;
    }
}
