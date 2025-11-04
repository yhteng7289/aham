package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.DailyTwapPricePO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * Created by hao.tong on 2018/12/24.
 */
public interface DailyTwapPriceMapper extends BaseMapper {

    void save(DailyTwapPricePO dailyTwapPricePO);
    DailyTwapPricePO getPrice(@Param("productCode") String etfCode, @Param("bsnDt") String bsnDt);
    void updatePrice(@Param("id") Long id, @Param("aveAsk") BigDecimal aveAsk, @Param("avgBid") BigDecimal avgBid, @Param("avgCount") Integer avgCount);
}
