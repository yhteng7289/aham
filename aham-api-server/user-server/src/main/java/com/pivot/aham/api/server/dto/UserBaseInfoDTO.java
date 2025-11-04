package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by luyang.li on 18/12/5.
 */
@Data
@Accessors(chain = true)
public class UserBaseInfoDTO extends BaseDTO{
    private UserInfoDTO userInfoDTO;
    private BankVirtualAccountDTO bankVirtualAccountDTO;
}
