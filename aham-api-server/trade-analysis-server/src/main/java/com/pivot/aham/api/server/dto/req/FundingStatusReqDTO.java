package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseDTO;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author bjoon
 */
@Data
@Accessors(chain = true)
public class FundingStatusReqDTO extends BaseDTO {
    private Integer pageNo;
    private Integer pageSize;
    private String clientId;
    private Date startCreateTime;
    private Date endCreateTime;
    private String operationType;
}
