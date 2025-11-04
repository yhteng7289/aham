package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pivot.aham.api.server.dto.req.*;
import com.pivot.aham.api.server.dto.resp.*;
import com.pivot.aham.api.server.remoteservice.SaxoTradeRemoteService;
import com.pivot.aham.api.service.EtfOrderService;
import com.pivot.aham.api.service.SaxoStatisService;
import com.pivot.aham.api.service.SingaporeHolidayService;
import com.pivot.aham.api.service.TradingSupportService;
import com.pivot.aham.api.service.impl.SaxoMockUtil;
import com.pivot.aham.api.service.mapper.model.SingaporeHolidayPO;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.PropertiesUtil;
import com.pivot.aham.common.enums.DateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hao.tong on 2018/12/11.
 */
@Service(interfaceClass = SaxoTradeRemoteService.class)
@Slf4j
public class SaxoTradeRemoteServiceImpl implements SaxoTradeRemoteService {

    @Autowired
    private EtfOrderService etfOrderService;

    @Autowired
    private TradingSupportService tradingSupportService;

    @Autowired
    private SaxoStatisService saxoStatisService;
    @Resource
    private SaxoMockUtil saxoMockUtil;
    @Resource
    private SingaporeHolidayService singaporeHolidayService;

    @Override
    public RpcMessage<SaxoTradeResult> buy(SaxoTradeReq saxoTradeReq) {
//        TradingEnableResult tradingEnableResult = tradingSupportService.tradingEnable();
//        if (tradingEnableResult.isTradingEnable()) {
            return etfOrderService.createBuyOrder(saxoTradeReq);
//        } else {
//            return RpcMessage.error("本日不可交易");
//        }
    }

    @Override
    public RpcMessage<SaxoTradeResult> sell(SaxoTradeReq saxoTradeReq) {
//        TradingEnableResult tradingEnableResult = tradingSupportService.tradingEnable();
//        if (tradingEnableResult.isTradingEnable()) {
            return etfOrderService.createSellOrder(saxoTradeReq);
//        } else {
//            return RpcMessage.error("本日不可交易");
//        }
    }

    @Override
    public RpcMessage<ClosingPriceResult> queryClosingPrice(ClosingPriceReq closingPriceReq) {
        ClosingPriceResult result = tradingSupportService.queryClosingPrice(closingPriceReq);
        return RpcMessage.success(result);
    }

    @Override
    public RpcMessage<BigDecimal> queryClosingPrice(String etfCode) {
        BigDecimal price = tradingSupportService.queryLastClosingPrice(etfCode);
        if (price != null) {
            return RpcMessage.success(price);
        }
        return RpcMessage.error("查询失败");
    }

    @Override
    public RpcMessage<ExchangeRateResult> queryExchangeRate(ExchangeRateReq exchangeRateReq) {
        if(PropertiesUtil.getString("env.remark").equals("dev")){
            ExchangeRateResult exchangeRateResult = new ExchangeRateResult();
            exchangeRateResult.setBsnDt(DateUtils.formatDate(new Date(),"yyyy-MM-dd"));
            exchangeRateResult.setUSD_TO_SGD(new BigDecimal("1.338"));
            return  RpcMessage.success(exchangeRateResult);
        }
        ExchangeRateResult result = tradingSupportService.queryExchangeRate(exchangeRateReq);
        if (result != null) {
            return RpcMessage.success(result);
        }
        return RpcMessage.error("查询失败");
    }

    @Override
    public RpcMessage<InterAccountTransferResult> transferToUSDFromSGD(InterAccountTransferReq req) {

        if(isHoliday()){
            return RpcMessage.error("今天周末或假日,不进行内部转账");
        }
        log.info("============Checked Holiday Done (transferToUSDFromSGD) ======================="); //added WooiTatt
        InterAccountTransferResult result = tradingSupportService.transferToUSDFromSGD(req);
        log.info("============Response Inter-Account (transferToUSDFromSGD) =======================");//added WooiTatt
        return RpcMessage.success(result);
    }

    private Boolean isHoliday(){
        //判断是否为新加坡非交易日
        Boolean isWeekEnd = DateUtils.isWeekEnd(new Date());
        //查询节假日
        SingaporeHolidayPO singaporeHolidayPO = new SingaporeHolidayPO();
        singaporeHolidayPO.setDateType(DateTypeEnum.HOLIDAY);
        List<SingaporeHolidayPO> singaporeHolidayPOList = singaporeHolidayService.queryList(singaporeHolidayPO);
        Set<Date> dateSet = Sets.newHashSet();
        for(SingaporeHolidayPO singaporeHoliday:singaporeHolidayPOList){
            dateSet.add(singaporeHoliday.getVaDate());
        }
        Date nowDate = DateUtils.getStartDate(new Date());

        log.info("判断新加坡节假日,nowDate:{},dateSet:{}",nowDate, JSON.toJSONString(dateSet));
        if(isWeekEnd || dateSet.contains(nowDate)){
            return true;
        }
        return false;
    }

    @Override
    public RpcMessage<InterAccountTransferResult> transferToSGDFromUSD(InterAccountTransferReq req) {
        if(isHoliday()){
            return RpcMessage.error("今天周末或假日,不进行内部转账");
        }

        InterAccountTransferResult result = tradingSupportService.transferToSGDFromUSD(req);
        return RpcMessage.success(result);
    }

    @Override
    public RpcMessage<EtfHoldingResult> queryEtfHolding(EtfHoldingReq etfHoldingReq) {
        EtfHoldingResult result = tradingSupportService.queryEtfHolding(etfHoldingReq);
        return RpcMessage.success(result);
    }

    @Override
    public RpcMessage<ExchangeRateResult> queryActualTimeRate() {
        if(saxoMockUtil.isMock()){
            ExchangeRateResult exchangeRateResult = new ExchangeRateResult();
            exchangeRateResult.setBsnDt(DateUtils.formatDate(new Date(),"yyyyMMdd"));
            exchangeRateResult.setUSD_TO_SGD(new BigDecimal("1.2345"));
            return RpcMessage.success(exchangeRateResult);
        }
        return RpcMessage.success(tradingSupportService.queryActualTimeRate());
    }




    @Override
    public RpcMessage<TradingEnableResult> tradingEnable() {
        TradingEnableResult result = tradingSupportService.tradingEnable();
        return RpcMessage.success(result);
    }
    @Override
    public RpcMessage<Map<Long, SaxoStatisShareTradesDTO>> statisShareTreadesExecute(Date nowDate) {
        Map<Long, SaxoStatisShareTradesDTO> saxoStatisShareTradesDTOMap = Maps.newHashMap();
        try {
            saxoStatisShareTradesDTOMap = saxoStatisService.statisShareTreadesExecute(nowDate);
        } catch (Exception e) {
            return RpcMessage.error(e.getMessage());
        }
        return RpcMessage.success(saxoStatisShareTradesDTOMap);
    }
}
