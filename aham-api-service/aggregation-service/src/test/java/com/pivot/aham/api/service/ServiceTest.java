package com.pivot.aham.api.service;

import com.google.common.eventbus.EventBus;
import com.pivot.aham.api.service.job.interevent.StaticRateForAccountEvent;
import com.pivot.aham.common.enums.analysis.FxRateTypeEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * Created by hao.tong on 2018/12/21.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceTest {

    @Resource
    private EventBus eventBus;


    @Test
    @Transactional
    public void Test() throws Exception {

        StaticRateForAccountEvent staticRateForAccountEvent = new StaticRateForAccountEvent();
        staticRateForAccountEvent.setAccountId(1116313982587187201L);
        //计算费率
        BigDecimal fxRate = new BigDecimal(0.0023);
        staticRateForAccountEvent.setFxRate(fxRate);
        staticRateForAccountEvent.setFxRateTypeEnum(FxRateTypeEnum.FUNDOUT);
        eventBus.post(staticRateForAccountEvent);
    }


}
