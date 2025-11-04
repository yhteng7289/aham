package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class AnnexPO extends BaseModel {
    private String goalId;
    private String goalName;
    private Date staticDate;
    private BigDecimal cashFlow;

    private BigDecimal vt = BigDecimal.ZERO;
    private BigDecimal eem= BigDecimal.ZERO;
    private BigDecimal bndx= BigDecimal.ZERO;
    private BigDecimal shv= BigDecimal.ZERO;
    private BigDecimal emb= BigDecimal.ZERO;
    private BigDecimal vwob= BigDecimal.ZERO;
    private BigDecimal bwx= BigDecimal.ZERO;
    private BigDecimal hyg= BigDecimal.ZERO;
    private BigDecimal jnk= BigDecimal.ZERO;
    private BigDecimal mub= BigDecimal.ZERO;
    private BigDecimal lqd= BigDecimal.ZERO;
    private BigDecimal vcit= BigDecimal.ZERO;

    private BigDecimal flot= BigDecimal.ZERO;
    private BigDecimal ief= BigDecimal.ZERO;
    private BigDecimal uup= BigDecimal.ZERO;
    private BigDecimal pdbc= BigDecimal.ZERO;
    private BigDecimal gld= BigDecimal.ZERO;
    private BigDecimal vnq= BigDecimal.ZERO;
    private BigDecimal vea= BigDecimal.ZERO;
    private BigDecimal vpl= BigDecimal.ZERO;
    private BigDecimal ewa= BigDecimal.ZERO;
    private BigDecimal spy= BigDecimal.ZERO;
    private BigDecimal voo= BigDecimal.ZERO;
    private BigDecimal vti= BigDecimal.ZERO;

    private BigDecimal vgk= BigDecimal.ZERO;
    private BigDecimal ewj= BigDecimal.ZERO;
    private BigDecimal qqq= BigDecimal.ZERO;
    private BigDecimal ews= BigDecimal.ZERO;
    private BigDecimal ewz= BigDecimal.ZERO;
    private BigDecimal ashr= BigDecimal.ZERO;
    private BigDecimal vwo= BigDecimal.ZERO;
    private BigDecimal ilf= BigDecimal.ZERO;
    private BigDecimal rsx= BigDecimal.ZERO;
    private BigDecimal aaxj= BigDecimal.ZERO;
//    private BigDecimal asx= BigDecimal.ZERO;
//    private BigDecimal awc= BigDecimal.ZERO;

//    @HandleDot(ifHandleDot = true,newScale = 4)
    private BigDecimal transactionFee= BigDecimal.ZERO;
    private BigDecimal mgtFee= BigDecimal.ZERO;
    private BigDecimal custFee= BigDecimal.ZERO;
    private BigDecimal gstMgtFee= BigDecimal.ZERO;
    private BigDecimal totalFee= BigDecimal.ZERO;
//    @HandleDot(ifHandleDot = true,newScale = 4)
    private BigDecimal cashHolding= BigDecimal.ZERO;
//    @HandleDot(ifHandleDot = true,newScale = 4)
    private BigDecimal totalAsset= BigDecimal.ZERO;
    /**
     * 新币净值
     */
    private BigDecimal navInSgd= BigDecimal.ZERO;
    /**
     * 美元净值
     */
    private BigDecimal navInUsd= BigDecimal.ZERO;
    /**
     * saxo出金汇率
     */
    private BigDecimal fxRateForFundOut= BigDecimal.ZERO;
}
