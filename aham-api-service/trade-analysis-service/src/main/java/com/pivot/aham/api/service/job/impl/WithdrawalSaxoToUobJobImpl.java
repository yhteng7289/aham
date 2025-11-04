package com.pivot.aham.api.service.job.impl;

import com.alibaba.fastjson.JSON;
import com.beust.jcommander.internal.Lists;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.eventbus.EventBus;
import com.pivot.aham.api.server.dto.PivotCharityDetailDTO;
import com.pivot.aham.api.server.dto.req.InterAccountTransferReq;
import com.pivot.aham.api.server.dto.resp.InterAccountTransferResult;
import com.pivot.aham.api.server.remoteservice.PivotCharityDetailRemoteService;
import com.pivot.aham.api.server.remoteservice.SaxoTradeRemoteService;
import com.pivot.aham.api.service.job.WithdrawalSaxoToUobJob;
import com.pivot.aham.api.service.job.interevent.StaticRateForAccountEvent;
import com.pivot.aham.api.service.mapper.model.ExchangeRatePO;
import com.pivot.aham.api.service.mapper.model.RedeemApplyPO;
import com.pivot.aham.api.service.mapper.model.SaxoAccountOrderPO;
import com.pivot.aham.api.service.mapper.model.SaxoToUobTotalRecordPO;
import com.pivot.aham.api.service.service.ExchangeRateService;
import com.pivot.aham.api.service.service.RedeemApplyService;
import com.pivot.aham.api.service.service.SaxoAccountOrderService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.support.cache.RedissonHelper;
import com.pivot.aham.common.core.support.file.excel.ExportExcel;
import com.pivot.aham.common.core.support.file.ftp.FTPClientUtil;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.ExchangeRateTypeEnum;
import com.pivot.aham.common.enums.analysis.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理用户提现划款
 *
 * @author addison
 * @since 2018年12月06日
 */
/*@ElasticJobConf(name = "WithdrawalSaxoToUobJob_2",
        cron = "0 45 15 * * ?",
        shardingItemParameters = "0=1",
        shardingTotalCount = 1,
        description = "交易05_交易分析#生成SAXO到UOB转账指令", eventTraceRdbDataSource = "dataSource")
@Slf4j
public class WithdrawalSaxoToUobJobImpl implements SimpleJob, WithdrawalSaxoToUobJob {
    @Autowired
    private RedeemApplyService bankVARedeemService;
    @Autowired
    private RedissonHelper redissonHelper;
    @Resource
    private WithdrawalSaxoToUobTransSupport withdrawalSaxoToUobTransSupport;
    @Resource
    private SaxoTradeRemoteService saxoTradeRemoteService;
    @Autowired
    private SaxoAccountOrderService saxoAccountOrderService;
    @Resource
    private EventBus eventBus;
    @Resource
    private ExchangeRateService exchangeRateService;

    @Resource
    private PivotCharityDetailRemoteService pivotCharityDetailRemoteService;

    @Override
    public String withdrawalSaxoToUob() {
        //存入实时汇率 
        // Get current ExchangeRateUsdToSgd
        BigDecimal actualTimeRate = exchangeRateService.getActualTimeRate();
        ExchangeRatePO exchangeRatePO = new ExchangeRatePO();
        exchangeRatePO.setExchangeRateType(ExchangeRateTypeEnum.SAXO_FXRT2);
        exchangeRatePO.setUsdToSgd(actualTimeRate);
        exchangeRatePO.setRateDate(DateUtils.getDate(DateUtils.now(), 0, 0, 0));
        exchangeRateService.saveDailyExchangeRate(exchangeRatePO);
        //查询待提现申请单
        List<RedeemApplyPO> waitApplyRedeemList = getWaitRedeemApply();
        if (CollectionUtils.isEmpty(waitApplyRedeemList)) {
            log.error("没有待申请的saxoToUob转账单");
            return "";
        }
        //产生一个saxo-uob的批次号(每日更新)
        String date = DateUtils.formatDate(new Date(), "yyyyMMdd");
        String saxoToUobBatchId = getSaxoToUobBatchId(date);
        //总转账金额
        List<RedeemApplyPO> redeemApplyPOList = Lists.newArrayList();
        //记录本次所有的购汇汇率求平均值
        /**
         * 处理提现时候的Saxo内部购汇
         */
