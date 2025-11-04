package com.pivot.aham.api.web.admin.vo.req;

import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@ApiModel("SysDeptUpdateReqVo-请求对象说明")
public class SysDeptUpdateReqVo extends BaseVo {
    @NotNull(message = "部门id不能为空")
    @ApiModelProperty(value = "id", required = true)
    private Long id;
    @ApiModelProperty(value = "部门名称", required = false)
    private String deptName;
    @ApiModelProperty(value = "上级id", required = false)
    private Long parentId;
    @ApiModelProperty(value = "上级名称", required = false)
    private String parentName;
    @ApiModelProperty(value = "排序号", required = false)
    private Integer sortNo;




}