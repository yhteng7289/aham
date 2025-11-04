package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.SysConfigPO;
import com.pivot.aham.common.core.base.BaseService;

/**
 *
 * @author HP
 */
public interface SysConfigService extends BaseService<SysConfigPO> {

    SysConfigPO getStatus(String configName);

}
