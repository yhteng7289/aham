package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.UserAssetPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.Date;
import java.util.List;

/**
 * Created by luyang.li on 18/12/9.
 */
public interface UserAssetService extends BaseService<UserAssetPO> {

    void saveBatch(List<UserAssetPO> userAssetPOs);

    void save(UserAssetPO userAssetPO);

    void update(UserAssetPO userAssetPO);

    UserAssetPO queryUserAssetPo(UserAssetPO queryPo);

    void saveOrUpdateUserTodayAsset(UserAssetPO userAssetPO);

    List<UserAssetPO> queryListByTime(UserAssetPO userAssetPO);

    List<UserAssetPO> litsUserAsset(Long accountId, List<String> clientIds, Date lastExDate, String productCode);

    void saveOrUpdateUserTodayAsset(List<UserAssetPO> userAssetPO);
}
