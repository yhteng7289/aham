package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.PivotPftAccountMapper;
import com.pivot.aham.api.service.mapper.PivotPftHoldingMapper;
import com.pivot.aham.api.service.mapper.model.PivotPftAccountPO;
import com.pivot.aham.api.service.mapper.model.PivotPftHoldingPO;
import com.pivot.aham.api.service.service.PivotPftAccountService;
import com.pivot.aham.api.service.service.PivotPftHoldingService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class PivotPftHoldingServiceImpl extends BaseServiceImpl<PivotPftHoldingPO, PivotPftHoldingMapper> implements PivotPftHoldingService {

    @Override
    public void savePftHolding(PivotPftHoldingPO pivotPftHoldingPO) {
         mapper.savePftHolding(pivotPftHoldingPO);
    }
    
    @Override
    public List<PivotPftHoldingPO> getListOfPftHolding(PivotPftHoldingPO pivotPftHoldingPO) {
         return mapper.getListOfPftHolding(pivotPftHoldingPO);
    }
    
    @Override
    public PivotPftHoldingPO getPftHolding(PivotPftHoldingPO pivotPftHoldingPO) {
         return mapper.getPftHolding(pivotPftHoldingPO);
    }
    
    @Override
    public void updatePftHolding(PivotPftHoldingPO pivotPftHoldingPO) {
         mapper.updatePftHolding(pivotPftHoldingPO);
    }
 
}