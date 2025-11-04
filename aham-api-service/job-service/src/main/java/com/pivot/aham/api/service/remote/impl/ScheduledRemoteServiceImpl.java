//package com.pivot.aham.api.service.remote.impl;
//
//import com.alibaba.dubbo.config.annotation.Service;
//import com.pivot.aham.api.server.service.SchedulerRemoteService;
//import com.pivot.aham.api.service.impl.SchedulerServiceImpl;
//import TaskFireLog;
//import TaskScheduled;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * 请填写类注释
// *
// * @author addison
// * @since 2018年12月06日
// */
//@Service(interfaceClass = SchedulerRemoteService.class)
//public class ScheduledRemoteServiceImpl implements SchedulerRemoteService{
//    @Autowired
//    private SchedulerServiceImpl schedulerService;
//    @Override
//    public List<TaskScheduled> getAllTaskDetail() {
//        return schedulerService.getAllTaskDetail();
//    }
//
//    @Override
//    public void execTask(TaskScheduled taskScheduler) {
//        schedulerService.execTask(taskScheduler);
//    }
//
//    @Override
//    public void openTask(TaskScheduled taskScheduler) {
//        schedulerService.resumeTask(taskScheduler);
//    }
//
//    @Override
//    public void closeTask(TaskScheduled taskScheduler) {
//        schedulerService.pauseTask(taskScheduler);
//    }
//
//    @Override
//    public void delTask(TaskScheduled taskScheduler) {
//        schedulerService.delTask(taskScheduler);
//    }
//
//    @Override
//    public TaskFireLog updateLog(TaskFireLog record) {
//        return schedulerService.updateLog(record);
//    }
//
//    @Override
//    public TaskFireLog getFireLogById(Long id) {
//        return schedulerService.getFireLogById(id);
//    }
//
//    @Override
//    public Object queryLog(Map<String, Object> params) {
//        return schedulerService.queryLog(params);
//    }
//
//    @Override
//    public void updateOrInsertTask(TaskScheduled taskScheduled) {
//        schedulerService.updateOrInsertTask(taskScheduled);
//    }
//
//}