/*        BigDecimal totalAmount = handleSaxoExchange(waitApplyRedeemList, saxoToUobBatchId);
        String fileName = "";
        if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            SaxoToUobTotalRecordPO saxoToUobTotalRecord = new SaxoToUobTotalRecordPO();
            saxoToUobTotalRecord.setSaxoToUobBatchId(saxoToUobBatchId);
            saxoToUobTotalRecord.setResidualAmount(totalAmount);
            saxoToUobTotalRecord.setConfirmedAmount(BigDecimal.ZERO);
            saxoToUobTotalRecord.setIntendAmount(totalAmount);
            saxoToUobTotalRecord.setTransferDate(DateUtils.now());
            withdrawalSaxoToUobTransSupport.setSaxoToUobTotalRecord(saxoToUobTotalRecord);

            withdrawalSaxoToUobTransSupport.setRedeemApplyPOList(redeemApplyPOList);
            withdrawalSaxoToUobTransSupport.withdrawalSaxoToUob();

            saxoToUobTotalRecord.setCurrency(CurrencyEnum.SGD);
            saxoToUobTotalRecord.setForm("SAXO");
            saxoToUobTotalRecord.setTo("UOB");
            List<SaxoToUobTotalRecordPO> exportList = Lists.newArrayList();
            exportList.add(saxoToUobTotalRecord);
            //生成excel，上传到ftp
            fileName = createRedeemApplyExcelToFtp(exportList, date);
            log.info("生成excel，上传到ftp,完成,fileName:{}", fileName);
        }
        return fileName;
    }

    /**
     * 处理提现时候的Saxo内部购汇
     *
     * @param waitApplyRedeemList
     * @param saxoToUobBatchId
     * @return
     */
