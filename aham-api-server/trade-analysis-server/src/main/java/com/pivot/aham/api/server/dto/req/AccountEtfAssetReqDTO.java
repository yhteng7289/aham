package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by luyang.li on 19/3/4.
 */
@Data
@Accessors(chain = true)
public class AccountEtfAssetReqDTO extends BaseDTO{
    private Long accountId;

}
