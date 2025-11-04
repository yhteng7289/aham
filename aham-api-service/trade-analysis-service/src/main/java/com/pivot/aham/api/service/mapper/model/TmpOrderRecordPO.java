package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.EtfOrderTypeEnum;
import com.pivot.aham.common.enums.TmpOrderActionTypeEnum;
import com.pivot.aham.common.enums.analysis.TmpOrderExecuteStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 临时订单
 *
 * @author addison
 * @since 2018年12月13日
 */
@Data
@Accessors(chain=true)
@TableName(value = "t_tmp_order_record",resultMap = "TmpOrderRecordRes")
public class TmpOrderRecordPO extends BaseModel{
    private Long accountId;
    private Long tmpOrderId;
    private Long totalTmpOrderId;
    private Long executeOrderId;
    private String productCode;
    private BigDecimal applyMoney;
    private BigDecimal confirmMoney;
    private BigDecimal confirmTradeShares;
    private TmpOrderActionTypeEnum actionType;
    private TmpOrderExecuteStatusEnum tmpOrderTradeStatus;
    private EtfOrderTypeEnum tmpOrderTradeType;
    private Date applyTime;
    private Date confirmTime;
    private BigDecimal transCost;

    private Date startApplyTime;
    private Date endApplyTime;

}
