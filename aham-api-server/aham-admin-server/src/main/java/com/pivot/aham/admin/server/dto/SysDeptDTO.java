package com.pivot.aham.admin.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 部门
 */
@Data
@Accessors(chain=true)
public class SysDeptDTO extends BaseDTO {
	/**
	 * 部门名称
	 */
	private String deptName;
	/**
	 * 部门父id
	 */
	private Long parentId;
	/**
	 * 部门名称
	 */
	private String parentName;
}
