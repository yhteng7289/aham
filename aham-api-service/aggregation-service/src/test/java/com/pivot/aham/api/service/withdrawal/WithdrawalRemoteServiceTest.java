package com.pivot.aham.api.service.withdrawal;

import com.pivot.aham.api.server.dto.EtfCallbackDTO;
import com.pivot.aham.api.server.dto.WithdrawalFromGoalDTO;
import com.pivot.aham.api.server.dto.WithdrawalFromVirtalAccountDTO;
import com.pivot.aham.api.server.remoteservice.AssetServiceRemoteService;
import com.pivot.aham.api.server.remoteservice.WithdrawalRemoteService;
import com.pivot.aham.api.service.job.impl.WithdrawalSaxoToUobJobImpl;
import com.pivot.aham.api.service.job.impl.WithdrawalUobToBankTransferJobImpl;
import com.pivot.aham.api.service.job.TradeAnalysisStrategy;
import com.pivot.aham.api.service.mapper.model.AccountInfoPO;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.TransferStatusEnum;
import com.pivot.aham.common.enums.analysis.WithdrawalTargetBankTypeEnum;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月06日
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WithdrawalRemoteServiceTest {
    @Resource
    private WithdrawalRemoteService withdrawalRemoteService;
    @Autowired
    private TradeAnalysisStrategy tradeAnalysisStrategy;
    @Resource
    private AssetServiceRemoteService assetServiceRemoteService;
    @Resource
    private WithdrawalSaxoToUobJobImpl withdrawalSaxoToUobJob;
    @Resource
    private WithdrawalUobToBankTransferJobImpl withdrawalUobToBankTransferJob;


    @Test
    public void withdrawalFromVirtalAccount(){
        WithdrawalFromVirtalAccountDTO withdrawalFromVirtalAccount = new WithdrawalFromVirtalAccountDTO();
        withdrawalFromVirtalAccount.setApplyAmount(new BigDecimal("1000"));
        withdrawalFromVirtalAccount.setBankAccountNo("X09988777");
        //withdrawalFromVirtalAccount.setBankCode("6010");
        withdrawalFromVirtalAccount.setBankName("杀马特银行");
        withdrawalFromVirtalAccount.setClientId("c123456");
        withdrawalFromVirtalAccount.setSourceAccountType(CurrencyEnum.SGD);
        withdrawalFromVirtalAccount.setTargetCurrency(CurrencyEnum.USD);
        withdrawalFromVirtalAccount.setWithdrawalTargetBankType(WithdrawalTargetBankTypeEnum.OVERSEA);
        withdrawalFromVirtalAccount.setSwift("sw22223344");
        withdrawalFromVirtalAccount.setBranch("b28774933");

        withdrawalRemoteService.withdrawalFromVirtalAccount(withdrawalFromVirtalAccount);
    }

    @Test
    public void withdrawalFromGoal(){
        WithdrawalFromGoalDTO withdrawalFromGoal = new WithdrawalFromGoalDTO();
        withdrawalFromGoal.setClientId("c123456");
        withdrawalFromGoal.setGoalId("g123");
        withdrawalFromGoal.setApplyMoney(new BigDecimal("1000"));
        withdrawalFromGoal.setBankAccountNo("X09988777");
//        withdrawalFromGoal.setBankCode("6010");
        withdrawalFromGoal.setBankName("杀马特银行");
        withdrawalFromGoal.setClientId("XU878999");
        withdrawalFromGoal.setSourceAccountType(CurrencyEnum.SGD);
        withdrawalFromGoal.setTargetCurrency(CurrencyEnum.USD);
        withdrawalFromGoal.setWithdrawalTargetBankType(WithdrawalTargetBankTypeEnum.OVERSEA);
        withdrawalFromGoal.setSwift("sw22223344");
        withdrawalFromGoal.setBranch("b28774933");

        withdrawalRemoteService.withdrawalFromGoal(withdrawalFromGoal);
    }

    @Test
    public void onlyWithdrawaltradeAnalysis(){
        BigDecimal totalRecharge = new BigDecimal("0");
        BigDecimal totalRedeem = new BigDecimal("1000");
        AccountInfoPO accountInfo = new AccountInfoPO();
        accountInfo.setId(1L);
        accountInfo.setPortfolioId("P1R1A2");
        tradeAnalysisStrategy.onlyWithdrawaltradeAnalysis(totalRecharge,totalRedeem,accountInfo, null);
    }

    @Test
    public void handlerSellConfirm(){
        List<EtfCallbackDTO> params = Lists.newArrayList();
        EtfCallbackDTO buyEtfCallbackDTO = new EtfCallbackDTO();
        buyEtfCallbackDTO.setAccountId(1L);
        buyEtfCallbackDTO.setConfirmMoney(new BigDecimal("450"));
        buyEtfCallbackDTO.setConfirmShare(new BigDecimal("450"));
        buyEtfCallbackDTO.setConfirmTime(DateUtils.now());
        buyEtfCallbackDTO.setProductCode("SHV");
        buyEtfCallbackDTO.setTmpOrderId(1084840540949966849L);
        buyEtfCallbackDTO.setTransCost(new BigDecimal("10"));
        buyEtfCallbackDTO.setTransferStatus(TransferStatusEnum.SUCCESS);


        EtfCallbackDTO buyEtfCallbackDTO1= new EtfCallbackDTO();
        buyEtfCallbackDTO1.setAccountId(1L);
        buyEtfCallbackDTO1.setConfirmMoney(new BigDecimal("450"));
        buyEtfCallbackDTO1.setConfirmShare(new BigDecimal("450"));
        buyEtfCallbackDTO1.setConfirmTime(DateUtils.now());
        buyEtfCallbackDTO1.setProductCode("EMB");
        buyEtfCallbackDTO1.setTmpOrderId(1084840542984204289L);
        buyEtfCallbackDTO1.setTransCost(new BigDecimal("10"));
        buyEtfCallbackDTO1.setTransferStatus(TransferStatusEnum.SUCCESS);

        params.add(buyEtfCallbackDTO);
        params.add(buyEtfCallbackDTO1);
        assetServiceRemoteService.etfCallBack(params);

    }
    @Test
    public void withdrawalSaxoToUob(){
        String fileName = withdrawalSaxoToUobJob.withdrawalSaxoToUob();
    }

    @Test
    public void withdrawalUobToBankTransfer(){
        withdrawalUobToBankTransferJob.withdrawalUobToBankTransfer();
    }


}
