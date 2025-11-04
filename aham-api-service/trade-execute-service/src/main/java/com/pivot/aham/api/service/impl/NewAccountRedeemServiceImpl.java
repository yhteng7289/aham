package com.pivot.aham.api.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.api.service.NewAccountRedeemService;
import lombok.extern.slf4j.Slf4j;
import com.pivot.aham.api.service.mapper.TAccountRedeemMapper;
import com.pivot.aham.api.service.mapper.model.TAccountRedeemPO;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by dexter on 17/4/2020
 */
@Service
@Slf4j
public class NewAccountRedeemServiceImpl implements NewAccountRedeemService {

    @Autowired
    private TAccountRedeemMapper tAccountRedeemMapper;
    
    @Override
    public List<TAccountRedeemPO> listAccountRedeemByCond(TAccountRedeemPO po) {
        return tAccountRedeemMapper.listAccountRedeemByCond(po);
    }
    
    
}
