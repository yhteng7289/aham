package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.OpenAccountInfoPO;
import com.pivot.aham.common.core.base.BaseMapper;

import java.util.List;

/**
 * @author YYYz
 */
public interface OpenAccountInfoMapper extends BaseMapper<OpenAccountInfoPO> {
    void insertBatch(List<OpenAccountInfoPO> openAccountInfoPOList);

    OpenAccountInfoPO queryByPO(OpenAccountInfoPO openAccountInfoPO);

    void disableAllByPO(OpenAccountInfoPO openAccountInfoPO);
}
