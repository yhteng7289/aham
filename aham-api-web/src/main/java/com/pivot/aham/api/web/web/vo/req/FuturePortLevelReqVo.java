package com.pivot.aham.api.web.web.vo.req;

import com.pivot.aham.api.server.dto.PortFutureLevelDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * Created by luyang.li on 18/12/6.
 */
@Data
@Accessors(chain = true)
public class FuturePortLevelReqVo {

    @NotBlank(message = "portfolioId 不能为空")
    private String portfolioId;
    private String goalId;


    public PortFutureLevelDTO convertToDto(FuturePortLevelReqVo portLevelReqVo) {
        PortFutureLevelDTO futurePortLevelDTO = new PortFutureLevelDTO();
        futurePortLevelDTO.setPortfolioId(portLevelReqVo.getPortfolioId());
        futurePortLevelDTO.setGoalId(portLevelReqVo.getGoalId());
        return futurePortLevelDTO;
    }
}
