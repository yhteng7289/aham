package com.pivot.aham.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.PivotErrorHandlingDetailDTO;
import com.pivot.aham.api.server.dto.ProductInfoResDTO;
import com.pivot.aham.api.server.dto.req.ClosingPriceReq;
import com.pivot.aham.api.server.dto.req.EtfHoldingReq;
import com.pivot.aham.api.server.dto.req.ExchangeRateReq;
import com.pivot.aham.api.server.dto.req.InterAccountTransferReq;
import com.pivot.aham.api.server.dto.resp.*;
import com.pivot.aham.api.server.remoteservice.AhamTradingRemoteService;
import com.pivot.aham.api.server.remoteservice.ModelServiceRemoteService;
import com.pivot.aham.api.server.remoteservice.PivotErrorDetailRemoteService;
import com.pivot.aham.api.service.TradingSupportService;
import com.pivot.aham.api.service.client.saxo.SaxoClient;
import com.pivot.aham.api.service.client.saxo.SaxoConstants;
import com.pivot.aham.api.service.client.saxo.resp.AccountFundingResp;
import com.pivot.aham.api.service.client.saxo.resp.AccountFundingRespV2;
import com.pivot.aham.api.service.client.saxo.resp.HoldingInstrumentResp;
import com.pivot.aham.api.service.client.saxo.resp.InterAccountTransferResp;
import com.pivot.aham.api.service.mapper.*;
import com.pivot.aham.api.service.mapper.model.*;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.support.file.ftp.SftpClient;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.core.util.FTPUtil;
import com.pivot.aham.common.enums.SaxoOrderFeeStatusEnum;
import com.pivot.aham.common.enums.SaxoOrderTypeEnum;
import com.pivot.aham.common.enums.analysis.ErrorFeeTypeEnum;
import com.pivot.aham.common.enums.analysis.OperateTypeEnum;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

@Service("tradingSupportService")
@Slf4j
public class TradingSupportServiceImpl implements TradingSupportService {

    private static final String SEND_TO = "wooitatt.khor@ezyit.asia";

    @Autowired
    private DailyClosingPriceMapper dailyClosingPriceMapper;

    @Autowired
    private DailyExchangeRateMapper dailyExchangeRateMapper;

    @Autowired
    private SaxoAccountFundingEventMapper saxoAccountFundingEventMapper;

    @Autowired
    private SaxoAccountFundingEventLogMapper saxoAccountFundingEventLogMapper;

    @Autowired
    private EtfInfoMapper etfInfoMapper;

    @Autowired
    private PivotErrorDetailRemoteService pivotErrorDetailRemoteService;

    @Autowired
    private SaxoOrderMapper saxoOrderMapper;
    
    @Resource
    private ModelServiceRemoteService modelServiceRemoteService;
    
    @Resource
    private AhamTradingRemoteService ahamTradingRemoteService;

    @Override
    public TradingEnableResult tradingEnable() {
        boolean tradingEnable = true;
        List<String> exchangeList = etfInfoMapper.getAllExchange();
        if (!CollectionUtils.isEmpty(exchangeList)) {
            Date nowUtc = DateUtils.nowUTC();
            for (String exchangeCode : exchangeList) {
                if (!SaxoClient.isAllOpenExchange(exchangeCode, nowUtc)) {
                    tradingEnable = false;
                    continue;
                }
            }
        }

        TradingEnableResult tradingEnableResult = new TradingEnableResult();
        tradingEnableResult.setTradingEnable(tradingEnable);
        return tradingEnableResult;
    }

    @Override
    public ClosingPriceResult queryClosingPrice(ClosingPriceReq closingPriceReq) {
        List<DailyClosingPricePO> priceList = dailyClosingPriceMapper.getPrice(closingPriceReq.getEtfCodeList(), closingPriceReq.getDate());

        List<ClosingPriceItem> closingPriceItemList = Lists.newArrayList();
        for (DailyClosingPricePO pricePO : priceList) {
            ClosingPriceItem item = new ClosingPriceItem();
            item.setEtfCode(pricePO.getEtfCode());
            item.setPrice(pricePO.getPrice());
            closingPriceItemList.add(item);
        }

        ClosingPriceResult result = new ClosingPriceResult();
        result.setBsnDt(closingPriceReq.getDate());
        result.setClosingPriceItemList(closingPriceItemList);
        return result;
    }

