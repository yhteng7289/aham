package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.UserBatchNavPO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by WooiTatt
 */
@Repository
public interface UserBatchNavMapper extends BaseMapper<UserBatchNavPO> {

    List<UserBatchNavPO> listUserBatchNavWithActive(UserBatchNavPO userBatchNavPO);
    
    void updateTotalShare(UserBatchNavPO userBatchNavPO);
    
    UserBatchNavPO queryLastBatchGoal(UserBatchNavPO userBatchNavPO);
    
    List<UserBatchNavPO> listUserBatchNavGreateFundNav(UserBatchNavPO userBatchNavPO);
    
    UserBatchNavPO queryUserBatch(UserBatchNavPO userBatchNavPO);

}
