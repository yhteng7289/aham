package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.OpenAccountInfoQuestionPO;
import com.pivot.aham.common.core.base.BaseMapper;

import java.util.List;

public interface OpenAccountInfoQuestionMapper extends BaseMapper<OpenAccountInfoQuestionPO> {

    void insertBatch(List<OpenAccountInfoQuestionPO> openAccountInfoQuestionPOList);

    void updatePO(OpenAccountInfoQuestionPO updatePO);

    OpenAccountInfoQuestionPO queryByPO(OpenAccountInfoQuestionPO queryPO);

    void deleteByPO(OpenAccountInfoQuestionPO deletePO);

    void disAbleAllByPO(OpenAccountInfoQuestionPO openAccountInfoQuestionPO);
}
