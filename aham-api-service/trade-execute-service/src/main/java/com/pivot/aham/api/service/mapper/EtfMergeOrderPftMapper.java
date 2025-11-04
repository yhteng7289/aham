package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.EtfMergePftOrderPO;
import com.pivot.aham.common.core.base.BaseMapper;
import com.pivot.aham.common.enums.SyncStatus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EtfMergeOrderPftMapper extends BaseMapper {

    void save(EtfMergePftOrderPO etfMergePftOrderPO);

    List<EtfMergePftOrderPO> listByEtfMergeOrderId(@Param("mergeOrderId") Long mergeOrderId);

    void updateSyncStatus(@Param("syncStatus") SyncStatus syncStatus, @Param("mergeOrderId") Long mergeOrderId);

    List<EtfMergePftOrderPO> listBySyncStatus(@Param("syncStatus") SyncStatus syncStatus);

}
