package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.EtfInfoPO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by hao.tong on 2018/12/24.
 */
public interface EtfInfoMapper extends BaseMapper {

    List<String> getAllExchange();

    List<String> getExchangeByEtf(@Param("etfList") List<String> etfList);

    List<EtfInfoPO> getAllEtf();

    EtfInfoPO getByCode(@Param("etfCode") String etfCode);

    List<EtfInfoPO> getByCodes(@Param("etfCodes") List<String> etfCodes);

    EtfInfoPO getByUic(@Param("uic") Integer uic);
}
