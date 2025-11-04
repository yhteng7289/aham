package com.pivot.aham.api.web.web.vo.req;

import com.pivot.aham.api.server.dto.NewSysUserDTO;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginOTPReqVo {

    private String userName;

    public NewSysUserDTO convertToDto(LoginOTPReqVo loginOTPReqVo) {
        NewSysUserDTO newSysUserDTO = new NewSysUserDTO();
        newSysUserDTO.setUserName(loginOTPReqVo.getUserName());
        return newSysUserDTO;
    }
}
