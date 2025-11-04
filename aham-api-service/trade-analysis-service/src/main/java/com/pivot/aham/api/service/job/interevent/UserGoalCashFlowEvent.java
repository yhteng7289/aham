package com.pivot.aham.api.service.job.interevent;

import com.pivot.aham.api.service.mapper.model.AccountRechargePO;
import com.pivot.aham.api.service.mapper.model.AccountRedeemPO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class UserGoalCashFlowEvent {
//    private Date staticDate;
    private Long accountId;
    private List<AccountRedeemPO> accountRedeemPOs;
    private List<AccountRechargePO> accountRechargePOS;
}