    @Override
    public BigDecimal queryLastClosingPrice(String etfCode) {
        DailyClosingPricePO dailyClosingPricePO = dailyClosingPriceMapper.getLastPrice(etfCode);
        if (dailyClosingPricePO != null) {
            return dailyClosingPricePO.getPrice();
        }
        return null;
    }

    @Override
    public void saveClosingPrice(String bsnDtStr) {
        Date bsnDt;
        if (StringUtils.isEmpty(bsnDtStr)) {
            bsnDt = DateUtils.addDays(DateUtils.now(), -1);
            bsnDtStr = DateUtils.formatDate(bsnDt, DateUtils.DATE_FORMAT2);
        } else {
            bsnDt = DateUtils.parseDate(bsnDtStr);
        }

        log.info("开始 saveClosingPrice --> bsnDt = " + bsnDtStr);
        SftpClient sftpClient = SftpClient.connect("3.0.163.17", 22, "ftpuser", "OmMsi93DBcNo", 5000, 10);

        try {
            List<DailyClosingPricePO> result = Lists.newArrayList();

            InputStream stream = null;
            List<String> lines = new ArrayList();
            try {
                stream = sftpClient.get("/home/ftpuser/pivot/marketData/dailyClose.csv");
                String thisLine = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                while ((thisLine = br.readLine()) != null) {
                    lines.add(thisLine);
                }
                // remove header;
                lines.remove(0);
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }

            if (CollectionUtils.isNotEmpty(lines)) {
                log.info("ftp查询成功，result.size() --> " + result.size() + ", bsnDt = " + bsnDtStr + "，开始入库处理");

                Map<String, Integer> productMap = ProductMapBuilder.getProductMap();
                for (int i = lines.size() - 1; i >= 0; i--) {
                    String line = lines.get(i);
                    List<String> lineItem = Lists.newArrayList(line.split(","));
                    if (lineItem.get(0).equals(bsnDtStr)) {
                        for (String productCode : productMap.keySet()) {
                            this.createDailyClosingPricePO(productCode, productMap.get(productCode), result, lineItem, bsnDt);
                        }
                        break;
                    }
                }
                log.info("入库完成 --> bsnDt = " + bsnDtStr);
            } else {
                ErrorLogAndMailUtil.logError(log, "未查询到收盘价 !!!!!!!, bsnDt = " + bsnDtStr, SEND_TO);
            }

            if (!CollectionUtils.isEmpty(result)) {
                log.info("清理当日数据 --> bsnDt = " + bsnDtStr);
                dailyClosingPriceMapper.clearByDt(bsnDtStr);
                dailyClosingPriceMapper.batchSave(result);
            } else {
                Date preBsnDt = DateUtils.addDays(bsnDt, -1);
                List<DailyClosingPricePO> prePriceList = dailyClosingPriceMapper.getByDt(preBsnDt);
                for (DailyClosingPricePO dailyClosingPrice : prePriceList) {
                    dailyClosingPrice.setId(null);
                    dailyClosingPrice.setBsnDt(bsnDt);
                    dailyClosingPrice.setCreateTime(DateUtils.now());
                }

                ErrorLogAndMailUtil.logErrorForTrade(log, "未从ftp获取到数据，取昨日数据作为今日价格 prePriceList.size() --> " + prePriceList.size() + ", bsnDt = " + bsnDtStr);
                if (CollectionUtils.isNotEmpty(prePriceList)) {
                    log.info("清理当日数据 --> bsnDt = " + bsnDtStr);
                    dailyClosingPriceMapper.clearByDt(bsnDtStr);
                    dailyClosingPriceMapper.batchSave(prePriceList);
                } else {
                    ErrorLogAndMailUtil.logErrorForTrade(log, "未从ftp获取到数据，取昨日数据作为今日价格，但是，昨日价格依旧为空！, bsnDt = " + bsnDtStr);
                }
            }
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        } finally {
            if (sftpClient != null) {
                sftpClient.disconnect();
            }
        }

        log.info("结束 saveClosingPrice --> bsnDt = " + bsnDtStr);
    }

