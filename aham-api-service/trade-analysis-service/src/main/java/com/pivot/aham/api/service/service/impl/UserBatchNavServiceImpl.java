package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.UserBatchNavMapper;
import com.pivot.aham.api.service.mapper.model.UserBatchNavPO;
import com.pivot.aham.api.service.service.UserBatchNavService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author WooiTatt
 */
@Service
public class UserBatchNavServiceImpl extends BaseServiceImpl<UserBatchNavPO, UserBatchNavMapper> implements UserBatchNavService {


    @Override
    public List<UserBatchNavPO> listUserBatchNavWithActive(UserBatchNavPO userBatchNavPO) {
        return mapper.listUserBatchNavWithActive(userBatchNavPO);
    }
    
    @Override
    public void updateTotalShare(UserBatchNavPO userBatchNavPO) {
        mapper.updateTotalShare(userBatchNavPO);
    }
    
    @Override
    public UserBatchNavPO queryLastBatchGoal(UserBatchNavPO userBatchNavPO) {
        return mapper.queryLastBatchGoal(userBatchNavPO);
    }
    
    @Override
    public List<UserBatchNavPO> listUserBatchNavGreateFundNav(UserBatchNavPO userBatchNavPO) {
        return mapper.listUserBatchNavGreateFundNav(userBatchNavPO);
    }
    
    @Override
    public UserBatchNavPO queryUserBatch(UserBatchNavPO userBatchNavPO) {
        return mapper.queryUserBatch(userBatchNavPO);
    }


}
