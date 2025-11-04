package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import java.util.Date;

@Data
public class TmpOrderRecordReqDTO extends BaseDTO {
    private Long accountId;
    private Date startApplyTime;
    private Date endApplyTime;

    private Integer pageNo;
    private Integer pageSize;
}
