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
@ApiModel(value = "Deposit请求返回参数")
public class DepositResVo {
    @ApiModelProperty(value = "建议投资金额", required = true)
    private BigDecimal recommendTransFerAmt;
    @ApiModelProperty(value = "recipientName", required = true)
    private String recipientName;
    @ApiModelProperty(value = "银行名称", required = true)
    private String bankName;
    @ApiModelProperty(value = "账户id", required = true)
    private String accountNum;
    @ApiModelProperty(value = "referCode", required = true)
    private String referCode;
    @ApiModelProperty(value = "bankCode", required = true)
    private String bankCode;
    @ApiModelProperty(value = "branchCode", required = true)
    private String branchCode;
    @ApiModelProperty(value = "bankAddress", required = true)
    private String bankAddress;
    @ApiModelProperty(value = "swiftCode", required = true)
    private String swiftCode;

}
