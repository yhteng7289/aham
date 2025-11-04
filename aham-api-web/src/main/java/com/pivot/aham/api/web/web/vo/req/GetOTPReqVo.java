package com.pivot.aham.api.web.web.vo.req;

import com.pivot.aham.api.server.dto.GetOTPDTO;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetOTPReqVo {

    private String mobileNum;

    public GetOTPDTO convertToDto(GetOTPReqVo getOTPReqVo) {
        GetOTPDTO getOTPDTO = new GetOTPDTO();
        getOTPDTO.setMobieNum(getOTPReqVo.getMobileNum());
        return getOTPDTO;
    }
}
