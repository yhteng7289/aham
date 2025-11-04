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
 * @since 2018年12月01日
 */
@Data
@Accessors(chain=true)
@ApiModel(value = "MemberLoginVo请求对象说明")
public class MemberLoginResVo extends BaseVo {
    @ApiModelProperty(value = "APP会话token")
    private String token;
}
