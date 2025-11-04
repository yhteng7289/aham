package com.pivot.aham.api.service.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.mapper.TransOrderMapper;
import com.pivot.aham.api.service.mapper.model.TransOrderPO;
import com.pivot.aham.api.service.service.TransOrderService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransOrderServiceImpl extends BaseServiceImpl<TransOrderPO, TransOrderMapper> implements TransOrderService {

    @Override
    public Page<TransOrderPO> queryTransOrder(Page<TransOrderPO> rowBounds, TransOrderPO transOrderPO) {
        List<TransOrderPO> ts = mapper.queryTransOrder(rowBounds, transOrderPO);
        rowBounds.setRecords(ts);
        return rowBounds;

    }

    @Override
    public List<TransOrderPO> queryTransOrderList(TransOrderPO transOrderPO) {
        return mapper.queryTransOrderList(transOrderPO);
    }
}
