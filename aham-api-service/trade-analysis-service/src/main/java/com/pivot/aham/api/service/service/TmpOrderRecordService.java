package com.pivot.aham.api.service.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.res.TmpOrderRecordResDTO;
import com.pivot.aham.api.service.mapper.model.TmpOrderRecordPO;
import com.pivot.aham.common.core.base.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TmpOrderRecordService extends BaseService<TmpOrderRecordPO> {
    void insertBatch(List<TmpOrderRecordPO> orderRecordPOs);

    TmpOrderRecordPO queryByTmpOrderId(Long tmpOrderId);

    List<TmpOrderRecordPO> listByTotalTmpOrderId(Long totalTmpOrderId);

    List<TmpOrderRecordPO> listTmpCashOrderByTotalTmpOrderId(Long totalTmpOrder, List<String> productCodes);

    void saveTmpOrder(TmpOrderRecordPO tmpOrderRecord);

    void updateTmpOrder(TmpOrderRecordPO tmpOrderRecord);

    Page<TmpOrderRecordPO> listTmpOrderRecord(Page<TmpOrderRecordPO> rowBounds, TmpOrderRecordPO tmpOrderRecordPO);
    
    List<TmpOrderRecordPO> listTmpOrderRecord(TmpOrderRecordPO tmpOrderRecordPO);
}
