package com.pivot.aham.api.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.UobExchangeDTO;
import com.pivot.aham.api.server.dto.req.ReceivedTransferReq;
import com.pivot.aham.api.server.dto.req.UobExchangeReq;
import com.pivot.aham.api.server.dto.res.AccountInfoResDTO;
import com.pivot.aham.api.server.dto.res.AccountRechargeResDTO;
import com.pivot.aham.api.server.dto.resp.ReceivedTransferItem;
import com.pivot.aham.api.server.dto.resp.ReceivedTransferResult;
import com.pivot.aham.api.server.dto.resp.UobExchangeResult;
import com.pivot.aham.api.server.mq.message.RechargeRefundDTO;
import com.pivot.aham.api.server.mq.message.RefundMessageDTO;
import com.pivot.aham.api.server.remoteservice.AccountInfoRemoteService;
import com.pivot.aham.api.server.remoteservice.RechargeServiceRemoteService;
import com.pivot.aham.api.server.remoteservice.UobTradeRemoteService;
import com.pivot.aham.api.service.bean.GoalSetMoneyBean;
import com.pivot.aham.api.service.bean.RechargeRefundBean;
import com.pivot.aham.api.service.service.*;
import com.pivot.aham.api.service.mapper.model.BankNameAlias;
import com.pivot.aham.api.service.mapper.model.BankVirtualAccount;
import com.pivot.aham.api.service.mapper.model.BankVirtualAccountOrder;
import com.pivot.aham.api.service.mapper.model.BankVirtualAccountOrderMsgPO;
import com.pivot.aham.api.service.mapper.model.UserGoalInfoPO;
import com.pivot.aham.api.service.mapper.model.UserRechargeStatus;
import com.pivot.aham.common.core.util.*;
import com.pivot.aham.common.enums.*;
import com.pivot.aham.common.enums.analysis.*;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.support.context.ApplicationContextHolder;
import com.pivot.aham.common.core.support.email.Email;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.enums.recharge.TpcfStatusEnum;
import com.pivot.aham.common.enums.recharge.UobRechargeStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.pivot.aham.common.core.Constants.MqConstants.REFUND_EXCHANGE;
import static com.pivot.aham.common.core.Constants.MqConstants.REFUND_MESSAGE_PREFIX;

import com.pivot.aham.common.enums.recharge.UserRechargeStatusEnum;

/**
 * Created by luyang.li on 2018/12/24.
 */
@Service
@Slf4j
public class UobRechargeServiceImpl implements UobRechargeService {

    /**
     * CMS邮件模板
     */
    private static final String EMAIL_TITLE_TEM = "【{0}】环境 用户ReferenceCode不一致";
    private static final String EMAIL_TITLE_CLIENTNAME = "【{0}】环境 用户充值,退款通知";

    @Resource
    private UobTradeRemoteService uobTradeRemoteService;
    @Resource
    private BankVirtualAccountOrderService bankVirtualAccountOrderService;
    @Resource
    private BankVirtualAccountService bankVirtualAccountService;
    @Resource
    private UserGoalInfoInfoService userGoalInfoInfoService;
    @Resource
    private BankVirtualAccountOrderMsgService bankVirtualAccountOrderMsgService;
    @Resource
    private RabbitTemplate pubConfirmTemplate;
    @Resource
    private BankNameAliasService bankNameAliasService;
    @Resource
    private UserRechargeStatusService userRechargeStatusService;
    @Resource
    private RechargeServiceRemoteService rechargeServiceRemoteService;
    @Resource
    private AccountInfoRemoteService accountInfoRemoteService;



//    private static String REFUND_MESSAGE_PREFIX = "bankRefund#";
//    private static String REFUND_EXCHANGE = "rabbitmq.refund_exchange";

