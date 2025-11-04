package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.beust.jcommander.internal.Lists;
import com.google.common.eventbus.EventBus;
import com.pivot.aham.api.server.dto.UobTransferToSaxoCallbackDTO;
import com.pivot.aham.api.server.dto.res.AccountRechargeResDTO;
import com.pivot.aham.api.server.remoteservice.RechargeServiceRemoteService;
import com.pivot.aham.api.server.remoteservice.SaxoTradeRemoteService;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.server.dto.req.ExchangeRateReq;
import com.pivot.aham.api.server.dto.req.InterAccountTransferReq;
import com.pivot.aham.api.server.dto.resp.ExchangeRateResult;
import com.pivot.aham.api.server.dto.resp.InterAccountTransferResult;
import com.pivot.aham.api.service.job.interevent.StaticRateForAccountEvent;
import com.pivot.aham.api.service.job.wrapperbean.RechargeCallbackExchangeBean;
import com.pivot.aham.api.service.mapper.model.AccountRechargePO;
import com.pivot.aham.api.service.mapper.model.ExchangeRatePO;
import com.pivot.aham.api.service.mapper.model.SaxoAccountOrderPO;
import com.pivot.aham.api.service.remote.impl.wrapperbean.UobTransferSaxoBean;
import com.pivot.aham.api.service.job.TradeAnalysisJob;
import com.pivot.aham.api.service.service.AccountRechargeService;
import com.pivot.aham.api.service.service.AnalysisSupportService;
import com.pivot.aham.api.service.service.ExchangeRateService;
import com.pivot.aham.api.service.service.SaxoAccountOrderService;
import com.pivot.aham.common.enums.analysis.*;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
import com.pivot.aham.common.core.support.cache.RedissonHelper;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.ExchangeRateTypeEnum;
import com.pivot.aham.common.enums.TransferStatusEnum;
import com.pivot.aham.common.enums.recharge.TpcfStatusEnum;
import com.pivot.aham.common.enums.recharge.UserRechargeStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月10日
 */
@Service(interfaceClass = RechargeServiceRemoteService.class)
@Slf4j
public class RechargeServiceRemoteServiceImpl implements RechargeServiceRemoteService {

    @Resource
    private RedissonHelper redissonHelper;

    @Resource
    private AccountRechargeService accountRechargeService;
    @Resource
    private AnalysisSupportService analysisSupportService;
    @Autowired
    private SaxoAccountOrderService saxoAccountOrderService;
    @Resource
    private ExchangeRateService exchangeRateService;
    @Resource
    private SaxoTradeRemoteService saxoTradeRemoteService;
    @Resource
    private TradeAnalysisJob tradeAnalysisJob;
    @Resource
    private EventBus eventBus;
    @Resource
    private UserServiceRemoteService userServiceRemoteService;

    @Value("${env.remark}")
    private String env;

