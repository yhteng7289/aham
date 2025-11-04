package com.pivot.aham.api.web.app.dto.resdto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class FriendResDTO extends BaseDTO{

    private String url;

    private String friendFirstName;

    private String friendLastName;

    private String status;

    private String clientId;

    private String resultCode;
    private String errorMsg;
}
