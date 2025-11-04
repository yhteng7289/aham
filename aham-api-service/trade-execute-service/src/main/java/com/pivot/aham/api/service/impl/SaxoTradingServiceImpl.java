//package com.pivot.aham.api.service.impl;
//
//import com.google.common.base.Function;
//import com.google.common.collect.Iterables;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.google.common.collect.Multimaps;
//import com.google.common.collect.Sets;
//import EtfCallbackDTO;
//import AccountEtfAssetReqDTO;
//import ReCalBuyEtfInBalReqDTO;
//import AccountEtfAssetResDTO;
//import AccountReBalanceRemoteService;
//import AssetServiceRemoteService;
//import SaxoTradingService;
//import SaxoClient;
//import OrderActivitiesResp;
//import PlaceNewOrderResp;
//import PositionDetailResp;
//import QueryOpenOrderItem;
//import QueryOpenOrderResp;
//import DailyClosingPriceMapper;
//import EtfInfoMapper;
//import EtfMergeOrderExtendMapper;
//import EtfMergeOrderMapper;
//import EtfOrderMapper;
//import SaxoOrderActivityMapper;
//import SaxoOrderMapper;
//import DailyClosingPricePO;
//import EtfInfoPO;
//import EtfMergeOrderExtendPO;
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
//import EtfmergeOrderTypeEnum;
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
//import java.util.Optional;
//import java.util.Set;
//import java.util.stream.Collectors;
//
///**
// * @program: aham
// * @description:
// * @author: zhang7
// * @create: 2019-06-27 16:13
// **/
//@Service("saxoTradingService")
//@Slf4j
//public class SaxoTradingServiceImpl implements SaxoTradingService {
//
//
//    @Autowired
//    private EtfOrderMapper etfOrderMapper;
//
//    @Autowired
//    private EtfMergeOrderMapper etfMergeOrderMapper;
//
//    @Autowired
//    private EtfMergeOrderExtendMapper etfMergeOrderExtendMapper;
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
//
//    @Override
//    public void mergeOrder() {
//        this.mergeOrder(true);
//    }
//
//    private void completionShareAndAmount(List<EtfOrderPO> orderList) {
//        Map<Long, List<EtfOrderPO>> etfOrderPOListMapByAccountId = Multimaps.asMap(Multimaps.index(orderList,
//                input -> input.getAccountId()));
//        Map<String, BigDecimal> dailyClosingPriceMapByProductId = getDailyClosingPriceMap(orderList);
//
//        //全赎取份额
//        final List<EtfOrderTypeEnum> conditionShare = Lists.newArrayList(EtfOrderTypeEnum.RSA,
//                EtfOrderTypeEnum.GSA);
//        //部分赎回算金额
//        final List<EtfOrderTypeEnum> conditionAmount = Lists.newArrayList(EtfOrderTypeEnum.RSP, EtfOrderTypeEnum.GSP,
//                EtfOrderTypeEnum.GBA);
//
//        etfOrderPOListMapByAccountId.forEach((k, v) -> {
//            AccountEtfAssetReqDTO reqDTO = new AccountEtfAssetReqDTO();
//            reqDTO.setAccountId(k);
//            RpcMessage<AccountEtfAssetResDTO> rpcMessage = assetServiceRemoteService.queryAccountEtfShare(reqDTO);
//
//            v.forEach(input -> {
//                BigDecimal holdingShare = Optional.ofNullable(rpcMessage.getContent().getDataMap()
//                        .get(input.getProductCode())).orElse(BigDecimal.ZERO);
//                String productId = input.getProductCode();
//                BigDecimal estimatedPrice = dailyClosingPriceMapByProductId.get(productId);
//                if (conditionShare.contains(input.getOrderType())) {
//                    input.setApplyShare(holdingShare);
//                }
//                if (conditionAmount.contains(input.getOrderType())) {
//                    BigDecimal share = input.getApplyAmount().divide(estimatedPrice, 2, RoundingMode.DOWN);
//                    input.setApplyShare(share);
//                }
//
//            });
//        });
//    }
//
//    final Function<List<EtfOrderPO>, BigDecimal> getEtfOrderPOAmount = input -> Optional.ofNullable(input)
//            .orElse(Lists.newArrayList()).stream().map(EtfOrderPO::getApplyAmount)
//            .reduce(BigDecimal.ZERO, BigDecimal::add);
//    final Function<List<EtfOrderPO>, BigDecimal> getEtfOrderPOShare = input -> Optional.ofNullable(input)
//            .orElse(Lists.newArrayList()).stream().map(EtfOrderPO::getApplyShare)
//            .reduce(BigDecimal.ZERO, BigDecimal::add);
//    final Function<List<EtfOrderPO>, BigDecimal> getEtfOrderPOCost = input -> Optional.ofNullable(input)
//            .orElse(Lists.newArrayList()).stream().map(EtfOrderPO::getCostFee)
//            .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//    private void mergeOrder(boolean checkExchange) {
//        try {
//            List<EtfOrderPO> orderList = etfOrderMapper.getListByStatus(EtfOrderStatusEnum.WAIT_MERGE);
//            if (CollectionUtils.isEmpty(orderList)) {
//                return;
//            }
//            completionShareAndAmount(orderList);
//
//            Map<String, List<EtfOrderPO>> etfOrderPOListMapByProductCode = Multimaps.asMap(Multimaps.index(orderList,
//                    input -> input.getProductCode()));
//
//
//            final List<EtfOrderTypeEnum> conditionSell = Lists.newArrayList(EtfOrderTypeEnum.RSA,
//                    EtfOrderTypeEnum.GSA, EtfOrderTypeEnum.RSP, EtfOrderTypeEnum.GSP, EtfOrderTypeEnum.PFT);
//            final List<EtfOrderTypeEnum> conditionBuy = Lists.newArrayList(EtfOrderTypeEnum.GBA);
//
//
//            final Function<BigDecimal, EtfmergeOrderTypeEnum> getEtfmergeOrderTypeEnum = input -> {
//                EtfmergeOrderTypeEnum mergeOrderType = null;
//                if (input.compareTo(BigDecimal.ZERO) == 0) mergeOrderType = EtfmergeOrderTypeEnum.DO_NOTHING;
//                if (input.compareTo(BigDecimal.ZERO) > 0) mergeOrderType = EtfmergeOrderTypeEnum.BUY;
//                if (input.compareTo(BigDecimal.ZERO) < 0) mergeOrderType = EtfmergeOrderTypeEnum.BUY;
//                return mergeOrderType;
//            };
//            final Function<BigDecimal, EtfMergeOrderStatusEnum> getEtfMergeOrderStatusEnum = input -> {
//                EtfMergeOrderStatusEnum orderStatus = null;
//                if (input.compareTo(BigDecimal.ZERO) == 0) orderStatus = EtfMergeOrderStatusEnum.WAIT_DEMERGE;
//                if (input.compareTo(BigDecimal.ZERO) > 0) orderStatus = EtfMergeOrderStatusEnum.WAIT_CONFIRM;
//                if (input.compareTo(BigDecimal.ZERO) < 0) orderStatus = EtfMergeOrderStatusEnum.WAIT_CONFIRM;
//                return orderStatus;
//            };
//
//
//            List<EtfMergeOrderPO> mergeOrderList = Lists.newArrayList();
//            List<EtfMergeOrderExtendPO> mergeOrderExtendList = Lists.newArrayList();
//
//            for (String productCode : etfOrderPOListMapByProductCode.keySet()) {
//                List<EtfOrderPO> etfOrderList = etfOrderPOListMapByProductCode.get(productCode);
//
//                Map<EtfOrderTypeEnum, List<EtfOrderPO>> etfOrderPOListMapByOrderType = Multimaps.asMap(Multimaps.index(etfOrderList, input -> input.getOrderType()));
//                List<EtfOrderPO> etfOrderPOBuyList = Lists.newArrayList(Iterables.filter(etfOrderList, t -> conditionBuy.contains(t.getOrderType())));
//                List<EtfOrderPO> etfOrderPOSellList = Lists.newArrayList(Iterables.filter(etfOrderList, t -> conditionSell.contains(t.getOrderType())));
//
//                BigDecimal sell = getEtfOrderPOAmount.apply(etfOrderPOSellList);
//                BigDecimal buy = getEtfOrderPOAmount.apply(etfOrderPOBuyList);
//
//                BigDecimal amount = sell.multiply(buy);
//                BigDecimal amountAbs = amount.abs();
//
//
//                final Long mergeOrderId = Sequence.next();
//                EtfMergeOrderPO mergeOrder = new EtfMergeOrderPO().setId(mergeOrderId)
//                        .setOrderType(getEtfmergeOrderTypeEnum.apply(amount))
//                        .setOrderStatus(getEtfMergeOrderStatusEnum.apply(amount))
//                        .setProductCode(productCode)
//                        .setApplyTime(DateUtils.now())
//                        .setApplyAmount(amountAbs);
//                mergeOrderList.add(mergeOrder);
//                EtfMergeOrderExtendPO mergeOrderExtend = new EtfMergeOrderExtendPO()
//                        .setEtfMergeOrderId(mergeOrderId)
//                        //钱
//                        .setCsaAmount(getEtfOrderPOAmount.apply(etfOrderPOListMapByOrderType.get(EtfOrderTypeEnum.GSA)))
//                        .setGbaAmount(getEtfOrderPOAmount.apply(etfOrderPOListMapByOrderType.get(EtfOrderTypeEnum.GBA)))
//                        .setGspAmount(getEtfOrderPOAmount.apply(etfOrderPOListMapByOrderType.get(EtfOrderTypeEnum.GSP)))
//                        .setPftAmount(getEtfOrderPOAmount.apply(etfOrderPOListMapByOrderType.get(EtfOrderTypeEnum.PFT)))
//                        .setRsaAmount(getEtfOrderPOAmount.apply(etfOrderPOListMapByOrderType.get(EtfOrderTypeEnum.RSA)))
//                        .setRspAmount(getEtfOrderPOAmount.apply(etfOrderPOListMapByOrderType.get(EtfOrderTypeEnum.RSP)))
//                        .setRbaAmount(getEtfOrderPOAmount.apply(etfOrderPOListMapByOrderType.get(EtfOrderTypeEnum.RBA)))
//                        //股数
//                        .setCsaShare(getEtfOrderPOShare.apply(etfOrderPOListMapByOrderType.get(EtfOrderTypeEnum.GSA)))
//                        .setGbaShare(getEtfOrderPOShare.apply(etfOrderPOListMapByOrderType.get(EtfOrderTypeEnum.GBA)))
//                        .setGspShare(getEtfOrderPOShare.apply(etfOrderPOListMapByOrderType.get(EtfOrderTypeEnum.GSP)))
//                        .setPftShare(getEtfOrderPOShare.apply(etfOrderPOListMapByOrderType.get(EtfOrderTypeEnum.PFT)))
//                        .setRsaShare(getEtfOrderPOShare.apply(etfOrderPOListMapByOrderType.get(EtfOrderTypeEnum.RSA)))
//                        .setRspShare(getEtfOrderPOShare.apply(etfOrderPOListMapByOrderType.get(EtfOrderTypeEnum.RSP)))
//                        .setRbaShare(getEtfOrderPOShare.apply(etfOrderPOListMapByOrderType.get(EtfOrderTypeEnum.RBA)));
//                mergeOrderExtendList.add(mergeOrderExtend);
//
//
//            }
//
//            if (checkExchange && !saxoMockUtil.isMock()) {
//                if (CollectionUtils.isEmpty(mergeOrderList)) {
//                    return;
//                }
//                List<String> exchangeList = etfInfoMapper.getExchangeByEtf(Lists.transform(mergeOrderList, input -> input.getProductCode()));
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
//            //TODO 后期优化io处理
//            mergeOrderList.forEach((EtfMergeOrderPO mergeOrder) -> etfMergeOrderMapper.save(mergeOrder));
//            mergeOrderExtendList.forEach((EtfMergeOrderExtendPO etfMergeOrderExtend) -> etfMergeOrderExtendMapper.save(etfMergeOrderExtend));
//            Map<String, EtfMergeOrderPO> etfMergeOrderMap = Maps.uniqueIndex(mergeOrderList, input -> input.getProductCode());
//            etfOrderPOListMapByProductCode.forEach((k, v) -> etfOrderMapper.updateMergeOrderId(Lists.transform(v, input -> input.getId())
//                    , etfMergeOrderMap.get(k).getId(), EtfOrderStatusEnum.WAIT_TRADE));
//        } catch (Exception e) {
//            ErrorLogAndMailUtil.logErrorForTrade(log, e);
//        }
//    }
//
//    @Override
//    public void sellOrBuy() {
//        List<EtfMergeOrderPO> mergeOrderList = etfMergeOrderMapper.getMergeOrder(
//                Lists.newArrayList(EtfmergeOrderTypeEnum.SELL, EtfmergeOrderTypeEnum.BUY),
//                EtfMergeOrderStatusEnum.WAIT_TRADE);
//
//        List<EtfMergeOrderPO> etfOrderListSell = mergeOrderList.stream().filter((EtfMergeOrderPO e)
//                -> EtfmergeOrderTypeEnum.SELL == e.getOrderType()).collect(Collectors.toList());
//        if (CollectionUtils.isNotEmpty(etfOrderListSell)) {
//            sell();
//        }
//        List<EtfMergeOrderPO> etfOrderListBuy = mergeOrderList.stream().filter((EtfMergeOrderPO e)
//                -> EtfmergeOrderTypeEnum.BUY == e.getOrderType()).collect(Collectors.toList());
//        if (CollectionUtils.isNotEmpty(etfOrderListBuy)) {
//            buy();
//        }
//
//    }
//
//
//    //    private static final BigDecimal rebate = new BigDecimal(0.005);
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
//    private BigDecimal getShareSell(BigDecimal dollarAmount, BigDecimal estimatedPrice) {
//        BigDecimal share = dollarAmount.divide(estimatedPrice, 6, RoundingMode.DOWN);
//        return share;
//    }
//
//    private int getShareSellUp(BigDecimal dollarAmount, BigDecimal estimatedPrice) {
//        BigDecimal share = getShareSell(dollarAmount, estimatedPrice);
//        return getUp(share);
//    }
//
//    private int getUp(BigDecimal b) {
//        return Double.valueOf(Math.ceil(b.doubleValue())).intValue();
//    }
//
//    private int getDown(BigDecimal b) {
//        return Double.valueOf(Math.ceil(b.doubleValue())).intValue();
//    }
//
//    private int getShareSellDown(BigDecimal dollarAmount, BigDecimal estimatedPrice) {
//        BigDecimal share = getShareSell(dollarAmount, estimatedPrice);
//        return getDown(share);
//    }
//
//    private int getShareBuy(BigDecimal dollarAmount, BigDecimal estimatedPrice) {
//        return Double.valueOf(Math.floor(dollarAmount.divide(estimatedPrice, 6, RoundingMode.DOWN).doubleValue())).intValue();
//    }
//
//    private Map<String, BigDecimal> getDailyClosingPriceMap(List<EtfOrderPO> orderList) {
//        List<String> productIdList = Lists.transform(orderList, input -> input.getProductCode());
//        Set<String> etfCodeList = Sets.newHashSet(productIdList);
//        Map<String, BigDecimal> dailyClosingPriceMap = Maps.newHashMap();
//        for (String etfCode : etfCodeList) {
//            DailyClosingPricePO dailyClosingPricePO = dailyClosingPriceMapper.getLastPrice(etfCode);
//            dailyClosingPriceMap.put(etfCode, dailyClosingPricePO.getPrice());
//        }
//        return dailyClosingPriceMap;
//    }
//
//    private Map<String, BigDecimal> getDailyClosingPriceMapByMergeOrder(List<EtfMergeOrderPO> mergeOrderList) {
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
//    @Override
//    public void sell() {
//        try {
//            List<EtfMergeOrderPO> mergeOrderList = etfMergeOrderMapper.getMergeOrder(Lists.newArrayList(EtfmergeOrderTypeEnum.SELL), EtfMergeOrderStatusEnum.WAIT_TRADE);
//            if (CollectionUtils.isEmpty(mergeOrderList)) {
//                log.info("未查询到需要出售的订单");
//                return;
//            }
//
//
//            Date now = DateUtils.now();
//            Map<String, EtfMergeOrderPO> etfOrderMap = Maps.uniqueIndex(mergeOrderList, input -> input.getProductCode());
//
//            List<EtfMergeOrderExtendPO> mergeOrderExtendPOList = etfMergeOrderExtendMapper.listByEtfMergeOrderIds(Lists.transform(mergeOrderList, input -> input.getId()));
//            Map<Long, EtfMergeOrderExtendPO> etfMergeOrderExtendMap = Maps.uniqueIndex(mergeOrderExtendPOList, input -> input.getEtfMergeOrderId());
//            List<EtfInfoPO> etfInfoPOList = etfInfoMapper.getByCodes(Lists.newArrayList(etfOrderMap.keySet()));
//            Map<String, EtfInfoPO> etfInfoMap = Maps.uniqueIndex(etfInfoPOList, input -> input.getEtfCode());
//            Map<String, BigDecimal> dailyClosingPriceMap = getDailyClosingPriceMapByMergeOrder(mergeOrderList);
//
//            Iterator<String> etfOrderMapIt = etfOrderMap.keySet().iterator();
//            while (etfOrderMapIt.hasNext()) {
//                try {
//                    final String productCode = etfOrderMapIt.next();
//                    final EtfInfoPO etfInfo = Optional.of(etfInfoMap.get(productCode)).get();
//                    final EtfMergeOrderPO etfMergeOrder = etfOrderMap.get(productCode);
//
//                    final BigDecimal amount = etfMergeOrder.getApplyAmount();
//                    final BigDecimal estimatedPrice = dailyClosingPriceMap.get(productCode);
//                    final EtfMergeOrderExtendPO etfMergeOrderExtend = etfMergeOrderExtendMap.get(productCode);
//
//                    BigDecimal estimatedCost = this.getEstimatedCost(amount, estimatedPrice);
//                    BigDecimal amountWithCost = amount.add(estimatedCost);
//
//                    //防止超卖
//                    int shares = new BigDecimal(this.getShareSellUp(amountWithCost, estimatedCost))
//                            .min(new BigDecimal(this.getDown(etfMergeOrderExtend.getSellShare())))
//                            .intValue();
//
//                    //TODO mock 做成注入的形式
//                    PlaceNewOrderResp placeNewOrderResp;
//                    if (saxoMockUtil.isMock()) {
//                        placeNewOrderResp = new PlaceNewOrderResp();
//                        placeNewOrderResp.setOrderId("1231231312312");
//                    } else {
//                        placeNewOrderResp = SaxoClient.placeSellMarketOrder(etfInfo.getUic(), shares, false);
//                    }
//
//                    SaxoOrderPO saxoOrder = new SaxoOrderPO();
//                    saxoOrder.setUic(etfInfo.getUic());
//                    saxoOrder.setSaxoOrderCode(placeNewOrderResp.getOrderId());
//                    saxoOrder.setOrderStatus(SaxoOrderStatusEnum.TRADING);
//                    saxoOrder.setOrderType(SaxoOrderTypeEnum.SELL);
//                    //TODO 过完整个逻辑 看看需不要吧EtfMergeOrderPO中的直接改掉
//                    saxoOrder.setApplyShare(shares);
//                    saxoOrder.setApplyAmount(amountWithCost);
//                    saxoOrder.setApplyTime(now);
//                    saxoOrderMapper.save(saxoOrder);
//                    etfMergeOrderMapper.tradeExecute(etfMergeOrder.getId(), EtfMergeOrderStatusEnum.WAIT_CONFIRM, saxoOrder.getId());
//                } catch (Exception e) {
//                    ErrorLogAndMailUtil.logErrorForTrade(log, e);
//                }
//            }
//
//        } catch (Exception e) {
//            ErrorLogAndMailUtil.logErrorForTrade(log, e);
//        }
//    }
//
//    @Override
//    public void reviseShareSell() {
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
//    public void tradeConfirmSell() {
//        this.tradeConfirm(SaxoOrderTypeEnum.SELL);
//    }
//
//    @Override
//    public void tradeConfirmSellOrBuy() {
//
//        List<SaxoOrderPO> saxoOrderSellList = saxoOrderMapper.getOrderList(SaxoOrderTypeEnum.SELL, SaxoOrderStatusEnum.TRADING);
//        if (CollectionUtils.isNotEmpty(saxoOrderSellList)) {
//            tradeConfirmSell();
//        }
//        List<SaxoOrderPO> saxoOrderBuyList = saxoOrderMapper.getOrderList(SaxoOrderTypeEnum.BUY, SaxoOrderStatusEnum.TRADING);
//        if (CollectionUtils.isNotEmpty(saxoOrderBuyList)) {
//            tradeConfirmBuy();
//        }
//
//    }
//
//    @Override
//    public void demergeOrderSell() {
//        this.demergeOrder(EtfmergeOrderTypeEnum.SELL);
//    }
//
//    @Override
//    public void demergeOrderSellOrBuy() {
//
//        List<EtfMergeOrderPO> mergeOrderList = etfMergeOrderMapper.getMergeOrder(
//                Lists.newArrayList(EtfmergeOrderTypeEnum.SELL, EtfmergeOrderTypeEnum.BUY, EtfmergeOrderTypeEnum.DO_NOTHING),
//                EtfMergeOrderStatusEnum.WAIT_TRADE);
//
//        List<EtfMergeOrderPO> etfOrderListSell = mergeOrderList.stream().filter((EtfMergeOrderPO e)
//                -> Lists.newArrayList(EtfmergeOrderTypeEnum.SELL, EtfmergeOrderTypeEnum.BUY).contains(e.getOrderType())).collect(Collectors.toList());
//        if (CollectionUtils.isNotEmpty(etfOrderListSell)) {
//            this.demergeOrder(EtfmergeOrderTypeEnum.SELL);
//        }
//        List<EtfMergeOrderPO> etfOrderListBuy = mergeOrderList.stream().filter((EtfMergeOrderPO e)
//                -> EtfmergeOrderTypeEnum.BUY == e.getOrderType()).collect(Collectors.toList());
//        if (CollectionUtils.isNotEmpty(etfOrderListBuy)) {
//            this.demergeOrder(EtfmergeOrderTypeEnum.BUY);
//        }
//
//    }
//
//    @Override
//    public void recalculate() {
//        List<ReCalBuyEtfInBalReqDTO> reqList = Lists.newArrayList();
//
//
//        final List<EtfOrderTypeEnum> conditionSell = Lists.newArrayList(EtfOrderTypeEnum.RSA,
//                EtfOrderTypeEnum.RSP);
//
//        List<EtfOrderPO> balanceSellOrderList = etfOrderMapper.getListByStatusAndType(EtfOrderStatusEnum.WAIT_NOTIFY, conditionSell);
//        for (EtfOrderPO etfOrderPO : balanceSellOrderList) {
//            BigDecimal price = etfOrderPO.getConfirmAmount().add(etfOrderPO.getCostFee()).divide(etfOrderPO.getConfirmShare(), 6, BigDecimal.ROUND_DOWN);
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
//            List<EtfMergeOrderPO> mergeOrderList = etfMergeOrderMapper.getMergeOrder(Lists.newArrayList(EtfmergeOrderTypeEnum.BUY), EtfMergeOrderStatusEnum.WAIT_TRADE);
//            if (CollectionUtils.isEmpty(mergeOrderList)) {
//                log.info("未查询到需要购买的订单");
//                return;
//            }
//
//            Map<String, BigDecimal> dailyClosingPriceMap = this.getDailyClosingPriceMapByMergeOrder(mergeOrderList);
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
////                    dollarAmountWithoutCost = dollarAmountWithoutCost.subtract(dollarAmountWithoutCost.multiply(new BigDecimal(0.05)));
//                    int nShares = getShareBuy(dollarAmountWithoutCost, estimatedPrice);
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
//    public void reviseShareBuy() {
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
//    public void tradeConfirmBuy() {
//        this.tradeConfirm(SaxoOrderTypeEnum.BUY);
//    }
//
//    @Override
//    public void demergeOrderBuy() {
//        this.demergeOrder(EtfmergeOrderTypeEnum.BUY);
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
//    private void demergeOrder(EtfmergeOrderTypeEnum orderType) {
//        try {
//            Date now = DateUtils.now();
//
//            //case 1
//            List<EtfmergeOrderTypeEnum> sellCase = Lists.newArrayList(EtfmergeOrderTypeEnum.SELL, EtfmergeOrderTypeEnum.DO_NOTHING);
//            //case 2 case3
//            List<EtfmergeOrderTypeEnum> buyCase = Lists.newArrayList(EtfmergeOrderTypeEnum.BUY);
//
//            List<EtfmergeOrderTypeEnum> orderTypeList = Lists.newArrayList();
//            //Same case donothing == sell
//            if (orderType == EtfmergeOrderTypeEnum.SELL) {
//                orderTypeList = sellCase;
//            }
//
//            List<EtfMergeOrderPO> mergeOrderPOList = etfMergeOrderMapper.getMergeOrder(orderTypeList, EtfMergeOrderStatusEnum.WAIT_DEMERGE);
//            List<EtfMergeOrderExtendPO> mergeOrderExtendPOList = etfMergeOrderExtendMapper.listByEtfMergeOrderIds(Lists.transform(mergeOrderPOList, input -> input.getId()));
//            Map<Long, EtfMergeOrderExtendPO> etfMergeOrderExtendMap = Maps.uniqueIndex(mergeOrderExtendPOList, input -> input.getEtfMergeOrderId());
//            Map<String, BigDecimal> dailyClosingPriceMap = this.getDailyClosingPriceMapByMergeOrder(mergeOrderPOList);
//            for (EtfMergeOrderPO order : mergeOrderPOList) {
//
//                //TODO 要换下
//                BigDecimal avgPrice = (order.getConfirmAmount().add(order.getCostFee())).divide(order.getConfirmShare(), 8, RoundingMode.DOWN);
//                EtfMergeOrderExtendPO etfMergeOrderExtend = etfMergeOrderExtendMap.get(order.getId());
//                List<EtfOrderPO> etfOrderList = etfOrderMapper.getListByMergeOrderId(order.getId());
//                Map<EtfOrderTypeEnum, List<EtfOrderPO>> etfOrderUpdateMap = Maps.newHashMap();
//
//
//                BigDecimal confirmAmount = order.getConfirmAmount();
//                //S_SAXO
//                BigDecimal confirmShare = order.getConfirmShare();
//                //TCS TCB
//                BigDecimal costFee = order.getCostFee();
//                String productCode = order.getProductCode();
//                BigDecimal dailyClosingPrice = dailyClosingPriceMap.get(productCode);
//
//
//                if (sellCase.contains(order.getOrderType())) {
//                    //SellX
//                    BigDecimal sellShare = etfMergeOrderExtend.getSellShare();
//                    //BuyX == CS == ZS2
//                    //TODO 这个要计算出来
//                    BigDecimal buyShare = etfMergeOrderExtend.getBuyShare();
//
//                    BigDecimal buyAmount = etfMergeOrderExtend.getGbaAmount();
//
//                    //CS = rounddown  ( Z2-TCS * [ ZS2 / ( SellX + BuyX ) ] ) / Price_Sell
//                    BigDecimal buyShareRate = buyShare.divide(sellShare.add(buyShare), 6, RoundingMode.DOWN);
//                    BigDecimal buyCostFee = costFee.multiply(buyShareRate);
//                    BigDecimal buyAmountWithOutCost = buyAmount.subtract(buyCostFee);
//                    BigDecimal buyShareByRealCostFee = buyAmountWithOutCost.divide(avgPrice, 2, BigDecimal.ROUND_HALF_UP);
//                    BigDecimal realCs = buyShareByRealCostFee;
//                    //S_SAXO + CS >= SellX  CS = SellX - S_SAXO
//                    if (confirmAmount.add(realCs).compareTo(sellShare) >= 0) {
//                        realCs = sellShare.subtract(confirmShare);
//                    }
//                    //分配XS1和XS2
//                    List<EtfOrderTypeEnum> firstPhase = Lists.newArrayList(EtfOrderTypeEnum.RSA, EtfOrderTypeEnum.GSA);
//                    List<EtfOrderPO> etfOrderListFilterByFirstPhase = etfOrderList.stream().filter((EtfOrderPO e)
//                            -> firstPhase.contains(e.getOrderType())).collect(Collectors.toList());
//                    for (EtfOrderPO etfOrderPO : etfOrderListFilterByFirstPhase) {
//                        //XS1 XS2
//
//                        //confirm amount = XS1 * Price_Sell - TC
//                        //confirm amount = XS2 * Price_Sell - TC
//                        BigDecimal shareTemp = etfOrderPO.getOrderType() == EtfOrderTypeEnum.RSA ?
//                                etfMergeOrderExtend.getRsaShare() :
//                                etfMergeOrderExtend.getGbaShare();
//                        BigDecimal shareRateTemp = shareTemp.divide(sellShare.add(buyShare), 6, RoundingMode.DOWN);
//
//                        //TC = XS1 / (SellX+BuyX) * TCS
//                        //TC = XS2 / (SellX+BuyX) * TCS
//                        BigDecimal costFeeTemp = costFee.multiply(shareRateTemp).setScale(2, RoundingMode.HALF_UP);
//                        BigDecimal realAmountWithOutCost = shareTemp.multiply(avgPrice).subtract(costFeeTemp);
//                        etfOrderUpdateMap.get(etfOrderPO.getOrderType()).add(new EtfOrderPO()
//                                .setId(etfOrderPO.getId())
//                                .setOrderStatus(EtfOrderStatusEnum.WAIT_NOTIFY)
//                                .setCostFee(costFeeTemp)
//                                .setConfirmAmount(realAmountWithOutCost)
//                                .setConfirmShare(shareTemp)
//                                .setConfirmTime(now));
//                    }
//
//                    //处理RSP GSP PFT
//                    List<EtfOrderTypeEnum> secondPhase = Lists.newArrayList(EtfOrderTypeEnum.RSP, EtfOrderTypeEnum.GSP);
//                    List<EtfOrderPO> etfOrderListFilterBySecondPhase = etfOrderList.stream().filter((EtfOrderPO e)
//                            -> secondPhase.contains(e.getOrderType())).collect(Collectors.toList());
//
//                    BigDecimal rsCs = costFee.subtract(getEtfOrderPOCost.apply(etfOrderUpdateMap.get(EtfOrderTypeEnum.RSA)))
//                            .subtract(getEtfOrderPOCost.apply(etfOrderUpdateMap.get(EtfOrderTypeEnum.GSA)));
//                    BigDecimal rsShare = confirmShare.add(realCs).subtract(etfMergeOrderExtend.getRsaShare()).subtract(etfMergeOrderExtend.getCsaShare());
//
//
//                    if (rsShare.compareTo(BigDecimal.ZERO) < 0) {
//                        //不卖只买，买shares=|RS|
//                        etfOrderUpdateMap.get(EtfOrderTypeEnum.PFT).add(new EtfOrderPO()
//                                .setOrderStatus(EtfOrderStatusEnum.WAIT_NOTIFY)
//                                //PFT Cost = 0
//                                .setCostFee(BigDecimal.ZERO)
//                                .setConfirmShare(rsShare.abs()));
//
//
//                        BigDecimal rsaShare = getEtfOrderPOShare.apply(etfOrderUpdateMap.get(EtfOrderTypeEnum.RSA));
//                        BigDecimal gapCost = getEtfOrderPOCost.apply(etfOrderUpdateMap.get(EtfOrderTypeEnum.GSP));
//                        BigDecimal rasCost = costFee.subtract(gapCost);
//
//                        etfOrderUpdateMap.get(EtfOrderTypeEnum.GSA).forEach(input -> {
//                            BigDecimal inputRate = input.getApplyShare().divide(rsaShare, 6, RoundingMode.DOWN);
//                            BigDecimal inputCost = rasCost.multiply(inputRate);
//                            input.setCostFee(inputCost);
//                        });
//
//                        //分配其他单shares为0
//                        List<EtfOrderTypeEnum> phase = Lists.newArrayList(EtfOrderTypeEnum.GSP, EtfOrderTypeEnum.RSP, EtfOrderTypeEnum.GBA);
//                        List<EtfOrderPO> etfOrderListFilterByphase = etfOrderList.stream().filter((EtfOrderPO e)
//                                -> phase.contains(e.getOrderType())).collect(Collectors.toList());
//                        etfOrderListFilterByphase.forEach(input ->
//                                etfOrderUpdateMap.get(input.getOrderType()).add(new EtfOrderPO()
//                                        .setId(input.getId())
//                                        .setOrderStatus(EtfOrderStatusEnum.WAIT_NOTIFY)
//                                        .setConfirmAmount(BigDecimal.ZERO)
//                                        .setCostFee(BigDecimal.ZERO)
//                                        .setConfirmShare(BigDecimal.ZERO)
//                                        .setConfirmTime(now)));
//                        continue;
//                    }
//
//                    if (rsShare.compareTo(BigDecimal.ZERO) >= 0) {
//                        //分配GSP&RSP
//                        Map<Long, List<EtfOrderPO>> rapAndGspEtfOrderByAccountMap = etfOrderListFilterBySecondPhase.stream().collect(Collectors.groupingBy(EtfOrderPO::getAccountId));
//                        for (Long accountId : rapAndGspEtfOrderByAccountMap.keySet()) {
//                            List<EtfOrderPO> etfOrderPOList = rapAndGspEtfOrderByAccountMap.get(accountId);
//                            BigDecimal accountSellAmout = getEtfOrderPOAmount.apply(etfOrderPOList);
//
//                            for (int i = 0; i < etfOrderPOList.size(); i++) {
//                                EtfOrderPO etfOrderPO = etfOrderPOList.get(i);
//                                BigDecimal rate = BigDecimal.ZERO;
//                                if (++i == etfOrderPOList.size()) {
//                                    rate = BigDecimal.ONE.subtract(rate);
//                                } else {
//                                    rate = etfOrderPO.getApplyAmount().divide(accountSellAmout, 6, RoundingMode.DOWN).setScale(2, RoundingMode.HALF_UP);
//                                }
//                                //shares = RS * [Y1/（Y1+Y2)]
//                                BigDecimal share = rsShare.multiply(rate);
//                                //TC = TC1 *[Y1/（Y1+Y2)]
//                                BigDecimal cost = rsCs.multiply(rate);
//
//                                share = this.handleOverBuySell(etfOrderPO, share);
//                                etfOrderPO.setCostFee(cost);
//                                etfOrderPO.setConfirmShare(share);
//                                BigDecimal amount = share.multiply(avgPrice).setScale(2, RoundingMode.HALF_UP);
//                                etfOrderPO.setConfirmAmount(amount);
//                                etfOrderPO.setConfirmTime(now);
//                            }
//
//                        }
//
//
//                        BigDecimal rspShare = getEtfOrderPOShare.apply(etfOrderUpdateMap.get(EtfOrderTypeEnum.RSP));
//                        BigDecimal gspShare = getEtfOrderPOShare.apply(etfOrderUpdateMap.get(EtfOrderTypeEnum.GSP));
//                        BigDecimal rspCost = getEtfOrderPOCost.apply(etfOrderUpdateMap.get(EtfOrderTypeEnum.RSP));
//                        BigDecimal gspCost = getEtfOrderPOCost.apply(etfOrderUpdateMap.get(EtfOrderTypeEnum.GSP));
//
//                        rsCs = costFee.subtract(rspCost).subtract(gspCost);
//                        rsShare = rsShare.subtract(rspShare).subtract(gspShare);
//
//                        //TODO 分配pft
//                        BigDecimal pftShare = etfMergeOrderExtend.getPftShare();
//                        if (rsShare.compareTo(BigDecimal.ZERO) > 0 && rsShare.compareTo(pftShare) <= 0) {
//
//                            //不卖只买，买shares=|RS|
//                            etfOrderUpdateMap.get(EtfOrderTypeEnum.PFT).add(new EtfOrderPO()
//                                    .setOrderStatus(EtfOrderStatusEnum.WAIT_NOTIFY)
//                                    //PFT Cost = 0
//                                    .setCostFee(BigDecimal.ZERO)
//                                    .setConfirmShare(rsShare.abs()));
//                            //TODO 记录买和卖
////                            BigDecimal share = pftShare.subtract(rsShare);
//                            continue;
//                        }
//                        if (rsShare.compareTo(BigDecimal.ZERO) > 0 && rsShare.compareTo(pftShare) > 0) {
//
//                            continue;
//                        }
//                        if (rsShare.compareTo(BigDecimal.ZERO) < 0) {
//                            BigDecimal share = rsShare.abs();
//                            //TODO 记录买和卖
//
//                            continue;
//                        }
//
//
//                        List<EtfOrderTypeEnum> thirdPhase = Lists.newArrayList(EtfOrderTypeEnum.GBA);
//                        List<EtfOrderPO> etfOrderListFilterByThirdPhase = etfOrderList.stream().filter((EtfOrderPO e)
//                                -> thirdPhase.contains(e.getOrderType())).collect(Collectors.toList());
//                        Map<Long, EtfOrderPO> gbaEtfOrderMapByAccount = Maps.uniqueIndex(etfOrderListFilterByThirdPhase, input -> input.getAccountId());
//                        BigDecimal accountAmout = getEtfOrderPOAmount.apply(etfOrderListFilterByThirdPhase);
//
//                        if (gbaEtfOrderMapByAccount.keySet().size() == 1) {
//                            BigDecimal share = realCs;
//                            BigDecimal cost = rsCs;
//                            BigDecimal amount = realCs.multiply(avgPrice).add(cost);
//
//                        }
//
//                        if (gbaEtfOrderMapByAccount.keySet().size() > 1) {
//                            for (Long accountId : gbaEtfOrderMapByAccount.keySet()) {
//                                EtfOrderPO etfOrder = gbaEtfOrderMapByAccount.get(accountId);
//                                BigDecimal amount = etfOrder.getApplyAmount();
//                                BigDecimal rate = amount.divide(accountAmout, 6, BigDecimal.ROUND_HALF_DOWN);
//                                BigDecimal share = realCs.multiply(rate).setScale(2, BigDecimal.ROUND_UP);
//                                BigDecimal cost = rsCs.multiply(rate).setScale(2, BigDecimal.ROUND_UP);
//                                BigDecimal amountWithCost = share.multiply(avgPrice).add(cost);
//                                if (amountWithCost.compareTo(amount) > 0) {
//                                    share = amount.subtract(cost).divide(avgPrice, 2, BigDecimal.ROUND_HALF_UP);
//                                }
//                                if (amountWithCost.compareTo(amount) <= 0) {
////TODO 不变
//                                }
//                                //TODO -1
//                            }
//                            BigDecimal gbaShare = getEtfOrderPOShare.apply(etfOrderUpdateMap.get(EtfOrderTypeEnum.GBA));
//                            if (gbaShare.compareTo(realCs) < 0) {
//                                //TODO but pft
//                            }
//
//
//                        }
//                    }
//
//
//                }
//
//                if (buyCase.contains(order.getOrderType())) {
//                    //收盘价判断
//                    //SellX
//                    BigDecimal sellShare = etfMergeOrderExtend.getBuyShare();
//                    //BuyX
//                    //TODO 这个要计算出来
//                    BigDecimal buyShare = etfMergeOrderExtend.getBuyShare();
//                    BigDecimal realCs = sellShare.subtract(costFee.multiply(
//                            sellShare.divide(sellShare.add(buyShare)))).divide(avgPrice, 2, BigDecimal.ROUND_HALF_DOWN);
//                    if (realCs.compareTo(sellShare) >= 0) {
//                        realCs = sellShare;
//                    }
//
////                    BigDecimal buyAmount = etfMergeOrderExtend.getGbaAmount();
////
////                    BigDecimal buyShareRate = buyShare.divide(sellShare.add(buyShare), 6, RoundingMode.DOWN);
////                    BigDecimal buyCostFee = costFee.multiply(buyShareRate);
////                    BigDecimal buyAmountWithOutCost = buyAmount.subtract(buyCostFee);
////                    BigDecimal buyShareByRealCostFee = buyAmountWithOutCost.divide(dailyClosingPrice, 2, BigDecimal.ROUND_HALF_UP);
////                    BigDecimal realCs = buyShareByRealCostFee;
//
//
//                    List<EtfOrderTypeEnum> firstPhase = Lists.newArrayList(EtfOrderTypeEnum.RSA, EtfOrderTypeEnum.GSA);
//                    List<EtfOrderPO> etfOrderListFilterByFirstPhase = etfOrderList.stream().filter((EtfOrderPO e)
//                            -> firstPhase.contains(e.getOrderType())).collect(Collectors.toList());
//                    for (EtfOrderPO etfOrderPO : etfOrderListFilterByFirstPhase) {
//                        //XS1 XS2
//                        BigDecimal shareTemp = etfMergeOrderExtend.getRsaShare();
//                        BigDecimal shareRateTemp = shareTemp.divide(sellShare.add(buyShare), 6, RoundingMode.DOWN);
//                        BigDecimal costFeeTemp = costFee.multiply(shareRateTemp).setScale(2, RoundingMode.HALF_UP);
//                        BigDecimal rsaRealAmount = shareTemp.multiply(avgPrice).subtract(costFeeTemp);
//                        etfOrderUpdateMap.get(etfOrderPO.getOrderType()).add(new EtfOrderPO()
//                                .setId(etfOrderPO.getId())
//                                .setOrderStatus(EtfOrderStatusEnum.WAIT_NOTIFY)
//                                .setCostFee(costFeeTemp)
//                                .setConfirmAmount(rsaRealAmount)
//                                .setConfirmShare(shareTemp)
//                                .setConfirmTime(now));
//                    }
//
//
//                    List<EtfOrderTypeEnum> secondPhase = Lists.newArrayList(EtfOrderTypeEnum.RSP, EtfOrderTypeEnum.GSP, EtfOrderTypeEnum.PFT);
//                    List<EtfOrderPO> etfOrderListFilterBySecondPhase = etfOrderList.stream().filter((EtfOrderPO e)
//                            -> secondPhase.contains(e.getOrderType())).collect(Collectors.toList());
//
//                    BigDecimal rsShare = realCs.multiply(etfMergeOrderExtend.getRsaShare()).multiply(etfMergeOrderExtend.getGbaShare());
//                    if (rsShare.compareTo(BigDecimal.ZERO) < 0) {
//                        BigDecimal share = rsShare.abs();
//                        //TODO buy pft
//
//                        for (EtfOrderPO etfOrderPO : etfOrderListFilterBySecondPhase) {
//                            etfOrderUpdateMap.get(etfOrderPO.getOrderType()).add(new EtfOrderPO()
//                                    .setId(etfOrderPO.getId())
//                                    .setOrderStatus(EtfOrderStatusEnum.WAIT_NOTIFY)
//                                    .setConfirmAmount(BigDecimal.ZERO)
//                                    .setConfirmShare(BigDecimal.ZERO)
//                                    .setCostFee(BigDecimal.ZERO)
//                                    .setConfirmTime(now));
//                        }
//
//                    }
//                    if (rsShare.compareTo(BigDecimal.ZERO) >= 0) {
//                        for (EtfOrderPO etfOrder : etfOrderListFilterBySecondPhase) {
//
//
//                        }
//                    }
//
//
//                }
//
//
//                etfOrderList.forEach((EtfOrderPO e) -> etfOrderMapper.confirm(e.getId(), EtfOrderStatusEnum.WAIT_NOTIFY, e.getCostFee(), now, e.getConfirmShare(), e.getConfirmAmount());)
//                etfMergeOrderMapper.updateStatus(order.getId(), EtfMergeOrderStatusEnum.FINISH);
//            }
//
//        } catch (
//                Exception e) {
//            ErrorLogAndMailUtil.logErrorForTrade(log, e);
//        }
//
//
//    }
//
//    private BigDecimal handleOverBuySell(EtfOrderPO etfOrder, BigDecimal share) {
//        BigDecimal holdingShare;
//        if (saxoMockUtil.isMock()) {
//            holdingShare = saxoMockUtil.getMockHoldingShare(etfOrder.getAccountId());
//        } else {
//            AccountEtfAssetReqDTO reqDTO = new AccountEtfAssetReqDTO();
//            reqDTO.setAccountId(etfOrder.getAccountId());
//            RpcMessage<AccountEtfAssetResDTO> rpcMessage = assetServiceRemoteService.queryAccountEtfShare(reqDTO);
//            holdingShare = rpcMessage.getContent().getDataMap().get(etfOrder.getProductCode());
//        }
//        if (share.compareTo(holdingShare) > 0) {
//            return holdingShare;
//        }
//        return share;
//    }
//
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
//                            //TODO 后期考虑 io入库
//                            EtfMergeOrderPO mergeOrder = etfMergeOrderMapper.getBySaxoOrderId(saxoOrder.getId());
//                            etfMergeOrderMapper.tradeConfirm(mergeOrder.getId(), EtfMergeOrderStatusEnum.WAIT_DEMERGE,
//                                    cost, DateUtils.now(), confirmAmount, new BigDecimal(confirmShare));
//                        } else {
//                            //TODO 最后重写
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
//                            //TODO 后期考虑 io入库
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
//                            EtfMergeOrderPO mergeOrder = etfMergeOrderMapper.getBySaxoOrderId(saxoOrder.getId());
//                            etfMergeOrderMapper.tradeConfirm(mergeOrder.getId(), EtfMergeOrderStatusEnum.WAIT_DEMERGE,
//                                    cost, DateUtils.now(), confirmAmount, new BigDecimal(confirmShare));
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
//}
