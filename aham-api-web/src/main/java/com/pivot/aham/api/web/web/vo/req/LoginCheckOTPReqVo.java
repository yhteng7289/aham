package com.pivot.aham.api.web.web.vo.req;

import com.pivot.aham.api.server.dto.CheckOTPDTO;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginCheckOTPReqVo {

    private String message;
    private String mobileNum;
    private String userName;

    public CheckOTPDTO convertToDto(LoginCheckOTPReqVo loginCheckOTPReqVo) {
        CheckOTPDTO checkOTPDTO = new CheckOTPDTO();
        checkOTPDTO.setMessage(loginCheckOTPReqVo.getMessage());
        checkOTPDTO.setMobileNum(loginCheckOTPReqVo.getMobileNum());
        checkOTPDTO.setUserName(loginCheckOTPReqVo.getUserName());
        return checkOTPDTO;
    }
}
