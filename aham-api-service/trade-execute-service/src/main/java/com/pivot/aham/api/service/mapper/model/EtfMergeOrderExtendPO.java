package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.enums.EtfOrderExtendStatusEnum;
import com.pivot.aham.common.enums.EtfOrderTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: aham
 * @description:
 * @author: zhang7
 * @create: 2019-06-28 18:48
 **/
@Data
@Accessors(chain = true)
public class EtfMergeOrderExtendPO {

    private Long id;
    private Long mergeOrderId;

    private String productCode;
    private BigDecimal applyAmount;
    private BigDecimal applyShare;
    private BigDecimal confirmAmount;
    private BigDecimal confirmShare;
    private BigDecimal costFee;
    private EtfOrderTypeEnum orderType;
    private EtfOrderExtendStatusEnum orderExtendStatus;

    private Date applyTime;
    private Date confirmTime;
    private Date updateTime;
    private Date createTime;

}
