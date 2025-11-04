package com.pivot.aham.api.web.app.dto.resdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class UserGoalDetailDTO extends BaseDTO {

    @JsonProperty(value = "goalname")
    private String goalName;
    @JsonProperty(value = "goalno")
    private String goalNo;
    @JsonProperty(value = "goalid")
    private String goalId;
    @JsonProperty(value = "suggestamt")
    private String suggestAmt;
    @JsonProperty(value = "frequency")
    private String frequency;
    @JsonProperty(value = "portfolioid")
    private String portfolioId;
    @JsonProperty(value = "currentassetvalue")
    private String currentAssetValue;
    @JsonProperty(value = "type")
    private String type;
    private String resultCode;
    private String errorMsg;
}
