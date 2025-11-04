package com.pivot.aham.api.web.app.vo.req;

import com.pivot.aham.api.web.app.dto.reqdto.DelGoalDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class DelGoalReqVo {

    @ApiModelProperty(value = "客户id", required = true)
    @NotBlank(message = "客户id不能为空")
    private String clientId;

    @ApiModelProperty(value = "客户id", required = true)
    @NotBlank(message = "策略目标id不能为空")
    private String goalId;

    public DelGoalDTO convertToDto(DelGoalReqVo delGoalReqVo) {
        DelGoalDTO delGoalDTO = new DelGoalDTO();
        delGoalDTO.setClientId(delGoalReqVo.getClientId());
        delGoalDTO.setGoalId(delGoalReqVo.getGoalId());
        return delGoalDTO;
    }
}
