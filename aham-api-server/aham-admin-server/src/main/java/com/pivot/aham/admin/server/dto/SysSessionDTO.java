package com.pivot.aham.admin.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 登录用户session
 */
@Data
@Accessors
public class SysSessionDTO extends BaseDTO {
    private String sessionId;
    private String account;
    private String ip;
    private Date startTime;


}
