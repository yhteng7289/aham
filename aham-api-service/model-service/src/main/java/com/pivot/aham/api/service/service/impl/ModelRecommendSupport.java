package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.ModelRecommendMapper;
import com.pivot.aham.api.service.mapper.PortFutureLevelMapper;
import com.pivot.aham.api.service.mapper.model.ModelRecommend;
import com.pivot.aham.api.service.mapper.model.PortFutureLevelPO;
import com.pivot.aham.common.enums.ModelStatusEnum;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by luyang.li on 18/12/9.
 */
@Service
public class ModelRecommendSupport {

    @Resource
    private ModelRecommendMapper modelRecommendMapper;
    @Resource
    private PortFutureLevelMapper portFutureLevelMapper;

    /**
     * 1.设置已经存在生效的模型数据为无效
     * 2.保存新的模型数据
     *
     * @param modelRecommendList
     */
    @Transactional
    public void saveNewModelRecommend(List<ModelRecommend> modelRecommendList) {
        List<ModelRecommend> modelRecommends = modelRecommendMapper.listValidModel(ModelStatusEnum.Effective);
        if(CollectionUtils.isNotEmpty(modelRecommends)) {
            List<Long> modelIds = modelRecommends.stream().map(ModelRecommend::getId).collect(Collectors.toList());
            modelRecommendMapper.updateDisableByIds(modelIds);
        }
        modelRecommendMapper.insertBatch(modelRecommendList);
    }

    @Transactional
    public void handelPortFutureLevels(List<PortFutureLevelPO> portFutureLevels,
                                       List<PortFutureLevelPO> newPortFutureLevels) {
        if (CollectionUtils.isNotEmpty(portFutureLevels)) {
            List<Long> portFutureLevelIds = portFutureLevels.stream().map(PortFutureLevelPO::getId).collect(Collectors.toList());
            portFutureLevelMapper.updateStatusByIds(portFutureLevelIds);
        }
        portFutureLevelMapper.insertBatch(newPortFutureLevels);
    }
}
