package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.UserRechargeStatus;
import com.pivot.aham.common.core.base.BaseMapper;
import java.math.BigDecimal;

/**
 * Created by WooiTatt
 */
public interface UserRechargeStatusMapper extends BaseMapper<UserRechargeStatus> {
    
    void saveUserRechargeStatus(UserRechargeStatus userRechargeStatus);
    
    void updateUserRechargeStatus(UserRechargeStatus userRechargeStatus);
    
    void updateUserRechargeStatusBySaxoOrderAccId(UserRechargeStatus userRechargeStatus);
    
    void updateUserRechargeStatusToSuccess(UserRechargeStatus userRechargeStatus);
    
    BigDecimal getPendingDeposit(UserRechargeStatus userRechargeStatus);

}
