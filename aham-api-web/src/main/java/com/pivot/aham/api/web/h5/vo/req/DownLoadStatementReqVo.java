package com.pivot.aham.api.web.h5.vo.req;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DownLoadStatementReqVo {
    private String url;
}
