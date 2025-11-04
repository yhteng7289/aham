package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.UserEtfSharesStaticMapper;
import com.pivot.aham.api.service.mapper.model.UserEtfSharesStaticPO;
import com.pivot.aham.api.service.service.UserEtfSharesStaticService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserEtfSharesStaticServiceImpl extends BaseServiceImpl<UserEtfSharesStaticPO, UserEtfSharesStaticMapper> implements UserEtfSharesStaticService {
    @Override
    public List<UserEtfSharesStaticPO> queryListByTime(UserEtfSharesStaticPO userEtfSharesStaticPO) {
        return mapper.queryListByTime(userEtfSharesStaticPO);
    }

    @Override
    public UserEtfSharesStaticPO selectByStaticDate(UserEtfSharesStaticPO userEtfSharesStaticPO) {
        return mapper.selectByStaticDate(userEtfSharesStaticPO);
    }
}
