package com.pivot.aham.api.web.app.dto.resdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class GoalDetailResDTO extends BaseDTO{

    @JsonProperty(value = "goalid")
    private String goalId;
    @JsonProperty(value = "refcode")
    private String refCode;
    @JsonProperty(value = "recommended")
    private String recommended;
    @JsonProperty(value = "goalname")
    private String goalName;
    @JsonProperty(value = "assetvalue")
    private String assetValue;
    @JsonProperty(value = "chartDate")
    private List<ChartDateResDTO> chartDate;
    @JsonProperty(value = "value1")
    private List<AssetValueResDTO> value1;
    @JsonProperty(value = "value2")
    private List<NetDepositResDTO> value2;

    private String resultCode;
    private String errorMsg;
}
