package com.pivot.aham.admin.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 部门
 */
@TableName("sys_dept")
@Data
@Accessors(chain=true)
public class SysDept extends BaseModel {
	/**
	 * 部门名称
	 */
	@TableField("dept_name")
	private String deptName;
	/**
	 * 部门父id
	 */
	@TableField("parent_id")
	private Long parentId;
//	/**
//	 * 排序
//	 */
//	@TableField("sort_no")
//	private Integer sortNo;
	/**
	 * 部门名称
	 */
	@TableField(exist = false)
	private String parentName;
}
