package com.pivot.aham.api.service.job;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * Created by luyang.li on 19/1/8.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TradeAnalysisJobTest {

    @Resource
    private TradeAnalysisJob tradeAnalysisJob;

    @Test
    public void tradeAnalysis() throws Exception {
        tradeAnalysisJob.tradeAnalysis(null);
    }

}