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
public class AddGoalResDTO extends BaseDTO{

    @JsonProperty(value="refcode")
    private String refCode;
    @JsonProperty(value="clientid")
    private String clientId;
    @JsonProperty(value="virtualaccount")
    private String virtualAccount;

    private String bankCode;

    private String branchCode;

    private String swiftCode;

    private String bankAddress;

    private String recipientName;

    private String resultCode;
    private String errorMsg;
}
