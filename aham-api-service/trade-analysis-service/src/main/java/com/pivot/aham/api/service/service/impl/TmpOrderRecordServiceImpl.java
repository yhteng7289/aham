package com.pivot.aham.api.service.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.res.TmpOrderRecordResDTO;
import com.pivot.aham.api.service.mapper.TmpOrderRecordMapper;
import com.pivot.aham.api.service.mapper.model.TmpOrderRecordPO;
import com.pivot.aham.api.service.service.TmpOrderRecordService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TmpOrderRecordServiceImpl extends BaseServiceImpl<TmpOrderRecordPO, TmpOrderRecordMapper> implements TmpOrderRecordService {

    @Override
    public void insertBatch(List<TmpOrderRecordPO> orderRecordPOs) {
        mapper.insertBatch(orderRecordPOs);
    }

    @Override
    public TmpOrderRecordPO queryByTmpOrderId(Long tmpOrderId) {
        return mapper.queryByTmpOrderId(tmpOrderId);
    }

    @Override
    public List<TmpOrderRecordPO> listByTotalTmpOrderId(Long totalTmpOrderId) {
        return mapper.queryByTotalTmpOrderId(totalTmpOrderId);
    }

    @Override
    public List<TmpOrderRecordPO> listTmpCashOrderByTotalTmpOrderId(Long totalTmpOrder, List<String> productCodes) {
        return mapper.listTmpCashOrderByTotalTmpOrderId(totalTmpOrder, productCodes);
    }

    @Override
    public void saveTmpOrder(TmpOrderRecordPO tmpOrderRecord) {
        mapper.saveTmpOrder(tmpOrderRecord);
    }

    @Override
    public void updateTmpOrder(TmpOrderRecordPO tmpOrderRecord) {
        mapper.updateTmpOrder(tmpOrderRecord);
    }

    @Override
    public Page<TmpOrderRecordPO> listTmpOrderRecord(Page<TmpOrderRecordPO> rowBounds, TmpOrderRecordPO tmpOrderRecordPO) {
        List<TmpOrderRecordPO> tmpOrderRecordList = mapper.listTmpOrderRecord(rowBounds,tmpOrderRecordPO);
        rowBounds.setRecords(tmpOrderRecordList);
        return rowBounds;
    }
    
    @Override
    public List<TmpOrderRecordPO> listTmpOrderRecord(TmpOrderRecordPO tmpOrderRecordPO) {
        return mapper.listTmpOrderRecord(tmpOrderRecordPO);
    }
}
