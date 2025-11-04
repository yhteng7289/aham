package com.pivot.aham.api.web.web.vo.req;

import com.pivot.aham.common.core.base.BaseVo;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.analysis.WithdrawalTargetBankTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月10日
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "WithdrawalFromVirtalAccountReqVo请求对象说明")
public class WithdrawalFromVirtalAccountReqVo extends BaseVo {
    @NotNull(message = "提现账户来源类型不能为空")
    @ApiModelProperty(value = "提现账户来源类型(1:新加坡币2:美元)",required = true)
    private CurrencyEnum sourceAccountType;

    @NotNull(message = "提现金额不能为空")
    @ApiModelProperty(value = "提现金额",required = true)
    private BigDecimal applyMoney;

    @NotNull(message = "目标货币类型不能为空")
    @ApiModelProperty(value = "目标货币(1:新加坡币2:美元)",required = true)
    private CurrencyEnum targetCurrency;

    @NotBlank(message = "银行名称不能为空")
    @ApiModelProperty(value = "银行名称",required = true)
    private String bankName;

    @NotBlank(message = "银行账号不能为空")
    @ApiModelProperty(value = "银行账号",required = true)
    private String bankAccountNo;

    @NotBlank(message = "用户id不能为空")
    @ApiModelProperty(value = "用户id",required = true)
    private String clientId;

    @NotNull(message = "提现目标银行类型不能为空")
    @ApiModelProperty(value = "提现目标银行类型（1:本地2：海外）",required = true)
    private WithdrawalTargetBankTypeEnum withdrawalTargetBankType;

    //下面三个参数是提现到海外银行卡需要
    @ApiModelProperty(value = "swift",required = false)
    private String swift;
    @ApiModelProperty(value = "branch",required = false)
    private String branch;
    @ApiModelProperty(value = "bankAddress",required = false)
    private String bankaddress;

}
