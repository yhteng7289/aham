package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.AccountTypeEnum;
import com.pivot.aham.common.enums.analysis.InitDayEnum;
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
public class AccountInfoResDTO extends BaseDTO{
    private AccountTypeEnum investType;
    private String portfolioId;
    private InitDayEnum initDay;
    private String goalId;
    private String clientId;
    private String referenceCode;
    private String accountId;

}
