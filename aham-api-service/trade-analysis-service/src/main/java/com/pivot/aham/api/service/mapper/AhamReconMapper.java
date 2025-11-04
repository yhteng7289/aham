package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.AhamReconPO;
import com.pivot.aham.common.core.base.BaseMapper;

import java.util.List;

import org.springframework.stereotype.Repository;

/**
 * Created by luyang.li on 18/12/9.
 */
@Repository
public interface AhamReconMapper extends BaseMapper<AhamReconPO> {

    void save (AhamReconPO ahamRecon);

    List<AhamReconPO> findAhamRecon(AhamReconPO ahamRecon);
}
