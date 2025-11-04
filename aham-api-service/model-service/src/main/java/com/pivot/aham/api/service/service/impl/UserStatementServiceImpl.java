package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.UserStatementMapper;
import com.pivot.aham.api.service.mapper.model.UserStatementPO;
import com.pivot.aham.api.service.service.UserStatementService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserStatementServiceImpl extends BaseServiceImpl<UserStatementPO, UserStatementMapper> implements UserStatementService {
    @Override
    public UserStatementPO selectByStaticDate(UserStatementPO userCustStatementPO) {
        return null;
    }

    @Override
    public List<UserStatementPO> queryByClientId(UserStatementPO userCustStatementPO) {
        return mapper.queryByClientId(userCustStatementPO);
    }
}
