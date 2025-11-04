package com.pivot.aham.api.service.job.custstatment.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.pivot.aham.api.server.dto.*;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.service.mapper.model.*;
import com.pivot.aham.api.service.service.ExchangeRateService;
import com.pivot.aham.api.service.service.RedeemApplyService;
import com.pivot.aham.api.service.service.SaxoAccountOrderService;
import com.pivot.aham.api.service.service.UserDividendService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.CalDecimal;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.ExchangeRateTypeEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderActionTypeEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderTradeTypeEnum;
import com.pivot.aham.common.enums.analysis.VAOrderActionTypeEnum;
import com.pivot.aham.common.enums.analysis.VAOrderTradeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class GenCashActivity {

    @Resource
    private UserServiceRemoteService userServiceRemoteService;
    @Resource
    private SaxoAccountOrderService saxoAccountOrderService;
    @Resource
    private UserDividendService userDividendService;
    @Resource
    private RedeemApplyService redeemApplyService;
    @Resource
    private ExchangeRateService exchangeRateService;


    public CashActivityBean genCashActivity(AccountUserPO accountUserPO,Date startTime,Date endTime){
        log.info("生成月报-cashactivity:{}", JSON.toJSONString(accountUserPO));
        CashActivityBean cashActivityBean = new CashActivityBean();
        //获取开始时间和结束时间
//        Date lastMonth = DateUtils.addMonths(new Date(),-1);
//        Date startTime = DateUtils.monthStart(lastMonth);
//        Date endTime = DateUtils.monthEnd(lastMonth);

        //按client获取所有虚拟账户
        BankVirtualAccountDTO bankVirtualAccount = new BankVirtualAccountDTO();
        bankVirtualAccount.setClientId(accountUserPO.getClientId());
        List<BankVirtualAccountResDTO> bankVirtualAccountList = userServiceRemoteService.queryListBankVirtualAccount(bankVirtualAccount);

        log.info("生成月报-cashactivity,clientid:{},bankaccount记录:{}", accountUserPO.getClientId(),JSON.toJSONString(bankVirtualAccountList));
        CashActivityForSquirrelSaveBean cashActivityForSquirrelSaveBean =new CashActivityForSquirrelSaveBean();
        for(BankVirtualAccountResDTO bankVirtualAccountRes:bankVirtualAccountList){
            BankVirtualAccountOrderDTO bankVirtualAccountOrderDTO = new BankVirtualAccountOrderDTO();
            bankVirtualAccountOrderDTO.setVirtualAccountNo(bankVirtualAccountRes.getVirtualAccountNo());
            bankVirtualAccountOrderDTO.setStartTradeTime(startTime);
            bankVirtualAccountOrderDTO.setEndTradeTime(endTime);
            bankVirtualAccountOrderDTO.setActionTypes(Lists.newArrayList(VAOrderActionTypeEnum.REDEEM_EXCHANGE,VAOrderActionTypeEnum.SAXOTOUOB,
                    VAOrderActionTypeEnum.RECHARGE_EXCHANGE,VAOrderActionTypeEnum.RECHARGE,VAOrderActionTypeEnum.REFUND));
            List<BankVirtualAccountOrderResDTO> bankVirtualAccountOrderResDTOS = userServiceRemoteService.getByTradeTime(bankVirtualAccountOrderDTO);

            log.info("生成月报-cashactivity,clientId:{},BankVirtualAccountOrder记录:{}", accountUserPO.getClientId(),JSON.toJSONString(bankVirtualAccountOrderResDTOS));
            for(BankVirtualAccountOrderResDTO bankVirtualAccountOrder:bankVirtualAccountOrderResDTOS){
                CashActivityForSquirrelSavePO cashActivityForSquirrelSavePO = new CashActivityForSquirrelSavePO();

                if(bankVirtualAccountOrder.getActionType() == VAOrderActionTypeEnum.RECHARGE){
                    cashActivityForSquirrelSavePO.setActivityDesc("Deposit");
                }
                else if(bankVirtualAccountOrder.getActionType() == VAOrderActionTypeEnum.RECHARGE_EXCHANGE
                        && bankVirtualAccountOrder.getOperatorType() == VAOrderTradeTypeEnum.COME_INTO){
                    //通过bankno找到对应的出，计算费率
                    BankVirtualAccountOrderDTO bankVirtualAccountQuery = new BankVirtualAccountOrderDTO();
                    bankVirtualAccountQuery.setActionType(VAOrderActionTypeEnum.RECHARGE_EXCHANGE);
                    bankVirtualAccountQuery.setOperatorType(VAOrderTradeTypeEnum.COME_OUT);
                    bankVirtualAccountQuery.setBankOrderNo(bankVirtualAccountOrder.getBankOrderNo());
                    BankVirtualAccountOrderResDTO bankVirtualAccountOrderRes = userServiceRemoteService.queryVAOrder(bankVirtualAccountQuery);
                    BigDecimal fxr = bankVirtualAccountOrderRes.getCashAmount().divide(bankVirtualAccountOrder.getCashAmount(),6,BigDecimal.ROUND_DOWN);

                    BigDecimal amount = bankVirtualAccountOrder.getCashAmount().setScale(2,BigDecimal.ROUND_DOWN);

                    cashActivityForSquirrelSavePO.setActivityDesc("Currency conversion(SGD "+amount+"@ FX "+fxr+")");
                }

                else if(bankVirtualAccountOrder.getActionType() == VAOrderActionTypeEnum.SAXOTOUOB){
                    if(bankVirtualAccountOrder.getRedeemApplyId() != null) {
                        //根据redeemApplyId
                        RedeemApplyPO redeemApplyPO = redeemApplyService.queryById(bankVirtualAccountOrder.getRedeemApplyId());
                        cashActivityForSquirrelSavePO.setActivityDesc("Withdrawal:" + redeemApplyPO.getWithdrawalTargetType().getDesc());
                    }else{
                        cashActivityForSquirrelSavePO.setActivityDesc("Withdrawal");
                    }
                }
                else if(bankVirtualAccountOrder.getActionType() == VAOrderActionTypeEnum.REDEEM_EXCHANGE
                        && bankVirtualAccountOrder.getOperatorType() == VAOrderTradeTypeEnum.COME_INTO){

                    //通过redeem_id找到对应的出，计算费率
                    BankVirtualAccountOrderDTO bankVirtualAccountQuery = new BankVirtualAccountOrderDTO();
                    bankVirtualAccountQuery.setActionType(VAOrderActionTypeEnum.SAXOTOUOB);
                    bankVirtualAccountQuery.setOperatorType(VAOrderTradeTypeEnum.COME_INTO);
                    bankVirtualAccountQuery.setRedeemApplyId(bankVirtualAccountOrder.getRedeemApplyId());
                    BankVirtualAccountOrderResDTO bankVirtualAccountOrderRes = userServiceRemoteService.queryVAOrder(bankVirtualAccountQuery);
                    BigDecimal fxr =bankVirtualAccountOrder.getCashAmount().divide(bankVirtualAccountOrderRes.getCashAmount(),6,BigDecimal.ROUND_DOWN);

                    BigDecimal amount = bankVirtualAccountOrderRes.getCashAmount().setScale(2,BigDecimal.ROUND_DOWN);

                    cashActivityForSquirrelSavePO.setActivityDesc("Currency conversion(SGD "+amount+"@ FX "+fxr);
                }else{
                    continue;
                }
                if(bankVirtualAccountOrder.getCurrency() == CurrencyEnum.SGD){
                    cashActivityForSquirrelSavePO.setActivityAmountSgd(bankVirtualAccountOrder.getCashAmount());
                }else{
                    cashActivityForSquirrelSavePO.setActivityAmountUsd(bankVirtualAccountOrder.getCashAmount());
                }
                cashActivityForSquirrelSavePO.setActivityTime(bankVirtualAccountOrder.getTradeTime());
                cashActivityForSquirrelSavePO.setVirtualAccountNo(bankVirtualAccountOrder.getVirtualAccountNo());
                cashActivityForSquirrelSaveBean.getCashActivityForSquirrelSaveList().add(cashActivityForSquirrelSavePO);
            }
        }
//        if(cashActivityForSquirrelSaveBean.getCashActivityForSquirrelSaveList().size()>0) {
        cashActivityBean.setCashActivityForSquirrelSaveBean(cashActivityForSquirrelSaveBean);
//        }

        SaxoAccountOrderPO saxoAccountOrderQuery = new SaxoAccountOrderPO();
        saxoAccountOrderQuery.setClientId(accountUserPO.getClientId());
        saxoAccountOrderQuery.setStartTradeTime(startTime);
        saxoAccountOrderQuery.setEndTradeTime(endTime);
        saxoAccountOrderQuery.setActionTypes(Lists.newArrayList(SaxoOrderActionTypeEnum.REDEEM_EXCHANGE,SaxoOrderActionTypeEnum.REDEEM,
                SaxoOrderActionTypeEnum.RECHARGE_EXCHANGE,SaxoOrderActionTypeEnum.UOBTOSAXO,SaxoOrderActionTypeEnum.REFUND));
        List<SaxoAccountOrderPO> saxoAccountOrderList = saxoAccountOrderService.listSaxoAccountOrder(saxoAccountOrderQuery);

        //按goal分组
        Multimap<String,SaxoAccountOrderPO> saxoOrderMultimap = ArrayListMultimap.create();
        for(SaxoAccountOrderPO saxoAccountOrderPO:saxoAccountOrderList){
            saxoOrderMultimap.put(saxoAccountOrderPO.getGoalId(),saxoAccountOrderPO);
        }

        //用户分红
        UserDividendPO userDividendQuery = new UserDividendPO();
        userDividendQuery.setClientId(accountUserPO.getClientId());
        userDividendQuery.setStartDividendDate(startTime);
        userDividendQuery.setEndDividendDate(endTime);
        List<UserDividendPO> userDividendList = userDividendService.queryUserByTime(userDividendQuery);

        log.info("生成月报-cashactivity,clientId:{},userDividendList记录:{}",accountUserPO.getClientId(), JSON.toJSONString(userDividendList));
        Multimap<String,UserDividendPO> dividendMultimap = ArrayListMultimap.create();
        for(UserDividendPO userDividend:userDividendList){
            dividendMultimap.put(userDividend.getGoalId(),userDividend);
        }

//        Set<String> goals = saxoOrderMultimap.keySet();


        UserGoalInfoDTO userGoalInfoDTO = new UserGoalInfoDTO();
        userGoalInfoDTO.setClientId(accountUserPO.getClientId());
        RpcMessage<List<UserGoalInfoResDTO>> rpcMessageUserGoal
                = userServiceRemoteService.getUserGoalInfoList(userGoalInfoDTO);
        List<UserGoalInfoResDTO> userGoalInfoResDTOList = rpcMessageUserGoal.getContent();

        //按goal分组
        List<String> goals = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(userGoalInfoResDTOList)) {
            goals = Lists.transform(userGoalInfoResDTOList, new Function<UserGoalInfoResDTO, String>() {
                @Nullable
                @Override
                public String apply(@Nullable UserGoalInfoResDTO input) {
                    return input.getGoalId();
                }
            });
        }


        for(String goalId:goals){
            CashActivityForGoalBean cashActivityForGoalBean = new CashActivityForGoalBean();
            cashActivityForGoalBean.setGoalId(goalId);
            //查询goalName
            UserGoalInfoDTO userGoalInfoDTO1 = new UserGoalInfoDTO();
            userGoalInfoDTO1.setClientId(accountUserPO.getClientId());
            userGoalInfoDTO1.setGoalId(goalId);
            RpcMessage<UserGoalInfoResDTO> userGoalInfoRes = userServiceRemoteService.getUserGoalInfo(userGoalInfoDTO1);
            if(userGoalInfoRes.isSuccess()) {
                UserGoalInfoResDTO userGoal = userGoalInfoRes.getContent();
                cashActivityForGoalBean.setGoalName(userGoal.getGoalName());
            }else{
                cashActivityForGoalBean.setGoalName(accountUserPO.getGoalId());
            }
            List<CashActivityForGoalPO> cashActivityForGoalList = Lists.newArrayList();
            List<SaxoAccountOrderPO> saxoAccountOrderPOList = Lists.newArrayList(saxoOrderMultimap.get(goalId));
            List<UserDividendPO> userDividendPOList =  Lists.newArrayList(dividendMultimap.get(goalId));
            for(UserDividendPO userDividend:userDividendPOList){
                CashActivityForGoalPO cashActivityForGoalPO = new CashActivityForGoalPO();
                cashActivityForGoalPO.setActivityTime(userDividend.getDividendDate());
                cashActivityForGoalPO.setGoalId(userDividend.getGoalId());
                String desc = "Dividend received:"+userDividend.getProductCode();
                cashActivityForGoalPO.setActivityDesc(desc);

                //分红的sgd展示，用fxr的T2汇率
                ExchangeRatePO exchangeRateParam = new ExchangeRatePO();
                exchangeRateParam.setExchangeRateType(ExchangeRateTypeEnum.SAXO_FXRT2);
                ExchangeRatePO exchangeRateT2 = exchangeRateService.queryLastExchangeRate(exchangeRateParam);
                BigDecimal usdToSgd = BigDecimal.ZERO;
                if(exchangeRateT2 == null){
                    usdToSgd = exchangeRateT2.getUsdToSgd();
                }
                BigDecimal dividendSgd = usdToSgd.multiply(userDividend.getDividendAmount());
                cashActivityForGoalPO.setActivityAmountSgd(dividendSgd);
                cashActivityForGoalPO.setActivityAmountUsd(userDividend.getDividendAmount());

                cashActivityForGoalList.add(cashActivityForGoalPO);
            }

            for(SaxoAccountOrderPO saxoAccountOrderPO:saxoAccountOrderPOList){
                CashActivityForGoalPO cashActivityForGoalPO = new CashActivityForGoalPO();

                if(saxoAccountOrderPO.getActionType() == SaxoOrderActionTypeEnum.UOBTOSAXO){
                    cashActivityForGoalPO.setActivityDesc("Deposit");
                }
                else if(saxoAccountOrderPO.getActionType() == SaxoOrderActionTypeEnum.RECHARGE_EXCHANGE &&
                        saxoAccountOrderPO.getOperatorType() == SaxoOrderTradeTypeEnum.COME_INTO){
                    //根据bankno,找到对应的出，计算费率
                    SaxoAccountOrderPO saxoAccountOrderOutQuery = new SaxoAccountOrderPO();
                    saxoAccountOrderOutQuery.setActionType(SaxoOrderActionTypeEnum.RECHARGE_EXCHANGE);
                    saxoAccountOrderOutQuery.setBankOrderNo(saxoAccountOrderPO.getBankOrderNo());
                    saxoAccountOrderOutQuery.setOperatorType(SaxoOrderTradeTypeEnum.COME_OUT);
                    SaxoAccountOrderPO saxoAccountOrder = saxoAccountOrderService.selectOne(saxoAccountOrderOutQuery);
                    BigDecimal fxr = BigDecimal.ZERO;
                    if(saxoAccountOrderPO.getCashAmount().compareTo(BigDecimal.ZERO)>0) {
                        fxr = saxoAccountOrder.getCashAmount().divide(saxoAccountOrderPO.getCashAmount(), 6, BigDecimal.ROUND_DOWN);
                    }

                    BigDecimal amount = saxoAccountOrder.getCashAmount().setScale(2,BigDecimal.ROUND_DOWN);

                    cashActivityForGoalPO.setActivityDesc("Currency conversion(SGD "+amount+"@ FX "+fxr+")");
                }
                else if(saxoAccountOrderPO.getActionType() == SaxoOrderActionTypeEnum.REDEEM){
                    cashActivityForGoalPO.setActivityDesc("Withdrawal");
                }
                else if(saxoAccountOrderPO.getActionType() == SaxoOrderActionTypeEnum.REDEEM_EXCHANGE &&
                        saxoAccountOrderPO.getOperatorType() == SaxoOrderTradeTypeEnum.COME_INTO){
                    SaxoAccountOrderPO saxoAccountOrderOutQuery = new SaxoAccountOrderPO();
                    saxoAccountOrderOutQuery.setActionType(SaxoOrderActionTypeEnum.REDEEM_EXCHANGE);
                    saxoAccountOrderOutQuery.setRedeemApplyId(saxoAccountOrderPO.getRedeemApplyId());
                    saxoAccountOrderOutQuery.setOperatorType(SaxoOrderTradeTypeEnum.COME_OUT);
                    SaxoAccountOrderPO saxoAccountOrder = saxoAccountOrderService.selectOne(saxoAccountOrderOutQuery);
                    BigDecimal fxr=BigDecimal.ZERO;
                    if(saxoAccountOrder.getCashAmount().compareTo(BigDecimal.ZERO)>0) {
                        fxr = saxoAccountOrderPO.getCashAmount().divide(saxoAccountOrder.getCashAmount(), 6, BigDecimal.ROUND_DOWN);
                    }

                    BigDecimal amount = saxoAccountOrder.getCashAmount().setScale(2,BigDecimal.ROUND_DOWN);

                    cashActivityForGoalPO.setActivityDesc("Currency conversion(USD "+amount+"@ FX "+fxr+")");
                }else{
                    continue;
                }
                if(saxoAccountOrderPO.getCurrency() == CurrencyEnum.SGD){
                    cashActivityForGoalPO.setActivityAmountSgd(saxoAccountOrderPO.getCashAmount());
                }else{
                    cashActivityForGoalPO.setActivityAmountUsd(saxoAccountOrderPO.getCashAmount());
                }
                cashActivityForGoalPO.setGoalId(goalId);
                cashActivityForGoalPO.setActivityTime(saxoAccountOrderPO.getTradeTime());
                cashActivityForGoalList.add(cashActivityForGoalPO);
            }


            log.info("生成月报-cashactivity,clientId:{},cashActivityForGoalList记录:{}",accountUserPO.getClientId(),JSON.toJSONString(cashActivityForGoalList));
            cashActivityForGoalBean.setCashActivityForGoalList(cashActivityForGoalList);
            cashActivityBean.getCashActivityForGoalBeanList().add(cashActivityForGoalBean);
        }

        //统一处理小数位
        for(CashActivityForGoalBean cashActivityForGoalBean:cashActivityBean.getCashActivityForGoalBeanList()){
            for(CashActivityForGoalPO cashActivityForGoalPO:cashActivityForGoalBean.getCashActivityForGoalList()){
                CalDecimal<CashActivityForGoalPO> calDecimal = new CalDecimal<>();
                calDecimal.handleDot(cashActivityForGoalPO);
            }

        }
        for(CashActivityForSquirrelSavePO cashActivityForSquirrelSavePO:cashActivityBean.getCashActivityForSquirrelSaveBean().getCashActivityForSquirrelSaveList()) {
            CalDecimal<CashActivityForSquirrelSavePO> calDecimal = new CalDecimal<>();
            calDecimal.handleDot(cashActivityForSquirrelSavePO);
        }

        log.info("生成月报-cashactivity,clientId:{},cashActivityBean记录:{}",accountUserPO.getClientId(),JSON.toJSONString(cashActivityBean));
        return cashActivityBean;
    }
}
