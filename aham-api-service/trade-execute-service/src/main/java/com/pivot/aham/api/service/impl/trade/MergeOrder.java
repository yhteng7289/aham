package com.pivot.aham.api.service.impl.trade;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.pivot.aham.api.server.dto.req.AccountEtfAssetReqDTO;
import com.pivot.aham.api.server.dto.req.PivotPftAccountResDTO;
import com.pivot.aham.api.server.dto.req.PivotPftHoldingResDTO;
import com.pivot.aham.api.server.dto.res.AccountEtfAssetResDTO;
import com.pivot.aham.api.server.remoteservice.AssetServiceRemoteService;
import com.pivot.aham.api.server.remoteservice.PivotPftRemoteService;
import com.pivot.aham.api.service.client.saxo.SaxoClient;
import com.pivot.aham.api.service.impl.SaxoMockUtil;
import com.pivot.aham.api.service.mapper.*;
import com.pivot.aham.common.enums.*;
import com.pivot.aham.api.service.mapper.model.DailyClosingPricePO;
import com.pivot.aham.api.service.mapper.model.EtfMergeOrderExtendPO;
import com.pivot.aham.api.service.mapper.model.EtfMergeOrderPO;
import com.pivot.aham.api.service.mapper.model.EtfOrderPO;
import com.pivot.aham.api.service.mapper.model.SaxoMinOrderPO;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * @program: squirrelsave
 * @description:
 * @author: zhang7
 * @create: 2019-07-02 15:31
 *
 */
@Component
@Slf4j
public class MergeOrder {

    @Autowired
    private EtfOrderMapper etfOrderMapper;

    @Autowired
    private EtfMergeOrderMapper etfMergeOrderMapper;

    @Autowired
    private EtfMergeOrderExtendMapper etfMergeOrderExtendMapper;

    @Autowired
    private DailyClosingPriceMapper dailyClosingPriceMapper;

    @Autowired
    private EtfInfoMapper etfInfoMapper;

    @Autowired
    private AssetServiceRemoteService assetServiceRemoteService;

    @Resource
    private SaxoMockUtil saxoMockUtil;

    @Autowired
    private PivotPftRemoteService pivotPftRemoteService;
    
    @Autowired
    private SaxoMinOrderMapper saxoMinOrderMapper;

