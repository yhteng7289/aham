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
@ApiModel(value = "目标详情")
public class UserGoalDetailVo {

    @ApiModelProperty(value = "目标名称", required = true)
    private String goalName;

    @ApiModelProperty(value = "目标Num", required = true)
    private String goalNo;

    @ApiModelProperty(value = "目标id", required = true)
    private String goalId;

    @ApiModelProperty(value = "建议金额", required = true)
    private BigDecimal suggestAmt;

    @ApiModelProperty(value = "投资频率", required = true)
    private String frequency;

    @ApiModelProperty(value = "portfolioId", required = true)
    private String portfolioId;

    @ApiModelProperty(value = "risk", required = true)
    private String risk;

    @ApiModelProperty(value = "持有资产金额", required = true)
    private BigDecimal currentAssetValue;
}
