package com.pivot.aham.api.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.mapper.model.SaxoOpenPositionStockPO;
import java.util.Date;
import java.util.List;


public interface SaxoOpenPositionStockService {
    
    Page<SaxoOpenPositionStockPO> saxoOpenPositionStock(SaxoOpenPositionStockPO saxoOpenPosStkPO, Page<SaxoOpenPositionStockPO> rowBounds, Date startCreateTime, Date endCreateTime);
    
    List<SaxoOpenPositionStockPO> querySaxoOpenPositionList(SaxoOpenPositionStockPO saxoOpenPositionStockPO);

}
