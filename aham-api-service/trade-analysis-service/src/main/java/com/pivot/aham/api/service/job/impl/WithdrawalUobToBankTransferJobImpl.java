package com.pivot.aham.api.service.job.impl;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.*;
import com.pivot.aham.api.server.dto.req.UobExchangeReq;
import com.pivot.aham.api.server.dto.req.UobTransferReq;
import com.pivot.aham.api.server.dto.resp.UobExchangeResult;
import com.pivot.aham.api.server.dto.resp.UobTransferResult;
import com.pivot.aham.api.server.remoteservice.UobTradeRemoteService;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.service.job.WithdrawalUobToBankTransferJob;
import com.pivot.aham.api.service.mapper.model.RedeemApplyPO;
import com.pivot.aham.api.service.service.RedeemApplyService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.ExchangeTypeEnum;
import com.pivot.aham.common.enums.analysis.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 处理用户提现划款
 *
 * @author addison
 * @since 2018年12月06日
 */
@ElasticJobConf(name = "WithdrawalUobToBankTransferJob_2",
cron = "0 30 16 * * ?",
shardingItemParameters = "0=1",
shardingTotalCount=1,
description = "交易06_交易分析#UOB购汇下单和提现到银行卡",eventTraceRdbDataSource = "dataSource")
@Slf4j
public class WithdrawalUobToBankTransferJobImpl implements SimpleJob,WithdrawalUobToBankTransferJob {
    @Autowired
    private RedeemApplyService bankVARedeemService;
    @Resource
    private UobTradeRemoteService uobTradeRemoteService;
    @Resource
    private UserServiceRemoteService userServiceRemoteService;

