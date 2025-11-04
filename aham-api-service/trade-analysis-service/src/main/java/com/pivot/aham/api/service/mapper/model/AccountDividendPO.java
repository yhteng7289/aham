package com.pivot.aham.api.service.mapper.model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.analysis.DividendHandelStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年02月21日
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_account_dividend",resultMap = "AccountDividendRes")
public class AccountDividendPO extends BaseModel{
    private Long accountId;
    /**
     * 除息日
     */
    private Date exDate;

    /**
     * 价值日期
     */
    private Date tradeDate;
    /**
     * 分红类型id
     */
    private Integer caEventTypeID;
    /**
     * 分红类型名称
     */
    private String caEventTypeName;
    /**
     * 入账额(税后金额)
     */
    private BigDecimal dividendAmount;
    private BigDecimal navDividendAmount;
    private DividendHandelStatusEnum handelStatus;
    private String productCode;
    private String dividendOrderId;


    private Date startTradeDate;
    private Date endTradeDate;


    //查询辅助
    private String likeProductCode;



}
