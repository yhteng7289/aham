package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.PivotErrorHandlingDetailServiceMapper;
import com.pivot.aham.api.service.mapper.model.PivotErrorHandlingDetailPO;
import com.pivot.aham.api.service.service.PivotErrorHandlingDetailService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class PivotErrorHandlingDetailServiceImpl extends BaseServiceImpl<PivotErrorHandlingDetailPO, PivotErrorHandlingDetailServiceMapper> implements PivotErrorHandlingDetailService {

    @Override
    public void batchInsert(List<PivotErrorHandlingDetailPO> insertList) {
        mapper.batchInsert(insertList);
    }

    @Override
    public BigDecimal getTotalMoneyByDateAndType(PivotErrorHandlingDetailPO queryRechargePO) {
        return mapper.getTotalMoneyByDateAndType(queryRechargePO);
    }

    @Override
    public List<PivotErrorHandlingDetailPO> queryByTypeAndDate(PivotErrorHandlingDetailPO queryExchangePO) {
        return mapper.queryByTypeAndDate(queryExchangePO);
    }

    @Override
    public BigDecimal getTotalMoney() {
        return mapper.getTotalMoney();
    }
}
