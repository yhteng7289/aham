package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.SysConfigPO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author HP
 */
@Repository
public interface SysConfigMapper extends BaseMapper<SysConfigPO> {

    public SysConfigPO getStatus(String configName);

}
