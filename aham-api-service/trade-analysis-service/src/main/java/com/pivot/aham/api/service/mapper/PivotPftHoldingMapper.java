package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.PivotPftAccountPO;
import com.pivot.aham.api.service.mapper.model.PivotPftHoldingPO;
import com.pivot.aham.common.core.base.BaseMapper;
import java.util.List;

public interface PivotPftHoldingMapper extends BaseMapper<PivotPftHoldingPO> {
    
    void savePftHolding(PivotPftHoldingPO pivotPftHoldingPO);
    
    List<PivotPftHoldingPO> getListOfPftHolding(PivotPftHoldingPO pivotPftHoldingPO);
    
    PivotPftHoldingPO getPftHolding(PivotPftHoldingPO pivotPftHoldingPO);
    
    void updatePftHolding(PivotPftHoldingPO pivotPftHoldingPO);

}