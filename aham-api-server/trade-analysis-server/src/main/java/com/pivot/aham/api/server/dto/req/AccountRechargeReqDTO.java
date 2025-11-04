package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
@Data
@Accessors
public class AccountRechargeReqDTO extends BaseDTO {
    private Long accountId;
    private String clientId;
    private String goalId;


}
