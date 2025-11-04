package com.pivot.aham.api.service.core;

import com.pivot.aham.api.server.dto.DividendCallBackDTO;
import com.pivot.aham.api.service.mapper.model.AccountAssetPO;
import com.pivot.aham.api.service.mapper.model.AccountDividendPO;
import com.pivot.aham.api.service.mapper.model.AccountEtfSharesPO;
import com.pivot.aham.api.service.mapper.model.UserDividendPO;
import com.pivot.aham.api.service.service.AccountDividendService;
import com.pivot.aham.api.service.service.DividendSupportService;
import com.pivot.aham.api.service.service.impl.DividendServiceImpl;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.enums.analysis.DividendHandelStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/fix/")
@Slf4j
public class FixDividendController extends AbstractController {
    @Resource
    private AccountDividendService accountDividendService;
    @Resource
    private DividendServiceImpl dividendService;
    @Resource
    private DividendSupportService dividendSupportService;

    @RequestMapping(value = "/fixDividend")
    @ResponseBody
    public Message fixDividend(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
            dividendCallBackDTO.setDividendOrderId(accountDividend.getDividendOrderId());
//            AccountDividendPO accountDividendPO = new AccountDividendPO();
//            accountDividendPO.setDividendAmount(accountDividend.getDividendAmount());
            //处理该 account下的用户分红
            List<UserDividendPO> userDividendPOS = dividendService.handleUserDividend(accountEtfSharesPO, dividendCallBackDTO, accountDividend);

            //分红添加进资产
            AccountAssetPO accountAssetPO = dividendService.handelAccountNavDividend(accountDividend);

            dividendSupportService.handelUserDividend(userDividendPOS, accountAssetPO, accountDividend, accountEtfSharesPO);
        }

        return Message.success();
    }



}
