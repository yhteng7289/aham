package com.pivot.aham.api.web.in.vo;

import com.pivot.aham.common.enums.analysis.BalTradeTypeEnum;
import com.pivot.aham.common.enums.analysis.ExecuteStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("AccountBalAdjDetailResVo-请求对象说明")
public class AccountBalAdjDetailResVo {
    @ApiModelProperty(value = "调仓记录id",required = true)
    private String balId;
    @ApiModelProperty(value = "产品code",required = true)
    private String productCode;
    @ApiModelProperty(value = "当前金额",required = true)
    private BigDecimal currentHold;
    @ApiModelProperty(value = "目标金额",required = true)
    private BigDecimal targetHold;
    @ApiModelProperty(value = "实际交易金额",required = true)
    private BigDecimal tradeAmount;
    @ApiModelProperty(value = "执行状态",required = true)
    private ExecuteStatusEnum executeStatus;
    @ApiModelProperty(value = "交易类型",required = true)
    private BalTradeTypeEnum tradeType;

}
