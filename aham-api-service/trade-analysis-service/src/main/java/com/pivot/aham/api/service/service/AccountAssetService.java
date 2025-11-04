package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.AccountAssetPO;
import com.pivot.aham.api.service.mapper.model.AccountRechargePO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.Date;
import java.util.List;

/**
 * Created by luyang.li on 18/12/9.
 */
public interface AccountAssetService extends BaseService<AccountAssetPO> {

    List<AccountAssetPO> listAccountUnBuyAssets(AccountAssetPO accountAssetPO);

    void insertBatch(List<AccountAssetPO> accountAssetList);

    List<AccountAssetPO> listAccountAssetBeforeDate(Long accountId, Date endTime);

    void saveAccountAsset(AccountAssetPO accountAsset);

    AccountAssetPO queryAccountAsset(AccountAssetPO queryParam);

    void update(AccountAssetPO accountAssetPO);

    AccountAssetPO genUnBuyHoldAccountAssetInfo(AccountRechargePO accountRecharge);

    List<AccountAssetPO> listByRechargeOrderNos(List<Long> recahrgeOrderNos);

}
