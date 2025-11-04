package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.analysis.NeedRefundTypeEnum;
import com.pivot.aham.common.enums.analysis.VAOrderActionTypeEnum;
import com.pivot.aham.common.enums.analysis.VAOrderTradeStatusEnum;
import com.pivot.aham.common.enums.analysis.VAOrderTradeTypeEnum;
import com.pivot.aham.common.enums.CurrencyEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by luyang.li on 18/11/30.
 *
 * 银行虚拟账户流水
 */
@Data
@Accessors(chain = true)
public class BankVirtualAccountOrderDTO extends BaseDTO {
    private String clientId;
    private String virtualAccountNo;
    private BigDecimal cashAmount;
    private CurrencyEnum currency;
    private VAOrderTradeTypeEnum operatorType;
    private VAOrderActionTypeEnum actionType;
    private VAOrderTradeStatusEnum orderStatus;
//    private BankTransferStatusEnum bankStatus;
    private String bankOrderNo;
//    //防止UOB转账账户更换,流水可以对上
//    private String bankAccountNo;
    //是否需要退款
    private NeedRefundTypeEnum needRefundType;
    private Date tradeTime;
    private Long redeemApplyId;
    /**
     * 投资目标关联标识
     */
    private String referenceCode;

    /**
     * 辅助查询
     */
    private List<VAOrderActionTypeEnum> actionTypes;

    private Date startCreateTime;
    private Date endCreateTime;

    private Date startTradeTime;
    private Date endTradeTime;

    private List<String> virtualAccountNoList;
    private Integer pageNo;
    private Integer pageSize;

}
