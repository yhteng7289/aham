package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.UserDividendPO;
import com.pivot.aham.common.core.base.BaseService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 18/12/9.
 */
public interface UserDividendService extends BaseService<UserDividendPO> {

    UserDividendPO queryUserDividend(UserDividendPO userDividendParam);

    void insert(UserDividendPO userDividendPO);

    void update(UserDividendPO userDividendPO);

    BigDecimal getUserDividendMoney(UserDividendPO userDividendPO);
    BigDecimal getListByCond(UserDividendPO userDividendPO);

    List<UserDividendPO> listUserDividend(UserDividendPO userDividendPO);

    List<UserDividendPO> queryUserByTime(UserDividendPO userDividendPO);

}
