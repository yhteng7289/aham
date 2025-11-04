package com.pivot.aham.api.web.app.dto.reqdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class AddGoalDTO extends BaseDTO {

    private String clientId;

    private String goalstype;

    private String portfolioId;

    private String childName;

    private String frequency;

    private String recommendamt;

    @JsonProperty("e_Money")
    private String e_Money;

    @JsonProperty("e_RiskProfile")
    private String e_RiskProfile;

    @JsonProperty("e_age")
    private String e_age;

    @JsonProperty("e_Year")
    private String e_Year;

    @JsonProperty("e_AgeReach")
    private String e_AgeReach;

    @JsonProperty("e_StudyCourse")
    private String e_StudyCourse;

    @JsonProperty("r_Year")
    private String r_Year;

    @JsonProperty("r_Money")
    private String r_Money;

    @JsonProperty("r_RiskProfile")
    private String r_RiskProfile;

    @JsonProperty("r_AgeNow")
    private String r_AgeNow;

    @JsonProperty("r_AgeRetire")
    private String r_AgeRetire;

    @JsonProperty("investm")
    private String investm;

    @JsonProperty("nyears")
    private String nyears;

    @JsonProperty("riskc")
    private String riskc;

    @JsonProperty("g_RiskProfile")
    private String g_RiskProfile;

    @JsonProperty("k_RiskProfile")
    private String k_RiskProfile;

}
