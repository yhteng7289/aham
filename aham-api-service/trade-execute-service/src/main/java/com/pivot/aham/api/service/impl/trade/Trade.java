package com.pivot.aham.api.service.impl.trade;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pivot.aham.api.server.remoteservice.AhamTradingRemoteService;
import com.pivot.aham.api.service.client.saxo.SaxoClient;
import com.pivot.aham.api.service.client.saxo.resp.PlaceNewOrderResp;
import com.pivot.aham.api.service.impl.SaxoMockUtil;
import com.pivot.aham.api.service.mapper.*;
import com.pivot.aham.api.service.mapper.model.*;
import com.pivot.aham.common.enums.*;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.support.email.Email;
import com.pivot.aham.common.core.support.file.excel.ExportExcel;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.pivot.aham.common.core.support.file.excel.ExportExcel;
import com.pivot.aham.common.core.util.EmailUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import org.springframework.core.env.Environment;

/**
 * @program: aham
 * @description:
 * @author: zhang7
 * @create: 2019-07-02 15:32
 *
 */
@Component
@Slf4j
public class Trade {

    @Autowired
    private EtfMergeOrderMapper etfMergeOrderMapper;

    @Autowired
    private DailyClosingPriceMapper dailyClosingPriceMapper;

    @Autowired
    private EtfInfoMapper etfInfoMapper;

    @Autowired
    private SaxoOrderMapper saxoOrderMapper;

    @Resource
    private SaxoMockUtil saxoMockUtil;

    @Autowired
    private EtfOrderMapper etfOrderMapper;
    
    @Autowired
    private TradeOrderTypeMapper tradeOrderTypeMapper;
    
    @Autowired
    private Environment env;
    
    @Resource
    private Recalculate recalculate;
    @Resource
    private MergeOrder mergeOrder;
    @Resource
    private Trade trade;
    
    @Resource
    private AhamTradingRemoteService ahamTradingRemoteService;
    
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    private final String emailReceiver = PropertiesUtil.getString("aham.order.email.receiver");
    
    private final String emailTopic = PropertiesUtil.getString("aham.order.email.topic");
    
    private final String emailFilename = PropertiesUtil.getString("aham.order.email.filename");

    public void sellOrBuy() {
        List<EtfMergeOrderPO> mergeOrderList = etfMergeOrderMapper.getMergeOrder(
                Lists.newArrayList(EtfmergeOrderTypeEnum.SELL, EtfmergeOrderTypeEnum.BUY),
                EtfMergeOrderStatusEnum.WAIT_TRADE);
        
        List<SaxoOrderPO> lOrderETF = Lists.newArrayList();
        Long orderId = Sequence.next();
        List<EtfMergeOrderPO> etfOrderListSell = mergeOrderList.stream().filter((EtfMergeOrderPO e)
                -> EtfmergeOrderTypeEnum.SELL == e.getOrderType()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(etfOrderListSell)) {
            lOrderETF.addAll(sell(orderId));
            log.info("Sell lOrderETF >>" + lOrderETF.size());
        }
        List<EtfMergeOrderPO> etfOrderListBuy = mergeOrderList.stream().filter((EtfMergeOrderPO e)
                -> EtfmergeOrderTypeEnum.BUY == e.getOrderType()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(etfOrderListBuy)) {
            lOrderETF.addAll(buy(orderId));
            log.info("Buy lOrderETF >>" + lOrderETF.size());
        }
        log.info("lOrderETF >>" + lOrderETF.size());
        if(lOrderETF.size() > 0){
            TradeOrderTypePO tradeOrderTypePO = new TradeOrderTypePO();
            tradeOrderTypePO.setSaxoOrderCode(orderId.toString());
            tradeOrderTypePO.setIsRebalanceOrder("N");
            tradeOrderTypeMapper.save(tradeOrderTypePO);
            generateOrderExcel(lOrderETF);
        }else{
            recalculate.recalculate();
            mergeOrder.mergeEtfOrderForOrderType(true, true);
            Long newOrderId = Sequence.next();
            trade.buyTrade90(newOrderId);
        }

    }

