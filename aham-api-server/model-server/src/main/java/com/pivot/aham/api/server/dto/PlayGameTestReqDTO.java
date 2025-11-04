package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PlayGameTestReqDTO extends BaseDTO {

    private Integer question;
    private Integer answer;
}
