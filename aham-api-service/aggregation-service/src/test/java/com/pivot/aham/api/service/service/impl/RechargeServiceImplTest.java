package com.pivot.aham.api.service.service.impl;

import com.beust.jcommander.internal.Lists;
import com.pivot.aham.api.service.bean.RechargeRefundBean;
import com.pivot.aham.api.service.service.UobRechargeService;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.CurrencyEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 19/3/24.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RechargeServiceImplTest {

    @Resource
    private UobRechargeService uobRechargeService;

    @Test
    public void handelOfflineTransferVirtualAccount() throws Exception {

    }

    @Test
    public void getBVAOrder() throws Exception {

    }

    @Test
    public void notifyTransferErrorUser() throws Exception {

    }

    @Test
    public void handelUobExchangeCallBack() throws Exception {

    }

    @Test
    public void handelGoalSetMoney() throws Exception {

    }

    @Test
    public void getUserRechargeMoney() throws Exception {

    }

    @Test
    public void notifyRechargeRefund() throws Exception {
        RechargeRefundBean rechargeRefund = new RechargeRefundBean();
        rechargeRefund.setAmount(new BigDecimal("200"));
        rechargeRefund.setBankOrderNumber("122");
        rechargeRefund.setBankProvidedName("abc");
        rechargeRefund.setClientId("123");
        rechargeRefund.setClientName("123");
        rechargeRefund.setCurrency(CurrencyEnum.SGD.getCode());
        rechargeRefund.setTradeTime(DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT4));
        rechargeRefund.setVirtualAccountNo("12345");

        RechargeRefundBean rechargeRefund2 = new RechargeRefundBean();
        rechargeRefund2.setAmount(new BigDecimal("200"));
        rechargeRefund2.setBankOrderNumber("122");
        rechargeRefund2.setBankProvidedName("abc");
        rechargeRefund2.setClientId("123");
        rechargeRefund2.setClientName("123");
        rechargeRefund2.setCurrency(CurrencyEnum.SGD.getCode());
        rechargeRefund2.setTradeTime(DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT4));
        rechargeRefund2.setVirtualAccountNo("12345");

        List<RechargeRefundBean> rechargeRefunds = Lists.newArrayList();
        rechargeRefunds.add(rechargeRefund);
        rechargeRefunds.add(rechargeRefund2);

        uobRechargeService.notifyRechargeRefund(rechargeRefunds);
    }

}