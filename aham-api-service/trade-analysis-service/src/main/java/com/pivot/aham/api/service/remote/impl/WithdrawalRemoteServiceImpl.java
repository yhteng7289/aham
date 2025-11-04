package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.*;
import com.pivot.aham.api.server.dto.res.RedeemApplyResDTO;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.server.remoteservice.WithdrawalRemoteService;
import com.pivot.aham.api.service.mapper.model.*;
import com.pivot.aham.api.service.service.*;
import com.pivot.aham.common.enums.analysis.*;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.RedeemTypeEnum;
import com.pivot.aham.common.enums.recharge.TncfStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 用户提现操作
 *
 * @author addison
 * @since 2018年12月10日
 */
@Slf4j
@Service(interfaceClass = WithdrawalRemoteService.class, validation = "true")
public class WithdrawalRemoteServiceImpl implements WithdrawalRemoteService {

    /**
     * 日志记录器
     */
    @Autowired
    private RedeemApplyService bankVARedeemService;
    @Autowired
    private AccountUserService accountUserService;
    @Autowired
    private AssetFundNavService assetFundNavService;
    @Resource
    private UserServiceRemoteService userServiceRemoteService;
    @Resource
    private SaxoToUobOrderMockService saxoToUobOrderMockService;
    @Resource
    private SaxoToUobTotalRecordService saxoToUobTotalRecordService;
    @Resource
    private SaxoToUobRecordDetailService saxoToUobRecordDetailService;
    @Resource
    private WithdrawalRemoteTransSupport withdrawalRemoteTransSupport;
    @Resource
    private UserFundNavService userFundNavService;
    @Resource
    private AccountRedeemService accountRedeemService;

    /**
     * 从银行虚拟账户提现
     *
     * @param param
     * @return
     */
    @Override
    public RpcMessage<RedeemApplyResDTO> withdrawalFromVirtalAccount(WithdrawalFromVirtalAccountDTO param) {
        //计算当前用户的所有资产，与当日所有申请记录和做比较，如果大于，不进行提现。
        RedeemApplyPO vaRedeemApplyPO = new RedeemApplyPO();
        vaRedeemApplyPO.setClientId(param.getClientId());
        vaRedeemApplyPO.setBankAccountNo(param.getBankAccountNo());
        vaRedeemApplyPO.setRedeemApplyStatus(RedeemApplyStatusEnum.HANDLING);
        List<RedeemApplyPO> vaRedeemApplyList = bankVARedeemService.queryList(vaRedeemApplyPO);
        BigDecimal totalHasRedeem = param.getApplyAmount();
        for (RedeemApplyPO vaRedeem : vaRedeemApplyList) {
            totalHasRedeem = totalHasRedeem.add(vaRedeem.getApplyMoney());
        }

        //获取这个用户的虚拟账户
        BankVirtualAccountDTO bankVirtualAccountQuery = new BankVirtualAccountDTO();
        bankVirtualAccountQuery.setClientId(param.getClientId());
        bankVirtualAccountQuery.setCurrency(param.getSourceAccountType());
        List<BankVirtualAccountResDTO> bankVirtualAccountDTOList = userServiceRemoteService.queryListBankVirtualAccount(bankVirtualAccountQuery);
        if (CollectionUtils.isEmpty(bankVirtualAccountDTOList)) {
            return RpcMessage.error("Missing virtual account records");
        }
        BankVirtualAccountResDTO bankVirtualAccountDTO = bankVirtualAccountDTOList.get(0);

        if (totalHasRedeem.compareTo(bankVirtualAccountDTO.getCashAmount()) > 0) {
            return RpcMessage.error("Exceed redeem asset");
        }

        //记录提现申请
        RedeemApplyPO bankVARedeem = new RedeemApplyPO();
//        bankVARedeem.setId(Sequence.next());
        bankVARedeem.setApplyMoney(param.getApplyAmount());
        bankVARedeem.setConfirmAmount(param.getApplyAmount());
        bankVARedeem.setApplyTime(DateUtils.now());
        bankVARedeem.setBankAccountNo(param.getBankAccountNo());
        bankVARedeem.setBankName(param.getBankName());
        bankVARedeem.setClientId(param.getClientId());
        bankVARedeem.setSourceAccountType(param.getSourceAccountType());
        bankVARedeem.setTargetCurrency(param.getTargetCurrency());
        bankVARedeem.setWithdrawalTargetBankType(param.getWithdrawalTargetBankType());
        bankVARedeem.setSwift(param.getSwift());
        bankVARedeem.setBranch(param.getBranch());
        bankVARedeem.setWithdrawalTargetType(WithdrawalTargetTypeEnum.BankAccount);
        bankVARedeem.setEtfExecutedStatus(EtfExecutedStatusEnum.SUCCESS);
        if (param.getSourceAccountType() == param.getTargetCurrency()) {
            bankVARedeem.setBankTransferStatus(BankTransferStatusEnum.HASEXCHANGE);
        } else {
            bankVARedeem.setBankTransferStatus(BankTransferStatusEnum.NOTEXCHANGE);
        }
        bankVARedeem.setSaxoToUobTransferStatus(SaxoToUobTransferStatusEnum.APPLYSUCCESS);
        bankVARedeem.setWithdrawalSourceType(WithdrawalSourceTypeEnum.FROMVIRTUALACCOUNT);
        bankVARedeem.setBankAddress(param.getBankAddress());
        bankVARedeem.setUpdateTime(DateUtils.now());
        withdrawalRemoteTransSupport.setRedeemApplyPO(bankVARedeem);
        withdrawalRemoteTransSupport.saveRedeemInfoForFromVirtalAccount();

        log.info("用户{},从虚拟账户提现,提现结果:{}", param.getClientId(), JSON.toJSONString(bankVARedeem));
        RedeemApplyResDTO resDTO = buildResDTO(bankVARedeem);
        return RpcMessage.success(resDTO);
    }

