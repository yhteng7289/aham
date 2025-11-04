package com.pivot.aham.api.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.server.dto.req.ExchangeRateReq;
import com.pivot.aham.api.server.dto.resp.ExchangeRateResult;
import com.pivot.aham.api.server.remoteservice.SaxoTradeRemoteService;
import com.pivot.aham.api.service.mapper.ExchangeRateMapper;
import com.pivot.aham.api.service.mapper.model.ExchangeRatePO;
import com.pivot.aham.api.service.service.ExchangeRateService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.enums.ExchangeRateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 19/3/31.
 */
@Service
@Slf4j
public class ExchangeRateServiceImpl extends BaseServiceImpl<ExchangeRatePO, ExchangeRateMapper> implements ExchangeRateService {

    @Resource
    private SaxoTradeRemoteService saxoTradeRemoteService;

    @Override
    public void saveDailyExchangeRate(ExchangeRatePO po) {
        ExchangeRatePO exchangeRateParam = new ExchangeRatePO();
        exchangeRateParam.setExchangeRateType(po.getExchangeRateType());
        exchangeRateParam.setRateDate(po.getRateDate());
        ExchangeRatePO exchangeRatePO = mapper.queryExchangeRate(exchangeRateParam);
        if (exchangeRatePO == null) {
            exchangeRatePO = new ExchangeRatePO();
            exchangeRatePO.setId(Sequence.next());
            exchangeRatePO.setRateDate(po.getRateDate());
            exchangeRatePO.setExchangeRateType(po.getExchangeRateType());
            exchangeRatePO.setUsdToSgd(po.getUsdToSgd());
            exchangeRatePO.setCreateTime(DateUtils.now());
            exchangeRatePO.setUpdateTime(DateUtils.now());
            mapper.saveExchangeRate(exchangeRatePO);
        } else {
            exchangeRatePO.setUpdateTime(DateUtils.now());
            exchangeRatePO.setUsdToSgd(po.getUsdToSgd());
            mapper.updateExchangeRate(exchangeRatePO);
        }
    }

    /**
     * 获取实时的汇率
     *
     * @return
     */
    @Override
    public BigDecimal getActualTimeRate() {
        BigDecimal exchangeRate = BigDecimal.ZERO;
        log.info("======查询实时的汇率");
        try {
            RpcMessage<ExchangeRateResult> exchangeRateResultRpcMessage = saxoTradeRemoteService.queryActualTimeRate();
            log.info("======查询实时的汇率，返回:{}", JSON.toJSONString(exchangeRateResultRpcMessage));
            if (exchangeRateResultRpcMessage.getResultCode() == RpcMessageStandardCode.OK.value()) {
                exchangeRate = exchangeRateResultRpcMessage.getContent().getUSD_TO_SGD();
            } else {
                throw new BusinessException("查询实时的汇率失败:" + JSON.toJSONString(exchangeRateResultRpcMessage));
            }
        } catch (Exception ex) {
            ExchangeRateReq exchangeRateReq = new ExchangeRateReq();
            exchangeRateReq.setDate(DateUtils.now());
            log.info("======查询实时的汇率,没有查询到,查询最新的汇率");
            RpcMessage<ExchangeRateResult> exchangeRateResult = saxoTradeRemoteService.queryExchangeRate(exchangeRateReq);
            log.info("======查询实时的汇率,没有查询到,查询最新的汇率返回:{}", JSON.toJSONString(exchangeRateResult));
            if (RpcMessageStandardCode.OK.value() == exchangeRateResult.getResultCode()) {
                exchangeRate = exchangeRateResult.getContent().getUSD_TO_SGD();
            }
            ErrorLogAndMailUtil.logError(log, ex);
        }
        return exchangeRate;
    }

    /**
     * 记录汇率（交易）没有此交易汇率的话就查实时的汇率
     *
     * @param exchangeRateList
     * @param actualTimeRate
     * @param exchangeRateType
     */
    @Override
    public void handelExchangeRate(List<BigDecimal> exchangeRateList, BigDecimal actualTimeRate, ExchangeRateTypeEnum exchangeRateType) {
        BigDecimal exchangeRate = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(exchangeRateList)) {
            BigDecimal sumRate = BigDecimal.ZERO;
            for (BigDecimal rate : exchangeRateList) {
                sumRate = sumRate.add(rate).setScale(6, BigDecimal.ROUND_HALF_UP);
            }
            exchangeRate = sumRate.divide(new BigDecimal(exchangeRateList.size()), 6, BigDecimal.ROUND_HALF_UP);
        } else {
            exchangeRate = actualTimeRate;
        }
        ExchangeRatePO exchangeRatePO = new ExchangeRatePO();
        exchangeRatePO.setExchangeRateType(exchangeRateType);
        exchangeRatePO.setUsdToSgd(exchangeRate);
        exchangeRatePO.setRateDate(DateUtils.getDate(DateUtils.now(), 0, 0, 0));
        saveDailyExchangeRate(exchangeRatePO);
    }

    @Override
    public ExchangeRatePO getExchangeRate(ExchangeRatePO exchangeRateParam) {
        return mapper.queryExchangeRate(exchangeRateParam);
    }

    @Override
    public ExchangeRatePO queryLastExchangeRate(ExchangeRatePO exchangeRatePO) {
        return mapper.queryLastExchangeRate(exchangeRatePO);
    }

    @Override
    public void updateDailyExchangeRate(ExchangeRatePO exchangeRatePO) {
         mapper.updateExchangeRate(exchangeRatePO);
    }


}
