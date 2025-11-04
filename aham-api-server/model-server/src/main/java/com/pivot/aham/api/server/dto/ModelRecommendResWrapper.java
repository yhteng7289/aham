package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 19/3/5.
 */
@Data
@Accessors(chain = true)
public class ModelRecommendResWrapper extends BaseDTO {

    private String date;
    private String portfolioId;
    private BigDecimal score;
    //同 portfolio 下的用户平均收益
    private BigDecimal portfolioAveReturn;
    private List<ClassfiyEtfWrapper> modelData;

}
