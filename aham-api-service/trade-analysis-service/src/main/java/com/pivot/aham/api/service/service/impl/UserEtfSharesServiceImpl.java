package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.UserEtfSharesMapper;
import com.pivot.aham.api.service.mapper.model.UserEtfSharesPO;
import com.pivot.aham.api.service.service.UserEtfSharesService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserEtfSharesServiceImpl extends BaseServiceImpl<UserEtfSharesPO, UserEtfSharesMapper> implements UserEtfSharesService {

    @Override
    public List<UserEtfSharesPO> queryListByTime(UserEtfSharesPO userEtfSharesPO) {
        return mapper.queryListByTime(userEtfSharesPO);
    }

    @Override
    public UserEtfSharesPO selectByStaticDate(UserEtfSharesPO userEtfSharesPO) {
        return mapper.selectByStaticDate(userEtfSharesPO);
    }
}
