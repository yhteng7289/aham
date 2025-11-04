/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.SaxoShareTradeService;
import com.pivot.aham.api.service.mapper.SaxoShareTradeMapper;
import com.pivot.aham.api.service.mapper.model.SaxoShareTradePO;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 * @author ASUS
 */
@Service
@Slf4j
public class SaxoShareTradeServiceImpl extends BaseServiceImpl<SaxoShareTradePO, SaxoShareTradeMapper> implements SaxoShareTradeService {
    
    @Override
    public Page<SaxoShareTradePO> saxoShareTrade(SaxoShareTradePO saxoShareTradePO, Page<SaxoShareTradePO> rowBounds, Date startCreateTime, Date endCreateTime){
        
        Page<SaxoShareTradePO> saxoShareTradePagePO = querySaxoReconciliation(saxoShareTradePO, rowBounds, startCreateTime, endCreateTime );
        
        return saxoShareTradePagePO;
    }
    
    @Override
    public List<SaxoShareTradePO> querySaxoShareTradeList(SaxoShareTradePO saxoShareTradePO){
        
        return mapper.querySaxoShareTradeList(saxoShareTradePO);
    }
}
