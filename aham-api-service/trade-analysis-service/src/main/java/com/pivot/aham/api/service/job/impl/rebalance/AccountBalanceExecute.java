package com.pivot.aham.api.service.job.impl.rebalance;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.server.dto.req.SaxoTradeReq;
import com.pivot.aham.api.server.dto.resp.SaxoTradeResult;
import com.pivot.aham.api.server.remoteservice.SaxoTradeRemoteService;
import com.pivot.aham.api.service.job.wrapperbean.AnalyTpcfTncfWrapperBean;
import com.pivot.aham.api.service.mapper.model.*;
import com.pivot.aham.api.service.service.*;
import com.pivot.aham.common.enums.analysis.*;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.EtfOrderTypeEnum;
import com.pivot.aham.common.enums.ProductAssetStatusEnum;
import com.pivot.aham.common.enums.TmpOrderActionTypeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 调仓执行
 *
 * @author addison
 * @since 2019年03月24日
 */
@Component
@Scope(value = "prototype")
@Data
@Slf4j
public class AccountBalanceExecute {

    /**
     * 方案明细
     */
    private List<AccountBalanceAdjDetail> accountBalanceAdjDetails;
    /**
     * 现金流
     */
    private AnalyTpcfTncfWrapperBean analyTpcfTncfWrapperBean;

    @Autowired
    private AccountBalanceRecordService accountBalanceRecordService;
    @Autowired
    private AccountBalanceAdjDetailService accountBalanceAdjDetailService;
    @Autowired
    private TmpOrderRecordService tmpOrderRecordService;
    @Resource
    private SaxoTradeRemoteService saxoTradeRemoteService;
    @Autowired
    private AccountAssetService accountAssetService;
    @Autowired
    private AccountRechargeService accountRechargeService;
    @Resource
    private TpcfTncfService tpcfTncfService;
    @Resource
    private AccountRedeemService accountRedeemService;
    @Resource
    private RedeemApplyService redeemApplyService;

