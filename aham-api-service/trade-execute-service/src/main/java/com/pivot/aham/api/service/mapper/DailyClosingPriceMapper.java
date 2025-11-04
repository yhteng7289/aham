package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.DailyClosingPricePO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by hao.tong on 2018/12/24.
 */
public interface DailyClosingPriceMapper extends BaseMapper {

    void save(DailyClosingPricePO dailyClosingPricePO);
    void batchSave(@Param("dailyClosingPricePOList") List<DailyClosingPricePO> dailyClosingPricePOList);
    List<DailyClosingPricePO> getPrice(@Param("etfCodeList") List<String> etfCodeList, @Param("bsnDt") Date bsnDt);
    List<DailyClosingPricePO> getByDt(@Param("bsnDt") Date bsnDt);
    DailyClosingPricePO getLastPrice(@Param("etfCode") String etfCode);
    void clearByDt(@Param("bsnDt")String bsnDt);
}
