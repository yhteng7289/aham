package com.pivot.aham.api.web.monitordemo;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.prometheus.client.Summary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Summary，时间范围统计类
 *
 * @author addison
 * @since 2019年01月09日
 */
//@Service
public class MetriceSummaryService {
    static final io.prometheus.client.Summary testOrgSummary = io.prometheus.client.Summary.build().name("testorg_summary").labelNames("metriceSummaryService", "demo")
    .help("request summary").register();

    static final DistributionSummary summary = DistributionSummary.builder("metricetest.summary")
    .tag("summary1", "123")
    .description("summary sample test")
    .register(new SimpleMeterRegistry());

    public void test() throws InterruptedException {


        summary.record(2D);
        summary.record(3D);
        summary.record(4D);

        Summary.Timer timer = testOrgSummary.labels("metriceSummaryService","demo").startTimer();
        Thread.sleep(1000);
        timer.observeDuration();




    }
 
}
