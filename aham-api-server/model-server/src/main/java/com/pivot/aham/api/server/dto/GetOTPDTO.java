package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * Created by luyang.li on 18/12/6.
 */
@Data
@Accessors(chain = true)
public class GetOTPDTO extends BaseDTO {

    private String mobieNum;

}
