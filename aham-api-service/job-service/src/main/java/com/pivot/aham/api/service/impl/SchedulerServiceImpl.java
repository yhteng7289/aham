//package com.pivot.aham.api.service.impl;
//
//import com.baomidou.mybatisplus.plugins.Page;
//import TaskFireLogMapper;
//import com.pivot.aham.api.service.scheduler.SchedulerManager;
//import com.pivot.aham.common.core.support.Pagination;
//import InstanceUtil;
//import PageUtil;
//import TaskFireLog;
//import TaskScheduled;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cache.annotation.CachePut;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * 请填写类注释
// *
// * @author addison
// * @since 2018年11月16日
// */
//@Service
//public class SchedulerServiceImpl implements ApplicationContextAware {
//    @Autowired
//    private TaskFireLogMapper logMapper;
//    @Lazy
//    @Autowired
//    private SchedulerManager schedulerManager;
//    protected ApplicationContext applicationContext;
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) {
//        this.applicationContext = applicationContext;
//    }
//
//    // 获取所有作业
//    public List<TaskScheduled> getAllTaskDetail() {
//        return schedulerManager.getAllJobDetail();
//    }
//
//    // 执行作业
//    public void execTask(TaskScheduled taskScheduler) {
//        schedulerManager.runJob(taskScheduler);
//    }
//
//    // 恢复作业
//    public void resumeTask(TaskScheduled taskScheduled) {
//        schedulerManager.resumeJob(taskScheduled);
//    }
//
//    // 暂停作业
//    public void pauseTask(TaskScheduled taskScheduled) {
//        schedulerManager.stopJob(taskScheduled);
//    }
//
//    // 删除作业
//    public void delTask(TaskScheduled taskScheduled) {
//        schedulerManager.delJob(taskScheduled);
//    }
//
//    // 修改任务
//    public void updateOrInsertTask(TaskScheduled taskScheduled) {
//        schedulerManager.updateOrInsertTask(taskScheduled);
//    }
//
//    @Cacheable("taskFireLog")
//    public TaskFireLog getFireLogById(Long id) {
//        return logMapper.selectById(id);
//    }
//
//    @Transactional
//    @CachePut("taskFireLog")
//    public TaskFireLog updateLog(TaskFireLog record) {
//        if (record.getId() == null) {
//            logMapper.insert(record);
//        } else {
//            logMapper.updateById(record);
//        }
//        return record;
//    }
//
//    public Pagination<TaskFireLog> queryLog(Map<String, Object> params) {
//        Page<Long> ids = PageUtil.getPage(params);
//        ids.setRecords(logMapper.selectIdByMap(ids, params));
//        Pagination<TaskFireLog> page = new Pagination<TaskFireLog>(ids.getCurrent(), ids.getSize());
//        page.setTotal(ids.getTotal());
//        if (ids != null) {
//            List<TaskFireLog> records = InstanceUtil.newArrayList();
//            for (Long id : ids.getRecords()) {
//                records.add(applicationContext.getBean(getClass()).getFireLogById(id));
//            }
//            page.setRecords(records);
//        }
//        return page;
//    }
//}
