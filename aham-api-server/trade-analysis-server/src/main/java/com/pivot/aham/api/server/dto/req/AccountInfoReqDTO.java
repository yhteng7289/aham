package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年11月29日
 */
@Data
@Accessors(chain = true)
public class AccountInfoReqDTO extends BaseDTO {

    private String portfolioId;
    private Integer pageNo;
    private Integer pageSize;
    private Long accountId;

    //查询辅助
    private Long likeAccountId;

}
