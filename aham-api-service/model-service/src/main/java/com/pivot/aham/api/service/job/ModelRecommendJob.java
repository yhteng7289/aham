package com.pivot.aham.api.service.job;

/**
 * Created by luyang.li on 18/12/26.
 */
public interface ModelRecommendJob {

    /**
     * 同步模型数据
     *
     * @param date
     */
    void synchroModelRecommend(String date);

    void synchroNewModelRecommend();

}
