package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.AccountAssetPO;
import com.pivot.aham.api.service.mapper.model.AccountDividendPO;
import com.pivot.aham.api.service.mapper.model.AccountEtfSharesPO;
import com.pivot.aham.api.service.mapper.model.UserDividendPO;

import java.util.List;

/**
 * Created by luyang.li on 2018/12/24.
 */
public interface DividendSupportService {

    void handelUserDividend(List<UserDividendPO> userDividendPOS,
                            AccountAssetPO accountAssetPO,
                            AccountDividendPO accountDividendPO,
                            AccountEtfSharesPO accountEtfSharesPO);

    void handelAccountDividend(AccountDividendPO accountDividendPO, AccountEtfSharesPO accountEtfSharesPO);

}
