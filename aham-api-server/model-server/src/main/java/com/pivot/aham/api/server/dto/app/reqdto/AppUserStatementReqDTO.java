package com.pivot.aham.api.server.dto.app.reqdto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class AppUserStatementReqDTO extends BaseDTO {

    private String clientId;
}
