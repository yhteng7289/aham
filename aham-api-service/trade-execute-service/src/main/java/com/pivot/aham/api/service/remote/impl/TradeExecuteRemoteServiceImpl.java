package com.pivot.aham.api.service.remote.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pivot.aham.api.server.dto.req.OrderDetailReq;
import com.pivot.aham.api.server.dto.req.OrderDetailReq.OrderDetailR;
import com.pivot.aham.api.server.dto.resp.OrderDetailRes;
import com.pivot.aham.api.server.remoteservice.TPCFRemoteService;
import com.pivot.aham.api.server.remoteservice.TradeExecuteRemoteService;
import com.pivot.aham.api.service.mapper.EtfInfoMapper;
import com.pivot.aham.api.service.mapper.EtfMergeOrderMapper;
import com.pivot.aham.api.service.mapper.SaxoOrderMapper;
import com.pivot.aham.api.service.mapper.model.EtfInfoPO;
import com.pivot.aham.api.service.mapper.model.EtfMergeOrderPO;
import com.pivot.aham.api.service.mapper.model.SaxoOrderPO;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.enums.EtfMergeOrderStatusEnum;
import com.pivot.aham.common.enums.SaxoOrderStatusEnum;
import com.pivot.aham.common.enums.SaxoOrderTypeEnum;
import com.alibaba.dubbo.common.json.JSONObject;
import com.pivot.aham.api.service.impl.trade.Demerge;
import com.pivot.aham.api.service.impl.trade.MergeOrder;
import com.pivot.aham.api.service.impl.trade.Recalculate;
import com.pivot.aham.api.service.impl.trade.Trade;
import com.pivot.aham.api.service.mapper.TradeOrderTypeMapper;
import com.pivot.aham.api.service.mapper.model.TradeOrderTypePO;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.enums.EtfmergeOrderTypeEnum;

import lombok.extern.slf4j.Slf4j;

@Service(interfaceClass = TradeExecuteRemoteService.class)
@Slf4j
public class TradeExecuteRemoteServiceImpl implements TradeExecuteRemoteService{
	
    private static final Logger log = LoggerFactory.getLogger(TradeExecuteRemoteServiceImpl.class);

    @Resource
    private SaxoOrderMapper saxoOrderMapper;
    @Resource
    private EtfInfoMapper etfInfoMapper;
    @Resource
    private EtfMergeOrderMapper etfMergeOrderMapper;
    @Resource
    private TradeOrderTypeMapper tradeOrderTypeMapper;
    @Resource
    private Demerge demerge;
    @Resource
    private Recalculate recalculate;
    @Resource
    private MergeOrder mergeOrder;
    @Resource
    private Trade trade;

    @Override
    public RpcMessage<List<String>> findOrderNumber() {
            List<String> result = new ArrayList<String>();
            SaxoOrderPO saxoOrderQuery = new SaxoOrderPO();
            saxoOrderQuery.setOrderStatus(SaxoOrderStatusEnum.TRADING_SUCCESS);
            List<SaxoOrderPO> saxoOrderList = saxoOrderMapper.findSaxoOrder(saxoOrderQuery);
            log.info("==saxoOrderList.size():{}", saxoOrderList.size());
            for(SaxoOrderPO saxoOrder : saxoOrderList) {
                    String orderNumber = String.valueOf(saxoOrder.getSaxoOrderCode());
                    if(!result.contains(orderNumber)) {
                            result.add(orderNumber);
            }
            }
            return RpcMessage.success(result);
    }

    @Override
    public RpcMessage<List<OrderDetailRes>> findOrderDetail(String orderNumber){
            List<OrderDetailRes> result = new ArrayList<OrderDetailRes>();
            log.info("===findOrderDetail.orderNumber = :{}", orderNumber);
            SaxoOrderPO saxoOrderQuery = new SaxoOrderPO();
            saxoOrderQuery.setSaxoOrderCode(orderNumber);
            List<SaxoOrderPO> saxoOrderList = saxoOrderMapper.findSaxoOrder(saxoOrderQuery);
            log.info("===saxoOrderList.size():{}", saxoOrderList.size());
            List<EtfInfoPO> etfInfoList = etfInfoMapper.getAllEtf();
            log.info("===etfInfoList.size():{}", etfInfoList.size());
            for(SaxoOrderPO saxoOrder : saxoOrderList) {
                    List<EtfInfoPO> etfInfo = etfInfoList.stream().filter(p -> p.getUic().equals(saxoOrder.getUic())).collect(Collectors.toList());

                    OrderDetailRes orderDetail = new OrderDetailRes();
                    orderDetail.setId(saxoOrder.getId());
                    orderDetail.setApplyShare(BigDecimal.valueOf(saxoOrder.getApplyShare()));
                    orderDetail.setApplyTime(saxoOrder.getApplyTime());
                    orderDetail.setEtfCode(etfInfo.get(0).getEtfCode());
                    if(saxoOrder.getOrderType().equals(SaxoOrderTypeEnum.BUY)) {
                            orderDetail.setOrderTypeAhamDesc("SA");
                    }else {
                            orderDetail.setOrderTypeAhamDesc("RD");
                    }
                    orderDetail.setSaxoOrderCode(saxoOrder.getSaxoOrderCode());
                    result.add(orderDetail);
            }
            return RpcMessage.success(result);
    }