    /**
     * 合并转账成功之后的回调(UOB --> SAXO 完成之后的回调)
     * <p>
     * 1、转账成功 : 修改虚拟账户订单为成功、SAXO SGD转账入金成功 2、内部转账 : SAXO SGD换汇成USD 合并转账
     * 3、转账完成之后按照申请金额分配确认金额 4、记录充值记录 、中间值 、汇率
     *
     * @param orderNo
     * @param params
     * @return
     */
    @Override
    public RpcMessage rechargeUobTransferToSaxoCallback(String orderNo, List<UobTransferToSaxoCallbackDTO> params) {
        log.info("合并转账成功之后的回调,请求参数:{}", JSON.toJSON(params));
        if (CollectionUtils.isEmpty(params)) {
            return RpcMessage.success();
        }
        //实时汇率，有充值就用充值计算的，没有充值的就用实时的
        BigDecimal fxRate = null;
        BigDecimal actualTimeRate = exchangeRateService.getActualTimeRate();
        UobTransferSaxoBean uobTransferSaxoBean = getNeedTransferSaxoOrder(params);
        log.info("===合并转账成功之后的回调需要Saxo内部购汇的，uobTransferSaxoBean:{}", JSON.toJSONString(uobTransferSaxoBean));
        if (uobTransferSaxoBean.getApplyMoney().compareTo(BigDecimal.ZERO) > 0) {
            RechargeCallbackExchangeBean exchangeBean = rechargeCallbackExchange(uobTransferSaxoBean.getApplyMoney());
            if (exchangeBean.isExchangeSuccess()) {
                String key = "rechargeUobTransferToSaxoCallback_count";
                try {
                    redissonHelper.incr(key);
                } catch (Exception e) {
                    log.info("rechargeUobTransferToSaxoCallback_count fail to set Cache");
                }
                //分帐确认的金额 + 记录充值记录
                fxRate = transferSplitConfirmMoney(uobTransferSaxoBean, exchangeBean.getConfirmMoney());
            }
        } else {
            log.info("===合并转账成功之后的回调需要Saxo内部购汇的金额为0，不做购汇");
        }

        //保存exchangeRate
        if (null == fxRate) {
            fxRate = actualTimeRate;
        }
        ExchangeRatePO exchangeRatePO = new ExchangeRatePO();
        exchangeRatePO.setExchangeRateType(ExchangeRateTypeEnum.SAXO_FXRT1);
        exchangeRatePO.setUsdToSgd(fxRate);
        exchangeRatePO.setRateDate(DateUtils.getDate(DateUtils.now(), 0, 0, 0));
        exchangeRateService.saveDailyExchangeRate(exchangeRatePO);

        String key = "transferToUSDFromSGD:" + orderNo;
        try {
            String value = redissonHelper.get(key);
            if (value.equalsIgnoreCase("0")) {
                redissonHelper.set(key, "1");
            }
        } catch (Exception e) {
            log.info("rechargeUobTransferToSaxoCallback fail to set Cache");
        }

        return RpcMessage.success();
    }

    /**
     * 合并转账成功之后的回调(UOB --> SAXO 完成之后的回调)
     * <p>
     * 1、转账成功 : 修改虚拟账户订单为成功、SAXO SGD转账入金成功 2、内部转账 : SAXO SGD换汇成USD 合并转账
     * 3、转账完成之后按照申请金额分配确认金额 4、记录充值记录 、中间值 、汇率
     *
     * @param params
     * @return
     */
    @Override
    public RpcMessage rechargeUobTransferToSaxoCallback(List<UobTransferToSaxoCallbackDTO> params) {
        return rechargeUobTransferToSaxoCallback("", params);
    }

