package com.pivot.aham.api.web.app.dto.resdto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class ReferResDTO extends BaseDTO{

    private List<FriendResDTO> friendList;

    private String resultCode;
    private String errorMsg;
}
