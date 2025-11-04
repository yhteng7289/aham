package com.pivot.aham.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pivot.aham.api.server.dto.AccountTotalAssetDTO;
import com.pivot.aham.api.server.dto.DividendCallBackDTO;
import com.pivot.aham.api.server.dto.PivotFeeDetailDTO;
import com.pivot.aham.api.server.dto.ProductStatisDTO;
import com.pivot.aham.api.server.dto.resp.SaxoStatisShareTradesDTO;
import com.pivot.aham.api.server.remoteservice.AssetServiceRemoteService;
import com.pivot.aham.api.server.remoteservice.DividendRemoteService;
import com.pivot.aham.api.server.remoteservice.PivotFeeDetailRemoteService;
import com.pivot.aham.api.server.remoteservice.SaxoTradeRemoteService;
import com.pivot.aham.api.service.SaxoStatisticService;
import com.pivot.aham.api.service.mapper.SaxoAccountStatusMapper;
import com.pivot.aham.api.service.mapper.SaxoBalanceMapper;
import com.pivot.aham.api.service.mapper.SaxoBalanceToAccountRecordMapper;
import com.pivot.aham.api.service.mapper.SaxoBookkeepingCashMapper;
import com.pivot.aham.api.service.mapper.SaxoCashTransactionsMapper;
import com.pivot.aham.api.service.mapper.SaxoShareDividEndMapper;
import com.pivot.aham.api.service.mapper.SaxoShareOpenPositionsMapper;
import com.pivot.aham.api.service.mapper.SaxoShareTradesExecutedMapper;
import com.pivot.aham.api.service.mapper.model.SaxoAccountStatusPO;
import com.pivot.aham.api.service.mapper.model.SaxoBalCashPO;
import com.pivot.aham.api.service.mapper.model.SaxoBalETFHoldMoneyPO;
import com.pivot.aham.api.service.mapper.model.SaxoBalHoldMoneyPO;
import com.pivot.aham.api.service.mapper.model.SaxoBalOfAccNoticePO;
import com.pivot.aham.api.service.mapper.model.SaxoBalTradeOrderPO;
import com.pivot.aham.api.service.mapper.model.SaxoBookkeepingCashPO;
import com.pivot.aham.api.service.mapper.model.SaxoCashTransactionsPO;
import com.pivot.aham.api.service.mapper.model.SaxoReconBalCashPO;
import com.pivot.aham.api.service.mapper.model.SaxoShareDividEndPO;
import com.pivot.aham.api.service.mapper.model.SaxoShareOpenPositionsPO;
import com.pivot.aham.api.service.mapper.model.SaxoShareTradesExecutedPO;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.exception.DataParseException;
import com.pivot.aham.common.core.exception.MessageException;
import com.pivot.aham.common.core.support.context.ApplicationContextHolder;
import com.pivot.aham.common.core.support.email.Email;
import com.pivot.aham.common.core.support.file.ftp.FTPClientUtil;
import com.pivot.aham.common.core.support.file.ftp.SftpClient;
import com.pivot.aham.common.core.util.DataUtil;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.EmailUtil;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.core.util.InstanceUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;
import com.pivot.aham.common.enums.BalanceOfAccountType;
import com.pivot.aham.common.enums.SaxoOrderTypeEnum;
import com.pivot.aham.common.enums.TransferStatusEnum;
import com.pivot.aham.common.enums.analysis.CaEventTypeEnum;
import com.pivot.aham.common.enums.analysis.FeeTypeEnum;
import com.pivot.aham.common.enums.analysis.OperateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SaxoStatisticServiceImpl extends BaseServiceImpl<SaxoReconBalCashPO, SaxoBalanceMapper> implements SaxoStatisticService {

    @Autowired
    private SaxoShareDividEndMapper saxoShareDividEndMapper;
    @Autowired
    private DividendRemoteService dividendRemoteService;
    @Autowired
    private AssetServiceRemoteService assetServiceRemoteService;

    @Autowired
    private SaxoAccountStatusMapper saxoAccountStatusMapper;

    @Autowired
    private SaxoBookkeepingCashMapper saxoBookkeepingCashMapper;

    @Autowired
    private SaxoShareTradesExecutedMapper saxoShareTradesExecutedMapper;

    @Autowired
    private SaxoTradeRemoteService saxoTradeRemoteService;
    @Autowired
    private SaxoShareOpenPositionsMapper saxoShareOpenPositionsMapper;
    @Autowired
    private SaxoCashTransactionsMapper saxoCashTransactionsMapper;

    @Autowired
    private SaxoBalanceToAccountRecordMapper saxoBalanceToAccountRecordMapper;


    @Autowired
    private PivotFeeDetailRemoteService pivotFeeDetailRemoteService;

    private static String getEnvRemark() {
        return PropertiesUtil.getString("email.env.name");
    }

    private final static String USD = "USD";

    private final static String SAXO_FEE = "Client Custody Fee";
    //saxo读取文件时间格式
    private final static String SAXO_DATE = "yyyyMMdd";

    private final static String SAXO_TIME = "yyyyMMdd HH:mm:ss";

    //    static final String NOTICE_TO_ADD = "jinling.cui@pintec.com,yi.zhang@pintec.com,chengchen.huo@pintec.com,yichen.jia@pintec.com";
    static final String NOTICE_TO_ADD = PropertiesUtil.getString("pivot.error.alert.email");

    private final static Integer ZERO = 0;

    private final static BigDecimal ONE = BigDecimal.ONE;
    private final static BigDecimal TEN = BigDecimal.TEN;
    private final static String CONTACT_Email = PropertiesUtil.getString("saxo.balance.warn.contatctemail");
    private final static boolean openSend = PropertiesUtil.getBoolean("env.error.log.send");


    private static ExecutorService executorService = new ThreadPoolExecutor(2, 20, 1, TimeUnit.SECONDS,
            new ArrayBlockingQueue(20), new ThreadPoolExecutor.DiscardOldestPolicy());

    @Override
    public void shareDividEnd(Date loadDate) {
        Date nowDate = DateUtils.now();
        if (DataUtil.isNotEmpty(loadDate)) {
            nowDate = loadDate;
        }
        log.info("saxo日终分红job start");
        SaxoShareDividEndPO saxoShareDividEndPO = new SaxoShareDividEndPO();
        saxoShareDividEndPO.setTransferStatusEnum(TransferStatusEnum.FAIL);
        List<SaxoShareDividEndPO> shareFails = saxoShareDividEndMapper.getListByCond(saxoShareDividEndPO);
        StringBuffer msgFail = new StringBuffer();
        if (DataUtil.isNotEmpty(shareFails)) {
            shareFails.stream().forEach(input -> {
                try {
                    RpcMessage rpcMessage = dividendRemoteService.dividendCallBack(new DividendCallBackDTO(
                            input.getExDate(), input.getTradeDate(), input.getValueDate()
                            , CaEventTypeEnum.forValue(input.getCaEventTypeId().intValue())
                            , input.getNetAmountAccountCurrency(), input.getInstrumentCode(), input.getCaEventId().toString()));
                    if (rpcMessage.isSuccess()) {
                        input.setTransferStatusEnum(TransferStatusEnum.SUCCESS);
                    } else {
                        input.setTransferStatusEnum(TransferStatusEnum.FAIL);
                    }
                    input.setUpdateDate(DateUtils.now());
                } catch (Exception e) {
                    msgFail.append(String.format("分红关联单号:%s,e:%s \n", input.getCaEventId(), e));
                }
            });
            saxoShareDividEndMapper.batchUpdate(shareFails);
            if (StringUtils.isNotEmpty(msgFail.toString())) {
                ErrorLogAndMailUtil.logError(log, msgFail.toString());
            }
        }

        Integer count = saxoShareDividEndMapper.getCountByCond(new SaxoShareDividEndPO().setCreateDate(DateUtils.getStartDate(nowDate)).setUpdateDate(DateUtils.dayEnd(nowDate)));
        if (count > ZERO) {
            return;
        }

        String yesterdayDate = DateUtils.formatDate(DateUtils.addDays(nowDate, -1), "dd-MM-yyyy");
        String fileName = "/ShareDividend_" + PropertiesUtil.getString("sftp.account.name") + yesterdayDate + ".txt";
        List<String> lines = Lists.newArrayList();
        try {
            String activeProfile = ApplicationContextHolder.getActiveProfile();
            if(!activeProfile.equals("prod")){
                String filePath = PropertiesUtil.getString("ftp.pivot.uob.balance") + "/" + fileName;
                lines = FTPClientUtil.readFileContent(filePath,false);
            }else{
                SftpClient sftpClient = SftpClient.saxoConnect();
                lines = sftpClient.getsaxoFile(PropertiesUtil.getString("sftp.saxo.baseDir") + fileName, true);
            }
        } catch (Exception e) {
            log.error("saxo日终分红文件读取失败，e:{}", e);
            return;
        }
        if (DataUtil.isEmpty(lines)) {
            log.warn("saxo日终分红文件数据为空");
            return;
        }
        List<SaxoShareDividEndPO> saxoShareDividEndPOS = handDividData(lines, nowDate);


        log.info("share dividEnd datas:{}", JSON.toJSON(saxoShareDividEndPOS));
        StringBuffer messageError = new StringBuffer();

        if (DataUtil.isNotEmpty(saxoShareDividEndPOS)) {
            saxoShareDividEndPOS.stream().forEach(input -> {
                try {
                    RpcMessage rpcMessage = dividendRemoteService.dividendCallBack(new DividendCallBackDTO(
                            input.getExDate(), input.getTradeDate(), input.getValueDate()
                            , CaEventTypeEnum.forValue(input.getCaEventTypeId().intValue())
                            , input.getNetAmountAccountCurrency(), input.getInstrumentCode(), input.getCaEventId().toString()));
                    if (rpcMessage.isSuccess()) {
                        input.setTransferStatusEnum(TransferStatusEnum.SUCCESS);
                    } else {
                        input.setTransferStatusEnum(TransferStatusEnum.FAIL);
                    }
                } catch (Exception e) {
                    messageError.append(String.format("分红关联单号:%s,e:%s \n", input.getCaEventId(), e));
                }
            });
            saxoShareDividEndMapper.batchInsert(saxoShareDividEndPOS);
            if (StringUtils.isNotEmpty(messageError.toString())) {
                ErrorLogAndMailUtil.logError(log, messageError.toString());
            }
        }
        log.info("saxo日终分红job end");

    }


    @Override
    public void totalStatisEnd(Date date) {
        Date balDate = DateUtils.now();
        if(date != null){
            balDate = date;
        }
        log.info("saxo日终总资产对账job start");
        SaxoAccountStatusPO statusPO = saxoAccountStatusMapper.getDataByCond(new SaxoAccountStatusPO(PropertiesUtil.getString("saxo.openApi.usd.accountId"), DateUtils.getStartDate(DateUtils.now())));
        SaxoAccountStatusPO saxoAccountStatusPO = statusPO;
        Date yesterdayTime = DateUtils.addDays(DateUtils.now(), -1);

        String yesterdayDate = DateUtils.formatDate(yesterdayTime, "dd-MM-yyyy");
        String fileName = "/AccountStatus_" + PropertiesUtil.getString("sftp.account.name") + yesterdayDate + ".txt";
        if (statusPO == null) {
            SftpClient sftpClient = SftpClient.saxoConnect();

            List<String> lines = Lists.newArrayList();
            try {
                lines = sftpClient.getsaxoFile(PropertiesUtil.getString("sftp.saxo.baseDir") + fileName, true);
//                lines = FileUtil.readFile("/Users/jinling.cui/Documents/IdeaProjects/pivot/aham/aham-api-service/aggregation-service/src/main/resources/config/dev/AccountStatus_9349835_17-07-2019.txt");
//                lines.remove(0);
            } catch (Exception e) {
                log.error("saxo日终总资产对账文件读取失败，e:{}", e);
                return;
            }
            if (DataUtil.isEmpty(lines)) {
                log.warn("saxo日终总资产对账文件数据为空");
                return;
//                ErrorLogAndMailUtil.logError(log, "AccountStatus_" + currentTime + ".txt 文件无数据");
            }
            List<SaxoAccountStatusPO> saxoAccountStatusPOS = handleTotalAccountData(lines);
            Optional<SaxoAccountStatusPO> optional = saxoAccountStatusPOS.stream().filter(input -> input.getAccount().equals(PropertiesUtil.getString("saxo.openApi.usd.accountId"))).findFirst();
            if (optional.isPresent()) {
                saxoAccountStatusPO = optional.get();
            }
            log.info("share AccountStatus datas:{}", JSON.toJSON(saxoAccountStatusPOS));
            saxoAccountStatusMapper.batchInsert(saxoAccountStatusPOS);
        }
        if (saxoAccountStatusPO == null) {
            log.error("saxo日终总资产对账文件数据USD");
            return;
        }

        RpcMessage<AccountTotalAssetDTO> rpcMessage = assetServiceRemoteService.queryAccountTotalInfo(balDate);
        if (!rpcMessage.isSuccess()) {
            throw new MessageException(rpcMessage.getErrMsg());
        }
        AccountTotalAssetDTO accountTotalAssetDTO = rpcMessage.getContent();

        String baseStr = "对账异常_";
        String currentDate = DateUtils.formatDate(balDate, "yyyyMMdd");

        log.info("余额总账对账:saxo-余额:{},das-余额:{}", saxoAccountStatusPO.getBalance(), accountTotalAssetDTO.getTotalCash());

        BigDecimal dasSubSaxoCash = accountTotalAssetDTO.getTotalCash().subtract(saxoAccountStatusPO.getBalance());
        SaxoBalCashPO saxoBalCashPO = new SaxoBalCashPO(saxoAccountStatusPO.getBalance().setScale(6, BigDecimal.ROUND_DOWN), accountTotalAssetDTO.getTotalCash().setScale(6, BigDecimal.ROUND_DOWN), dasSubSaxoCash.setScale(6, BigDecimal.ROUND_DOWN), "金额不匹配", fileName, generateNum(), currentDate);

        if (dasSubSaxoCash.abs().compareTo(ONE) > ZERO) {
            String topic = baseStr + "余额对账_" + currentDate + "【" + getEnvRemark() + "】";
            saxoBalanceToAccountRecordMapper.deleteBalOfAccNotice(new SaxoBalOfAccNoticePO(balDate, BalanceOfAccountType.CASH_BAL));
            if (accountTotalAssetDTO.getTotalCash().subtract(saxoAccountStatusPO.getBalance()).compareTo(TEN) > ZERO) {
                saxoBalanceToAccountRecordMapper.insertBalOfAccNotice(new SaxoBalOfAccNoticePO(balDate, BalanceOfAccountType.CASH_BAL, saxoBalCashPO.getTransNumber()));
            }
            log.info("余额对账邮件------");
            Email email = new Email()
                    .setTemplateName("SaxoBalCashWarn")
                    .setTemplateVariables(InstanceUtil.newHashMap("balCashVO", saxoBalCashPO))
                    .setSendTo(CONTACT_Email)
                    .setTopic(topic);
            EmailUtil.sendEmail(email);
        } else {
            sendMail("对账[" + getEnvRemark() + "]", "余额对账ok");
        }
        BigDecimal bigDecimal = accountTotalAssetDTO.getTotalCash().subtract(saxoAccountStatusPO.getBalance());
        if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) {
            saxoBalCashPO.setStatusDes("平账");
        } else {
            saxoBalCashPO.setStatusDes(bigDecimal.compareTo(BigDecimal.ZERO) > 0 ? "业务系统余额大" : "saxo余额大");
        }

        saxoBalanceToAccountRecordMapper.deleteBalCash(new SaxoBalCashPO(currentDate));
        saxoBalanceToAccountRecordMapper.insertBalCash(saxoBalCashPO);
        SaxoBalHoldMoneyPO saxoBalHoldMoneyPO = new SaxoBalHoldMoneyPO(saxoAccountStatusPO.getOpenPositionsStock()
                .setScale(6, BigDecimal.ROUND_DOWN),
                accountTotalAssetDTO.getTotalHold().setScale(6, BigDecimal.ROUND_DOWN), "金额不匹配", fileName, generateNum(), currentDate);




        log.info("总持仓对账:saxo-总持仓:{},das-总持仓:{}", saxoAccountStatusPO.getOpenPositionsStock(), accountTotalAssetDTO.getTotalHold());
        if (saxoAccountStatusPO.getOpenPositionsStock().subtract(accountTotalAssetDTO.getTotalHold()).abs().compareTo(ONE) > ZERO) {
            String topic = baseStr + "总持仓对账_" + currentDate + "【" + getEnvRemark() + "】";
            saxoBalanceToAccountRecordMapper.deleteBalOfAccNotice(new SaxoBalOfAccNoticePO(DateUtils.now(), BalanceOfAccountType.HOLD_BAL));

            if (saxoAccountStatusPO.getOpenPositionsStock().subtract(accountTotalAssetDTO.getTotalHold()).abs().compareTo(TEN) > ZERO) {
                saxoBalanceToAccountRecordMapper.insertBalOfAccNotice(new SaxoBalOfAccNoticePO(DateUtils.now(), BalanceOfAccountType.HOLD_BAL, saxoBalHoldMoneyPO.getTransNumber()));
            }
            Email email = new Email()
                    .setTemplateName("SaxoBalHoldMoneyWarn")
                    .setTemplateVariables(InstanceUtil.newHashMap("balHoldMoney", saxoBalHoldMoneyPO))
                    .setSendTo(CONTACT_Email)
                    .setTopic(topic);
            EmailUtil.sendEmail(email);
        } else {
            sendMail("对账[" + getEnvRemark() + "]", "总持仓对账ok");
        }
        BigDecimal diffHoldMoney = saxoAccountStatusPO.getOpenPositionsStock().subtract(accountTotalAssetDTO.getTotalHold());
        if (diffHoldMoney.compareTo(BigDecimal.ZERO) == 0) {
            saxoBalHoldMoneyPO.setStatusDes("平账");
        } else {
            saxoBalHoldMoneyPO.setStatusDes(diffHoldMoney.compareTo(BigDecimal.ZERO) > 0 ? "saxo总持仓余额大" : "业务系统总持仓余额大");
        }
        saxoBalanceToAccountRecordMapper.deleteBalHoldMoney(new SaxoBalHoldMoneyPO(currentDate));
        saxoBalanceToAccountRecordMapper.insertBalHoldMoney(saxoBalHoldMoneyPO);
        log.info("saxo日终总资产对账job end");
    }


    @Override
    public void recordBookkeepingCash() {
        log.info("saxo记录簿记job start");
        Integer recordCount = saxoBookkeepingCashMapper.getDataByCond(new SaxoBookkeepingCashPO(DateUtils.getStartDate(DateUtils.now())));
        if (recordCount > ZERO) {
            return;
        }
        SftpClient sftpClient = SftpClient.saxoConnect();
        String yesterdayDate = DateUtils.formatDate(DateUtils.addDays(DateUtils.now(), -1), "dd-MM-yyyy");
        String fileName = "/BookkeepingCash_" + PropertiesUtil.getString("sftp.account.name") + yesterdayDate + ".txt";
        List<String> lines = Lists.newArrayList();
        try {
            lines = sftpClient.getsaxoFile(PropertiesUtil.getString("sftp.saxo.baseDir") + fileName, true);
        } catch (Exception e) {
            log.error("saxo记录簿记文件读取失败，e:{}", e);
            return;
        }
        if (DataUtil.isEmpty(lines)) {
            log.warn("saxo记录簿记文件数据为空");
            return;
        }
        List<SaxoBookkeepingCashPO> saxoBookkeepingCashPOS = handleBookkeepingData(lines);
        saxoBookkeepingCashMapper.batchInsert(saxoBookkeepingCashPOS);
        List<PivotFeeDetailDTO> pivotFeeDetailDTOs = Lists.newArrayList();
        saxoBookkeepingCashPOS = saxoBookkeepingCashPOS.stream().filter(input -> input.getBkAmountType().equals(SAXO_FEE.trim())).collect(Collectors.toList());
        saxoBookkeepingCashPOS.stream().forEach(input -> {
            PivotFeeDetailDTO pivotFeeDetailDTO = new PivotFeeDetailDTO();
            pivotFeeDetailDTO.setMoney(input.getAmountAccountCurrency());
            pivotFeeDetailDTO.setAccountId(input.getCounterpartID());
            pivotFeeDetailDTO.setFeeType(FeeTypeEnum.CUST_FEE);
            pivotFeeDetailDTO.setOperateType(OperateTypeEnum.WITHDRAW);
            pivotFeeDetailDTO.setOperateDate(DateUtils.now());
        });

        if (DataUtil.isNotEmpty(pivotFeeDetailDTOs)) {
            try {
                pivotFeeDetailRemoteService.savePivotFeeDetail(pivotFeeDetailDTOs);
            } catch (Exception e) {
                ErrorLogAndMailUtil.logError(log, e);
            }
        }


        log.info("saxo记录簿记job end");
    }

    @Override
    public void statisShareTrades() {
        log.info("saxo_share交易执行job start");

        List<SaxoShareTradesExecutedPO> saxoShareTradesExecutedPOS = saxoShareTradesExecutedMapper.getDataByCond(new SaxoShareTradesExecutedPO(DateUtils.getStartDate(DateUtils.now())));
        Date nowDate = DateUtils.now();
        String currentTime = DateUtils.formatDate(nowDate, "yyyy-MM-dd");
        Date yesterdayTime = DateUtils.addDays(nowDate, -1);
        String yesterdayDate = DateUtils.formatDate(yesterdayTime, "dd-MM-yyyy");
        String fileName = "/ShareTradesExecuted_" + PropertiesUtil.getString("sftp.account.name") + yesterdayDate + ".txt";
        if (DataUtil.isEmpty(saxoShareTradesExecutedPOS)) {
            SftpClient sftpClient = SftpClient.saxoConnect();

            List<String> lines = Lists.newArrayList();
            try {
                lines = sftpClient.getsaxoFile(PropertiesUtil.getString("sftp.saxo.baseDir") + fileName, true);
            } catch (Exception e) {
                log.error("saxo_share交易执行文件读取失败，e:{}", e);
                return;
            }
            if (DataUtil.isEmpty(lines)) {
                sendMail("对账[" + getEnvRemark() + "]", "交易订单对账ok");
                log.warn("saxo_share交易执行文件数据为空");
                return;
            }
            List<SaxoShareTradesExecutedPO> saxoShareTradesExecutedPOS1 = handleShareTradesData(lines);
            saxoShareTradesExecutedMapper.batchInsert(saxoShareTradesExecutedPOS1);
            saxoShareTradesExecutedPOS = saxoShareTradesExecutedPOS1;
        }
        if (DataUtil.isEmpty(saxoShareTradesExecutedPOS)) {
            return;
        }
        //saxo提供数据
        Map<Long, SaxoShareTradesExecutedPO> saxoProvideS = saxoShareTradesExecutedPOS.stream().collect(Collectors.toMap(SaxoShareTradesExecutedPO::getOrderNumber, in -> in));
//        das数据
        RpcMessage<Map<Long, SaxoStatisShareTradesDTO>> rpcMessage = saxoTradeRemoteService.statisShareTreadesExecute(DateUtils.now());


        if (!rpcMessage.isSuccess()) {
            ErrorLogAndMailUtil.logError(log, "获取das中SaxoShareTrade数据异常");
            return;
        }
        Map<Long, SaxoStatisShareTradesDTO> saxoStatisShareTradesDTOMap = rpcMessage.getContent();
        if (DataUtil.isEmpty(saxoStatisShareTradesDTOMap)) {
            saxoStatisShareTradesDTOMap = Maps.newHashMap();
        }
        //邮件显示
        List<SaxoBalTradeOrderPO> saxoBalTradeOrderPOS = Lists.newArrayList();
        //对账流水数据记录
        List<SaxoBalTradeOrderPO> saxoBalTradeOrderPOList = Lists.newArrayList();
        for (Long orderNumber : saxoProvideS.keySet()) {
            boolean flag = false;
            SaxoBalTradeOrderPO saxoBalTradeOrderPO = new SaxoBalTradeOrderPO();
            //das
            SaxoStatisShareTradesDTO saxoStatisShareTradesDTO = saxoStatisShareTradesDTOMap.get(orderNumber);
//          saxo
            SaxoShareTradesExecutedPO saxoShareTradesExecutedPO = saxoProvideS.get(orderNumber);
            saxoBalTradeOrderPO.setProductCode(saxoShareTradesExecutedPO.getInstrumentCode());
            saxoBalTradeOrderPO.setOrderNumber(orderNumber);

            if (saxoStatisShareTradesDTO == null) {
                flag = true;
                saxoBalTradeOrderPO.setDasCommission(BigDecimal.ZERO);
                saxoBalTradeOrderPO.setDasTradeShare(BigDecimal.ZERO);
                saxoBalTradeOrderPO.setSaxoCommission(saxoShareTradesExecutedPO.getCommissionInstrumentCurrency());
                saxoBalTradeOrderPO.setSaxoTradeShare(saxoShareTradesExecutedPO.getTradedAmount());
                saxoBalTradeOrderPO.setStatusDes("持有份额不匹配 && 手续费不匹配");
                saxoBalTradeOrderPO.setFileName(fileName);
                saxoBalTradeOrderPO.setTransNumber(generateNum());
                saxoBalTradeOrderPO.setCompareTime(DateUtils.formatDate(nowDate, "yyyyMMdd"));

                if (flag) {
                    saxoBalTradeOrderPOS.add(saxoBalTradeOrderPO);
                }
                saxoBalTradeOrderPO.setStatusDes("saxo持有份额大 && saxo手续费大");
                saxoBalTradeOrderPOList.add(saxoBalTradeOrderPO);
                continue;
            }
            StringBuffer stringBuffer01 = new StringBuffer();
            log.info("持有份额,productCode:{},saxo-持有份额:{},das-持有份额:{}", saxoShareTradesExecutedPO.getInstrumentCode(), saxoShareTradesExecutedPO.getTradedAmount(), saxoStatisShareTradesDTO.getTradeShares());
            BigDecimal diffTradeShare = saxoShareTradesExecutedPO.getTradedAmount().abs().subtract(saxoStatisShareTradesDTO.getTradeShares().abs());
            if (diffTradeShare.abs().compareTo(BigDecimal.ZERO) != ZERO) {
                stringBuffer01.append("持有份额不匹配");
                flag = true;
            }

            saxoBalTradeOrderPO.setTransNumber(generateNum());

            log.info("手续费,productCode:{},saxo-手续费:{},das-手续费:{}", saxoShareTradesExecutedPO.getInstrumentCode(), saxoShareTradesExecutedPO.getCommissionInstrumentCurrency(), saxoStatisShareTradesDTO.getCommission());
            BigDecimal diffCommission = saxoShareTradesExecutedPO.getCommissionInstrumentCurrency().abs().subtract(saxoStatisShareTradesDTO.getCommission().abs());
            if (diffCommission.abs().compareTo(ONE) > ZERO) {
                stringBuffer01.append("  手续费不匹配");
                flag = true;
            }
            if (saxoStatisShareTradesDTO.getOrderType() == SaxoOrderTypeEnum.BUY) {
                saxoBalTradeOrderPO.setSaxoTradeShare(saxoStatisShareTradesDTO.getTradeShares());
            } else {
                saxoBalTradeOrderPO.setSaxoTradeShare(saxoStatisShareTradesDTO.getTradeShares().negate());
            }
            saxoBalTradeOrderPO.setDasTradeShare(saxoShareTradesExecutedPO.getTradedAmount());
            saxoBalTradeOrderPO.setSaxoCommission(saxoStatisShareTradesDTO.getCommission().negate());
            saxoBalTradeOrderPO.setDasCommission(saxoShareTradesExecutedPO.getCommissionInstrumentCurrency());
            saxoBalTradeOrderPO.setStatusDes(stringBuffer01.toString());
            saxoBalTradeOrderPO.setFileName(fileName);

            saxoBalTradeOrderPO.setCompareTime(DateUtils.formatDate(nowDate, "yyyyMMdd"));
            if (flag) {
                saxoBalTradeOrderPOS.add(saxoBalTradeOrderPO);
            }

            String statusDes = "";
            if (diffTradeShare.compareTo(BigDecimal.ZERO) == 0) {
                statusDes = "份额平账";
            } else {
                statusDes = diffTradeShare.compareTo(BigDecimal.ZERO) > 0 ? "saxo份额大" : "业务系统份额大";
            }
            if (diffCommission.compareTo(BigDecimal.ZERO) == 0) {
                statusDes = statusDes + " 手续费平账";
            } else {
                statusDes = statusDes + (diffCommission.compareTo(BigDecimal.ZERO) > 0 ? "saxo手续费大" : "业务系手续费大");
            }
            saxoBalTradeOrderPO.setStatusDes(statusDes);
            saxoBalTradeOrderPOList.add(saxoBalTradeOrderPO);

        }

        if (DataUtil.isNotEmpty(saxoBalTradeOrderPOS)) {
            String baseStr = "对账异常_";
            String currentDate = DateUtils.formatDate(nowDate, "yyyyMMdd");
            String topic = baseStr + "交易订单对账_" + currentDate + "【" + getEnvRemark() + "】";
            Email email = new Email()
                    .setTemplateName("SaxoTradeOrderWarn")
                    .setTemplateVariables(InstanceUtil.newHashMap("tradeOrderS", saxoBalTradeOrderPOS))
                    .setSendTo(CONTACT_Email)
                    .setTopic(topic);
            EmailUtil.sendEmail(email);
        } else {
            sendMail("对账[" + getEnvRemark() + "]", "交易订单对账ok");
        }
        saxoBalanceToAccountRecordMapper.deleteBalTradeOrder(new SaxoBalTradeOrderPO(currentTime));
        if (DataUtil.isNotEmpty(saxoBalTradeOrderPOList)) {
            saxoBalanceToAccountRecordMapper.batchInsertTradeOrder(saxoBalTradeOrderPOList);
        }
        log.info("saxo_share交易执行job end");
    }

    @Override
    public void statisShareOpenPositions() {
        log.info("saxo_ShareOpenPositionsJob start");
        SftpClient sftpClient = SftpClient.saxoConnect();
        Date nowDate = DateUtils.now();
        Date yesterdayTime = DateUtils.addDays(DateUtils.now(), -1);
        List<SaxoShareOpenPositionsPO> saxoShareOpenPositionsPOS = saxoShareOpenPositionsMapper.getListByCond(new SaxoShareOpenPositionsPO(DateUtils.getStartDate(nowDate)));
        String yesterdayDate = DateUtils.formatDate(yesterdayTime, "dd-MM-yyyy");
        String fileName = "/ShareOpenPositions_" + PropertiesUtil.getString("sftp.account.name") + yesterdayDate + ".txt";
        if(DataUtil.isEmpty(saxoShareOpenPositionsPOS)){
            List<String> lines = Lists.newArrayList();
            try {
                lines = sftpClient.getsaxoFile(PropertiesUtil.getString("sftp.saxo.baseDir") + fileName, true);
            } catch (Exception e) {
//                throw new MessageException(e);
                log.error("saxo_ShareOpenPositions文件读取失败，e:{}", e);
                return;
            }
            if (DataUtil.isEmpty(lines)) {
                log.warn("saxo_ShareOpenPositions文件数据为空");
                return;
            }
            saxoShareOpenPositionsPOS = handleShareOpenPositionsData(lines);
            saxoShareOpenPositionsMapper.batchInsert(saxoShareOpenPositionsPOS);
        }

        RpcMessage<List<ProductStatisDTO>> rpcMessage = assetServiceRemoteService.querySpecificData(DateUtils.now());
        if (!rpcMessage.isSuccess()) {
            ErrorLogAndMailUtil.logError(log, rpcMessage.getErrMsg());
            return;
        }

        List<ProductStatisDTO> productStatisDTOS = rpcMessage.getContent();
        //das
        Map<String, ProductStatisDTO> dasDatas = Maps.newHashMap();
        if (DataUtil.isEmpty(rpcMessage.getContent())) {
            productStatisDTOS = Lists.newArrayList();
        }
        if (DataUtil.isNotEmpty(productStatisDTOS)) {
            dasDatas = productStatisDTOS.stream().collect(Collectors.toMap(ProductStatisDTO::getProductCode, in -> in));
        }
//      saxo
        Map<String, SaxoShareOpenPositionsPO> saxoDatas = Maps.newHashMap();
        saxoShareOpenPositionsPOS.stream().forEach(input -> {
            SaxoShareOpenPositionsPO saxoShareOpenPositionsPO = saxoDatas.get(input.getInstrument());
            if (saxoShareOpenPositionsPO == null) {
                saxoShareOpenPositionsPO = new SaxoShareOpenPositionsPO();
                saxoShareOpenPositionsPO.setAmount(input.getAmount());
                saxoShareOpenPositionsPO.setEodRate(input.getEodRate());
                saxoDatas.put(input.getInstrument(), saxoShareOpenPositionsPO);
            } else {
                saxoShareOpenPositionsPO.setEodRate(input.getEodRate());
                saxoShareOpenPositionsPO.setAmount(saxoShareOpenPositionsPO.getAmount().add(input.getAmount()));
            }
        });

        StringBuffer stringBuffer = new StringBuffer();
        //邮件列表
        List<SaxoBalETFHoldMoneyPO> saxoBalETFHoldMoneyPOS = Lists.newArrayList();
        //保存流水列表
        List<SaxoBalETFHoldMoneyPO> saxoBalETFHoldMoneyPOList = Lists.newArrayList();
        List<SaxoBalOfAccNoticePO> saxoBalOfAccNoticePOS = Lists.newArrayList();

        for (String productCode : saxoDatas.keySet()) {
            boolean flag = false;
            SaxoBalETFHoldMoneyPO saxoBalETFHoldMoneyPO = new SaxoBalETFHoldMoneyPO();
            saxoBalETFHoldMoneyPO.setProductCode(productCode);
            //das
            ProductStatisDTO productStatisDTO = dasDatas.get(productCode);
            //saxo
            SaxoShareOpenPositionsPO saxoShareOpenPositionsPO = saxoDatas.get(productCode);


            if (productStatisDTO == null) {
                flag = true;
                saxoBalETFHoldMoneyPO.setSaxoHoldShare(saxoShareOpenPositionsPO.getAmount().setScale(6, BigDecimal.ROUND_DOWN));
                saxoBalETFHoldMoneyPO.setSaxoHoldAmount(saxoShareOpenPositionsPO.getAmount().multiply(saxoShareOpenPositionsPO.getEodRate()).setScale(6, BigDecimal.ROUND_DOWN));
                saxoBalETFHoldMoneyPO.setDasHoldAmount(BigDecimal.ZERO);
                saxoBalETFHoldMoneyPO.setDasHoldShare(BigDecimal.ZERO);
                saxoBalETFHoldMoneyPO.setStatusDes("持仓份额不匹配  持仓金额不匹配");

                saxoBalETFHoldMoneyPO.setFileName(fileName);
                saxoBalETFHoldMoneyPO.setTransNumber(generateNum());

                saxoBalETFHoldMoneyPO.setCompareTime(DateUtils.formatDate(nowDate, "yyyyMMdd"));

                if (flag) {
                    saxoBalETFHoldMoneyPOS.add(saxoBalETFHoldMoneyPO);
                }
                saxoBalETFHoldMoneyPO.setStatusDes("saxo-etf持有份额大 && saxo-etf金额大");
                saxoBalETFHoldMoneyPOList.add(saxoBalETFHoldMoneyPO);
                continue;
            }
            saxoBalETFHoldMoneyPO.setTransNumber(generateNum());
            StringBuffer stringBuffer01 = new StringBuffer();
            log.info("etf持有份额,productCode:{},saox-持有份额:{},das-持有份额:{}", productCode, saxoShareOpenPositionsPO.getAmount(), productStatisDTO.getTotalProductShare());
            if (saxoShareOpenPositionsPO.getAmount().abs().compareTo(productStatisDTO.getTotalProductShare()) != ZERO) {
                stringBuffer01.append("持有份额不匹配");
                flag = true;
            }

            log.info("etf持有金额,productCode:{},saox-持有金额:{},das-持有金额:{}", productCode, saxoShareOpenPositionsPO.getAmount().multiply(saxoShareOpenPositionsPO.getEodRate()), productStatisDTO.getTotalProductMoney());
            BigDecimal diffETFMoney = saxoShareOpenPositionsPO.getAmount().multiply(saxoShareOpenPositionsPO.getEodRate()).subtract(productStatisDTO.getTotalProductMoney());
            if (diffETFMoney.abs().compareTo(ONE) > ZERO) {

                stringBuffer01.append("  持有金额不匹配");
                if (saxoShareOpenPositionsPO.getAmount().multiply(saxoShareOpenPositionsPO.getEodRate()).subtract(productStatisDTO.getTotalProductMoney()).abs().compareTo(TEN) > ZERO) {
                    saxoBalOfAccNoticePOS.add(new SaxoBalOfAccNoticePO(DateUtils.now(), BalanceOfAccountType.HOLD_PRODUCT_BAL, saxoBalETFHoldMoneyPO.getTransNumber()));
                }
                flag = true;
            }
            saxoBalETFHoldMoneyPO.setSaxoHoldShare(saxoShareOpenPositionsPO.getAmount().setScale(6, BigDecimal.ROUND_DOWN));
            saxoBalETFHoldMoneyPO.setSaxoHoldAmount(saxoShareOpenPositionsPO.getAmount().multiply(saxoShareOpenPositionsPO.getEodRate()).setScale(6, RoundingMode.DOWN));
            saxoBalETFHoldMoneyPO.setDasHoldShare(productStatisDTO.getTotalProductShare());
            saxoBalETFHoldMoneyPO.setDasHoldAmount(productStatisDTO.getTotalProductMoney());
            saxoBalETFHoldMoneyPO.setStatusDes(stringBuffer01.toString());
            saxoBalETFHoldMoneyPO.setFileName(fileName);
            saxoBalETFHoldMoneyPO.setCompareTime(DateUtils.formatDate(nowDate, "yyyyMMdd"));
            if (flag) {
                saxoBalETFHoldMoneyPOS.add(saxoBalETFHoldMoneyPO);
            }
            String statusDes = "";
            if (saxoShareOpenPositionsPO.getAmount().abs().compareTo(productStatisDTO.getTotalProductShare()) == ZERO) {
                statusDes = "持有份额平账";
            } else {
                statusDes = saxoShareOpenPositionsPO.getAmount().abs().compareTo(productStatisDTO.getTotalProductShare()) > ZERO ? "saxo份额大" : "业务系统份额大";
            }
            if (diffETFMoney.compareTo(BigDecimal.ZERO) == ZERO) {
                statusDes = statusDes + " 持有金额平账";
            } else {
                statusDes = statusDes + (diffETFMoney.compareTo(BigDecimal.ZERO) > 0 ? "saxo持有金额大" : "业务系统持有金额大");
            }

            saxoBalETFHoldMoneyPO.setStatusDes(statusDes);
            saxoBalETFHoldMoneyPOList.add(saxoBalETFHoldMoneyPO);
        }
        saxoBalanceToAccountRecordMapper.deleteBalOfAccNotice(new SaxoBalOfAccNoticePO(DateUtils.now(), BalanceOfAccountType.HOLD_PRODUCT_BAL));
        if (DataUtil.isNotEmpty(saxoBalOfAccNoticePOS)) {
            saxoBalanceToAccountRecordMapper.batchInsertBalOfAccNotice(saxoBalOfAccNoticePOS);
        }

        if (DataUtil.isNotEmpty(saxoBalETFHoldMoneyPOS)) {
            String baseStr = "对账异常_";
            String currentDate = DateUtils.formatDate(nowDate, "yyyyMMdd");
            String topic = baseStr + "ETF持仓对账_" + currentDate + "【" + getEnvRemark() + "】";

            Email email = new Email()
                    .setTemplateName("SaxoETFHoldMoneyWarn")
                    .setTemplateVariables(InstanceUtil.newHashMap("ETFHoldMoneys", saxoBalETFHoldMoneyPOS))
                    .setSendTo(CONTACT_Email)
                    .setTopic(topic);
            EmailUtil.sendEmail(email);
        } else {
            sendMail("对账[" + getEnvRemark() + "]", "ETF持仓对账ok");
        }
        saxoBalanceToAccountRecordMapper.deleteBalETFHoldMoney(new SaxoBalETFHoldMoneyPO(DateUtils.formatDate(nowDate)));
        if (DataUtil.isNotEmpty(saxoBalETFHoldMoneyPOList)) {
            saxoBalanceToAccountRecordMapper.batchInsertETFHoldMoney(saxoBalETFHoldMoneyPOList);
        }
        log.info("saxo_ShareOpenPositionsJob end");
    }

    @Override
    public void recordCashTransactions() {
        log.info("saxo_recordCashTransactions任务 start");
        SftpClient sftpClient = SftpClient.saxoConnect();
        String yesterdayDate = DateUtils.formatDate(DateUtils.addDays(DateUtils.now(), -1), "dd-MM-yyyy");
        String fileName = "/CashTransactions_" + PropertiesUtil.getString("sftp.account.name") + yesterdayDate + ".txt";
        List<String> lines = Lists.newArrayList();
        try {
            lines = sftpClient.getsaxoFile(PropertiesUtil.getString("sftp.saxo.baseDir") + fileName, true);
        } catch (Exception e) {
//                throw new MessageException(e);
            log.error("saxo_CashTransactions文件读取失败，e:{}", e);
            return;
        }
        if (DataUtil.isEmpty(lines)) {
            log.warn("saxo_CashTransactions文件数据为空");
            return;
        }
        List<SaxoCashTransactionsPO> saxoCashTransactionsPOS = handleCashTransactionData(lines);
        saxoCashTransactionsMapper.batchInsert(saxoCashTransactionsPOS);
        log.info("saxo_recordCashTransactions任务 end");
    }

    private List<SaxoCashTransactionsPO> handleCashTransactionData(List<String> lines) {
        List<SaxoCashTransactionsPO> saxoCashTransactionsPOS = Lists.newArrayList();
        lines.stream().forEach(input -> {
            int i = 0;
            String[] cols = input.split(",");
            SaxoCashTransactionsPO saxoCashTransactionsPO = new SaxoCashTransactionsPO();
            try {
                saxoCashTransactionsPO.setAccount(cols[i]);
                saxoCashTransactionsPO.setAccountCurrency(cols[++i]);
                saxoCashTransactionsPO.setTransactionNumber(NumberUtils.toLong(cols[++i], 0));
                saxoCashTransactionsPO.setOrderNumber(NumberUtils.toLong(cols[++i], 0));
                saxoCashTransactionsPO.setFrontOfficeLinkID(NumberUtils.toLong(cols[++i], 0));
                saxoCashTransactionsPO.setDate(DateUtils.parseDate(cols[++i], SAXO_DATE));
                saxoCashTransactionsPO.setValueDate(DateUtils.parseDate(cols[++i], SAXO_DATE));
                saxoCashTransactionsPO.setGrossAmountCashCurrency(new BigDecimal(cols[++i]));
                saxoCashTransactionsPO.setCashCurrency(cols[++i]);
                saxoCashTransactionsPO.setFeeCashCurrency(new BigDecimal(cols[++i]));
                saxoCashTransactionsPO.setCashToAccountRate(new BigDecimal(cols[++i]));
                saxoCashTransactionsPO.setGrossAmountAccountCurrency(new BigDecimal(cols[++i]));
                saxoCashTransactionsPO.setFeeAccountCurrency(new BigDecimal(cols[++i]));
                saxoCashTransactionsPO.setNetAmountAccountCurrency(new BigDecimal(cols[++i]));
                saxoCashTransactionsPO.setPartnerAccountKey(cols[++i]);
                saxoCashTransactionsPO.setComment(cols[++i]);
                saxoCashTransactionsPO.setCounterpartID(NumberUtils.toLong(cols[++i], 0));
            } catch (ParseException e) {
                throw new MessageException(e.getMessage());
            }
            saxoCashTransactionsPOS.add(saxoCashTransactionsPO);
        });
        return saxoCashTransactionsPOS;
    }


    private List<SaxoShareOpenPositionsPO> handleShareOpenPositionsData(List<String> lines) {

        List<SaxoShareOpenPositionsPO> saxoShareOpenPositionsPOS = Lists.newArrayList();
        lines.stream().forEach(input -> {
            int i = 0;
            String[] cols = input.split(",");
            SaxoShareOpenPositionsPO saxoShareOpenPositionsPO = new SaxoShareOpenPositionsPO();
            try {

                saxoShareOpenPositionsPO.setReportingDate(DateUtils.parseDate(cols[i], SAXO_DATE));
                saxoShareOpenPositionsPO.setInstrumentType(cols[++i]);
                saxoShareOpenPositionsPO.setCounterpartID(NumberUtils.toLong(cols[++i], 0));
                saxoShareOpenPositionsPO.setCounterpartName(cols[++i]);
                saxoShareOpenPositionsPO.setAccountNumber(cols[++i]);
                saxoShareOpenPositionsPO.setPartnerAccountKey(cols[++i]);
                saxoShareOpenPositionsPO.setAccountCurrency(cols[++i]);
                saxoShareOpenPositionsPO.setInstrument(cols[++i].split(":")[0]);
                saxoShareOpenPositionsPO.setDescription(cols[++i]);
                saxoShareOpenPositionsPO.setInstrumentCurrency(cols[++i]);
                saxoShareOpenPositionsPO.setIsInCode(cols[++i]);
                saxoShareOpenPositionsPO.setExchangeDescription(cols[++i]);
                saxoShareOpenPositionsPO.setAmount(new BigDecimal(cols[++i]));
                saxoShareOpenPositionsPO.setFigureSize(new BigDecimal(cols[++i]));
                saxoShareOpenPositionsPO.setTradeNumber(NumberUtils.toLong(cols[++i], 0));
                saxoShareOpenPositionsPO.setOrderNumber(NumberUtils.toLong(cols[++i], 0));
                saxoShareOpenPositionsPO.setTradeTime(DateUtils.parseDate(cols[++i], SAXO_TIME));
                saxoShareOpenPositionsPO.setTradeDate(DateUtils.parseDate(cols[++i], SAXO_DATE));
                saxoShareOpenPositionsPO.setValueDate(DateUtils.parseDate(cols[++i], SAXO_DATE));
                saxoShareOpenPositionsPO.setBuySell(cols[++i]);
                saxoShareOpenPositionsPO.setPrice(new BigDecimal(cols[++i]));
                saxoShareOpenPositionsPO.setQuotedValue(new BigDecimal(cols[++i]));
                saxoShareOpenPositionsPO.setEodRate(new BigDecimal(cols[++i]));
                saxoShareOpenPositionsPO.setInstrumentToAccountTate(new BigDecimal(cols[++i]));
                saxoShareOpenPositionsPO.setIsPartial(cols[++i]);
                saxoShareOpenPositionsPO.setUnrealisedValueInstrument(new BigDecimal(cols[++i]));
                saxoShareOpenPositionsPO.setUnrealisedValueAccount(new BigDecimal(cols[++i]));
                saxoShareOpenPositionsPO.setExternalOrderID(cols[++i]);
                saxoShareOpenPositionsPO.setPreviousIsInCode(cols[++i]);
                saxoShareOpenPositionsPO.setDvp(cols[++i]);
                saxoShareOpenPositionsPO.setExchangeIsoCode(cols[++i]);
                saxoShareOpenPositionsPO.setIsoMic(cols[++i]);
                saxoShareOpenPositionsPO.setCreateTime(DateUtils.now());
            } catch (ParseException e) {
                throw new DataParseException(e.getMessage());
            }
            saxoShareOpenPositionsPOS.add(saxoShareOpenPositionsPO);
        });
        return saxoShareOpenPositionsPOS;
    }

    private List<SaxoShareTradesExecutedPO> handleShareTradesData(List<String> lines) {
        List<SaxoShareTradesExecutedPO> saxoShareTradesExecutedPOS = Lists.newArrayList();
        lines.stream().forEach(input -> {
            String[] cols = input.split(",");
            SaxoShareTradesExecutedPO saxoShareTradesExecutedPO = new SaxoShareTradesExecutedPO();
            try {
                saxoShareTradesExecutedPO.setReportingDate(DateUtils.parseDate(cols[0], SAXO_DATE));
                saxoShareTradesExecutedPO.setInstrumentType(cols[1]);
                saxoShareTradesExecutedPO.setCounterpartID(NumberUtils.toLong(cols[2], 0));
                saxoShareTradesExecutedPO.setCounterpartName(cols[3]);
                saxoShareTradesExecutedPO.setAccountNumber(cols[4]);
                saxoShareTradesExecutedPO.setAccountCurrency(cols[6]);
                saxoShareTradesExecutedPO.setInstrumentCode(cols[7].split(":")[0]);
                saxoShareTradesExecutedPO.setInstrumentDescription(cols[8]);
                saxoShareTradesExecutedPO.setTradedAmount(new BigDecimal(cols[12]));
                saxoShareTradesExecutedPO.setOrderNumber(NumberUtils.toLong(cols[15], 0));
                saxoShareTradesExecutedPO.setTradeTime(DateUtils.parseDate(cols[16], SAXO_TIME));
                saxoShareTradesExecutedPO.setTradeDate(DateUtils.parseDate(cols[17], SAXO_DATE));
                saxoShareTradesExecutedPO.setBuySell(cols[19]);
                saxoShareTradesExecutedPO.setPrice(new BigDecimal(cols[20]));
                saxoShareTradesExecutedPO.setCommissionInstrumentCurrency(new BigDecimal(cols[22]));
                saxoShareTradesExecutedPO.setTradeType(cols[27]);
                saxoShareTradesExecutedPO.setCreateDate(DateUtils.now());
            } catch (ParseException e) {
                throw new DataParseException(e.getMessage());
            }
            saxoShareTradesExecutedPOS.add(saxoShareTradesExecutedPO);
        });
        return saxoShareTradesExecutedPOS;
    }

    private List<SaxoBookkeepingCashPO> handleBookkeepingData(List<String> lines) {
        List<SaxoBookkeepingCashPO> saxoBookkeepingCashPOS = Lists.newArrayList();
        lines.stream().forEach(input -> {
            int i = 0;
            String[] cols = input.split(",");
            SaxoBookkeepingCashPO saxoBookkeepingCashPO = new SaxoBookkeepingCashPO();
            try {
                saxoBookkeepingCashPO.setReportingDate(DateUtils.parseDate(cols[i], SAXO_DATE));
                saxoBookkeepingCashPO.setInstrumentType(cols[++i]);
                saxoBookkeepingCashPO.setCounterpartID(NumberUtils.toLong(cols[++i], 0));
                saxoBookkeepingCashPO.setCounterpartName(cols[++i]);
                saxoBookkeepingCashPO.setAccountNumber(cols[++i]);
                saxoBookkeepingCashPO.setPartnerAccountKey(cols[++i]);
                saxoBookkeepingCashPO.setAccountCurrency(cols[++i]);
                saxoBookkeepingCashPO.setInstrumentCode(cols[++i]);
                saxoBookkeepingCashPO.setInstrumentCurrency(cols[++i]);
                saxoBookkeepingCashPO.setAmountInstrumentCurreny(new BigDecimal(cols[++i]));
                saxoBookkeepingCashPO.setBkAmountID(NumberUtils.toLong(cols[++i], 0));
                saxoBookkeepingCashPO.setTradeDate(DateUtils.parseDate(cols[++i], SAXO_DATE));
                saxoBookkeepingCashPO.setRelatedPositionID(NumberUtils.toLong(cols[++i], 0));
                saxoBookkeepingCashPO.setRelatedTradeId(NumberUtils.toLong(cols[++i], 0));
                saxoBookkeepingCashPO.setOrderNumber(NumberUtils.toLong(cols[++i], 0));
                saxoBookkeepingCashPO.setBkAmountTypeID(NumberUtils.toLong(cols[++i], 0));
                saxoBookkeepingCashPO.setBkAmountType(cols[++i]);
                saxoBookkeepingCashPO.setValueDate(DateUtils.parseDate(cols[++i], SAXO_DATE));
                saxoBookkeepingCashPO.setAmountAccountCurrency(new BigDecimal(cols[++i]));
                saxoBookkeepingCashPO.setInstrumentToAccountRate(new BigDecimal(cols[++i]));

                saxoBookkeepingCashPO.setOriginalTradeID(NumberUtils.toLong(cols[++i], 0));
                saxoBookkeepingCashPO.setCorrectionLeg(cols[++i]);

                saxoBookkeepingCashPO.setCaEventTypeID(NumberUtils.toLong(cols[++i], 0));
                saxoBookkeepingCashPO.setCaEventTypeName(cols[++i]);


                saxoBookkeepingCashPO.setAdhocBookingCodeID(NumberUtils.toLong(cols[++i], 0));
                saxoBookkeepingCashPO.setAdhocBookingCode(cols[++i]);

                saxoBookkeepingCashPO.setInstrumentUIC(NumberUtils.toLong(cols[++i], 0));
                String actualTradeDate = cols[++i];
                if (!actualTradeDate.trim().equals("")) {
                    saxoBookkeepingCashPO.setActualTradeDate(DateUtils.parseDate(actualTradeDate, SAXO_DATE));
                }
                String caEventID;
                try {
                    caEventID = cols[++i];
                } catch (Exception e) {
                    caEventID = "";
                }
                saxoBookkeepingCashPO.setCaEventID(caEventID);
                saxoBookkeepingCashPO.setCreateDate(DateUtils.now());
            } catch (ParseException e) {
                throw new DataParseException(e.getMessage());
            }
            saxoBookkeepingCashPOS.add(saxoBookkeepingCashPO);
        });
        return saxoBookkeepingCashPOS;
    }

    private List<SaxoShareDividEndPO> handDividData(List<String> lines, Date nowDate) {
        List<SaxoShareDividEndPO> saxoShareDividEndPOS = Lists.newArrayList();
        lines.stream().forEach(input -> {
            int i = 0;
            String[] cols = input.split(",");
            SaxoShareDividEndPO saxoShareDividEndPO = new SaxoShareDividEndPO();
            saxoShareDividEndPO.setInstrumentType(cols[i]);
            saxoShareDividEndPO.setCaEventId(NumberUtils.toLong(cols[++i], 0));
            try {
                String exDate = cols[++i];
                if (StringUtils.isEmpty(exDate)) {
                    throw new DataParseException("exDate is null");
                }
                saxoShareDividEndPO.setExDate(DateUtils.parseDate(exDate, SAXO_DATE));
                String tradeDate = cols[++i];
                if (StringUtils.isEmpty(tradeDate)) {
                    throw new DataParseException("tradeDate is null");
                }
                saxoShareDividEndPO.setTradeDate(DateUtils.parseDate(tradeDate, SAXO_DATE));
                saxoShareDividEndPO.setValueDate(DateUtils.parseDate(cols[++i], SAXO_DATE));
                saxoShareDividEndPO.setCounterpartId(NumberUtils.toLong(cols[++i], 0));
                saxoShareDividEndPO.setCounterpartName(cols[++i]);
                saxoShareDividEndPO.setAccountNumber(cols[++i]);
                saxoShareDividEndPO.setPartnerAccountKey(cols[++i]);
                saxoShareDividEndPO.setAccountNumber(cols[++i]);
                saxoShareDividEndPO.setTradeNumber(NumberUtils.toLong(cols[++i], 0));
                saxoShareDividEndPO.setInstrumentDesctiption(cols[++i]);
                String instrumentCode = cols[++i];
                if (StringUtils.isEmpty(instrumentCode)) {
                    throw new DataParseException("instrumentCode is null");
                }
                saxoShareDividEndPO.setInstrumentCode(instrumentCode.split(":")[0]);
                saxoShareDividEndPO.setIsInCode(cols[++i]);
                saxoShareDividEndPO.setCountryOfIssue(cols[++i]);
                saxoShareDividEndPO.setFigureSize(new BigDecimal(cols[++i]));
                saxoShareDividEndPO.setInstrumentCurrency(cols[++i]);
                saxoShareDividEndPO.setEligibleQuantity(new BigDecimal(cols[++i]));
                saxoShareDividEndPO.setNetAmountDividendCurrency(new BigDecimal(cols[++i]));
                saxoShareDividEndPO.setDividendToAccountRate(new BigDecimal(cols[++i]));
                String netAmountAccountCurrency = cols[++i];
                if (StringUtils.isEmpty(netAmountAccountCurrency)) {
                    throw new DataParseException("netAmountAccountCurrency is null");
                }
                saxoShareDividEndPO.setNetAmountAccountCurrency(new BigDecimal(netAmountAccountCurrency));
                saxoShareDividEndPO.setWithholdingTaxPercentage(new BigDecimal(cols[++i]));
                saxoShareDividEndPO.setWithholdingTaxAmountDividendCurrency(new BigDecimal(cols[++i]));
                saxoShareDividEndPO.setGrossAmountDividendCurrency(new BigDecimal(cols[++i]));
                saxoShareDividEndPO.setDividendCurrency(cols[++i]);
                saxoShareDividEndPO.setFrankedAmountDividendCurrency(new BigDecimal(cols[++i]));
                saxoShareDividEndPO.setUnFrankedAmountDividendCurrency(new BigDecimal(cols[++i]));
                saxoShareDividEndPO.setFrankingCreditDividendCurrency(new BigDecimal(cols[++i]));
                saxoShareDividEndPO.setGrossRate(new BigDecimal(cols[++i]));
                saxoShareDividEndPO.setCaEventTypeId(NumberUtils.toLong(cols[++i], 0));
                String caEventTypeName = cols[++i];
                if (StringUtils.isEmpty(caEventTypeName)) {
                    throw new DataParseException("caEventTypeName is null");
                }
                saxoShareDividEndPO.setCaEventTypeName(caEventTypeName);
                saxoShareDividEndPO.setExchangelSoCode(cols[++i]);
                saxoShareDividEndPO.setIsoMic(cols[++i]);
                saxoShareDividEndPO.setInstrumentUic(Long.valueOf(cols[++i]));
                saxoShareDividEndPO.setGrossDividendAmountDividendCurrency(new BigDecimal(cols[++i]));
                saxoShareDividEndPO.setInterestAmountDividendCurrency(new BigDecimal(cols[++i]));
                String str = cols[++i];
                saxoShareDividEndPO.setResidencyTaxPercentage(new BigDecimal(DataUtil.isEmpty(str) ? "0" : str));
                saxoShareDividEndPO.setResidencyTaxAmountDividendCurrency(new BigDecimal(cols[++i]));
                saxoShareDividEndPO.setEuTaxPercentage(new BigDecimal(cols[++i]));
                saxoShareDividEndPO.setEuTaxAmountDividendCurrency(new BigDecimal(cols[++i]));
                saxoShareDividEndPO.setFeeAmountDividendCurrendcy(new BigDecimal(cols[++i]));
                saxoShareDividEndPO.setTransferStatusEnum(TransferStatusEnum.FAIL);
                saxoShareDividEndPO.setCreateDate(nowDate);
                saxoShareDividEndPO.setUpdateDate(nowDate);
            } catch (ParseException e) {
                throw new DataParseException(e.getMessage());
            }
            saxoShareDividEndPOS.add(saxoShareDividEndPO);
        });
        return saxoShareDividEndPOS;
    }


    private List<SaxoAccountStatusPO> handleTotalAccountData(List<String> lines) {
        List<SaxoAccountStatusPO> saxoAccountStatusPOS = Lists.newArrayList();
        lines.stream().forEach(input -> {
            int i = 0;
            String[] cols = input.split(",");
            SaxoAccountStatusPO saxoAccountStatusPO = new SaxoAccountStatusPO();
            try {
                saxoAccountStatusPO.setAccount(cols[i]);
                saxoAccountStatusPO.setDate(DateUtils.parseDate(cols[++i], SAXO_DATE));
                saxoAccountStatusPO.setAccountCurrency(cols[++i].trim());
                saxoAccountStatusPO.setBalance(new BigDecimal(cols[++i]));
                saxoAccountStatusPO.setOpenPositionsFX(new BigDecimal(cols[++i]));
                saxoAccountStatusPO.setOpenPositionsFXOptions(new BigDecimal(cols[++i]));
                saxoAccountStatusPO.setOpenPositionsStock(new BigDecimal(cols[++i]));
                saxoAccountStatusPO.setOpenPositionsCFD(new BigDecimal(cols[++i]));
                saxoAccountStatusPO.setOpenPositionsCFDOnOption(new BigDecimal(cols[++i]));
                saxoAccountStatusPO.setOpenPositionsFutures(new BigDecimal(cols[++i]));
                saxoAccountStatusPO.setOpenPositionsETO(new BigDecimal(cols[++i]));
                saxoAccountStatusPO.setOpenPositionsMutualFunds(new BigDecimal(cols[++i]));
                saxoAccountStatusPO.setOpenPositionsBonds(new BigDecimal(cols[++i]));
                saxoAccountStatusPO.setReservedForFutureInstrument2(new BigDecimal(cols[++i]));
                saxoAccountStatusPO.setActiveAccount(cols[++i]);
                saxoAccountStatusPO.setReservedForFutureUse(new BigDecimal(cols[++i]));
                saxoAccountStatusPO.setTotalEquity(new BigDecimal(cols[++i]));
                saxoAccountStatusPO.setAccountFunding(new BigDecimal(cols[++i]));
                saxoAccountStatusPO.setReservedForFutureUse2(new BigDecimal(cols[++i]));
                saxoAccountStatusPO.setPartnerAccountKey(cols[++i]);
                saxoAccountStatusPO.setValueDateCashBalance(new BigDecimal(cols[++i]));
                saxoAccountStatusPO.setMarginFOrTrading(new BigDecimal(cols[++i]));
                saxoAccountStatusPO.setRiskGroupProfile(NumberUtils.toLong(cols[++i], 0));
                saxoAccountStatusPO.setAccountRiskProfile(cols[++i]);
                saxoAccountStatusPO.setAccountLevelMargining(cols[++i]);
                saxoAccountStatusPO.setCounterpartID(NumberUtils.toLong(cols[++i], 0));
                saxoAccountStatusPO.setOtherCollateral(new BigDecimal(cols[++i]));
                saxoAccountStatusPO.setNotAvailableAsMarginCollateral(new BigDecimal(cols[++i]));
                saxoAccountStatusPO.setOwnerID(NumberUtils.toLong(cols[++i], 0));
                saxoAccountStatusPO.setIsClientAccount(cols[++i]);
                saxoAccountStatusPO.setCreateDate(DateUtils.now());
            } catch (ParseException e) {
                throw new DataParseException(e.getMessage());
            }
            saxoAccountStatusPOS.add(saxoAccountStatusPO);
        });
        return saxoAccountStatusPOS;
    }

    public String generateNum() {
        return "bal" + new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date()).toString() + (int) ((Math.random() * 9 + 1) * 100);
    }

    public void sendMail(String topic, String body) {
        log.info(body);
        if (openSend) {
            Email email = new Email();
            email.setBody(body);
            email.setTopic(topic);
            email.setSendTo(NOTICE_TO_ADD);
            email.setSSL(true);
            EmailUtil.sendEmail(email);
        }

    }
    
    public Page<SaxoReconBalCashPO> saxoReconBalanceCash(SaxoReconBalCashPO saxoBalCashPO, Page<SaxoReconBalCashPO> rowBounds, Date startCreateTime, Date endCreateTime){

    Page<SaxoReconBalCashPO> saxoBalCashPagePO = querySaxoReconciliation(saxoBalCashPO, rowBounds, startCreateTime, endCreateTime );

    return saxoBalCashPagePO;
    }
    
    public List<SaxoReconBalCashPO> querySaxoReconBalanceList(SaxoReconBalCashPO saxoReconBalanceCashPO){

    return mapper.querySaxoReconBalanceList(saxoReconBalanceCashPO);
    }
}


