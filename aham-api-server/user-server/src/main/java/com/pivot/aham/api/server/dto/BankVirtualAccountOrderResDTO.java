package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.MatchTypeEnum;
import com.pivot.aham.common.enums.analysis.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by luyang.li on 18/11/30.
 *
 * 银行虚拟账户流水
 */
@Data
@Accessors(chain = true)
public class BankVirtualAccountOrderResDTO extends BaseDTO {
    private String virtualAccountNo;
    private BigDecimal cashAmount;
    private CurrencyEnum currency;
    private VAOrderTradeTypeEnum operatorType;
    private VAOrderActionTypeEnum actionType;
    private VAOrderTradeStatusEnum orderStatus;
    private BankTransferStatusEnum bankStatus;
    private String bankOrderNo;
//    //防止UOB转账账户更换,流水可以对上
//    private String bankAccountNo;
    //是否需要退款
    private NeedRefundTypeEnum needRefundType;
    private Date tradeTime;
    private Long redeemApplyId;
    /**
     * 投资目标关联标识 ,要投资必须有此值
     */
    private String referenceCode;
    /**
     * 匹配类型
     */
    private MatchTypeEnum matchType;

}
