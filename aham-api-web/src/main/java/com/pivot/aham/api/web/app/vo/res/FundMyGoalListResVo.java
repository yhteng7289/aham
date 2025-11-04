package com.pivot.aham.api.web.app.vo.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "所有目标查询请求参数")
public class FundMyGoalListResVo {

    @ApiModelProperty(value = "userGoalDetailList", required = true)
    private List<UserGoalDetailVo> userGoalDetailList;

    @ApiModelProperty(value = "客户id", required = false)
    private String clientId;

    @ApiModelProperty(value = "squirrelCashSGD", required = false)
    private BigDecimal squirrelCashSGD;

}
