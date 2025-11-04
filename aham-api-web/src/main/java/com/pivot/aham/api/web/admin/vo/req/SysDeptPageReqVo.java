package com.pivot.aham.api.web.admin.vo.req;

import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
@ApiModel("SysDeptPageReqVo-请求对象说明")
public class SysDeptPageReqVo extends BaseVo {
	@ApiModelProperty(value = "部门名称", required = false)
	private String deptName;
	@ApiModelProperty(value = "上级id", required = false)
	private Long parentId;
	@ApiModelProperty(value = "上级名称", required = false)
	private String parentName;


	@ApiModelProperty(value = "页码", required = true)
	private Integer pageIndex;
	@ApiModelProperty(value = "页大小", required = true)
	private Integer pageSize;
	@ApiModelProperty(value = "是否升序", required = true)
	private Boolean isAsc;
	@ApiModelProperty(value = "排序字符串", required = true)
	private String orderBy;
}
