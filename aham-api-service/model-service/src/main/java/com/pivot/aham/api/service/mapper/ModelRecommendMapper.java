package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.ModelRecommend;
import com.pivot.aham.common.core.base.BaseMapper;
import com.pivot.aham.common.enums.ModelStatusEnum;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by luyang.li on 18/12/6.
 */
@Repository
public interface ModelRecommendMapper extends BaseMapper<ModelRecommend> {

    List<ModelRecommend> queryByDate(@Param("date") Date date);

    List<ModelRecommend> listValidModel(@Param("modelStatus") ModelStatusEnum modelStatus);

    void updateDisableByIds(@Param("modelIds") List<Long> modelIds);

    void insertBatch(List<ModelRecommend> modelRecommendList);

    ModelRecommend getValidRecommendByPortfolioId(@Param("portfolioId") String portfolioId);

    ModelRecommend queryModelRecommend(ModelRecommend queryParam);

    ModelRecommend queryModelById(ModelRecommend queryParam);

}
