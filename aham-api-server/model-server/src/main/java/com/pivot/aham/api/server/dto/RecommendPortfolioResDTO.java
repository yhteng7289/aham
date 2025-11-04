package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Created by luyang.li on 18/12/6.
 */
@Data
@Accessors(chain = true)
public class RecommendPortfolioResDTO extends BaseDTO {
//    @JSONField(serializeUsing = CustomDoubleIntSerialize.class)

    private RecommendPortfolio fixedIncome;
    private RecommendPortfolio alternative;
    private RecommendPortfolio developedEquity;
    private RecommendPortfolio emergingEquity;
    private RecommendPortfolio cash;
    private BigDecimal returnVol;

}
