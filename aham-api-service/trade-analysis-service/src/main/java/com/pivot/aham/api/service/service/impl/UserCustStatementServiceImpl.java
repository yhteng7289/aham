package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.UserCustStatementMapper;
import com.pivot.aham.api.service.mapper.model.UserCustStatementPO;
import com.pivot.aham.api.service.service.UserCustStatementService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserCustStatementServiceImpl extends BaseServiceImpl<UserCustStatementPO, UserCustStatementMapper> implements UserCustStatementService {
    @Override
    public UserCustStatementPO selectByStaticDate(UserCustStatementPO userCustStatementPO) {
        return null;
    }
}
