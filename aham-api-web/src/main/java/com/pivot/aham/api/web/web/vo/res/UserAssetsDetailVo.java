package com.pivot.aham.api.web.web.vo.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 18/12/10.
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "用户资产详情")
public class UserAssetsDetailVo {
    @ApiModelProperty(value = "用户资产,某只goal的Id", required = true)
    private String goalId;
    @ApiModelProperty(value = "用户资产,某只portfolio的Id", required = true)
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

    private String goalType;
    
    private String goalName;
    
    private String childName;

    private String RiskLevel;
    
    private String frequency;

    private BigDecimal targetMoney;

    private List<AssetValueVo> assetValueVos;
    
    private BigDecimal pendingDeposit = BigDecimal.ZERO; //Added by WooiTatt
    private BigDecimal pendingWithdraw = BigDecimal.ZERO; //Added by WooiTatt
}
