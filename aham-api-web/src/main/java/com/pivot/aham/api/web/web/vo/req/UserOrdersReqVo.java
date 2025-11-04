package com.pivot.aham.api.web.web.vo.req;

import com.pivot.aham.api.server.dto.req.UserGoalOrderDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by luyang.li on 18/12/9.
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "用户订单查询请求参数")
public class UserOrdersReqVo {
    @ApiModelProperty(value = "clientId", required = true)
    private String clientId;
    private String goalId;
    private String portfolioId;

    public UserGoalOrderDTO convertToDto(UserOrdersReqVo userOrdersReqVo) {
        UserGoalOrderDTO userOrderDTO = new UserGoalOrderDTO();
        userOrderDTO.setClientId(userOrdersReqVo.getClientId());
        userOrderDTO.setGoalId(userOrdersReqVo.getGoalId());
        userOrderDTO.setPortfolioId(userOrdersReqVo.getPortfolioId());
        return userOrderDTO;
    }
}