    /**
     * 从投资资产中提现
     *
     * @param param
     * @return
     */
    @Override
    public RpcMessage<RedeemApplyResDTO> withdrawalFromGoal(WithdrawalFromGoalDTO param) {
        //根据goalId找到account，添加对应的accountredeem
        AccountUserPO accountUserPO = new AccountUserPO();
        accountUserPO.setClientId(param.getClientId());
        accountUserPO.setGoalId(param.getGoalId());
        AccountUserPO accountUser = accountUserService.selectOne(accountUserPO);
        if (accountUser == null) {
            return RpcMessage.error("该goal找不到对应账户信息");
        }

//        Date now = DateUtils.now();
        //查询所有etf昨日的收市价格
//        Date yesterday = DateUtils.addDateByDay(now, -1);
//        Map<String, BigDecimal> etfClosingPriceMap = assetFundNavService.getEtfClosingPrice(yesterday);
//        log.info("=======date:{},统计用户资产任务etf收市价:{}=======", yesterday, JSON.toJSON(etfClosingPriceMap));
//        if (etfClosingPriceMap == null || etfClosingPriceMap.size() == 0) {
//            return RpcMessage.error("昨日收市价错误");
//        }
        //计算当前用户在提现账号的所有资产，与当日所有申请记录和做比较，如果大于，不进行提现。
        //昨日资产
        UserFundNavPO userFundNav = userFundNavService.selectUserGoalLastOne(accountUser.getAccountId(), accountUser.getClientId(), param.getGoalId());
        log.info("用户{},投资账号:{},统计前资产明细:{}", accountUser.getClientId(), accountUser.getAccountId(), JSON.toJSON(userFundNav));
        if (null == userFundNav) {
            return RpcMessage.error("该投资账户已无资产");
        }

        BigDecimal totalHasRedeem = accountRedeemService.totalHasRedeemMoney(accountUser.getAccountId(), param.getClientId(), param.getGoalId());
        log.info("用户{},投资账号:{},已申请金额:{},现申请金额:{}", accountUser.getClientId(), accountUser.getAccountId(), totalHasRedeem, param.getApplyMoney());

        //如果提现金额超过现有资产的90%或剩余金额小于10就全部提现
        BigDecimal needApplyMoney = param.getApplyMoney();
        BigDecimal totalAsset = userFundNav.getTotalAsset();
        BigDecimal residue = totalAsset.subtract(totalHasRedeem).subtract(param.getApplyMoney());
        BigDecimal precent = residue.divide(totalAsset, 2, BigDecimal.ROUND_DOWN);
        RedeemTypeEnum redeemTypeEnum = RedeemTypeEnum.NOTALLRedeem;

        BigDecimal totalRedeemMoney = totalHasRedeem.add(needApplyMoney);
        if (totalRedeemMoney.compareTo(totalAsset) > 0) {
            return RpcMessage.error("Exceed redeem amount");
        }

        log.info("用户{},投资账号{},当前总资产{},剩余资产{},剩余资产比例{}", accountUser.getClientId(), accountUser.getAccountId(), totalAsset, residue, precent);
        if (residue.compareTo(new BigDecimal("1")) < 0 || precent.compareTo(new BigDecimal("0.1")) < 0) {
            needApplyMoney = totalAsset.subtract(totalHasRedeem);
            redeemTypeEnum = RedeemTypeEnum.ALLRedeem;
        }

        AccountRedeemPO accountRedeemPO = new AccountRedeemPO();
        accountRedeemPO.setOldApplyMoney(needApplyMoney);
        accountRedeemPO.setRedeemType(redeemTypeEnum);
        accountRedeemPO.setGoalId(param.getGoalId());
        accountRedeemPO.setAccountId(accountUser.getAccountId());
        accountRedeemPO.setApplyMoney(needApplyMoney);
        accountRedeemPO.setClientId(accountUser.getClientId());
        accountRedeemPO.setRedeemApplyTime(DateUtils.now());
        accountRedeemPO.setOrderStatus(RedeemOrderStatusEnum.PROCESSING);
        accountRedeemPO.setTncfStatus(TncfStatusEnum.PROCESSING);
        //记录提现申请
        RedeemApplyPO bankVARedeem = new RedeemApplyPO();
        bankVARedeem.setRedeemType(redeemTypeEnum);
        bankVARedeem.setGoalId(param.getGoalId());
        bankVARedeem.setAccountId(accountUser.getAccountId());
        bankVARedeem.setApplyMoney(needApplyMoney);
        bankVARedeem.setApplyTime(DateUtils.now());
        bankVARedeem.setBankAccountNo(param.getBankAccountNo());
        bankVARedeem.setBankName(param.getBankName());
        bankVARedeem.setClientId(param.getClientId());
        bankVARedeem.setSourceAccountType(param.getSourceAccountType());
        bankVARedeem.setTargetCurrency(param.getTargetCurrency());
        bankVARedeem.setWithdrawalTargetType(param.getWithdrawalTargetType());
        bankVARedeem.setWithdrawalTargetBankType(param.getWithdrawalTargetBankType());
        bankVARedeem.setSwift(param.getSwift());
        bankVARedeem.setBranch(param.getBranch());
        bankVARedeem.setSourceApplyMoney(param.getSourceApplyMoney());
//        if (param.getSourceAccountType() == param.getTargetCurrency()) {
//            bankVARedeem.setBankTransferStatus(BankTransferStatusEnum.HASEXCHANGE);
//        } else {
            bankVARedeem.setBankTransferStatus(BankTransferStatusEnum.NOTEXCHANGE);
//        }
        bankVARedeem.setEtfExecutedStatus(EtfExecutedStatusEnum.DEFAULT);
        bankVARedeem.setWithdrawalSourceType(WithdrawalSourceTypeEnum.FROMGOAL);
        bankVARedeem.setBankAddress(param.getBankAddress());
        bankVARedeem.setUpdateTime(DateUtils.now());
        withdrawalRemoteTransSupport.setRedeemApplyPO(bankVARedeem);
        withdrawalRemoteTransSupport.setAccountRedeemPO(accountRedeemPO);
        withdrawalRemoteTransSupport.saveRedeemInfoForFromGoal();
        log.info("用户{},投资账号:{},提现结果:{}", accountUser.getClientId(), accountUser.getAccountId(), JSON.toJSONString(bankVARedeem));
        RedeemApplyResDTO resDTO = buildResDTO(bankVARedeem);
        return RpcMessage.success(resDTO);

    }

