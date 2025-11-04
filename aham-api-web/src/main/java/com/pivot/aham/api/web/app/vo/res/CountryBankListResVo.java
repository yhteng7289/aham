package com.pivot.aham.api.web.app.vo.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class CountryBankListResVo {

    @ApiModelProperty(value = "bankList", required = true)
    private List<UserBankDetailResVo> bankList;

    @ApiModelProperty(value = "country", required = true)
    private String country;
}
