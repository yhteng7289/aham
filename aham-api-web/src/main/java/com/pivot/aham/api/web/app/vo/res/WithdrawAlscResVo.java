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
@ApiModel(value = "提现接口返回参数")
public class WithdrawAlscResVo {

    @ApiModelProperty(value = "订单id", required = true)
    private String orderId;

    @ApiModelProperty(value = "客户id", required = true)
    private String clientId;

    @ApiModelProperty(value = "金额", required = true)
    private BigDecimal amt;

    @ApiModelProperty(value = "银行名称", required = true)
    private String bankName;

    @ApiModelProperty(value = "银行账号", required = true)
    private String bankAcctNo;

    @ApiModelProperty(value = "银行编号", required = true)
    private String bankCode;

    @ApiModelProperty(value = "日期", required = true)
    private String date;

    @ApiModelProperty(value = "时间", required = true)
    private String time;

}
