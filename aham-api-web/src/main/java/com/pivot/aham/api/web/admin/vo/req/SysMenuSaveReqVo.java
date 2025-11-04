package com.pivot.aham.api.web.admin.vo.req;

import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel("SysMenuUpdateReqVo-请求对象说明")
public class SysMenuSaveReqVo extends BaseVo {
//    @ApiModelProperty(value = "id", required = true)
//    private Long permissionId;
    @ApiModelProperty(value = "权限名", required = false)
    private String permissionName;
    @ApiModelProperty(value = "父id", required = false)
    private Long parentId;
    @ApiModelProperty(value = "请求地址", required = false)
    private String request;
//    @ApiModelProperty(value = "是否展示", required = false)
//    private String isShow;
    @ApiModelProperty(value = "权限标识", required = false)
    private String permissionCode;



}