package com.pivot.aham.api.service.job.wrapperbean;

import com.google.common.collect.Lists;
import com.pivot.aham.api.service.mapper.model.AccountRechargePO;
import com.pivot.aham.api.service.mapper.model.AccountRedeemPO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 2018/12/24.
 */

@Data
@Accessors
public class AnalyTpcfTncfWrapperBean {
    private BigDecimal tncf = BigDecimal.ZERO;
    private List<AccountRedeemPO> accountRedeemPOs = Lists.newArrayList();

    private BigDecimal tpcf = BigDecimal.ZERO;
    List<AccountRechargePO> accountRechargePOS = Lists.newArrayList();

}
