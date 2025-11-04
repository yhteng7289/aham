package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.TAccountRechargePO;
import com.pivot.aham.common.core.base.BaseMapper;

import java.util.List;

public interface TAccountRechargeMapper extends BaseMapper {

    void saveAccountRecharge(TAccountRechargePO accountRecharge);

    TAccountRechargePO queryAccountRecharge(TAccountRechargePO accountRechargePO);

    void updateAccountRecharge(TAccountRechargePO accountRechargePO);

    List<TAccountRechargePO> listByAccountId(TAccountRechargePO po);

    List<TAccountRechargePO> listByDate(TAccountRechargePO po);

    List<TAccountRechargePO> listById(TAccountRechargePO po);

}
