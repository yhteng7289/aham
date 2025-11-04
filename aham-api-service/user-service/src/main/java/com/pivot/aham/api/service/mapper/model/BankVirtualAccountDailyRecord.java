package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.CurrencyEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by luyang.li on 18/11/30.
 *
 * 银行虚拟账户
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_bank_virtual_account_dr",resultMap = "BankVirtualAccountDailyRecordRes")
public class BankVirtualAccountDailyRecord extends BaseModel {
    private String clientId;
    /**
     * //虚拟账户
     */
    private String virtualAccountNo;
    /**
     * //可用金额
     */
    private BigDecimal cashAmount;
    /**
     * //冻结金额
     */
    private BigDecimal freezeAmount;
    /**
     * //'账户已使用金额'
     */
    private BigDecimal usedAmount;
    /**
     * //账户币种类型：1:美金账户,2:新币账户
     */
    private CurrencyEnum currency;

    private Date staticDate;


    //查询辅助
    private Date startStaticDate;
    private Date endStaticDate;



}
