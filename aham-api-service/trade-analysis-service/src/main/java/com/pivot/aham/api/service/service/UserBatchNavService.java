package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.UserBatchNavPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;

/**
 * Created by WooiTatt
 */
public interface UserBatchNavService extends BaseService<UserBatchNavPO> {

    List<UserBatchNavPO> listUserBatchNavWithActive(UserBatchNavPO userBatchNavPO);
    
    void updateTotalShare(UserBatchNavPO userBatchNavPO);
    
    UserBatchNavPO queryLastBatchGoal(UserBatchNavPO userBatchNavPO);
    
    List<UserBatchNavPO> listUserBatchNavGreateFundNav(UserBatchNavPO userBatchNavPO);
    
    UserBatchNavPO queryUserBatch(UserBatchNavPO userBatchNavPO);

}