    @Override
    public void saveClosingPrice_all() {
        log.info("开始 saveClosingPrice_all");
        FTPUtil ftpUtil = new FTPUtil();
        try {

            List<String> lines = ftpUtil.readFileContent("/home/ftpuser/pivot/marketData/dailyClose.csv");

            Map<String, Integer> productMap = ProductMapBuilder.getProductMap();
            for (int i = lines.size() - 1; i >= 0; i--) {
                List<DailyClosingPricePO> result = Lists.newArrayList();

                String line = lines.get(i);
                List<String> lineItem = Lists.newArrayList(line.split(","));
                String bsnDtStr = lineItem.get(0);
                Date bsnDt = DateUtils.parseDate(bsnDtStr, DateUtils.DATE_FORMAT2);

                for (String productCode : productMap.keySet()) {
                    this.createDailyClosingPricePO(productCode, productMap.get(productCode), result, lineItem, bsnDt);
                }

                if (!CollectionUtils.isEmpty(result)) {
                    dailyClosingPriceMapper.clearByDt(bsnDtStr);
                    dailyClosingPriceMapper.batchSave(result);
                } else {
                    ErrorLogAndMailUtil.logErrorForTrade(log, "未查询到收盘价， bsnDt --> " + bsnDtStr);
                }
            }

        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        } finally {
            ftpUtil.free();
        }

        log.info("结束 saveClosingPrice");
    }

    private void createDailyClosingPricePO(String etf, int idx, List<DailyClosingPricePO> result, List<String> lineItem, Date bsnDt) {
        DailyClosingPricePO dailyClosingPricePO = new DailyClosingPricePO();
        dailyClosingPricePO.setEtfCode(etf);
        dailyClosingPricePO.setBsnDt(bsnDt);
        dailyClosingPricePO.setPrice(new BigDecimal(lineItem.get(idx)));
        result.add(dailyClosingPricePO);
    }

    @Override
    public ExchangeRateResult queryExchangeRate(ExchangeRateReq exchangeRateReq) {
        String bsnDt = DateUtils.formatDate(exchangeRateReq.getDate(), DateUtils.DATE_FORMAT2);
        DailyExchangeRatePO dailyExchangeRatePO = dailyExchangeRateMapper.getRate(bsnDt);

        BigDecimal rate;
        if (dailyExchangeRatePO != null) {
            rate = dailyExchangeRatePO.getUsdToSgd();
        } else {
            rate = SaxoClient.getExchangeRateUsdToSgd();
        }

        ExchangeRateResult result = new ExchangeRateResult();
        result.setBsnDt(bsnDt);
        result.setUSD_TO_SGD(rate);
        return result;
    }

    @Override
    public void saveExchangeRate() {
        String bsnDt = DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT2);
        log.info("开始 saveExchangeRate --> bsnDt = " + bsnDt);
        try {
            DailyExchangeRatePO dailyExchangeRatePO = new DailyExchangeRatePO();
            dailyExchangeRatePO.setBsnDt(bsnDt);
            dailyExchangeRatePO.setUsdToSgd(SaxoClient.getExchangeRateUsdToSgd());

            dailyExchangeRateMapper.clearByDt(bsnDt);
            dailyExchangeRateMapper.save(dailyExchangeRatePO);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
        log.info("完成 saveExchangeRate --> bsnDt = " + bsnDt);
    }

    @Override
    public InterAccountTransferResult transferToUSDFromSGD(InterAccountTransferReq req) {
        InterAccountTransferResp resp = SaxoClient.transferToUSDFromSGD(req.getApplyAmount());

        InterAccountTransferResult result = new InterAccountTransferResult();
        result.setSuccessAmount(resp.getToAccountAmount());
        return result;
    }

    @Override
    public InterAccountTransferResult transferToSGDFromUSD(InterAccountTransferReq req) {
        InterAccountTransferResp resp = SaxoClient.transferToSGDFromUSD(req.getApplyAmount());

        InterAccountTransferResult result = new InterAccountTransferResult();
        result.setSuccessAmount(resp.getToAccountAmount());
        return result;
    }

