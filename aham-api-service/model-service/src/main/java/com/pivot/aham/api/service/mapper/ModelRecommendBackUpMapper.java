package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.ModelRecommendBackUp;
import com.pivot.aham.common.core.base.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * Created by luyang.li on 18/12/6.
 */
@Repository
public interface ModelRecommendBackUpMapper extends BaseMapper<ModelRecommendBackUp> {


    ModelRecommendBackUp queryModelRecommendBackUp(ModelRecommendBackUp modelRecommendBackUp);

}
