package com.pivot.aham.api.service.impl;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pivot.aham.api.service.mapper.DailyClosingPriceMapper;
import com.pivot.aham.api.service.mapper.EtfMergeOrderMapper;
import com.pivot.aham.api.service.mapper.model.DailyClosingPricePO;
import com.pivot.aham.api.service.mapper.model.EtfMergeOrderPO;
import com.pivot.aham.common.core.util.PropertiesUtil;
import com.pivot.aham.common.enums.SaxoOrderTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("saxoMockUtil")
public class SaxoMockUtil {

    @Autowired
    private EtfMergeOrderMapper etfMergeOrderMapper;

    @Autowired
    private DailyClosingPriceMapper dailyClosingPriceMapper;

    public boolean isMock() {
        return PropertiesUtil.getBoolean("env.saxo.trade.mock");
    }

    public BigDecimal getMockExecutionPrice(Long saxoOrderId, SaxoOrderTypeEnum orderType) {
        EtfMergeOrderPO etfMergeOrder = etfMergeOrderMapper.getBySaxoOrderId(saxoOrderId);
        Map<String, BigDecimal> dailyClosingPriceMap = this.getDailyClosingPriceMap(Lists.newArrayList(etfMergeOrder));
        return dailyClosingPriceMap.get(etfMergeOrder.getProductCode());
    }

    public BigDecimal getMockHoldingShare(Long accountId) {
//        Map<Long, BigDecimal> mockAccountHoldingMap = Maps.newHashMap();
//        mockAccountHoldingMap.put(1120216392869711873L, new BigDecimal(0.8));
//        mockAccountHoldingMap.put(1120216392869711874L, new BigDecimal(10.2));
//        mockAccountHoldingMap.put(1120216392869711875L, new BigDecimal(20));
//        mockAccountHoldingMap.put(1120216392869711876L, new BigDecimal(10));
////        mockAccountHoldingMap.put(1120216392869711877L, new BigDecimal(200));
////        mockAccountHoldingMap.put(1120216392869711878L, new BigDecimal(100));
////        mockAccountHoldingMap.put(1120216392869711879L, new BigDecimal(200));
//
//        return mockAccountHoldingMap.get(accountId);
        return new BigDecimal(9999999);
    }

    private Map<String, BigDecimal> getDailyClosingPriceMap(List<EtfMergeOrderPO> mergeOrderList) {
        Set<String> etfCodeList = Sets.newHashSet();
        mergeOrderList.forEach((EtfMergeOrderPO mergeOrder) -> etfCodeList.add(mergeOrder.getProductCode()));
        Map<String, BigDecimal> dailyClosingPriceMap = Maps.newHashMap();
        for (String etfCode : etfCodeList) {
            DailyClosingPricePO dailyClosingPricePO = dailyClosingPriceMapper.getLastPrice(etfCode);
            dailyClosingPriceMap.put(etfCode, dailyClosingPricePO.getPrice());
        }
        return dailyClosingPriceMap;
    }

}