/*    private BigDecimal handleSaxoExchange(List<RedeemApplyPO> waitApplyRedeemList, String saxoToUobBatchId) {
        BigDecimal totalConfirmMoney = BigDecimal.ZERO;
        BigDecimal totalApplyMoney = BigDecimal.ZERO;
        BigDecimal totalClientConfirmAbandon = BigDecimal.ZERO;
        for (RedeemApplyPO vaRedeemApply : waitApplyRedeemList) {
            totalApplyMoney = totalApplyMoney.add(vaRedeemApply.getConfirmAmount());
        }
        //saxo内部购汇
        InterAccountTransferReq req = new InterAccountTransferReq();
        req.setApplyAmount(totalApplyMoney);
        log.info("处理提现,购汇申请,req:{}", JSON.toJSONString(req));
        RpcMessage<InterAccountTransferResult> accountTransferResult = saxoTradeRemoteService.transferToSGDFromUSD(req);
        log.info("处理提现,购汇result,accountTransferResult:{}", JSON.toJSONString(accountTransferResult));
        //分账
        if (RpcMessageStandardCode.OK.value() == accountTransferResult.getResultCode()) {
            Long exchangeTotalOrderId = Sequence.next();
            int handleNum = 0;
            BigDecimal successMoney = BigDecimal.ZERO;
            totalConfirmMoney = accountTransferResult.getContent().getSuccessAmount();
            for (RedeemApplyPO vaRedeemApply : waitApplyRedeemList) {
                List<SaxoAccountOrderPO> saxoAccountOrderPOAdds = Lists.newArrayList();
                handleNum++;
                BigDecimal confirmMoney = vaRedeemApply.getConfirmAmount().multiply(totalConfirmMoney).divide(totalApplyMoney, 6, BigDecimal.ROUND_DOWN);
                if (handleNum == waitApplyRedeemList.size()) {
                    //最后一个用户兜底
                    confirmMoney = totalConfirmMoney.subtract(successMoney).setScale(6, BigDecimal.ROUND_DOWN);
                }
                successMoney = successMoney.add(confirmMoney).setScale(6, BigDecimal.ROUND_DOWN);
                vaRedeemApply.setSaxoToUobTransferStatus(SaxoToUobTransferStatusEnum.EXCHANGEREADY);
                vaRedeemApply.setSaxoToUobBatchId(saxoToUobBatchId);
                vaRedeemApply.setSaxoToUobTransferStatus(SaxoToUobTransferStatusEnum.APPLYING);
                //最终提现的金额
                BigDecimal clientConfirmMoneyLast = confirmMoney.setScale(2, BigDecimal.ROUND_DOWN);
                totalClientConfirmAbandon = totalClientConfirmAbandon.add(clientConfirmMoneyLast);
                //剩余的金额
                BigDecimal clientConfirmAbandon = confirmMoney.subtract(clientConfirmMoneyLast);

                vaRedeemApply.setConfirmAmountInSgd(clientConfirmMoneyLast);
                vaRedeemApply.setConfirmAbandonAmount(clientConfirmAbandon);
                bankVARedeemService.updateOrInsert(vaRedeemApply);

                //usd入
                SaxoAccountOrderPO exchangeInUsd = new SaxoAccountOrderPO();
                exchangeInUsd.setAccountId(vaRedeemApply.getAccountId());
                exchangeInUsd.setClientId(vaRedeemApply.getClientId());
                exchangeInUsd.setExchangeOrderNo(0L);
                exchangeInUsd.setId(Sequence.next());
                exchangeInUsd.setCurrency(CurrencyEnum.MYR);
                exchangeInUsd.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
                exchangeInUsd.setOperatorType(SaxoOrderTradeTypeEnum.COME_INTO);
                exchangeInUsd.setActionType(SaxoOrderActionTypeEnum.REDEEM);
                exchangeInUsd.setTradeTime(DateUtils.now());
                exchangeInUsd.setCashAmount(vaRedeemApply.getConfirmAmount());
                exchangeInUsd.setBankOrderNo(StringUtils.EMPTY);
                exchangeInUsd.setUpdateTime(DateUtils.now());
                exchangeInUsd.setCreateTime(DateUtils.now());
                exchangeInUsd.setGoalId(vaRedeemApply.getGoalId());
                exchangeInUsd.setRedeemApplyId(vaRedeemApply.getId());
                exchangeInUsd.setExchangeTotalOrderId(exchangeTotalOrderId);
                //Usd出
                SaxoAccountOrderPO exchangeOutUsd = new SaxoAccountOrderPO();
                exchangeOutUsd.setAccountId(vaRedeemApply.getAccountId());
                exchangeOutUsd.setClientId(vaRedeemApply.getClientId());
                exchangeOutUsd.setExchangeOrderNo(0L);
                exchangeOutUsd.setId(Sequence.next());
                exchangeOutUsd.setCurrency(CurrencyEnum.MYR);
                exchangeOutUsd.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
                exchangeOutUsd.setOperatorType(SaxoOrderTradeTypeEnum.COME_OUT);
                exchangeOutUsd.setActionType(SaxoOrderActionTypeEnum.REDEEM_EXCHANGE);
                exchangeOutUsd.setTradeTime(DateUtils.now());
                exchangeOutUsd.setCashAmount(vaRedeemApply.getConfirmAmount());
                exchangeOutUsd.setBankOrderNo(StringUtils.EMPTY);
                exchangeOutUsd.setUpdateTime(DateUtils.now());
                exchangeOutUsd.setCreateTime(DateUtils.now());
                exchangeOutUsd.setGoalId(vaRedeemApply.getGoalId());
                exchangeOutUsd.setRedeemApplyId(vaRedeemApply.getId());
                exchangeOutUsd.setExchangeTotalOrderId(exchangeTotalOrderId);

                //Sgd入
                SaxoAccountOrderPO exchangeInSgd = new SaxoAccountOrderPO();
                exchangeInSgd.setAccountId(vaRedeemApply.getAccountId());
                exchangeInSgd.setClientId(vaRedeemApply.getClientId());
                exchangeInSgd.setExchangeOrderNo(0L);
                exchangeInSgd.setId(Sequence.next());
                exchangeInSgd.setCurrency(CurrencyEnum.SGD);
                exchangeInSgd.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
                exchangeInSgd.setOperatorType(SaxoOrderTradeTypeEnum.COME_INTO);
                exchangeInSgd.setActionType(SaxoOrderActionTypeEnum.REDEEM_EXCHANGE);
                exchangeInSgd.setTradeTime(DateUtils.now());
                exchangeInSgd.setCashAmount(confirmMoney);
                exchangeInSgd.setBankOrderNo(StringUtils.EMPTY);
                exchangeInSgd.setUpdateTime(DateUtils.now());
                exchangeInSgd.setCreateTime(DateUtils.now());
                exchangeInSgd.setGoalId(vaRedeemApply.getGoalId());
                exchangeInSgd.setRedeemApplyId(vaRedeemApply.getId());
                exchangeInSgd.setExchangeTotalOrderId(exchangeTotalOrderId);
                //Sgd出
                SaxoAccountOrderPO exchangeOutSgd = new SaxoAccountOrderPO();
                exchangeOutSgd.setAccountId(vaRedeemApply.getAccountId());
                exchangeOutSgd.setClientId(vaRedeemApply.getClientId());
                exchangeOutSgd.setExchangeOrderNo(0L);
                exchangeOutSgd.setId(Sequence.next());
                exchangeOutSgd.setCurrency(CurrencyEnum.SGD);
                exchangeOutSgd.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
                exchangeOutSgd.setOperatorType(SaxoOrderTradeTypeEnum.COME_OUT);
                exchangeOutSgd.setActionType(SaxoOrderActionTypeEnum.SAXOTOUOB);
                exchangeOutSgd.setTradeTime(DateUtils.now());
                exchangeOutSgd.setCashAmount(confirmMoney);
                exchangeOutSgd.setBankOrderNo(StringUtils.EMPTY);
                exchangeOutSgd.setUpdateTime(DateUtils.now());
                exchangeOutSgd.setCreateTime(DateUtils.now());
                exchangeOutSgd.setGoalId(vaRedeemApply.getGoalId());
                exchangeOutSgd.setRedeemApplyId(vaRedeemApply.getId());
                exchangeOutSgd.setExchangeTotalOrderId(exchangeTotalOrderId);

                saxoAccountOrderPOAdds.add(exchangeInSgd);
                saxoAccountOrderPOAdds.add(exchangeOutSgd);
                saxoAccountOrderPOAdds.add(exchangeInUsd);
                saxoAccountOrderPOAdds.add(exchangeOutUsd);
                saxoAccountOrderService.saveBatch(saxoAccountOrderPOAdds);

                buildThreadSaveCharityFee(vaRedeemApply.getId(), vaRedeemApply.getConfirmAbandonAmount(), pivotCharityDetailRemoteService);

                StaticRateForAccountEvent staticRateForAccountEvent = new StaticRateForAccountEvent();
                staticRateForAccountEvent.setAccountId(vaRedeemApply.getAccountId());
                //计算汇率
                BigDecimal fxRate = confirmMoney.divide(vaRedeemApply.getConfirmAmount(), 6, BigDecimal.ROUND_HALF_DOWN);
                staticRateForAccountEvent.setFxRate(fxRate);
                staticRateForAccountEvent.setFxRateTypeEnum(FxRateTypeEnum.FUNDOUT);
                eventBus.post(staticRateForAccountEvent);

            }
        }
        if(totalConfirmMoney.compareTo(BigDecimal.ZERO)>0 && totalApplyMoney.compareTo(BigDecimal.ZERO)>0) {
            ExchangeRatePO exchangeRateQuery = new ExchangeRatePO();
            exchangeRateQuery.setExchangeRateType(ExchangeRateTypeEnum.SAXO_FXRT2);
            exchangeRateQuery.setRateDate(DateUtils.getDate(DateUtils.now(), 0, 0, 0));
            ExchangeRatePO exchangeRate = exchangeRateService.getExchangeRate(exchangeRateQuery);

            ExchangeRatePO exchangeRatePO = new ExchangeRatePO();
            exchangeRatePO.setExchangeRateType(ExchangeRateTypeEnum.SAXO_FXRT2);
            exchangeRatePO.setUsdToSgd(totalConfirmMoney.divide(totalApplyMoney, 6, BigDecimal.ROUND_DOWN));
            exchangeRatePO.setRateDate(DateUtils.getDate(DateUtils.now(), 0, 0, 0));
            if (exchangeRate != null) {
                exchangeRatePO.setId(exchangeRate.getId());
            }
            exchangeRateService.updateDailyExchangeRate(exchangeRatePO);
        }

        return totalClientConfirmAbandon;
    }

    /**
     * 生成excel，上传到ftp
     *
     * @param exportList
     * @param date
     */
