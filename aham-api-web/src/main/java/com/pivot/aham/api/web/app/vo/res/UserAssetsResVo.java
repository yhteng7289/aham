package com.pivot.aham.api.web.app.vo.res;

import com.pivot.aham.api.web.web.vo.res.UserAssetsDetailVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 18/12/9.
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "用户资产查询返回结果")
public class UserAssetsResVo {
    @ApiModelProperty(value = "clientId", required = true)
    private String clientId;
    @ApiModelProperty(value = "用户资产,资产详情:assetDetails", required = true)
    private List<UserAssetsDetailVo> assetDetails;

    private BigDecimal totalWealthUsd = BigDecimal.ZERO;
    private BigDecimal totalInvestmentUsd = BigDecimal.ZERO;
    private BigDecimal squireelCashUsd = BigDecimal.ZERO;
    //总新币资产
    private BigDecimal totalWealthSGD = BigDecimal.ZERO;
    //新币总投资
    private BigDecimal totalInvestmentSGD = BigDecimal.ZERO;
    //松鼠账户总新币
    private BigDecimal squireelCashSGD = BigDecimal.ZERO;

	private BigDecimal freezeAmountUSD = BigDecimal.ZERO;
	private BigDecimal freezeAmountSGD = BigDecimal.ZERO;
        
    private BigDecimal pendingDeposit = BigDecimal.ZERO; //Added by WooiTatt
    private BigDecimal pendingWithdraw = BigDecimal.ZERO; //Added by WooiTatt

}
