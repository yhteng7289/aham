package com.pivot.aham.api.web.web.vo;

import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月02日
 */
@Data
@Accessors(chain=true)
@ApiModel(value = "GetUserInfoReqVo请求对象说明")
public class ModifyUserInfoReqVo extends BaseVo{
    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名", required = true)
    private String userName;
    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号", required = true)
    private String phone;
    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称", required = true)
    private String nickName;

}
