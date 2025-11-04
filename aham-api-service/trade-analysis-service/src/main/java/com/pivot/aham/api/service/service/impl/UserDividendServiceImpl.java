package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.UserDividendMapper;
import com.pivot.aham.api.service.mapper.model.UserDividendPO;
import com.pivot.aham.api.service.service.UserDividendService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 2018/12/24.
 */
@Service
@Slf4j
public class UserDividendServiceImpl extends BaseServiceImpl<UserDividendPO, UserDividendMapper> implements UserDividendService {


    @Override
    public UserDividendPO queryUserDividend(UserDividendPO userDividendPO) {
        return mapper.queryUserDividend(userDividendPO);
    }

    @Override
    public void insert(UserDividendPO userDividendPO) {
        mapper.insertUserDividend(userDividendPO);
    }

    @Override
    public void update(UserDividendPO userDividendPO) {
        mapper.updateUserDividend(userDividendPO);
    }

    @Override
    public BigDecimal getUserDividendMoney(UserDividendPO userDividendPO) {
        BigDecimal userDividendMoney = BigDecimal.ZERO;
        List<UserDividendPO> userDividendPOS = mapper.listUserDividend(userDividendPO);
        for (UserDividendPO dividendPO : userDividendPOS) {
            userDividendMoney = userDividendMoney.add(dividendPO.getDividendAmount()).setScale(6, BigDecimal.ROUND_HALF_UP);
        }
        return userDividendMoney;
    }

    @Override
    public BigDecimal getListByCond(UserDividendPO userDividendPO) {
        return mapper.getListByCond(userDividendPO);
    }

    @Override
    public List<UserDividendPO> listUserDividend(UserDividendPO userDividendPO) {
        return mapper.listUserDividend(userDividendPO);
    }

    @Override
    public List<UserDividendPO> queryUserByTime(UserDividendPO userDividendPO) {
        return  mapper.queryUserByTime(userDividendPO);
    }
}

