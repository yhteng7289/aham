package com.pivot.aham.api.web.web.vo.req;

import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月01日
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "删除用户策略目标")
public class DeleteUserGoalInfoReqVo extends BaseVo {

    @NotNull(message = "投资目标不能为空")
    @ApiModelProperty(value = "投资目标:goalsId", required = true)
    private String goalsId;

    @NotNull(message = "clientId不能为空")
    @ApiModelProperty(value = "clientId", required = true)
    private String clientId;

}
