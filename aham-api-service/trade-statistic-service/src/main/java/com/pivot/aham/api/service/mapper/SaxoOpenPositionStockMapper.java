/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.service.mapper;


import com.pivot.aham.api.service.mapper.model.SaxoOpenPositionStockPO;
import com.pivot.aham.common.core.base.BaseMapper;
import java.util.List;


/**
 *
 * @author ASUS
 */
public interface SaxoOpenPositionStockMapper extends BaseMapper <SaxoOpenPositionStockPO>{
    
    List<SaxoOpenPositionStockPO> querySaxoOpenPositionList(SaxoOpenPositionStockPO saxoOpenPositionStockPO);
    
}
