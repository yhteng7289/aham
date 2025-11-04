package com.pivot.aham.api.web.app.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class MyGoalReqVo {

    @ApiModelProperty(value = "客户id", required = true)
    @NotBlank(message = "客户id不能为空")
    private String clientId;

    @ApiModelProperty(value = "目标名称", required = true)
    @NotBlank(message = "目标名称不能为空")
    private String goalName;

    @ApiModelProperty(value = "目标id", required = true)
    @NotBlank(message = "目标id不能为空")
    private String goalId;

    @ApiModelProperty(value = "金额", required = true)
    @NotBlank(message = "金额不能为空")
    private String applyMoney;

    @ApiModelProperty(value = "portfolioId", required = true)
    @NotBlank(message = "portfolioId")
    private String portfolioId;

    @ApiModelProperty(value = "goalNo", required = true)
    @NotBlank(message = "goalNo")
    private String goalNo;

    @ApiModelProperty(value = "日期", required = true)
    @NotBlank(message = "日期不能为空")
    private String date;

    @ApiModelProperty(value = "时间", required = true)
    @NotBlank(message = "时间不能为空")
    private String time;
}
