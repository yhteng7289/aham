package com.pivot.aham.api.service.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.mapper.model.TmpOrderRecordPO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TmpOrderRecordMapper extends BaseMapper<TmpOrderRecordPO> {

    void insertBatch(List<TmpOrderRecordPO> orderRecordPOs);

    TmpOrderRecordPO queryByTmpOrderId(@Param("tmpOrderId") Long tmpOrderId);

    List<TmpOrderRecordPO> queryByTotalTmpOrderId(@Param("totalTmpOrderId") Long totalTmpOrderId);

    List<TmpOrderRecordPO> listTmpCashOrderByTotalTmpOrderId(@Param("totalTmpOrderId") Long totalTmpOrderId,
                                                             @Param("productCodes") List<String> productCodes);

    void saveTmpOrder(TmpOrderRecordPO tmpOrderRecord);

    void updateTmpOrder(TmpOrderRecordPO tmpOrderRecord);

    List<TmpOrderRecordPO> listTmpOrderRecord(Page<TmpOrderRecordPO> rowBounds, TmpOrderRecordPO tmpOrderRecordPO);
    
    List<TmpOrderRecordPO> listTmpOrderRecord(TmpOrderRecordPO tmpOrderRecordPO);

}