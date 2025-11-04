package com.pivot.aham.api.web.web.vo.res;

import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月01日
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "用户在goal上分配金额")
public class UserSetGoalMoneyResVo extends BaseVo {
    @ApiModelProperty(value = "投资目标:goalsId", required = true)
    private String goalsId;
    @ApiModelProperty(value = "clientId", required = true)
    private String clientId;
    @ApiModelProperty(value = "用户分配给goal的金额:money", required = true)
    private BigDecimal money = BigDecimal.ZERO;
    @ApiModelProperty(value = "用户分配给goal的转出订单号:transNo", required = true)
    private Long transNo;

}
