package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Created by luyang.li on 19/3/5.
 */
@Data
@Accessors(chain = true)
public class UserProfitInfoReqDTO extends BaseDTO {
    private String clientId;
    private Long accountId;
    private String goalId;
    private Date profitDate;


}
