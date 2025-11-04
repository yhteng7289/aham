package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;

@Data
public class SysConfigResDTO extends BaseDTO {

    private String configName;
    private Boolean status;

}
