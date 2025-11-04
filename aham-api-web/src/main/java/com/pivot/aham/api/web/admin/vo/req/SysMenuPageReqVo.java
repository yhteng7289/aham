package com.pivot.aham.api.web.admin.vo.req;

import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel("SysMenuPageReqVo-请求对象说明")
public class SysMenuPageReqVo extends BaseVo {
    @ApiModelProperty(value = "菜单名", required = false)
    private String menuName;
    @ApiModelProperty(value = "父id", required = false)
    private Long parentId;
    @ApiModelProperty(value = "请求地址", required = false)
    private String request;
    @ApiModelProperty(value = "是否展示", required = false)
    private String isShow;
    @ApiModelProperty(value = "权限标识", required = false)
    private String permission;

    @ApiModelProperty(value = "页码", required = true)
    private Integer pageIndex;
    @ApiModelProperty(value = "页大小", required = true)
    private Integer pageSize;
    @ApiModelProperty(value = "是否升序", required = true)
    private Boolean isAsc;
    @ApiModelProperty(value = "排序字符串", required = true)
    private String orderBy;

}