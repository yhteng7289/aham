package com.pivot.aham.api.web.web.controller;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.server.dto.WithdrawalFromGoalDTO;
import com.pivot.aham.api.server.dto.WithdrawalFromVirtalAccountDTO;
import com.pivot.aham.api.server.dto.req.ExchangeRateDTO;
import com.pivot.aham.api.server.dto.res.ExchangeRateResDTO;
import com.pivot.aham.api.server.dto.res.RedeemApplyResDTO;
import com.pivot.aham.api.server.remoteservice.ExchangeRemoteService;
import com.pivot.aham.api.server.remoteservice.WithdrawalRemoteService;
import com.pivot.aham.api.web.web.vo.req.WithdrawalFromGoalReqVo;
import com.pivot.aham.api.web.web.vo.req.WithdrawalFromVirtalAccountReqVo;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.ExchangeRateTypeEnum;
import com.pivot.aham.common.enums.analysis.WithdrawalTargetTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月10日
 */
@RestController
@RequestMapping("/app/")
@Api(value = "提现接口", description = "APP-提现接口")
@Slf4j
public class WebWithdrawalController extends AbstractController {

    @Resource
    private WithdrawalRemoteService withdrawalRemoteService;
    @Resource
    private ExchangeRemoteService exchangeRemoteService;

//    private static final Date EXCHANGE_RATE_TIME = DateUtils.getDate(DateUtils.now(), 12, 45, 0);
    @PostMapping("withdrawal.fromVirtualAccount")
    @ApiOperation(value = "虚拟账户提现", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message<String> withdrawalFromVirtualAccount(@RequestBody @Valid WithdrawalFromVirtalAccountReqVo withdrawalFromVirtalAccountReqVo) {
        log.info("用户从虚拟账户上提现:{}", JSON.toJSON(withdrawalFromVirtalAccountReqVo));
        if (withdrawalFromVirtalAccountReqVo.getApplyMoney().compareTo(BigDecimal.ZERO) <= 0) {
            log.info("非法的提现金额");
            Message.error("Amount needs to be more than 0");
        }

        WithdrawalFromVirtalAccountDTO withdrawalFromVirtalAccount = new WithdrawalFromVirtalAccountDTO();
        withdrawalFromVirtalAccount.setBankAccountNo(withdrawalFromVirtalAccountReqVo.getBankAccountNo());
        withdrawalFromVirtalAccount.setClientId(withdrawalFromVirtalAccountReqVo.getClientId());
        withdrawalFromVirtalAccount.setApplyAmount(withdrawalFromVirtalAccountReqVo.getApplyMoney());
        withdrawalFromVirtalAccount.setBankName(withdrawalFromVirtalAccountReqVo.getBankName());
        withdrawalFromVirtalAccount.setSourceAccountType(withdrawalFromVirtalAccountReqVo.getSourceAccountType());
        withdrawalFromVirtalAccount.setTargetCurrency(withdrawalFromVirtalAccountReqVo.getTargetCurrency());
        withdrawalFromVirtalAccount.setSwift(withdrawalFromVirtalAccountReqVo.getSwift());
        withdrawalFromVirtalAccount.setBranch(withdrawalFromVirtalAccountReqVo.getBranch());
        withdrawalFromVirtalAccount.setWithdrawalTargetBankType(withdrawalFromVirtalAccountReqVo.getWithdrawalTargetBankType());
        withdrawalFromVirtalAccount.setWithdrawalTargetType(WithdrawalTargetTypeEnum.BankAccount);
        withdrawalFromVirtalAccount.setBankAddress(withdrawalFromVirtalAccountReqVo.getBankaddress());

        RpcMessage<RedeemApplyResDTO> rpcMessage = withdrawalRemoteService.withdrawalFromVirtalAccount(withdrawalFromVirtalAccount);
        if (rpcMessage.getResultCode() == RpcMessageStandardCode.OK.value()) {
            // clientId, goalId, orderId
            // Set to redis because of no method to get back the redeem apply ID with remote, need to create and read only from APP side (TTL , 5 sec)            
            setCurrentWithdrawalOrderId(withdrawalFromVirtalAccount.getClientId(), "SquirrelCash", String.valueOf(rpcMessage.getContent().getId()));
            return Message.success("Withdrawal is successful, orderId:" + rpcMessage.getContent().getId());
        } else {
            setCurrentWithdrawalOrderId(withdrawalFromVirtalAccount.getClientId(), "SquirrelCash", "Withdrawal is unsuccessful:" + rpcMessage.getErrMsg());
            return Message.error("Withdrawal is unsuccessful:" + rpcMessage.getErrMsg());
        }
    }

    @PostMapping("withdrawal.fromGoal")
    @ApiOperation(value = "资产账户提现", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message<String> withdrawalFromGoal(@RequestBody @Valid WithdrawalFromGoalReqVo withdrawalFromGoalReqVo) {
        log.info("用户从goal上提现:{}", JSON.toJSON(withdrawalFromGoalReqVo));
        if (withdrawalFromGoalReqVo.getApplyMoney().compareTo(BigDecimal.ZERO) <= 0) {
            log.info("非法的提现金额");
            Message.error("Amount needs to be more than 0");
        }
        BigDecimal sourceApplyMoney = withdrawalFromGoalReqVo.getApplyMoney();
        BigDecimal applyMoney = BigDecimal.ZERO;
        /*if (withdrawalFromGoalReqVo.getApplyCurrency() == CurrencyEnum.SGD) {
            ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO();
            exchangeRateDTO.setExchangeRateType(ExchangeRateTypeEnum.SAXO_FXRT2);
            RpcMessage<ExchangeRateResDTO> resDTORpcMessage = exchangeRemoteService.getLastExchangeRate(exchangeRateDTO);
            if (RpcMessageStandardCode.OK.value() != resDTORpcMessage.getResultCode()) {
                log.info("提现申请失败:没有可用的汇率");
                return Message.error("Withdrawal is unsuccessful: Missing FX rate");
            }
            applyMoney = sourceApplyMoney.divide(resDTORpcMessage.getContent().getUsdToSgd(), 6, BigDecimal.ROUND_DOWN);
        } else {
            applyMoney = sourceApplyMoney;
        }*/

        WithdrawalFromGoalDTO withdrawalFromGoal = new WithdrawalFromGoalDTO();
        withdrawalFromGoal.setBankAccountNo(withdrawalFromGoalReqVo.getBankAccountNo());
        withdrawalFromGoal.setClientId(withdrawalFromGoalReqVo.getClientId());
        withdrawalFromGoal.setApplyMoney(sourceApplyMoney);
        withdrawalFromGoal.setBankName(withdrawalFromGoalReqVo.getBankName());
        withdrawalFromGoal.setSourceAccountType(withdrawalFromGoalReqVo.getApplyCurrency());
        withdrawalFromGoal.setTargetCurrency(withdrawalFromGoalReqVo.getTargetCurrency());
        withdrawalFromGoal.setGoalId(withdrawalFromGoalReqVo.getGoalId());
        withdrawalFromGoal.setSwift(withdrawalFromGoalReqVo.getSwift());
        withdrawalFromGoal.setBranch(withdrawalFromGoalReqVo.getBranch());
        withdrawalFromGoal.setWithdrawalTargetBankType(withdrawalFromGoalReqVo.getTargetBankType());
        withdrawalFromGoal.setWithdrawalTargetType(withdrawalFromGoalReqVo.getTargetType());
        withdrawalFromGoal.setSourceApplyMoney(sourceApplyMoney);
        withdrawalFromGoal.setBankAddress(withdrawalFromGoalReqVo.getBankAddress());
        RpcMessage<RedeemApplyResDTO> rpcMessage = withdrawalRemoteService.withdrawalFromGoal(withdrawalFromGoal);
        if (rpcMessage.getResultCode() == RpcMessageStandardCode.OK.value()) {
            // clientId, goalId, orderId
            // Set to redis because of no method to get back the redeem apply ID with remote, need to create and read only from APP side (TTL , 5 sec)
            setCurrentWithdrawalOrderId(withdrawalFromGoalReqVo.getClientId(), withdrawalFromGoalReqVo.getGoalId(), String.valueOf(rpcMessage.getContent().getId()));
            return Message.success("Withdrawal is successful, orderId:" + rpcMessage.getContent().getId());
        } else {
            setCurrentWithdrawalOrderId(withdrawalFromGoalReqVo.getClientId(), withdrawalFromGoalReqVo.getGoalId(), "Withdrawal is unsuccessful:" + rpcMessage.getErrMsg());
            return Message.error("Withdrawal is unsuccessful:" + rpcMessage.getErrMsg());
        }
    }

}
