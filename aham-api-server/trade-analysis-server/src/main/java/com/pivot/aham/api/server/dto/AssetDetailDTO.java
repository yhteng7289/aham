package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.enums.ProductAssetStatusEnum;
import com.pivot.aham.common.enums.analysis.BankTransferStatusEnum;
import com.pivot.aham.common.enums.analysis.VAOrderTradeStatusEnum;
import com.pivot.aham.common.enums.analysis.VAOrderTradeTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Created by luyang.li on 18/12/12.
 */
@Data
@Accessors(chain = true)
public class AssetDetailDTO {
    /**
     * 交易类型
     */
    private VAOrderTradeTypeEnum operatorType;
    /**
     * 银行操作状态
     */
    private BankTransferStatusEnum bankStatus;
    /**
     * 交易状态
     */
    private VAOrderTradeStatusEnum orderStatus;
    /**
     * 产品ETF
     */
    private String productCode;
    /**
     * 产品份额
     */
    private BigDecimal productShare;
    /**
     * 产品金额
     */
    private BigDecimal productMonry;
    /**
     * 产品资产状态
     */
    private ProductAssetStatusEnum productAssetStatus;
    /**
     * 交易订单号
     */
    private Long bankOrderId;
    /**
     * 交易订单号
     */
    private Long dasOrderId;
    /**
     * das订单号
     */
    private String dasOrderNo;
    /**
     * das总订单号
     */
    private String dasTotalOrderNo;

}
