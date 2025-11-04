package com.pivot.aham.api.service.job.elasticjobdemo;

import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;


@Slf4j
public class MyElasticSimpleTestInitListener implements ElasticJobListener {
    @Override
    public void beforeJobExecuted(ShardingContexts shardingContexts) {
//        try {
//            Thread.sleep(3 * 1000L);
            log.warn("beforeJobExecuted 当前时间:{}.",new Date());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void afterJobExecuted(ShardingContexts shardingContexts) {

    }
}
