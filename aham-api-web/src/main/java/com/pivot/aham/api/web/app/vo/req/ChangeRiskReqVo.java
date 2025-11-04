package com.pivot.aham.api.web.app.vo.req;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class ChangeRiskReqVo {
    private String clientId;
    private String risk;
}
