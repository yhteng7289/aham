package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.service.ExchangeRateService;
import com.pivot.aham.common.enums.ExchangeRateTypeEnum;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 2018/12/24.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ExchangeRateServiceImplTest {

    @Resource
    private ExchangeRateService exchangeRateService;

    @Test
    public void saveDailyExchangeRate() {
    }

    @Test
    public void getActualTimeRate() {
    }

    @Test
    public void handelExchangeRate() {

        List<BigDecimal> exchangeRateList = Lists.newArrayList();
        exchangeRateList.add(new BigDecimal("1.5"));
        exchangeRateList.add(new BigDecimal("2"));

        exchangeRateService.handelExchangeRate(exchangeRateList, new BigDecimal("1.35"), ExchangeRateTypeEnum.SAXO_FXRT1);
    }

    @Test
    public void getExchangeRate() {
    }

    @Test
    public void queryLastExchangeRate() {
    }
}