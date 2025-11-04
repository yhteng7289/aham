package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by luyang.li on 2018/12/24.
 */
@Data
@Accessors(chain = true)
public class UserInfoResDTO extends BaseDTO {
    private String clientId;
    private String clientName;
    private String address;
    private String mobileNum;

}
