package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.PortFutureLevelPO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by luyang.li on 18/12/6.
 */
@Repository
public interface PortFutureLevelMapper extends BaseMapper<PortFutureLevelPO> {


    List<PortFutureLevelPO> listPortFutureLevel(PortFutureLevelPO queryParam);

    void insertBatch(List<PortFutureLevelPO> newPortFutureLevels);

    void updateStatusByIds(@Param("portFutureLevelIds") List<Long> portFutureLevelIds);

}
