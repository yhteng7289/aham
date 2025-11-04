package com.pivot.aham.api.web.admin.vo.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
@ApiModel("CurrentUserInfoResVo-请求对象说明")
public class CurrentUserInfoResVo implements Serializable {
    @ApiModelProperty(value = "用户实体",required = true)
    private SysUserResVo user;
    @ApiModelProperty(value = "权限列表",required = true)
    private List<?> menus;
}
