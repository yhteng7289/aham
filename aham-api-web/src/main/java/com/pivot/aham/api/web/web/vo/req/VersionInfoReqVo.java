package com.pivot.aham.api.web.web.vo.req;


import com.pivot.aham.api.server.dto.VersionInfoDTO;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class VersionInfoReqVo {

    private Integer version;
    private Integer clientId;

    public VersionInfoDTO convertToDto(VersionInfoReqVo versionInfoReqVo) {
        VersionInfoDTO versionInfoDTO = new VersionInfoDTO();
        versionInfoDTO.setVersion(versionInfoReqVo.getVersion());
        versionInfoDTO.setClientId(versionInfoReqVo.getClientId());
        return versionInfoDTO;
    }

}
