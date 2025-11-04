package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Created by luyang.li on 19/1/9.
 */
@Data
@Accessors(chain = true)
public class UserAssetsDetailWrapperDTO extends BaseDTO {
    private String goalId;
    private String portfolioId;

    //总收益 贾总给的计算公式
    private BigDecimal totalReturnMYR;
    //策略收益   贾总的计算公式
    private BigDecimal portfolioReturn;
    //fxImpact  换汇收益 贾总的计算公式
    //private BigDecimal fxImpactSGD;
    //目前现有的持仓金额（美金资产 * T2）
    //private BigDecimal assetValueSGD;
    //目前持有的投资的新币 （总投资新币 - 总提现新币）
    private BigDecimal investedAmountMYR;
    //总的投资新币
    private BigDecimal totalDepositMYR;
    //总的提现新币
    private BigDecimal totalWithdrawalMYR;
    
    private BigDecimal pendingDeposit = BigDecimal.ZERO; //Added by WooiTatt
    private BigDecimal pendingWithdraw = BigDecimal.ZERO; //Added by WooiTatt

}