/*    private String createRedeemApplyExcelToFtp(List<SaxoToUobTotalRecordPO> exportList, String date) {
        ExportExcel exportExcel = new ExportExcel(null, SaxoToUobTotalRecordPO.class);
        exportExcel.setDataList(exportList);
        String fileName = date + "_order.xlsx";
        String ftpPath = PropertiesUtil.getString("ftp.pivot.order") + "/order/saxoOfflineOrder/" + fileName;
        try (OutputStream outputStream = FTPClientUtil.getFtpOutPutStream(ftpPath)) {
            exportExcel.write(outputStream);
        } catch (IOException e) {
            throw new BusinessException("生产SAXO TO UOB指令文件错误", e);
        } finally {
            exportExcel.dispose();
        }
        return fileName;
    }

    /**
     * 产生一个saxo-uob的批次号(每日更新)
     *
     * @param date
     * @return
     */
/*    private String getSaxoToUobBatchId(String date) {
        Long incr = redissonHelper.incr(date);
        redissonHelper.expireAt(date, DateUtils.dayEnd(new Date()).getTime());
        String saxoToUobBatchId = date + incr;
        log.info("产生一个saxo-uob的批次号(每日更新),saxoToUobBatchId:{}", saxoToUobBatchId);
        return saxoToUobBatchId;
    }

    /**
     * //过滤数据:过滤出需要从投资资产中提现的申请
     *
     * @return
     */
