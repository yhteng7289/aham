package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.PivotCharityDetailPO;
import com.pivot.aham.common.core.base.BaseService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.session.RowBounds;

public interface PivotCharityDetailService extends BaseService<PivotCharityDetailPO> {

    void batchInsert(List<PivotCharityDetailPO> insertList);

    BigDecimal getTotalMoneyByDateAndType(PivotCharityDetailPO queryRechargePO);

    BigDecimal getTotalMoney();

    List<PivotCharityDetailPO> getRoundingPageList(RowBounds rowBound, PivotCharityDetailPO pivotCharityDetailPO, Date startCreateTime, Date endCreateTime);

    void deleteByRedeemId(PivotCharityDetailPO deletePO);
}
