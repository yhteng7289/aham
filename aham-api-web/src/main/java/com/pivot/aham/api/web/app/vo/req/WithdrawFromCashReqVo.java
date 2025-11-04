package com.pivot.aham.api.web.app.vo.req;

import com.pivot.aham.api.web.app.dto.reqdto.WithdrawAlscDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class WithdrawFromCashReqVo {

    @NotNull(message = "提现账户来源类型不能为空")
    @ApiModelProperty(value = "提现账户来源类型(1:新加坡币2:美元)", required = true)
    private Integer sourceAccountType;

    @NotNull(message = "提现金额不能为空")
    @ApiModelProperty(value = "提现金额")
    private BigDecimal applyMoney;

    @NotNull(message = "goalId不能为空")
    @ApiModelProperty(value = "目标")
    private String goalId;

    @NotNull(message = "目标货币类型不能为空")
    @ApiModelProperty(value = "目标货币(1:新加坡币2:美元)", required = true)
    private Integer targetCurrency;

    @NotBlank(message = "银行名称不能为空")
    @ApiModelProperty(value = "银行名称", required = true)
    private String bankName;

    @NotBlank(message = "银行账号不能为空")
    @ApiModelProperty(value = "银行账号", required = true)
    private String bankAccountNo;

    @NotBlank(message = "银行编号不能为空")
    @ApiModelProperty(value = "银行编号", required = true)
    private String bankCode;

    @NotBlank(message = "用户id不能为空")
    @ApiModelProperty(value = "用户id", required = true)
    private String clientId;

    @NotNull(message = "提现目标银行类型不能为空")
    @ApiModelProperty(value = "提现目标银行类型（1:本地2：海外）", required = true)
    private Integer withdrawalTargetBankType;

    @NotNull(message = "国家不能为空")
    @ApiModelProperty(value = "国家", required = true)
    private String country;

    //下面三个参数是提现到海外银行卡需要
    @ApiModelProperty(value = "swift", required = false)
    private String swift;
    @ApiModelProperty(value = "branch", required = false)
    private String branch;
    @ApiModelProperty(value = "bankAddress", required = false)
    private String bankaddress;
    @ApiModelProperty(value = "targetBankType", required = false)
    private String targetBankType;
    @ApiModelProperty(value = "targetType", required = false)
    private String targetType;

    public WithdrawAlscDTO convertToDto(WithdrawFromCashReqVo withdrawFromCashReqVo) {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        String parseDate = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            parseDate = sdf.format(today);
        } catch (Exception e) {

        }

        WithdrawAlscDTO withdrawAlscDTO = new WithdrawAlscDTO();
        withdrawAlscDTO.setAmt(String.valueOf(withdrawFromCashReqVo.getApplyMoney()))
                .setBankacctno(withdrawFromCashReqVo.getBankAccountNo())
                .setBankcode(withdrawFromCashReqVo.getBankCode());

        if (withdrawFromCashReqVo.getGoalId() != null) {
            withdrawAlscDTO.setGoalid(withdrawFromCashReqVo.getGoalId());
        }

        if (withdrawFromCashReqVo.getCountry() != null) {
            withdrawAlscDTO.setCountry(withdrawFromCashReqVo.getCountry());
        } else {
            withdrawAlscDTO.setCountry("");
        }

        Integer withdrawalTargetBankTypeParam = withdrawFromCashReqVo.getWithdrawalTargetBankType();
        if (1 == withdrawalTargetBankTypeParam) {
            withdrawAlscDTO.setType("Local");
        } else if (2 == withdrawalTargetBankTypeParam) {
            withdrawAlscDTO.setType("Oversea");
        }

        if (withdrawFromCashReqVo.getTargetType() != null) {
            if (withdrawFromCashReqVo.getTargetType().equalsIgnoreCase("1")) {
                withdrawAlscDTO.setTargetType("SquirrelCashAccount");
            } else if (withdrawFromCashReqVo.getTargetType().equalsIgnoreCase("2")) {
                withdrawAlscDTO.setTargetType("BankAccount");
            }
        }

        withdrawAlscDTO.setBankname(withdrawFromCashReqVo.getBankName())
                .setClientid(withdrawFromCashReqVo.getClientId())
                .setDate(parseDate.split("\\s+")[0])
                .setTime(parseDate.split("\\s+")[1]);
        return withdrawAlscDTO;
    }
}
