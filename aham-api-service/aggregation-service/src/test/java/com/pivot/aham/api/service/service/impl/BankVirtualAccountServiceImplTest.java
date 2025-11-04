package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.model.BankVirtualAccountOrder;
import com.pivot.aham.api.service.service.BankVirtualAccountOrderService;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.MatchTypeEnum;
import com.pivot.aham.common.enums.analysis.NeedRefundTypeEnum;
import com.pivot.aham.common.enums.analysis.VAOrderActionTypeEnum;
import com.pivot.aham.common.enums.analysis.VAOrderTradeStatusEnum;
import com.pivot.aham.common.enums.analysis.VAOrderTradeTypeEnum;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BankVirtualAccountServiceImplTest {

    @Resource
    private BankVirtualAccountOrderService bankVirtualAccountService;

    @Test
    public void save() {
        List<BankVirtualAccountOrder> vAOrderList = Lists.newArrayList();

        BankVirtualAccountOrder order = new BankVirtualAccountOrder();
        order.setRedeemApplyId(0L)
                .setActionType(VAOrderActionTypeEnum.RECHARGE_EXCHANGE)
                .setReferenceCode("test")
                .setVirtualAccountNo("29389484s")
                .setCashAmount(new BigDecimal("10.09"))
                .setCurrency(CurrencyEnum.SGD)
                .setOperatorType(VAOrderTradeTypeEnum.COME_INTO)
                .setBankOrderNo("we34555")
                .setOrderStatus(VAOrderTradeStatusEnum.SUCCESS)
                .setNeedRefundType(NeedRefundTypeEnum.UN_REFUND)
                .setTradeTime(DateUtils.now())
                .setCreateTime(DateUtils.now())
                .setUpdateTime(DateUtils.now())
                .setId(Sequence.next());

        order.setMatchType(MatchTypeEnum.NAME_UNMATCH);


        vAOrderList.add(order);
        bankVirtualAccountService.saveVAOrders(vAOrderList);


    }
}