    /**
     * * 用户线下充值之后的任务同步从UOB进行银行卡到UOB的充值流水的同步:银行卡 --> UOB
     * * <p>
     * * 1、记录银行流水:
     * * 2、检查UOB给的virtual_account_id 和 client_name是否一致 需要退款的用户邮件通知
     * * 3、记录虚拟流水:t_bank_virtual_account_order
     * * ==2.1、带了referenceCode表示已经选择了goal,流水状态为完成 + 处理中(完成指线下转账完成,处理中指冻结,合并转账完成之后状态修改为完成)。
     * * ==eg:USD带referenceCode:记录一条入金完成记录(USD),记录一条处理中(冻结)记录,合并转账完成之后修改处理中为转出
     * * ==eg:SGD带referenceCode:记录一条入金完成(SGD),购汇成USD记录一条SGD购汇支出,一条USD购汇收入,一条USD处理中(冻结),合并转账完成之后修改处理中为转出
     * * ==2.2、没有带referenceCode表示单纯的充值,流水状态为为完成。
     * * ==eg:USD或者SGD不带referenceCode:只是记录相应的币种上的一条入金完成。
     * * ==2.3、clientName 不等的需要退款
     * * <p>
     * * 4、重新计算现金虚拟账户:t_bank_virtual_account
     * * 5、用户是否有设置referenceCode,
     * * ## 5.1、有 --> 检查goal,有就进行资产充值,没有goal就报警
     * * ## 5.2、无 --> 无剩余业务
     * * 6、处理完毕之后回调修改saxo 的充值单状态为完成
     */
    @Override
    public void syncUobRechargeToVirtualAccount() {
        //获取UOB线下转账记录
        List<ReceivedTransferItem> transferLogList = getUobRecharges();
        if (CollectionUtils.isEmpty(transferLogList)) {
            log.info("处理用户入金,查询UOB接口没有返回数据。日期:{}", DateUtils.getDate(DateUtils.DATE_TIME_FORMAT));
            return;
        }
        List<RechargeRefundBean> needRefundUsers = Lists.newArrayList();
        List<ReceivedTransferItem> transferErrorUsers = Lists.newArrayList();
        List<ReceivedTransferItem> transferSuccessItem = Lists.newArrayList();
        for (ReceivedTransferItem receivedTransfer : transferLogList) {
            try {
                //1.幂等校验
                BankVirtualAccountOrder bankVirtualAccountOrderParam = new BankVirtualAccountOrder();
                bankVirtualAccountOrderParam.setBankOrderNo(receivedTransfer.getBankOrderNo());
                List<BankVirtualAccountOrder> bankVirtualAccountOrders = bankVirtualAccountOrderService.listBankVirtualAccountOrders(bankVirtualAccountOrderParam);
                if (CollectionUtils.isNotEmpty(bankVirtualAccountOrders)) {
                    log.info("充值同步请求参数item:{},已经处理过。", JSON.toJSON(receivedTransfer));
                    transferSuccessItem.add(receivedTransfer);
                    continue;
                }
                //2.clientName检查
                BankVirtualAccount queryParam = new BankVirtualAccount();
                queryParam.setVirtualAccountNo(receivedTransfer.getVirtualAccountNo());
                BankVirtualAccount bankVirtualAccount = bankVirtualAccountService.quaryBankVirtualAccount(queryParam);
                //boolean isNeedRefund = null == bankVirtualAccount || !bankVirtualAccount.getClientName().trim().equalsIgnoreCase(receivedTransfer.getClientName().trim());
                
                //Added by WooiTatt
                boolean unMatchName = null == bankVirtualAccount || !bankVirtualAccount.getClientName().trim().equalsIgnoreCase(receivedTransfer.getClientName().trim());
                if (unMatchName) {
                    List<BankNameAlias> lBankNameAliasApprove = Lists.newArrayList();
                    
                    BankNameAlias bankNameAlias = new BankNameAlias();
                    bankNameAlias.setVirtualAccountNo(bankVirtualAccount.getVirtualAccountNo());
                    bankNameAlias.setStatus(NameAliasEnum.APPROVE);
                    lBankNameAliasApprove = bankNameAliasService.queryListBankNameAliasByVirtualAccount(bankNameAlias);
                    boolean getApproval = false;
                    boolean getRejection = false;
                    
                    //Check on NameAlias Approval
                    if(lBankNameAliasApprove != null && lBankNameAliasApprove.size() > 0){
                        for(BankNameAlias oBankNameAlias : lBankNameAliasApprove ){
                            if(oBankNameAlias.getBankClientName().equalsIgnoreCase(receivedTransfer.getClientName())){
                                //3.处理正常的订单
                                handelUobRecharge(receivedTransfer, bankVirtualAccount.getClientId());
                                //6、记录充值成功的订单回调
                                transferSuccessItem.add(receivedTransfer);
                                getApproval = true;
                                break;
                            }
                        }
                    }
                    
                    //Check on NameAlias Rejected
                    if(!getApproval){
                        List<BankNameAlias> lBankNameAliasRejected = Lists.newArrayList();
                        BankNameAlias bankNameAliasRejected = new BankNameAlias();
                        bankNameAliasRejected.setVirtualAccountNo(bankVirtualAccount.getVirtualAccountNo());
                        bankNameAliasRejected.setStatus(NameAliasEnum.REJECTED);
                        lBankNameAliasRejected = bankNameAliasService.queryListBankNameAliasByVirtualAccount(bankNameAliasRejected);
                        if(lBankNameAliasRejected != null && lBankNameAliasRejected.size() > 0){
                            for(BankNameAlias oBankNameAlias : lBankNameAliasRejected ){
                                 if(oBankNameAlias.getRechargeId().equalsIgnoreCase(String.valueOf(receivedTransfer.getId()))){
                                    log.error("从UOB同步充值记录,姓名对不上需要退款transferLog:{}", receivedTransfer.getVirtualAccountNo(), JSON.toJSON(receivedTransfer));
                                    RechargeRefundBean rechargeRefund = constractRechargeRefund(receivedTransfer, bankVirtualAccount);
                                    needRefundUsers.add(rechargeRefund);
                                    handleRefundVirtualAccount(receivedTransfer);
                                    transferSuccessItem.add(receivedTransfer);
                                    getRejection = true;
                                    break;
                                 }
                            }
                        }
                    }
                    
                    //Check on NameAlias Pending
                    if(!getApproval && !getRejection){
                        List<BankNameAlias> lBankNameAliasPending = Lists.newArrayList();
                        BankNameAlias oBankNameAlias = new BankNameAlias();
                        oBankNameAlias.setVirtualAccountNo(bankVirtualAccount.getVirtualAccountNo());
                        oBankNameAlias.setStatus(NameAliasEnum.PENDING);
                        lBankNameAliasPending = bankNameAliasService.queryListBankNameAliasByVirtualAccount(oBankNameAlias);
                        boolean duplicate = false;
                        
                        if(lBankNameAliasPending != null && lBankNameAliasPending.size() > 0){
                            for(BankNameAlias lBankNameAlias : lBankNameAliasPending ){
                                if(lBankNameAlias.getBankClientName().equalsIgnoreCase(receivedTransfer.getClientName())){
                                    duplicate = true;
                                    break;
                                }
                            }
                        }
                        if(!duplicate){
                            bankNameAlias.setBankClientName(receivedTransfer.getClientName());
                            bankNameAlias.setSysClientName(bankVirtualAccount.getClientName());
                            bankNameAlias.setClientId(bankVirtualAccount.getClientId());
                            bankNameAlias.setStatus(NameAliasEnum.PENDING);
                            bankNameAlias.setRechargeId(String.valueOf(receivedTransfer.getId()));
                                
                            bankNameAliasService.saveBanNameAlias(bankNameAlias);
                        }
                    }
                }else{
                    //3.处理正常的订单
                    handelUobRecharge(receivedTransfer, bankVirtualAccount.getClientId());
                    //6、记录充值成功的订单回调
                    transferSuccessItem.add(receivedTransfer);
                    
                }
                    
             /*   if (isNeedRefund) {
                    log.error("从UOB同步充值记录,姓名对不上需要退款transferLog:{}", receivedTransfer.getVirtualAccountNo(), JSON.toJSON(receivedTransfer));
                    RechargeRefundBean rechargeRefund = constractRechargeRefund(receivedTransfer, bankVirtualAccount);
                    needRefundUsers.add(rechargeRefund);
                    handleRefundVirtualAccount(receivedTransfer);
                    transferSuccessItem.add(receivedTransfer);
                    continue;
                }
                
                 //3.处理正常的订单
                handelUobRecharge(receivedTransfer, bankVirtualAccount.getClientId());
               //6、记录充值成功的订单回调
               transferSuccessItem.add(receivedTransfer);
*/

            } catch (Exception ex) {
                transferErrorUsers.add(receivedTransfer);
                log.error("transferLog:{},同步UOB充值记录进行充值异常:", JSON.toJSON(receivedTransfer), ex);
            }
        }
        //处理成功的充值单回调
        if (CollectionUtils.isNotEmpty(transferSuccessItem)) {
            callBackUobRechargeSuccess(transferSuccessItem);
        }

        if (CollectionUtils.isNotEmpty(transferErrorUsers)) {
            notifyTransferErrorUser(transferErrorUsers);
        }
        if (CollectionUtils.isNotEmpty(needRefundUsers)) {
            notifyRechargeRefund(needRefundUsers);
        }

    }

