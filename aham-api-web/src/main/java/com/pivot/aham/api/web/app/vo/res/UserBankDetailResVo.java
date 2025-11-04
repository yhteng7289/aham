package com.pivot.aham.api.web.app.vo.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "银行信息详情")
public class UserBankDetailResVo {

    @ApiModelProperty(value = "银行名称", required = true)
    @NotBlank(message = "银行名称不能为空")
    private String bankName;

    @ApiModelProperty(value = "用户银行账号", required = true)
    @NotBlank(message = "用户银行账号不能为空")
    private String bankAcctNo;

    @ApiModelProperty(value = "用户名称", required = true)
    @NotBlank(message = "用户名称不能为空")
    private String accountName;

    @ApiModelProperty(value = "银行code", required = true)
    @NotBlank(message = "银行code不能为空")
    private String bankCode;

    @ApiModelProperty(value = "国家", required = true)
    @NotBlank(message = "country不能为空")
    private String country;
}
