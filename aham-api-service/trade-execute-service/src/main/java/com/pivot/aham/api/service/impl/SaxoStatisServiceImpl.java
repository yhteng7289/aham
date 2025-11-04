package com.pivot.aham.api.service.impl;

import com.google.common.collect.Maps;
import com.pivot.aham.api.server.dto.resp.SaxoStatisShareTradesDTO;
import com.pivot.aham.api.service.SaxoStatisService;
import com.pivot.aham.api.service.mapper.SaxoOrderMapper;
import com.pivot.aham.api.service.mapper.model.SaxoOrderPO;
import com.pivot.aham.common.core.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("saxoStatisService")
@Slf4j
public class SaxoStatisServiceImpl implements SaxoStatisService {
    @Autowired
    private SaxoOrderMapper saxoOrderMapper;
    @Override
    public  Map<Long,SaxoStatisShareTradesDTO> statisShareTreadesExecute(Date nowDate) {
        List<SaxoOrderPO> saxoOrderPOS=saxoOrderMapper.getListByTime(nowDate);
        if(DataUtil.isEmpty(saxoOrderPOS)){
            return null;
        }
        Map<Long,SaxoStatisShareTradesDTO>datas= Maps.newHashMap();
        saxoOrderPOS.stream().forEach(in->{
            SaxoStatisShareTradesDTO saxoStatisShareTradesDTO=new SaxoStatisShareTradesDTO();
            saxoStatisShareTradesDTO.setOrderNumber(Long.valueOf(in.getPositionId()));
            saxoStatisShareTradesDTO.setCommission(in.getCommission());
            saxoStatisShareTradesDTO.setOrderType(in.getOrderType());
            saxoStatisShareTradesDTO.setTradeShares(new BigDecimal(in.getConfirmShare()));
            datas.put(saxoStatisShareTradesDTO.getOrderNumber(),saxoStatisShareTradesDTO);
        });
        return datas;
    }
}