    private RedeemApplyResDTO buildResDTO(RedeemApplyPO bankVARedeem) {
        RedeemApplyResDTO resDTO = new RedeemApplyResDTO();
        BeanMapperUtils.copy(bankVARedeem, resDTO);
        return resDTO;
    }

    @Override
    public RpcMessage saxoToUobOfflineConfirm(SaxoToUobOfflineConfirmDTO saxoToUobOfflineConfirmDTO) {
        //根据流水号获取uob划款记录,现阶段是Mock，直接从表中获取
        SaxoToUobOrderMock saxoToUobOrderMock = new SaxoToUobOrderMock();
        saxoToUobOrderMock.setTransactionId(saxoToUobOfflineConfirmDTO.getSaxoToUobOrderId());
        SaxoToUobOrderMock saxoToUobOrder = saxoToUobOrderMockService.selectOne(saxoToUobOrderMock);

        SaxoToUobTotalRecordPO recordUpdated = new SaxoToUobTotalRecordPO();
        if (saxoToUobOrder != null) {
            //新增流水
            SaxoToUobRecordDetail saxoToUobRecordDetail = new SaxoToUobRecordDetail();
            saxoToUobRecordDetail.setAmount(saxoToUobOrder.getAmount());
            saxoToUobRecordDetail.setTransactionId(saxoToUobOrder.getTransactionId());
            saxoToUobRecordDetail.setSaxoToUobBatchId(saxoToUobOfflineConfirmDTO.getSaxoToUobBatchId());
            saxoToUobRecordDetailService.updateOrInsert(saxoToUobRecordDetail);

            //更新剩余
            SaxoToUobTotalRecordPO saxoToUobTotalQuery = new SaxoToUobTotalRecordPO();
            saxoToUobTotalQuery.setSaxoToUobBatchId(saxoToUobOfflineConfirmDTO.getSaxoToUobBatchId());
            SaxoToUobTotalRecordPO saxoToUobTotalRecord = saxoToUobTotalRecordService.selectOne(saxoToUobTotalQuery);

            BigDecimal residualAmount = saxoToUobTotalRecord.getResidualAmount().subtract(saxoToUobOrder.getAmount());
            if (residualAmount.compareTo(BigDecimal.ZERO) < 0) {
                RpcMessage.error("转账金额太多了");
            }

            saxoToUobTotalRecord.setResidualAmount(saxoToUobTotalRecord.getResidualAmount().subtract(saxoToUobOrder.getAmount()));
            recordUpdated = saxoToUobTotalRecordService.updateOrInsert(saxoToUobTotalRecord);
        } else {
            RpcMessage.error("从UOB查不到该笔转账记录:" + saxoToUobOfflineConfirmDTO.getSaxoToUobOrderId());
        }

        //如果剩余金额为0，说明saxo到uob转账完毕
        if (recordUpdated.getResidualAmount().compareTo(BigDecimal.ZERO) == 0) {
            String batchId = saxoToUobOfflineConfirmDTO.getSaxoToUobBatchId();
            RedeemApplyPO vaRedeemApplyPO = new RedeemApplyPO();
            vaRedeemApplyPO.setSaxoToUobBatchId(batchId);
            List<RedeemApplyPO> vaRedeemApplyPOList = bankVARedeemService.queryList(vaRedeemApplyPO);
            for (RedeemApplyPO vaRedeemApply : vaRedeemApplyPOList) {
                vaRedeemApply.setSaxoToUobTransferStatus(SaxoToUobTransferStatusEnum.APPLYSUCCESS);
            }
        }

        return RpcMessage.success();
    }

