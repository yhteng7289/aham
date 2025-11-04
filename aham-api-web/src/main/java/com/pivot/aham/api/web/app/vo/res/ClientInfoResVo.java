package com.pivot.aham.api.web.app.vo.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "用户资产查询请求参数")
public class ClientInfoResVo {

    @ApiModelProperty(value = "名", required = true)
    private String firstName;

    @ApiModelProperty(value = "姓", required = true)
    private String lastName;

    @ApiModelProperty(value = "性别", required = true)
    private String gender;

    @ApiModelProperty(value = "公民身份", required = true)
    private String citizenShip;

    @ApiModelProperty(value = "nric", required = true)
    private String nric;

    @ApiModelProperty(value = "dob日期", required = true)
    private String dob;

    @ApiModelProperty(value = "singaporePr", required = true)
    private String singaporePr;

    @ApiModelProperty(value = "email", required = true)
    private String email;

    @ApiModelProperty(value = "家庭电话", required = true)
    private String homeNumber;

    @ApiModelProperty(value = "手机号", required = true)
    private String mobileNumber;

    @ApiModelProperty(value = "地址1", required = true)
    private String addressLine1;

    @ApiModelProperty(value = "地址2", required = true)
    private String addressLine2;

    @ApiModelProperty(value = "邮编", required = true)
    private String postalCode;

    @ApiModelProperty(value = "客户id", required = true)
    private String clientId;

    @ApiModelProperty(value = "风险等级", required = true)
    private String risk;

    @ApiModelProperty(value = "风险等级", required = true)
    private String portfolioId;

    @ApiModelProperty(value = "汇率", required = true)
    private BigDecimal currency;

    @ApiModelProperty(value = "squirrelCashSGD", required = true)
    private BigDecimal squirrelCashSGD;

    @ApiModelProperty(value = "汇率日期", required = true)
    private String currencyDate;

}
