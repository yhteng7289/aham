package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.DailyExchangeRatePO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by hao.tong on 2018/12/24.
 */
public interface DailyExchangeRateMapper extends BaseMapper {

    void save(DailyExchangeRatePO dailyExchangeRatePO);
    DailyExchangeRatePO getRate(@Param("bsnDt") String bsnDt);
    DailyExchangeRatePO getLastRate();
    void clearByDt(@Param("bsnDt") String bsnDt);
}