    /**
     * 处理成功的充值单回调:可重复调用，失败没有关系，拉取的处理做了幂等
     *
     * @param transferSuccessItems
     */
    private void callBackUobRechargeSuccess(List<ReceivedTransferItem> transferSuccessItems) {
        log.info("处理成功的充值单回调,transferSuccessItems:{}", JSON.toJSONString(transferSuccessItems));
        uobTradeRemoteService.callBackUobRechargeSuccess(transferSuccessItems);
    }

    /**
     * * UOB内部购汇完成回调
     * * 1、修改虚拟购汇订单为完成
     * * 2、添加虚拟订单: UOB准备转账的订单
     *
     * @param uobExchange
     */
    @Override
    public void handelUobExchangeCallBack(UobExchangeDTO uobExchange) {
        //1、查询处理购汇订单
        BankVirtualAccountOrder virtualAccountOrder = bankVirtualAccountOrderService.queryById(uobExchange.getVirtualAccountOrderId());
        if (null == virtualAccountOrder) {
            throw new BusinessException("UOB购汇回调没有查询到虚拟订单");
        }
        //2、幂等处理订单
        if (VAOrderTradeStatusEnum.HANDLING != virtualAccountOrder.getOrderStatus()) {
            log.info("UOB内部购汇处理幂等过滤uobExchange：{}", JSON.toJSONString(uobExchange));
            return;
        }
        //3、购汇后需要插入新币账户账户号
        BankVirtualAccount queryParam = new BankVirtualAccount();
        queryParam.setVirtualAccountNo(virtualAccountOrder.getVirtualAccountNo());
        BankVirtualAccount bankVirtualAccount = bankVirtualAccountService.quaryBankVirtualAccount(queryParam);
        String sgdVirtualAccountNo = StringUtils.EMPTY;
        BankVirtualAccount bankVirtualAccountParam = new BankVirtualAccount();
        bankVirtualAccountParam.setClientId(bankVirtualAccount.getClientId());
        List<BankVirtualAccount> bankVirtualAccounts = bankVirtualAccountService.queryListBankVirtualAccount(bankVirtualAccountParam);
        for (BankVirtualAccount virtualAccount : bankVirtualAccounts) {
            if (CurrencyEnum.SGD == virtualAccount.getCurrency()) {
                sgdVirtualAccountNo = virtualAccount.getVirtualAccountNo();
            }
        }
        //4、修改购汇充值记录状态
        virtualAccountOrder.setOrderStatus(VAOrderTradeStatusEnum.SUCCESS);
        virtualAccountOrder.setUpdateTime(DateUtils.now());
        //5、基础信息检查
        log.info("####,clientId:{}, ReferenceCode:{}", bankVirtualAccount.getClientId(), virtualAccountOrder.getReferenceCode());
        UserGoalInfoPO userGoal = getUserGoal(bankVirtualAccount.getClientId(), virtualAccountOrder.getReferenceCode());
        if (null == userGoal) {
            throw new BusinessException("UOB购汇回调：用户没有设置goal信息");
        }
        //6、添加美金的入 美金的出
        List<BankVirtualAccountOrder> vAOrderList = Lists.newArrayList();
        ReceivedTransferItem item = new ReceivedTransferItem();
        item.setCurrency(CurrencyEnum.SGD);
        item.setBankOrderNo(virtualAccountOrder.getBankOrderNo());
        item.setCashAmount(uobExchange.getConfirmMoney());
        item.setTradeTime(virtualAccountOrder.getTradeTime());
        item.setVirtualAccountNo(sgdVirtualAccountNo);
        item.setReferenceCode(virtualAccountOrder.getReferenceCode());
        BankVirtualAccountOrder exchangeInOrder = bankVirtualAccountOrderService.getBVAOrder(item, VAOrderTradeStatusEnum.SUCCESS,
                VAOrderTradeTypeEnum.COME_INTO, VAOrderActionTypeEnum.RECHARGE_EXCHANGE, virtualAccountOrder.getReferenceCode());
        vAOrderList.add(exchangeInOrder);
        BankVirtualAccountOrder processingOrder = bankVirtualAccountOrderService.getBVAOrder(item, VAOrderTradeStatusEnum.HANDLING,
                VAOrderTradeTypeEnum.COME_OUT, VAOrderActionTypeEnum.UOBTOSAXO, virtualAccountOrder.getReferenceCode());
        vAOrderList.add(processingOrder);
        //处理GSD流水状态 + 美金流水
        bankVirtualAccountOrderService.haldelUobExchangeCallback(virtualAccountOrder, vAOrderList, bankVirtualAccount.getClientId());

    }

