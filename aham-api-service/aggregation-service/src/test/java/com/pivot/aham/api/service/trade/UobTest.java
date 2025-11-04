package com.pivot.aham.api.service.trade;

import com.pivot.aham.api.server.dto.req.UobTransferReq;
import com.pivot.aham.api.service.UobRemoteSupportService;
import com.pivot.aham.api.service.UobTradingService;
import com.pivot.aham.common.enums.CurrencyEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * Created by hao.tong on 2018/12/21.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UobTest {

    @Resource
    private UobTradingService uobTradingService;

    @Resource
    private UobRemoteSupportService uobRemoteSupportService;

    @Test
    public void test() {
        uobTradingService.saveRechargeLog();
        //virtualAccountOfflineTransferJob.virtualAccountOfflineTransfer();
        //uobTradingService.executeExchangeOrder();
        //uobTradingService.confirmExchangeOrderFromFtp();
        //uobTradingService.notifyExchangeBusinessOrder();

        //uobTradingService.createTransferExecutionOrderToSaxo();
        //uobTradingService.executeTransferOrder();
//        uobTradingService.confirmTransferBusinessOrder();
        //uobTradingService.confirmTransferBusinessOrder();
//        uobTradingService.notifyTransferBusinessOrder();
    }

    private void transferToSaxo(BigDecimal amount){
        UobTransferReq req = new UobTransferReq();
        req.setAmount(amount);
        req.setCurrency(CurrencyEnum.SGD);
        req.setOutBusinessId(111222333L);
        uobRemoteSupportService.transferToSaxo(req);
    }

    private void withdrawToBankCard(BigDecimal amount){
        UobTransferReq req = new UobTransferReq();
        req.setAmount(amount);
        req.setCurrency(CurrencyEnum.SGD);
        req.setOutBusinessId(111222333L);
        req.setBankName("11223344");
        req.setBankAccountNumber("test");
        req.setBankUserName("test");
        req.setBranchCode("test");
        req.setSwiftCode("test");
        uobRemoteSupportService.withdrawToBankCard(req);
    }
}
