package com.pivot.aham.api.web.app.vo.res;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "银行信息返回参数")
public class BankInfoResVo {

    @ApiModelProperty(value = "userBankDetailListVo", required = true)
    private List<UserBankDetailResVo> userBankDetailListVo;

    @ApiModelProperty(value = "countryBankList", required = true)
    private List<CountryBankListResVo> countryBankList;

    @ApiModelProperty(value = "客户id", required = true)
    private String clientId;


}