    @Override
    public void saveAccountFoundingEvent() {
        log.info("开始 saveAccountFoundingEvent");
        try {
            SaxoAccountFundingEventPO lastEvent = saxoAccountFundingEventMapper.getLast();

            String sequenceId = null;
            if (lastEvent != null) {
                sequenceId = lastEvent.getSequenceId();
            }

            AccountFundingRespV2 result = SaxoClient.queryAccountFundingEventV2(sequenceId);
            if (result != null && !CollectionUtils.isEmpty(result.getData())) {
                for (AccountFundingResp resp : result.getData()) {
                    if (resp.getFundingType().equals("Deposit") && resp.getCurrencyCode().equals("SGD")) {
                        SaxoAccountFundingEventPO newPo = SaxoAccountFundingEventPO.convert(resp);
                        SaxoAccountFundingEventPO old = saxoAccountFundingEventMapper.getBySequence(newPo.getSequenceId());
                        if (old == null) {
                            saxoAccountFundingEventMapper.save(newPo);
                        }
                    }
                }
            }
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
        log.info("结束 saveAccountFoundingEvent");
    }

    @Override
    public EtfHoldingResult queryEtfHolding(EtfHoldingReq etfHoldingReq) {
        EtfHoldingResult result = new EtfHoldingResult();
        result.setAmount(0);

        EtfInfoPO etfInfoPO = etfInfoMapper.getByCode(etfHoldingReq.getEtfCode());
        if (etfInfoPO != null) {
            HoldingInstrumentResp holdingInstrumentResp = SaxoClient.queryHoldingInstrument(etfInfoPO.getUic());
            if (holdingInstrumentResp != null && holdingInstrumentResp.getAmount() != null) {
                result.setAmount(holdingInstrumentResp.getAmount().intValue());
            }
        }

        return result;
    }

    @Override
    public ExchangeRateResult queryActualTimeRate() {
        log.info("开始 queryActualTimeRate");
        ExchangeRateResult exchangeRateResult = new ExchangeRateResult();
        BigDecimal rate = SaxoClient.getExchangeRateUsdToSgd();
        exchangeRateResult.setUSD_TO_SGD(rate);
        exchangeRateResult.setBsnDt(DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT2));
        log.info("开始 queryActualTimeRate,返回结果：{}", JSON.toJSONString(exchangeRateResult));
        return exchangeRateResult;
    }

    @Override
    public void errorFeeNotify() {
        List<SaxoOrderPO> saxoOrderList = saxoOrderMapper.getWaitNotifyFee(SaxoOrderFeeStatusEnum.WAIT_NOTIFY);
        for (SaxoOrderPO saxoOrder : saxoOrderList) {
            try {
                List<PivotErrorHandlingDetailDTO> errorFeeList = Lists.newArrayList();
                String orderNo = saxoOrder.getSaxoOrderCode();
                Date tradeTime = saxoOrder.getConfirmTime();

                if (saxoOrder.getOrderType() == SaxoOrderTypeEnum.BUY) {
                    if (BigDecimal.ZERO.compareTo(saxoOrder.getExchangeFee()) != 0) {
                        PivotErrorHandlingDetailDTO feeDto = this.createErrorFee(
                                orderNo,
                                ErrorFeeTypeEnum.EXCHANGE_FEE,
                                tradeTime,
                                saxoOrder.getExchangeFee());
                        errorFeeList.add(feeDto);
                    }
                }

                if (BigDecimal.ZERO.compareTo(saxoOrder.getExternalCharges()) != 0) {
                    PivotErrorHandlingDetailDTO feeDto = this.createErrorFee(
                            orderNo,
                            ErrorFeeTypeEnum.EXTERNAL_CHARGES,
                            tradeTime,
                            saxoOrder.getExternalCharges());
                    errorFeeList.add(feeDto);
                }

                if (BigDecimal.ZERO.compareTo(saxoOrder.getPerformanceFee()) != 0) {
                    PivotErrorHandlingDetailDTO feeDto = this.createErrorFee(
                            orderNo,
                            ErrorFeeTypeEnum.PERFORMANCE_FEE,
                            tradeTime,
                            saxoOrder.getPerformanceFee());
                    errorFeeList.add(feeDto);
                }

                if (BigDecimal.ZERO.compareTo(saxoOrder.getStampDuty()) != 0) {
                    PivotErrorHandlingDetailDTO feeDto = this.createErrorFee(
                            orderNo,
                            ErrorFeeTypeEnum.STAMP_DUTY,
                            tradeTime,
                            saxoOrder.getStampDuty());
                    errorFeeList.add(feeDto);
                }

                if (CollectionUtils.isNotEmpty(errorFeeList)) {
                    RpcMessage rpcMessage = pivotErrorDetailRemoteService.saveErrorHandlingDetail(errorFeeList);
                    if (rpcMessage.isSuccess()) {
                        saxoOrderMapper.confirmNotifyFee(saxoOrder.getId(), SaxoOrderFeeStatusEnum.FINISH);
                    } else {
                        ErrorLogAndMailUtil.logErrorForTrade(log, "error fee notify error --->>> rpcMessage: " + JSON.toJSON(rpcMessage));
                    }
                } else {
                    saxoOrderMapper.confirmNotifyFee(saxoOrder.getId(), SaxoOrderFeeStatusEnum.FINISH);
                }
            } catch (Exception e) {
                ErrorLogAndMailUtil.logErrorForTrade(log, e);
            }
        }
    }

