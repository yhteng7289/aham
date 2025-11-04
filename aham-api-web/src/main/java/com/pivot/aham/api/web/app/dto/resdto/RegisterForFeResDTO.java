package com.pivot.aham.api.web.app.dto.resdto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class RegisterForFeResDTO extends BaseDTO{

    private String resultCode;

    private String errorMsg;
}
