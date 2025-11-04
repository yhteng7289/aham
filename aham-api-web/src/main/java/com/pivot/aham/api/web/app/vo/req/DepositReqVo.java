package com.pivot.aham.api.web.app.vo.req;

import com.pivot.aham.api.web.app.dto.reqdto.DepositReqDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class DepositReqVo {

    private String clientId;
    private String goalId;
    private String portfolioId;


    public DepositReqDTO convertToDto(DepositReqVo depositReqVo) {
        DepositReqDTO depositReqDTO = new DepositReqDTO();
        depositReqDTO.setClientId(depositReqVo.getClientId())
                .setGoalId(depositReqVo.getClientId())
                .setPortfolioId(depositReqVo.getPortfolioId());
        return depositReqDTO;
    }
}
