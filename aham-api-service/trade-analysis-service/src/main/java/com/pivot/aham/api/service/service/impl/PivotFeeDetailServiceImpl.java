package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.PivotFeeDetailServiceMapper;
import com.pivot.aham.api.service.mapper.model.PivotFeeDetailPO;
import com.pivot.aham.api.service.service.PivotFeeDetailService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class PivotFeeDetailServiceImpl extends BaseServiceImpl<PivotFeeDetailPO, PivotFeeDetailServiceMapper> implements PivotFeeDetailService {

    @Override
    public void batchInsert(List<PivotFeeDetailPO> insertList) {
        mapper.batchInsert(insertList);
    }

    @Override
    public BigDecimal getTotalMoneyByDateAndType(PivotFeeDetailPO pivotFeeDetailPO) {
        return mapper.getTotalMoneyByDateAndType(pivotFeeDetailPO);
    }
    
    @Override
    public BigDecimal getTotalMoneyByDateAndFeeType(PivotFeeDetailPO pivotFeeDetailPO) {
        return mapper.getTotalMoneyByDateAndFeeType(pivotFeeDetailPO);
    }    

    @Override
    public BigDecimal getTotalMoneyByFeeType(Integer feeType) {
        return mapper.getTotalMoneyByFeeType(feeType);
    }

    @Override
    public void disableAll(PivotFeeDetailPO queryPO) {
        mapper.disableAll(queryPO);
    }

}
