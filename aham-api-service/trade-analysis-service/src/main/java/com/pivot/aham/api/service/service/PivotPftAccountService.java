package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.PivotPftAccountPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;

public interface PivotPftAccountService extends BaseService<PivotPftAccountPO> {
    void updateAccount(List<Long> ids,List<PivotPftAccountPO> pivotPftAccountPOList);
}
