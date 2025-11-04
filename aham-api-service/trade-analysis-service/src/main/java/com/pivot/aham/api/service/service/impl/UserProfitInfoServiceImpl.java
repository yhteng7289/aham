package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.UserProfitInfoMapper;
import com.pivot.aham.api.service.mapper.model.UserProfitInfoPO;
import com.pivot.aham.api.service.service.UserProfitInfoService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author addison
 */
@Service
public class UserProfitInfoServiceImpl extends BaseServiceImpl<UserProfitInfoPO, UserProfitInfoMapper> implements UserProfitInfoService {

    @Override
    public void saveBatch(UserProfitInfoPO userProfitInfoPO) {
        mapper.saveBatch(userProfitInfoPO);
    }

    @Override
    public void saveOrUpdateUserProfit(UserProfitInfoPO userProfitInfoPO) {
        UserProfitInfoPO profitParam = new UserProfitInfoPO();
        profitParam.setAccountId(userProfitInfoPO.getAccountId());
        profitParam.setClientId(userProfitInfoPO.getClientId());
        profitParam.setGoalId(userProfitInfoPO.getGoalId());
        profitParam.setProfitDate(userProfitInfoPO.getProfitDate());
        UserProfitInfoPO po = mapper.selectOneByTime(profitParam);
        if (null == po) {
            mapper.saveUserProfit(userProfitInfoPO);
        } else {
            userProfitInfoPO.setId(po.getId());
            mapper.updateUserProfit(userProfitInfoPO);
        }
    }

    @Override
    public UserProfitInfoPO selectOneByTime(UserProfitInfoPO userProfitInfoPO) {
        return mapper.selectOneByTime(userProfitInfoPO);
    }

    @Override
    public List<UserProfitInfoPO> listByGoalds(List<String> goalIds, Date profitDate) {
        return mapper.listByGoalds(goalIds, profitDate);
    }

    @Override
    public List<UserProfitInfoPO> queryProfitList(UserProfitInfoPO userProfitInfoPO) {
        return mapper.queryProfitList(userProfitInfoPO);
    }
}