    final Function<List<EtfMergeOrderExtendPO>, BigDecimal> getEtfMergeOrderExtendPOAmount = input -> Optional.ofNullable(input)
            .orElse(Lists.newArrayList()).stream().map(EtfMergeOrderExtendPO::getApplyAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    final Function<List<EtfMergeOrderExtendPO>, BigDecimal> getEtfMergeOrderExtendPOShare = input -> Optional.ofNullable(input)
            .orElse(Lists.newArrayList()).stream().map(EtfMergeOrderExtendPO::getApplyShare)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    final Function<List<EtfMergeOrderExtendPO>, BigDecimal> getEtfMergeOrderExtendPOCost = input -> Optional.ofNullable(input)
            .orElse(Lists.newArrayList()).stream().map(EtfMergeOrderExtendPO::getCostFee)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    public List<SaxoOrderPO> sell(Long orderId) {
        List<SaxoOrderPO> lOrderPO = Lists.newArrayList();
        try {
            List<EtfMergeOrderPO> mergeOrderList = etfMergeOrderMapper.getMergeOrder(Lists.newArrayList(EtfmergeOrderTypeEnum.SELL), EtfMergeOrderStatusEnum.WAIT_TRADE);
            if (CollectionUtils.isEmpty(mergeOrderList)) {
                log.info("未查询到需要出售的订单");
                return null;
            }
           
            Date now = DateUtils.now();
            Map<String, EtfMergeOrderPO> etfOrderMap = Maps.uniqueIndex(mergeOrderList, input -> input.getProductCode());

            List<EtfInfoPO> etfInfoPOList = etfInfoMapper.getByCodes(Lists.newArrayList(etfOrderMap.keySet()));
            Map<String, EtfInfoPO> etfInfoMap = Maps.uniqueIndex(etfInfoPOList, input -> input.getEtfCode());
            Map<String, BigDecimal> dailyClosingPriceMap = getDailyClosingPriceMapByMergeOrder(mergeOrderList);

            Iterator<String> etfOrderMapIt = etfOrderMap.keySet().iterator();
            final List<EtfOrderTypeEnum> conditionSell = Lists.newArrayList(EtfOrderTypeEnum.RSA,
                    EtfOrderTypeEnum.GSA, EtfOrderTypeEnum.RSP, EtfOrderTypeEnum.GSP, EtfOrderTypeEnum.PFT);

            final List<EtfOrderTypeEnum> conditionBuy = Lists.newArrayList(EtfOrderTypeEnum.GBA);
            while (etfOrderMapIt.hasNext()) {
                try {
                    final String productCode = etfOrderMapIt.next();
                    final EtfInfoPO etfInfo = Optional.of(etfInfoMap.get(productCode)).get();
                    final EtfMergeOrderPO etfMergeOrder = etfOrderMap.get(productCode);

                    final BigDecimal amount = etfMergeOrder.getApplyAmount();
                    BigDecimal estimatedPrice = BigDecimal.ZERO;
                    if (saxoMockUtil.isMock()) {
                        //estimatedPrice = getMockSellPrice(etfInfo.getEtfCode());
                        estimatedPrice = dailyClosingPriceMap.get(etfInfo.getEtfCode());
                    } else {
                        estimatedPrice = dailyClosingPriceMap.get(etfInfo.getEtfCode());
                    }

                    final List<EtfOrderPO> etfOrderPOSellList = etfOrderMapper.getListByMergeOrderId(etfMergeOrder.getId())
                            .stream().filter((EtfOrderPO e) -> conditionSell.contains(e.getOrderType())).collect(Collectors.toList());

                    final List<EtfOrderPO> etfOrderPOBuyList = etfOrderMapper.getListByMergeOrderId(etfMergeOrder.getId())
                            .stream().filter((EtfOrderPO e) -> conditionBuy.contains(e.getOrderType())).collect(Collectors.toList());

                    //BigDecimal estimatedCost = getEstimatedCost(amount, estimatedPrice).setScale(6, BigDecimal.ROUND_DOWN);
                    //log.info("amount {} , estimatedPrice {} , estimatedCost {} ", amount, estimatedPrice, estimatedCost);
                    //BigDecimal amountWithCost = amount.add(estimatedCost).setScale(6, BigDecimal.ROUND_DOWN);
                    BigDecimal amountWithCost = amount.add(BigDecimal.ZERO).setScale(6, BigDecimal.ROUND_DOWN);
                    //log.info("amount {} , estimatedCost {} , amountWithCost {} ", amount, estimatedCost, amountWithCost);
                    log.info("amount {} , amountWithCost {} ", amount, amountWithCost);
                    
                    
                    
//                    int sellShare = getShareSell(amountWithCost, estimatedPrice).intValue();
//                    int sellApplyShare = this.getDown(getEtfOrderPOShare.apply(etfOrderPOSellList));
                    final Function<List<EtfOrderPO>, BigDecimal> getEtfOrderPOShare = input -> Optional.ofNullable(input).
                            orElse(Lists.newArrayList()).stream().map(EtfOrderPO::getApplyShare).reduce(BigDecimal.ZERO, BigDecimal::add);
                    final Function<List<EtfOrderPO>, BigDecimal> getEtfOrderPOAmount = input -> Optional.ofNullable(input).
                            orElse(Lists.newArrayList()).stream().map(EtfOrderPO::getApplyAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

                    Integer differentShare = 0;

                    BigDecimal totalSellShare = getEtfOrderPOShare.apply(etfOrderPOSellList);
                    BigDecimal totalBuyShare = getEtfOrderPOShare.apply(etfOrderPOBuyList);
                    BigDecimal totalSellAmount = getEtfOrderPOAmount.apply(etfOrderPOSellList);
                    BigDecimal totalBuyAmount = getEtfOrderPOAmount.apply(etfOrderPOBuyList);

                    log.info("ProductCode {} , totalSellShare {} , totalBuyShare {} ", etfInfo.getEtfCode(), totalSellShare, totalBuyShare);
                    if (totalSellShare != null) {
                        BigDecimal compareShare = totalSellShare.subtract(totalBuyShare);
                        String strCompareShare = "" + compareShare;
                        if (strCompareShare.contains(".")) {
                            try {
                                String decimalPoint = "0." + strCompareShare.split("\\.")[1];
                                Double decimalDouble = Double.valueOf(decimalPoint);
                                //if (decimalDouble >= 0.5) {
                                //    differentShare = getUp(compareShare);
                                //} else {
                                    differentShare = getDown(compareShare);
                                //}

                            } catch (NumberFormatException e) {
                                differentShare = getDown(compareShare);
                            }

                        } else {
                            differentShare = getDown(compareShare);
                        }
                    }
                    log.info("ProductCode {} , differentShare {}", etfInfo.getEtfCode(), differentShare);

                    //防止超卖
                    BigDecimal _differentShare = new BigDecimal(differentShare);
                    if (_differentShare.compareTo(totalSellShare) > 0) {
                        _differentShare = totalSellShare;
                    }
                    log.info("ProductCode {} , _differentShare {}", etfInfo.getEtfCode(), _differentShare);
                    int shares = 0;
                    if (_differentShare != null) {
                        shares = _differentShare.intValue();
                    }

                    log.info("ProductCode {} , sellShare: {} , sellApplyShare: {}, shares : {}.", etfInfo.getEtfCode(), shares);
                    PlaceNewOrderResp placeNewOrderResp;

                    // Prevention on share is negative value
                    if (shares > 0) {
                        //预下单
                        final Long id = Sequence.next();
                        SaxoOrderPO saxoOrder = new SaxoOrderPO()
                                .setId(id)
                                .setUic(etfInfo.getUic())
                                .setEtfCode(etfInfo.getEtfCode())
                                .setOrderStatus(SaxoOrderStatusEnum.TRADING)
                                .setOrderType(SaxoOrderTypeEnum.SELL)
                                .setOrderTypeAhamDesc("RD")
                                .setApplyShare(shares)
                                .setApplyAmount(amountWithCost)
                                .setApplyTime(now)
                                .setSaxoOrderCode(orderId.toString());
                        saxoOrderMapper.save(saxoOrder);
                        lOrderPO.add(saxoOrder);
                        //if (saxoMockUtil.isMock()) {
                            //placeNewOrderResp = new PlaceNewOrderResp();
                           // placeNewOrderResp.setOrderId("1231231312312");
   
                            //ahamTradingRemoteService.placeAhamNewOrder(saxoOrder.getEtfCode(), saxoOrder.getOrderTypeAhamDesc(), 
                            //        saxoOrder.getApplyShare(), saxoOrder.getSaxoOrderCode(), saxoOrder.getApplyTime());
                        //} else {
                            //placeNewOrderResp = SaxoClient.placeSellMarketOrder(etfInfo.getUic(), shares, false);
                            log.info("====CALL SELL ORDER API=====MOCK FALSE");
                            ahamTradingRemoteService.placeAhamNewOrder(saxoOrder.getEtfCode(), saxoOrder.getOrderTypeAhamDesc(), 
                                    saxoOrder.getApplyShare(), saxoOrder.getSaxoOrderCode(), saxoOrder.getApplyTime());
                       // }

                        saxoOrderMapper.confirmOrderSuccess(id, orderId.toString(), SaxoOrderStatusEnum.TRADING_SUCCESS);
                        etfMergeOrderMapper.tradeExecute(etfMergeOrder.getId(), EtfMergeOrderStatusEnum.WAIT_CONFIRM, saxoOrder.getId(), totalSellAmount, totalBuyAmount, totalSellShare, totalBuyShare);
                    }
                } catch (Exception e) {
                    ErrorLogAndMailUtil.logErrorForTrade(log, e);
                }
            }

        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
       return lOrderPO;
    }

    public List<SaxoOrderPO> buy(Boolean isRebalancing, Long orderId) {
        List<SaxoOrderPO> lOrderBuyPO = Lists.newArrayList();
        try {
            List<EtfMergeOrderPO> mergeOrderList = etfMergeOrderMapper.getMergeOrder(Lists.newArrayList(EtfmergeOrderTypeEnum.BUY), EtfMergeOrderStatusEnum.WAIT_TRADE);
            if (CollectionUtils.isEmpty(mergeOrderList)) {
                log.info("未查询到需要购买的订单");
                return null;
            }

            Map<String, BigDecimal> dailyClosingPriceMap = this.getDailyClosingPriceMapByMergeOrder(mergeOrderList);
            Date now = DateUtils.now();

            Map<String, List<EtfMergeOrderPO>> etfMergeOrderMap = Maps.newHashMap();
            for (EtfMergeOrderPO mergeOrder : mergeOrderList) {
                List<EtfMergeOrderPO> etfMergeOrderList = etfMergeOrderMap.get(mergeOrder.getProductCode());
                if (etfMergeOrderList == null) {
                    etfMergeOrderList = Lists.newArrayList();
                }

                etfMergeOrderList.add(mergeOrder);
                etfMergeOrderMap.put(mergeOrder.getProductCode(), etfMergeOrderList);
            }

            Iterator<String> etfOrderMapIt = etfMergeOrderMap.keySet().iterator();
            final List<EtfOrderTypeEnum> conditionSell = Lists.newArrayList(EtfOrderTypeEnum.RSA,
                    EtfOrderTypeEnum.GSA, EtfOrderTypeEnum.RSP, EtfOrderTypeEnum.GSP, EtfOrderTypeEnum.PFT);

            final List<EtfOrderTypeEnum> conditionBuy = Lists.newArrayList(EtfOrderTypeEnum.GBA);
            while (etfOrderMapIt.hasNext()) {
                try {
                    String productCode = etfOrderMapIt.next();
                    List<EtfMergeOrderPO> _etfMergeOrderList = etfMergeOrderMap.get(productCode);
                    EtfInfoPO etfInfo = etfInfoMapper.getByCode(productCode);

                    BigDecimal dollarAmount = BigDecimal.ZERO;
                    for (EtfMergeOrderPO mergeOrder : _etfMergeOrderList) {
                        dollarAmount = dollarAmount.add(mergeOrder.getApplyAmount());
                    }

                    if (_etfMergeOrderList.size() > 1) {
                        ErrorLogAndMailUtil.logErrorForTrade(log, new BusinessException("Trade Merge Order List [Buy] More than 1"));
                        continue;
                    }

                    // _etfMergeOrderList always stick with buy / sell with 1 etf code, wont duplicated
                    EtfMergeOrderPO etfMergeOrder = _etfMergeOrderList.get(0);

                    final List<EtfOrderPO> etfOrderPOSellList = etfOrderMapper.getListByMergeOrderId(etfMergeOrder.getId())
                            .stream().filter((EtfOrderPO e) -> conditionSell.contains(e.getOrderType())).collect(Collectors.toList());

                    final List<EtfOrderPO> etfOrderPOBuyList = etfOrderMapper.getListByMergeOrderId(etfMergeOrder.getId())
                            .stream().filter((EtfOrderPO e) -> conditionBuy.contains(e.getOrderType())).collect(Collectors.toList());

                    final Function<List<EtfOrderPO>, BigDecimal> getEtfOrderPOAmount = input -> Optional.ofNullable(input).
                            orElse(Lists.newArrayList()).stream().map(EtfOrderPO::getApplyAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    final Function<List<EtfOrderPO>, BigDecimal> getEtfOrderPOShare = input -> Optional.ofNullable(input).
                            orElse(Lists.newArrayList()).stream().map(EtfOrderPO::getApplyShare).reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalSellAmount = getEtfOrderPOAmount.apply(etfOrderPOSellList);
                    BigDecimal totalBuyAmount = getEtfOrderPOAmount.apply(etfOrderPOBuyList);
                    BigDecimal totalSellShare = getEtfOrderPOShare.apply(etfOrderPOSellList);
                    BigDecimal totalBuyShare = getEtfOrderPOShare.apply(etfOrderPOBuyList);

                    BigDecimal estimatedPrice = BigDecimal.ZERO;

                    if (saxoMockUtil.isMock()) {
                        if (isRebalancing) {
                            estimatedPrice = getRebalancingBuyPrice(etfInfo.getEtfCode());
                        } else {
                            //estimatedPrice = getMockBuyPrice(etfInfo.getEtfCode());
                            estimatedPrice = dailyClosingPriceMap.get(etfInfo.getEtfCode());
                        }
                    } else {
                        estimatedPrice = dailyClosingPriceMap.get(etfInfo.getEtfCode());
                    }

                    //BigDecimal estimatedCost = getEstimatedCost(dollarAmount, estimatedPrice).setScale(6, BigDecimal.ROUND_DOWN);
                    //BigDecimal dollarAmountWithoutCost = dollarAmount.subtract(estimatedCost);

                    //为了防止超买，这里把总购买价缩小5%
//                    dollarAmountWithoutCost = dollarAmountWithoutCost.subtract(dollarAmountWithoutCost.multiply(new BigDecimal(0.05)));
                    //int nShares = getShareBuy(dollarAmountWithoutCost, estimatedPrice);
                    dollarAmount = dollarAmount.subtract(dollarAmount.multiply(new BigDecimal(0.05)));
                    int nShares = getShareBuy(dollarAmount, estimatedPrice);

                    if (nShares == 0) {
                        BigDecimal zero = BigDecimal.ZERO;
                        for (EtfMergeOrderPO mergeOrder : _etfMergeOrderList) {
                            etfMergeOrderMapper.tradeConfirm(mergeOrder.getId(), EtfMergeOrderStatusEnum.WAIT_DEMERGE, zero, now, zero, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                        }
                        continue;
                    }

                    //预下单
                    final Long id = Sequence.next();
                    SaxoOrderPO saxoOrder = new SaxoOrderPO()
                            .setId(id)
                            .setUic(etfInfo.getUic())
                            .setEtfCode(etfInfo.getEtfCode())
                            .setOrderStatus(SaxoOrderStatusEnum.TRADING)
                            .setOrderType(SaxoOrderTypeEnum.BUY)
                            .setOrderTypeAhamDesc("SA")
                            .setApplyShare(nShares)
                            //.setApplyAmount(dollarAmountWithoutCost)
                            .setApplyAmount(dollarAmount)
                            .setApplyTime(now)
                            .setSaxoOrderCode(orderId.toString());
                    saxoOrderMapper.save(saxoOrder);
                    lOrderBuyPO.add(saxoOrder);
                    PlaceNewOrderResp placeNewOrderResp;
                    if (saxoMockUtil.isMock()) {
                        //placeNewOrderResp = new PlaceNewOrderResp();
                        //placeNewOrderResp.setOrderId("231231231231");
                        //ahamTradingRemoteService.placeAhamNewOrder(saxoOrder.getEtfCode(), saxoOrder.getOrderTypeAhamDesc(), 
                        //            saxoOrder.getApplyShare(), saxoOrder.getSaxoOrderCode(), saxoOrder.getApplyTime());
                    } else {
                       // placeNewOrderResp = SaxoClient.placeBuyMarketOrder(etfInfo.getUic(), nShares, false);
                       log.info("====CALL BUY ORDER API=====MOCK FALSE");
                       ahamTradingRemoteService.placeAhamNewOrder(saxoOrder.getEtfCode(), saxoOrder.getOrderTypeAhamDesc(), 
                                    saxoOrder.getApplyShare(), saxoOrder.getSaxoOrderCode(), saxoOrder.getApplyTime());
                    }
                    saxoOrderMapper.confirmOrderSuccess(id, orderId.toString(), SaxoOrderStatusEnum.TRADING_SUCCESS);
                    for (EtfMergeOrderPO mergeOrder : _etfMergeOrderList) {
                        etfMergeOrderMapper.tradeExecute(mergeOrder.getId(), EtfMergeOrderStatusEnum.WAIT_CONFIRM, saxoOrder.getId(), totalSellAmount, totalBuyAmount, totalSellShare, totalBuyShare);
                    }
                } catch (Exception e) {
                    ErrorLogAndMailUtil.logErrorForTrade(log, e);
                }
            }
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
        return lOrderBuyPO;
    }

    public List<SaxoOrderPO> buy(Long orderId) {
        boolean isRebalancing = false;
        return buy(isRebalancing, orderId);
    }
    
    public void buyTrade90(Long orderId) {
        boolean isRebalancing = false;
        List<SaxoOrderPO> lOrderETF = Lists.newArrayList();
        lOrderETF = buy(isRebalancing, orderId);
        if(lOrderETF!=null && lOrderETF.size() > 0){
            TradeOrderTypePO tradeOrderTypePO = new TradeOrderTypePO();
            tradeOrderTypePO.setSaxoOrderCode(orderId.toString());
            tradeOrderTypePO.setIsRebalanceOrder("Y");
            tradeOrderTypeMapper.save(tradeOrderTypePO);
            generateOrderExcel(lOrderETF);
        }
    }

    private BigDecimal getEstimatedCost(BigDecimal dollarAmount, BigDecimal estimatedPrice) {
        return (dollarAmount.divide(estimatedPrice, 6, RoundingMode.DOWN).multiply(new BigDecimal(0.007))).max(new BigDecimal(3.99));
    }

    private BigDecimal getShareSell(BigDecimal dollarAmount, BigDecimal estimatedPrice) {
        BigDecimal share = dollarAmount.divide(estimatedPrice, 6, RoundingMode.DOWN);
        return share;
    }

    private int getShareSellUp(BigDecimal dollarAmount, BigDecimal estimatedPrice) {
        BigDecimal share = getShareSell(dollarAmount, estimatedPrice);
        return getUp(share);
    }

    private int getUp(BigDecimal b) {
        return Double.valueOf(Math.ceil(b.doubleValue())).intValue();
    }

    private int getDown(BigDecimal b) {
        return Double.valueOf(Math.floor(b.doubleValue())).intValue();
    }

    private int getShareBuy(BigDecimal dollarAmount, BigDecimal estimatedPrice) {
        return Double.valueOf(Math.floor(dollarAmount.divide(estimatedPrice, 6, RoundingMode.DOWN).doubleValue())).intValue();
    }

    private Map<String, BigDecimal> getDailyClosingPriceMapByMergeOrder(List<EtfMergeOrderPO> mergeOrderList) {
        Set<String> etfCodeList = Sets.newHashSet();
        mergeOrderList.forEach((EtfMergeOrderPO mergeOrder) -> etfCodeList.add(mergeOrder.getProductCode()));
        Map<String, BigDecimal> dailyClosingPriceMap = Maps.newHashMap();
        for (String etfCode : etfCodeList) {
            DailyClosingPricePO dailyClosingPricePO = dailyClosingPriceMapper.getLastPrice(etfCode);
            dailyClosingPriceMap.put(etfCode, dailyClosingPricePO.getPrice());
        }
        return dailyClosingPriceMap;
    }

    public BigDecimal getMockSellPrice(String productCode) {
        HashMap<String, BigDecimal> sellPriceMap = new HashMap();

        sellPriceMap.put("VT", new BigDecimal(70.51));
        sellPriceMap.put("EEM", new BigDecimal(42.06));
        sellPriceMap.put("BNDX", new BigDecimal(54.81));
        sellPriceMap.put("SHV", new BigDecimal(110.23));
        sellPriceMap.put("EMB", new BigDecimal(107.8));
        sellPriceMap.put("VWOB", new BigDecimal(77.01));
        sellPriceMap.put("BWX", new BigDecimal(27.65));
        sellPriceMap.put("HYG", new BigDecimal(85.01));
        sellPriceMap.put("JNK", new BigDecimal(34.89));
        sellPriceMap.put("MUB", new BigDecimal(109.51));
        sellPriceMap.put("LQD", new BigDecimal(116.26));
        sellPriceMap.put("VCIT", new BigDecimal(84.72));
        sellPriceMap.put("FLOT", new BigDecimal(51.09));
        sellPriceMap.put("IEF", new BigDecimal(104.41));
        sellPriceMap.put("UUP", new BigDecimal(26.18));
        sellPriceMap.put("PDBC", new BigDecimal(16.31));
        sellPriceMap.put("GLD", new BigDecimal(123.15));
        sellPriceMap.put("VNQ", new BigDecimal(84.37));
        sellPriceMap.put("VEA", new BigDecimal(39.52));
        sellPriceMap.put("VPL", new BigDecimal(64.18));
        sellPriceMap.put("EWA", new BigDecimal(20.72));
        sellPriceMap.put("SPY", new BigDecimal(270.74));
        sellPriceMap.put("VOO", new BigDecimal(248.59));
        sellPriceMap.put("VTI", new BigDecimal(139.45));
        sellPriceMap.put("VGK", new BigDecimal(51.15));
        sellPriceMap.put("EWJ", new BigDecimal(52.39));
        sellPriceMap.put("QQQ", new BigDecimal(168.52));
        sellPriceMap.put("EWS", new BigDecimal(23.49));
        sellPriceMap.put("EWZ", new BigDecimal(42.41));
        sellPriceMap.put("ASHR", new BigDecimal(24.75));
        sellPriceMap.put("VWO", new BigDecimal(40.79));
        sellPriceMap.put("ILF", new BigDecimal(33.42));
        sellPriceMap.put("RSX", new BigDecimal(20.89));
        sellPriceMap.put("AAXJ", new BigDecimal(68.09));

        BigDecimal price = sellPriceMap.get(productCode.toUpperCase());
        sellPriceMap.clear();
        return price;
    }

    public BigDecimal getMockBuyPrice(String productCode) {
        HashMap<String, BigDecimal> buyPriceMap = new HashMap();

        buyPriceMap.put("1ABF", new BigDecimal(0.63));
        buyPriceMap.put("BOND", new BigDecimal(0.72));
        buyPriceMap.put("BNDX", new BigDecimal(54.79));
        buyPriceMap.put("SHV", new BigDecimal(110.45));
        buyPriceMap.put("EMB", new BigDecimal(107.45));
        buyPriceMap.put("VWOB", new BigDecimal(76.57));
        buyPriceMap.put("BWX", new BigDecimal(27.43));
        buyPriceMap.put("HYG", new BigDecimal(84.83));
        buyPriceMap.put("JNK", new BigDecimal(34.91));
        buyPriceMap.put("MUB", new BigDecimal(109.34));
        buyPriceMap.put("LQD", new BigDecimal(116.3));
        buyPriceMap.put("VCIT", new BigDecimal(84.79));
        buyPriceMap.put("FLOT", new BigDecimal(50.75));
        buyPriceMap.put("IEF", new BigDecimal(104.41));
        buyPriceMap.put("UUP", new BigDecimal(25.62));
        buyPriceMap.put("PDBC", new BigDecimal(15.8));
        buyPriceMap.put("GLD", new BigDecimal(123.63));
        buyPriceMap.put("VNQ", new BigDecimal(84.11));
        buyPriceMap.put("VEA", new BigDecimal(39.29));
        buyPriceMap.put("VPL", new BigDecimal(63.95));
        buyPriceMap.put("EWA", new BigDecimal(21.05));
        buyPriceMap.put("SPY", new BigDecimal(270.61));
        buyPriceMap.put("VOO", new BigDecimal(248.93));
        buyPriceMap.put("VTI", new BigDecimal(139.09));
        buyPriceMap.put("VGK", new BigDecimal(51.38));
        buyPriceMap.put("EWJ", new BigDecimal(52.48));
        buyPriceMap.put("QQQ", new BigDecimal(168.29));
        buyPriceMap.put("EWS", new BigDecimal(23.5));
        buyPriceMap.put("EWZ", new BigDecimal(42.76));
        buyPriceMap.put("ASHR", new BigDecimal(24.48));
        buyPriceMap.put("VWO", new BigDecimal(41.06));
        buyPriceMap.put("ILF", new BigDecimal(33.41));
        buyPriceMap.put("RSX", new BigDecimal(20.78));
        buyPriceMap.put("AAXJ", new BigDecimal(68.08));

        BigDecimal price = buyPriceMap.get(productCode.toUpperCase());
        buyPriceMap.clear();
        return price;
    }

    public BigDecimal getRebalancingBuyPrice(String productCode) {
        HashMap<String, BigDecimal> buyPriceMap = new HashMap();
        buyPriceMap.put("VT", new BigDecimal(70.56));
        buyPriceMap.put("EEM", new BigDecimal(41.96));
        buyPriceMap.put("BNDX", new BigDecimal(54.82));
        buyPriceMap.put("SHV", new BigDecimal(110.37));
        buyPriceMap.put("EMB", new BigDecimal(107.57));
        buyPriceMap.put("VWOB", new BigDecimal(76.75));
        buyPriceMap.put("BWX", new BigDecimal(27.57));
        buyPriceMap.put("HYG", new BigDecimal(84.89));
        buyPriceMap.put("JNK", new BigDecimal(35.01));
        buyPriceMap.put("MUB", new BigDecimal(109.45));
        buyPriceMap.put("LQD", new BigDecimal(116.22));
        buyPriceMap.put("VCIT", new BigDecimal(84.76));
        buyPriceMap.put("FLOT", new BigDecimal(50.86));
        buyPriceMap.put("IEF", new BigDecimal(104.42));
        buyPriceMap.put("UUP", new BigDecimal(25.87));
        buyPriceMap.put("PDBC", new BigDecimal(16.05));
        buyPriceMap.put("GLD", new BigDecimal(123.46));
        buyPriceMap.put("VNQ", new BigDecimal(84.28));
        buyPriceMap.put("VEA", new BigDecimal(39.38));
        buyPriceMap.put("VPL", new BigDecimal(64.01));
        buyPriceMap.put("EWA", new BigDecimal(20.83));
        buyPriceMap.put("SPY", new BigDecimal(270.66));
        buyPriceMap.put("VOO", new BigDecimal(248.77));
        buyPriceMap.put("VTI", new BigDecimal(139.27));
        buyPriceMap.put("VGK", new BigDecimal(51.27));
        buyPriceMap.put("EWJ", new BigDecimal(52.55));
        buyPriceMap.put("QQQ", new BigDecimal(168.40));
        buyPriceMap.put("EWS", new BigDecimal(23.43));
        buyPriceMap.put("EWZ", new BigDecimal(42.55));
        buyPriceMap.put("ASHR", new BigDecimal(24.56));
        buyPriceMap.put("VWO", new BigDecimal(40.86));
        buyPriceMap.put("ILF", new BigDecimal(33.48));
        buyPriceMap.put("RSX", new BigDecimal(20.81));
        buyPriceMap.put("AAXJ", new BigDecimal(68.12));

        BigDecimal price = buyPriceMap.get(productCode.toUpperCase());
        buyPriceMap.clear();
        return price;
    }
    
    public void generateOrderExcel(List<SaxoOrderPO> lOrderPO){
        Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.MONTH, -1);
        log.info("calendar time {} ", calendar.getTime());
        String reportDate = sdf.format(calendar.getTime());
        log.info("report date {} ", reportDate);

        //List<UserAssetReport> userAssetList = userAssetReportingImpl.getUserAssetReportingByDate(reportDate);
        ExportExcel exportExcel = new ExportExcel(null, SaxoOrderPO.class);
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
        } catch (IOException | MessagingException e) {

        } finally {
            exportExcel.dispose();
        }
    }

}
