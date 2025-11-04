package com.pivot.aham.api.service.mapper;


import com.pivot.aham.api.service.mapper.model.ExchangeRatePO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.springframework.stereotype.Repository;


/**
 * Created by luyang.li on 18/12/9.
 */
@Repository
public interface ExchangeRateMapper extends BaseMapper<ExchangeRatePO> {

    ExchangeRatePO queryExchangeRate(ExchangeRatePO exchangeRateParam);

    void saveExchangeRate(ExchangeRatePO exchangeRatePO);

    void updateExchangeRate(ExchangeRatePO exchangeRatePO);

    ExchangeRatePO queryLastExchangeRate(ExchangeRatePO exchangeRatePO);

}