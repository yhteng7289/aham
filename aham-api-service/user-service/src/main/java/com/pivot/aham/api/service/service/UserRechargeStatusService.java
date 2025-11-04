package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.UserRechargeStatus;
import com.pivot.aham.common.core.base.BaseService;
import java.math.BigDecimal;


public interface UserRechargeStatusService extends BaseService<UserRechargeStatus> {

    void saveUserRechargeStatus(UserRechargeStatus userRechargeStatus);
    
    void updateUserRechargeStatus(UserRechargeStatus userRechargeStatus);
    
    void updateUserRechargeStatusBySaxoOrderAccId(UserRechargeStatus userRechargeStatus);
    
    void updateUserRechargeStatusToSuccess(UserRechargeStatus userRechargeStatus);
    
    BigDecimal getPendingDeposit(UserRechargeStatus userRechargeStatus);
}
