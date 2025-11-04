package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.PivotPftAssetPO;
import com.pivot.aham.common.core.base.BaseService;
import java.util.List;

public interface PivotPftAssetService extends BaseService<PivotPftAssetPO> {

    void updateOrInsertPivotPftAsset(PivotPftAssetPO pivotPftAssetPO);

    List<PivotPftAssetPO> queryListByTime(PivotPftAssetPO pivotPftAssetPO);

}
