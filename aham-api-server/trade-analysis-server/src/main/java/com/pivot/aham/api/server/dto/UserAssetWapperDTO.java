package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 19/1/9.
 */
@Data
@Accessors(chain = true)
public class UserAssetWapperDTO extends BaseDTO {

    private String clientId;
    private BigDecimal totalWealthMYR = BigDecimal.ZERO;
    private BigDecimal totalInvestmentMYR = BigDecimal.ZERO;
    //private BigDecimal squireelCashUsd = BigDecimal.ZERO;
    private List<UserAssetsDetailWrapperDTO> assetDetails;

    //总新币资产
    //private BigDecimal totalWealthSGD = BigDecimal.ZERO;
    //新币总投资
    //private BigDecimal totalInvestmentSGD = BigDecimal.ZERO;
    //松鼠账户总新币
    //private BigDecimal squireelCashSGD = BigDecimal.ZERO;
	
	//private BigDecimal freezeAmountUSD = BigDecimal.ZERO;
	//private BigDecimal freezeAmountSGD = BigDecimal.ZERO;
        
    //private BigDecimal pendingTotalDeposit = BigDecimal.ZERO; //Added by WooiTatt
    //private BigDecimal pendingTotalWithdraw = BigDecimal.ZERO; //Added by WooiTatt
	
}
