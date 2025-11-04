package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.OpenAccountInfoPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;

/**
 * @author YYYz
 */
public interface OpenAccountInfoService extends BaseService<OpenAccountInfoPO>{
    void insertBatch(List<OpenAccountInfoPO> openAccountInfoPOList);

    OpenAccountInfoPO queryByPO(OpenAccountInfoPO openAccountInfoPO);

    void disableAllByPO(OpenAccountInfoPO openAccountInfoPO);
}
