package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.pivot.aham.api.service.NewAccountRechargeService;
import com.pivot.aham.api.service.NewAccountRedeemService;
import com.pivot.aham.api.service.mapper.model.TAccountRechargePO;
import com.pivot.aham.api.service.mapper.model.TAccountRedeemPO;
import com.pivot.aham.api.server.remoteservice.TPCFRemoteService;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.Resource;
import com.pivot.aham.api.server.dto.AccountRechargeDTO;
import com.pivot.aham.api.server.dto.AccountRedeemDTO;
import com.pivot.aham.common.enums.recharge.TncfStatusEnum;
import com.pivot.aham.common.enums.recharge.TpcfStatusEnum;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.pivot.aham.common.core.util.DateUtils;

import java.util.List;

/**
 * Created by dexter on 17/4/2020
 */
@Service(interfaceClass = TPCFRemoteService.class)
@Slf4j
public class TPCFRemoteServiceImpl implements TPCFRemoteService {

    @Resource
    private NewAccountRechargeService newAccountRechargeService;

    @Resource
    private NewAccountRedeemService newAccountRedeemService;

    
    @Override
    public BigDecimal getTPCF(String checkDate){
        
        BigDecimal TPCF = BigDecimal.ZERO;
        
        AccountRechargeDTO accountRechargeDTO = new AccountRechargeDTO();
        
        List<TAccountRechargePO> rechargePOs = Lists.newArrayList();
        List<String> clientIdList = Lists.newArrayList();
        List<BigDecimal> rechargeAmountList = Lists.newArrayList();
    
        TAccountRechargePO queryParam = new TAccountRechargePO();
        queryParam.setTpcfStatus(TpcfStatusEnum.SUCCESS);
        
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        Date yesterday = null;
        
        checkDate = checkDate + " 18:00:00";
        
        try {
            date = sd.parse(checkDate);
            yesterday = DateUtils.addDays(date, -1);
            queryParam.setTpcfTime(date);
            queryParam.setRechargeTime(yesterday);
            
        }catch(Exception e){
            
            
        }
        
        rechargePOs = newAccountRechargeService.listByAccountId(queryParam);
        
        for(TAccountRechargePO rechargePO:rechargePOs){
        
            TPCF = TPCF.add(rechargePO.getRechargeAmount());
            
        
        }
        
        
        return TPCF;
    }
    
    @Override
    public List<BigDecimal> getRechargeAmount(String checkDate){
    
        
        AccountRechargeDTO accountRechargeDTO = new AccountRechargeDTO();
        
        List<TAccountRechargePO> rechargePOs = Lists.newArrayList();
        List<String> clientIdList = Lists.newArrayList();
        List<BigDecimal> rechargeAmountList = Lists.newArrayList();
    
        TAccountRechargePO queryParam = new TAccountRechargePO();
        queryParam.setTpcfStatus(TpcfStatusEnum.SUCCESS);
        
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        Date yesterday = null;
        
        checkDate = checkDate + " 18:00:00";
        
        try {
            date = sd.parse(checkDate);
            yesterday = DateUtils.addDays(date, -1);
            queryParam.setTpcfTime(date);
            queryParam.setRechargeTime(yesterday);
        }catch(Exception e){
            
        
        }
        
        rechargePOs = newAccountRechargeService.listByAccountId(queryParam);
        
        for(TAccountRechargePO rechargePO:rechargePOs){
        
            rechargeAmountList.add(rechargePO.getRechargeAmount());
            
        
        }
        
        
        return rechargeAmountList;
    
    }
    
    @Override
    public List<String> getRechargeClient(String checkDate){
    
        
        AccountRechargeDTO accountRechargeDTO = new AccountRechargeDTO();
        
        List<TAccountRechargePO> rechargePOs = Lists.newArrayList();
        List<String> clientIdList = Lists.newArrayList();
        List<BigDecimal> rechargeAmountList = Lists.newArrayList();
    
        TAccountRechargePO queryParam = new TAccountRechargePO();
        queryParam.setTpcfStatus(TpcfStatusEnum.SUCCESS);
        
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        Date yesterday = null;
        
        checkDate = checkDate + " 18:00:00";
        
        try {
            date = sd.parse(checkDate);
            yesterday = DateUtils.addDays(date, -1);
            queryParam.setTpcfTime(date);
            queryParam.setRechargeTime(yesterday);
            
            
        }catch(Exception e){
            
        
        }
        
        rechargePOs = newAccountRechargeService.listByAccountId(queryParam);
        
        for(TAccountRechargePO rechargePO:rechargePOs){
        
            clientIdList.add(rechargePO.getClientId());
            
        
        }
        
        
        return clientIdList;
    
    }
    
