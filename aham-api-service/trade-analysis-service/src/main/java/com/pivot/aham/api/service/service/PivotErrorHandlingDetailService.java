package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.PivotErrorHandlingDetailPO;
import com.pivot.aham.common.core.base.BaseService;
import java.math.BigDecimal;
import java.util.List;

public interface PivotErrorHandlingDetailService extends BaseService<PivotErrorHandlingDetailPO> {

    void batchInsert(List<PivotErrorHandlingDetailPO> insertList);

    BigDecimal getTotalMoneyByDateAndType(PivotErrorHandlingDetailPO queryRechargePO);

    BigDecimal getTotalMoney();

    List<PivotErrorHandlingDetailPO> queryByTypeAndDate(PivotErrorHandlingDetailPO queryExchangePO);
}
