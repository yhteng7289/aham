/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.SaxoShareOpenPositionService;
import com.pivot.aham.api.service.mapper.SaxoShareOpenPositionMapper;
import com.pivot.aham.api.service.mapper.model.SaxoShareOpenPositionPO;
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
public class SaxoShareOpenPositionServiceImpl extends BaseServiceImpl<SaxoShareOpenPositionPO, SaxoShareOpenPositionMapper> implements SaxoShareOpenPositionService {
    
    @Override
    public Page<SaxoShareOpenPositionPO> saxoShareopenPositionService(SaxoShareOpenPositionPO saxoShareOpenPositionPO, Page<SaxoShareOpenPositionPO> rowBounds, Date startCreateTime, Date endCreateTime){
        
        Page<SaxoShareOpenPositionPO> saxoShareOpenPositionPagePO = querySaxoReconciliation(saxoShareOpenPositionPO, rowBounds, startCreateTime, endCreateTime );
        
        return saxoShareOpenPositionPagePO;
    }
    
    @Override
    public List<SaxoShareOpenPositionPO> querySaxoShareOpenPositionList(SaxoShareOpenPositionPO saxoShareOpenPositionPO){
        
        return mapper.querySaxoShareOpenPositionList(saxoShareOpenPositionPO);
    }
}
