//package com.pivot.aham.api.service.impl;
//
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.google.common.collect.Sets;
//import EtfCallbackDTO;
//import AccountEtfAssetReqDTO;
//import ReCalBuyEtfInBalReqDTO;
//import AccountEtfAssetResDTO;
//import AccountReBalanceRemoteService;
//import AssetServiceRemoteService;
//import DividendRemoteService;
//import SaxoTradingService;
//import SaxoClient;
//import HoldingInstrumentResp;
//import OrderActivitiesResp;
//import PlaceNewOrderResp;
//import PositionDetailResp;
//import QueryOpenOrderItem;
//import QueryOpenOrderResp;
//import DailyClosingPriceMapper;
//import EtfInfoMapper;
//import EtfMergeOrderMapper;
//import EtfOrderMapper;
//import SaxoOrderActivityMapper;
//import SaxoOrderMapper;
//import DailyClosingPricePO;
//import EtfInfoPO;
//import EtfMergeOrderPO;
//import EtfOrderPO;
//import SaxoOrderActivityPO;
//import SaxoOrderPO;
//import RpcMessage;
//import Sequence;
//import DateUtils;
//import ErrorLogAndMailUtil;
//import EtfMergeOrderStatusEnum;
//import EtfOrderStatusEnum;
//import EtfOrderTypeEnum;
//import SaxoOrderStatusEnum;
//import SaxoOrderTypeEnum;
//import TransferStatusEnum;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
///**
// * Created by hao.tong on 2018/12/26.
// */
//@Service("saxoTradingService")
//@Slf4j
//public class SaxoTradingServiceOldImpl implements SaxoTradingService {
//    @Autowired
//    private EtfOrderMapper etfOrderMapper;
//
//    @Autowired
//    private EtfMergeOrderMapper etfMergeOrderMapper;
//
//    @Autowired
//    private DailyClosingPriceMapper dailyClosingPriceMapper;
//
//    @Autowired
//    private EtfInfoMapper etfInfoMapper;
//
//    @Autowired
//    private SaxoOrderMapper saxoOrderMapper;
//
//    @Autowired
//    private SaxoOrderActivityMapper saxoOrderActivityMapper;
//
//    @Autowired
//    private AssetServiceRemoteService assetServiceRemoteService;
//
//    @Resource
//    private AccountReBalanceRemoteService accountReBalanceRemoteService;
//
//    @Resource
//    private SaxoMockUtil saxoMockUtil;
//
//    @Autowired
//    private DividendRemoteService dividendRemoteService;
//
//    @Override
//    public void mergeOrder() {
//        this.mergeOrder(true);
//    }
//
//    private void mergeOrder(boolean checkExchange) {
//        try {
//            List<EtfOrderPO> orderList = etfOrderMapper.getListByStatus(EtfOrderStatusEnum.WAIT_MERGE);
//            if (CollectionUtils.isEmpty(orderList)) {
//                return;
//            }
//
//            List<EtfOrderPO> balanceOrderList = Lists.newArrayList();
//            Map<String, List<EtfOrderPO>> map = Maps.newHashMap();
//            for (EtfOrderPO order : orderList) {
//                if (order.getBalanceOrder()) {
//                    balanceOrderList.add(order);
//                } else {
//                    String code = order.getProductCode();
//                    if (!map.containsKey(code)) {
//                        map.put(code, Lists.newArrayList());
//                    }
//                    map.get(code).add(order);
//                }
//            }
//
//            List<String> etfToTradeList = Lists.newArrayList();
//            List<EtfMergeOrderPO> mergeOrderList = Lists.newArrayList();
//            Map<Long, List<Long>> orderIdToUpdate = Maps.newHashMap();
//
//            for (String code : map.keySet()) {
//                List<EtfOrderPO> list = map.get(code);
//                List<Long> orderIdList = Lists.newArrayList();
//
//                BigDecimal amount = BigDecimal.ZERO;
//                for (EtfOrderPO order : list) {
//                    switch (order.getOrderType()) {
//                        case BUY:
//                            amount = amount.add(order.getApplyAmount());
//                            break;
//                        case SELL:
//                            amount = amount.subtract(order.getApplyAmount());
//                            break;
//                        default:
//                            break;
//                    }
//                    orderIdList.add(order.getId());
//                }
//
//                EtfOrderTypeEnum orderType = null;
//                EtfMergeOrderStatusEnum orderStatus = null;
//                if (amount.compareTo(BigDecimal.ZERO) == 0) {
//                    orderType = EtfOrderTypeEnum.DO_NOTHING;
//                    orderStatus = EtfMergeOrderStatusEnum.WAIT_DEMERGE;
//                }
//                if (amount.compareTo(BigDecimal.ZERO) > 0) {
//                    orderType = EtfOrderTypeEnum.BUY;
//                    orderStatus = EtfMergeOrderStatusEnum.WAIT_TRADE;
//                }
//                if (amount.compareTo(BigDecimal.ZERO) < 0) {
//                    orderType = EtfOrderTypeEnum.SELL;
//                    orderStatus = EtfMergeOrderStatusEnum.WAIT_TRADE;
//                }
//
//                Long mergeOrderId = Sequence.next();
//                EtfMergeOrderPO mergeOrder = new EtfMergeOrderPO();
//                mergeOrder.setId(mergeOrderId);
//                mergeOrder.setOrderType(orderType);
//                mergeOrder.setOrderStatus(orderStatus);
//                mergeOrder.setProductCode(code);
//                mergeOrder.setApplyTime(DateUtils.now());
//                mergeOrder.setApplyAmount(amount.abs());
//                mergeOrder.setBalanceOrder(false);
//
//                etfToTradeList.add(mergeOrder.getProductCode());
//                mergeOrderList.add(mergeOrder);
//                orderIdToUpdate.put(mergeOrderId, orderIdList);
//            }
//
//            for (EtfOrderPO balanceOrder : balanceOrderList) {
//                Long mergeOrderId = Sequence.next();
//                EtfMergeOrderPO mergeOrder = new EtfMergeOrderPO();
//                mergeOrder.setId(mergeOrderId);
//                mergeOrder.setOrderType(balanceOrder.getOrderType());
//                mergeOrder.setOrderStatus(EtfMergeOrderStatusEnum.WAIT_TRADE);
//                mergeOrder.setProductCode(balanceOrder.getProductCode());
//                mergeOrder.setApplyTime(DateUtils.now());
//                mergeOrder.setApplyAmount(balanceOrder.getApplyAmount());
//                mergeOrder.setBalanceOrder(true);
//                mergeOrderList.add(mergeOrder);
//                etfToTradeList.add(mergeOrder.getProductCode());
//                orderIdToUpdate.put(mergeOrderId, Lists.newArrayList(balanceOrder.getId()));
//            }
//
////            if (checkExchange && !saxoMockUtil.isMock()) {
//            if (checkExchange) {
//                if(etfToTradeList.size()==0){
//                    return;
//                }
//                //去重复
//                List<String> newList = Lists.newArrayList(Sets.newHashSet(etfToTradeList));
//
//                List<String> exchangeList = etfInfoMapper.getExchangeByEtf(newList);
//                Date nowUtc = DateUtils.nowUTC();
//                for (String exchangeCode : exchangeList) {
//                    if (!SaxoClient.isAllOpenExchange(exchangeCode, nowUtc)) {
//                        for (EtfOrderPO etfOrderPO : orderList) {
//                            etfOrderMapper.confirm(etfOrderPO.getId(), EtfOrderStatusEnum.WAIT_NOTIFY, BigDecimal.ZERO, DateUtils.now(), BigDecimal.ZERO, BigDecimal.ZERO);
//                        }
//                        ErrorLogAndMailUtil.logErrorForTrade(log, "交易所开市时间未满足约定时长, 终止合并订单, 并将所有订单全部0金额确认，交易所code --> " + exchangeCode);
//                        return;
//                    }
//                }
//            }
//
//            mergeOrderList.forEach((EtfMergeOrderPO mergeOrder) -> etfMergeOrderMapper.save(mergeOrder));
//            orderIdToUpdate.keySet().forEach((Long mergeOrderId) -> etfOrderMapper.updateMergeOrderId(orderIdToUpdate.get(mergeOrderId), mergeOrderId, EtfOrderStatusEnum.WAIT_TRADE));
//        } catch (Exception e) {
//            ErrorLogAndMailUtil.logErrorForTrade(log, e);
//        }
//    }
//
////    private static final BigDecimal rebate = new BigDecimal(0.005);
////    private BigDecimal offsetEstimatedPrice_sell(BigDecimal estimatedPrice) {
////        return estimatedPrice.multiply(new BigDecimal(1).add(rebate));
////    }
////
////    private BigDecimal offsetEstimatedPrice_buy(BigDecimal estimatedPrice) {
////        return estimatedPrice.multiply(new BigDecimal(1).subtract(rebate));
////    }
//
//    private BigDecimal getEstimatedCost(Integer fillShare) {
//        return (new BigDecimal(fillShare).multiply(new BigDecimal(0.007))).max(new BigDecimal(3.99));
//    }
//
//    private BigDecimal getEstimatedCost(BigDecimal dollarAmount, BigDecimal estimatedPrice) {
//        return (dollarAmount.divide(estimatedPrice, 6, RoundingMode.DOWN).multiply(new BigDecimal(0.007))).max(new BigDecimal(3.99));
//    }
//
//    private int getShare_sell(BigDecimal dollarAmount, BigDecimal estimatedPrice) {
//        BigDecimal share = dollarAmount.divide(estimatedPrice, 6, RoundingMode.DOWN).multiply(new BigDecimal(1.02));
//        return Double.valueOf(Math.ceil(share.doubleValue())).intValue();
//    }
//
//    private int getShare_buy(BigDecimal dollarAmount, BigDecimal estimatedPrice) {
//        return Double.valueOf(Math.floor(dollarAmount.divide(estimatedPrice, 6, RoundingMode.DOWN).doubleValue())).intValue();
//    }
//
//    private Map<String, BigDecimal> getDailyClosingPriceMap(List<EtfMergeOrderPO> mergeOrderList) {
//        Set<String> etfCodeList = Sets.newHashSet();
//        mergeOrderList.forEach((EtfMergeOrderPO mergeOrder) -> etfCodeList.add(mergeOrder.getProductCode()));
//        Map<String, BigDecimal> dailyClosingPriceMap = Maps.newHashMap();
//        for (String etfCode : etfCodeList) {
//            DailyClosingPricePO dailyClosingPricePO = dailyClosingPriceMapper.getLastPrice(etfCode);
//            dailyClosingPriceMap.put(etfCode, dailyClosingPricePO.getPrice());
//        }
//        return dailyClosingPriceMap;
//    }
//
//    /**
//     * 防止超卖
//     */
//    private int getFinalSharesSell(int nShares, List<EtfMergeOrderPO> mergeOrderList, Integer uic, String productCode){
//        int holdingShares;
//        if (saxoMockUtil.isMock()) {
//            holdingShares = 10000000;
//        } else {
//            HoldingInstrumentResp holdingInstrumentResp = SaxoClient.queryHoldingInstrument(uic);
//            holdingShares = holdingInstrumentResp.getAmount().intValue();
//        }
//
//        nShares = Math.min(nShares, holdingShares);
//
//        BigDecimal totalAmount = BigDecimal.ZERO;
//        List<EtfOrderPO> etfOrderList = Lists.newArrayList();
//        for (EtfMergeOrderPO mergeOrder : mergeOrderList) {
//            etfOrderList.addAll(etfOrderMapper.getListByMergeOrderId(mergeOrder.getId()));
//        }
//
//        Map<Long, List<EtfOrderPO>> etfOrderMapByAccount = Maps.newHashMap();
//        for (EtfOrderPO etfOrder : etfOrderList) {
//            if (etfOrder.getOrderType() == EtfOrderTypeEnum.SELL) {
//                totalAmount = totalAmount.add(etfOrder.getApplyAmount());
//                if (CollectionUtils.isEmpty(etfOrderMapByAccount.get(etfOrder.getAccountId()))) {
//                    etfOrderMapByAccount.put(etfOrder.getAccountId(), Lists.newArrayList(etfOrder));
//                } else {
//                    etfOrderMapByAccount.get(etfOrder.getAccountId()).add(etfOrder);
//                }
//            }
//        }
//
//        BigDecimal usedRate = BigDecimal.ZERO;
//        Map<Long, BigDecimal> accountRateMap = Maps.newHashMap();
//        Map<Long, BigDecimal> accountHoldingMap = Maps.newHashMap();
//
//        Iterator<Long> etfOrderMapByAccountIt = etfOrderMapByAccount.keySet().iterator();
//        while (etfOrderMapByAccountIt.hasNext()) {
//            Long accountId = etfOrderMapByAccountIt.next();
//
//            if (saxoMockUtil.isMock()) {
//                accountHoldingMap.put(accountId, saxoMockUtil.getMockHoldingShare(accountId));
//            } else {
//                AccountEtfAssetReqDTO reqDTO = new AccountEtfAssetReqDTO();
//                reqDTO.setAccountId(accountId);
//                RpcMessage<AccountEtfAssetResDTO> rpcMessage = assetServiceRemoteService.queryAccountEtfShare(reqDTO);
//                accountHoldingMap.put(accountId, rpcMessage.getContent().getDataMap().get(productCode));
//            }
//
//            BigDecimal accountApply = BigDecimal.ZERO;
//            for (EtfOrderPO etfOrder : etfOrderMapByAccount.get(accountId)) {
//                accountApply = accountApply.add(etfOrder.getApplyAmount());
//            }
//            BigDecimal rate;
//            if (!etfOrderMapByAccountIt.hasNext()) {
//                rate = BigDecimal.ONE.subtract(usedRate);
//            } else {
//                rate = accountApply.divide(totalAmount, 6, RoundingMode.DOWN);
//            }
//            accountRateMap.put(accountId, rate);
//            usedRate = usedRate.add(rate);
//        }
//
//        BigDecimal finalShares = BigDecimal.ZERO;
//        for (Long accountId : accountRateMap.keySet()) {
//            BigDecimal toSell = accountRateMap.get(accountId).multiply(new BigDecimal(nShares));
//            finalShares = finalShares.add(toSell.min(accountHoldingMap.get(accountId)));
//        }
//
//        return finalShares.intValue();
//    }
//
//    @Override
//    public void sell() {
//        try {
//            List<EtfMergeOrderPO> mergeOrderList = etfMergeOrderMapper.getMergeOrder(Lists.newArrayList(EtfOrderTypeEnum.SELL), EtfMergeOrderStatusEnum.WAIT_TRADE);
//            if (CollectionUtils.isEmpty(mergeOrderList)) {
//                log.info("未查询到需要出售的订单");
//                return;
//            }
//
//            Map<String, BigDecimal> dailyClosingPriceMap = this.getDailyClosingPriceMap(mergeOrderList);
//            Date now = DateUtils.now();
//
//            Map<String, List<EtfMergeOrderPO>> etfOrderMap = Maps.newHashMap();
//            for (EtfMergeOrderPO mergeOrder : mergeOrderList) {
//                List<EtfMergeOrderPO> etfOrderList = etfOrderMap.get(mergeOrder.getProductCode());
//                if (etfOrderList == null) {
//                    etfOrderList = Lists.newArrayList();
//                }
//
//                etfOrderList.add(mergeOrder);
//                etfOrderMap.put(mergeOrder.getProductCode(), etfOrderList);
//            }
//
//            Iterator<String> etfOrderMapIt = etfOrderMap.keySet().iterator();
//            while (etfOrderMapIt.hasNext()) {
//                try {
//                    String productCode = etfOrderMapIt.next();
//                    List<EtfMergeOrderPO> etfOrderList = etfOrderMap.get(productCode);
//
//                    EtfInfoPO etfInfo = etfInfoMapper.getByCode(productCode);
//                    BigDecimal estimatedPrice = dailyClosingPriceMap.get(productCode);
//                    BigDecimal dollarAmount = BigDecimal.ZERO;
//                    for (EtfMergeOrderPO mergeOrder : etfOrderList) {
//                        dollarAmount = dollarAmount.add(mergeOrder.getApplyAmount());
//                    }
//
//                    BigDecimal estimatedCost = getEstimatedCost(dollarAmount, estimatedPrice);
//                    BigDecimal dollarAmountWithCost = dollarAmount.add(estimatedCost);
//
//                    int nShares = this.getShare_sell(dollarAmountWithCost, estimatedPrice);
//                    int finalShares = this.getFinalSharesSell(nShares, etfOrderList, etfInfo.getUic(), productCode);
//
//                    if (finalShares == 0) {
//                        //如果持有量为0，则直接确认，确认金额为0
//                        BigDecimal zero = BigDecimal.ZERO;
//                        for (EtfMergeOrderPO mergeOrder : etfOrderList) {
//                            etfMergeOrderMapper.tradeConfirm(mergeOrder.getId(), EtfMergeOrderStatusEnum.WAIT_DEMERGE, zero, now, zero, BigDecimal.ZERO);
//                        }
//                        continue;
//                    }
//
//                    PlaceNewOrderResp placeNewOrderResp;
//                    if (saxoMockUtil.isMock()) {
//                        placeNewOrderResp = new PlaceNewOrderResp();
//                        placeNewOrderResp.setOrderId("1231231312312");
//                    } else {
//                        placeNewOrderResp = SaxoClient.placeSellMarketOrder(etfInfo.getUic(), finalShares, false);
//                    }
//
//                    SaxoOrderPO saxoOrder = new SaxoOrderPO();
//                    saxoOrder.setUic(etfInfo.getUic());
//                    saxoOrder.setSaxoOrderCode(placeNewOrderResp.getOrderId());
//                    saxoOrder.setOrderStatus(SaxoOrderStatusEnum.TRADING);
//                    saxoOrder.setOrderType(SaxoOrderTypeEnum.SELL);
//                    saxoOrder.setApplyShare(finalShares);
//                    saxoOrder.setApplyAmount(dollarAmountWithCost);
//                    saxoOrder.setApplyTime(now);
//                    saxoOrderMapper.save(saxoOrder);
//
//                    for (EtfMergeOrderPO mergeOrder : etfOrderList) {
//                        etfMergeOrderMapper.tradeExecute(mergeOrder.getId(), EtfMergeOrderStatusEnum.WAIT_CONFIRM, saxoOrder.getId());
//                    }
//                } catch (Exception e) {
//                    ErrorLogAndMailUtil.logErrorForTrade(log, e);
//                }
//            }
//        } catch (Exception e) {
//            ErrorLogAndMailUtil.logErrorForTrade(log, e);
//        }
//    }
//
//    @Override
//    public void reviseShare_sell() {
//        //this.reviseShare(SaxoOrderTypeEnum.SELL);
//    }
//
////    @Override
////    public void revisePrice_sell() {
////        this.revisePrice(SaxoOrderTypeEnum.SELL);
////    }
//
////    @Override
////    public void reviseMarket_sell() {
////        this.reviseMarket(SaxoOrderTypeEnum.SELL);
////    }
//
//    @Override
//    public void tradeConfirm_sell() {
//        this.tradeConfirm(SaxoOrderTypeEnum.SELL);
//    }
//
//    @Override
//    public void demergeOrder_sell() {
//        this.demergeOrder(EtfOrderTypeEnum.SELL);
//    }
//
//    @Override
//    public void recalculate() {
//        List<ReCalBuyEtfInBalReqDTO> reqList = Lists.newArrayList();
//        List<EtfOrderPO> balanceSellOrderList = etfOrderMapper.getBalanceListByStatusAndType(EtfOrderStatusEnum.WAIT_NOTIFY, EtfOrderTypeEnum.SELL);
//        for (EtfOrderPO etfOrderPO : balanceSellOrderList) {
//            BigDecimal price;
//            if (etfOrderPO.getOrderType() == EtfOrderTypeEnum.SELL) {
//                price = etfOrderPO.getConfirmAmount().add(etfOrderPO.getCostFee()).divide(etfOrderPO.getConfirmShare(), 6, BigDecimal.ROUND_DOWN);
//            } else {
//                price = etfOrderPO.getConfirmAmount().subtract(etfOrderPO.getCostFee()).divide(etfOrderPO.getConfirmShare(), 6, BigDecimal.ROUND_DOWN);
//            }
//
//            ReCalBuyEtfInBalReqDTO etfInBalReq = new ReCalBuyEtfInBalReqDTO();
//            etfInBalReq.setConfirmPrice(price);
//            etfInBalReq.setTmpOrderId(etfOrderPO.getOutBusinessId());
//            etfInBalReq.setAccountId(etfOrderPO.getAccountId());
//            etfInBalReq.setProductCode(etfOrderPO.getProductCode());
//            etfInBalReq.setConfirmMoney(etfOrderPO.getConfirmAmount());
//            etfInBalReq.setConfirmShare(etfOrderPO.getConfirmShare());
//            etfInBalReq.setTransferStatus(TransferStatusEnum.SUCCESS);
//            reqList.add(etfInBalReq);
//        }
//
//        if (!CollectionUtils.isEmpty(reqList)) {
//            RpcMessage rpcMessage = accountReBalanceRemoteService.reCalBuyEtfInBal(reqList);
//            if (rpcMessage.isSuccess()) {
//                this.mergeOrder(false);
//            } else {
//                ErrorLogAndMailUtil.logErrorForTrade(log, "recalculate 调用 reCalBuyEtfInBal 失败！");
//            }
//        }
//    }
//
//    @Override
//    public void buy() {
//        try {
//            List<EtfMergeOrderPO> mergeOrderList = etfMergeOrderMapper.getMergeOrder(Lists.newArrayList(EtfOrderTypeEnum.BUY), EtfMergeOrderStatusEnum.WAIT_TRADE);
//            if (CollectionUtils.isEmpty(mergeOrderList)) {
//                log.info("未查询到需要购买的订单");
//                return;
//            }
//
//            Map<String, BigDecimal> dailyClosingPriceMap = this.getDailyClosingPriceMap(mergeOrderList);
//            Date now = DateUtils.now();
//
//            Map<String, List<EtfMergeOrderPO>> etfOrderMap = Maps.newHashMap();
//            for (EtfMergeOrderPO mergeOrder : mergeOrderList) {
//                List<EtfMergeOrderPO> etfOrderList = etfOrderMap.get(mergeOrder.getProductCode());
//                if (etfOrderList == null) {
//                    etfOrderList = Lists.newArrayList();
//                }
//
//                etfOrderList.add(mergeOrder);
//                etfOrderMap.put(mergeOrder.getProductCode(), etfOrderList);
//            }
//
//            Iterator<String> etfOrderMapIt = etfOrderMap.keySet().iterator();
//            while (etfOrderMapIt.hasNext()) {
//                try {
//                    String productCode = etfOrderMapIt.next();
//                    List<EtfMergeOrderPO> etfOrderList = etfOrderMap.get(productCode);
//                    EtfInfoPO etfInfo = etfInfoMapper.getByCode(productCode);
//
//                    BigDecimal dollarAmount = BigDecimal.ZERO;
//                    for (EtfMergeOrderPO mergeOrder : etfOrderList) {
//                        dollarAmount = dollarAmount.add(mergeOrder.getApplyAmount());
//                    }
//
//                    BigDecimal estimatedPrice = dailyClosingPriceMap.get(etfInfo.getEtfCode());
//                    BigDecimal estimatedCost = getEstimatedCost(dollarAmount, estimatedPrice);
//                    BigDecimal dollarAmountWithoutCost = dollarAmount.subtract(estimatedCost);
//
//                    //为了防止超买，这里把总购买价缩小5%
//                    dollarAmountWithoutCost = dollarAmountWithoutCost.subtract(dollarAmountWithoutCost.multiply(new BigDecimal(0.05)));
//                    int nShares = getShare_buy(dollarAmountWithoutCost, estimatedPrice);
//
//                    if (nShares == 0) {
//                        BigDecimal zero = BigDecimal.ZERO;
//                        for (EtfMergeOrderPO mergeOrder : etfOrderList) {
//                            etfMergeOrderMapper.tradeConfirm(mergeOrder.getId(), EtfMergeOrderStatusEnum.WAIT_DEMERGE, zero, now, zero, BigDecimal.ZERO);
//                        }
//                        continue;
//                    }
//
//                    PlaceNewOrderResp placeNewOrderResp;
//                    if (saxoMockUtil.isMock()) {
//                        placeNewOrderResp = new PlaceNewOrderResp();
//                        placeNewOrderResp.setOrderId("231231231231");
//                    } else {
//                        placeNewOrderResp = SaxoClient.placeBuyMarketOrder(etfInfo.getUic(), nShares, false);
//                    }
//
//                    SaxoOrderPO saxoOrder = new SaxoOrderPO();
//                    saxoOrder.setUic(etfInfo.getUic());
//                    saxoOrder.setSaxoOrderCode(placeNewOrderResp.getOrderId());
//                    saxoOrder.setOrderStatus(SaxoOrderStatusEnum.TRADING);
//                    saxoOrder.setOrderType(SaxoOrderTypeEnum.BUY);
//                    saxoOrder.setApplyShare(nShares);
//                    saxoOrder.setApplyAmount(dollarAmountWithoutCost);
//                    saxoOrder.setApplyTime(now);
//                    saxoOrderMapper.save(saxoOrder);
//
//                    for (EtfMergeOrderPO mergeOrder : etfOrderList) {
//                        etfMergeOrderMapper.tradeExecute(mergeOrder.getId(), EtfMergeOrderStatusEnum.WAIT_CONFIRM, saxoOrder.getId());
//                    }
//                } catch (Exception e) {
//                    ErrorLogAndMailUtil.logErrorForTrade(log, e);
//                }
//            }
//        } catch (Exception e) {
//            ErrorLogAndMailUtil.logErrorForTrade(log, e);
//        }
//    }
//
//    @Override
//    public void reviseShare_buy() {
//        //this.reviseShare(SaxoOrderTypeEnum.BUY);
//    }
//
////    @Override
////    public void revisePrice_buy() {
////        this.revisePrice(SaxoOrderTypeEnum.BUY);
////    }
//
////    @Override
////    public void reviseMarket_buy() {
////        this.reviseMarket(SaxoOrderTypeEnum.BUY);
////    }
//
//    @Override
//    public void tradeConfirm_buy() {
//        this.tradeConfirm(SaxoOrderTypeEnum.BUY);
//    }
//
//    @Override
//    public void demergeOrder_buy() {
//        this.demergeOrder(EtfOrderTypeEnum.BUY);
//    }
//
//    @Override
//    public void finishNotify() {
//        try {
//            List<EtfOrderPO> etfOrderList = etfOrderMapper.getListByStatus(EtfOrderStatusEnum.WAIT_NOTIFY);
//
//            if (!CollectionUtils.isEmpty(etfOrderList)) {
//                List<EtfCallbackDTO> params = Lists.newArrayList();
//                List<Long> orderIdList = Lists.newArrayList();
//
//                for (EtfOrderPO etfOrder : etfOrderList) {
//                    EtfCallbackDTO etfCallbackDTO = new EtfCallbackDTO();
//                    etfCallbackDTO.setTmpOrderId(etfOrder.getOutBusinessId());
//                    etfCallbackDTO.setAccountId(etfOrder.getAccountId());
//                    etfCallbackDTO.setProductCode(etfOrder.getProductCode());
//                    etfCallbackDTO.setConfirmMoney(etfOrder.getConfirmAmount());
//                    etfCallbackDTO.setConfirmShare(etfOrder.getConfirmShare());
//                    etfCallbackDTO.setTransCost(etfOrder.getCostFee());
//                    etfCallbackDTO.setTransferStatus(TransferStatusEnum.SUCCESS);
//                    etfCallbackDTO.setConfirmTime(DateUtils.now());
//                    params.add(etfCallbackDTO);
//                    orderIdList.add(etfOrder.getId());
//                }
//
//                RpcMessage rpcMessage = assetServiceRemoteService.etfCallBack(params);
//                if (rpcMessage.isSuccess()) {
//                    etfOrderMapper.notifySuccess(orderIdList, EtfOrderStatusEnum.FINISH);
//                }
//            }
//        } catch (Exception e) {
//            ErrorLogAndMailUtil.logErrorForTrade(log, e);
//        }
//    }
//
//    private void demergeOrder(EtfOrderTypeEnum orderType) {
//        Date now = DateUtils.now();
//
//        List<EtfOrderTypeEnum> orderTypeList = Lists.newArrayList(orderType);
//        if (orderType == EtfOrderTypeEnum.SELL) {
//            orderTypeList.add(EtfOrderTypeEnum.DO_NOTHING);
//        }
//        List<EtfMergeOrderPO> mergeOrderPOList = etfMergeOrderMapper.getMergeOrder(orderTypeList, EtfMergeOrderStatusEnum.WAIT_DEMERGE);
//        for (EtfMergeOrderPO etfMergeOrder : mergeOrderPOList) {
//            try {
//                List<EtfOrderPO> confirmOrderList = Lists.newArrayList();
//
//                List<EtfOrderPO> etfOrderList = etfOrderMapper.getListByMergeOrderId(etfMergeOrder.getId());
//                DailyClosingPricePO lastClosingPrice = dailyClosingPriceMapper.getLastPrice(etfMergeOrder.getProductCode());
//
//                if (etfMergeOrder.getConfirmAmount().compareTo(BigDecimal.ZERO) == 0
//                        && etfMergeOrder.getConfirmShare().compareTo(BigDecimal.ZERO) == 0
//                        && etfMergeOrder.getCostFee().compareTo(BigDecimal.ZERO) == 0) {
//                    for (EtfOrderPO etfOrder : etfOrderList) {
//                        etfOrderMapper.confirm(etfOrder.getId(), EtfOrderStatusEnum.WAIT_NOTIFY, BigDecimal.ZERO, now, BigDecimal.ZERO, BigDecimal.ZERO);
//                    }
//                    etfMergeOrderMapper.updateStatus(etfMergeOrder.getId(), EtfMergeOrderStatusEnum.FINISH);
//                } else {
//                    if (etfMergeOrder.getBalanceOrder() && etfOrderList.size() == 1) {
//                        EtfOrderPO etfOrder = etfOrderList.get(0);
//                        etfOrder.setCostFee(etfMergeOrder.getCostFee());
//                        etfOrder.setConfirmShare(etfMergeOrder.getConfirmShare());
//                        etfOrder.setConfirmAmount(etfMergeOrder.getConfirmAmount());
//                        confirmOrderList.add(etfOrder);
//                    } else {
//                        if (etfMergeOrder.getOrderType() == EtfOrderTypeEnum.DO_NOTHING) {
//                            for (EtfOrderPO etfOrder : etfOrderList) {
//                                BigDecimal share = etfOrder.getApplyAmount().divide(lastClosingPrice.getPrice(), 2, RoundingMode.DOWN);
//
//                                etfOrder.setCostFee(BigDecimal.ZERO);
//                                etfOrder.setConfirmShare(share);
//                                etfOrder.setConfirmAmount(etfOrder.getApplyAmount());
//                                confirmOrderList.add(etfOrder);
//                            }
//                        } else {
//                            BigDecimal avgPrice;
//
//                            if (etfMergeOrder.getConfirmAmount().compareTo(BigDecimal.ZERO) == 0) {
//                                avgPrice = BigDecimal.ZERO;
//                            } else {
//                                if (etfMergeOrder.getOrderType() == EtfOrderTypeEnum.BUY) {
//                                    avgPrice = (etfMergeOrder.getConfirmAmount().subtract(etfMergeOrder.getCostFee())).divide(etfMergeOrder.getConfirmShare(), 8, RoundingMode.DOWN);
//                                } else {
//                                    avgPrice = (etfMergeOrder.getConfirmAmount().add(etfMergeOrder.getCostFee())).divide(etfMergeOrder.getConfirmShare(), 8, RoundingMode.DOWN);
//                                }
//                            }
//
//                            List<EtfOrderPO> pendingList = Lists.newArrayList();
//                            List<EtfOrderPO> otherList = Lists.newArrayList();
//
//                            BigDecimal commonVal = BigDecimal.ZERO;
//                            BigDecimal commonShare = BigDecimal.ZERO;
//                            BigDecimal totalApply = BigDecimal.ZERO;
//
//                            if (etfMergeOrder.getOrderType() == EtfOrderTypeEnum.SELL) {
//                                for (EtfOrderPO etfOrder : etfOrderList) {
//                                    //如何合并后的订单是卖单，则它对应的所有买单就是全额成交
//                                    if (etfOrder.getOrderType() == EtfOrderTypeEnum.BUY) {
//                                        commonVal = commonVal.add(etfOrder.getApplyAmount());
//                                        otherList.add(etfOrder);
//                                    } else {
//                                        totalApply = totalApply.add(etfOrder.getApplyAmount());
//                                        pendingList.add(etfOrder);
//                                    }
//                                }
//                            }
//
//                            if (etfMergeOrder.getOrderType() == EtfOrderTypeEnum.BUY) {
//                                for (EtfOrderPO etfOrder : etfOrderList) {
//                                    //如何合并后的订单是买单，则它对应的所有卖单就是全额成交
//                                    if (etfOrder.getOrderType() == EtfOrderTypeEnum.SELL) {
//                                        commonVal = commonVal.add(etfOrder.getApplyAmount());
//                                        otherList.add(etfOrder);
//                                    } else {
//                                        totalApply = totalApply.add(etfOrder.getApplyAmount());
//                                        pendingList.add(etfOrder);
//                                    }
//                                }
//                            }
//
//                            BigDecimal usedConfirm = BigDecimal.ZERO;
//                            BigDecimal usedShare = BigDecimal.ZERO;
//                            BigDecimal usedCost = BigDecimal.ZERO;
//
//                            //没执行任何实际交易的订单的处理方式
//                            for (EtfOrderPO etfOrder : otherList) {
//                                BigDecimal costRate = etfOrder.getApplyAmount().divide(totalApply.add(commonVal), 6, RoundingMode.DOWN);
//                                BigDecimal cost = etfMergeOrder.getCostFee().multiply(costRate);
//
//                                BigDecimal share;
//                                if (etfOrder.getOrderType() == EtfOrderTypeEnum.BUY) {
//                                    share = etfOrder.getApplyAmount().subtract(cost).divide(avgPrice, 2, RoundingMode.DOWN);
//                                } else {
//                                    share = etfOrder.getApplyAmount().add(cost).divide(avgPrice, 2, RoundingMode.DOWN);
//                                }
//
//                                usedCost = usedCost.add(cost);
//                                commonShare = commonShare.add(share);
//
//                                etfOrder.setCostFee(cost);
//                                etfOrder.setConfirmShare(share);
//                                etfOrder.setConfirmAmount(etfOrder.getApplyAmount());
//                                confirmOrderList.add(etfOrder);
//                            }
//
//                            //进行过实际交易的订单的处理方式
//                            BigDecimal totalShare = commonShare.add(etfMergeOrder.getConfirmShare());
//
//                            for (int i = 0; i < pendingList.size(); i++) {
//                                EtfOrderPO etfOrder = pendingList.get(i);
//
//                                BigDecimal cost;
//                                BigDecimal confirm;
//                                BigDecimal share;
//
//                                if (i == pendingList.size() - 1) {
//                                    cost = etfMergeOrder.getCostFee().subtract(usedCost);
//                                    share = totalShare.subtract(usedShare);
//
//                                    if (etfOrder.getOrderType() == EtfOrderTypeEnum.BUY) {
//                                        confirm = share.multiply(avgPrice).add(cost);
//                                    } else {
//                                        confirm = share.multiply(avgPrice).subtract(cost);
//                                    }
//
//                                } else {
//                                    BigDecimal costRate = etfOrder.getApplyAmount().divide(totalApply.add(commonVal), 6, RoundingMode.DOWN);
//                                    cost = etfMergeOrder.getCostFee().multiply(costRate).setScale(8, RoundingMode.DOWN);
//
//                                    BigDecimal shareRate = etfOrder.getApplyAmount().divide(totalApply, 6, RoundingMode.DOWN);
//                                    share = totalShare.multiply(shareRate).setScale(2, RoundingMode.DOWN);
//
//                                    if (etfOrder.getOrderType() == EtfOrderTypeEnum.BUY) {
//                                        confirm = share.multiply(avgPrice).add(cost);
//                                    } else {
//                                        confirm = share.multiply(avgPrice).subtract(cost);
//                                    }
//
//                                    usedConfirm = usedConfirm.add(confirm);
//                                    usedShare = usedShare.add(share);
//                                    usedCost = usedCost.add(cost);
//                                }
//
//                                etfOrder.setCostFee(cost);
//                                etfOrder.setConfirmShare(share);
//                                etfOrder.setConfirmAmount(confirm);
//                                confirmOrderList.add(etfOrder);
//                            }
//                        }
//                    }
//
//                    this.handleOverBuySell(confirmOrderList, etfMergeOrder);
//                    for (EtfOrderPO etfOrder : confirmOrderList) {
//                        etfOrderMapper.confirm(etfOrder.getId(), EtfOrderStatusEnum.WAIT_NOTIFY, etfOrder.getCostFee(), now, etfOrder.getConfirmShare(), etfOrder.getConfirmAmount());
//                    }
//                    etfMergeOrderMapper.updateStatus(etfMergeOrder.getId(), EtfMergeOrderStatusEnum.FINISH);
//                }
//            } catch (Exception e) {
//                ErrorLogAndMailUtil.logErrorForTrade(log, e);
//            }
//        }
//    }
//
//    private void handleOverBuySell(List<EtfOrderPO> confirmOrderList, EtfMergeOrderPO mergeOrder) {
//        //判断是否买卖同时存在
//        List<EtfOrderPO> buyList = Lists.newArrayList();
//        List<EtfOrderPO> sellList = Lists.newArrayList();
//
//        List<EtfOrderPO> overBuyList = Lists.newArrayList();
//        List<EtfOrderPO> overSellList = Lists.newArrayList();
//        Map<Long, BigDecimal> overSellShareMap = Maps.newHashMap();
//
//        for (EtfOrderPO etfOrderPO : confirmOrderList) {
//            if (etfOrderPO.getOrderType() == EtfOrderTypeEnum.BUY) {
//                if (etfOrderPO.getConfirmAmount().compareTo(etfOrderPO.getApplyAmount()) > 0) {
//                    overBuyList.add(etfOrderPO);
//                }
//                buyList.add(etfOrderPO);
//            }
//
//            if (etfOrderPO.getOrderType() == EtfOrderTypeEnum.SELL) {
//                BigDecimal holdingShare;
//                if (saxoMockUtil.isMock()) {
//                    holdingShare = saxoMockUtil.getMockHoldingShare(etfOrderPO.getAccountId());
//                } else {
//                    AccountEtfAssetReqDTO reqDTO = new AccountEtfAssetReqDTO();
//                    reqDTO.setAccountId(etfOrderPO.getAccountId());
//                    RpcMessage<AccountEtfAssetResDTO> rpcMessage = assetServiceRemoteService.queryAccountEtfShare(reqDTO);
//                    holdingShare = rpcMessage.getContent().getDataMap().get(mergeOrder.getProductCode());
//                }
//
//                if (etfOrderPO.getConfirmShare().compareTo(holdingShare) > 0) {
//                    overSellList.add(etfOrderPO);
//                    overSellShareMap.put(etfOrderPO.getId(), etfOrderPO.getConfirmShare().subtract(holdingShare));
//                }
//
//                sellList.add(etfOrderPO);
//            }
//        }
//
//        if (buyList.size() < 1 || sellList.size() < 1) {
//            return;
//        }
//
//        //超买
//        if (!CollectionUtils.isEmpty(overBuyList)) {
//            BigDecimal overBuyMoneyTotal = BigDecimal.ZERO;
//            for (EtfOrderPO overBuyOrder : overBuyList) {
//                overBuyMoneyTotal = overBuyMoneyTotal.add(overBuyOrder.getConfirmAmount().subtract(overBuyOrder.getApplyAmount()));
//            }
//
//            BigDecimal price = (mergeOrder.getConfirmAmount().subtract(mergeOrder.getCostFee()))
//                    .divide(mergeOrder.getConfirmShare(), 2, RoundingMode.DOWN);
//
//            BigDecimal overBuyShareTotal = overBuyMoneyTotal.divide(price, 8, RoundingMode.DOWN);
//
//            //平衡买单
//            for (EtfOrderPO overBuyOrder : overBuyList) {
//                BigDecimal adjustMoney = overBuyOrder.getConfirmAmount().subtract(overBuyOrder.getApplyAmount());
//                BigDecimal reConfirmMoney = overBuyOrder.getConfirmAmount().subtract(adjustMoney);
//
//                BigDecimal adjustShare = adjustMoney.divide(price, 2, RoundingMode.DOWN);
//                BigDecimal reConfirmShare = overBuyOrder.getConfirmShare().subtract(adjustShare);
//
//                overBuyOrder.setConfirmShare(reConfirmShare);
//                overBuyOrder.setConfirmAmount(reConfirmMoney);
//            }
//
//            BigDecimal totalFinalBuyConfirmShare = BigDecimal.ZERO;
//            for (EtfOrderPO buyOrder : buyList) {
//                totalFinalBuyConfirmShare = totalFinalBuyConfirmShare.add(buyOrder.getConfirmShare());
//            }
//
//            //平衡卖单
//            if (CollectionUtils.isEmpty(sellList)) {
//                // TODO: 2019-03-25
//
//
//            } else {
//                BigDecimal totalSellApplyAmount = BigDecimal.ZERO;
//                for (EtfOrderPO sellOrder : sellList) {
//                    totalSellApplyAmount = totalSellApplyAmount.add(sellOrder.getApplyAmount());
//                }
//
//                BigDecimal totalFinalSellUsedConfirmShare = BigDecimal.ZERO;
//
//                Iterator<EtfOrderPO> sellListIt = sellList.iterator();
//                BigDecimal usedRate = BigDecimal.ZERO;
//                while (sellListIt.hasNext()) {
//                    EtfOrderPO sellOrder = sellListIt.next();
//
//                    BigDecimal adjustShare;
//                    if (sellListIt.hasNext()) {
//                        BigDecimal rate = sellOrder.getApplyAmount().divide(totalSellApplyAmount, 6, RoundingMode.DOWN);
//                        adjustShare = overBuyShareTotal.multiply(rate);
//                    } else {
//                        if (mergeOrder.getOrderType() == EtfOrderTypeEnum.SELL) {
//                            adjustShare = (mergeOrder.getConfirmShare().add(totalFinalBuyConfirmShare)).subtract(totalFinalSellUsedConfirmShare);
//                        } else {
//                            adjustShare = (totalFinalBuyConfirmShare.subtract(mergeOrder.getConfirmAmount())).subtract(totalFinalSellUsedConfirmShare);
//                        }
//                    }
//
//                    BigDecimal reConfirmShare = sellOrder.getConfirmShare().subtract(adjustShare);
//
//                    BigDecimal adjustMoney = adjustShare.multiply(price);
//                    BigDecimal reConfirmMoney = sellOrder.getConfirmAmount().subtract(adjustMoney);
//
//                    sellOrder.setConfirmShare(reConfirmShare);
//                    sellOrder.setConfirmAmount(reConfirmMoney);
//
//                    totalFinalSellUsedConfirmShare = totalFinalSellUsedConfirmShare.add(reConfirmShare);
//                }
//            }
//        }
//
//        //超卖
//        if (!CollectionUtils.isEmpty(overSellList)) {
//            BigDecimal overSellShareTotal = BigDecimal.ZERO;
//            for (EtfOrderPO overSellOrder : overSellList) {
//                overSellShareTotal = overSellShareTotal.add(overSellShareMap.get(overSellOrder.getId()));
//            }
//
//            BigDecimal price = (mergeOrder.getConfirmAmount().add(mergeOrder.getCostFee()))
//                    .divide(mergeOrder.getConfirmShare(), 2, RoundingMode.DOWN);
//
//            //平衡卖单
//            for (EtfOrderPO overSellOrder : overSellList) {
//                BigDecimal adjustShare = overSellShareMap.get(overSellOrder.getId());
//                BigDecimal reConfirmShare = overSellOrder.getConfirmShare().subtract(adjustShare);
//
//                BigDecimal adjustMoney = adjustShare.multiply(price);
//                BigDecimal reConfirmMoney = overSellOrder.getConfirmAmount().subtract(adjustMoney);
//
//                overSellOrder.setConfirmShare(reConfirmShare);
//                overSellOrder.setConfirmAmount(reConfirmMoney);
//
//            }
//
//            //平衡买单
//            if (CollectionUtils.isEmpty(buyList)) {
//                // TODO: 2019-03-25
//
//
//            } else {
//                BigDecimal totalBuyApplyAmount = BigDecimal.ZERO;
//                for (EtfOrderPO buyOrder : buyList) {
//                    totalBuyApplyAmount = totalBuyApplyAmount.add(buyOrder.getApplyAmount());
//                }
//
//                Iterator<EtfOrderPO> buyListIt = buyList.iterator();
//                BigDecimal usedRate = BigDecimal.ZERO;
//                while (buyListIt.hasNext()) {
//                    EtfOrderPO buyOrder = buyListIt.next();
//
//                    BigDecimal rate;
//                    if (buyListIt.hasNext()) {
//                        rate = buyOrder.getApplyAmount().divide(totalBuyApplyAmount, 6, RoundingMode.DOWN);
//                        usedRate = usedRate.add(rate);
//                    } else {
//                        rate = BigDecimal.ONE.subtract(usedRate);
//                    }
//
//                    BigDecimal adjustShare = overSellShareTotal.multiply(rate);
//                    BigDecimal reConfirmShare = buyOrder.getConfirmShare().subtract(adjustShare);
//
//                    BigDecimal adjustMoney = adjustShare.multiply(price);
//                    BigDecimal reConfirmMoney = buyOrder.getConfirmAmount().subtract(adjustMoney);
//
//                    buyOrder.setConfirmShare(reConfirmShare);
//                    buyOrder.setConfirmAmount(reConfirmMoney);
//                }
//            }
//        }
//    }
//
//
//
//    //    @Override
////    public void saveTwapPrice() {
////        try {
////            String bsnDt = DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT2);
////
////            List<EtfInfoPO> etfInfoPOList = etfInfoMapper.getAllEtf();
////            for (EtfInfoPO etfInfo : etfInfoPOList) {
////                PriceInfoResp priceInfoResp = SaxoClient.queryPrice(etfInfo.getUic());
////
////                BigDecimal ask = BigDecimal.ZERO;
////                BigDecimal bid = BigDecimal.ZERO;
////                if (priceInfoResp != null && priceInfoResp.getMarketDepth() != null) {
////                    if (priceInfoResp.getMarketDepth().getAsk() != null) {
////                        if (priceInfoResp.getMarketDepth().getAsk()[0] != null) {
////                            ask = priceInfoResp.getMarketDepth().getAsk()[0];
////                        }
////                    }
////
////                    if (priceInfoResp.getMarketDepth().getBid() != null) {
////                        if (priceInfoResp.getMarketDepth().getBid()[0] != null) {
////                            bid = priceInfoResp.getMarketDepth().getBid()[0];
////                        }
////                    }
////                }
////
////                DailyTwapPricePO twapPrice = dailyTwapPriceMapper.getPrice(etfInfo.getEtfCode(), bsnDt);
////                if (twapPrice == null) {
////                    twapPrice = new DailyTwapPricePO();
////                    twapPrice.setEtfCode(etfInfo.getEtfCode());
////                    twapPrice.setBsnDt(bsnDt);
////                    twapPrice.setAvgAsk(ask);
////                    twapPrice.setAvgBid(bid);
////                    twapPrice.setAvgCount(1);
////                    dailyTwapPriceMapper.save(twapPrice);
////                } else {
////                    Integer avgCount = twapPrice.getAvgCount() + 1;
////                    BigDecimal avgAsk = (twapPrice.getAvgAsk().add(ask)).divide(new BigDecimal(avgCount), 2, RoundingMode.DOWN);
////                    BigDecimal avgBid = (twapPrice.getAvgBid().add(bid)).divide(new BigDecimal(avgCount), 2, RoundingMode.DOWN);
////                    dailyTwapPriceMapper.updatePrice(twapPrice.getId(), avgAsk, avgBid, avgCount);
////                }
////            }
////        } catch (Exception e) {
////            ErrorLogAndMailUtil.logErrorForTrade(log, e);
////        }
////    }
//
//    private void reviseShare(SaxoOrderTypeEnum orderType) {
//        try {
//            List<SaxoOrderPO> saxoOrderList = saxoOrderMapper.getOrderList(orderType, SaxoOrderStatusEnum.TRADING);
//            if (!CollectionUtils.isEmpty(saxoOrderList)) {
//                for (SaxoOrderPO saxoOrder : saxoOrderList) {
//                    List<String> orderLogStatus = Lists.newArrayList(OrderActivitiesResp.OrderLogStatus.Fill, OrderActivitiesResp.OrderLogStatus.FinalFill);
//                    OrderActivitiesResp activitiesResp = SaxoClient.queryOrderActivities(saxoOrder.getSaxoOrderCode(), orderLogStatus);
//
//                    if (activitiesResp.haveFill() && !activitiesResp.haveFinalFill()) {
//                        BigDecimal avgExecutionPrice;
//                        int nShares;
//
//                        BigDecimal fillShare = BigDecimal.ZERO;
//                        BigDecimal fillAmount = BigDecimal.ZERO;
//                        List<OrderActivitiesResp.ActivityData> activityDataList = activitiesResp.getFillActivity();
//                        for (OrderActivitiesResp.ActivityData activityData : activityDataList) {
//                            fillAmount = fillAmount.add(activityData.getExecutionPrice().multiply(activityData.getFillAmount()));
//                            fillShare = fillShare.add(activityData.getFillAmount());
//                        }
//                        avgExecutionPrice = fillAmount.divide(fillShare, 6, RoundingMode.DOWN);
//
//                        if (orderType == SaxoOrderTypeEnum.BUY) {
//                            nShares = getShare_buy(saxoOrder.getApplyAmount(), avgExecutionPrice);
//                        } else {
//                            nShares = getShare_sell(saxoOrder.getApplyAmount(), avgExecutionPrice);
//
////                            EtfInfoPO etfInfo = etfInfoMapper.getByUic(saxoOrder.getUic());
////                            List<EtfMergeOrderPO> etfOrderList = etfOrderMap.get(productCode);
////                            nShares = this.getFinalSharesSell(nShares, etfOrderList, etfInfo.getUic(), etfInfo.getEtfCode());
//                        }
//
//                        if (orderType == SaxoOrderTypeEnum.SELL) {
//                            HoldingInstrumentResp holdingInstrumentResp = SaxoClient.queryHoldingInstrument(saxoOrder.getUic());
//                            int holdingShares = holdingInstrumentResp.getAmount().intValue();
//                            nShares = Math.min(nShares - activitiesResp.getFilledShare(), holdingShares) + activitiesResp.getFilledShare();
//                        }
//
//                        SaxoClient.reviseMarketShare(saxoOrder.getSaxoOrderCode(), nShares);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            ErrorLogAndMailUtil.logErrorForTrade(log, e);
//        }
//    }
//
////    private void revisePrice(SaxoOrderTypeEnum orderType) {
////        try {
////            List<SaxoOrderPO> saxoOrderList = saxoOrderMapper.getOrderList(orderType, SaxoOrderStatusEnum.TRADING);
////            if (!CollectionUtils.isEmpty(saxoOrderList)) {
////                for (SaxoOrderPO saxoOrder : saxoOrderList) {
////                    List<String> orderLogStatus = Lists.newArrayList(OrderActivitiesResp.OrderLogStatus.Fill, OrderActivitiesResp.OrderLogStatus.FinalFill);
////                    OrderActivitiesResp activitiesResp = SaxoClient.queryOrderActivities(saxoOrder.getSaxoOrderCode(), orderLogStatus);
////
////                    if (!activitiesResp.haveFinalFill()) {
////                        BigDecimal estimatedPrice;
////                        BigDecimal finalPrice;
////                        int nShares;
////
////                        if (orderType == SaxoOrderTypeEnum.BUY) {
////                            estimatedPrice = SaxoClient.getEstimatedPrice_buy(saxoOrder.getUic());
////                            finalPrice = offsetEstimatedPrice_buy(estimatedPrice);
////                            nShares = getShare_buy(saxoOrder.getApplyAmount(), estimatedPrice);
////                        } else {
////                            estimatedPrice = SaxoClient.getEstimatedPrice_sell(saxoOrder.getUic());
////                            finalPrice = offsetEstimatedPrice_sell(estimatedPrice);
////                            nShares = getShare_sell(saxoOrder.getApplyAmount(), estimatedPrice);
////                        }
////
////                        if (orderType == SaxoOrderTypeEnum.SELL) {
////                            HoldingInstrumentResp holdingInstrumentResp = SaxoClient.queryHoldingInstrument(saxoOrder.getUic());
////                            int holdingShares = holdingInstrumentResp.getAmount().intValue();
////                            nShares = Math.min(nShares - activitiesResp.getFilledShare(), holdingShares) + activitiesResp.getFilledShare();
////                        }
////
////                        SaxoClient.revisePrice(saxoOrder.getSaxoOrderCode(), nShares, finalPrice);
////                    }
////                }
////            }
////        } catch (Exception e) {
////            ErrorLogAndMailUtil.logErrorForTrade(log, e);
////        }
////    }
////
////    private void reviseMarket(SaxoOrderTypeEnum orderType) {
////        try {
////            List<SaxoOrderPO> saxoOrderList = saxoOrderMapper.getOrderList(orderType, SaxoOrderStatusEnum.TRADING);
////            if (!CollectionUtils.isEmpty(saxoOrderList)) {
////                for (SaxoOrderPO saxoOrder : saxoOrderList) {
////                    List<String> orderLogStatus = Lists.newArrayList(OrderActivitiesResp.OrderLogStatus.Fill, OrderActivitiesResp.OrderLogStatus.FinalFill);
////                    OrderActivitiesResp activitiesResp = SaxoClient.queryOrderActivities(saxoOrder.getSaxoOrderCode(), orderLogStatus);
////
////                    if (!activitiesResp.haveFinalFill()) {
////                        SaxoClient.reviseToMarket(saxoOrder.getSaxoOrderCode());
////                    }
////                }
////            }
////        } catch (Exception e) {
////            ErrorLogAndMailUtil.logErrorForTrade(log, e);
////        }
////    }
//
//    @Override
//    public void reviseCancel() {
//        try {
//            QueryOpenOrderResp resp = SaxoClient.queryAllOpenOrder();
//            if (resp != null && !CollectionUtils.isEmpty(resp.getData())) {
//                for (QueryOpenOrderItem saxoOrder : resp.getData()) {
//                    List<String> orderLogStatus = Lists.newArrayList(OrderActivitiesResp.OrderLogStatus.Fill, OrderActivitiesResp.OrderLogStatus.FinalFill);
//                    OrderActivitiesResp activitiesResp = SaxoClient.queryOrderActivities(saxoOrder.getOrderId(), orderLogStatus);
//
//                    if (!activitiesResp.haveFinalFill()) {
//                        SaxoClient.cancelOrder(saxoOrder.getOrderId());
//                    }
//                }
//            }
//        } catch (Exception e) {
//            ErrorLogAndMailUtil.logErrorForTrade(log, e);
//        }
//    }
//
//    private void tradeConfirm(SaxoOrderTypeEnum orderType) {
//        try {
//            List<SaxoOrderPO> saxoOrderList = saxoOrderMapper.getOrderList(orderType, SaxoOrderStatusEnum.TRADING);
//            if (!CollectionUtils.isEmpty(saxoOrderList)) {
//                for (SaxoOrderPO saxoOrder : saxoOrderList) {
//                    try {
//                        if (saxoMockUtil.isMock()) {
//                            Integer confirmShare = saxoOrder.getApplyShare();
//                            Date now = DateUtils.now();
//                            BigDecimal cost = this.getEstimatedCost(confirmShare);
//                            BigDecimal confirmAmount;
//                            BigDecimal price = saxoMockUtil.getMockExecutionPrice(saxoOrder.getId(), orderType);
//                            if (orderType == SaxoOrderTypeEnum.BUY) {
//                                confirmAmount = new BigDecimal(confirmShare).multiply(price).add(cost);
//                            } else {
//                                confirmAmount = new BigDecimal(confirmShare).multiply(price).subtract(cost);
//                            }
//
//                            saxoOrderMapper.confirmOrder(
//                                    saxoOrder.getId(),
//                                    confirmShare,
//                                    confirmAmount,
//                                    now,
//                                    cost,
//                                    BigDecimal.ZERO,
//                                    BigDecimal.ZERO,
//                                    BigDecimal.ZERO,
//                                    BigDecimal.ZERO,
//                                    "",
//                                    SaxoOrderStatusEnum.FINISH);
//                            this.demergeSaxoOrder(saxoOrder.getId(), cost, confirmAmount, new BigDecimal(confirmShare));
//                        } else {
//                            List<String> orderLogStatus = Lists.newArrayList(OrderActivitiesResp.OrderLogStatus.Fill, OrderActivitiesResp.OrderLogStatus.FinalFill);
//                            OrderActivitiesResp activitiesResp = SaxoClient.queryOrderActivities(saxoOrder.getSaxoOrderCode(), orderLogStatus);
//
//                            List<SaxoOrderActivityPO> fillActivityList = Lists.newArrayList();
//                            for (OrderActivitiesResp.ActivityData activityData : activitiesResp.getData()) {
//                                SaxoOrderActivityPO orderActivity = SaxoOrderActivityPO.convert(activityData);
//
//                                if (orderActivity.isFillActivity()) {
//                                    fillActivityList.add(orderActivity);
//                                }
//
//                                saxoOrderActivityMapper.save(orderActivity);
//                            }
//
//                            Integer confirmShare = 0;
//                            BigDecimal confirmAmount = BigDecimal.ZERO;
//                            BigDecimal commission = BigDecimal.ZERO;
//                            BigDecimal exchangeFee = BigDecimal.ZERO;
//                            BigDecimal externalCharges = BigDecimal.ZERO;
//                            BigDecimal performanceFee = BigDecimal.ZERO;
//                            BigDecimal stampDuty = BigDecimal.ZERO;
//
//                            for (SaxoOrderActivityPO fillActivity : fillActivityList) {
//                                confirmShare = confirmShare + fillActivity.getFillAmount();
//                                confirmAmount = confirmAmount.add(fillActivity.getExecutionPrice().multiply(new BigDecimal(fillActivity.getFillAmount())));
//                            }
//
//                            String positionId = "";
//                            if (fillActivityList.size() > 0) {
//                                if (fillActivityList.stream().map(SaxoOrderActivityPO::getPositionId).distinct().count() != 1) {
//                                    // TODO: 2019-04-18 error
//                                }
//
//                                positionId = fillActivityList.get(0).getPositionId();
//                                PositionDetailResp positionDetailResp = SaxoClient.queryPositionDetail(positionId);
//
//                                PositionDetailResp.PositionDetails.CostData costData = positionDetailResp.getPositionDetails().getCloseCost();
//                                if (costData != null && costData.getCommission().compareTo(BigDecimal.ZERO) != 0) {
//                                    costData = positionDetailResp.getPositionDetails().getCloseCost();
//                                } else {
//                                    costData = positionDetailResp.getPositionDetails().getOpenCost();
//                                }
//
//                                commission = costData.getCommission();
//                                exchangeFee = costData.getExchangeFee();
//                                externalCharges = costData.getExternalCharges();
//                                performanceFee = costData.getPerformanceFee();
//                                stampDuty = costData.getStampDuty();
//                            }
//
//                            Date now = DateUtils.now();
//                            BigDecimal cost;
//                            if (orderType == SaxoOrderTypeEnum.BUY) {
//                                cost = commission;
//                                confirmAmount = confirmAmount.add(cost);
//                            } else {
//                                cost = commission.add(exchangeFee);
//                                confirmAmount = confirmAmount.subtract(cost);
//                            }
//
//                            saxoOrderMapper.confirmOrder(
//                                    saxoOrder.getId(),
//                                    confirmShare,
//                                    confirmAmount,
//                                    now,
//                                    commission,
//                                    exchangeFee,
//                                    externalCharges,
//                                    performanceFee,
//                                    stampDuty,
//                                    positionId,
//                                    SaxoOrderStatusEnum.FINISH);
//                            this.demergeSaxoOrder(saxoOrder.getId(), cost, confirmAmount, new BigDecimal(confirmShare));
//                        }
//                    } catch (Exception e) {
//                        ErrorLogAndMailUtil.logErrorForTrade(log, e);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            ErrorLogAndMailUtil.logErrorForTrade(log, e);
//        }
//    }
//
//    public void demergeSaxoOrder(
//            Long saxoOrderId,
//            BigDecimal saxoOrderCost,
//            BigDecimal saxoOrderConfirmAmount,
//            BigDecimal saxoOrderConfirmShare)
//    {
//        List<EtfMergeOrderPO> mergeOrderList = etfMergeOrderMapper.getBySaxoOrderId(saxoOrderId);
//
//        BigDecimal totalApply = BigDecimal.ZERO;
//        for (EtfMergeOrderPO mergeOrder : mergeOrderList) {
//            totalApply = totalApply.add(mergeOrder.getApplyAmount());
//        }
//
//        Map<Long, BigDecimal> rateMap = Maps.newHashMap();
//        BigDecimal used = BigDecimal.ZERO;
//        Iterator<EtfMergeOrderPO> mergeOrderListIt = mergeOrderList.iterator();
//        while (mergeOrderListIt.hasNext()){
//            EtfMergeOrderPO mergeOrder = mergeOrderListIt.next();
//            BigDecimal rate;
//            if (mergeOrderListIt.hasNext()) {
//                rate = mergeOrder.getApplyAmount().divide(totalApply, 6, RoundingMode.DOWN);
//            } else {
//                rate = BigDecimal.ONE.subtract(used);
//            }
//            rateMap.put(mergeOrder.getId(), rate);
//            used = used.add(rate);
//        }
//
//        for (EtfMergeOrderPO mergeOrder : mergeOrderList) {
//            BigDecimal rate = rateMap.get(mergeOrder.getId());
//            BigDecimal confirmAmount = saxoOrderConfirmAmount.multiply(rate);
//            BigDecimal costFee = saxoOrderCost.multiply(rate);
//            BigDecimal confirmShare = saxoOrderConfirmShare.multiply(rate);
//
//            etfMergeOrderMapper.tradeConfirm(mergeOrder.getId(), EtfMergeOrderStatusEnum.WAIT_DEMERGE, costFee, DateUtils.now(), confirmAmount, confirmShare);
//        }
//    }
//
//
//}
