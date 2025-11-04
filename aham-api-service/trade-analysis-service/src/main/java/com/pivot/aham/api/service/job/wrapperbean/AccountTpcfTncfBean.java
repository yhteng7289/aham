package com.pivot.aham.api.service.job.wrapperbean;

import com.pivot.aham.api.service.mapper.model.AccountDividendPO;
import com.pivot.aham.api.service.mapper.model.AccountRechargePO;
import com.pivot.aham.api.service.mapper.model.AccountRedeemPO;
import com.pivot.aham.api.service.mapper.model.UserDividendPO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Created by luyang.li on 2018/12/24.
 */
@Data
@Accessors
public class AccountTpcfTncfBean {
    private List<AccountRechargePO> accountRechargePOS;
    private List<AccountDividendPO> accountDividendPOS;
    private List<AccountRedeemPO> accountRedeemPOs;
    private List<UserDividendPO> userDividendPOS;

}
