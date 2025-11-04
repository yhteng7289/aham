package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.MatchTypeEnum;
import com.pivot.aham.common.enums.analysis.NeedRefundTypeEnum;
import com.pivot.aham.common.enums.analysis.VAOrderActionTypeEnum;
import com.pivot.aham.common.enums.analysis.VAOrderTradeStatusEnum;
import com.pivot.aham.common.enums.analysis.VAOrderTradeTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by luyang.li on 18/11/30.
 * <p>
 * 银行虚拟账户流水
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_bank_virtual_account_order",resultMap = "bankVirtualAccountOrderMap")
public class BankVirtualAccountOrder extends BaseModel {
    /**
     * 虚拟账户
     */
    private String virtualAccountNo;
    /**
     * 交易金额
     */
    private BigDecimal cashAmount;

    /**
     * 账户类型：1:新币账户,2:美金账户
     */
    private CurrencyEnum currency;
    /**
     * 交易状态
     */
    private VAOrderTradeStatusEnum orderStatus;
    /**
     * 交易订单号
     */
    private Long orderNo;
    /**
     * 交易类型
     */
    private VAOrderTradeTypeEnum operatorType;

    /**
     * 动作类型
     */
    private VAOrderActionTypeEnum actionType;
    /**
     * 银行订单号
     */
    private String bankOrderNo;
    /**
     * 是否需要退款
     */
    private NeedRefundTypeEnum needRefundType;
    /**
     * 交易时间
     */
    private Date tradeTime;
    /**
     * 提现申请id
     */
    private Long redeemApplyId;
    /**
     * 投资目标关联标识
     */
    private String referenceCode;

    /**
     * 匹配类型
     */
    private MatchTypeEnum matchType=MatchTypeEnum.MATCH;
    /**
     * 辅助查询
     */
    private List<VAOrderActionTypeEnum> actionTypes;

    private Date startCreateTime;
    private Date endCreateTime;

    private Date startTradeTime;
    private Date endTradeTime;

    private List<String> virtualAccountNoList;
}
