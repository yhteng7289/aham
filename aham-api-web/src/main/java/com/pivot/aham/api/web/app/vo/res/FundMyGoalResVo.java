package com.pivot.aham.api.web.app.vo.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "FundMyGoal请求参数")
public class FundMyGoalResVo {

    @ApiModelProperty(value = "目标名称", required = true)
    private String goalName;

    @ApiModelProperty(value = "订单id", required = true)
    private String orderId;

    @ApiModelProperty(value = "目标id", required = true)
    private String goalId;

    @ApiModelProperty(value = "申请金额", required = true)
    private BigDecimal applymoney;

    @ApiModelProperty(value = "portfolioId", required = true)
    private String portfolioId;

    @ApiModelProperty(value = "risk", required = true)
    private String risk;

    @ApiModelProperty(value = "goalNo", required = true)
    private String goalNo;

    @ApiModelProperty(value = "客户id", required = true)
    private String clientId;

    @ApiModelProperty(value = "日期", required = true)
    private String date;

    @ApiModelProperty(value = "时间", required = true)
    private String time;


}
