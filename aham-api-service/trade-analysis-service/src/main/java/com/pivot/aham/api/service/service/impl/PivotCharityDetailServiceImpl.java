package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.PivotCharityDetailMapper;
import com.pivot.aham.api.service.mapper.model.PivotCharityDetailPO;
import com.pivot.aham.api.service.service.PivotCharityDetailService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.session.RowBounds;

@Service
@Slf4j
public class PivotCharityDetailServiceImpl extends BaseServiceImpl<PivotCharityDetailPO, PivotCharityDetailMapper> implements PivotCharityDetailService {

    @Override
    public void batchInsert(List<PivotCharityDetailPO> insertList) {
        mapper.batchInsert(insertList);
    }

    @Override
    public BigDecimal getTotalMoneyByDateAndType(PivotCharityDetailPO queryRechargePO) {
        return mapper.getTotalMoneyByDateAndType(queryRechargePO);
    }

    @Override
    public void deleteByRedeemId(PivotCharityDetailPO deletePO) {
        mapper.deleteByRedeemId(deletePO);
    }

    @Override
    public BigDecimal getTotalMoney() {
        return mapper.getTotalMoney();
    }

    @Override
    public List<PivotCharityDetailPO> getRoundingPageList(RowBounds rowBound, PivotCharityDetailPO pivotCharityDetailPO, Date startCreateTime, Date endCreateTime) {
        return mapper.getRoundingPageList(rowBound, pivotCharityDetailPO, startCreateTime, endCreateTime);
    }

}
