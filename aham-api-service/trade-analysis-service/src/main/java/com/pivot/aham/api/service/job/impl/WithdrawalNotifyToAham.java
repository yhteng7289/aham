package com.pivot.aham.api.service.job.impl;

import cn.hutool.core.date.DateUtil;
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
import com.pivot.aham.common.core.support.email.Email;
import com.pivot.aham.common.core.support.file.excel.ExportExcel;
import com.pivot.aham.common.core.support.file.ftp.FTPClientUtil;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.EmailUtil;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.ExchangeRateTypeEnum;
import com.pivot.aham.common.enums.analysis.*;
import java.io.ByteArrayOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import org.springframework.stereotype.Component;

/**
 * 处理用户提现划款
 *
 * @author addison
 * @since 2018年12月06日
 */
//@ElasticJobConf(name = "WithdrawalNotifyToAham_2",
//        cron = "0 15 15 * * ?",
//        shardingItemParameters = "0=1",
//        shardingTotalCount = 1,
//        description = "交易05_交易分析#生成SAXO到UOB转账指令", eventTraceRdbDataSource = "dataSource")
@Slf4j
@Component
public class WithdrawalNotifyToAham implements SimpleJob, WithdrawalSaxoToUobJob {
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
    
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    private final String emailReceiver = PropertiesUtil.getString("aham.redeem.email.receiver");
    
    private final String emailTopic = PropertiesUtil.getString("aham.redeem.email.topic");
    
    private final String emailFilename = PropertiesUtil.getString("aham.redeem.email.filename");

    @Override
    public void withdrawalSaxoToUob() {

        //查询待提现申请单
        List<RedeemApplyPO> waitApplyRedeemList = getWaitRedeemApply();
        if (CollectionUtils.isEmpty(waitApplyRedeemList)) {
            log.error("Without Redeem Payout Order");
            //return "";
            return;
        }
        String date = DateUtils.formatDate(new Date(), "yyyyMMdd");
        String batchId = getSaxoToUobBatchId(date);
        
        List<RedeemApplyPO> listRedeemApply = Lists.newArrayList();
        for(RedeemApplyPO redeemApplyPO:waitApplyRedeemList){
            redeemApplyPO.setSaxoToUobBatchId(batchId);
            listRedeemApply.add(redeemApplyPO);
        }
        
        boolean isEmailGen = generateOrderExcel(listRedeemApply);
        if(isEmailGen){
            for(RedeemApplyPO redeemApplyPO:waitApplyRedeemList){
                redeemApplyPO.setRedeemApplyStatus(RedeemApplyStatusEnum.SUCCESS);
                redeemApplyPO.setSaxoToUobTransferStatus(SaxoToUobTransferStatusEnum.APPLYSUCCESS);
                redeemApplyPO.setSaxoToUobBatchId(batchId);
                redeemApplyPO.setConfirmTime(DateUtils.now());
                bankVARedeemService.updateOrInsert(redeemApplyPO);
            }
        }

    }

    private String getSaxoToUobBatchId(String date) {
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
    private List<RedeemApplyPO> getWaitRedeemApply() {
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
    
    public boolean generateOrderExcel(List<RedeemApplyPO> lOrderPO){
        boolean isSuccess = true;
        Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.MONTH, -1);
        log.info("calendar time {} ", calendar.getTime());
        String reportDate = sdf.format(calendar.getTime());
        log.info("report date {} ", reportDate);

        //List<UserAssetReport> userAssetList = userAssetReportingImpl.getUserAssetReportingByDate(reportDate);
        ExportExcel exportExcel = new ExportExcel(null, RedeemApplyPO.class);
        exportExcel.setDataList(lOrderPO);
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            log.info("Start Writing data into excel file ...");
            exportExcel.write(os);
            log.info("Done writing data into excel file ...");
            BodyPart bodyPart = new MimeBodyPart();
            ByteArrayDataSource dataSource = new ByteArrayDataSource(os.toByteArray(), "application/vnd.ms-excel");
            bodyPart.setDataHandler(new DataHandler(dataSource));
            bodyPart.setFileName(emailFilename +" "+reportDate +".xlsx");

            Email email = new Email();
            //email.setEnv(env);
            email.setSSL(true);
            email.setBodyPart(bodyPart);
            email.setSendTo(emailReceiver);
            email.setTopic(reportDate + "-" + emailTopic);
            email.setBody(reportDate + ", Kindly find attachment");
            log.info("Start Sending userAssetReport excel file to {} ", emailReceiver);
            EmailUtil.sendEmail(email);
            log.info("Done Sending userAssetReport excel file to {} ", emailReceiver);
        } catch (IOException | MessagingException e) { isSuccess = false;

        } finally {
            exportExcel.dispose();
        }
        
        return isSuccess;
    }
}
