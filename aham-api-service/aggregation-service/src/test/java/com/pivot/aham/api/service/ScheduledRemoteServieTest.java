package com.pivot.aham.api.service;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.server.remoteservice.SchedulerRemoteService;
import com.pivot.aham.common.core.support.context.ApplicationContextHolder;
import com.pivot.aham.common.model.TaskFireLog;
import com.pivot.aham.common.model.TaskScheduled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月28日
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ScheduledRemoteServieTest {

    @Resource
    private SchedulerRemoteService schedulerRemoteService;

    @Resource
    private SaxoTradingService saxoTradingService;


    @Test
    public void getAllTaskDetail() {
        List<TaskScheduled> taskScheduledList =  schedulerRemoteService.getAllTaskDetail();
        System.out.println("===================SchedulerRemoteService======================"+JSON.toJSON(taskScheduledList));
    }

    @Test
    public void execTask() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        TaskScheduled taskSchedule = new TaskScheduled();
        taskSchedule.setTaskGroup("DAS");
        taskSchedule.setTaskName("getAllValidRecommend");
        schedulerRemoteService.execTask(taskSchedule);
    }

    @Test
    public void openTask() {
        TaskScheduled taskSchedule = new TaskScheduled();
        taskSchedule.setTaskGroup("DAS");
        taskSchedule.setTaskName("getAllValidRecommend");

        schedulerRemoteService.openTask(taskSchedule);
    }

    @Test
    public void closeTask() {
        TaskScheduled taskSchedule = new TaskScheduled();
        taskSchedule.setTaskGroup("DAS");
        taskSchedule.setTaskName("getAllValidRecommend");

        schedulerRemoteService.closeTask(taskSchedule);
    }

    @Test
    public void delTask() {
        TaskScheduled taskSchedule = new TaskScheduled();
        taskSchedule.setTaskGroup("DAS");
        taskSchedule.setTaskName("getAllValidRecommend");
        schedulerRemoteService.delTask(taskSchedule);
    }

    public TaskFireLog updateLog(TaskFireLog record) {
        return schedulerRemoteService.updateLog(record);
    }

    public TaskFireLog getFireLogById(Long id) {
        return schedulerRemoteService.getFireLogById(id);
    }

    public Object queryLog(Map<String, Object> params) {
        return schedulerRemoteService.queryLog(params);
    }

    @Test
    public void updateTask() throws Exception {

        saxoTradingService.recalculate();

        SaxoTradingService saxoTradingService = (SaxoTradingService)ApplicationContextHolder.getBean("saxoTradingService");

        System.out.println();
    }


}