    @Override
    public void withdrawalUobToBankTransfer() {
        //查询提现申请单(saxo转账成功的)
        RedeemApplyPO vaRedeemApplyPO = new RedeemApplyPO();
        //saox到uob已经申请成功
        vaRedeemApplyPO.setSaxoToUobTransferStatus(SaxoToUobTransferStatusEnum.APPLYSUCCESS);
        vaRedeemApplyPO.setRedeemApplyStatus(RedeemApplyStatusEnum.HANDLING);
        List<RedeemApplyPO> vaRedeemApplyList = bankVARedeemService.queryList(vaRedeemApplyPO);
        if(CollectionUtils.isEmpty(vaRedeemApplyList)){
            log.info("没有需要转账的提现申请单");
        }

        for(RedeemApplyPO vaRedeemApply : vaRedeemApplyList) {
            //获取这个用户的虚拟账户
            BankVirtualAccountDTO bankVirtualAccountDTO = new BankVirtualAccountDTO();
            bankVirtualAccountDTO.setClientId(vaRedeemApply.getClientId());
            bankVirtualAccountDTO.setCurrency(vaRedeemApply.getSourceAccountType());
            List<BankVirtualAccountResDTO> bankVirtualAccountDTOList = userServiceRemoteService.queryListBankVirtualAccount(bankVirtualAccountDTO);

            if(CollectionUtils.isEmpty(bankVirtualAccountDTOList)){
                log.error("未找到对应币种的虚拟账户:{}",JSON.toJSONString(vaRedeemApply));
                continue;
            }
            BankVirtualAccountResDTO bankVirtualAccount = bankVirtualAccountDTOList.get(0);

            //从虚拟账户或从投资账户且申请成功且提现到银行卡
            Boolean needRedeemToBankCard =
            vaRedeemApply.getWithdrawalSourceType() == WithdrawalSourceTypeEnum.FROMVIRTUALACCOUNT
            || (vaRedeemApply.getWithdrawalSourceType() == WithdrawalSourceTypeEnum.FROMGOAL
            && SaxoToUobTransferStatusEnum.APPLYSUCCESS == vaRedeemApply.getSaxoToUobTransferStatus()
            && vaRedeemApply.getWithdrawalTargetType() == WithdrawalTargetTypeEnum.BankAccount);

            //如果源账户和目标货币不相等就进行购汇
            Boolean needExchangeflag = vaRedeemApply.getSourceAccountType() != vaRedeemApply.getTargetCurrency()
            && vaRedeemApply.getBankTransferStatus() == BankTransferStatusEnum.NOTEXCHANGE;
            if(needExchangeflag){
                //幂等校验-购汇的出
                if (idempotentCheck(vaRedeemApply, VAOrderActionTypeEnum.REDEEM_EXCHANGE,VAOrderTradeTypeEnum.COME_OUT)) return;

                Long orderId = Sequence.next();
                UobExchangeReq uobExchangeReq = new UobExchangeReq();
                uobExchangeReq.setOutBusinessId(orderId);
                //如果是从虚拟账户提现
                BigDecimal redeemMoney = BigDecimal.ZERO;
                if(vaRedeemApply.getWithdrawalSourceType() == WithdrawalSourceTypeEnum.FROMVIRTUALACCOUNT){
                    uobExchangeReq.setExchangeAmount(vaRedeemApply.getApplyMoney());
                    redeemMoney = vaRedeemApply.getApplyMoney();
                }else{
                    uobExchangeReq.setExchangeAmount(vaRedeemApply.getConfirmAmountInSgd());
                    redeemMoney = vaRedeemApply.getConfirmAmountInSgd();
                }

                if(vaRedeemApply.getSourceAccountType() == CurrencyEnum.SGD){
                    uobExchangeReq.setExchangeType(ExchangeTypeEnum.SGD_USD);
                }else{
                    uobExchangeReq.setExchangeType(ExchangeTypeEnum.USD_SGD);
                }
                RpcMessage<UobExchangeResult> msg = uobTradeRemoteService.exchangeForWithdraw(uobExchangeReq);
                log.info("购汇远程调用,返回内容{}", JSON.toJSONString(msg));
                if(RpcMessage.isSuccess(msg)) {
                    vaRedeemApply.setBankTransferStatus(BankTransferStatusEnum.HASEXCHANGE);
                    bankVARedeemService.updateOrInsert(vaRedeemApply);

                    //增加银行虚拟账户资产
                    BankVirtualAccountOrderDTO bankVirtualAccountOrder = new BankVirtualAccountOrderDTO();
                    bankVirtualAccountOrder.setReferenceCode("")
                    .setRedeemApplyId(vaRedeemApply.getId())
                    .setVirtualAccountNo(bankVirtualAccount.getVirtualAccountNo())
                    .setCashAmount(redeemMoney)
                    .setCurrency(bankVirtualAccount.getCurrency())
                    .setOperatorType(VAOrderTradeTypeEnum.COME_OUT)
                    .setActionType(VAOrderActionTypeEnum.REDEEM_EXCHANGE)
                    .setBankOrderNo("")
                    .setOrderStatus(VAOrderTradeStatusEnum.HANDLING)
                    .setNeedRefundType(NeedRefundTypeEnum.UN_REFUND)
                    .setTradeTime(DateUtils.now())
                    .setCreateTime(DateUtils.now())
                    .setUpdateTime(DateUtils.now())
                    .setId(Sequence.next());

                    List<BankVirtualAccountOrderDTO> vAOrderList = Lists.newArrayList();
                    vAOrderList.add(bankVirtualAccountOrder);
                    userServiceRemoteService.saveOrdersAndUpdateAccount(vAOrderList,vaRedeemApply.getClientId());
                }
                continue;
            }

            if(!needRedeemToBankCard){
                //查询uob的是否到账接口，目前直接mock成功
                vaRedeemApply.setConfirmTime(DateUtils.now());
                vaRedeemApply.setRedeemApplyStatus(RedeemApplyStatusEnum.SUCCESS);
                bankVARedeemService.updateOrInsert(vaRedeemApply);
                continue;
            }

            if(vaRedeemApply.getSourceAccountType() == vaRedeemApply.getTargetCurrency()) {
                //幂等校验
                if (idempotentCheck(vaRedeemApply,VAOrderActionTypeEnum.REDEEM,VAOrderTradeTypeEnum.COME_OUT)) return;

                //====虚拟账户提现======
                Long orderId = Sequence.next();
                String bankCard = vaRedeemApply.getBankAccountNo();
                BigDecimal redeemMoney = BigDecimal.ZERO;
                if(vaRedeemApply.getWithdrawalSourceType() == WithdrawalSourceTypeEnum.FROMVIRTUALACCOUNT){
                    redeemMoney = vaRedeemApply.getConfirmAmount();
                }else{
                    redeemMoney = vaRedeemApply.getConfirmAmountInSgd();
                }

                UserInfoResDTO userInfoDTO = userServiceRemoteService.queryByClientId(vaRedeemApply.getClientId());
                UobTransferReq uobTransferReq = new UobTransferReq();
                uobTransferReq.setOutBusinessId(orderId);
                uobTransferReq.setBankAccountNumber(bankCard);
                uobTransferReq.setBankName(vaRedeemApply.getBankName());
                uobTransferReq.setBankUserName(userInfoDTO.getClientName());
                uobTransferReq.setSwiftCode(vaRedeemApply.getSwift());
                uobTransferReq.setBranchCode(vaRedeemApply.getBranch());
                uobTransferReq.setCurrency(vaRedeemApply.getTargetCurrency());
                uobTransferReq.setAmount(redeemMoney);
                uobTransferReq.setBankAddress(vaRedeemApply.getBankAddress());

                RpcMessage<UobTransferResult> msg = uobTradeRemoteService.withdrawToBankCard(uobTransferReq);
                Long bankOrderId = msg.getContent().getOrderId();
                if(bankOrderId != null) {
                    vaRedeemApply.setBankTransferStatus(BankTransferStatusEnum.SEND_SUCCESS);
                    vaRedeemApply.setBankTransferOrderId(bankOrderId);
//                    bankVARedeemService.updateOrInsert(vaRedeemApply);
                }
                //查询uob的是否到账接口，目前直接mock成功
                vaRedeemApply.setBankTransferStatus(BankTransferStatusEnum.CORRECT_ARRIVAL);
                vaRedeemApply.setRedeemApplyStatus(RedeemApplyStatusEnum.SUCCESS);
                vaRedeemApply.setConfirmTime(DateUtils.now());
                bankVARedeemService.updateOrInsert(vaRedeemApply);

                //增加银行虚拟账户资产
                BankVirtualAccountOrderDTO bankVirtualAccountOrder = new BankVirtualAccountOrderDTO();
                bankVirtualAccountOrder.setReferenceCode("")
                .setRedeemApplyId(vaRedeemApply.getId())
                .setVirtualAccountNo(bankVirtualAccount.getVirtualAccountNo())
                .setCashAmount(redeemMoney)
                .setCurrency(bankVirtualAccount.getCurrency())
                .setOperatorType(VAOrderTradeTypeEnum.COME_OUT)
                .setActionType(VAOrderActionTypeEnum.REDEEM)
                .setBankOrderNo("")
                .setOrderStatus(VAOrderTradeStatusEnum.SUCCESS)
                .setNeedRefundType(NeedRefundTypeEnum.UN_REFUND)
                .setTradeTime(DateUtils.now())
                .setCreateTime(DateUtils.now())
                .setUpdateTime(DateUtils.now())
                .setId(Sequence.next());
                List<BankVirtualAccountOrderDTO> vAOrderList = Lists.newArrayList();
                vAOrderList.add(bankVirtualAccountOrder);
                userServiceRemoteService.saveOrdersAndUpdateAccount(vAOrderList,vaRedeemApply.getClientId());
            }
        }
    }

