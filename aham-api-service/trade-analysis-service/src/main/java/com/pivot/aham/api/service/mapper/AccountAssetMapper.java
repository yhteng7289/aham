package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.AccountAssetPO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by luyang.li on 18/12/9.
 */
@Repository
public interface AccountAssetMapper extends BaseMapper<AccountAssetPO> {

    List<AccountAssetPO> listAccountUnBuyAssets(AccountAssetPO accountAssetPO);

    void insertBatch(List<AccountAssetPO> accountAssetList);

    List<AccountAssetPO> listAccountAssetBeforeDate(@Param("accountId") Long accountId,
            @Param("endTime") Date endTime);

    void saveAccountAsset(AccountAssetPO accountAsset);

    AccountAssetPO queryAccountAsset(AccountAssetPO accountAsset);

    void update(AccountAssetPO accountAssetPO);

    List<AccountAssetPO> listByRechargeOrderNos(@Param("recahrgeOrderNos") List<Long> recahrgeOrderNos);

}
