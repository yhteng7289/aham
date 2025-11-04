package com.pivot.aham.api.web.app.vo.req;

import com.pivot.aham.api.web.app.dto.reqdto.BankInfoDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class BankInfoReqVo {

    @ApiModelProperty(value = "客户id", required = true)
    @NotBlank(message = "客户id不能为空")
    private String clientId;

    public BankInfoDTO convertToDto(BankInfoReqVo bankInfoReqVo) {
        BankInfoDTO bankInfoDTO = new BankInfoDTO();
        bankInfoDTO.setClientId(bankInfoReqVo.getClientId());
        return bankInfoDTO;
    }
}