    final Function<List<EtfOrderPO>, BigDecimal> getEtfOrderPOAmount = input -> Optional.ofNullable(input)
            .orElse(Lists.newArrayList()).stream().map(EtfOrderPO::getApplyAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    final Function<List<EtfOrderPO>, BigDecimal> getEtfOrderPOShare = input -> Optional.ofNullable(input)
            .orElse(Lists.newArrayList()).stream().map(EtfOrderPO::getApplyShare)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    final List<EtfOrderTypeEnum> conditionSell = Lists.newArrayList(EtfOrderTypeEnum.RSA,
            EtfOrderTypeEnum.GSA, EtfOrderTypeEnum.RSP, EtfOrderTypeEnum.GSP, EtfOrderTypeEnum.PFT);
    final List<EtfOrderTypeEnum> conditionBuy = Lists.newArrayList(EtfOrderTypeEnum.GBA, EtfOrderTypeEnum.RBA);

    public void mergeEtfOrderForOrderType(boolean checkExchange, boolean buyFlag) {

        List<EtfOrderPO> orderList = etfOrderMapper.getListByStatus(EtfOrderStatusEnum.WAIT_MERGE);

        //在平衡买单需要单独处理
        if (buyFlag) {
            orderList = orderList.stream().filter((EtfOrderPO e) -> e.getOrderType() == EtfOrderTypeEnum.RBA).collect(Collectors.toList());
        } else {
            orderList = orderList.stream().filter((EtfOrderPO e) -> e.getOrderType() != EtfOrderTypeEnum.RBA).collect(Collectors.toList());
        }

        // update the apply share and apply money into etf order table;
        completionShareAndAmount(orderList);
        if (!buyFlag) {
            completionPftShareAmountAndToDB(orderList);
        }

        if (CollectionUtils.isEmpty(orderList)) {
            return;
        }

        Map<String, List<EtfOrderPO>> etfOrderPOListMapByProductCode = Multimaps.asMap(Multimaps.index(orderList,
                input -> input.getProductCode()));

        //交易事件判断
        /*if (checkExchange && !saxoMockUtil.isMock()) {
            if (CollectionUtils.isEmpty(etfOrderPOListMapByProductCode.keySet())) {
                return;
            }
            List<String> exchangeList = etfInfoMapper.getExchangeByEtf(Lists.newArrayList(etfOrderPOListMapByProductCode.keySet()));
            Date nowUtc = DateUtils.nowUTC();
            for (String exchangeCode : exchangeList) {
                if (!SaxoClient.isAllOpenExchange(exchangeCode, nowUtc)) {
                    for (EtfOrderPO etfOrderPO : orderList) {
                        etfOrderMapper.confirm(etfOrderPO.getId(), EtfOrderStatusEnum.WAIT_NOTIFY, BigDecimal.ZERO, DateUtils.now(), BigDecimal.ZERO, BigDecimal.ZERO, "");
                    }
                    ErrorLogAndMailUtil.logErrorForTrade(log, "交易所开市时间未满足约定时长, 终止合并订单, 并将所有订单全部0金额确认，交易所code --> " + exchangeCode);
                    return;
                }
            }
        }*/

        Map<String, BigDecimal> dailyClosingPriceMapByProductId = this.getDailyClosingPriceMap(Lists.transform(orderList, input -> input.getProductCode()));

        final BiFunction<BigDecimal, String, EtfmergeOrderTypeEnum> getEtfmergeOrderTypeEnum = (bigDecimal, s) -> {
            EtfmergeOrderTypeEnum mergeOrderType = null;
            if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) {
                mergeOrderType = EtfmergeOrderTypeEnum.DO_NOTHING;
            }
            if (bigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                mergeOrderType = EtfmergeOrderTypeEnum.SELL;
            }
            if (bigDecimal.compareTo(BigDecimal.ZERO) < 0) {
                mergeOrderType = EtfmergeOrderTypeEnum.BUY;
            }
            //小于收盘价则不处理
            if (dailyClosingPriceMapByProductId.get(s).compareTo(bigDecimal.abs()) > 0) {
                mergeOrderType = EtfmergeOrderTypeEnum.DO_NOTHING;
            }
            
            //added by wooitatt for PDBC Min USD condition
            Map<String, SaxoMinOrderPO> mapSaxoMinOrder= getMapSaxoMinOrder();  
            if(mapSaxoMinOrder.containsKey(s) && mergeOrderType != EtfmergeOrderTypeEnum.DO_NOTHING ){
                SaxoMinOrderPO saxoMinOrderPO = mapSaxoMinOrder.get(s);
                if (bigDecimal.abs().compareTo(saxoMinOrderPO.getMinAmount()) < 0) {
                    mergeOrderType = EtfmergeOrderTypeEnum.DO_NOTHING;
                }
            }
            return mergeOrderType;
        };

        final BiFunction<BigDecimal, String, EtfMergeOrderStatusEnum> getEtfMergeOrderStatusEnum = (bigDecimal, s) -> {
            EtfMergeOrderStatusEnum orderStatus = null;
            if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) {
                orderStatus = EtfMergeOrderStatusEnum.WAIT_DEMERGE;
            }
            if (bigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                orderStatus = EtfMergeOrderStatusEnum.WAIT_TRADE;
            }
            if (bigDecimal.compareTo(BigDecimal.ZERO) < 0) {
                orderStatus = EtfMergeOrderStatusEnum.WAIT_TRADE;
            }
            //小于收盘价则不处理
            if (dailyClosingPriceMapByProductId.get(s).compareTo(bigDecimal.abs()) > 0) {
                orderStatus = EtfMergeOrderStatusEnum.WAIT_DEMERGE;
            }
            
            //added by wooitatt for PDBC Min USD condition
            Map<String, SaxoMinOrderPO> mapSaxoMinOrder= getMapSaxoMinOrder(); 
            if(mapSaxoMinOrder.containsKey(s) && orderStatus != EtfMergeOrderStatusEnum.WAIT_DEMERGE ){
                SaxoMinOrderPO saxoMinOrderPO = mapSaxoMinOrder.get(s);
                if (bigDecimal.abs().compareTo(saxoMinOrderPO.getMinAmount()) < 0) {
                    orderStatus = EtfMergeOrderStatusEnum.WAIT_DEMERGE;
                }
            }

            return orderStatus;
        };

