/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.SaxoOpenPositionStockService;
import com.pivot.aham.api.service.mapper.SaxoOpenPositionStockMapper;
import com.pivot.aham.api.service.mapper.model.SaxoOpenPositionStockPO;
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
public class SaxoOpenPositionStockServiceImpl extends BaseServiceImpl<SaxoOpenPositionStockPO, SaxoOpenPositionStockMapper> implements SaxoOpenPositionStockService {
    
    @Override
    public Page<SaxoOpenPositionStockPO> saxoOpenPositionStock(SaxoOpenPositionStockPO saxoOpenPosStkPO, Page<SaxoOpenPositionStockPO> rowBounds, Date startCreateTime, Date endCreateTime){
        
        Page<SaxoOpenPositionStockPO> saxoBalCashPagePO = querySaxoReconciliation(saxoOpenPosStkPO, rowBounds, startCreateTime, endCreateTime );
        
        return saxoBalCashPagePO;
    }
    
    @Override
    public List<SaxoOpenPositionStockPO> querySaxoOpenPositionList(SaxoOpenPositionStockPO saxoOpenPositionStockPO){
        
        return mapper.querySaxoOpenPositionList(saxoOpenPositionStockPO);
    }
}
