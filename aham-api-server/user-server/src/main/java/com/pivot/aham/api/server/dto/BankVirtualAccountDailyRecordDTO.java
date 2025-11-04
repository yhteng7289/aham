package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Created by luyang.li on 18/12/3.
 */
@Data
@Accessors(chain = true)
public class BankVirtualAccountDailyRecordDTO extends BaseDTO {
    private String clientId;
    private String virtualAccountNo;

    private Date startStaticDate;
    private Date endStaticDate;
}
