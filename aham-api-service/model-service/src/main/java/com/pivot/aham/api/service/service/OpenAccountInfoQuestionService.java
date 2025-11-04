package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.OpenAccountInfoQuestionPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;

public interface OpenAccountInfoQuestionService extends BaseService<OpenAccountInfoQuestionPO> {
    void insertBatch(List<OpenAccountInfoQuestionPO> openAccountInfoQuestionPOList);

    void updateByPO(OpenAccountInfoQuestionPO updatePO);

    OpenAccountInfoQuestionPO queryByPO(OpenAccountInfoQuestionPO queryPO);

    void deleteByPO(OpenAccountInfoQuestionPO deletePO);

    void disAbleAllByPO(OpenAccountInfoQuestionPO openAccountInfoQuestionPO);
}
