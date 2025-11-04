package com.pivot.aham.common.model;

import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;

import java.util.Date;

/**
 * 任务操作类
 *
 * @author addison
 * @since 2018年11月16日
 */
@Data
public class TaskScheduled extends BaseModel {
	public interface TimeZoneType {
		String america = "America/New_York";
		String defaultTimezone = "Asia/Shanghai";
	}


	public interface JobType {
		String job = "job";
		String statefulJob = "statefulJob";
	}

	//任务调用类型
	public interface TaskType {
		String local = "LOCAL";
		String dubbo = "DUBBO";
	}

	public TaskScheduled() {
	}

	public TaskScheduled(String taskGroup, String taskName) {
		this.taskGroup = taskGroup;
		this.taskName = taskName;
	}

	/** 任务名称 */
	private String taskName;
	/** 任务分组 */
	private String taskGroup;
	/** 任务状态 0禁用 1启用 2删除 */
	private String status;
	/** 任务运行时间表达式 */
	private String taskCron;
	/** 最后一次执行时间 */
	private Date previousFireTime;
	/** 下次执行时间 */
	private Date nextFireTime;
	/** 任务描述 */
	private String taskDesc;
	// 任务类型(是否阻塞)
	private String jobType;
	// 本地任务/dubbo任务
	private String taskType;
	// 运行系统
	private String targetSystem;
	// 任务对象
	private String targetObject;
	// 任务方法
	private String targetMethod;
	//联系人姓名
	private String contactName;
	//联系人邮箱
	private String contactEmail;

	private String timeZone = TimeZoneType.defaultTimezone;


}
