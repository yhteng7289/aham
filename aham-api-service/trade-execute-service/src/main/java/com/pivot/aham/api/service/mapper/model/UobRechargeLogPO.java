package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.recharge.UobRechargeStatusEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
@Data
public class UobRechargeLogPO extends BaseModel {

    private String bankOrderNo;
    private String clientName;
    private String virtualAccountNo;
    private CurrencyEnum currency;
    private String referenceCode;
    private BigDecimal cashAmount;
    private Date tradeTime;
    private UobRechargeStatusEnum rechargeStatus;

}