    public void executePlanDetailSell(BalTradeTypeEnum tradeTypeEnum) {

        if (CollectionUtils.isEmpty(accountBalanceAdjDetails)) {
            throw new BusinessException("方案不能为空");
        }

        // TODO : Fix it 
        Long balId = accountBalanceAdjDetails.get(0).getBalId();
        AccountBalanceRecord accountBalanceRecord = accountBalanceRecordService.queryById(balId);

        if (accountBalanceRecord.getBalStatus() == BalStatusEnum.BUYING && tradeTypeEnum == BalTradeTypeEnum.BUY) {
            log.info("该方案买已执行:{}", JSON.toJSONString(accountBalanceRecord));
            return;
        }

        if (accountBalanceRecord.getBalStatus() == BalStatusEnum.SELLING && tradeTypeEnum == BalTradeTypeEnum.SELL) {
            log.info("该方案买已执行:{}", JSON.toJSONString(accountBalanceRecord));
            return;
        }

        Long totalId = Sequence.next();
        Boolean buyComplete = false;
        Long accountId = null;

        //和非调仓时候一样，更新提现申请
        if (analyTpcfTncfWrapperBean != null) {
            for (AccountRedeemPO accountRedeem : analyTpcfTncfWrapperBean.getAccountRedeemPOs()) {
                accountRedeem.setAccountId(accountRedeem.getAccountId());
                accountRedeem.setTotalTmpOrderId(totalId);
                accountRedeem.setOrderStatus(RedeemOrderStatusEnum.HANDLING);
                accountRedeemService.updateOrInsert(accountRedeem);

                RedeemApplyPO redeemApply = redeemApplyService.queryById(accountRedeem.getRedeemApplyId());
                redeemApply.setAccountId(accountRedeem.getAccountId());
                redeemApply.setTotalTmpOrderId(totalId);
                redeemApply.setEtfExecutedStatus(EtfExecutedStatusEnum.HANDLING);
                redeemApplyService.updateOrInsert(redeemApply);
            }
        }

        for (AccountBalanceAdjDetail accountBalanceAdjDetail : accountBalanceAdjDetails) {
            String productCode = accountBalanceAdjDetail.getProductCode();
            if (productCode.equals(Constants.MAIN_CASH) || productCode.equals(Constants.SUB_CASH) || productCode.equals(Constants.CASH)) {
                continue;
            }
            if (accountId == null) {
                accountId = accountBalanceRecord.getAccountId();
            }

            TmpOrderRecordPO tmpOrderRecordPO = new TmpOrderRecordPO();
            tmpOrderRecordPO.setAccountId(accountBalanceRecord.getAccountId());
            tmpOrderRecordPO.setApplyMoney(accountBalanceAdjDetail.getTradeAmount());
            tmpOrderRecordPO.setApplyTime(DateUtils.now());
            tmpOrderRecordPO.setTotalTmpOrderId(totalId);
            tmpOrderRecordPO.setProductCode(accountBalanceAdjDetail.getProductCode());
            Long tmpOrder = Sequence.next();
            tmpOrderRecordPO.setTmpOrderId(tmpOrder);
            tmpOrderRecordPO.setTmpOrderTradeStatus(TmpOrderExecuteStatusEnum.CREATE);
            //请求执行器下单
            SaxoTradeReq tradeItemDTO = new SaxoTradeReq();
            tradeItemDTO.setAccountId(accountBalanceRecord.getAccountId());
            tradeItemDTO.setAmount(accountBalanceAdjDetail.getTradeAmount());
            tradeItemDTO.setEtfCode(accountBalanceAdjDetail.getProductCode());
            tradeItemDTO.setOutBusinessId(tmpOrder);

            //根据方案详情下etf单
            if (accountBalanceAdjDetail.getTradeType() == BalTradeTypeEnum.BUY) {

                tmpOrderRecordPO.setActionType(TmpOrderActionTypeEnum.BUY);
                tmpOrderRecordPO.setTmpOrderTradeType(EtfOrderTypeEnum.RBA);
                tmpOrderRecordPO = tmpOrderRecordService.updateOrInsert(tmpOrderRecordPO);

                log.info("远程调用saxoTradeRemoteService.buy,入参:{}", JSON.toJSONString(tradeItemDTO));
                //再平衡买单
                tradeItemDTO.setOrderType(EtfOrderTypeEnum.RBA);
                RpcMessage<SaxoTradeResult> rpcMessage = saxoTradeRemoteService.buy(tradeItemDTO);
                log.info("远程调用saxoTradeRemoteService.buy,出参:{}", JSON.toJSONString(rpcMessage));
                accountBalanceRecord.setBalStatus(BalStatusEnum.BUYING);

                AccountAssetPO outAsset = new AccountAssetPO();
                outAsset.setAssetSource(AssetSourceEnum.BUYETF);
                outAsset.setAccountId(tmpOrderRecordPO.getAccountId());
                outAsset.setProductCode(Constants.CASH);
                outAsset.setConfirmShare(BigDecimal.ZERO);
                outAsset.setConfirmMoney(tmpOrderRecordPO.getApplyMoney());
                outAsset.setApplyMoney(tmpOrderRecordPO.getApplyMoney());
                outAsset.setProductAssetStatus(ProductAssetStatusEnum.SELL_ING);
                outAsset.setRechargeOrderNo(0L);
                outAsset.setApplyTime(DateUtils.now());
                outAsset.setConfirmTime(DateUtils.now());
                outAsset.setTotalTmpOrderId(tmpOrderRecordPO.getTotalTmpOrderId());
                outAsset.setTmpOrderId(0L);
                if (!buyComplete) {
                    buyComplete = true;
                }
                rpcMessageHandler(accountBalanceAdjDetail, tmpOrderRecordPO, rpcMessage, accountBalanceRecord, outAsset);
            } else {
                EtfOrderTypeEnum etfOrderTypeEnum = null;
                if (accountBalanceAdjDetail.getTargetHold().compareTo(BigDecimal.ZERO) == 0) {
                    etfOrderTypeEnum = EtfOrderTypeEnum.RSA;
                }

                if (accountBalanceAdjDetail.getTargetHold().compareTo(BigDecimal.ZERO) > 0) {
                    etfOrderTypeEnum = EtfOrderTypeEnum.RSP;
                }

                tmpOrderRecordPO.setTmpOrderTradeType(etfOrderTypeEnum);
                tmpOrderRecordPO.setActionType(TmpOrderActionTypeEnum.SELL);
                tmpOrderRecordPO = tmpOrderRecordService.updateOrInsert(tmpOrderRecordPO);
                tradeItemDTO.setOrderType(etfOrderTypeEnum);
                log.info("远程调用saxoTradeRemoteService.sell,入参:{}", JSON.toJSONString(tradeItemDTO));
                RpcMessage<SaxoTradeResult> rpcMessage = saxoTradeRemoteService.sell(tradeItemDTO);
                log.info("远程调用saxoTradeRemoteService.sell,出参:{}", JSON.toJSONString(rpcMessage));
                accountBalanceRecord.setBalStatus(BalStatusEnum.SELLING);
                rpcMessageHandler(accountBalanceAdjDetail, tmpOrderRecordPO, rpcMessage, accountBalanceRecord, null);
            }
        }
        if (buyComplete) {
            tpcfHandler(accountId, totalId);
        }
    }

