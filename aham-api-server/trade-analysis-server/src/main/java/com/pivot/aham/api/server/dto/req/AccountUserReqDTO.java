package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;

@Data
public class AccountUserReqDTO extends BaseDTO {
    private String clientId;
    private Long accountId;
    private String goalId;

}
