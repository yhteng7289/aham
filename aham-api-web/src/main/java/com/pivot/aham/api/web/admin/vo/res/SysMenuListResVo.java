package com.pivot.aham.api.web.admin.vo.res;

import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel("SysMenuResVo-返回对象说明")
public class SysMenuListResVo extends BaseVo {
    @ApiModelProperty(value = "id", required = true)
    private Long permissionId;
    @ApiModelProperty(value = "菜单名", required = true)
    private String permissionName;
//    @ApiModelProperty(value = "上级菜单id", required = true)
//    private Long parentId;
//    @ApiModelProperty(value = "请求地址", required = true)
//    private String request;
//    @ApiModelProperty(value = "是否展示", required = true)
//    private String isShow;
    @ApiModelProperty(value = "权限标识", required = true)
    private String permissionCode;
    @ApiModelProperty(value = "权限标识", required = true)
    private String description;
//    @ApiModelProperty(value = "节点类型", required = true)
//    private MenuTypeEnum nodeType;
//    @ApiModelProperty(value = "菜单样式", required = true)
//    private String iconcls;
//    @ApiModelProperty(value = "是否展开", required = true)
//    private Boolean expand;
//    @ApiModelProperty(value = "排序号", required = true)
//    private Integer sortNo;
//    @ApiModelProperty(value = "上级菜单名", required = true)
//    private String parentName;
//    @ApiModelProperty(value = "子菜单", required = false)
//    private List<SysMenuListResVo> childNodes;

}