    /**
     * 用户在goal上设置金额
     *
     * @param goalSetMoney
     * @param userGoal
     * @return
     */
    @Override
    public Long handelGoalSetMoney(GoalSetMoneyBean goalSetMoney, UserGoalInfoPO userGoal) {
        AccountInfoResDTO accountInfoResDTO = new AccountInfoResDTO();
        accountInfoResDTO.setPortfolioId(userGoal.getPortfolioId());
        accountInfoResDTO.setGoalId(userGoal.getGoalId());
        accountInfoResDTO.setClientId(goalSetMoney.getClientId());
        accountInfoResDTO.setReferenceCode(goalSetMoney.getReferenceCode());
        //accountInfoResDTO.setInvestType(AccountTypeEnum.POOLING);
        //accountInfoResDTO.setInitDay(InitDayEnum.UN_INIT_DAY);
        accountInfoResDTO = accountInfoRemoteService.createNewAccIfNotExist(accountInfoResDTO);
        

        AccountRechargeResDTO accountRechargeResDTO = new AccountRechargeResDTO();

        accountRechargeResDTO.setClientId(goalSetMoney.getClientId());
        accountRechargeResDTO.setGoalId(userGoal.getGoalId());
        accountRechargeResDTO.setRechargeAmount(goalSetMoney.getMoney());
        accountRechargeResDTO.setAccountId(accountInfoResDTO.getId());
        accountRechargeResDTO.setRechargeTime(DateUtils.now());
        accountRechargeResDTO.setCurrency(goalSetMoney.getCurrency());
        accountRechargeResDTO.setBankOrderNo("Das"+Sequence.next().toString());
        accountRechargeResDTO.setRechargeOrderNo(Sequence.next());
        rechargeServiceRemoteService.rechargeAhamTransfer(accountRechargeResDTO);

        return accountRechargeResDTO.getRechargeOrderNo();
    }


