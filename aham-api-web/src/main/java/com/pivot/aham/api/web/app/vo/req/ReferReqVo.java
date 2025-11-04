package com.pivot.aham.api.web.app.vo.req;

import com.pivot.aham.api.web.app.dto.reqdto.ReferDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class ReferReqVo {

    @ApiModelProperty(value = "客户id", required = true)
    @NotBlank(message = "客户id不能为空")
    private String clientId;

    public ReferDTO convertToDto(ReferReqVo referReqVo) {
        ReferDTO referDTO = new ReferDTO();
        referDTO.setClientId(referReqVo.getClientId());
        return referDTO;
    }
}
