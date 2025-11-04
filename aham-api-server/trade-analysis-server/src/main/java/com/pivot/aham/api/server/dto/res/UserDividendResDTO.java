package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.analysis.DividendHandelStatusEnum;
import com.pivot.aham.common.enums.analysis.DividendHandelTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
@Data
@Accessors
public class UserDividendResDTO extends BaseDTO {
    private String clientId;
    private Long accountId;
    private String goalId;
    private Date dividendDate;
    private BigDecimal dividendAmount;
    private DividendHandelStatusEnum handelStatus;
    private DividendHandelTypeEnum handelType;
    private String productCode;
    private String dividendOrderId;

    private List<String> goalIdList;


}
