package com.pivot.aham.api.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.BankVirtualAccountOrderDTO;
import com.pivot.aham.api.server.dto.BankVirtualAccountOrderResDTO;
import com.pivot.aham.api.server.dto.UserGoalInfoDTO;
import com.pivot.aham.api.server.dto.UserGoalInfoResDTO;
import com.pivot.aham.api.server.dto.req.UobTransferReq;
import com.pivot.aham.api.server.dto.resp.ExchangeRateResult;
import com.pivot.aham.api.server.dto.resp.UobTransferResult;
import com.pivot.aham.api.server.remoteservice.SaxoTradeRemoteService;
import com.pivot.aham.api.server.remoteservice.UobTradeRemoteService;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.service.mapper.model.AccountInfoPO;
import com.pivot.aham.api.service.mapper.model.AccountRechargePO;
import com.pivot.aham.api.service.mapper.model.AccountUserPO;
import com.pivot.aham.api.service.mapper.model.SaxoAccountOrderPO;
import com.pivot.aham.api.service.service.*;
import com.pivot.aham.common.enums.*;
import com.pivot.aham.common.enums.analysis.*;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;
import com.pivot.aham.common.enums.recharge.UserRechargeStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 19/1/23.
 */
@Service
@Slf4j
public class RechargeServiceImpl implements RechargeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RechargeServiceImpl.class);

    @Resource
    private UserServiceRemoteService userServiceRemoteService;
    @Resource
    private UobTradeRemoteService uobTradeRemoteService;
    @Resource
    private AccountUserService accountUserService;
    @Resource
    private AccountInfoService accountInfoService;
    @Autowired
    private SaxoAccountOrderService saxoAccountOrderService;
    @Resource
    private AccountRechargeService accountRechargeService;
    @Resource
    private SaxoTradeRemoteService saxoTradeRemoteService;
    @Resource
    private AnalysisSupportService analysisSupportService;

    @Override
    public BigDecimal getUserRechargeMoney(AccountRechargePO accountRechargePO) {
        BigDecimal money = BigDecimal.ZERO;
        List<AccountRechargePO> accountRechargePOs = accountRechargeService.listByAccountId(accountRechargePO);
        for (AccountRechargePO accountRecharge : accountRechargePOs) {
            if (RechargeOrderStatusEnum.SUCCESS == accountRecharge.getOrderStatus()) {
                money = money.add(accountRecharge.getRechargeAmount()).setScale(6, BigDecimal.ROUND_HALF_UP);
            }
        }
        return money;
    }

    /**
     * 充值完分析下指令转账UOB到SAXO
     */
    @Override
    public void handelUobTransferToSaxo() {
        List<Long> errorBVAOrderIds = Lists.newArrayList();
        //1、检查是否开市
        log.info("充值完分析下指令转账UOB到SAXO,检查是否开市");
        boolean saxoIsTrading = analysisSupportService.checkSaxoIsTranding();
        if (!saxoIsTrading) {
            log.info("===充值完分析下指令转账UOB到SAXO，check SAXO 是否开市为false,不做交易");
            return;
        }
        //2、查询UOB中的处理中的操作类型是从UOB转SAXO的订单，下转账指令
        List<BankVirtualAccountOrderResDTO> bankVirtualAccountOrderResDTOS = getUobRechargeTransferToSaxo();
        if (CollectionUtils.isEmpty(bankVirtualAccountOrderResDTOS)) {
            log.info("查询UOB中的处理中的操作类型是从UOB转SAXO的订单，下转账指令,没有查询到需要操作的订单");
            return;
        }
        for (BankVirtualAccountOrderResDTO bankVirtualAccountOrder : bankVirtualAccountOrderResDTOS) {
            try {
                log.info("accountId:{},开始发送转账指令", bankVirtualAccountOrder.getId());
                //2.1、检查基本信息
                UserGoalInfoResDTO userGoalInfo = getUserGoal(bankVirtualAccountOrder);
                //2.2、幂等检查
                boolean hasDeal = checkBVAOrder(bankVirtualAccountOrder);
                if (hasDeal) {
                    log.info("幂等校验未通过:{}", JSON.toJSONString(bankVirtualAccountOrder));
                    //2.2.1、幂等校验已经在处理中就修改申请单状态
                    handelBVAOrderSuccess(bankVirtualAccountOrder, userGoalInfo.getClientId());
                    continue;
                }
                //2.3、先落单在下指令
                SaxoAccountOrderPO saxoAccountOrderPO = getSaxoAccountOrder(userGoalInfo, bankVirtualAccountOrder);
                //2.4、下转账指令
                UobTransferResult uobTransferResult = uobTransferToSaxoInstructions(saxoAccountOrderPO);
                //3、指令下单成功分配账号 (区分Pooling和tailor),没有就绑定，有就返回
                AccountInfoPO accountInfoPO = bindOrGetUserAccountInfo(userGoalInfo, bankVirtualAccountOrder, saxoAccountOrderPO);
                //4、修改转账流水处理中，修改申请转账指令订单状态为完成
                handelTransferSuccess(saxoAccountOrderPO, uobTransferResult, bankVirtualAccountOrder, accountInfoPO);
                //Added by WooiTatt
                userServiceRemoteService.updateUserRechargeStatus(bankVirtualAccountOrder.getId(), saxoAccountOrderPO.getId(), UserRechargeStatusEnum.PROGRESSUOBTOSAXO);
                log.info("accountId:{},发送转账指令成功", bankVirtualAccountOrder.getId());
            } catch (Exception ex) {
                log.error("充值完分析下指令转账UOB到SAXO,发送转账指令失败。bankVirtualAccountOrderId:{},ex:", bankVirtualAccountOrder.getId(), ex);
                errorBVAOrderIds.add(bankVirtualAccountOrder.getId());
            }
        }
        //5、等待回调
        //6、失败邮件通知
        if (CollectionUtils.isNotEmpty(errorBVAOrderIds)) {
            log.error("充值完分析下指令转账UOB到SAXO,失败的订单号,errorBVAOrderIds:{}", JSON.toJSONString(errorBVAOrderIds));
            ErrorLogAndMailUtil.logError(log, errorBVAOrderIds);
        }
    }

    /**
     * 获取用户账户信息,有就查询,没有就绑定
     *
     * @param userGoalInfo
     * @param bankVirtualAccountOrder
     * @param saxoAccountOrderPO
     * @return
     */
    private AccountInfoPO bindOrGetUserAccountInfo(UserGoalInfoResDTO userGoalInfo,
                                                   BankVirtualAccountOrderResDTO bankVirtualAccountOrder,
                                                   SaxoAccountOrderPO saxoAccountOrderPO) {
        CurrencyEnum firstRechargeCurrency = getVirtualAccountFirstRechargeCurrency(bankVirtualAccountOrder);
        BigDecimal transferSGDMoney = saxoAccountOrderPO.getCashAmount();
        String clientId = userGoalInfo.getClientId();
        LOGGER.info("clientId:{},goalId:{},money:{},获取账户分配信息", clientId, userGoalInfo.getGoalId(), saxoAccountOrderPO.getCashAmount());
        AccountInfoPO accountInfo = null;
        AccountUserPO queryParam = new AccountUserPO();
        queryParam.setClientId(clientId);
        queryParam.setReferenceCode(userGoalInfo.getReferenceCode());
        AccountUserPO accountUser = accountUserService.queryAccountUser(queryParam);
        if (null == accountUser) {
            LOGGER.info("clientId:{},currency:{},money:{},获取账户分配信息,是一个新账户", clientId, firstRechargeCurrency, transferSGDMoney);
            //创建新账户或者关联账户(分配账户)
            accountInfo = distributionAccount(userGoalInfo, saxoAccountOrderPO.getCashAmount(), firstRechargeCurrency);
        } else {
            LOGGER.info("clientId:{},currency:{},money:{},获取账户分配信息,是一个已存在的账户", clientId, firstRechargeCurrency, transferSGDMoney);
            //这里只能根据Id查询account.因为相同的portfolio可能关联在不同的用户上
            AccountInfoPO accountInfoPO = new AccountInfoPO();
            accountInfoPO.setId(accountUser.getAccountId());
            accountInfo = accountInfoService.queryAccountInfo(accountInfoPO);
        }
        LOGGER.info("clientId:{},currency:{},money:{},账户分配完毕:{}", clientId, firstRechargeCurrency, transferSGDMoney, JSON.toJSON(accountInfo));

        return accountInfo;
    }

    private CurrencyEnum getVirtualAccountFirstRechargeCurrency(BankVirtualAccountOrderResDTO bankVirtualAccountOrder) {
        log.info("查询改账户上的首次充值的货币类型，VirtualAccountNo：{}", bankVirtualAccountOrder.getVirtualAccountNo());
        RpcMessage<BankVirtualAccountOrderResDTO> rpcMessage = userServiceRemoteService.queryFirstBVAOrder(bankVirtualAccountOrder.getVirtualAccountNo());
        log.info("查询改账户上的首次充值的货币类型，返回：{}", JSON.toJSONString(rpcMessage));
        return rpcMessage.getContent().getCurrency();
    }

    /**
     * 分配账户
     *
     * @param userGoalInfo
     * @param transferSGDMoney
     * @param firstRechargeCurrency
     * @return
     */
    private AccountInfoPO distributionAccount(UserGoalInfoResDTO userGoalInfo,
                                              BigDecimal transferSGDMoney,
                                              CurrencyEnum firstRechargeCurrency) {
        BigDecimal usdMoney = BigDecimal.ZERO;
        LOGGER.info("clientId:{},currency:{},money:{},获取账户分配信息,汇率实时获取",
                userGoalInfo.getClientId(), firstRechargeCurrency, transferSGDMoney);
        RpcMessage<ExchangeRateResult> result = saxoTradeRemoteService.queryActualTimeRate();
        LOGGER.info("clientId:{},currency:{},money:{},获取账户分配信息,汇率获取,返回结果:{}",
                userGoalInfo.getClientId(), firstRechargeCurrency, transferSGDMoney, JSON.toJSON(result));
        if (RpcMessageStandardCode.OK.value() == result.getResultCode()) {
            usdMoney = transferSGDMoney.divide(result.getContent().getUSD_TO_SGD(), 6, BigDecimal.ROUND_HALF_UP);
        }else{
            throw new BusinessException("实时汇率获取失败");
        }
        AccountInfoPO accountInfoRes = null;
        String portfolioId = StringUtils.EMPTY;
        BigDecimal limitMoney = getAccountTypeLimitMoney();
        List<String> pollingAccounts = getInitPollingAccount();
        if (pollingAccounts.contains(String.valueOf(userGoalInfo.getClientId())) || usdMoney.compareTo(limitMoney) < 0) {
            LOGGER.info("clientId:{},currency:{},money:{},获取账户分配信息,汇率计算后的金额是,usdMoney:{},limitMoney:{},进入到Pooling",
                    userGoalInfo.getClientId(), firstRechargeCurrency, transferSGDMoney, usdMoney, limitMoney);
            //Pooling需要查询是否应存在Pooling,存在的话只是需要添加关系就好,不存在就是需要新建Pooling账户
            //Pooling需要根据用户选择的portfolio设置新的portfolio
            portfolioId = getPoolingPortfolio(userGoalInfo.getPortfolioId());
            AccountInfoPO accountInfoPool = new AccountInfoPO();
            accountInfoPool.setPortfolioId(portfolioId);
            accountInfoPool.setInvestType(AccountTypeEnum.POOLING);
            accountInfoRes = accountInfoService.queryAccountInfo(accountInfoPool);
            if (null == accountInfoRes) {
                accountInfoPool.setId(Sequence.next());
                accountInfoPool.setCreateTime(DateUtils.now());
                accountInfoPool.setUpdateTime(DateUtils.now());
                createTailerAccount(accountInfoPool);
                accountInfoRes = accountInfoPool;
            }
        } else {
            LOGGER.info("clientId:{},currency:{},money:{},获取账户分配信息,汇率计算后的金额是,usdMoney:{},limitMoney:{},进入到Tailor",
                    userGoalInfo.getClientId(), firstRechargeCurrency, transferSGDMoney, usdMoney, limitMoney);
            AccountInfoPO accountInfoTailor = new AccountInfoPO();
            accountInfoTailor.setInvestType(AccountTypeEnum.TAILOR);
            accountInfoTailor.setPortfolioId(userGoalInfo.getPortfolioId());
            accountInfoTailor.setCreateTime(DateUtils.now());
            accountInfoTailor.setUpdateTime(DateUtils.now());
            accountInfoTailor.setId(Sequence.next());
            createTailerAccount(accountInfoTailor);
            accountInfoRes = accountInfoTailor;
            portfolioId = userGoalInfo.getPortfolioId();
        }

        //维护账户和用户关系
        AccountUserPO accountUserPO = new AccountUserPO();
        accountUserPO.setAccountId(accountInfoRes.getId());
        accountUserPO.setClientId(userGoalInfo.getClientId());
        accountUserPO.setGoalId(userGoalInfo.getGoalId());
        accountUserPO.setPortfolioId(portfolioId);
        accountUserPO.setReferenceCode(userGoalInfo.getReferenceCode());
        accountUserPO.setAgeLevel(getAgeLevelByPortfolio(userGoalInfo.getPortfolioId()));
        accountUserPO.setRiskLevel(getRiskLevelByPortfolio(userGoalInfo.getPortfolioId()));
        accountUserPO.setCreateTime(DateUtils.now());
        accountUserPO.setUpdateTime(DateUtils.now());
        accountUserPO.setId(Sequence.next());
        accountUserPO.setEffectTime(DateUtils.now());
        accountUserPO.setFirstRechargeCurrency(firstRechargeCurrency);
        AccountUserPO accountUser = accountUserService.queryAccountUser(accountUserPO);
        if (accountUser == null) {
            createAccountUser(accountUserPO);
        }
        return accountInfoRes;
    }

    private RiskLevelEnum getRiskLevelByPortfolio(String portfolioId) {
        Integer riskValue = Integer.parseInt(StringUtils.substring(portfolioId, 3, 4));
        return RiskLevelEnum.forValue(riskValue);
    }

    private AgeLevelEnum getAgeLevelByPortfolio(String portfolioId) {
        Integer ageValue = Integer.parseInt(StringUtils.substring(portfolioId, 5, 6));
        return AgeLevelEnum.forValue(ageValue);
    }

    /**
     * 如果是Pooling用户需要更具选择的portfolio转换成对应的Pooling账户
     *
     * @param portfolioId P1R1A1
     * @return
     */
    private String getPoolingPortfolio(String portfolioId) {
        Integer poolingValue = Integer.parseInt(StringUtils.substring(portfolioId, 1, 2));
        Integer riskValue = Integer.parseInt(StringUtils.substring(portfolioId, 3, 4));
        Integer ageValue = Integer.parseInt(StringUtils.substring(portfolioId, 5, 6));

//        PoolingEnum poolingEnum = PoolingEnum.forValue(poolingValue);
//        if (PoolingEnum.P2 == poolingEnum) {
//            poolingValue = PoolingEnum.P3.getValue();
//        }
//        AgeLevelEnum ageLevelEnum = AgeLevelEnum.forValue(ageValue);
//        if (AgeLevelEnum.LEVEL_1 == ageLevelEnum) {
//            ageValue = AgeLevelEnum.LEVEL_2.getValue();
//        } else if (AgeLevelEnum.LEVEL_5 == ageLevelEnum) {
//            ageValue = AgeLevelEnum.LEVEL_4.getValue();
//        }

        return "P" + PoolingEnum.P3.getValue() + "R" + riskValue + "A" + AgeLevelEnum.LEVEL_2.getValue();
    }

    private void createAccountUser(AccountUserPO accountUserPO) {
        accountUserService.insertAccountUser(accountUserPO);
    }

    private void createTailerAccount(AccountInfoPO accountInfoTailor) {
        accountInfoService.insert(accountInfoTailor);

    }

    private List<String> getInitPollingAccount() {
        String pollingAccountStr = PropertiesUtil.getString("polling.username.init");
        if (StringUtils.isEmpty(pollingAccountStr)) {
            return Lists.newArrayList();
        }
        return Splitter.on(",").trimResults().splitToList(pollingAccountStr);
    }

    private BigDecimal getAccountTypeLimitMoney() {
        String limitMoney = PropertiesUtil.getString("username.type.invest.money");
        if (StringUtils.isEmpty(limitMoney)) {
            limitMoney = Constants.INVEST_TYPE;
        }
        return new BigDecimal(limitMoney);
    }

    private void handelBVAOrderSuccess(BankVirtualAccountOrderResDTO bankVirtualAccountOrder, String clientId) {
        BankVirtualAccountOrderDTO bankVirtualAccountOrderDTO = new BankVirtualAccountOrderDTO();
        bankVirtualAccountOrderDTO.setId(bankVirtualAccountOrder.getId());
        bankVirtualAccountOrderDTO.setOrderStatus(VAOrderTradeStatusEnum.SUCCESS);
        userServiceRemoteService.transferFinish(bankVirtualAccountOrderDTO, clientId);
    }

    /**
     * 处理转账成功
     *
     * @param saxoAccountOrderPO
     * @param uobTransferResult
     * @param bankVirtualAccountOrder
     * @param accountInfoPO
     */
    private void handelTransferSuccess(SaxoAccountOrderPO saxoAccountOrderPO,
                                       UobTransferResult uobTransferResult,
                                       BankVirtualAccountOrderResDTO bankVirtualAccountOrder,
                                       AccountInfoPO accountInfoPO) {
        saxoAccountOrderPO.setExchangeOrderNo(uobTransferResult.getOrderId());
        saxoAccountOrderPO.setAccountId(accountInfoPO.getId());
        saxoAccountOrderService.saveSaxoAccountOrder(saxoAccountOrderPO);

        BankVirtualAccountOrderDTO bankVirtualAccountOrderDTO = new BankVirtualAccountOrderDTO();
        bankVirtualAccountOrderDTO.setId(bankVirtualAccountOrder.getId());
        bankVirtualAccountOrderDTO.setOrderStatus(VAOrderTradeStatusEnum.SUCCESS);
        userServiceRemoteService.transferFinish(bankVirtualAccountOrderDTO, saxoAccountOrderPO.getClientId());

    }

    /**
     * 下UOB到SAXO的转账指令
     *
     * @param saxoAccountOrderPO
     */
    private UobTransferResult uobTransferToSaxoInstructions(SaxoAccountOrderPO saxoAccountOrderPO) {
        UobTransferReq uobTransferReq = new UobTransferReq();
        uobTransferReq.setOutBusinessId(saxoAccountOrderPO.getId());
        uobTransferReq.setCurrency(CurrencyEnum.SGD);
        uobTransferReq.setAmount(saxoAccountOrderPO.getCashAmount());
        log.info("下UOB到SAXO的转账指令,请求参数:{}", JSON.toJSONString(uobTransferReq));
        RpcMessage<UobTransferResult> transferToSaxoMsg = uobTradeRemoteService.transferToSaxo(uobTransferReq);
        log.info("下UOB到SAXO的转账指令,返回结果:{}", JSON.toJSONString(transferToSaxoMsg));
        if (RpcMessageStandardCode.OK.value() == transferToSaxoMsg.getResultCode()) {
            return transferToSaxoMsg.getContent();
        } else {
            throw new BusinessException("下UOB到SAXO的转账指令,调用接口失败");
        }
    }

    /**
     * 先落单在修改状态和分配账户
     * ！！！先落单在修改状态，为了防止后面重复发送幂等。
     *
     * @param userGoalInfo
     * @param bankVirtualAccountOrder
     * @return
     */
    private SaxoAccountOrderPO getSaxoAccountOrder(UserGoalInfoResDTO userGoalInfo,
                                                   BankVirtualAccountOrderResDTO bankVirtualAccountOrder) {
        //生成交易单号,用来关联SAXO的交易订单
        Long saxoAccountOrderId = Sequence.next();
        SaxoAccountOrderPO saxoAccountOrderPO = new SaxoAccountOrderPO();
        saxoAccountOrderPO.setId(saxoAccountOrderId);
        saxoAccountOrderPO.setCurrency(CurrencyEnum.SGD);
        saxoAccountOrderPO.setCashAmount(bankVirtualAccountOrder.getCashAmount());
        //用来关联一批saxo订单,是SAXO指令下成功之后返回的订单好
        saxoAccountOrderPO.setExchangeOrderNo(0L);
        saxoAccountOrderPO.setOperatorType(SaxoOrderTradeTypeEnum.COME_INTO);
        saxoAccountOrderPO.setActionType(SaxoOrderActionTypeEnum.UOBTOSAXO);
        saxoAccountOrderPO.setOrderStatus(SaxoOrderTradeStatusEnum.HANDLING);
        saxoAccountOrderPO.setTradeTime(DateUtils.now());
        saxoAccountOrderPO.setCreateTime(DateUtils.now());
        saxoAccountOrderPO.setUpdateTime(DateUtils.now());
        saxoAccountOrderPO.setBankOrderNo(bankVirtualAccountOrder.getBankOrderNo());
        saxoAccountOrderPO.setClientId(userGoalInfo.getClientId());
        //这里采用默认值，在指令发送成功之后更新
        saxoAccountOrderPO.setAccountId(0L);
        saxoAccountOrderPO.setGoalId(userGoalInfo.getGoalId());
        return saxoAccountOrderPO;
    }

    /**
     * 获取用户goal详情
     *
     * @param bankVirtualAccountOrder
     * @return
     */
    private UserGoalInfoResDTO getUserGoal(BankVirtualAccountOrderResDTO bankVirtualAccountOrder) {
        UserGoalInfoDTO userGoalInfoDTO = new UserGoalInfoDTO();
        userGoalInfoDTO.setReferenceCode(bankVirtualAccountOrder.getReferenceCode());
        log.info("充值完分析下指令转账UOB到SAXO，获取用户goal详情，参数:{}", JSON.toJSONString(userGoalInfoDTO));
        RpcMessage<UserGoalInfoResDTO> userGoalInfoResDTORpcMessage = userServiceRemoteService.getUserGoalInfo(userGoalInfoDTO);
        log.info("充值完分析下指令转账UOB到SAXO，获取用户goal详情，结果:{}", JSON.toJSONString(userGoalInfoResDTORpcMessage));
        if (RpcMessageStandardCode.OK.value() == userGoalInfoResDTORpcMessage.getResultCode()) {
            return userGoalInfoResDTORpcMessage.getContent();
        } else {
            throw new BusinessException("充值完分析下指令转账UOB到SAXO,获取用户goal详情,查询接口失败");
        }
    }

    /**
     * 查询需要下指令的UOB的充值单
     *
     * @return
     */
    private List<BankVirtualAccountOrderResDTO> getUobRechargeTransferToSaxo() {
        BankVirtualAccountOrderDTO virtualAccountOrderDTO = new BankVirtualAccountOrderDTO();
        virtualAccountOrderDTO.setCurrency(CurrencyEnum.SGD);
        virtualAccountOrderDTO.setOrderStatus(VAOrderTradeStatusEnum.HANDLING);
        virtualAccountOrderDTO.setActionType(VAOrderActionTypeEnum.UOBTOSAXO);
        virtualAccountOrderDTO.setOperatorType(VAOrderTradeTypeEnum.COME_OUT);
        log.info("充值完分析下指令转账UOB到SAXO,virtualAccountOrderDTO:{}", JSON.toJSONString(virtualAccountOrderDTO));
        RpcMessage<List<BankVirtualAccountOrderResDTO>> rpcMessage = userServiceRemoteService.listBankVirtualAccountOrders(virtualAccountOrderDTO);
        log.info("充值完分析下指令转账UOB到SAXO返回,rpcMessage:{}", JSON.toJSONString(rpcMessage));
        if (RpcMessageStandardCode.OK.value() == rpcMessage.getResultCode()) {
            return rpcMessage.getContent();
        } else {
            throw new BusinessException("充值完分析下指令转账UOB到SAXO，查询虚拟账户订单失败");
        }
    }

    /**
     * 校验改订单是否以及处理，只要有处理中或处理成功的都是已经处理的
     *
     * @param bankVirtualAccountOrder
     * @return
     */
    private boolean checkBVAOrder(BankVirtualAccountOrderResDTO bankVirtualAccountOrder) {
        SaxoAccountOrderPO saxoAccountOrderParam = new SaxoAccountOrderPO();
        saxoAccountOrderParam.setBankOrderNo(bankVirtualAccountOrder.getBankOrderNo());
        List<SaxoAccountOrderPO> saxoAccountOrderPOS = saxoAccountOrderService.listSaxoAccountOrder(saxoAccountOrderParam);
        if (CollectionUtils.isEmpty(saxoAccountOrderPOS)) {
            return false;
        }
        for (SaxoAccountOrderPO saxoAccountOrderPO : saxoAccountOrderPOS) {
            if (SaxoOrderTradeStatusEnum.SUCCESS == saxoAccountOrderPO.getOrderStatus()
                    || SaxoOrderTradeStatusEnum.HANDLING == saxoAccountOrderPO.getOrderStatus()) {
                return true;
            }
        }
        return false;
    }

}
