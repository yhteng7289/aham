package com.pivot.aham.api.web.web.vo.req;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PlayGameTestReqVo {
    private Integer question;
    private Integer answer;
}