    @Override
    public RpcMessage<String> submitOrderDetail(JSONArray orderDetailList) {
        try {
            log.info("====AAAAA====");
            Date now = DateUtils.now();
            Long lastId = Long.valueOf("0");
            for (int i = 0; i < orderDetailList.size(); i++) {
                HashMap<String, String> order = (HashMap<String, String>) orderDetailList.get(i);
                Long id = Long.valueOf(order.get("id"));
                String orderTypeAhamDesc = order.get("orderTypeAhamDesc");
                BigDecimal confirmShare = BigDecimal.valueOf(Double.valueOf(order.get("confirmShare")));
                BigDecimal confirmAmount = BigDecimal.valueOf(Double.valueOf(order.get("confirmAmount")));
                BigDecimal navPrice = confirmAmount.divide(confirmShare, 6, BigDecimal.ROUND_DOWN);
                lastId = id;
                log.info("===id:{}===orderTypeAhamDesc:{}===confirmShare:{}===confirmAmount:{}===navPrice:{}", id, orderTypeAhamDesc, confirmShare, confirmAmount, navPrice);

                log.info("===navPrice:{}===saxoOrder ID:{}", navPrice, id);

                saxoOrderMapper.confirmOrder(id, confirmShare.intValue(), confirmAmount, now, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "", SaxoOrderStatusEnum.FINISH);
                log.info("===updated to FINISH STATUS");
                EtfMergeOrderPO mergeOrder = etfMergeOrderMapper.getBySaxoOrderId(id);

                    if (orderTypeAhamDesc.equalsIgnoreCase("RD")) {
                        BigDecimal totalBuyShare = mergeOrder.getTotalBuyShare();
                        BigDecimal transactionCost = BigDecimal.ZERO;
                        BigDecimal crossedShare = mergeOrder.getTotalBuyAmount().subtract(transactionCost).divide(navPrice, 2, BigDecimal.ROUND_DOWN).setScale(2, BigDecimal.ROUND_DOWN);; // TotalBuyAmount - Cost / Sell Price
                        log.info("mergeOrder.getTotalBuyAmount() {} , transactionCost {} , price {} ", mergeOrder.getTotalBuyAmount(), transactionCost, navPrice);
                        // Overwrite the value and update
                        totalBuyShare = crossedShare;
                        BigDecimal totalSellShare = crossedShare.add(confirmShare);
                        etfMergeOrderMapper.tradeConfirm(mergeOrder.getId(), EtfMergeOrderStatusEnum.WAIT_DEMERGE, BigDecimal.ZERO,
                                DateUtils.now(), confirmAmount, confirmShare, totalSellShare, totalBuyShare, crossedShare);
                    } else {
                        BigDecimal totalBuyAmount = mergeOrder.getTotalBuyAmount();
                        BigDecimal totalSellAmount = mergeOrder.getTotalSellAmount();
                        BigDecimal totalAmountWithCost = totalBuyAmount.subtract(totalSellAmount).subtract(BigDecimal.ZERO);
                        BigDecimal netBuyShare = totalAmountWithCost.divide(navPrice, 2, BigDecimal.ROUND_DOWN);
                        String strNetBuyShare = netBuyShare+"";
                        Integer intNetBuyShare = 0;
                        log.info("totalBuyAmount:{} , totalSellAmount:{} , totalAmountWithCost:{}, netBuyShare:{}, strNetBuyShare:{}, intNetBuyShare:{}", totalBuyAmount, totalSellAmount, totalAmountWithCost, netBuyShare, strNetBuyShare, intNetBuyShare);
                        if (strNetBuyShare.contains(".")) {
                            try {
                                String decimalPoint = "0." + strNetBuyShare.split("\\.")[1];
                                Double decimalDouble = Double.valueOf(decimalPoint);
                                if (decimalDouble >= 0.5) {
                                    intNetBuyShare = getUp(netBuyShare);
                                } else {
                                    intNetBuyShare = getDown(netBuyShare);
                                }

                            } catch (NumberFormatException e) {
                                intNetBuyShare = getUp(netBuyShare);
                            }
                        }

                        netBuyShare = new BigDecimal(intNetBuyShare);

                        log.info("mergeOrder.getTotalSellAmount(), totalBuyAmount {} , totalSellAmount {} ", totalBuyAmount, totalSellAmount);
                        log.info("mergeOrder.getTotalSellAmount(), totalAmountWithCost {} , totalAmountWithCost {} ", totalAmountWithCost, totalAmountWithCost);

                        BigDecimal totalSellShare = mergeOrder.getTotalSellShare();
                        BigDecimal totalBuyShare = mergeOrder.getTotalBuyShare();
                        log.info("mergeOrder.getTotalSellAmount(), totalSellShare {} , totalBuyShare {} ", totalSellShare, totalBuyShare);

                        BigDecimal transactionCost = BigDecimal.ZERO;

                        BigDecimal crossedShare = BigDecimal.ZERO;
                        BigDecimal crossedShare1 = mergeOrder.getTotalSellAmount().subtract(transactionCost).divide(navPrice, 2, BigDecimal.ROUND_DOWN).setScale(2, BigDecimal.ROUND_DOWN);; // TotalSellAmount - Cost / Buy Price
                        log.info("mergeOrder.getTotalSellAmount() {} , transactionCost {} , price {} ", mergeOrder.getTotalSellAmount(), transactionCost, navPrice);
                        BigDecimal crossedShare2 = totalSellShare;

                        log.info("mergeOrder.getTotalSellAmount(), crossedShare1 {} , crossedShare2 {} , netBuyShare {} ", crossedShare1, crossedShare2, netBuyShare);
                        log.info("mergeOrder.getTotalSellAmount(), totalBuyAmount {} , price {} ", totalBuyAmount, navPrice);

                        if (crossedShare2.add(netBuyShare).multiply(navPrice).add(BigDecimal.ZERO).compareTo(totalBuyAmount) < 0) {
                            crossedShare = crossedShare2;
                        } else {
                            crossedShare = crossedShare1;
                        }

                        totalSellShare = crossedShare;
                        totalBuyShare = crossedShare.add(confirmShare);
                        log.info("mergeOrder.getTotalSellAmount(), totalSellShare {} , totalBuyShare {} ", totalSellShare, totalBuyShare);

                        //etfMergeOrderMapper.tradeConfirm(mergeOrder.getId(), EtfMergeOrderStatusEnum.WAIT_DEMERGE, BigDecimal.ZERO, DateUtils.now(), navPrice, confirmShare, totalSellShare, totalBuyShare, crossedShare);
                        etfMergeOrderMapper.tradeConfirm(mergeOrder.getId(), EtfMergeOrderStatusEnum.WAIT_DEMERGE, BigDecimal.ZERO, DateUtils.now(), confirmAmount, confirmShare, totalSellShare, totalBuyShare, crossedShare);
                    }
            }

            SaxoOrderPO saxoOrderPO = saxoOrderMapper.getById(lastId);
            if(saxoOrderPO !=null){
                TradeOrderTypePO tradeOrderTypePO = new TradeOrderTypePO();
                tradeOrderTypePO.setSaxoOrderCode(saxoOrderPO.getSaxoOrderCode());
                tradeOrderTypePO = tradeOrderTypeMapper.getTradeOrderTypePOBySaxoCode(tradeOrderTypePO);
                if(tradeOrderTypePO.getIsRebalanceOrder().equalsIgnoreCase("N")){
                    demerge._demergeOrderSellOrBuy();
                    recalculate.recalculate();
                    mergeOrder.mergeEtfOrderForOrderType(true, true);
                    Long orderId = Sequence.next();
                    trade.buyTrade90(orderId);
                }else{
                    demerge.demergeOrder(EtfmergeOrderTypeEnum.BUY);
                }
            }
            
            return RpcMessage.success("SUCCESS");
        }catch (Exception e) {
        ErrorLogAndMailUtil.logError(log, e);
    }
            // TODO Auto-generated method stub
            return RpcMessage.error("FAILED");
    }

    private int getUp(BigDecimal b) {
        log.info("==getUp:{}", b);
        log.info("==Double.valueOf(Math.ceil(b.doubleValue())).intValue()", Double.valueOf(Math.ceil(b.doubleValue())).intValue());
        return Double.valueOf(Math.ceil(b.doubleValue())).intValue();
    }

    private int getDown(BigDecimal b) {
    	log.info("==getDown:{}", b);
        log.info("==Double.valueOf(Math.floor(b.doubleValue())).intValue()", Double.valueOf(Math.floor(b.doubleValue())).intValue());
        return Double.valueOf(Math.floor(b.doubleValue())).intValue();
    }
}
