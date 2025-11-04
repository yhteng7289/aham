package com.pivot.aham.api.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.mapper.model.SaxoShareTradePO;
import java.util.Date;
import java.util.List;


public interface SaxoShareTradeService {
    
    Page<SaxoShareTradePO> saxoShareTrade(SaxoShareTradePO saxoShareTradePO, Page<SaxoShareTradePO> rowBounds, Date startCreateTime, Date endCreateTime);

    List<SaxoShareTradePO> querySaxoShareTradeList(SaxoShareTradePO saxoShareTradePO);
}
