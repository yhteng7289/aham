package com.pivot.aham.api.web.app.vo.req;


import com.pivot.aham.api.web.app.dto.reqdto.ForgetPwdDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class ForgetPwdReqVo {

    @ApiModelProperty(value = "忘记密码发送的email地址", required = true)
    @NotBlank(message = "email地址不能为空")
    private String email;

    public ForgetPwdDTO convertToDto(ForgetPwdReqVo forgetPwdReqVo) {
        ForgetPwdDTO forgetPwdDTO = new ForgetPwdDTO();
        forgetPwdDTO.setEmail(forgetPwdReqVo.getEmail());
        return forgetPwdDTO;
    }
}
