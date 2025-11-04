package com.pivot.aham.api.web.h5.vo.req;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class GetSuggestMoneyReqVo {
    @ApiModelProperty(value = "建议投资金额", required = true)
    private String childName;
    @ApiModelProperty(value = "目标金额", required = true)
    private BigDecimal targetMoney;
    @ApiModelProperty(value = "投资年限", required = true)
    private Integer totalYear;
    @ApiModelProperty(value = "风险等级", required = true)
    private Integer riskLevel;
    @ApiModelProperty(value = "投资期限", required = true)
    private Integer frequency;
    @ApiModelProperty(value = "goal类型", required = true)
    private Integer goalsType;
    @ApiModelProperty(value = "goal类型", required = true)
    private String portfolioId;

}