    private PivotErrorHandlingDetailDTO createErrorFee(
            String orderNumber,
            ErrorFeeTypeEnum errorFeeType,
            Date tradeTime,
            BigDecimal amount) {
        PivotErrorHandlingDetailDTO exchangeFeeDto = new PivotErrorHandlingDetailDTO();
        exchangeFeeDto.setMoney(amount);
        exchangeFeeDto.setOperateDate(tradeTime);
        exchangeFeeDto.setOperateType(OperateTypeEnum.WITHDRAW);
        exchangeFeeDto.setTransNo(orderNumber);
        exchangeFeeDto.setType(errorFeeType);
        return exchangeFeeDto;
    }

    @Override
    public void saveAccountFoundingEventLog() {
        log.info("开始 saveAccountFoundingEventLog");
        SaxoAccountFundingEventLogPO lastEvent = saxoAccountFundingEventLogMapper.getLast();

        String sequenceId = null;
        if (lastEvent != null) {
            sequenceId = lastEvent.getSequenceId();
        }

        try {
            AccountFundingRespV2 sgdResult = SaxoClient.queryAccountFundingEventV2(SaxoConstants.getSGDAccountKey(), sequenceId);
            this.saveAllAccountFoundingLog(sgdResult);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }

        try {
            AccountFundingRespV2 usdResult = SaxoClient.queryAccountFundingEventV2(SaxoConstants.getUSDAccountKey(), sequenceId);
            this.saveAllAccountFoundingLog(usdResult);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
        log.info("结束 saveAccountFoundingEventLog");
    }

    private void saveAllAccountFoundingLog(AccountFundingRespV2 result) {
        if (result != null && !CollectionUtils.isEmpty(result.getData())) {
            for (AccountFundingResp resp : result.getData()) {
                SaxoAccountFundingEventLogPO newPo = SaxoAccountFundingEventLogPO.convert(resp);
                saxoAccountFundingEventLogMapper.save(newPo);
            }
        }
    }
    
    @Override
    public void saveAhamClosingPrice(String bsnDtStr) {
    	log.info("清理当日数据 --> bsnDt:{}", bsnDtStr);
        try{
            Date bsnDt;
            if (StringUtils.isEmpty(bsnDtStr)) {
                bsnDt = DateUtils.addDays(DateUtils.now(), -1);
                bsnDtStr = DateUtils.formatDate(bsnDt, DateUtils.DATE_FORMAT);
            } else {
                bsnDt = DateUtils.parseDate(bsnDtStr);
            }

            List<ProductInfoResDTO> listProductInfoResDTO = modelServiceRemoteService.queryAllProductInfo();
            log.info("listProductInfoResDTO:{}",JSON.toJSONString(listProductInfoResDTO));
            for(ProductInfoResDTO productInfoResDTO:listProductInfoResDTO){
                productInfoResDTO.setBsnDt(bsnDt);
                log.info("productInfoResDTO:{}",JSON.toJSONString(productInfoResDTO));
                ProductInfoResDTO prdInfoResDTO = ahamTradingRemoteService.getAhamDailyClosingPrice(productInfoResDTO);
                log.info("prdInfoResDTO:{}",JSON.toJSONString(prdInfoResDTO));
                if(prdInfoResDTO.getProductCode() == null || prdInfoResDTO.getProductCode().equalsIgnoreCase("")){
                	log.info("======price not found for :{}", productInfoResDTO.getProductCode());
                    DailyClosingPricePO dailyClosingPricePO = dailyClosingPriceMapper.getLastPrice(productInfoResDTO.getProductCode());
                    log.info("latest dailyClosingPricePO from DB:{}",JSON.toJSONString(dailyClosingPricePO));
                    DailyClosingPricePO newDailyClosingPrice = new DailyClosingPricePO();
                    newDailyClosingPrice.setEtfCode(dailyClosingPricePO.getEtfCode());
                    newDailyClosingPrice.setBsnDt(bsnDt);
                    newDailyClosingPrice.setPrice(dailyClosingPricePO.getPrice());
                    newDailyClosingPrice.setCreateTime(DateUtils.now());
                    newDailyClosingPrice.setNavDate(dailyClosingPricePO.getNavDate());
                    dailyClosingPriceMapper.save(newDailyClosingPrice);
                    log.info("==dailyClosingPricePO:{}",JSON.toJSONString(dailyClosingPricePO));
                    ErrorLogAndMailUtil.logErrorForTrade(log, "NOT ABLE GET CLOSING PRICE --> " +productInfoResDTO.getProductCode() + ", bsnDt = " + bsnDtStr);
                }else{
                    DailyClosingPricePO dailyClosingPricePO = new DailyClosingPricePO();
                    dailyClosingPricePO.setEtfCode(prdInfoResDTO.getProductCode());
                    //dailyClosingPricePO.setBsnDt(prdInfoResDTO.getBsnDt());
                    dailyClosingPricePO.setBsnDt(bsnDt);
                    dailyClosingPricePO.setPrice(prdInfoResDTO.getClosingPrice());
                    dailyClosingPricePO.setCreateTime(DateUtils.now());
                    dailyClosingPricePO.setNavDate(prdInfoResDTO.getNavDate());
                    log.info("====dailyClosingPricePO:{}",JSON.toJSONString(dailyClosingPricePO));
                    dailyClosingPriceMapper.save(dailyClosingPricePO);
                }

            }
        }catch (Exception e){
        	ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
        
        
       /* if (!CollectionUtils.isEmpty(result)) {
                log.info("清理当日数据 --> bsnDt = " + bsnDtStr);
                dailyClosingPriceMapper.clearByDt(bsnDtStr);
                dailyClosingPriceMapper.batchSave(result);
            } else {
                Date preBsnDt = DateUtils.addDays(bsnDt, -1);
                List<DailyClosingPricePO> prePriceList = dailyClosingPriceMapper.getByDt(preBsnDt);
                for (DailyClosingPricePO dailyClosingPrice : prePriceList) {
                    dailyClosingPrice.setId(null);
                    dailyClosingPrice.setBsnDt(bsnDt);
                    dailyClosingPrice.setCreateTime(DateUtils.now());
                }

                ErrorLogAndMailUtil.logErrorForTrade(log, "未从ftp获取到数据，取昨日数据作为今日价格 prePriceList.size() --> " + prePriceList.size() + ", bsnDt = " + bsnDtStr);
                if (CollectionUtils.isNotEmpty(prePriceList)) {
                    log.info("清理当日数据 --> bsnDt = " + bsnDtStr);
                    dailyClosingPriceMapper.clearByDt(bsnDtStr);
                    dailyClosingPriceMapper.batchSave(prePriceList);
                } else {
                    ErrorLogAndMailUtil.logErrorForTrade(log, "未从ftp获取到数据，取昨日数据作为今日价格，但是，昨日价格依旧为空！, bsnDt = " + bsnDtStr);
                }
            }
*/
        //get product code
        // yest Date
        //loop each product and send to aham
        //read and save in t_daily_closing price
    }
}
