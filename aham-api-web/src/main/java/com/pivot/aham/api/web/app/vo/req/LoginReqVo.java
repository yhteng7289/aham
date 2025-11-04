package com.pivot.aham.api.web.app.vo.req;

import com.pivot.aham.api.web.app.dto.reqdto.LoginDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class LoginReqVo {
    @ApiModelProperty(value = "登录接口，请求参数", required = true)
    @NotBlank(message = "phoneNumber不能为空")
    private String phoneNumber;
    @ApiModelProperty(value = "登录接口，请求参数", required = true)
    @NotBlank(message = "password不能为空")
    private String password;

    public LoginDTO convertToDto(LoginReqVo loginReqVo) {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPwd(loginReqVo.getPassword())
                .setPhonenumber(loginReqVo.getPhoneNumber());
        return loginDTO;
    }
}
