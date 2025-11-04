//package com.pivot.aham.api.service.scheduler.config;
//
//import com.pivot.aham.api.service.scheduler.JobListener;
//import com.pivot.aham.api.service.scheduler.SchedulerManager;
//import InstanceUtil;
//import org.quartz.Scheduler;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.DefaultResourceLoader;
//import org.springframework.core.io.Resource;
//import org.springframework.scheduling.quartz.SchedulerFactoryBean;
//
//import javax.sql.DataSource;
//import java.util.List;
//
///**
// * 注册quartz调度器
// *
// * @author addison
// * @since 2018年11月16日
// */
//@Configuration
//@ConditionalOnClass(org.quartz.JobListener.class)
//public class SchedulerConfig {
//
//
//    @Bean
//    public SchedulerFactoryBean schedulerFactory(DataSource dataSource) {
//        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
//        schedulerFactory.setSchedulerName("SquirrelSave-Scheduler");
//        schedulerFactory.setApplicationContextSchedulerContextKey("applicationContext");
//        schedulerFactory.setDataSource(dataSource);
//        Resource resouce = new DefaultResourceLoader().getResource("classpath:config/quartz.properties");
//        schedulerFactory.setConfigLocation(resouce);
//        return schedulerFactory;
//    }
//
//    @Bean
//    public SchedulerManager scheduler(Scheduler scheduler, JobListener jobListener) {
//        SchedulerManager schedulerManager = new SchedulerManager();
//        schedulerManager.setScheduler(scheduler);
//        List<org.quartz.JobListener> jobListeners = InstanceUtil.newArrayList();
//        jobListeners.add(jobListener);
//        schedulerManager.setJobListeners(jobListeners);
//        return schedulerManager;
//    }
//
//    @Bean
//    public JobListener jobListener() {
//        return new JobListener();
//    }
//}
