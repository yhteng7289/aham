package com.pivot.aham.api.web.web.vo.req;

import com.pivot.aham.api.server.dto.CheckOTPDTO;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CheckOTPReqVo {

    private String message;
    private String mobileNum;

    public CheckOTPDTO convertToDto(CheckOTPReqVo checkOTPReqVo) {
        CheckOTPDTO checkOTPDTO = new CheckOTPDTO();
        checkOTPDTO.setMessage(checkOTPReqVo.getMessage());
        checkOTPDTO.setMobileNum(checkOTPReqVo.getMobileNum());
        return checkOTPDTO;
    }
}
