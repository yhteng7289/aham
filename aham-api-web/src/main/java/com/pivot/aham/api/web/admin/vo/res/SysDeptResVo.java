package com.pivot.aham.api.web.admin.vo.res;

import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
@ApiModel("SysDeptResVo-请求对象说明")
public class SysDeptResVo extends BaseVo {
	@ApiModelProperty(value = "部门名称", required = true)
	private String deptName;
	@ApiModelProperty(value = "上级id", required = true)
	private Long parentId;
	@ApiModelProperty(value = "上级名称", required = true)
	private String parentName;
	@ApiModelProperty(value = "排序号", required = true)
	private Integer sortNo;
}
