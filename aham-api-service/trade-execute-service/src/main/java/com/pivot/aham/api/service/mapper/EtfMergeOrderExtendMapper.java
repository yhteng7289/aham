package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.EtfMergeOrderExtendPO;
import com.pivot.aham.common.core.base.BaseMapper;
import com.pivot.aham.common.enums.EtfOrderExtendStatusEnum;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @program: aham
 * @description:
 * @author: zhang7
 * @create: 2019-07-01 22:45
 **/
public interface EtfMergeOrderExtendMapper extends BaseMapper {
    void save(EtfMergeOrderExtendPO etfMergeOrderExtendPO);

    List<EtfMergeOrderExtendPO> getByEtfMergeOrderId(@Param("mergeOrderId") Long mergeOrderId);

    List<EtfMergeOrderExtendPO> listByEtfMergeOrderIds(@Param("mergeOrderIds") List<Long> mergeOrderIds);

    EtfMergeOrderExtendPO getById(@Param("id") Long id);

    void updateForConfirm(EtfMergeOrderExtendPO etfMergeOrderExtendPO);

    List<EtfMergeOrderExtendPO> listByOrderStatus(@Param("orderExtendStatus") EtfOrderExtendStatusEnum orderExtendStatus);

    void updateMergeId(@Param("orderIdList") List<Long> orderIdList,
                       @Param("mergeOrderId") Long mergeOrderId,
                       @Param("orderExtendStatus") EtfOrderExtendStatusEnum orderExtendStatus,
                       @Param("updateTime") Date updateTime);

    void updateExtendStatus(@Param("id") Long id, @Param("orderExtendStatus") EtfOrderExtendStatusEnum orderExtendStatus
            , @Param("updateTime") Date updateTime);


}

