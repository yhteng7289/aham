package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.SystemUserPO;
import com.pivot.aham.common.core.base.BaseService;


/**
 * Created by dexter on 11/4/2020
 */
public interface SystemUserService extends BaseService<SystemUserPO> {

    
    SystemUserPO queryUserByName(SystemUserPO queryParam);

}