    private boolean idempotentCheck(RedeemApplyPO vaRedeemApply,
                                    VAOrderActionTypeEnum vaOrderActionTypeEnum,
                                    VAOrderTradeTypeEnum operatorType) {
        BankVirtualAccountOrderDTO bankVirtualAccountOrderQuery = new BankVirtualAccountOrderDTO();
        bankVirtualAccountOrderQuery.setRedeemApplyId(vaRedeemApply.getId());
        bankVirtualAccountOrderQuery.setActionType(vaOrderActionTypeEnum);
        bankVirtualAccountOrderQuery.setOperatorType(operatorType);
        bankVirtualAccountOrderQuery.setOrderStatus(VAOrderTradeStatusEnum.SUCCESS);
        RpcMessage<List<BankVirtualAccountOrderResDTO>> rpcMessage = userServiceRemoteService.listBankVirtualAccountOrders(bankVirtualAccountOrderQuery);

        if (!rpcMessage.isSuccess()) {
            log.error("幂等查询异常");
            return true;
        }
        List<BankVirtualAccountOrderResDTO> bankVirtualAccountOrderResList = rpcMessage.getContent();
        if (CollectionUtils.isNotEmpty(bankVirtualAccountOrderResList)) {
            log.error("提现单号:{},已经处理过了", vaRedeemApply.getId());
            return true;
        }
        return false;
    }