    /**
     * 处理正常的UOB充值订单 : 先记录到virtual Account里，在下一个任务中进行操作处理中的virtual充值单去进行转账
     * * * 1、没有referenceCode的充值直接记录松鼠账户不做其他处理  !!
     * * * <p>
     * * * 2、美金和新币的不同策略
     * * * ==美金需要购汇等待回调 !! 异步
     * * * ==新币直接处理
     * * * <p>
     * * * 3、新币的话不做处理 : 新币不用做转换 1条入金 1条冻结
     * * * <p>
     * * * ==以上处理虚拟账户流水  !! 一定要重新计算冻结金额,下面的校验就是使用的冻结金额
     * * * <p>
     * * * 4、绑定用户账户关系
     * * * 5、发送UOB->SAXO转账信息
     *
     * @param receivedTransfer
     * @param clientId
     */
    private void handelUobRecharge(ReceivedTransferItem receivedTransfer, String clientId) {
        //1、基础信息设定
        UserGoalInfoPO userGoal = getUserGoal(clientId, receivedTransfer.getReferenceCode());
        //2、检查referenceCode
        List<BankVirtualAccountOrder> vAOrderList = Lists.newArrayList();
        if (userGoal == null || StringUtils.isEmpty(receivedTransfer.getReferenceCode())) {
            log.info("用户clientId:{},x`", clientId);
            BankVirtualAccountOrder finishOrder = bankVirtualAccountOrderService.getBVAOrder(receivedTransfer, VAOrderTradeStatusEnum.SUCCESS,
                    VAOrderTradeTypeEnum.COME_INTO, VAOrderActionTypeEnum.RECHARGE, StringUtils.EMPTY);
            finishOrder.setMatchType(MatchTypeEnum.REFERENCECODE_UNMATCH);
            vAOrderList.add(finishOrder);
            bankVirtualAccountOrderService.saveOrdersAndUpdateAccount(vAOrderList, clientId);
            return;
        }
        //3、检查是否美金入金 ==》 USD兑换成SGD
        if (CurrencyEnum.MYR == receivedTransfer.getCurrency()) {
            log.info("clientId:{},从UOB同步转账美金必须购汇成新币,等待回调处理", clientId);
            Long bVAOrderId = Sequence.next();
            RpcMessage<UobExchangeResult> uobExchangeResultRpc = getSGDCashByMYR(receivedTransfer.getCashAmount(),
                    bVAOrderId, receivedTransfer.getVirtualAccountNo());
            if (RpcMessageStandardCode.OK.value() == uobExchangeResultRpc.getResultCode()) {
                //美金需要先转换成新币 --> 1条入金 + 1条转换支出 + 1条转换收入 + 一条冻结
                BankVirtualAccountOrder finishOrder = bankVirtualAccountOrderService.getBVAOrder(receivedTransfer, VAOrderTradeStatusEnum.SUCCESS,
                        VAOrderTradeTypeEnum.COME_INTO, VAOrderActionTypeEnum.RECHARGE, userGoal.getReferenceCode());
                vAOrderList.add(finishOrder);
                BankVirtualAccountOrder exchangeOutOrder = bankVirtualAccountOrderService.getBVAOrder(receivedTransfer, VAOrderTradeStatusEnum.HANDLING,
                        VAOrderTradeTypeEnum.COME_OUT, VAOrderActionTypeEnum.RECHARGE_EXCHANGE, userGoal.getReferenceCode());
                exchangeOutOrder.setId(bVAOrderId);
                vAOrderList.add(exchangeOutOrder);
            }
            //如果购汇失败的话只记录了处理中的购汇出,可以在后面单独处理
            bankVirtualAccountOrderService.saveOrdersAndUpdateAccount(vAOrderList, clientId);
            log.info("clientId:{},从UOB同步转账信息,用户是美金:vAOrderList:{},需要购汇走异步确认", clientId, JSON.toJSON(vAOrderList));
            return;
        }
        //4、新币不用做转换 1条新币入金 1条新币冻结
        BankVirtualAccountOrder finishOrder = bankVirtualAccountOrderService.getBVAOrder(receivedTransfer, VAOrderTradeStatusEnum.SUCCESS,
                VAOrderTradeTypeEnum.COME_INTO, VAOrderActionTypeEnum.RECHARGE, userGoal.getReferenceCode());
        BankVirtualAccountOrder processingOrder = bankVirtualAccountOrderService.getBVAOrder(receivedTransfer, VAOrderTradeStatusEnum.HANDLING,
                VAOrderTradeTypeEnum.COME_OUT, VAOrderActionTypeEnum.UOBTOSAXO, userGoal.getReferenceCode());
        vAOrderList.add(finishOrder);
        vAOrderList.add(processingOrder);
        bankVirtualAccountOrderService.saveOrdersAndUpdateAccount(vAOrderList, clientId);
        saveUserRechargeStatus(processingOrder, clientId); //Added By WooiTatt
        //5、先记录到virtual Account里，在下一个任务中进行操作处理中的virtual充值单去进行转账
    }

