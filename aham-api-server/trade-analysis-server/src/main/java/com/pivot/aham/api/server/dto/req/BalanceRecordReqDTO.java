package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;

import java.util.Date;

@Data
public class BalanceRecordReqDTO extends BaseModel {
    private Long accountId;
    private Date balStartTime;

    private Date startBalStartTime;
    private Date endBalStartTime;

    private Integer pageNo;
    private Integer pageSize;

}