    /**
     * uob转账saxo后回调分账 1、按照申请金额配比分账, 最后一个用户处理 confirmMoney = totalConfirmMoney -
     * successMoney; 2、记录充值订单 3、修改saxo的订单
     * <p>
     * * SAXO 内部转账 : * 1、saxo SGD账户持有 * 2、saxo SGD账户出 * 3、saxo USD账户入 * 4、saxo
     * USD账户出
     *
     * @param uobTransferSaxoBean
     * @param totalConfirmMoney
     */
    private BigDecimal transferSplitConfirmMoney(UobTransferSaxoBean uobTransferSaxoBean, BigDecimal totalConfirmMoney) {
        BigDecimal fxRate = BigDecimal.ZERO;
        int handleNum = 0;
        Long exchangeTotalOrderId = Sequence.next();
        BigDecimal successMoney = BigDecimal.ZERO;
        BigDecimal totalApplyMoney = uobTransferSaxoBean.getApplyMoney();
        List<SaxoAccountOrderPO> confirmSaxoAccountOrders = uobTransferSaxoBean.getConfirmSaxoAccountOrders();
        for (SaxoAccountOrderPO processingSgd : confirmSaxoAccountOrders) {
            try {
                handleNum++;
                BigDecimal confirmMoney = processingSgd.getCashAmount().multiply(totalConfirmMoney).divide(totalApplyMoney, 6, BigDecimal.ROUND_DOWN);
                //最后一个用户处理
                if (handleNum == confirmSaxoAccountOrders.size()) {
                    confirmMoney = totalConfirmMoney.subtract(successMoney).setScale(6, BigDecimal.ROUND_DOWN);
                }
                if (confirmMoney.compareTo(BigDecimal.ZERO) <= 0) {
                    log.info("bankOrderNo:{},计算出来的确认金额为0，不做充值记录", processingSgd.getBankOrderNo());
                    continue;
                }
                successMoney = successMoney.add(confirmMoney);
                List<SaxoAccountOrderPO> saxoAccountOrderPOAdds = Lists.newArrayList();
                processingSgd.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
                processingSgd.setExchangeTotalOrderId(exchangeTotalOrderId);
                SaxoAccountOrderPO exchangeOutSgd = saxoAccountOrderService.getRechargeSaxoAccountOrder(processingSgd,
                        CurrencyEnum.SGD, processingSgd.getCashAmount(), SaxoOrderTradeStatusEnum.SUCCESS,
                        SaxoOrderTradeTypeEnum.COME_OUT, SaxoOrderActionTypeEnum.RECHARGE_EXCHANGE, exchangeTotalOrderId);
                SaxoAccountOrderPO exchangeInUsd = saxoAccountOrderService.getRechargeSaxoAccountOrder(processingSgd,
                        CurrencyEnum.MYR, confirmMoney, SaxoOrderTradeStatusEnum.SUCCESS,
                        SaxoOrderTradeTypeEnum.COME_INTO, SaxoOrderActionTypeEnum.RECHARGE_EXCHANGE, exchangeTotalOrderId);
                SaxoAccountOrderPO exchangeOutUsd = saxoAccountOrderService.getRechargeSaxoAccountOrder(exchangeInUsd,
                        CurrencyEnum.MYR, confirmMoney, SaxoOrderTradeStatusEnum.SUCCESS,
                        SaxoOrderTradeTypeEnum.COME_OUT, SaxoOrderActionTypeEnum.RECHARGE, exchangeTotalOrderId);
                saxoAccountOrderPOAdds.add(exchangeOutSgd);
                saxoAccountOrderPOAdds.add(exchangeInUsd);
                saxoAccountOrderPOAdds.add(exchangeOutUsd);

                /**
                 * 保存充值流水 账户资产,unBuy状态为持有 记录投资充值记录 !! 账户充值金额币种都是 美金
                 * 能记录投资充值记录必定是有goal的,即:1、线下充值的时候填写referenceCode;2、用户手动设置了goal(给goal分配金额)
                 */
                AccountRechargePO accountRecharge = accountRechargeService.handleAccountRecharge(exchangeOutUsd, processingSgd.getTradeTime());
                analysisSupportService.handelTransferCallback(saxoAccountOrderPOAdds, processingSgd, accountRecharge);
                //Added by WooiTatt 
                AccountRechargePO accountRec = accountRechargeService.queryAccountRecharge(accountRecharge);
                userServiceRemoteService.updateUserRechargeStatus(processingSgd.getId(), accountRec.getId(), UserRechargeStatusEnum.INTERTRANSTOUSD);

                StaticRateForAccountEvent staticRateForAccountEvent = new StaticRateForAccountEvent();
                staticRateForAccountEvent.setAccountId(exchangeOutSgd.getAccountId());
                //计算费率
                fxRate = totalApplyMoney.divide(totalConfirmMoney, 6, BigDecimal.ROUND_HALF_DOWN);
                staticRateForAccountEvent.setFxRate(fxRate);
                staticRateForAccountEvent.setFxRateTypeEnum(FxRateTypeEnum.FUNDIN);
                eventBus.post(staticRateForAccountEvent);

            } catch (Exception ex) {
                log.error("saxoAccountOrderId:{},充值UOB转账SAXO回调确认,异常", processingSgd.getId(), ex);
                RpcMessage.error(ex.getMessage());
            }
        }
        return fxRate;
    }

