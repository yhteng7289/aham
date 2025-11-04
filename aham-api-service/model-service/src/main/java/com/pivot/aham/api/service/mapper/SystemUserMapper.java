package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.SystemUserPO;
import org.springframework.stereotype.Repository;
import com.pivot.aham.common.core.base.BaseMapper;
@Repository
public interface SystemUserMapper extends BaseMapper<SystemUserPO> {
	
	SystemUserPO queryUserByName(SystemUserPO queryParam);

}
