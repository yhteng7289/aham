package com.pivot.aham.api.server.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.analysis.CaEventTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;


@Data
@Accessors(chain = true)
public class DividendCallBackDTO  extends BaseDTO {
    /**
     * 除息日
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date exDate;
    /**
     * 股息登记日
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date tradeDate;
    /**
     * 价值日期
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date valueDate;

    private CaEventTypeEnum caEventTypeEnum;
    /**
     * 入账额
     */
    private BigDecimal netAmountAccountCurrency;

    /**
     * 产品code ，对应saxo的uic
     */
    private String productCode;

    /**
     * 分红幂等关联单号
     */
    private String dividendOrderId;

    public DividendCallBackDTO(Date exDate, Date tradeDate, Date valueDate, CaEventTypeEnum caEventTypeEnum, BigDecimal netAmountAccountCurrency, String productCode, String dividendOrderId) {
        this.exDate = exDate;
        this.tradeDate = tradeDate;
        this.valueDate = valueDate;
        this.caEventTypeEnum = caEventTypeEnum;
        this.netAmountAccountCurrency = netAmountAccountCurrency;
        this.productCode = productCode;
        this.dividendOrderId = dividendOrderId;
    }

    public DividendCallBackDTO() {
    }

    public static void main(String[] args){
        DividendCallBackDTO dividendCallBackDTO = new DividendCallBackDTO();
        dividendCallBackDTO.setCaEventTypeEnum(CaEventTypeEnum.CASH);
        dividendCallBackDTO.setExDate(new Date());
        dividendCallBackDTO.setNetAmountAccountCurrency(new BigDecimal("100.001"));
//        dividendCallBackDTO.setProductCode("");
        dividendCallBackDTO.setValueDate(new Date());
        dividendCallBackDTO.setTradeDate(new Date());
        dividendCallBackDTO.setDividendOrderId("1112");
        System.out.println(JSON.toJSONString(dividendCallBackDTO));
    }
}
