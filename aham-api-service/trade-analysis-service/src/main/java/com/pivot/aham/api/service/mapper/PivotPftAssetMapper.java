package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.PivotPftAssetPO;
import com.pivot.aham.common.core.base.BaseMapper;
import java.util.List;

public interface PivotPftAssetMapper extends BaseMapper<PivotPftAssetPO> {

    List<PivotPftAssetPO> queryListByTime(PivotPftAssetPO pivotPftAssetPO);

}
