package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.analysis.RedeemOrderStatusEnum;
import com.pivot.aham.common.enums.recharge.TncfStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
@Data
@Accessors
public class AccountRedeemResDTO extends BaseDTO {
    private Long accountId;
    private String clientId;
    private Date redeemApplyTime;
    private Date redeemConfirmTime;
    private BigDecimal applyMoney;
    private BigDecimal confirmMoney;
    private BigDecimal confirmShares;
    private RedeemOrderStatusEnum orderStatus;
    private Long totalTmpOrderId;
    private String goalId;
    private Long redeemApplyId;
    private TncfStatusEnum tncfStatus;
    private Date navDate;
    private Date tncfTime;


}
