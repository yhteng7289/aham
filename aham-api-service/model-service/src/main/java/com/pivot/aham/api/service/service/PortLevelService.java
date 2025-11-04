package com.pivot.aham.api.service.service;

import com.pivot.aham.api.server.dto.PortLevelDTO;
import com.pivot.aham.api.service.mapper.model.PortLevel;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;

/**
 * Created by luyang.li on 18/12/7.
 */
public interface PortLevelService extends BaseService<PortLevel> {

    /**
     * 收益曲线(查询的是历史所有的所以这个方法没有设置日期)
     *
     * @return
     * @param portLevelDTO
     */
    List<PortLevel> getPortLevels(PortLevelDTO portLevelDTO);

    /**
     * 同步 FTP 收益曲线
     *
     */
    void synchroPortLevel();

    PortLevel getPortLevel(PortLevel portLevelParam);

    PortLevel getLastPortLevel(String portfolioId);

    void portLevelInit();

    PortLevel getFirstPortLevel(String portfolioId);

}
