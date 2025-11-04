package com.pivot.aham.api.service;

import com.pivot.aham.common.core.util.CacheUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月28日
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DisLockTest {

    Logger logger = LogManager.getLogger();

    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Test
    public void testDisLock(){
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                int count=1;
                while(count>10) {
                    logger.info("线程1获取锁");
                    Boolean su = CacheUtil.getLock("testLock");
                    if(su) {
                        System.out.println("=====线程1：" + su);
                    }
                    count++;
                }
            }
        });

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                int count =1;
                if(count>10) {
                    logger.info("线程2获取锁");
                    Boolean bu = CacheUtil.getLock("testLock");
                    if(bu) {
                        System.out.println("=====线程2：" + bu);
                    }
                }
                count++;
            }
        });




//        CacheUtil.getLock("testLock");
//        CacheUtil.unLock("testLock");
    }

}