    /**
     * USD --> SGD
     *
     * @param cashAmount
     * @param bVAOrderId
     * @param virtualAccountNo
     * @return
     */
    private RpcMessage<UobExchangeResult> getSGDCashByMYR(BigDecimal cashAmount, Long bVAOrderId, String virtualAccountNo) {
        log.info("虚拟账户USD兑换SGD,cashAmount:{}, bVAOrderId:{}, virtualAccountNo:{}", cashAmount, bVAOrderId, virtualAccountNo);
        UobExchangeReq uobExchangeReq = new UobExchangeReq();
        uobExchangeReq.setOutBusinessId(bVAOrderId);
        uobExchangeReq.setExchangeAmount(cashAmount);
        uobExchangeReq.setExchangeType(ExchangeTypeEnum.USD_SGD);
        RpcMessage<UobExchangeResult> msg = uobTradeRemoteService.exchangeForRecharge(uobExchangeReq);
        log.info("虚拟账户USD兑换SGD返回,cashAmount:{}, bVAOrderId:{}, virtualAccountNo:{}, msg:{}",
                cashAmount, bVAOrderId, virtualAccountNo, JSON.toJSON(msg));
        return msg;
    }

    /**
     * 基础信息设定
     *
     * @param clientId
     * @param referenceCode
     * @return
     */
    private UserGoalInfoPO getUserGoal(String clientId, String referenceCode) {
        UserGoalInfoPO userGoalInfoPO = new UserGoalInfoPO();
        userGoalInfoPO.setClientId(clientId);
        List<UserGoalInfoPO> userGoalInfoPOs = userGoalInfoInfoService.queryUserGoalInfos(userGoalInfoPO);
        Map<String, UserGoalInfoPO> userGoalInfoMap = userGoalInfoPOs.stream()
                .collect(Collectors.toMap(UserGoalInfoPO::getReferenceCode, account -> account));
        return userGoalInfoMap.get(referenceCode);
    }

    /**
     * 构造用户退款流水
     * * * clientName和线下转账进来的对不上需要给用户退款
     * * * 一条正确到账,一条准备划款支出
     *
     * @param receivedTransfer
     */
    private void handleRefundVirtualAccount(ReceivedTransferItem receivedTransfer) {
        List<BankVirtualAccountOrder> vAOrderList = Lists.newArrayList();
        BankVirtualAccountOrder finishInOrder = bankVirtualAccountOrderService.getBVAOrder(receivedTransfer, VAOrderTradeStatusEnum.SUCCESS,
                VAOrderTradeTypeEnum.COME_INTO, VAOrderActionTypeEnum.REFUND, StringUtils.EMPTY);
        finishInOrder.setMatchType(MatchTypeEnum.NAME_UNMATCH);
        finishInOrder.setNeedRefundType(NeedRefundTypeEnum.REFUND);
        BankVirtualAccountOrder finishOutOrder = bankVirtualAccountOrderService.getBVAOrder(receivedTransfer, VAOrderTradeStatusEnum.HANDLING,
                VAOrderTradeTypeEnum.COME_OUT, VAOrderActionTypeEnum.REFUND, StringUtils.EMPTY);
        finishOutOrder.setMatchType(MatchTypeEnum.NAME_UNMATCH);
        finishOutOrder.setNeedRefundType(NeedRefundTypeEnum.REFUND);
        vAOrderList.add(finishInOrder);
        vAOrderList.add(finishOutOrder);
        bankVirtualAccountOrderService.saveVAOrders(vAOrderList);
    }

