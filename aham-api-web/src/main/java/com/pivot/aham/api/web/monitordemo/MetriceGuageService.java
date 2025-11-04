package com.pivot.aham.api.web.monitordemo;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * guage例子，可增可减
 *
 * @author addison
 * @since 2019年01月09日
 */
//@Service
public class MetriceGuageService {
 
    List<Tag> init(){
        ArrayList<Tag> list = new ArrayList(){};
        list.add(new ImmutableTag("metricsGuageService", "demo"));
        return list;
    }
 
    AtomicInteger atomicInteger = new AtomicInteger(0);
    AtomicInteger passCases =  Metrics.gauge("metricetest.cases.guage.value", init(), atomicInteger);
 
    Gauge passCaseGuage = Gauge.builder("metricetest.cases.guage", atomicInteger, AtomicInteger::get)
            .tag("metricsGuageService", "demo")
            .description("pass cases guage of demo")
            .register(new SimpleMeterRegistry());

    static final io.prometheus.client.Gauge testOrgGuage = io.prometheus.client.Gauge.build().name("testorg_gauge").labelNames("metricsGuageService", "demo")
    .help("request guage").register();


 
    public void test() {
 
//        while (true){
            if (System.currentTimeMillis() % 2 == 0){
                passCases.addAndGet(101);
                testOrgGuage.labels("gauge1","123").inc();
                System.out.println("ADD + " + " : " + passCases);
                System.out.println("ADD + " + " : " + passCaseGuage.measure());
            }else {
                int val = passCases.addAndGet(-99);
                if (val < 0){
                    passCases.set(2);
                }
                testOrgGuage.labels("gauge1","123").dec();
                System.out.println("DECR - " + " : " + passCases);
                System.out.println("ADD + " + " : " + passCaseGuage.measure());
            }
//        }
 
    }
 
}
