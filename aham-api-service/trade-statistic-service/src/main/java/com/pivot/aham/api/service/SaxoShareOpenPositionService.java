package com.pivot.aham.api.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.mapper.model.SaxoShareOpenPositionPO;
import java.util.Date;
import java.util.List;


public interface SaxoShareOpenPositionService {
    
    
    Page<SaxoShareOpenPositionPO> saxoShareopenPositionService(SaxoShareOpenPositionPO saxoShareOpenPositionPO, Page<SaxoShareOpenPositionPO> rowBounds, Date startCreateTime, Date endCreateTime);
    
    List<SaxoShareOpenPositionPO> querySaxoShareOpenPositionList(SaxoShareOpenPositionPO saxoShareOpenPositionPO);

}
