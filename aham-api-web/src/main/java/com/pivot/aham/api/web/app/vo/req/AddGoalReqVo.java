package com.pivot.aham.api.web.app.vo.req;

import com.pivot.aham.api.web.app.dto.reqdto.AddGoalDTO;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.enums.RiskLevelEnum;
import com.pivot.aham.common.enums.app.CostTypeEnum;
import com.pivot.aham.common.enums.app.FrequencyEnum;
import com.pivot.aham.common.enums.app.GoalTypeEnum;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class AddGoalReqVo {

    @ApiModelProperty(value = "客户id", required = true)
    @NotBlank(message = "客户id不能为空")
    private String clientId;

    @ApiModelProperty(value = "goal类型", required = true)
    @NotBlank(message = "goal类型不能为空")
    private String goalstype;

    @ApiModelProperty(value = "策略id", required = true)
    @NotBlank(message = "策略id不能为空")
    private String portfolioId;

    @ApiModelProperty(value = "孩子姓名", required = false)
    private String childname;

    @ApiModelProperty(value = "投资频率", required = true)
    @NotBlank(message = "投资频率不能为空")
    private String frequency;

    @ApiModelProperty(value = "投资金额", required = true)
    @NotBlank(message = "投资金额不能为空")
    private String recommendamt;

    private String investm;

    private String nyears;

    private String riskc;

    private String e_Money;

    private String e_RiskProfile;

    private String e_age;

    private String e_AgeReach;

    private String e_Year;

    private String e_StudyCourse;

    private String r_Year;

    private String r_Money;

    private String r_AgeNow;

    private String r_AgeRetire;

    private String r_RiskProfile;

    private String g_RiskProfile;

    private String k_RiskProfile;

    public AddGoalDTO convertToDto(AddGoalReqVo addGoalReqVo) {
        AddGoalDTO addGoalDTO = new AddGoalDTO();
        addGoalDTO.setClientId(addGoalReqVo.getClientId());
        addGoalDTO.setFrequency(FrequencyEnum.forValue(Integer.valueOf(addGoalReqVo.getFrequency())).getDesc());
        addGoalDTO.setGoalstype(GoalTypeEnum.forValue(Integer.valueOf(addGoalReqVo.getGoalstype())).getDesc());
        addGoalDTO.setPortfolioId(addGoalReqVo.getPortfolioId());
        addGoalDTO.setRecommendamt(addGoalReqVo.getRecommendamt());

        if (addGoalReqVo.getGoalstype().equalsIgnoreCase("1")) {
            if (addGoalReqVo.getInvestm().isEmpty()) {
                throw new BusinessException("Parameter [investm] cannot be empty");
            }
            if (addGoalReqVo.getNyears().isEmpty()) {
                throw new BusinessException("Parameter [nyears] cannot be empty or zero");
            }
            if (addGoalReqVo.getRiskc().isEmpty()) {
                throw new BusinessException("Parameter [riskc] cannot be empty or zero");
            }
            addGoalDTO.setInvestm(addGoalReqVo.getInvestm());
            addGoalDTO.setNyears(addGoalReqVo.getNyears());
            addGoalDTO.setRiskc(RiskLevelEnum.forValue(Integer.valueOf(addGoalReqVo.getRiskc())).getDesc());
        } else if (addGoalReqVo.getGoalstype().equalsIgnoreCase("2")) {
            if (addGoalReqVo.getChildname().isEmpty()) {
                throw new BusinessException("Parameter [childname] cannot be empty or zero");
            }
            if (addGoalReqVo.getE_age().isEmpty()) {
                throw new BusinessException("Parameter [e_age] cannot be empty or zero");
            }
            if (addGoalReqVo.getE_Money().isEmpty()) {
                throw new BusinessException("Parameter [e_Money] cannot be empty or zero");
            }
            if (addGoalReqVo.getE_RiskProfile().isEmpty()) {
                throw new BusinessException("Parameter [e_RiskProfile] cannot be empty");
            }
            if (addGoalReqVo.getE_AgeReach().isEmpty()) {
                throw new BusinessException("Parameter [e_AgeReach] cannot be empty or zero");
            }
            if (addGoalReqVo.getE_Year().isEmpty()) {
                throw new BusinessException("Parameter [e_Year] cannot be empty or zero");
            }
            if (addGoalReqVo.getE_StudyCourse().isEmpty()) {
                throw new BusinessException("Parameter [e_StudyCourse] cannot be empty or null");
            }
            addGoalDTO.setChildName(addGoalReqVo.getChildname());
            addGoalDTO.setE_AgeReach(addGoalReqVo.getE_AgeReach());
            addGoalDTO.setE_Money(addGoalReqVo.getE_Money());
            addGoalDTO.setE_RiskProfile(RiskLevelEnum.forValue(Integer.valueOf(addGoalReqVo.getE_RiskProfile())).getDesc());
            addGoalDTO.setE_age(addGoalReqVo.getE_age());
            addGoalDTO.setE_Year(addGoalReqVo.getE_Year());
            addGoalDTO.setE_StudyCourse(CostTypeEnum.forValue(Integer.valueOf(addGoalReqVo.getE_StudyCourse())).getDesc());
        } else if (addGoalReqVo.getGoalstype().equalsIgnoreCase("3")) {
            if (addGoalReqVo.getR_Money().isEmpty()) {
                throw new BusinessException("Parameter [r_Money] cannot be empty or zero");
            }
            if (addGoalReqVo.getR_RiskProfile().isEmpty()) {
                throw new BusinessException("Parameter [r_RiskProfile] cannot be empty");
            }
            if (addGoalReqVo.getR_Year().isEmpty()) {
                throw new BusinessException("Parameter [r_Year] cannot be empty or zero");
            }
            if (addGoalReqVo.getR_AgeNow().isEmpty()) {
                throw new BusinessException("Parameter [r_AgeNow] cannot be empty or zero");
            }
            if (addGoalReqVo.getR_AgeRetire().isEmpty()) {
                throw new BusinessException("Parameter [r_AgeRetire] cannot be empty or zero");
            }
            addGoalDTO.setR_Money(addGoalReqVo.getR_Money());
            addGoalDTO.setR_Year(addGoalReqVo.getR_Year());
            addGoalDTO.setR_RiskProfile(RiskLevelEnum.forValue(Integer.valueOf(addGoalReqVo.getR_RiskProfile())).getDesc());
            addGoalDTO.setR_AgeNow(addGoalReqVo.getR_AgeNow());
            addGoalDTO.setR_AgeRetire(addGoalReqVo.getR_AgeRetire());
        } else if (addGoalReqVo.getGoalstype().equalsIgnoreCase("4")) {
            if (addGoalReqVo.getG_RiskProfile().isEmpty()) {
                throw new BusinessException("Parameter [g_RiskProfile] cannot be empty or zero");
            }
            addGoalDTO.setG_RiskProfile(RiskLevelEnum.forValue(Integer.valueOf(addGoalReqVo.getG_RiskProfile())).getDesc());

        } else if (addGoalReqVo.getGoalstype().equalsIgnoreCase("5")) {
            if (addGoalReqVo.getChildname().isEmpty()) {
                throw new BusinessException("Parameter [childname] cannot be empty or zero");
            }
            if (addGoalReqVo.getK_RiskProfile().isEmpty()) {
                throw new BusinessException("Parameter [k_RiskProfile] cannot be empty or zero");
            }
            addGoalDTO.setK_RiskProfile(RiskLevelEnum.forValue(Integer.valueOf(addGoalReqVo.getK_RiskProfile())).getDesc());
            addGoalDTO.setChildName(addGoalReqVo.getChildname());
        }
        return addGoalDTO;
    }

}
