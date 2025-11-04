package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.PortLevel;
import com.pivot.aham.common.core.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by luyang.li on 18/12/6.
 */
@Repository
public interface PortLevelMapper extends BaseMapper<PortLevel> {


    void insertBatch(List<PortLevel> portLevels);

    List<PortLevel> getPortLevels(PortLevel portLevel);

    PortLevel getPortLevel(PortLevel queryParam);

    void updatePortLevel(PortLevel portLevel);

    PortLevel getLastPortLevel(@Param("portfolioId") String portfolioId);

    PortLevel getFirstPortLevel(@Param("portfolioId") String portfolioId);

}
