package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.PortFutureLevelPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.Date;
import java.util.List;

/**
 * Created by luyang.li on 19/2/22.
 */
public interface PortFutureLevelService extends BaseService<PortFutureLevelPO> {

    void synchroPortFutureLevel(Date date);

    List<PortFutureLevelPO> queryPortFutureLevel(PortFutureLevelPO queryParam);

}
