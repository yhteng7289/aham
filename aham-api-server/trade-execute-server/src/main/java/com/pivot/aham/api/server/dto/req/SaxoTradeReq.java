package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.enums.EtfOrderTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by hao.tong on 2018/12/11.
 */
@Data
public class SaxoTradeReq implements Serializable {
    private Long outBusinessId;
    private Long accountId;
    private String etfCode;
    private BigDecimal amount;
    private EtfOrderTypeEnum orderType;
}