    /**
     * 获取需要购汇的金额和saxo订单
     *
     * @param uobTransferToSaxoCallbackDTOS
     * @return
     */
    private UobTransferSaxoBean getNeedTransferSaxoOrder(List<UobTransferToSaxoCallbackDTO> uobTransferToSaxoCallbackDTOS) {
        BigDecimal totalApplyMoney = BigDecimal.ZERO;
        List<SaxoAccountOrderPO> confirmSaxoAccountOrders = Lists.newArrayList();
        for (UobTransferToSaxoCallbackDTO uobTransferToSaxoCallbackDTO : uobTransferToSaxoCallbackDTOS) {
            if (TransferStatusEnum.SUCCESS != uobTransferToSaxoCallbackDTO.getTransferStatus()) {
                log.info("合并转账成功之后的回调,状态为失败:{}", JSON.toJSONString(uobTransferToSaxoCallbackDTO));
                continue;
            }
            SaxoAccountOrderPO saxoAccountOrderPO = new SaxoAccountOrderPO();
            saxoAccountOrderPO.setId(uobTransferToSaxoCallbackDTO.getOrderNo());
            SaxoAccountOrderPO processingSgd = saxoAccountOrderService.querySaxoAccountOrder(saxoAccountOrderPO);
            if (null == processingSgd) {
                log.error("合并转账成功之后的回调,根据:{},没有查询到订单", JSON.toJSON(uobTransferToSaxoCallbackDTO));
                continue;
            }
            //幂等 ->
            if (SaxoOrderTradeStatusEnum.HANDLING != processingSgd.getOrderStatus()) {
                log.info("合并转账成功之后的回调,幂等校验,已经处理过,bankOrderNo:{}", processingSgd.getBankOrderNo());
                continue;
            }
            confirmSaxoAccountOrders.add(processingSgd);
            totalApplyMoney = totalApplyMoney.add(processingSgd.getCashAmount());
        }

        UobTransferSaxoBean uobTransferSaxoBean = new UobTransferSaxoBean();
        uobTransferSaxoBean.setConfirmSaxoAccountOrders(confirmSaxoAccountOrders);
        uobTransferSaxoBean.setApplyMoney(totalApplyMoney.setScale(6, BigDecimal.ROUND_HALF_UP));
        return uobTransferSaxoBean;
    }

    /**
     * 回调金额在SAXO中新币必须转美金
     *
     * @param cashAmount
     * @return
     */
    private RechargeCallbackExchangeBean rechargeCallbackExchange(BigDecimal cashAmount) {
        boolean exchangeSuccess = false;
        BigDecimal confirmMoney = BigDecimal.ZERO;
        if ("dev".equals(env) || "test".equals(env)) {
            //TODO 测试环境saxo不可用,自己计算
            ExchangeRateReq exchangeRateReq = new ExchangeRateReq();
            exchangeRateReq.setDate(DateUtils.now());
            log.info("===测试环境,充值回调SAXO内部sgd转usd,请求参数：{}", cashAmount);
            RpcMessage<ExchangeRateResult> exchangeRateResult = saxoTradeRemoteService.queryExchangeRate(exchangeRateReq);
            log.info("===测试环境,充值回调SAXO内部sgd转usd,返回结果：{}", JSON.toJSONString(exchangeRateResult));
            if (RpcMessageStandardCode.OK.value() == exchangeRateResult.getResultCode()) {
                confirmMoney = cashAmount.divide(exchangeRateResult.getContent().getUSD_TO_SGD(), 6, BigDecimal.ROUND_DOWN);
                exchangeSuccess = true;
            }
        } else {
            InterAccountTransferReq req = new InterAccountTransferReq();
            req.setApplyAmount(cashAmount);
            log.info("===充值回调SAXO内部sgd转usd,请求参数：{}", cashAmount);
            RpcMessage<InterAccountTransferResult> accountTransferResult = saxoTradeRemoteService.transferToUSDFromSGD(req);
            log.info("===充值回调SAXO内部sgd转usd,返回结果：{}", JSON.toJSONString(accountTransferResult));
            if (RpcMessageStandardCode.OK.value() == accountTransferResult.getResultCode()) {
                confirmMoney = accountTransferResult.getContent().getSuccessAmount();
                exchangeSuccess = true;
            }
        }
        RechargeCallbackExchangeBean exchangeBean = new RechargeCallbackExchangeBean();
        exchangeBean.setConfirmMoney(confirmMoney);
        exchangeBean.setExchangeSuccess(exchangeSuccess);
        return exchangeBean;
    }

