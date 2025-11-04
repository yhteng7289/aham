package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;


@Data
@Accessors(chain = true)
public class AccountNavDTO extends BaseDTO {
    private Long accountId;
    private BigDecimal fundNav;
    private Date navTime;
    private BigDecimal totalShare;
    private BigDecimal totalAsset;

}