    /**
     * 线下excel确认saxo到uob的转账
     *
     * @param saxoToUobOfflineConfirmByExcelDTO
     * @return
     */
    @Override
    public RpcMessage saxoToUobOfflineConfirmByExcel(SaxoToUobOfflineConfirmByExcelDTO saxoToUobOfflineConfirmByExcelDTO) {
        //幂等验证
        SaxoToUobRecordDetail saxoToUobRecordDetailQuery = new SaxoToUobRecordDetail();
        saxoToUobRecordDetailQuery.setTransactionId(saxoToUobOfflineConfirmByExcelDTO.getTransactionId());
        SaxoToUobRecordDetail hasDetail = saxoToUobRecordDetailService.selectOne(saxoToUobRecordDetailQuery);
        if (hasDetail != null) {
            log.error("该确认单已确认{}", JSON.toJSONString(hasDetail));
            RpcMessage.error("该单子已确认");
        }

        SaxoToUobTotalRecordPO recordUpdated = new SaxoToUobTotalRecordPO();
        //更新剩余和确认金额
        SaxoToUobTotalRecordPO saxoToUobTotalQuery = new SaxoToUobTotalRecordPO();
        saxoToUobTotalQuery.setSaxoToUobBatchId(saxoToUobOfflineConfirmByExcelDTO.getSaxoToUobBatchId());
        SaxoToUobTotalRecordPO saxoToUobTotalRecord = saxoToUobTotalRecordService.selectOne(saxoToUobTotalQuery);
        
        Double doubleResidualAmount = saxoToUobTotalRecord.getResidualAmount().doubleValue();
        Double doubleExcellAmount = saxoToUobOfflineConfirmByExcelDTO.getAmount().doubleValue();
        DecimalFormat df = new DecimalFormat("######0.00");
        String strResidualAmount = df.format(doubleResidualAmount);
        String strExcelAmount = df.format(doubleExcellAmount);

        BigDecimal _residualAmount = new BigDecimal(strResidualAmount);
        BigDecimal excelAmount = new BigDecimal(strExcelAmount);

        BigDecimal residualAmount = _residualAmount.subtract(excelAmount);
        if (residualAmount.compareTo(BigDecimal.ZERO) < 0) {
            log.info("{}该批次转账金额太多了", saxoToUobOfflineConfirmByExcelDTO.getSaxoToUobBatchId());
            return RpcMessage.error("转账金额太多了");
        }

        saxoToUobTotalRecord.setConfirmedAmount(saxoToUobTotalRecord.getConfirmedAmount().add(excelAmount));
        saxoToUobTotalRecord.setResidualAmount(_residualAmount.subtract(excelAmount));
        recordUpdated = saxoToUobTotalRecordService.updateOrInsert(saxoToUobTotalRecord);

        //如果剩余金额为0，说明saxo到uob转账完毕
        if (recordUpdated.getResidualAmount() != null && recordUpdated.getResidualAmount().compareTo(BigDecimal.ZERO) == 0) {
            String batchId = saxoToUobOfflineConfirmByExcelDTO.getSaxoToUobBatchId();
            RedeemApplyPO vaRedeemApplyPO = new RedeemApplyPO();
            vaRedeemApplyPO.setSaxoToUobBatchId(batchId);
            List<RedeemApplyPO> vaRedeemApplyPOList = bankVARedeemService.queryList(vaRedeemApplyPO);
            for (RedeemApplyPO vaRedeemApply : vaRedeemApplyPOList) {
                if (vaRedeemApply.getSaxoToUobTransferStatus() == SaxoToUobTransferStatusEnum.APPLYFAIL
                        || vaRedeemApply.getSaxoToUobTransferStatus() == SaxoToUobTransferStatusEnum.APPLYSUCCESS) {
                    continue;
                }
                //saox到uob确认成功
                vaRedeemApply.setSaxoToUobTransferStatus(SaxoToUobTransferStatusEnum.APPLYSUCCESS);
                bankVARedeemService.updateOrInsert(vaRedeemApply);

                //获取这个用户的虚拟账户
                BankVirtualAccountDTO bankVirtualAccountDTO = new BankVirtualAccountDTO();
                bankVirtualAccountDTO.setClientId(vaRedeemApply.getClientId());
                bankVirtualAccountDTO.setCurrency(CurrencyEnum.SGD);
                List<BankVirtualAccountResDTO> bankVirtualAccountDTOList = userServiceRemoteService.queryListBankVirtualAccount(bankVirtualAccountDTO);

                if (CollectionUtils.isEmpty(bankVirtualAccountDTOList)) {
                    return RpcMessage.error("Missing virtual account records");
                }
                BankVirtualAccountResDTO bankVirtualAccount = bankVirtualAccountDTOList.get(0);

                //增加银行虚拟账户资产
                BankVirtualAccountOrderDTO bankVirtualAccountOrder = new BankVirtualAccountOrderDTO();
                bankVirtualAccountOrder.setReferenceCode("")
                        .setRedeemApplyId(vaRedeemApply.getId())
                        .setVirtualAccountNo(bankVirtualAccount.getVirtualAccountNo())
                        .setCashAmount(vaRedeemApply.getConfirmAmountInSgd())
                        .setCurrency(bankVirtualAccount.getCurrency())
                        .setOperatorType(VAOrderTradeTypeEnum.COME_INTO)
                        .setActionType(VAOrderActionTypeEnum.SAXOTOUOB)
                        .setBankOrderNo("")
                        .setOrderStatus(VAOrderTradeStatusEnum.SUCCESS)
                        .setNeedRefundType(NeedRefundTypeEnum.UN_REFUND)
                        .setTradeTime(DateUtils.now())
                        .setCreateTime(DateUtils.now())
                        .setUpdateTime(DateUtils.now())
                        .setId(Sequence.next());
                List<BankVirtualAccountOrderDTO> vAOrderList = Lists.newArrayList();
                vAOrderList.add(bankVirtualAccountOrder);
                userServiceRemoteService.saveOrdersAndUpdateAccount(vAOrderList, vaRedeemApply.getClientId());
            }
        }
        //新增流水
        SaxoToUobRecordDetail saxoToUobRecordDetail = new SaxoToUobRecordDetail();
        saxoToUobRecordDetail.setAmount(saxoToUobOfflineConfirmByExcelDTO.getAmount());
        saxoToUobRecordDetail.setTransactionId(saxoToUobOfflineConfirmByExcelDTO.getTransactionId());
        saxoToUobRecordDetail.setSaxoToUobBatchId(saxoToUobOfflineConfirmByExcelDTO.getSaxoToUobBatchId());
        saxoToUobRecordDetail.setConfirmDate(saxoToUobOfflineConfirmByExcelDTO.getConfirmDate());
        saxoToUobRecordDetailService.updateOrInsert(saxoToUobRecordDetail);

        return RpcMessage.success();
    }
}
