package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.PivotCharityDetailPO;
import com.pivot.aham.common.core.base.BaseMapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface PivotCharityDetailMapper extends BaseMapper<PivotCharityDetailPO> {

    void batchInsert(List<PivotCharityDetailPO> insertList);

    BigDecimal getTotalMoneyByDateAndType(PivotCharityDetailPO queryRechargePO);

    BigDecimal getTotalMoney();

    List<PivotCharityDetailPO> getRoundingPageList(RowBounds rowBound, PivotCharityDetailPO pivotCharityDetailPO,
            @Param("startCreateTime") Date startCreateTime, @Param("endCreateTime") Date endCreateTime);

    void deleteByRedeemId(PivotCharityDetailPO deletePO);
}
