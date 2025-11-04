package com.pivot.aham.api.server.dto.app.resdto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class StatementResDTO extends BaseDTO {

    private String statementUrl;
    private String month;
}