        Date now = DateUtils.now();
        //按产品合并MergeOrder
        etfOrderPOListMapByProductCode.forEach((k, v) -> {
            List<EtfOrderPO> sellList = v.stream().filter((EtfOrderPO e) -> conditionSell.contains(e.getOrderType())).collect(Collectors.toList());
            List<EtfOrderPO> buyList = v.stream().filter((EtfOrderPO e) -> conditionBuy.contains(e.getOrderType())).collect(Collectors.toList());
            BigDecimal sellAmountTotal = this.getEtfOrderPOAmount.apply(sellList);
            BigDecimal buyAmountTotal = this.getEtfOrderPOAmount.apply(buyList);

            BigDecimal totalSellShare = getEtfOrderPOShare.apply(sellList);
            BigDecimal totalBuyShare = getEtfOrderPOShare.apply(buyList);

            BigDecimal applyShare = totalSellShare.subtract(totalBuyShare).abs();

            Map<String, BigDecimal> priceListMap = getDailyClosingPriceMap(Lists.newArrayList(k));
            BigDecimal price = priceListMap.get(k);
            log.info("product_code , applyShare : {} , price {} ", k, applyShare, price);

            BigDecimal finalApplyAmount = applyShare.multiply(price).setScale(2, RoundingMode.HALF_DOWN);
            log.info("product_code {} finalApplyAmount {} ", k, finalApplyAmount);

            final Long mergeOrderId = Sequence.next();
            EtfMergeOrderPO mergeOrder = new EtfMergeOrderPO()
                    .setId(mergeOrderId)
                    .setOrderType(getEtfmergeOrderTypeEnum.apply(sellAmountTotal.subtract(buyAmountTotal), k))
                    .setOrderStatus(getEtfMergeOrderStatusEnum.apply(sellAmountTotal.subtract(buyAmountTotal), k))
                    .setProductCode(k)
                    .setApplyTime(DateUtils.now())
                    .setTotalSellShare(totalSellShare)
                    .setTotalBuyShare(totalBuyShare)
                    .setApplyAmount(finalApplyAmount);

            etfMergeOrderMapper.save(mergeOrder);
            for(int i=0; i < sellList.size(); i++){
                if(sellList.get(i).getOrderType() == EtfOrderTypeEnum.PFT ){
                    PivotPftHoldingResDTO pivotPftHoldingResDTO = new PivotPftHoldingResDTO();
                    pivotPftHoldingResDTO.setMerdeOrderId(mergeOrderId);
                    pivotPftHoldingResDTO.setEtfOrderId(sellList.get(i).getId());
                    pivotPftHoldingResDTO.setShare(sellList.get(i).getApplyShare());
                    pivotPftHoldingResDTO.setStatus(PftHoldingStatusEnum.HOLDING);
                    pivotPftHoldingResDTO.setProductCode(sellList.get(i).getProductCode());
                    pivotPftRemoteService.savePftHolding(pivotPftHoldingResDTO);
                }
            }
            //按OrderType合并MergeOrderExtend
            Map<EtfOrderTypeEnum, List<EtfOrderPO>> etfOrderPOListMapByType = Multimaps.asMap(Multimaps.index(v,
                    input -> input.getOrderType()));
            etfOrderPOListMapByType.forEach((ik, iv) -> {
                BigDecimal totalApplyShare = getEtfOrderPOShare.apply(iv);
                BigDecimal totalApplyAmount = getEtfOrderPOAmount.apply(iv);
                etfMergeOrderExtendMapper.save(new EtfMergeOrderExtendPO().setProductCode(k).setMergeOrderId(mergeOrderId)
                        .setApplyShare(totalApplyShare).setApplyAmount(totalApplyAmount)
                        .setOrderType(ik).setOrderExtendStatus(EtfOrderExtendStatusEnum.WAIT_CONFIRM)
                        .setApplyTime(now).setUpdateTime(now).setCreateTime(now));
         
            });
            etfOrderMapper.updateMergeOrderId(Lists.transform(v, input -> input.getId()), mergeOrderId, EtfOrderStatusEnum.WAIT_TRADE, price);
            
        });

    }

    private void completionShareAndAmount(List<EtfOrderPO> orderList) {

        Map<Long, List<EtfOrderPO>> etfOrderPOListMapByAccountId = Multimaps.asMap(Multimaps.index(orderList,
                input -> input.getAccountId()));
        List<String> productIdList = Lists.transform(orderList, input -> input.getProductCode());

        Map<String, BigDecimal> dailyClosingPriceMapByProductId = getDailyClosingPriceMap(productIdList);

        //全赎取份额
        final List<EtfOrderTypeEnum> conditionShare = Lists.newArrayList(EtfOrderTypeEnum.RSA,
                EtfOrderTypeEnum.GSA);
        //部分赎回 和 申购 算金额
        final List<EtfOrderTypeEnum> conditionAmount = Lists.newArrayList(EtfOrderTypeEnum.RSP, EtfOrderTypeEnum.GSP,
                EtfOrderTypeEnum.GBA, EtfOrderTypeEnum.RBA);

        etfOrderPOListMapByAccountId.forEach((k, v) -> {
            AccountEtfAssetReqDTO reqDTO = new AccountEtfAssetReqDTO();
            reqDTO.setAccountId(k);
            RpcMessage<AccountEtfAssetResDTO> rpcMessage = assetServiceRemoteService.queryAccountEtfShare(reqDTO);

            v.forEach(input -> {

                BigDecimal holdingShare = Optional.ofNullable(rpcMessage.getContent().getDataMap()
                        .get(input.getProductCode())).orElse(BigDecimal.ZERO);
                String productId = input.getProductCode();
                BigDecimal estimatedPrice = dailyClosingPriceMapByProductId.get(productId);
                log.info("holdingShare:{}.", holdingShare);

                // if isMock, it wont calculate the normal sell etf totalShare amount in testing environment
                if (conditionShare.contains(input.getOrderType())) {
                    input.setApplyShare(holdingShare);
                }
                if (conditionAmount.contains(input.getOrderType())) {
                    BigDecimal share = input.getApplyAmount().divide(estimatedPrice, 2, RoundingMode.HALF_UP);
                    input.setApplyShare(share);
                }

//                if (saxoMockUtil.isMock()) {
//                    if (conditionAmount.contains(input.getOrderType())) {
//                        BigDecimal share = input.getApplyAmount().divide(estimatedPrice, 2, RoundingMode.DOWN);
//                        input.setApplyShare(share);
//                    }
//                } else {
//                    if (conditionShare.contains(input.getOrderType())) {
//                        input.setApplyShare(holdingShare);
//                    }
//                    if (conditionAmount.contains(input.getOrderType())) {
//                        BigDecimal share = input.getApplyAmount().divide(estimatedPrice, 2, RoundingMode.DOWN);
//                        input.setApplyShare(share);
//                    }
//                }
            });
        });
        orderList.forEach(input -> etfOrderMapper.updateApplyShare(input.getId(), input.getApplyShare()));

    }

    private void completionPftShareAmountAndToDB(List<EtfOrderPO> orderList) {
        // Query all pft account asset
        RpcMessage<List<PivotPftAccountResDTO>> pivotPftAccountListRpc = pivotPftRemoteService.getPftAccountAssets();
        List<PivotPftAccountResDTO> pivotPftAccountList = pivotPftAccountListRpc.getContent();
        log.info("completionPftShareAmountAndToDB, pivotPftAccountList {} ", pivotPftAccountList);

        String unHandleProductCode = "cash";
        // Read all none cash data
        pivotPftAccountList = pivotPftAccountList.stream().filter((PivotPftAccountResDTO p) -> !unHandleProductCode.equals(p.getProductCode())).collect(Collectors.toList());
        log.info("completionPftShareAmountAndToDB, pivotPftAccountList {} ", pivotPftAccountList);
        List<String> productIdList = Lists.transform(pivotPftAccountList, input -> input.getProductCode());
        log.info("completionPftShareAmountAndToDB, productIdList {} ", productIdList);
        Map<String, BigDecimal> dailyClosingPriceMapByProductId = getDailyClosingPriceMap(productIdList);
        log.info("completionPftShareAmountAndToDB, dailyClosingPriceMapByProductId {} ", dailyClosingPriceMapByProductId);
        List<EtfOrderPO> etfOrderPOList = Lists.newArrayList();
        // Loop and insert PFT records [wait merge] into etf order
        for (PivotPftAccountResDTO pivotPftAccountResDTO : pivotPftAccountList) {
            //To get holding
            PivotPftHoldingResDTO pivotPftHoldingResDTO = new PivotPftHoldingResDTO();
            pivotPftHoldingResDTO.setProductCode(pivotPftAccountResDTO.getProductCode());
            pivotPftHoldingResDTO.setStatus(PftHoldingStatusEnum.HOLDING);
            RpcMessage<List<PivotPftHoldingResDTO>> RpcPftHolding =  pivotPftRemoteService.getListOfPftHolding(pivotPftHoldingResDTO);
            List<PivotPftHoldingResDTO> pivotPftHolding = RpcPftHolding.getContent();
            BigDecimal totalHolding = BigDecimal.ZERO;
            
            for(PivotPftHoldingResDTO pivotPftHoldingDTO:pivotPftHolding){
                totalHolding = totalHolding.add(pivotPftHoldingDTO.getShare());     
            }
            BigDecimal availableShare = pivotPftAccountResDTO.getShare().subtract(totalHolding);
            
            if(availableShare.compareTo(BigDecimal.ZERO) <= 0){
                continue;
            }
            
            
            BigDecimal amount = pivotPftAccountResDTO.getShare().multiply(dailyClosingPriceMapByProductId.get(pivotPftAccountResDTO.getProductCode())).setScale(6, BigDecimal.ROUND_DOWN);
            etfOrderPOList.add(new EtfOrderPO()
                    .setId(Sequence.next())
                    .setAccountId(0L)
                    .setProductCode(pivotPftAccountResDTO.getProductCode())
                    .setOrderType(EtfOrderTypeEnum.PFT)
                    .setApplyShare(pivotPftAccountResDTO.getShare())
                    .setApplyAmount(amount)
                    .setOrderStatus(EtfOrderStatusEnum.WAIT_MERGE)
                    .setApplyTime(DateUtils.now())
            );
        }

        //匹配存在conditionOtherOrder的订单或是>=1 都进行处理
        List<String> productCodeList = orderList.stream().map(EtfOrderPO::getProductCode).collect(Collectors.toList());
        etfOrderPOList = etfOrderPOList.stream().filter((EtfOrderPO e)
                -> productCodeList.contains(e.getProductCode())
                || e.getApplyShare().compareTo(BigDecimal.ONE) >= 0
        ).collect(Collectors.toList());

        //orderList.addAll(etfOrderPOList);
        
        //Added by WooiTatt --> Due to PDBC Min Sell Amount is USD50. 
        List<EtfOrderPO> etfOrderPOListFilterAmt = Lists.newArrayList();
        Map<String, SaxoMinOrderPO> mapSaxoMinOrder= getMapSaxoMinOrder();       
        for(EtfOrderPO etfOrder : etfOrderPOList){
            //if(etfOrder.getProductCode().equalsIgnoreCase("PDBC")){
            if(mapSaxoMinOrder.containsKey(etfOrder.getProductCode()) 
                    && !productCodeList.contains(etfOrder.getProductCode())){ //if product code IN saxo min order condition & Not in order list
                log.info("Condition Min 50USD & NOT in order list " + etfOrder);
                //if(!productCodeList.contains(etfOrder.getProductCode())){
                    //if(etfOrder.getApplyAmount().compareTo(new BigDecimal("50")) >=0){
                    SaxoMinOrderPO saxoMinOrderPO = mapSaxoMinOrder.get(etfOrder.getProductCode());
                    BigDecimal applyShare = etfOrder.getApplyShare().setScale(0, RoundingMode.DOWN);
                    BigDecimal applyAmount = applyShare.multiply(dailyClosingPriceMapByProductId.get(saxoMinOrderPO.getProductCode())).setScale(6, BigDecimal.ROUND_DOWN);
                    log.info("Apply Share >> " + applyShare +"Apply Amount >>"+applyAmount);
                    if(applyAmount.compareTo(saxoMinOrderPO.getMinAmount()) >=0){
                        etfOrderPOListFilterAmt.add(etfOrder);
                    }
                    //}
                //}else{//if order list exist PDBC, then it will no to check on Min sell.
                //    etfOrderPOListFilterAmt.add(etfOrder);
                //}                  
            }else{ //if product code NOT IN saxo min order condition OR in order list
                etfOrderPOListFilterAmt.add(etfOrder);
            }
        }
        log.info("etfOrderPOListFilterAmt{}",etfOrderPOListFilterAmt);
        orderList.addAll(etfOrderPOListFilterAmt);
        etfOrderPOListFilterAmt.forEach(input -> etfOrderMapper.save(input));
        //etfOrderPOList.forEach(input -> etfOrderMapper.save(input));

    }

    private Map<String, BigDecimal> getDailyClosingPriceMap(List<String> productIdList) {
        Set<String> etfCodeList = Sets.newHashSet(productIdList);
        Map<String, BigDecimal> dailyClosingPriceMap = Maps.newHashMap();
        for (String etfCode : etfCodeList) {
            DailyClosingPricePO dailyClosingPricePO = dailyClosingPriceMapper.getLastPrice(etfCode);
            dailyClosingPriceMap.put(etfCode, dailyClosingPricePO.getPrice());
        }
        return dailyClosingPriceMap;
    }
    
    private Map<String, SaxoMinOrderPO> getMapSaxoMinOrder() {
        
        Map<String, SaxoMinOrderPO> mapSaxoMinOrder = Maps.newHashMap();
        List<SaxoMinOrderPO> listSaxoMinOrder = saxoMinOrderMapper.getListofSaxoMinOrder();
        for ( SaxoMinOrderPO saxoMinOrderPO: listSaxoMinOrder) {
            mapSaxoMinOrder.put(saxoMinOrderPO.getProductCode(), saxoMinOrderPO);
        }
        return mapSaxoMinOrder;
    }

}