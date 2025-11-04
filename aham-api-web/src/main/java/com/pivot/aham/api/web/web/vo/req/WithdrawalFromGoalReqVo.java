package com.pivot.aham.api.web.web.vo.req;

import com.pivot.aham.common.core.base.BaseVo;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.analysis.WithdrawalTargetBankTypeEnum;
import com.pivot.aham.common.enums.analysis.WithdrawalTargetTypeEnum;
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
@ApiModel(value = "WithdrawalFromGoalReqVo请求对象说明")
public class WithdrawalFromGoalReqVo extends BaseVo{
   // @NotNull(message = "申请币种 不能为空")
    @ApiModelProperty(value = "申请币种",required = true)
    private CurrencyEnum applyCurrency;

   // @NotNull(message = "提现金额 不能为空")
    @ApiModelProperty(value = "提现金额",required = true)
    private BigDecimal applyMoney;

   // @NotNull(message = "目标货币 不能为空")
    @ApiModelProperty(value = "目标货币",required = true)
    private CurrencyEnum targetCurrency;

    @NotBlank(message = "用户Id 不能为空")
    @ApiModelProperty(value = "用户Id",required = true)
    private String clientId;

    @NotBlank(message = "goalId 不能为空")
    @ApiModelProperty(value = "goalId",required = true)
    private String goalId;

//    @NotBlank(message = "银行名称 不能为空")
    @ApiModelProperty(value = "银行名称",required = true)
    private String bankName;

//    @NotBlank(message = "银行账号 不能为空")
    @ApiModelProperty(value = "银行账号",required = true)
    private String bankAccountNo;

   // @NotNull(message = "提现目标类型 不能为空")
    @ApiModelProperty(value = "目标类型(1:银行现金账户2:银行卡)",required = true)
    private WithdrawalTargetTypeEnum targetType;

//    @NotNull(message = "目标银行类型 不能为空")
    @ApiModelProperty(value = "目标银行类型(1:本地2:海外)",required = true)
    private WithdrawalTargetBankTypeEnum targetBankType;

    //下面的三个参数是需要提现到海外银行卡的时候需要
    @ApiModelProperty(value = "swift",required = false)
    private String swift;
    @ApiModelProperty(value = "branch",required = false)
    private String branch;
    @ApiModelProperty(value = "bankAddress",required = false)
    private String bankAddress;

}
