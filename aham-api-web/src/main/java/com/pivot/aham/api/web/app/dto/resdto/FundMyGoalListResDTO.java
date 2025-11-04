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
public class FundMyGoalListResDTO extends BaseDTO{

    @JsonProperty(value = "fundmygoals")
    private List<UserGoalDetailDTO> fundMyGoals;
    @JsonProperty(value = "clientid")
    private String clientId;

    private String resultCode;
    private String errorMsg;
}
