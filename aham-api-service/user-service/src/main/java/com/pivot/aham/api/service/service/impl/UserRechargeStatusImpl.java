package com.pivot.aham.api.service.service.impl;


import com.pivot.aham.api.service.mapper.UserRechargeStatusMapper;
import com.pivot.aham.api.service.mapper.model.UserRechargeStatus;
import com.pivot.aham.api.service.service.BankVirtualAccountService;
import com.pivot.aham.api.service.service.UserRechargeStatusService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class UserRechargeStatusImpl extends BaseServiceImpl<UserRechargeStatus, UserRechargeStatusMapper> implements UserRechargeStatusService {

    @Resource
    private BankVirtualAccountService bankVirtualAccountService;

    @Override
    @Transactional
    public void saveUserRechargeStatus(UserRechargeStatus userRechargeStatus) {
        mapper.saveUserRechargeStatus(userRechargeStatus);
    }
    
    @Override
    @Transactional
    public void updateUserRechargeStatus(UserRechargeStatus userRechargeStatus) {
        mapper.updateUserRechargeStatus(userRechargeStatus);
    }
    
    @Override
    @Transactional
    public void updateUserRechargeStatusBySaxoOrderAccId(UserRechargeStatus userRechargeStatus) {
        mapper.updateUserRechargeStatusBySaxoOrderAccId(userRechargeStatus);
    }
    
    @Override
    @Transactional
    public void updateUserRechargeStatusToSuccess(UserRechargeStatus userRechargeStatus) {
        mapper.updateUserRechargeStatusToSuccess(userRechargeStatus);
    }
    
    @Override
    @Transactional
    public BigDecimal getPendingDeposit(UserRechargeStatus userRechargeStatus) {
        return mapper.getPendingDeposit(userRechargeStatus);
    }
}
