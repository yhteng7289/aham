package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;

import java.util.Date;

@Data
public class UserEtfSharesReqDTO extends BaseDTO {
    private Date staticDate;
    private Long accountId;
    private String clientId;
    private String goalId;
}
