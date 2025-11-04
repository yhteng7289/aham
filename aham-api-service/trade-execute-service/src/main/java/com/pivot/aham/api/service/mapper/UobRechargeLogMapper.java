package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.UobRechargeLogPO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by hao.tong on 2018/12/24.
 */
public interface UobRechargeLogMapper extends BaseMapper {

    void save(UobRechargeLogPO uobRechargeLogPO);

    List<UobRechargeLogPO> queryList(UobRechargeLogPO uobRechargeLogPO);

    void updateByBankOrderNo(UobRechargeLogPO uobRechargeLogPO);

    UobRechargeLogPO queryByBankOrderNo(@Param("bankOrderNo") String bankOrderNo);

    List<UobRechargeLogPO> queryProcessingByVirtualAccountNo(@Param("virtualAccountNo") String virtualAccountNo,
            @Param("referenceCode") String referenceCode);
}
