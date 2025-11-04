package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.PivotFeeDetailPO;
import com.pivot.aham.common.core.base.BaseMapper;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PivotFeeDetailServiceMapper extends BaseMapper<PivotFeeDetailPO> {

    void batchInsert(List<PivotFeeDetailPO> insertList);

    // Date and OperateType
    BigDecimal getTotalMoneyByDateAndType(PivotFeeDetailPO pivotFeeDetailPO);

    // Date and FeeType
    BigDecimal getTotalMoneyByDateAndFeeType(PivotFeeDetailPO pivotFeeDetailPO);

    BigDecimal getTotalMoneyByFeeType(@Param("feeType") Integer feeType);

    void disableAll(PivotFeeDetailPO queryPO);
}
