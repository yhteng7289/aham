package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.UserProfitInfoPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.Date;
import java.util.List;

/**
 * Created by luyang.li on 18/12/9.
 */
public interface UserProfitInfoService extends BaseService<UserProfitInfoPO> {


    void saveBatch(UserProfitInfoPO userProfitInfoPO);

    void saveOrUpdateUserProfit(UserProfitInfoPO userProfitInfoPO);

    UserProfitInfoPO selectOneByTime(UserProfitInfoPO userProfitInfoParam);

    List<UserProfitInfoPO> listByGoalds(List<String> goalIds, Date now);

    List<UserProfitInfoPO> queryProfitList(UserProfitInfoPO userProfitInfoPO);

}
