package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.support.file.excel.annotation.ExcelField;
import com.pivot.aham.common.enums.SaxoOrderFeeStatusEnum;
import com.pivot.aham.common.enums.SaxoOrderStatusEnum;
import com.pivot.aham.common.enums.SaxoOrderTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class SaxoOrderPO {
    private Long id;
    private Integer uic;
    //private String saxoOrderCode;
    private SaxoOrderStatusEnum orderStatus;
    private SaxoOrderTypeEnum orderType;
    //private Integer applyShare;
    //private BigDecimal applyAmount;
    private Date applyTime;
    private Integer confirmShare;
    private BigDecimal confirmAmount;
    private Date confirmTime;
    private BigDecimal commission;
    private BigDecimal exchangeFee;
    private BigDecimal externalCharges;
    private BigDecimal performanceFee;
    private BigDecimal stampDuty;
    private String positionId;
    private SaxoOrderFeeStatusEnum feeStatus;
    
    @ExcelField(title = "Schema")
    private String etfCode;
    @ExcelField(title = "Agent Code")
    private String agentCode;
    @ExcelField(title = "Sub-Agent Code")
    private String subAgentCode;
    @ExcelField(title = "Portfolio ID/ Acc Number")
    private String portfolioId;
    @ExcelField(title = "SA/RD")
    private String orderTypeAhamDesc;
    @ExcelField(title = "SHARE")
    private Integer applyShare;
    @ExcelField(title = "Payment Method")
    private String paymentMethod;
    @ExcelField(title = "Sales Charge(%)")
    private String salesCharge;
    @ExcelField(title = "Agent Apporting(%)")
    private String agentApporting;
    @ExcelField(title = "Management Apporting(%)")
    private String managementApporting;
    @ExcelField(title = "MICR Code")
    private String micrCode;
    @ExcelField(title = "Remark")
    private String remark;
    @ExcelField(title = "Order ID")
    private String saxoOrderCode;
    @ExcelField(title = "Apply Amount")
    private BigDecimal applyAmount;
    
}
