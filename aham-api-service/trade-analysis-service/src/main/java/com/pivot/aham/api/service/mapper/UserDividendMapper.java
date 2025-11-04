package com.pivot.aham.api.service.mapper;


import com.pivot.aham.api.service.mapper.model.UserDividendPO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 18/12/9.
 */
@Repository
public interface UserDividendMapper extends BaseMapper<UserDividendPO> {


    UserDividendPO queryUserDividend(UserDividendPO userDividendPO);

    void insertUserDividend(UserDividendPO userDividendPO);

    void updateUserDividend(UserDividendPO userDividendPO);

    BigDecimal getListByCond(UserDividendPO userDividendPO);

    List<UserDividendPO> listUserDividend(UserDividendPO userDividendPO);

    List<UserDividendPO> queryUserByTime(UserDividendPO userDividendPO);

}
