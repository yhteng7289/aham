package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.SystemUserMapper;
import com.pivot.aham.api.service.mapper.model.SystemUserPO;
import com.pivot.aham.api.service.service.SystemUserService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;


/**
 * Created by dexter on 11/4/2020
 * 
 */

@Service
public class SystemUserServiceImpl extends BaseServiceImpl<SystemUserPO, SystemUserMapper> implements SystemUserService {

    @Override
    public SystemUserPO queryUserByName(SystemUserPO queryParam) {
        return mapper.queryUserByName(queryParam);
    }
    

}
