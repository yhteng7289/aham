package com.pivot.aham.api.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.api.service.NewAccountRechargeService;
import lombok.extern.slf4j.Slf4j;
import com.pivot.aham.api.service.mapper.TAccountRechargeMapper;
import com.pivot.aham.api.service.mapper.model.TAccountRechargePO;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by dexter on 17/4/2020
 */
@Service
@Slf4j
public class NewAccountRechargeServiceImpl implements NewAccountRechargeService {

    @Autowired
    private TAccountRechargeMapper tAccountRechargeMapper;
    
    @Override
    public List<TAccountRechargePO> listByAccountId(TAccountRechargePO po) {
        return tAccountRechargeMapper.listByDate(po);
    }
    
    
}
