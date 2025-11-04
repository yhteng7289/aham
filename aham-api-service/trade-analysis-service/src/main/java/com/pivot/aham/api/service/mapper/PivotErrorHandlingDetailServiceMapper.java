package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.PivotErrorHandlingDetailPO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PivotErrorHandlingDetailServiceMapper extends BaseMapper<PivotErrorHandlingDetailPO> {

    void batchInsert(@Param("list") List<PivotErrorHandlingDetailPO> insertList);

    BigDecimal getTotalMoneyByDateAndType(PivotErrorHandlingDetailPO queryRechargePO);

    BigDecimal getTotalMoney();

    List<PivotErrorHandlingDetailPO> queryByTypeAndDate(PivotErrorHandlingDetailPO queryExchangePO);

}
