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
public class DelGoalResDTO extends BaseDTO {

    @JsonProperty(value = "clientid")
    private String clientId;
    @JsonProperty(value = "goalid")
    private String goalId;

    private String resultCode;
    private String errorMsg;
}
