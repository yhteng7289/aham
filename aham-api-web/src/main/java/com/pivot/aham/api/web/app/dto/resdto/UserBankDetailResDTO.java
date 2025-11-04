package com.pivot.aham.api.web.app.dto.resdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class UserBankDetailResDTO extends BaseDTO{

    @JsonProperty(value = "bankname")
    private String bankName;
    @JsonProperty(value = "bankacctno")
    private String bankAcctNo;
    @JsonProperty(value = "accountname")
    private String accountName;
    @JsonProperty(value = "bankcode")
    private String bankCode;
    @JsonProperty(value = "country")
    private String country;
}
