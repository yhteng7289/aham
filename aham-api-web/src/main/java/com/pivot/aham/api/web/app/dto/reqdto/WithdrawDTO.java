package com.pivot.aham.api.web.app.dto.reqdto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.analysis.WithdrawalTargetBankTypeEnum;
import com.pivot.aham.common.enums.analysis.WithdrawalTargetTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class WithdrawDTO extends BaseDTO {

    @NotBlank(message = "clientid 不能为空")
    @ApiModelProperty(value = "用户Id", required = true)
    private String clientid;

    @NotBlank(message = "clientid 不能为空")
    @ApiModelProperty(value = "goalid", required = true)
    private String goalid;

    @NotBlank(message = "type 不能为空")
    @ApiModelProperty(value = "type", required = true)
    private String type;

    @NotNull(message = "提现金额 不能为空")
    @ApiModelProperty(value = "提现金额", required = true)
    private String amt;

    @NotNull(message = "申请币种 不能为空")
    @ApiModelProperty(value = "申请币种", required = true)
    private CurrencyEnum applyCurrency;

    @NotNull(message = "提现金额 不能为空")
    @ApiModelProperty(value = "提现金额", required = true)
    private BigDecimal applyMoney;

    @NotNull(message = "目标货币 不能为空")
    @ApiModelProperty(value = "目标货币", required = true)
    private CurrencyEnum targetCurrency;

    @ApiModelProperty(value = "银行名称", required = true)
    private String bankname;

    @ApiModelProperty(value = "银行账号", required = true)
    private String bankacctno;

    @ApiModelProperty(value = "银行编号", required = true)
    private String bankcode;

    @ApiModelProperty(value = "日期", required = true)
    private String date;

    @ApiModelProperty(value = "時分", required = true)
    private String time;

    @NotNull(message = "提现目标类型 不能为空")
    @ApiModelProperty(value = "目标类型(1:银行现金账户2:银行卡)", required = true)

    private WithdrawalTargetTypeEnum targetType;
    @ApiModelProperty(value = "目标银行类型(1:本地2:海外)", required = true)
    private WithdrawalTargetBankTypeEnum targetBankType;

    @ApiModelProperty(value = "swift", required = false)
    private String swift;

    @ApiModelProperty(value = "branch", required = false)
    private String branch;

    @ApiModelProperty(value = "bankAddress", required = false)
    private String bankAddress;
}
