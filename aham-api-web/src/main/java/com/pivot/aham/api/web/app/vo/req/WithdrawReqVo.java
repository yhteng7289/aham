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
public class WithdrawReqVo {

    @NotNull(message = "申请币种 不能为空")
    @ApiModelProperty(value = "申请币种", required = true)
    private Integer applyCurrency;

    @NotNull(message = "提现金额 不能为空")
    @ApiModelProperty(value = "提现金额", required = true)
    private BigDecimal applyMoney;

    @NotNull(message = "目标货币 不能为空")
    @ApiModelProperty(value = "目标货币", required = true)
    private Integer targetCurrency;

    @NotBlank(message = "用户Id 不能为空")
    @ApiModelProperty(value = "用户Id", required = true)
    private String clientId;

    @NotBlank(message = "goalId 不能为空")
    @ApiModelProperty(value = "goalId", required = true)
    private String goalId;

    @ApiModelProperty(value = "银行名称", required = true)
    private String bankName;

    @ApiModelProperty(value = "银行账号", required = true)
    private String bankAccountNo;

    @ApiModelProperty(value = "银行账号", required = true)
    private String bankCode;

    @NotNull(message = "提现目标类型 不能为空")
    @ApiModelProperty(value = "目标类型(1:银行现金账户2:银行卡)", required = true)
    private Integer targetType;

    //@NotNull(message = "目标银行类型 不能为空")
    @ApiModelProperty(value = "目标银行类型(1:本地2:海外)", required = true)
    private Integer targetBankType;

    //下面的三个参数是需要提现到海外银行卡的时候需要
    @ApiModelProperty(value = "swift", required = false)
    private String swift;
    @ApiModelProperty(value = "branch", required = false)
    private String branch;
    @ApiModelProperty(value = "bankAddress", required = false)
    private String bankAddress;

    @ApiModelProperty(value = "country", required = false)
    private String country;

    public WithdrawAlscDTO convertToDto(WithdrawReqVo withdrawFromCashReqVo) {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        String parseDate = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
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

        if (withdrawFromCashReqVo.getTargetType() == 1) {
            withdrawAlscDTO.setTargetType("SquirrelCashAccount");
        } else if (withdrawFromCashReqVo.getTargetType() == 2) {
            withdrawAlscDTO.setTargetType("BankAccount");
        }
        // 1 : Local , 2 : Oversea
        if (withdrawFromCashReqVo.getTargetBankType() == 1) {
            withdrawAlscDTO.setType("Local");
        } else {
            withdrawAlscDTO.setType("Oversea");

        }
        withdrawAlscDTO.setBankname(withdrawFromCashReqVo.getBankName())
                .setClientid(withdrawFromCashReqVo.getClientId())
                .setDate(parseDate.split("\\s+")[0])
                .setTime(parseDate.split("\\s+")[1]);
        return withdrawAlscDTO;
    }

}
