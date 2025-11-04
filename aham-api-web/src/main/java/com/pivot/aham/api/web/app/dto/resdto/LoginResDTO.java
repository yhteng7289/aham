package com.pivot.aham.api.web.app.dto.resdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;

import java.util.List;

/**
 * @author YYYz
 */
@Data
public class LoginResDTO extends BaseDTO {

    @JsonProperty(value = "clientId")
    private String clientId;
    @JsonProperty(value = "firstname")
    private String firstName;
    @JsonProperty(value = "lastname")
    private String lastName;
    @JsonProperty(value = "virtualacctnosgd")
    private String virtualAcctNoSgd;
    @JsonProperty(value = "portfolioid")
    private String portfolioId;
    @JsonProperty(value = "goallist")
    private List<GoalDetailResDTO> goalList;

    private String resultCode;
    private String errorMsg;
}
