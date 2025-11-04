package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.OpenAccountInfoQuestionMapper;
import com.pivot.aham.api.service.mapper.model.OpenAccountInfoQuestionPO;
import com.pivot.aham.api.service.service.OpenAccountInfoQuestionService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;
@CacheConfig(cacheNames = "openAccountInfoQuestionService")
@Service
public class OpenAccountInfoQuestionServiceImpl extends BaseServiceImpl<OpenAccountInfoQuestionPO, OpenAccountInfoQuestionMapper> implements OpenAccountInfoQuestionService {
    @Override
    public void insertBatch(List<OpenAccountInfoQuestionPO> openAccountInfoQuestionPOList) {
        mapper.insertBatch(openAccountInfoQuestionPOList);
    }

    @Override
    public void updateByPO(OpenAccountInfoQuestionPO updatePO) {
        mapper.updatePO(updatePO);
    }

    @Override
    public OpenAccountInfoQuestionPO queryByPO(OpenAccountInfoQuestionPO queryPO) {
        return mapper.queryByPO(queryPO);
    }

    @Override
    public void deleteByPO(OpenAccountInfoQuestionPO deletePO) {
        mapper.deleteByPO(deletePO);
    }

    @Override
    public void disAbleAllByPO(OpenAccountInfoQuestionPO openAccountInfoQuestionPO) {
        mapper.disAbleAllByPO(openAccountInfoQuestionPO);
    }
}