    @Override
    public BigDecimal getTNCF(String checkDate){
    
        BigDecimal TNCF = BigDecimal.ZERO;
        
        AccountRedeemDTO accountRedeemDTO = new AccountRedeemDTO();
        
        List<TAccountRedeemPO> redeemPOs = Lists.newArrayList();
        List<String> clientIdList = Lists.newArrayList();
        List<BigDecimal> redeemAmountList = Lists.newArrayList();
    
        TAccountRedeemPO queryParam = new TAccountRedeemPO();
        queryParam.setTncfStatus(TncfStatusEnum.SUCCESS);
        
        

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        Date yesterday = null;
        
        checkDate = checkDate + " 18:00:00";
        
        try {
            date = sd.parse(checkDate);
            yesterday = DateUtils.addDays(date, -1);
            queryParam.setTncfTime(date);
            queryParam.setRedeemApplyTime(yesterday);
            
            
        }catch(Exception e){
            
        
        }
        
        redeemPOs = newAccountRedeemService.listAccountRedeemByCond(queryParam);
        
        for(TAccountRedeemPO redeemPO:redeemPOs){
        
            TNCF = TNCF.add(redeemPO.getApplyMoney());
            
        
        }
        
        
        return TNCF;
    }
    
    @Override
    public List<BigDecimal> getRedeemAmount(String checkDate){
    
        BigDecimal TNCF = BigDecimal.ZERO;
        
        AccountRedeemDTO accountRedeemDTO = new AccountRedeemDTO();
        
        List<TAccountRedeemPO> redeemPOs = Lists.newArrayList();
        List<String> clientIdList = Lists.newArrayList();
        List<BigDecimal> redeemAmountList = Lists.newArrayList();
    
        TAccountRedeemPO queryParam = new TAccountRedeemPO();
        queryParam.setTncfStatus(TncfStatusEnum.SUCCESS);
        
        

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        Date yesterday = null;
        
        checkDate = checkDate + " 18:00:00";
        
        try {
            date = sd.parse(checkDate);
            yesterday = DateUtils.addDays(date, -1);
            queryParam.setTncfTime(date);
            queryParam.setRedeemApplyTime(yesterday);
            
            
        }catch(Exception e){
            
        
        }
        
        redeemPOs = newAccountRedeemService.listAccountRedeemByCond(queryParam);
        
        
        for(TAccountRedeemPO redeemPO:redeemPOs){
        
            redeemAmountList.add(redeemPO.getApplyMoney());
            
        
        }
        
        return redeemAmountList;
    }
    
    @Override
    public List<String> getRedeemClient(String checkDate){
    
        BigDecimal TNCF = BigDecimal.ZERO;
        
        AccountRedeemDTO accountRedeemDTO = new AccountRedeemDTO();
        
        List<TAccountRedeemPO> redeemPOs = Lists.newArrayList();
        List<String> clientIdList = Lists.newArrayList();
        List<BigDecimal> redeemAmountList = Lists.newArrayList();
    
        TAccountRedeemPO queryParam = new TAccountRedeemPO();
        queryParam.setTncfStatus(TncfStatusEnum.SUCCESS);
        
        

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        Date yesterday = null;
        
        checkDate = checkDate + " 18:00:00";
        
        try {
            date = sd.parse(checkDate);
            yesterday = DateUtils.addDays(date, -1);
            queryParam.setTncfTime(date);
            queryParam.setRedeemApplyTime(yesterday);
            
            
        }catch(Exception e){
            
        
        }
        
        redeemPOs = newAccountRedeemService.listAccountRedeemByCond(queryParam);
        
        for(TAccountRedeemPO redeemPO:redeemPOs){
        
            clientIdList.add(redeemPO.getClientId());
            
        
        }
        
        return clientIdList;
    }
    
}
