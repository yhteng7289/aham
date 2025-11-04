package com.pivot.aham.api.web.app.vo.req;

import com.pivot.aham.api.server.dto.UserAssetDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "用户资产查询请求参数")
public class UserAssetsReqVo {
    @ApiModelProperty(value = "clientId", required = true)
    private String clientId;

    public UserAssetDTO convertToDto(UserAssetsReqVo userAssetsReqVo) {
        UserAssetDTO userAssetDTO = new UserAssetDTO();
        userAssetDTO.setClientId(userAssetsReqVo.getClientId());
        return userAssetDTO;
    }
}