/*    private List<RedeemApplyPO> getWaitRedeemApply() {
        RedeemApplyPO vaRedeemApplyPO = new RedeemApplyPO();
        vaRedeemApplyPO.setSaxoToUobTransferStatus(SaxoToUobTransferStatusEnum.WAITAPPLY);
        List<RedeemApplyPO> vaRedeemApplyList = bankVARedeemService.queryList(vaRedeemApplyPO);

        //etf执行成功
        List<RedeemApplyPO> waitApplyRedeemList = vaRedeemApplyList.stream().
                filter(input -> input.getWithdrawalSourceType() == WithdrawalSourceTypeEnum.FROMGOAL
                        && EtfExecutedStatusEnum.SUCCESS == input.getEtfExecutedStatus()).collect(Collectors.toList());
        log.info("过滤出需要从投资资产中提现的申请:{}", JSON.toJSONString(waitApplyRedeemList));
        return waitApplyRedeemList;
    }

    private void buildThreadSaveCharityFee(Long redeemApplyId, BigDecimal clientConfirmAbandon, PivotCharityDetailRemoteService pivotCharityDetailRemoteService) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                saveCharityFee(redeemApplyId, clientConfirmAbandon, pivotCharityDetailRemoteService);
            }
        });
        thread.start();
    }

    private void saveCharityFee(Long redeemApplyId, BigDecimal clientConfirmAbandon, PivotCharityDetailRemoteService pivotCharityDetailRemoteService) {
        try {//挂了不影响其他业务
            List<PivotCharityDetailDTO> pivotCharityDetailDTOs = com.google.common.collect.Lists.newArrayList();
            PivotCharityDetailDTO pivotCharityDetailDTO = new PivotCharityDetailDTO();
            pivotCharityDetailDTO.setRedeemId(redeemApplyId);
            pivotCharityDetailDTO.setOperateTime(DateUtils.now());
            pivotCharityDetailDTO.setOperateMoney(clientConfirmAbandon);
            pivotCharityDetailDTO.setOperateType(OperateTypeEnum.RECHARGE);
            pivotCharityDetailDTOs.add(pivotCharityDetailDTO);
            pivotCharityDetailRemoteService.savePivotCharityDetail(pivotCharityDetailDTOs);
        } catch (Exception e) {
            log.error("保存pivotFeeDetail错误 {}", e.getMessage(), e);
        }
    }


    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            withdrawalSaxoToUob();
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
    }
}
*/