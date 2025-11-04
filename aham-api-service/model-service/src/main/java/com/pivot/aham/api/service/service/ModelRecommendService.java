package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.ModelRecommend;
import com.pivot.aham.api.service.mapper.model.ModelRecommendBackUp;
import com.pivot.aham.common.core.base.BaseService;

import java.util.Date;
import java.util.List;

/**
 * Created by luyang.li on 18/12/6.
 */
public interface ModelRecommendService extends BaseService<ModelRecommend> {

    List<ModelRecommend> queryByDate(Date date);

    /**
     * 同步FTP模型文件
     *
     * @param date
     */
    void synchroModelRecommend(String date);

    /**
     * 插叙所有有效的模型数据
     *
     * @return
     */
    List<ModelRecommend> queryAllValidRecommend();

    ModelRecommend getValidRecommendByPortfolioId(String portfolioId);

    void sendFtpFileNotFound(String message);

    ModelRecommend queryModelRecommend(ModelRecommend queryParam);

    /**
     * 同步历史数据
     *
     * @param date
     * @param days
     */
    void synchroHisModelRecommend(String date, int days);

    ModelRecommendBackUp queryModelRecommendBackUp(ModelRecommendBackUp queryParam);

    void modelRecommendInit(String date);

    ModelRecommend queryModelById(ModelRecommend queryParam);

    void saveNewPortfolioRecommend();
}
