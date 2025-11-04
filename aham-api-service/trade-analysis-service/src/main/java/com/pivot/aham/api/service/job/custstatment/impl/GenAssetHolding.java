package com.pivot.aham.api.service.job.custstatment.impl;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.pivot.aham.api.server.dto.UserGoalInfoDTO;
import com.pivot.aham.api.server.dto.UserGoalInfoResDTO;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.service.mapper.model.AccountUserPO;
import com.pivot.aham.api.service.mapper.model.AssetHoldingPO;
import com.pivot.aham.api.service.mapper.model.UserDividendPO;
import com.pivot.aham.api.service.mapper.model.UserEtfSharesStaticPO;
import com.pivot.aham.api.service.mapper.model.UserStaticsPO;
import com.pivot.aham.api.service.service.AssetFundNavService;
import com.pivot.aham.api.service.service.UserDividendService;
import com.pivot.aham.api.service.service.UserEtfSharesStaticService;
import com.pivot.aham.api.service.service.UserStaticsService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GenAssetHolding {
    @Autowired
    private UserEtfSharesStaticService userEtfSharesStaticService;
    @Autowired
    private UserDividendService userDividendService;
    @Resource
    private UserStaticsService userStaticsService;
    @Resource
    private AssetFundNavService assetFundNavService;
    @Autowired
    private UserServiceRemoteService userServiceRemoteService;

    public List<AssetHoldingPO> genAssetHolding(AccountUserPO accountUserPO,Date startTime,Date endTime){
        List<AssetHoldingPO> assetHoldingList = Lists.newArrayList();

        //获取开始时间和结束时间
                //Edited By WooiTatt
        Date lastMonth = DateUtils.addMonths(startTime, -1); 
        Date startTimeStatic = DateUtils.monthEnd(lastMonth);
//        Date lastMonth = DateUtils.addMonths(new Date(),-1);
//        Date startTime = DateUtils.monthStart(lastMonth);
//        Date endTime = DateUtils.monthEnd(lastMonth);

        UserEtfSharesStaticPO userEtfSharesStaticPO = new UserEtfSharesStaticPO();
        userEtfSharesStaticPO.setAccountId(accountUserPO.getAccountId());
        userEtfSharesStaticPO.setClientId(accountUserPO.getClientId());
        userEtfSharesStaticPO.setGoalId(accountUserPO.getGoalId());
        //userEtfSharesStaticPO.setStartStaticDate(startTime);
        userEtfSharesStaticPO.setStartStaticDate(startTimeStatic); //Edited By WooiTatt
        userEtfSharesStaticPO.setEndStaticDate(endTime);
        List<UserEtfSharesStaticPO> userEtfSharesStaticList = userEtfSharesStaticService.queryListByTime(userEtfSharesStaticPO);
        if(CollectionUtils.isEmpty(userEtfSharesStaticList)){
            log.error("统计用户资产错误,用户本月无etf统计记录:{}",JSON.toJSONString(userEtfSharesStaticPO));
            return null;
        }
        UserEtfSharesStaticPO openStatic = userEtfSharesStaticList.get(0);
        UserEtfSharesStaticPO closeStatic = userEtfSharesStaticList.get(userEtfSharesStaticList.size()-1);

        String closeDateStr = DateUtils.formatDate(closeStatic.getStaticDate(),"yyyy-MM-dd");
        String endDateStr = DateUtils.formatDate(endTime,"yyyy-MM-dd");
        boolean isMonthLastDate = false;
        if(closeDateStr.equalsIgnoreCase(endDateStr)){
            isMonthLastDate = true;
        }
        
        
        //获取时间段内所有分红
        UserDividendPO userDividendParam = new UserDividendPO();
        userDividendParam.setAccountId(accountUserPO.getAccountId());
        userDividendParam.setClientId(accountUserPO.getClientId());
        userDividendParam.setGoalId(accountUserPO.getGoalId());
        userDividendParam.setStartDividendDate(startTime);
        userDividendParam.setEndDividendDate(endTime);
        List<UserDividendPO> userDividendPOS = userDividendService.queryUserByTime(userDividendParam);

        //分红按etf分组
        Multimap<String,UserDividendPO> dividendPOMultimap = ArrayListMultimap.create();
        for(UserDividendPO userDividend:userDividendPOS){
            dividendPOMultimap.put(userDividend.getProductCode(),userDividend);
        }

        Field[] openStaticfields = ReflectUtil.getFieldsDirectly(openStatic.getClass(),false);

        for(Field field:openStaticfields){
            if(field.getType() != BigDecimal.class){
                continue;
            }
            AssetHoldingPO assetHolding = new AssetHoldingPO();
            assetHolding.setProductCode(field.getName().toUpperCase());
            BigDecimal totalDividend = BigDecimal.ZERO;
            Collection<UserDividendPO> aaxjDividend = dividendPOMultimap.get(field.getName());
            for(UserDividendPO userDividendPO:aaxjDividend){
                totalDividend = totalDividend.add(userDividendPO.getDividendAmount());
            }
            BigDecimal etfOpenShares = (BigDecimal) ReflectUtil.getFieldValue(openStatic,field);
            Field closeField = ReflectUtil.getField(closeStatic.getClass(),field.getName());
            BigDecimal etfCloseShares = (BigDecimal) ReflectUtil.getFieldValue(closeStatic,closeField);

            //获取当时的收盘价
            Date openClosePriceDate = DateUtils.addDateByDay(openStatic.getStaticDate(), -1);
            Map<String, BigDecimal> etfOpenClosingPriceMap = assetFundNavService.getEtfClosingPrice(openClosePriceDate);

            Date closeClosePriceDate = DateUtils.addDateByDay(closeStatic.getStaticDate(), -1);
            Map<String, BigDecimal> etfCloseClosingPriceMap = assetFundNavService.getEtfClosingPrice(closeClosePriceDate);

            BigDecimal openPrice = etfOpenClosingPriceMap.get(field.getName().toUpperCase());
            BigDecimal closePrice = etfCloseClosingPriceMap.get(field.getName().toUpperCase());
            if(openPrice == null){
                log.error("日期:{},etf:{},没有收盘价",openClosePriceDate,field.getName().toUpperCase());
                openPrice = BigDecimal.ZERO;
            }
            if(closePrice == null){
                log.error("日期:{},etf:{},没有收盘价",closeClosePriceDate,field.getName().toUpperCase());
                closePrice = BigDecimal.ZERO;
            }

            BigDecimal openValue = etfOpenShares.multiply(openPrice).setScale(2,BigDecimal.ROUND_DOWN);
            BigDecimal closeValue = etfCloseShares.multiply(closePrice).setScale(2,BigDecimal.ROUND_DOWN);;

            assetHolding.setOpenValue(openValue);
            if(isMonthLastDate){
                assetHolding.setCloseValue(closeValue);
            }else{
                assetHolding.setCloseValue(new BigDecimal("0"));
            }
            assetHolding.setGoalId(accountUserPO.getGoalId());
            //查询goalName
            UserGoalInfoDTO userGoalInfoDTO = new UserGoalInfoDTO();
            userGoalInfoDTO.setClientId(accountUserPO.getClientId());
            userGoalInfoDTO.setGoalId(accountUserPO.getGoalId());
            RpcMessage<UserGoalInfoResDTO> userGoalInfoRes = userServiceRemoteService.getUserGoalInfo(userGoalInfoDTO);
            if(userGoalInfoRes.isSuccess()) {
                UserGoalInfoResDTO userGoal = userGoalInfoRes.getContent();
                assetHolding.setGoalName(userGoal.getGoalName());
            }else{
                assetHolding.setGoalName(accountUserPO.getGoalId());
            }


            assetHolding.setDividendRecive(totalDividend);
            assetHoldingList.add(assetHolding);
        }

        //cash封装
        //获取过程数据
        UserStaticsPO userStaticsQuery = new UserStaticsPO();
        userStaticsQuery.setGoalId(accountUserPO.getGoalId());
        userStaticsQuery.setAccountId(accountUserPO.getAccountId());
        userStaticsQuery.setClientId(accountUserPO.getClientId());
        userStaticsQuery.setStartStaticDate(startTime);
        userStaticsQuery.setEndStaticDate(endTime);
        List<UserStaticsPO> userStaticsList = userStaticsService.queryListByTime(userStaticsQuery);
        if(CollectionUtils.isEmpty(userStaticsList)){
            log.error("统计用户资产错误,用户本月无statics统计记录:{}", JSON.toJSONString(userStaticsQuery));
            return null;
        }
        //第一天
        UserStaticsPO openUserStatic = userStaticsList.get(0);
        //最后一天
        UserStaticsPO closeUserStatic = userStaticsList.get(userStaticsList.size()-1);
        AssetHoldingPO assetHoldingOpenCash = new AssetHoldingPO();
        assetHoldingOpenCash.setProductCode("Cash");
        assetHoldingOpenCash.setOpenValue(openUserStatic.getAdjCashHolding());
        if(isMonthLastDate){
            assetHoldingOpenCash.setCloseValue(closeUserStatic.getAdjCashHolding());
        }else{
            assetHoldingOpenCash.setCloseValue(new BigDecimal("0"));
        }
        assetHoldingOpenCash.setGoalId(accountUserPO.getGoalId());
        assetHoldingOpenCash.setDividendRecive(BigDecimal.ZERO);
        assetHoldingList.add(assetHoldingOpenCash);

        //计算配比
        BigDecimal totalOpen = BigDecimal.ZERO;
        for(AssetHoldingPO assetHolding:assetHoldingList){
            totalOpen = totalOpen.add(assetHolding.getOpenValue());
        }
        for (AssetHoldingPO assetHolding:assetHoldingList){
            BigDecimal precent = assetHolding.getOpenValue().divide(totalOpen, 4, BigDecimal.ROUND_HALF_UP);
            assetHolding.setOpenPrecnet(precent);
        }

        //尾差处理
        BigDecimal totalPrecent = BigDecimal.ZERO;
        for(AssetHoldingPO assetHolding :assetHoldingList){
            totalPrecent = totalPrecent.add(assetHolding.getOpenPrecnet());
        }
        BigDecimal resdiual = new BigDecimal(1).subtract(totalPrecent);
        if(resdiual.compareTo(BigDecimal.ZERO) != 0) {
            for (AssetHoldingPO assetHolding : assetHoldingList) {
                BigDecimal lastPrecent = assetHolding.getOpenPrecnet();
                if(lastPrecent.compareTo(BigDecimal.ZERO)==0){
                    continue;
                }
                if(resdiual.compareTo(BigDecimal.ZERO)<0){
                    if(lastPrecent.compareTo(resdiual.abs())<0){
                        continue;
                    }
                }
                lastPrecent = lastPrecent.add(resdiual);
                assetHolding.setOpenPrecnet(lastPrecent);
                break;
            }
        }
        //统一处理小数位
//        for(AssetHoldingPO assetHoldingPO:assetHoldingList) {
//            CalDecimal<AssetHoldingPO> calDecimal = new CalDecimal<>();
//            calDecimal.handleDot(assetHoldingPO);
//        }



        List<AssetHoldingPO> resList = Lists.newArrayList();
        for(AssetHoldingPO assetHoldingPO:assetHoldingList) {
            if(assetHoldingPO.getOpenValue().compareTo(BigDecimal.ZERO)!=0
            || assetHoldingPO.getCloseValue().compareTo(BigDecimal.ZERO)!=0
            || assetHoldingPO.getDividendRecive().compareTo(BigDecimal.ZERO)!=0){
                resList.add(assetHoldingPO);
            }
        }
        return resList;

    }
}
