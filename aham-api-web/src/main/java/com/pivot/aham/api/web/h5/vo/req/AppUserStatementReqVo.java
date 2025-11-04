package com.pivot.aham.api.web.h5.vo.req;

import com.pivot.aham.api.server.dto.app.reqdto.AppUserStatementReqDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class AppUserStatementReqVo {
    @ApiModelProperty(value = "客户id", required = true)
    private String clientId;

    public AppUserStatementReqDTO convertToDto(AppUserStatementReqVo userStatementReqVo) {
        AppUserStatementReqDTO appUserStatementDTO = new AppUserStatementReqDTO();
        appUserStatementDTO.setClientId(userStatementReqVo.getClientId());
        return appUserStatementDTO;
    }
}
