package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;

import java.util.Date;

@Data
public class AccountStaticsReqDTO extends BaseDTO {
    private Long accountId;
    /**
     * 统计日
     */
    private Date staticDate;

}