    /**
     * 构造退款记录
     *
     * @param receivedTransfer
     * @param bankVirtualAccount
     * @return
     */
    private RechargeRefundBean constractRechargeRefund(ReceivedTransferItem receivedTransfer,
                                                       BankVirtualAccount bankVirtualAccount) {
        String clientId = null == bankVirtualAccount ? StringUtils.EMPTY : bankVirtualAccount.getClientId();
        String clientName = null == bankVirtualAccount ? StringUtils.EMPTY : bankVirtualAccount.getClientName();
        String virtualAccountNo = null == bankVirtualAccount ? StringUtils.EMPTY : bankVirtualAccount.getVirtualAccountNo();
        RechargeRefundBean rechargeRefund = new RechargeRefundBean();
        rechargeRefund.setAmount(receivedTransfer.getCashAmount());
        rechargeRefund.setBankOrderNumber(receivedTransfer.getBankOrderNo());
        rechargeRefund.setBankProvidedName(receivedTransfer.getClientName());
        rechargeRefund.setClientId(clientId);
        rechargeRefund.setClientName(clientName);
        rechargeRefund.setCurrency(receivedTransfer.getCurrency().getCode());
        rechargeRefund.setTradeTime(DateUtils.formatDate(receivedTransfer.getTradeTime(), DateUtils.DATE_FORMAT4));
        rechargeRefund.setVirtualAccountNo(virtualAccountNo);
        return rechargeRefund;
    }

    /**
     * 从UOB同步用户充值记录
     *
     * @return
     */
    private List<ReceivedTransferItem> getUobRecharges() {
        ReceivedTransferReq receivedTransferReq = new ReceivedTransferReq();
        receivedTransferReq.setRechargeStatus(UobRechargeStatusEnum.PROCESSING);
        log.info("处理用户入金,查询UOB充值记录");
        RpcMessage<ReceivedTransferResult> resultRpcMessage = uobTradeRemoteService.queryReceivedTransferLog(receivedTransferReq);
        log.info("处理用户入金,查询UOB充值记录返回结果,resultRpcMessage：{}", JSON.toJSONString(resultRpcMessage));
        if (!resultRpcMessage.isSuccess()) {
            log.error("处理用户入金,查询UOB接口异常,日期:{}", DateUtils.getDate(DateUtils.DATE_TIME_FORMAT));
            throw new BusinessException("用户入金处理：查询用户入金记录接口错误");
        }
        return resultRpcMessage.getContent().getItemList();
    }

    /**
     * UOB转账失败需要通知查看的用户  //发送邮件给CMS
     *
     * @param transferErrorUsers
     */
    private void notifyTransferErrorUser(List<ReceivedTransferItem> transferErrorUsers) {
        log.error("UOB转账查询goal失败,需要通知的用户:{}", JSON.toJSON(transferErrorUsers));
        String contactEmail = PropertiesUtil.getString("cms_contactEmail");
        String activeProfile = ApplicationContextHolder.getActiveProfile();
        String topic = MessageFormat.format(EMAIL_TITLE_TEM, activeProfile);
        Email email = new Email()
                .setTemplateName("CmsEmail")
                .setTemplateVariables(InstanceUtil.newHashMap("cmsMsg", JSON.toJSON(transferErrorUsers)))
                .setSendTo(contactEmail)
                .setTopic(topic);
        EmailUtil.sendEmail(email);
    }

