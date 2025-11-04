package com.pivot.aham.api.web.monitordemo;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.springframework.stereotype.Service;
/**
 * counter例子，只增不减
 *
 * @author addison
 * @since 2019年01月09日
 */
//@Service
public class MetriceCounterService {

    static final Counter testCounter = Metrics.counter("metricetest.counter.total", "metricsCounterService", "demo");

    static final io.prometheus.client.Counter testOrgCounter = io.prometheus.client.Counter.build().name("testorg_counter_total").labelNames("metricsCounterService", "demo")
    .help("total request counter").register();
    public void test() throws InterruptedException {
//        while (true) {
            testCounter.increment(1D);
            testOrgCounter.labels("count123","123").inc(1D);
//        }
    }
}
