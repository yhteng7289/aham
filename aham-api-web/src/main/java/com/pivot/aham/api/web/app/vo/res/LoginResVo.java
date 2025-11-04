package com.pivot.aham.api.web.app.vo.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "登录接口返回请求参数")
public class LoginResVo {

    @ApiModelProperty(value = "客户id", required = true)
    private String clientId;

    @ApiModelProperty(value = "名", required = true)
    private String firstName;

    @ApiModelProperty(value = "姓", required = true)
    private String lastName;

    @ApiModelProperty(value = "虚拟SGD账户", required = true)
    private String virtualAcctNoSgd;

    @ApiModelProperty(value = "策略id", required = true)
    private String portfolioId;

    @ApiModelProperty(value = "风险等级", required = true)
    private String risk;

    @ApiModelProperty(value = "目标List", required = true)
    private List<GoalDetailResVo> goalList;

    @ApiModelProperty(value = "token", required = true)
    private String token;

}
