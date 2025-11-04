package com.pivot.aham.api.web.app.vo.req;

import com.pivot.aham.api.web.app.dto.reqdto.FundMyGoalListDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;


/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class FundMyGoalListReqVo {
    @ApiModelProperty(value = "客户id", required = true)
    @NotBlank(message = "客户id不能为空")
    private String clientId;

    public FundMyGoalListDTO convertToDto(FundMyGoalListReqVo fundMyGoalListReqVo) {
        FundMyGoalListDTO fundMyGoalListDTO = new FundMyGoalListDTO();
        fundMyGoalListDTO.setClientId(fundMyGoalListReqVo.getClientId());
        return fundMyGoalListDTO;
    }
}
