package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.server.dto.DividendCallBackDTO;
import com.pivot.aham.api.service.mapper.model.AccountDividendPO;
import com.pivot.aham.api.service.mapper.model.AccountEtfSharesPO;
import com.pivot.aham.api.service.service.AccountDividendService;
import com.pivot.aham.common.enums.analysis.DividendHandelStatusEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DividendServiceImplTest {
    @Resource
    private DividendServiceImpl dividendService;
    @Resource
    private AccountDividendService accountDividendService;

    @Test
    public void handleUserDividend() {

        //{"caEventTypeEnum":"CASH","dividendOrderId":"8312733","exDate":"2019-06-03 00:00:00","netAmountAccountCurrency":20.83,"productCode":"VWOB","tradeDate":"2019-06-06 00:00:00","valueDate":"2019-06-06 00:00:00"}

        AccountDividendPO accountDividendParam = new AccountDividendPO();
        accountDividendParam.setHandelStatus(DividendHandelStatusEnum.DEFAULT);
        List<AccountDividendPO> accountDividendPOList = accountDividendService.listAccountDividend(accountDividendParam);

        for(AccountDividendPO accountDividend:accountDividendPOList){
            AccountEtfSharesPO accountEtfSharesPO = new AccountEtfSharesPO();
            accountEtfSharesPO.setAccountId(accountDividend.getAccountId());
            accountEtfSharesPO.setProductCode(accountDividend.getProductCode());

            DividendCallBackDTO dividendCallBackDTO = new DividendCallBackDTO();
            dividendCallBackDTO.setProductCode(accountDividend.getProductCode());
            dividendCallBackDTO.setTradeDate(accountDividend.getTradeDate());
            dividendCallBackDTO.setExDate(accountDividend.getExDate());
            AccountDividendPO accountDividendPO = new AccountDividendPO();
            accountDividendPO.setDividendAmount(accountDividend.getDividendAmount());
            dividendService.handleUserDividend(accountEtfSharesPO,dividendCallBackDTO,accountDividendPO);
        }

    }
}
