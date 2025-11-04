package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.RedeemTypeEnum;
import com.pivot.aham.common.enums.analysis.RechargeOrderStatusEnum;
import com.pivot.aham.common.enums.analysis.RedeemOrderStatusEnum;
import com.pivot.aham.common.enums.recharge.TncfStatusEnum;
import com.pivot.aham.common.enums.recharge.TpcfStatusEnum;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * Created by luyang.li on 18/12/9.
 */
@Data
@Accessors(chain = true)
public class AccountRedeemVoDTO extends BaseDTO {
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
    /**
     * 提现类型
     */
    private RedeemTypeEnum redeemType;
    private BigDecimal oldApplyMoney;

    /**
     * 查询条件
     */
    private Date startRedeemApplyTime;
    private Date endRedeemApplyTime;

    //Added by WooiTatt
    private String isAnnualPerformanceFee; 
    private Long navBatchId;
    private Long accPerformanceFeeDesId;
}
