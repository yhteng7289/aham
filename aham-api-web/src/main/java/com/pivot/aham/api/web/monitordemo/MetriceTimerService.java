package com.pivot.aham.api.web.monitordemo;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 时间统计类
 *
 * @author addison
 * @since 2019年01月09日
 */
//@Service
public class MetriceTimerService {
    static final Timer timer = Timer.builder("metricetest.timer")
    .tag("metriceTimerService", "demo")
    .description("timer sample test.")
    .register(new SimpleMeterRegistry());
    public void test(){


        for(int i=0; i<2; i++) {
            timer.record(() -> {
                try {
                    TimeUnit.SECONDS.sleep(2);
                }catch (InterruptedException e){

                }

            });
        }

        System.out.println(timer.count());
        System.out.println(timer.measure());
        System.out.println(timer.totalTime(TimeUnit.SECONDS));
        System.out.println(timer.mean(TimeUnit.SECONDS));
        System.out.println(timer.max(TimeUnit.SECONDS));

    }
}