    public void executePlanDetailBuy(BalTradeTypeEnum tradeTypeEnum, Long balId) {

        if (CollectionUtils.isEmpty(accountBalanceAdjDetails)) {
            throw new BusinessException("方案不能为空");
        }

        AccountBalanceRecord accountBalanceRecord = accountBalanceRecordService.queryById(balId);

        if (accountBalanceRecord.getBalStatus() == BalStatusEnum.BUYING && tradeTypeEnum == BalTradeTypeEnum.BUY) {
            log.info("该方案买已执行:{}", JSON.toJSONString(accountBalanceRecord));
            return;
        }

        if (accountBalanceRecord.getBalStatus() == BalStatusEnum.SELLING && tradeTypeEnum == BalTradeTypeEnum.SELL) {
            log.info("该方案买已执行:{}", JSON.toJSONString(accountBalanceRecord));
            return;
        }

        Long totalId = Sequence.next();
        Boolean buyComplete = false;
        Long accountId = null;

        //和非调仓时候一样，更新提现申请
        if (analyTpcfTncfWrapperBean != null) {
            for (AccountRedeemPO accountRedeem : analyTpcfTncfWrapperBean.getAccountRedeemPOs()) {
                accountRedeem.setAccountId(accountRedeem.getAccountId());
                accountRedeem.setTotalTmpOrderId(totalId);
                accountRedeem.setOrderStatus(RedeemOrderStatusEnum.HANDLING);
                accountRedeemService.updateOrInsert(accountRedeem);

                RedeemApplyPO redeemApply = redeemApplyService.queryById(accountRedeem.getRedeemApplyId());
                redeemApply.setAccountId(accountRedeem.getAccountId());
                redeemApply.setTotalTmpOrderId(totalId);
                redeemApply.setEtfExecutedStatus(EtfExecutedStatusEnum.HANDLING);
                redeemApplyService.updateOrInsert(redeemApply);
            }
        }

        for (AccountBalanceAdjDetail accountBalanceAdjDetail : accountBalanceAdjDetails) {
            String productCode = accountBalanceAdjDetail.getProductCode();
            if (productCode.equals(Constants.MAIN_CASH) || productCode.equals(Constants.SUB_CASH) || productCode.equals(Constants.CASH)) {
                continue;
            }
            if (accountId == null) {
                accountId = accountBalanceRecord.getAccountId();
            }

            TmpOrderRecordPO tmpOrderRecordPO = new TmpOrderRecordPO();
            tmpOrderRecordPO.setAccountId(accountBalanceRecord.getAccountId());
            tmpOrderRecordPO.setApplyMoney(accountBalanceAdjDetail.getTradeAmount());
            tmpOrderRecordPO.setApplyTime(DateUtils.now());
            tmpOrderRecordPO.setTotalTmpOrderId(totalId);
            tmpOrderRecordPO.setProductCode(accountBalanceAdjDetail.getProductCode());
            Long tmpOrder = Sequence.next();
            tmpOrderRecordPO.setTmpOrderId(tmpOrder);
            tmpOrderRecordPO.setTmpOrderTradeStatus(TmpOrderExecuteStatusEnum.CREATE);
            //请求执行器下单
            SaxoTradeReq tradeItemDTO = new SaxoTradeReq();
            tradeItemDTO.setAccountId(accountBalanceRecord.getAccountId());
            tradeItemDTO.setAmount(accountBalanceAdjDetail.getTradeAmount());
            tradeItemDTO.setEtfCode(accountBalanceAdjDetail.getProductCode());
            tradeItemDTO.setOutBusinessId(tmpOrder);

            //根据方案详情下etf单
            if (accountBalanceAdjDetail.getTradeType() == BalTradeTypeEnum.BUY) {

                tmpOrderRecordPO.setActionType(TmpOrderActionTypeEnum.BUY);
                tmpOrderRecordPO.setTmpOrderTradeType(EtfOrderTypeEnum.RBA);
                tmpOrderRecordPO = tmpOrderRecordService.updateOrInsert(tmpOrderRecordPO);

                log.info("远程调用saxoTradeRemoteService.buy,入参:{}", JSON.toJSONString(tradeItemDTO));
                //再平衡买单
                tradeItemDTO.setOrderType(EtfOrderTypeEnum.RBA);
                RpcMessage<SaxoTradeResult> rpcMessage = saxoTradeRemoteService.buy(tradeItemDTO);
                log.info("远程调用saxoTradeRemoteService.buy,出参:{}", JSON.toJSONString(rpcMessage));
                accountBalanceRecord.setBalStatus(BalStatusEnum.BUYING);

                AccountAssetPO outAsset = new AccountAssetPO();
                outAsset.setAssetSource(AssetSourceEnum.BUYETF);
                outAsset.setAccountId(tmpOrderRecordPO.getAccountId());
                outAsset.setProductCode(Constants.CASH);
                outAsset.setConfirmShare(BigDecimal.ZERO);
                outAsset.setConfirmMoney(tmpOrderRecordPO.getApplyMoney());
                outAsset.setApplyMoney(tmpOrderRecordPO.getApplyMoney());
                outAsset.setProductAssetStatus(ProductAssetStatusEnum.SELL_ING);
                outAsset.setRechargeOrderNo(0L);
                outAsset.setApplyTime(DateUtils.now());
                outAsset.setConfirmTime(DateUtils.now());
                outAsset.setTotalTmpOrderId(tmpOrderRecordPO.getTotalTmpOrderId());
                outAsset.setTmpOrderId(0L);
                if (!buyComplete) {
                    buyComplete = true;
                }
                rpcMessageHandler(accountBalanceAdjDetail, tmpOrderRecordPO, rpcMessage, accountBalanceRecord, outAsset);
            }
        }
        if (buyComplete) {
            tpcfHandler(accountId, totalId);
        }
    }

