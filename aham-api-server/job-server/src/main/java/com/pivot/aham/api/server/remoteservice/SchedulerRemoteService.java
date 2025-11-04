package com.pivot.aham.api.server.remoteservice;

import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.model.TaskFireLog;
import com.pivot.aham.common.model.TaskScheduled;

import java.util.List;
import java.util.Map;

/**
 * 定时任务管理
 *
 * @author addison
 * @version 2018年11月15日 上午11:06:49
 */
public interface SchedulerRemoteService extends BaseRemoteService{

    /**
     * 获取所有任务
     * */
    List<TaskScheduled> getAllTaskDetail();

    /**
     * 执行任务
     * */
    void execTask(TaskScheduled taskScheduler);

    /**
     * 恢复
     * */
    void openTask(TaskScheduled taskScheduler);

    /**
     * 暂停
     * */
    void closeTask(TaskScheduled taskScheduler);

    /**
     * 删除作业
     * */
    void delTask(TaskScheduled taskScheduler);

    /**
     * 更新触发日志
     * @param record
     * @return
     */
    TaskFireLog updateLog(TaskFireLog record);

    /**
     * 获取日志
     * @param id
     * @return
     */
    TaskFireLog getFireLogById(Long id);

    /**
     * 查询日志
     * @param params
     * @return
     */
    Object queryLog(Map<String, Object> params);

    /**
     * 修改执行计划
     * */
    void updateOrInsertTask(TaskScheduled taskScheduled);

}
