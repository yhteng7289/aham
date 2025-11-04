package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.api.server.dto.BankVirtualAccountOrderResDTO;
import com.pivot.aham.api.server.dto.UobExchangeCallbackDTO;
import com.pivot.aham.api.server.remoteservice.ExchangeRemoteService;
import com.pivot.aham.api.server.remoteservice.SaxoTradeRemoteService;
import com.pivot.aham.api.server.remoteservice.TestRemoteService;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.server.dto.req.ExchangeRateReq;
import com.pivot.aham.api.server.dto.resp.ExchangeRateResult;
import com.pivot.aham.api.service.job.custstatment.CustomerStatementJob;
import com.pivot.aham.api.service.job.custstatment.impl.FixHisUserStatementService;
import com.pivot.aham.api.service.job.impl.*;
import com.pivot.aham.api.service.job.StaticAccountEtfJob;
import com.pivot.aham.api.service.service.FixDataService;
import com.pivot.aham.api.service.service.RechargeService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.CurrencyEnum;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 用户各种测试，和任务重跑
 *
 * @author addison
 * @since 2018年12月10日
 */
/*@Service(interfaceClass = TestRemoteService.class)
@Slf4j
public class TestRemoteServiceImpl implements TestRemoteService {
    @Resource
    private WithdrawalSaxoToUobJobImpl withdrawalSaxoToUobJob;
    @Resource
    private WithdrawalUobToBankTransferJobImpl withdrawalUobToBankTransferJob;
    @Resource
    private ExchangeRemoteService exchangeRemoteService;
    @Resource
    private UserServiceRemoteService userServiceRemoteService;
    @Resource
    private SaxoTradeRemoteService saxoTradeRemoteService;
    @Resource
    private StaticAccountEtfJob staticAccountEtfJob;
    @Resource
    private StaticUEtfJobImpl staticUserEtfJob;
    @Resource
    private FixDataService fixDataService;
    @Resource
    private AccountStaticSgdJobImpl accountStaticStatusJob;
    @Resource
    private RechargeService rechargeService;
    @Resource
    private UserProfitJobImpl userProfitJob;
    @Resource
    private CustomerStatementJob customerStatementJob;
    @Resource
    private FixHisUserStatementService fixHisUserStatementService;

    @Override
    public RpcMessage withdrawalSaxoToUob() {
        String fileName = withdrawalSaxoToUobJob.withdrawalSaxoToUob();
        return RpcMessage.success(fileName);
    }

    @Override
    public RpcMessage withdrawalUobToBankTransfer() {
        withdrawalUobToBankTransferJob.withdrawalUobToBankTransfer();
        return RpcMessage.success();
    }

    @Override
    public void uboExchangeCallback(Long vaOrderId) {
        BankVirtualAccountOrderResDTO resDTO = userServiceRemoteService.queryById(vaOrderId);
        UobExchangeCallbackDTO dto = new UobExchangeCallbackDTO();
        dto.setOrderNo(vaOrderId);
        BigDecimal usdMoney = BigDecimal.ZERO;
        ExchangeRateReq exchangeRateReq = new ExchangeRateReq();
        exchangeRateReq.setDate(DateUtils.now());
        RpcMessage<ExchangeRateResult> exchangeRateResult = saxoTradeRemoteService.queryExchangeRate(exchangeRateReq);
        if (RpcMessageStandardCode.OK.value() == exchangeRateResult.getResultCode()) {
            if (CurrencyEnum.USD == resDTO.getCurrency()) {
                usdMoney = resDTO.getCashAmount().multiply(exchangeRateResult.getContent().getUSD_TO_SGD()).setScale(6, BigDecimal.ROUND_HALF_UP);
            } else {
                usdMoney = resDTO.getCashAmount().divide(exchangeRateResult.getContent().getUSD_TO_SGD(), 6, BigDecimal.ROUND_HALF_UP);
            }
        }
        dto.setConfirmMoney(usdMoney);

        exchangeRemoteService.uobExchangeCallBack(dto);
    }

    @Override
    public void staticAccountEtfJob(Date date) {
//        System.out.println("被调用了");
        staticAccountEtfJob.staticAccountEtfJob(date);
    }

    @Override
    public void staticUserEtfJob(Date date) {
//        System.out.println("被调用了");
        staticUserEtfJob.staticUserEtfJob(date);
    }


    @Override
    public void cleanRegister(Integer clientId) {
    }

    @Override
    public void deleteFromTable(String tableName, Long id) {
        fixDataService.deleteFromTable(tableName, id);
    }

    @Override
    public void updateClientName(String clientName, Long id) {
        fixDataService.updateClientName(clientName, id);
    }

    @Override
    public void updateVACash(BigDecimal cashAmount, BigDecimal freezeAmount, BigDecimal usedAmount, Long id) {
        fixDataService.updateVACash(cashAmount, freezeAmount, usedAmount, id);
    }

    @Override
    public void updateSaxoStatu(Integer status, Long id) {
        fixDataService.updateSaxoStatu(status, id);
    }

    @Override
    public void updateBankVAStatu(Integer status, Long id) {
        fixDataService.updateBankVAStatu(status, id);
    }

    @Override
    public void updateAccountStatics(Long accountId,Date date) {
        accountStaticStatusJob.updateSgd(accountId,date);
    }

    @Override
    public void uobTransferToSaxoJob() {
        log.info("=======分析UOB的入金下指令转入SAXO开始");
        rechargeService.handelUobTransferToSaxo();
        log.info("=======分析UOB的入金下指令转入SAXO结束");
    }

    @Override
    public void userProfit(Date date) {
        userProfitJob.calculaterUserProfit(date);
    }

    @Override
    public void calCustStatement(String clientId,Integer monthOffset) {
        customerStatementJob.calculateCustomerStatement(clientId,monthOffset);
    }

    @Override
    public void fixCustStatement() {
        fixHisUserStatementService.fixUserGoalCashFlow();
        fixHisUserStatementService.fixEtfSharesStatics();
        fixHisUserStatementService.fixUserStatics();
    }

}
*/