    /**
     * 处理tpcf
     *
     * @param accountId
     * @param totalId
     */
    public void tpcfHandler(Long accountId, Long totalId) {
        List<AccountRechargePO> accountRechargePOs = tpcfTncfService.getAccountTpcf(accountId);
        BigDecimal tpcf = tpcfTncfService.getAccountTpcfMoney(accountRechargePOs);

        if (tpcf.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        AccountAssetPO unbuyAsset = new AccountAssetPO();
        unbuyAsset.setAssetSource(AssetSourceEnum.BUYETF);
        unbuyAsset.setAccountId(accountId);
        unbuyAsset.setProductCode(Constants.UN_BUY_PRODUCT_CODE);
        unbuyAsset.setConfirmShare(BigDecimal.ZERO);
        unbuyAsset.setConfirmMoney(tpcf);
        unbuyAsset.setApplyMoney(tpcf);
        unbuyAsset.setProductAssetStatus(ProductAssetStatusEnum.CONFIRM_SELL);
        unbuyAsset.setRechargeOrderNo(0L);
        unbuyAsset.setApplyTime(DateUtils.now());
        unbuyAsset.setConfirmTime(DateUtils.now());
        unbuyAsset.setTotalTmpOrderId(totalId);
        unbuyAsset.setTmpOrderId(0L);

        AccountAssetPO inAsset = new AccountAssetPO();
        inAsset.setAssetSource(AssetSourceEnum.BUYETF);
        inAsset.setAccountId(accountId);
        inAsset.setProductCode(Constants.CASH);
        inAsset.setConfirmShare(BigDecimal.ZERO);
        inAsset.setConfirmMoney(tpcf);
        inAsset.setApplyMoney(tpcf);
        inAsset.setProductAssetStatus(ProductAssetStatusEnum.HOLD_ING);
        inAsset.setRechargeOrderNo(0L);
        inAsset.setApplyTime(DateUtils.now());
        inAsset.setConfirmTime(DateUtils.now());
        inAsset.setTotalTmpOrderId(totalId);
        inAsset.setTmpOrderId(0L);

        accountAssetService.updateOrInsert(unbuyAsset);
        accountAssetService.updateOrInsert(inAsset);
    }

    private void rpcMessageHandler(AccountBalanceAdjDetail accountBalanceAdjDetail,
            TmpOrderRecordPO tmpOrderRecordPO,
            RpcMessage<SaxoTradeResult> rpcMessage,
            AccountBalanceRecord accountBalanceRecord,
            AccountAssetPO outasset) {
        if (rpcMessage.getResultCode() == RpcMessageStandardCode.OK.value()) {
            //更新执行单号
            tmpOrderRecordPO.setTmpOrderTradeStatus(TmpOrderExecuteStatusEnum.HANDLING);
            tmpOrderRecordPO.setExecuteOrderId(rpcMessage.getContent().getOrderId());
            tmpOrderRecordService.updateOrInsert(tmpOrderRecordPO);

            accountBalanceAdjDetail.setExecuteStatus(ExecuteStatusEnum.HANDLING);
            accountBalanceAdjDetail.setTmpOrderId(tmpOrderRecordPO.getTmpOrderId());
            accountBalanceAdjDetailService.updateOrInsert(accountBalanceAdjDetail);

            accountBalanceRecordService.updateOrInsert(accountBalanceRecord);

            if (outasset != null) {
                accountAssetService.updateOrInsert(outasset);
            }

        } else {
            throw new BusinessException("执行器下单失败");
        }
    }

}
