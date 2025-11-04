package com.pivot.aham.api.service.mapper;


import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.mapper.model.AccountDividendPO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by luyang.li on 18/12/9.
 */
@Repository
public interface AccountDividendMapper extends BaseMapper<AccountDividendPO> {

    AccountDividendPO queryAccountDividend(AccountDividendPO accountDividend);

    void saveAccountDividend(AccountDividendPO accountDividendPO);

    void updateAccountDividend(AccountDividendPO accountDividendPO);

    List<AccountDividendPO> listAccountDividend(AccountDividendPO accountDividendParam);

    List<AccountDividendPO> listAccountDividendPage(Page<AccountDividendPO> rowBounds, AccountDividendPO accountDividendParam);


}
