package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.ExchangeRatePO;
import com.pivot.aham.common.enums.ExchangeRateTypeEnum;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 19/3/31.
 */
public interface ExchangeRateService {

    void saveDailyExchangeRate(ExchangeRatePO exchangeRatePO);

    BigDecimal getActualTimeRate();

    void handelExchangeRate(List<BigDecimal> exchangeRateList, BigDecimal actualTimeRate, ExchangeRateTypeEnum saxoFxrt1);

    ExchangeRatePO getExchangeRate(ExchangeRatePO exchangeRateParam);

    ExchangeRatePO queryLastExchangeRate(ExchangeRatePO exchangeRateParam);

    void updateDailyExchangeRate(ExchangeRatePO exchangeRatePO);


}