    /**
     * 需要购汇的提现的回调处理
     * @param param
     */
    public void handlerExchangeCallBack(UobExchangeCallbackDTO param){
        BankVirtualAccountOrderResDTO virtualAccountOrderResDTO = userServiceRemoteService.queryById(param.getOrderNo());
        if (null == virtualAccountOrderResDTO) {
            throw new BusinessException("该购汇单查不到"+param.getOrderNo());
        }
        RedeemApplyPO vaRedeemApply = bankVARedeemService.queryById(virtualAccountOrderResDTO.getRedeemApplyId());
        //幂等校验
        if (idempotentCheck(vaRedeemApply,VAOrderActionTypeEnum.REDEEM_EXCHANGE,VAOrderTradeTypeEnum.COME_INTO)) return;

        vaRedeemApply.setExchangeAmount(param.getConfirmMoney());
        //从虚拟账户或从投资账户且申请成功且提现到银行卡
        Boolean needRedeemToBankCard =
        vaRedeemApply.getWithdrawalSourceType() == WithdrawalSourceTypeEnum.FROMVIRTUALACCOUNT
        || (vaRedeemApply.getWithdrawalSourceType() == WithdrawalSourceTypeEnum.FROMGOAL
        && SaxoToUobTransferStatusEnum.APPLYSUCCESS == vaRedeemApply.getSaxoToUobTransferStatus()
        && vaRedeemApply.getWithdrawalTargetType() == WithdrawalTargetTypeEnum.BankAccount);


        //获取这个用户的虚拟账户
        BankVirtualAccountDTO bankVirtualAccountDTO = new BankVirtualAccountDTO();
        bankVirtualAccountDTO.setClientId(vaRedeemApply.getClientId());
        bankVirtualAccountDTO.setCurrency(vaRedeemApply.getTargetCurrency());
        List<BankVirtualAccountResDTO> bankVirtualAccountDTOList = userServiceRemoteService.queryListBankVirtualAccount(bankVirtualAccountDTO);

        if(CollectionUtils.isEmpty(bankVirtualAccountDTOList)){
            throw new BusinessException("未找到对应币种的虚拟账户");
        }
        BankVirtualAccountResDTO bankVirtualAccount = bankVirtualAccountDTOList.get(0);

        if(needRedeemToBankCard){
            //====虚拟账户提现======
            Long orderId = Sequence.next();
            String bankCard = vaRedeemApply.getBankAccountNo();
            BigDecimal redeemMoney = param.getConfirmMoney();

            //获取用户姓名
            UserInfoResDTO userInfoDTO = userServiceRemoteService.queryByClientId(vaRedeemApply.getClientId());
            UobTransferReq uobTransferReq = new UobTransferReq();
            uobTransferReq.setOutBusinessId(orderId);
            uobTransferReq.setBankAccountNumber(bankCard);
            uobTransferReq.setBankName(vaRedeemApply.getBankName());
            uobTransferReq.setBankUserName(userInfoDTO.getClientName());
            uobTransferReq.setSwiftCode(vaRedeemApply.getSwift());
            uobTransferReq.setBranchCode(vaRedeemApply.getBranch());
            uobTransferReq.setCurrency(vaRedeemApply.getTargetCurrency());
            uobTransferReq.setAmount(redeemMoney);

            RpcMessage<UobTransferResult> msg = uobTradeRemoteService.withdrawToBankCard(uobTransferReq);
            Long bankOrderId = msg.getContent().getOrderId();
            if(bankOrderId != null) {
                vaRedeemApply.setBankTransferStatus(BankTransferStatusEnum.SEND_SUCCESS);
                vaRedeemApply.setBankTransferOrderId(bankOrderId);
                bankVARedeemService.updateOrInsert(vaRedeemApply);
            }
            //查询uob的是否到账接口，目前直接mock成功
            vaRedeemApply.setBankTransferStatus(BankTransferStatusEnum.CORRECT_ARRIVAL);
            vaRedeemApply.setConfirmTime(DateUtils.now());
            vaRedeemApply.setRedeemApplyStatus(RedeemApplyStatusEnum.SUCCESS);
            bankVARedeemService.updateOrInsert(vaRedeemApply);

            //修改在途记录
            BankVirtualAccountOrderDTO bankVirtualAccountOrderDTO = new BankVirtualAccountOrderDTO();
            bankVirtualAccountOrderDTO.setId(virtualAccountOrderResDTO.getId());
            bankVirtualAccountOrderDTO.setOrderStatus(VAOrderTradeStatusEnum.SUCCESS);
            userServiceRemoteService.updateVAOrder(bankVirtualAccountOrderDTO);

            List<BankVirtualAccountOrderDTO> vAOrderList = Lists.newArrayList();
            //目标币种购汇的进
            BankVirtualAccountOrderDTO bankVirtualAccountOrder1 = new BankVirtualAccountOrderDTO();
            bankVirtualAccountOrder1.setReferenceCode("")
            .setRedeemApplyId(vaRedeemApply.getId())
            .setVirtualAccountNo(bankVirtualAccount.getVirtualAccountNo())
            .setCashAmount(param.getConfirmMoney())
            .setCurrency(bankVirtualAccount.getCurrency())
            .setOperatorType(VAOrderTradeTypeEnum.COME_INTO)
            .setActionType(VAOrderActionTypeEnum.REDEEM_EXCHANGE)
            .setBankOrderNo("")
            .setOrderStatus(VAOrderTradeStatusEnum.SUCCESS)
            .setNeedRefundType(NeedRefundTypeEnum.UN_REFUND)
            .setTradeTime(DateUtils.now())
            .setCreateTime(DateUtils.now())
            .setUpdateTime(DateUtils.now())
            .setId(Sequence.next());
            vAOrderList.add(bankVirtualAccountOrder1);

            //目标币种提现的出
            BankVirtualAccountOrderDTO bankVirtualAccountOrder = new BankVirtualAccountOrderDTO();
            bankVirtualAccountOrder.setReferenceCode("")
            .setRedeemApplyId(vaRedeemApply.getId())
            .setVirtualAccountNo(bankVirtualAccount.getVirtualAccountNo())
            .setCashAmount(param.getConfirmMoney())
            .setCurrency(bankVirtualAccount.getCurrency())
            .setOperatorType(VAOrderTradeTypeEnum.COME_OUT)
            .setActionType(VAOrderActionTypeEnum.REDEEM)
            .setBankOrderNo("")
            .setOrderStatus(VAOrderTradeStatusEnum.SUCCESS)
            .setNeedRefundType(NeedRefundTypeEnum.UN_REFUND)
            .setTradeTime(DateUtils.now())
            .setCreateTime(DateUtils.now())
            .setUpdateTime(DateUtils.now())
            .setId(Sequence.next());
            vAOrderList.add(bankVirtualAccountOrder);
            userServiceRemoteService.saveOrdersAndUpdateAccount(vAOrderList,vaRedeemApply.getClientId());

        }else{
            //查询uob的是否到账接口，目前直接mock成功
            vaRedeemApply.setConfirmTime(DateUtils.now());
            vaRedeemApply.setRedeemApplyStatus(RedeemApplyStatusEnum.SUCCESS);
            bankVARedeemService.updateOrInsert(vaRedeemApply);
            //修改在途记录
            BankVirtualAccountOrderDTO bankVirtualAccountOrderDTO = new BankVirtualAccountOrderDTO();
            bankVirtualAccountOrderDTO.setId(virtualAccountOrderResDTO.getId());
            bankVirtualAccountOrderDTO.setOrderStatus(VAOrderTradeStatusEnum.SUCCESS);
            userServiceRemoteService.updateVAOrder(bankVirtualAccountOrderDTO);

            //增加银行虚拟账户资产
            BankVirtualAccountOrderDTO bankVirtualAccountOrder1 = new BankVirtualAccountOrderDTO();
            bankVirtualAccountOrder1.setReferenceCode("")
            .setRedeemApplyId(vaRedeemApply.getId())
            .setVirtualAccountNo(bankVirtualAccount.getVirtualAccountNo())
            .setCashAmount(param.getConfirmMoney())
            .setCurrency(bankVirtualAccount.getCurrency())
            .setOperatorType(VAOrderTradeTypeEnum.COME_INTO)
            .setActionType(VAOrderActionTypeEnum.REDEEM_EXCHANGE)
            .setBankOrderNo("")
            .setOrderStatus(VAOrderTradeStatusEnum.SUCCESS)
            .setNeedRefundType(NeedRefundTypeEnum.UN_REFUND)
            .setTradeTime(DateUtils.now())
            .setCreateTime(DateUtils.now())
            .setUpdateTime(DateUtils.now())
            .setId(Sequence.next());
            List<BankVirtualAccountOrderDTO> vAOrderList1 = Lists.newArrayList();
            vAOrderList1.add(bankVirtualAccountOrder1);
            userServiceRemoteService.saveOrdersAndUpdateAccount(vAOrderList1,vaRedeemApply.getClientId());
        }

    }

   @Override
    public void execute(ShardingContext shardingContext) {
        try{
         //   withdrawalUobToBankTransfer();
        }catch (Exception e){
            ErrorLogAndMailUtil.logError(log,e);
        }
    }
}
