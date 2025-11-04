package com.pivot.aham.api.service.job;

import com.pivot.aham.api.service.mapper.model.AccountStaticsPO;
import com.pivot.aham.api.service.mapper.model.ExchangeRatePO;
import com.pivot.aham.api.service.service.AccountStaticsService;
import com.pivot.aham.api.service.service.ExchangeRateService;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.ExchangeRateTypeEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountStaticSgdJobImplTest {

    @Resource
    private ExchangeRateService exchangeRateService;
    @Resource
    private AccountStaticsService accountStaticsService;

    @Test
    public void execute(){
        Date calDate = DateUtils.now();

        ExchangeRatePO exchangeRateParam = new ExchangeRatePO();
        exchangeRateParam.setRateDate(DateUtils.dayStart(calDate));
        exchangeRateParam.setExchangeRateType(ExchangeRateTypeEnum.SAXO_FXRT2);
        ExchangeRatePO exchangeRatePO = exchangeRateService.getExchangeRate(exchangeRateParam);
        AccountStaticsPO accountStaticsQuery = new AccountStaticsPO();
        accountStaticsQuery.setAccountId(1159386441548214273l);
        Date yesterDay = DateUtils.addDateByDay(calDate,-1);
        accountStaticsQuery.setStaticDate(yesterDay);
        AccountStaticsPO accountStaticsPO = accountStaticsService.selectByStaticDate(accountStaticsQuery);
    }
}
