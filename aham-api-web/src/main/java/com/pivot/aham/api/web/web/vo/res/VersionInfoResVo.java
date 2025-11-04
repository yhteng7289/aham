package com.pivot.aham.api.web.web.vo.res;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class VersionInfoResVo {

    private String newVersion;
    private String downloadUrl;
    private String updateMessage;
    private String forcedUpdate;

}
