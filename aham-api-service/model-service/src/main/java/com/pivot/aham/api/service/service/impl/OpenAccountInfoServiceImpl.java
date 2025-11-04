package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.OpenAccountInfoMapper;
import com.pivot.aham.api.service.mapper.model.OpenAccountInfoPO;
import com.pivot.aham.api.service.service.OpenAccountInfoService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * @author YYYz
 */
@CacheConfig(cacheNames = "openAccountInfoService")
@Service
public class OpenAccountInfoServiceImpl extends BaseServiceImpl<OpenAccountInfoPO, OpenAccountInfoMapper> implements OpenAccountInfoService {

    @Override
    public void insertBatch(List<OpenAccountInfoPO> openAccountInfoPOList) {
        mapper.insertBatch(openAccountInfoPOList);
    }

    @Override
    public OpenAccountInfoPO queryByPO(OpenAccountInfoPO openAccountInfoPO) {
        return mapper.queryByPO(openAccountInfoPO);
    }

    @Override
    public void disableAllByPO(OpenAccountInfoPO openAccountInfoPO) {
        mapper.disableAllByPO(openAccountInfoPO);
    }
}