    /**
     * 需要退款的用户通知
     *
     * //先入退款记录表
     * //再发送mq
     *  //设置退款记录发送状态
     * //消费者监听消息
     * //发送邮件
     * //确认消息状态
     * //定时任务扫描退款记录表，若发现未发送状态超过1天。重新发送到mq
     * @param needRefundUsers
     */
    public void notifyRechargeRefund(List<RechargeRefundBean> needRefundUsers) {
        if(CollectionUtils.isEmpty(needRefundUsers)){
            log.info("需退款用户列表为空");
            return;
        }

        List<String> bankOrders = Lists.transform(needRefundUsers, new Function<RechargeRefundBean, String>() {
            @Nullable
            @Override
            public String apply(@Nullable RechargeRefundBean input) {
                return input.getBankOrderNumber();
            }
        });

        String bankOrderNoStr = Joiner.on(",").join(bankOrders);
        //先入退款记录表
        String messageId = REFUND_MESSAGE_PREFIX+Sequence.next();
        BankVirtualAccountOrderMsgPO bankVirtualAccountOrderMsgPO = new BankVirtualAccountOrderMsgPO();
        bankVirtualAccountOrderMsgPO.setBankOrderNo(bankOrderNoStr);
        bankVirtualAccountOrderMsgPO.setMsgStatus(MsgStatusEnum.CREATE);
        bankVirtualAccountOrderMsgPO.setSendTime(DateUtils.now());
        bankVirtualAccountOrderMsgPO.setMessageId(messageId);
        bankVirtualAccountOrderMsgService.updateOrInsert(bankVirtualAccountOrderMsgPO);

        RefundMessageDTO refundMessageBean = new RefundMessageDTO();
        List<RechargeRefundDTO> rechargeRefundDTOS = BeanMapperUtils.mapList(needRefundUsers, RechargeRefundDTO.class);
        refundMessageBean.setNeedRefundUsers(rechargeRefundDTOS);

        //再发送mq
        pubConfirmTemplate.convertAndSend(PropertiesUtil.getString(REFUND_EXCHANGE), "",refundMessageBean, message -> {
            message.getMessageProperties().setMessageId(messageId);
            return message;
        },new CorrelationData(messageId));
        log.info("退款订单,mq发送完毕:{}",JSON.toJSONString(refundMessageBean));


        //设置退款记录发送状态

        //消费者监听消息

        //发送邮件

        //确认消息状态

        //定时任务扫描退款记录表，若发现未发送状态超过1天。重新发送到mq


//        log.info("充值用户退款,date:{}", DateUtils.getDate());
//        try {
//            ExportExcel exportExcel = new ExportExcel(null, RechargeRefundBean.class);
//            exportExcel.setDataList(needRefundUsers);
//            String fileName = "recharge_refund.xlsx";
//            String topic = "recharge_need_refund_" + DateUtils.getDate();
//            ByteArrayOutputStream os = new ByteArrayOutputStream();
//            exportExcel.write(os);
//
//            BodyPart bodyPart = new MimeBodyPart();
//            ByteArrayDataSource dataSource = new ByteArrayDataSource(os.toByteArray(), "application/png");
//            bodyPart.setDataHandler(new DataHandler(dataSource));
//            bodyPart.setFileName(fileName);
//
//            Email email = new Email();
//            email.setBodyPart(bodyPart);
//            email.setSendTo(PropertiesUtil.getString("email.recharge.refund"));
//            email.setTopic(topic);
//            email.setBody(DateUtils.getDate() + ",充值退款看附件");
//
//            EmailUtil.sendEmail(email);
//            exportExcel.dispose();
//
//        } catch (Exception e) {
//            log.error("充值用户退款,异常", e);
//        }
//        log.info("充值用户退款成功,date:{}", DateUtils.getDate());
    }
    
    //Added by wooitatt 
    private void saveUserRechargeStatus(BankVirtualAccountOrder bankVirtualAccountOrder, String clientId){
        try{
            UserGoalInfoPO userGoalInfoPO = new UserGoalInfoPO();
            userGoalInfoPO.setReferenceCode(bankVirtualAccountOrder.getReferenceCode());
            UserGoalInfoPO userGoalInfo = userGoalInfoInfoService.getUserGoal(userGoalInfoPO);

            BankVirtualAccountOrder bankVAOrder = bankVirtualAccountOrderService.queryLastBVAOrder(bankVirtualAccountOrder);

            UserRechargeStatus userRechargeStatus = new UserRechargeStatus();
            userRechargeStatus.setClientId(clientId);
            userRechargeStatus.setGoalId(userGoalInfo.getGoalId());
            userRechargeStatus.setCurrency(bankVirtualAccountOrder.getCurrency());
            userRechargeStatus.setAmount(bankVirtualAccountOrder.getCashAmount());
            userRechargeStatus.setBankVirtualAccOrderId(bankVAOrder.getId().toString());
            userRechargeStatus.setUserRechargeStatusEnum(UserRechargeStatusEnum.PROGRESS);

            userRechargeStatusService.saveUserRechargeStatus(userRechargeStatus);
        }catch(Exception e){e.toString();}
        
    }

}
