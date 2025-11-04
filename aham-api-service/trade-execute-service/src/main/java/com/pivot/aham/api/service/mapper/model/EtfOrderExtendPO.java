package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.enums.EtfOrderStatusEnum;
import com.pivot.aham.common.enums.EtfOrderTypeEnum;
import com.pivot.aham.common.enums.TradeType;
import com.pivot.aham.common.enums.analysis.PftAssetSourceEnum;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: aham
 * @description:
 * @author: zhang7
 * @create: 2019-07-02 17:38
 **/
@Data
@Accessors(chain = true)
@ToString
public class EtfOrderExtendPO {
    private Long id;
    private Long accountId;
    private EtfOrderTypeEnum orderType;
    private EtfOrderStatusEnum orderStatus;
    private String productCode;
    private BigDecimal costFee;
    private Date applyTime;
    private Date confirmTime;
    private BigDecimal applyAmount;
    private BigDecimal applyShare;
    private BigDecimal confirmAmount;
    private BigDecimal confirmShare;
    private Long outBusinessId;
    private Long mergeOrderId;
    private TradeType tradeType;
    private PftAssetSourceEnum sourceEnum;
}
