package com.pivot.aham.api.server.dto.app.resdto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class AppUserStatementResDTO extends BaseDTO {

    private String year;

    private List<StatementResDTO> statements;

}
