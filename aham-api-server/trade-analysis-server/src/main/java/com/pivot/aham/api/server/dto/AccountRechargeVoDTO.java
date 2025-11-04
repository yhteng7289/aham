package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.analysis.RechargeOrderStatusEnum;
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
public class AccountRechargeVoDTO extends BaseDTO {
    private Long accountId;
    
    private Date rechargeTime;

    private CurrencyEnum currency;

    private BigDecimal rechargeAmount;

    private String clientId;

    private RechargeOrderStatusEnum orderStatus;

    private Long rechargeOrderNo;

    private Long executeOrderNo;

    private String bankOrderNo;
//    private RechargeOperatTypeEnum operatType;
    private String goalId;
    private TpcfStatusEnum tpcfStatus;
    private Date tpcfTime;

    //辅助查询
    private Date rechargeStartTime;
    private Date rechargeEndTime;
    
    private Long totalTmpOrderId;
}
