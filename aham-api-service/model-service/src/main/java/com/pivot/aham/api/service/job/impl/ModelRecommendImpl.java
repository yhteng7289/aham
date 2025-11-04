package com.pivot.aham.api.service.job.impl;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.job.ModelRecommendJob;
import com.pivot.aham.api.service.service.ModelRecommendService;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by luyang.li on 18/12/26.
 * <p>
 * 模型数据同步
 */
@ElasticJobConf(name = "ModelRecommendJob_2",
        //cron = "0 0 9,10 * * ?",
        cron = "0 00 18 * * ?",
        shardingItemParameters = "0=1",
        shardingTotalCount = 1,
        description = "SynchronizeModelRecommendJob",eventTraceRdbDataSource = "dataSource")
@Slf4j
public class ModelRecommendImpl implements SimpleJob, ModelRecommendJob {

    @Resource
    private ModelRecommendService modelRecommendService;

    //模型同步
    @Override
    public void synchroModelRecommend(String date) {
        modelRecommendService.synchroModelRecommend(date);
    }

    @Override
    public void synchroNewModelRecommend() {
        modelRecommendService.saveNewPortfolioRecommend();
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            Date date = DateUtils.addDays(DateUtils.now(), -1);
            log.info("====投资模型同步#同步ModelRecommend开始,date:{}====", DateUtils.getDate());
            synchroNewModelRecommend();
//            synchroModelRecommend(DateUtils.formatDate(date, DateUtils.DATE_FORMAT2));
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
            log.error("====投资模型同步#同步ModelRecommend异常,date:{},ex:", DateUtils.getDate(), e);
        }
        log.info("====投资模型同步#同步ModelRecommend结束,date:{}====", DateUtils.getDate());
    }
}
