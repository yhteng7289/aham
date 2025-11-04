package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class VersionInfoResDTO extends BaseDTO {

    private String newVersion;
    private String downloadUrl;
    private String updateMessage;
    private String forcedUpdate;

}
