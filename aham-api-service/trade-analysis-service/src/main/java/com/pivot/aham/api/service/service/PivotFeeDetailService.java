package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.PivotFeeDetailPO;
import com.pivot.aham.common.core.base.BaseService;

import java.math.BigDecimal;
import java.util.List;

public interface PivotFeeDetailService extends BaseService<PivotFeeDetailPO> {

    void batchInsert(List<PivotFeeDetailPO> insertList);

    BigDecimal getTotalMoneyByDateAndType(PivotFeeDetailPO pivotFeeDetailPO);

    BigDecimal getTotalMoneyByDateAndFeeType(PivotFeeDetailPO pivotFeeDetailPO);

    BigDecimal getTotalMoneyByFeeType(Integer feeType);

    void disableAll(PivotFeeDetailPO queryPO);
}
