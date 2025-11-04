package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderActionTypeEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderTradeStatusEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderTradeTypeEnum;
import lombok.Data;

import java.util.List;

@Data
public class SaxoAccountOrderReqDTO extends BaseDTO {
    private String clientId;
    private Long accountId;
    private String goalId;
    private String bankOrderNo;
    private Long redeemApplyId;
    /**
     * 订单状态
     */
    private SaxoOrderTradeStatusEnum orderStatus;
    /**
     * 币种
     */
    private CurrencyEnum currency;
    /**
     * 交易状态
     */
    private SaxoOrderTradeTypeEnum operatorType;
    /**
     * 交易来源
     */
    private SaxoOrderActionTypeEnum actionType;

    private List<String> goalIdList;
}