    @Override
    public void uboTransferSaxoCallback() {
        List<UobTransferToSaxoCallbackDTO> params = Lists.newArrayList();

        SaxoAccountOrderPO po = new SaxoAccountOrderPO();
        po.setOrderStatus(SaxoOrderTradeStatusEnum.HANDLING);
        List<SaxoAccountOrderPO> saxoAccountOrderPOs = saxoAccountOrderService.listSaxoAccountOrder(po);
        params = saxoAccountOrderPOs.stream().map(item -> {
            UobTransferToSaxoCallbackDTO dto = new UobTransferToSaxoCallbackDTO();
            dto.setOrderNo(item.getId());
            dto.setTransferStatus(TransferStatusEnum.SUCCESS);
            return dto;
        }).collect(Collectors.toList());
        rechargeUobTransferToSaxoCallback(params);
    }

    @Override
    public void tradeAnalysisJob(String accountId) {
        tradeAnalysisJob.tradeAnalysis(accountId);
    }

    @Override
    public void rechargeAhamTransferList(List<AccountRechargeResDTO> accountRechargeResDTOList) {

        for (AccountRechargeResDTO accountRechargeResDTO : accountRechargeResDTOList){

            //Create account recharge PO
            AccountRechargePO accountRechargePO = new AccountRechargePO();
            accountRechargePO.setAccountId(accountRechargeResDTO.getAccountId());
            accountRechargePO.setClientId(accountRechargeResDTO.getClientId());
            accountRechargePO.setRechargeAmount(accountRechargeResDTO.getRechargeAmount());
            accountRechargePO.setRechargeTime(accountRechargeResDTO.getRechargeTime());
            accountRechargePO.setCreateTime(DateUtils.now());
            accountRechargePO.setUpdateTime(DateUtils.now());
            accountRechargePO.setOrderStatus(RechargeOrderStatusEnum.SUCCESS);
            accountRechargePO.setBankOrderNo(accountRechargeResDTO.getBankOrderNo());
            accountRechargePO.setRechargeOrderNo(Sequence.next());
            accountRechargePO.setExecuteOrderNo(Sequence.next());
            accountRechargePO.setTpcfStatus(TpcfStatusEnum.PROCESSING);
            accountRechargePO.setGoalId(accountRechargeResDTO.getGoalId());
            accountRechargePO.setCurrency(accountRechargeResDTO.getCurrency());

            //save account recharge
            accountRechargeService.saveAccountRecharge(accountRechargePO);
        }
    }

    @Override
    public void rechargeAhamTransfer(AccountRechargeResDTO accountRechargeResDTO) {
        AccountRechargePO accountRechargePO = new AccountRechargePO();
        accountRechargePO.setAccountId(accountRechargeResDTO.getAccountId());
        accountRechargePO.setClientId(accountRechargeResDTO.getClientId());
        accountRechargePO.setRechargeAmount(accountRechargeResDTO.getRechargeAmount());
        accountRechargePO.setRechargeTime(accountRechargeResDTO.getRechargeTime());
        accountRechargePO.setCreateTime(DateUtils.now());
        accountRechargePO.setUpdateTime(DateUtils.now());
        accountRechargePO.setOrderStatus(RechargeOrderStatusEnum.SUCCESS);
        accountRechargePO.setBankOrderNo(accountRechargeResDTO.getBankOrderNo());
        accountRechargePO.setRechargeOrderNo(accountRechargeResDTO.getRechargeOrderNo());
        accountRechargePO.setExecuteOrderNo(Sequence.next());
        accountRechargePO.setTpcfStatus(TpcfStatusEnum.PROCESSING);
        accountRechargePO.setGoalId(accountRechargeResDTO.getGoalId());
        accountRechargePO.setCurrency(accountRechargeResDTO.getCurrency());

        try {
            accountRechargeService.saveAccountRecharge(accountRechargePO);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Account ID:{},Client ID:{}, Failed to save account recharge:", accountRechargePO.getAccountId(), accountRechargePO.getClientId(), ex);
        }
    }
}
