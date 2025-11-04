package com.pivot.aham.api.web.app.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.server.dto.req.ExchangeRateDTO;
import com.pivot.aham.api.server.dto.res.ExchangeRateResDTO;
import com.pivot.aham.api.server.remoteservice.ExchangeRemoteService;
import com.pivot.aham.api.web.app.dto.reqdto.WithdrawAlscDTO;
import com.pivot.aham.api.web.app.dto.resdto.WithdrawAlscResDTO;
import com.pivot.aham.api.web.app.dto.resdto.WithdrawResDTO;
import com.pivot.aham.api.web.app.vo.req.WithdrawFromCashReqVo;
import com.pivot.aham.api.web.app.vo.req.WithdrawReqVo;
import com.pivot.aham.api.web.app.vo.res.WithdrawResVo;
import com.pivot.aham.api.web.app.febase.AppResultCode;
import com.pivot.aham.api.web.app.service.AppService;
import com.pivot.aham.api.web.core.ExceptionUtil;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.ExchangeRateTypeEnum;
import com.pivot.aham.common.enums.analysis.WithdrawalTargetTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;

/**
 * @author YYYz
 */
@RestController
@RequestMapping("/api/v1/")
@Api(value = "提现", description = "提现接口")
public class AppWithdrawController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppWithdrawController.class);
    @Resource
    private ExchangeRemoteService exchangeRemoteService;
    @Resource
    private AppService appService;

    @PostMapping("app/withdrawFromCash")
    @ApiOperation(value = "虚拟账户提现", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message<WithdrawResVo> withdrawalFromVirtualAccount(@RequestBody @Valid WithdrawFromCashReqVo withdrawFromCashReqVo) {
        if (!checkLogin(withdrawFromCashReqVo.getClientId())) {
            return Message.error(AppResultCode.UNAUTHORIZED.value(), AppResultCode.UNAUTHORIZED.msg());
        }
        LOGGER.info("用户从虚拟账户上提现:{}", JSON.toJSON(withdrawFromCashReqVo));
        if (withdrawFromCashReqVo.getApplyMoney().compareTo(BigDecimal.ZERO) <= 0) {
            LOGGER.info("非法的提现金额");
            Message.error("Amount needs to be more than 0");
        }

        if (withdrawFromCashReqVo.getApplyMoney() == null && withdrawFromCashReqVo.getApplyMoney().doubleValue() < 0) {
            withdrawFromCashReqVo.setApplyMoney(new BigDecimal("0.00"));
        }

        WithdrawAlscDTO withdrawAlscDTO = withdrawFromCashReqVo.convertToDto(withdrawFromCashReqVo);
        // Send data to FE and update the info
        WithdrawAlscResDTO withdrawAlscResDTO = appService.withdrawalsc(withdrawAlscDTO);
        if (withdrawAlscResDTO != null) {
            if (withdrawAlscResDTO.getResultCode().equals(String.valueOf(AppResultCode.OK))) {
                // Shouldn't perform withdraw again.
//                RpcMessage<RedeemApplyResDTO> rpcMessage = withdrawalRemoteService.withdrawalFromVirtalAccount(withdrawalFromVirtalAccount);
                WithdrawResVo withdrawResVo = new WithdrawResVo();
                withdrawResVo.setAmt(withdrawFromCashReqVo.getApplyMoney())
                        .setBankAcctNo(withdrawFromCashReqVo.getBankAccountNo())
                        .setBankCode(withdrawFromCashReqVo.getBankCode())
                        .setBankName(withdrawFromCashReqVo.getBankName())
                        .setClientId(withdrawFromCashReqVo.getClientId())
                        .setDate(DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT3))
                        .setTime(DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_TIME_FORMAT_UTC_HMS))
                        .setType(String.valueOf(WithdrawalTargetTypeEnum.BankAccount.getValue()));

                String responseStr = getCurrentWithdrawalOrderId(withdrawAlscDTO.getClientid(), "SquirrelCash");
                LOGGER.info("withdrawalFromVirtualAccount, What is the orderId {} ", responseStr);
                if (responseStr != null) {
                    if (responseStr.contains("unsuccessful")) {
                        // Get back error message
                        return Message.success(responseStr);
                    } else {
                        // Get back order Id;
                        withdrawResVo.setOrderId(String.valueOf(responseStr));
                        return Message.success(withdrawResVo);
                    }
                } else {
                    return Message.error("Withdrawal is unsuccessful .. Please contact client services for more details");
                }
            } else {
                return Message.error(AppResultCode.CONFLICT.value(), withdrawAlscResDTO.getErrorMsg());
            }
        } else {
            return Message.error("Withdrawal is unsuccessful .. Please contact client services for more details");
        }
    }

    @PostMapping("app/withdraw")
    @ApiOperation(value = "from Goal to Bank", produces = MediaType.APPLICATION_JSON_VALUE)
    @SentinelResource(value = "qpsFlow", blockHandler = "handleException", blockHandlerClass = {ExceptionUtil.class})
    public Message<WithdrawResVo> withdraw(@RequestBody WithdrawReqVo withDrawReqVo) throws Exception {
        if (!checkLogin(withDrawReqVo.getClientId())) {
            return Message.error(AppResultCode.UNAUTHORIZED.value(), AppResultCode.UNAUTHORIZED.msg());
        }
        LOGGER.info("用户从goal上提现:{}", JSON.toJSON(withDrawReqVo));
        if (withDrawReqVo.getApplyMoney().compareTo(BigDecimal.ZERO) <= 0) {
            LOGGER.info("非法的提现金额");
            Message.error("Amount needs to be more than 0");
        }
        BigDecimal sourceApplyMoney = withDrawReqVo.getApplyMoney();
        BigDecimal applyMoney = BigDecimal.ZERO;
        if (CurrencyEnum.forValue(withDrawReqVo.getApplyCurrency()) == CurrencyEnum.SGD) {
            ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO();
            exchangeRateDTO.setExchangeRateType(ExchangeRateTypeEnum.SAXO_FXRT2);
            RpcMessage<ExchangeRateResDTO> resDTORpcMessage = exchangeRemoteService.getLastExchangeRate(exchangeRateDTO);
            if (RpcMessageStandardCode.OK.value() != resDTORpcMessage.getResultCode()) {
                LOGGER.info("提现申请失败:没有可用的汇率");
                return Message.error("Withdrawal is unsuccessful: Missing FX rate");
            }
            applyMoney = sourceApplyMoney.divide(resDTORpcMessage.getContent().getUsdToSgd(), 6, BigDecimal.ROUND_DOWN);
        } else {
            applyMoney = sourceApplyMoney;
        }

        WithdrawAlscDTO withdrawAlscDTO = withDrawReqVo.convertToDto(withDrawReqVo);
        // Send data to FE and update the info
        WithdrawResDTO withdrawResDTO = appService.withdraw(withdrawAlscDTO);
        if (withdrawResDTO != null) {
            if (withdrawResDTO.getResultCode().equals(String.valueOf(AppResultCode.OK))) {
                WithdrawResVo withdrawResVo = new WithdrawResVo();
                withdrawResVo.setAmt(withDrawReqVo.getApplyMoney())
                        .setBankAcctNo(withDrawReqVo.getBankAccountNo())
                        .setBankCode(withDrawReqVo.getBankCode())
                        .setBankName(withDrawReqVo.getBankName())
                        .setClientId(withDrawReqVo.getClientId())
                        .setDate(DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT3))
                        .setTime(DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_TIME_FORMAT_UTC_HMS))
                        .setGoalId(withDrawReqVo.getGoalId())
                        .setType(String.valueOf(withDrawReqVo.getTargetType()));
                String responseStr = getCurrentWithdrawalOrderId(withdrawAlscDTO.getClientid(), withdrawAlscDTO.getGoalid());
                LOGGER.info("withdrawalFromGoal, What is the orderId {} ", responseStr);
                if (responseStr != null) {
                    if (responseStr.contains("unsuccessful")) {
                        // Get back error message
                        return Message.success(responseStr);
                    } else {
                        // Get back order Id;
                        withdrawResVo.setOrderId(String.valueOf(responseStr));
                        return Message.success(withdrawResVo);
                    }
                } else {
                    return Message.error("Withdrawal is unsuccessful .. Please contact client services for more details");
                }
            } else {
                return Message.error("Withdrawal is unsuccessful:" + withdrawResDTO.getErrorMsg());
            }
        } else {
            return Message.error("Withdrawal is unsuccessful: Please contact client services for more details");
        }
    }
}
