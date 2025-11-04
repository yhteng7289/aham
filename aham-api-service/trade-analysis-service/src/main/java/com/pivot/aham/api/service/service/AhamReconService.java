package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.AccountAssetPO;
import com.pivot.aham.api.service.mapper.model.AccountRechargePO;
import com.pivot.aham.api.service.mapper.model.AhamReconPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by luyang.li on 18/12/9.
 */
public interface AhamReconService extends BaseService<AhamReconPO> {

    void add(AhamReconPO ahamRecon);
    
    List<AhamReconPO> findAhamRecon(AhamReconPO ahamRecon);

}
