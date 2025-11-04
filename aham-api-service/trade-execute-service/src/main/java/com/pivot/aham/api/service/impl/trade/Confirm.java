package com.pivot.aham.api.service.impl.trade;

import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.OrderConfirmationResDTO;
import com.pivot.aham.api.server.remoteservice.AhamTradingRemoteService;
import com.pivot.aham.api.service.impl.SaxoMockUtil;
import com.pivot.aham.api.service.client.saxo.SaxoClient;
import com.pivot.aham.api.service.client.saxo.resp.OrderActivitiesResp;
import com.pivot.aham.api.service.client.saxo.resp.PositionDetailResp;
import com.pivot.aham.api.service.mapper.AhamOrderConfirmationMapper;
import com.pivot.aham.api.service.mapper.EtfMergeOrderMapper;
import com.pivot.aham.api.service.mapper.SaxoOrderActivityMapper;
import com.pivot.aham.api.service.mapper.SaxoOrderMapper;
import com.pivot.aham.api.service.mapper.model.AhamOrderConfirmationPO;
import com.pivot.aham.api.service.mapper.model.EtfMergeOrderPO;
import com.pivot.aham.api.service.mapper.model.SaxoOrderActivityPO;
import com.pivot.aham.api.service.mapper.model.SaxoOrderPO;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.enums.EtfMergeOrderStatusEnum;
import com.pivot.aham.common.enums.SaxoOrderStatusEnum;
import com.pivot.aham.common.enums.SaxoOrderTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

/**
 * @program: aham
 * @description:
 * @author: zhang7
 * @create: 2019-07-02 15:37
 *
 */
@Component
@Slf4j
public class Confirm {

    @Autowired
    private EtfMergeOrderMapper etfMergeOrderMapper;

    @Autowired
    private SaxoOrderMapper saxoOrderMapper;

    @Autowired
    private SaxoOrderActivityMapper saxoOrderActivityMapper;
    
    @Autowired
    private AhamOrderConfirmationMapper ahamOrderConfirmationMapper;

    @Resource
    private SaxoMockUtil saxoMockUtil;
    
    @Resource
    private AhamTradingRemoteService ahamTradingRemoteService;

    public void tradeConfirmSellOrBuy() {
        List<SaxoOrderPO> saxoOrderSellList = saxoOrderMapper.getOrderList(SaxoOrderTypeEnum.SELL, SaxoOrderStatusEnum.TRADING_SUCCESS);
        if (CollectionUtils.isNotEmpty(saxoOrderSellList)) {
            tradeConfirmSell();
        }
        List<SaxoOrderPO> saxoOrderBuyList = saxoOrderMapper.getOrderList(SaxoOrderTypeEnum.BUY, SaxoOrderStatusEnum.TRADING_SUCCESS);
        if (CollectionUtils.isNotEmpty(saxoOrderBuyList)) {
            tradeConfirmBuy();
        }
    }

    public void tradeConfirmBuy() {
        this.tradeConfirm(SaxoOrderTypeEnum.BUY);
    }

    public void tradeConfirmSell() {
        this.tradeConfirm(SaxoOrderTypeEnum.SELL);
    }

    public void tradeConfirm(SaxoOrderTypeEnum orderType) {
        this.tradeConfirm(orderType, false);
    }

    public void tradeRebalancingConfirmBuy() {
        this.tradeConfirm(SaxoOrderTypeEnum.BUY, true);
    }

    private void tradeConfirm(SaxoOrderTypeEnum orderType, Boolean isTestRebalancing) {

        try {
            List<SaxoOrderPO> saxoOrderList = saxoOrderMapper.getOrderList(orderType, SaxoOrderStatusEnum.TRADING_SUCCESS);
            if (!CollectionUtils.isEmpty(saxoOrderList)) {
                for (SaxoOrderPO saxoOrder : saxoOrderList) {
                    try {
                        log.info("saxoMockUtil.isMock() {} ", saxoMockUtil.isMock());
                        
                        List <OrderConfirmationResDTO> listOrderConfirmationResDTO = ahamTradingRemoteService.queryOrderConfirmation();
                        
                        //insert into ahamConfirmationtable
                       
                        //List<AhamOrderConfirmationPO> listAhamOrderConfirmPO = Lists.newArrayList();
                        for(OrderConfirmationResDTO orderConfirmationResDTO : listOrderConfirmationResDTO ){
                            AhamOrderConfirmationPO ahamOrderConfirmationPO = new AhamOrderConfirmationPO();
                            //BeanUtils.copyProperties(orderConfirmationResDTO, ahamOrderConfirmationPO);
                            BeanUtils.copyProperties(ahamOrderConfirmationPO, orderConfirmationResDTO);
                            ahamOrderConfirmationMapper.save(ahamOrderConfirmationPO);
                            
                            Date now = DateUtils.now();
                            saxoOrderMapper.confirmOrder(saxoOrder.getId(), ahamOrderConfirmationPO.getUnits().intValue(), ahamOrderConfirmationPO.getAmount(), now, BigDecimal.ZERO,
                            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "", SaxoOrderStatusEnum.FINISH);
                        
                            EtfMergeOrderPO mergeOrder = etfMergeOrderMapper.getBySaxoOrderId(saxoOrder.getId());
                            
                            if (orderType == SaxoOrderTypeEnum.SELL) {
                                // Get back value before updated
                                BigDecimal totalBuyShare = mergeOrder.getTotalBuyShare(); // B
                                BigDecimal transactionCost = BigDecimal.ZERO;
                                //if (totalBuyShare.compareTo(BigDecimal.ZERO) > 0) {
                                //    transactionCost = cost;
                                //}
                                BigDecimal crossedShare = mergeOrder.getTotalBuyAmount().subtract(transactionCost).divide(orderConfirmationResDTO.getNav(), 2, BigDecimal.ROUND_DOWN).setScale(2, BigDecimal.ROUND_DOWN);; // TotalBuyAmount - Cost / Sell Price
                                log.info("mergeOrder.getTotalBuyAmount() {} , transactionCost {} , price {} ", mergeOrder.getTotalBuyAmount(), transactionCost, orderConfirmationResDTO.getNav());
                                // Overwrite the value and update
                                totalBuyShare = crossedShare;
                                BigDecimal totalSellShare = crossedShare.add(orderConfirmationResDTO.getUnits());
                                etfMergeOrderMapper.tradeConfirm(mergeOrder.getId(), EtfMergeOrderStatusEnum.WAIT_DEMERGE, BigDecimal.ZERO,
                                        DateUtils.now(), orderConfirmationResDTO.getAmount(), orderConfirmationResDTO.getUnits(), totalSellShare, totalBuyShare, crossedShare);
                            } else {
                                BigDecimal totalBuyAmount = mergeOrder.getTotalBuyAmount();
                                BigDecimal totalSellAmount = mergeOrder.getTotalSellAmount();
                                BigDecimal totalAmountWithCost = totalBuyAmount.subtract(totalSellAmount).subtract(BigDecimal.ZERO);
                                BigDecimal netBuyShare = totalAmountWithCost.divide(orderConfirmationResDTO.getNav(), 2, BigDecimal.ROUND_DOWN);
                                String strNetBuyShare = "" + netBuyShare;
                                Integer intNetBuyShare = 0;
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

                                BigDecimal totalSellShare = mergeOrder.getTotalSellShare(); // A
                                BigDecimal totalBuyShare = mergeOrder.getTotalBuyShare(); // B
                                log.info("mergeOrder.getTotalSellAmount(), totalSellShare {} , totalBuyShare {} ", totalSellShare, totalBuyShare);

                                BigDecimal transactionCost = BigDecimal.ZERO;
                                //if (totalSellShare.compareTo(BigDecimal.ZERO) > 0) {
                                //    transactionCost = cost;
                                //}
                                BigDecimal crossedShare = BigDecimal.ZERO;
                                BigDecimal crossedShare1 = mergeOrder.getTotalSellAmount().subtract(transactionCost).divide(orderConfirmationResDTO.getNav(), 2, BigDecimal.ROUND_DOWN).setScale(2, BigDecimal.ROUND_DOWN);; // TotalSellAmount - Cost / Buy Price
                                log.info("mergeOrder.getTotalSellAmount() {} , transactionCost {} , price {} ", mergeOrder.getTotalSellAmount(), transactionCost, orderConfirmationResDTO.getNav());
                                BigDecimal crossedShare2 = totalSellShare;

                                log.info("mergeOrder.getTotalSellAmount(), crossedShare1 {} , crossedShare2 {} , netBuyShare {} ", crossedShare1, crossedShare2, netBuyShare);
                                log.info("mergeOrder.getTotalSellAmount(), totalBuyAmount {} , price {} ", totalBuyAmount, orderConfirmationResDTO.getNav());

                                if (crossedShare2.add(netBuyShare).multiply(orderConfirmationResDTO.getNav()).add(BigDecimal.ZERO).compareTo(totalBuyAmount) < 0) {
                                    crossedShare = crossedShare2;
                                } else {
                                    crossedShare = crossedShare1;
                                }

                                totalSellShare = crossedShare;
                                totalBuyShare = crossedShare.add(orderConfirmationResDTO.getUnits());
                                log.info("mergeOrder.getTotalSellAmount(), totalSellShare {} , totalBuyShare {} ", totalSellShare, totalBuyShare);

                                etfMergeOrderMapper.tradeConfirm(mergeOrder.getId(), EtfMergeOrderStatusEnum.WAIT_DEMERGE, BigDecimal.ZERO,
                                        DateUtils.now(), orderConfirmationResDTO.getNav(), orderConfirmationResDTO.getUnits(), totalSellShare, totalBuyShare, crossedShare);

                            }
                            
                        }
                        //**Update SAXO Order
                        //**Update merge ordder
                        //**Condition on crossshare.
                        
                       /* if (saxoMockUtil.isMock()) {

                            Date now = DateUtils.now();

                            BigDecimal confirmAmount;
                            EtfMergeOrderPO etfMergeOrder = etfMergeOrderMapper.getBySaxoOrderId(saxoOrder.getId());
                            String productCode = etfMergeOrder.getProductCode().toUpperCase();
                            BigDecimal price = BigDecimal.ZERO;
                            BigDecimal cost = BigDecimal.ZERO;
                            BigDecimal confirmShare = BigDecimal.ZERO;
                            BigDecimal applyAmount = etfMergeOrder.getApplyAmount();

                            Integer assumptionShare = 0;
                            if (orderType == SaxoOrderTypeEnum.BUY) {
                                if (isTestRebalancing) {
                                    price = getRebalancingBuyPrice(productCode);
                                    assumptionShare = getMockBuyShareAmount(productCode);
                                } else {
                                    price = getMockBuyPrice(productCode);
                                    assumptionShare = saxoOrder.getApplyShare();
                                }

                                log.info("Buy applyAmount {} price {} ", applyAmount, price);
                                // Let assume the apply share = confirm share 

                                confirmShare = new BigDecimal(assumptionShare);
                                cost = this.getMockEstimatedBuyCost(confirmShare.intValue()).setScale(6, BigDecimal.ROUND_DOWN);
                                log.info("Buy Price {} , etf code {} , confirmShare {}  ", price, productCode, confirmShare);
                                confirmAmount = confirmShare.multiply(price).add(cost);
//                                confirmAmount = new BigDecimal(confirmShare).multiply(new BigDecimal("77.05")).add(cost);
                            } else {
                                price = getMockSellPrice(productCode);
                                log.info("Sell applyAmount {} price {} ", applyAmount, price);
                                assumptionShare = saxoOrder.getApplyShare();
                                confirmShare = new BigDecimal(assumptionShare);
                                cost = this.getMockEstimatedSellCost(confirmShare.intValue()).setScale(6, BigDecimal.ROUND_DOWN);
                                log.info("Sell Price {} , etf code {} , confirmShare {}  ", price, productCode, confirmShare);
                                confirmAmount = confirmShare.multiply(price).subtract(cost);
//                                confirmAmount = new BigDecimal(confirmShare).multiply(new BigDecimal("77.05")).subtract(cost);
                            }
                            saxoOrderMapper.confirmOrder(saxoOrder.getId(), confirmShare.intValue(), confirmAmount, now, cost,
                                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                                    "", SaxoOrderStatusEnum.FINISH);
                            EtfMergeOrderPO mergeOrder = etfMergeOrderMapper.getBySaxoOrderId(saxoOrder.getId());

                            if (orderType == SaxoOrderTypeEnum.SELL) {
                                // Get back value before updated
                                BigDecimal totalSellShare = mergeOrder.getTotalSellShare(); // A
                                BigDecimal totalBuyShare = mergeOrder.getTotalBuyShare(); // B
                                BigDecimal totalTransactionShare = totalSellShare.add(totalBuyShare); // A + B
                                BigDecimal transactionCost = BigDecimal.ZERO;
                                if (totalBuyShare.compareTo(BigDecimal.ZERO) > 0) {
                                    transactionCost = getMockEstimatedSellCost(totalBuyShare.divide(totalTransactionShare, 2, BigDecimal.ROUND_DOWN)).setScale(2, BigDecimal.ROUND_DOWN); // C = A / (A + B) * Cost
                                }
                                BigDecimal crossedShare = mergeOrder.getTotalBuyAmount().subtract(transactionCost).divide(price, 2, BigDecimal.ROUND_DOWN).setScale(2, BigDecimal.ROUND_DOWN);; // TotalBuyAmount - Cost / Sell Price
                                log.info("mergeOrder.getTotalBuyAmount() {} , transactionCost {} , price {} ", mergeOrder.getTotalBuyAmount(), transactionCost, price);
                                // Overwrite the value and update
                                totalBuyShare = crossedShare;
                                totalSellShare = crossedShare.add(confirmShare);
                                etfMergeOrderMapper.tradeConfirm(mergeOrder.getId(), EtfMergeOrderStatusEnum.WAIT_DEMERGE, cost,
                                        DateUtils.now(), confirmAmount, confirmShare, totalSellShare, totalBuyShare, crossedShare);
                            } else {
                                BigDecimal totalBuyAmount = mergeOrder.getTotalBuyAmount();
                                BigDecimal totalSellAmount = mergeOrder.getTotalSellAmount();
                                BigDecimal totalAmountWithCost = totalBuyAmount.subtract(totalSellAmount).subtract(new BigDecimal(3.99));
                                BigDecimal netBuyShare = totalAmountWithCost.divide(price, 2, BigDecimal.ROUND_DOWN);
                                String strNetBuyShare = "" + netBuyShare;
                                Integer intNetBuyShare = 0;
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

                                BigDecimal totalSellShare = mergeOrder.getTotalSellShare(); // A
                                BigDecimal totalBuyShare = mergeOrder.getTotalBuyShare(); // B
                                log.info("mergeOrder.getTotalSellAmount(), totalSellShare {} , totalBuyShare {} ", totalSellShare, totalBuyShare);

                                BigDecimal totalTransactionShare = totalSellShare.add(totalBuyShare); // A + B
                                BigDecimal transactionCost = BigDecimal.ZERO;
                                if (totalSellShare.compareTo(BigDecimal.ZERO) > 0) {
                                    transactionCost = getMockEstimatedBuyCost(totalSellShare.divide(totalTransactionShare, 2, BigDecimal.ROUND_DOWN)).setScale(2, BigDecimal.ROUND_DOWN);; // C = A / (A + B) * Cost
                                }
                                BigDecimal crossedShare = BigDecimal.ZERO;
                                BigDecimal crossedShare1 = mergeOrder.getTotalSellAmount().subtract(transactionCost).divide(price, 2, BigDecimal.ROUND_DOWN).setScale(2, BigDecimal.ROUND_DOWN);; // TotalSellAmount - Cost / Buy Price
                                log.info("mergeOrder.getTotalSellAmount() {} , transactionCost {} , price {} ", mergeOrder.getTotalSellAmount(), transactionCost, price);
                                BigDecimal crossedShare2 = totalSellShare;

                                log.info("mergeOrder.getTotalSellAmount(), crossedShare1 {} , crossedShare2 {} , netBuyShare {} ", crossedShare1, crossedShare2, netBuyShare);
                                log.info("mergeOrder.getTotalSellAmount(), totalBuyAmount {} , price {} ", totalBuyAmount, price);

                                if (crossedShare2.add(netBuyShare).multiply(price).add(new BigDecimal(3.99)).compareTo(totalBuyAmount) < 0) {
                                    crossedShare = crossedShare2;
                                } else {
                                    crossedShare = crossedShare1;
                                }

                                totalSellShare = crossedShare;
                                totalBuyShare = crossedShare.add(confirmShare);
                                log.info("mergeOrder.getTotalSellAmount(), totalSellShare {} , totalBuyShare {} ", totalSellShare, totalBuyShare);

                                etfMergeOrderMapper.tradeConfirm(mergeOrder.getId(), EtfMergeOrderStatusEnum.WAIT_DEMERGE, cost,
                                        DateUtils.now(), confirmAmount, confirmShare, totalSellShare, totalBuyShare, crossedShare);

                            }

                        } else {
                            List<String> orderLogStatus = Lists.newArrayList(OrderActivitiesResp.OrderLogStatus.Fill, OrderActivitiesResp.OrderLogStatus.FinalFill);
                            OrderActivitiesResp activitiesResp = SaxoClient.queryOrderActivities(saxoOrder.getSaxoOrderCode(), orderLogStatus);

                            List<SaxoOrderActivityPO> fillActivityList = Lists.newArrayList();
                            for (OrderActivitiesResp.ActivityData activityData : activitiesResp.getData()) {
                                SaxoOrderActivityPO orderActivity = SaxoOrderActivityPO.convert(activityData);

                                if (orderActivity.isFillActivity()) {
                                    fillActivityList.add(orderActivity);
                                }

                                saxoOrderActivityMapper.save(orderActivity);
                            }

                            Integer intConfirmShare = 0;
                            BigDecimal confirmShare = BigDecimal.ZERO;
                            BigDecimal confirmAmount = BigDecimal.ZERO;
                            BigDecimal commission = BigDecimal.ZERO;
                            BigDecimal exchangeFee = BigDecimal.ZERO;
                            BigDecimal externalCharges = BigDecimal.ZERO;
                            BigDecimal performanceFee = BigDecimal.ZERO;
                            BigDecimal stampDuty = BigDecimal.ZERO;
                            BigDecimal price = BigDecimal.ZERO;

                            for (SaxoOrderActivityPO fillActivity : fillActivityList) {
                                intConfirmShare = intConfirmShare + fillActivity.getFillAmount();
                                confirmAmount = confirmAmount.add(fillActivity.getExecutionPrice().multiply(new BigDecimal(fillActivity.getFillAmount())));
                                // Get the price 
                                if (fillActivity.getStatus().equalsIgnoreCase("finalfill") && fillActivity.getSubStatus().equalsIgnoreCase("confirmed")) {
                                    price = fillActivity.getAveragePrice();
                                }
                            }

                            confirmShare = new BigDecimal(intConfirmShare);

                            String positionId = "";
                            if (fillActivityList.size() > 0) {
                                if (fillActivityList.stream().map(SaxoOrderActivityPO::getPositionId).distinct().count() != 1) {
                                    // TODO: 2019-04-18 error
                                }

                                positionId = fillActivityList.get(0).getPositionId();
                                PositionDetailResp positionDetailResp = SaxoClient.queryPositionDetail(positionId);

                                PositionDetailResp.PositionDetails.CostData costData = positionDetailResp.getPositionDetails().getCloseCost();
                                if (costData != null && costData.getCommission().compareTo(BigDecimal.ZERO) != 0) {
                                    costData = positionDetailResp.getPositionDetails().getCloseCost();
                                } else {
                                    costData = positionDetailResp.getPositionDetails().getOpenCost();
                                }

                                commission = costData.getCommission();
                                exchangeFee = costData.getExchangeFee();
                                externalCharges = costData.getExternalCharges();
                                performanceFee = costData.getPerformanceFee();
                                stampDuty = costData.getStampDuty();
                            }

                            Date now = DateUtils.now();
                            BigDecimal cost;
                            if (orderType == SaxoOrderTypeEnum.BUY) {
                                cost = commission;
                                confirmAmount = confirmAmount.add(cost);
                            } else {
                                cost = commission.add(exchangeFee);
                                confirmAmount = confirmAmount.subtract(cost);
                            }

                            saxoOrderMapper.confirmOrder(saxoOrder.getId(), intConfirmShare, confirmAmount, now, commission,
                                    exchangeFee, externalCharges, performanceFee, stampDuty, positionId, SaxoOrderStatusEnum.FINISH);

                            EtfMergeOrderPO mergeOrder = etfMergeOrderMapper.getBySaxoOrderId(saxoOrder.getId());

                            if (orderType == SaxoOrderTypeEnum.SELL) {
                                // Get back value before updated
                                BigDecimal totalBuyShare = mergeOrder.getTotalBuyShare(); // B
                                BigDecimal transactionCost = BigDecimal.ZERO;
                                if (totalBuyShare.compareTo(BigDecimal.ZERO) > 0) {
                                    transactionCost = cost;
                                }
                                BigDecimal crossedShare = mergeOrder.getTotalBuyAmount().subtract(transactionCost).divide(price, 2, BigDecimal.ROUND_DOWN).setScale(2, BigDecimal.ROUND_DOWN);; // TotalBuyAmount - Cost / Sell Price
                                log.info("mergeOrder.getTotalBuyAmount() {} , transactionCost {} , price {} ", mergeOrder.getTotalBuyAmount(), transactionCost, price);
                                // Overwrite the value and update
                                totalBuyShare = crossedShare;
                                BigDecimal totalSellShare = crossedShare.add(confirmShare);
                                etfMergeOrderMapper.tradeConfirm(mergeOrder.getId(), EtfMergeOrderStatusEnum.WAIT_DEMERGE, cost,
                                        DateUtils.now(), confirmAmount, confirmShare, totalSellShare, totalBuyShare, crossedShare);
                            } else {
                                BigDecimal totalBuyAmount = mergeOrder.getTotalBuyAmount();
                                BigDecimal totalSellAmount = mergeOrder.getTotalSellAmount();
                                BigDecimal totalAmountWithCost = totalBuyAmount.subtract(totalSellAmount).subtract(cost);
                                BigDecimal netBuyShare = totalAmountWithCost.divide(price, 2, BigDecimal.ROUND_DOWN);
                                String strNetBuyShare = "" + netBuyShare;
                                Integer intNetBuyShare = 0;
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

                                BigDecimal totalSellShare = mergeOrder.getTotalSellShare(); // A
                                BigDecimal totalBuyShare = mergeOrder.getTotalBuyShare(); // B
                                log.info("mergeOrder.getTotalSellAmount(), totalSellShare {} , totalBuyShare {} ", totalSellShare, totalBuyShare);

                                BigDecimal transactionCost = BigDecimal.ZERO;
                                if (totalSellShare.compareTo(BigDecimal.ZERO) > 0) {
                                    transactionCost = cost;
                                }
                                BigDecimal crossedShare = BigDecimal.ZERO;
                                BigDecimal crossedShare1 = mergeOrder.getTotalSellAmount().subtract(transactionCost).divide(price, 2, BigDecimal.ROUND_DOWN).setScale(2, BigDecimal.ROUND_DOWN);; // TotalSellAmount - Cost / Buy Price
                                log.info("mergeOrder.getTotalSellAmount() {} , transactionCost {} , price {} ", mergeOrder.getTotalSellAmount(), transactionCost, price);
                                BigDecimal crossedShare2 = totalSellShare;

                                log.info("mergeOrder.getTotalSellAmount(), crossedShare1 {} , crossedShare2 {} , netBuyShare {} ", crossedShare1, crossedShare2, netBuyShare);
                                log.info("mergeOrder.getTotalSellAmount(), totalBuyAmount {} , price {} ", totalBuyAmount, price);

                                if (crossedShare2.add(netBuyShare).multiply(price).add(cost).compareTo(totalBuyAmount) < 0) {
                                    crossedShare = crossedShare2;
                                } else {
                                    crossedShare = crossedShare1;
                                }

                                totalSellShare = crossedShare;
                                totalBuyShare = crossedShare.add(confirmShare);
                                log.info("mergeOrder.getTotalSellAmount(), totalSellShare {} , totalBuyShare {} ", totalSellShare, totalBuyShare);

                                etfMergeOrderMapper.tradeConfirm(mergeOrder.getId(), EtfMergeOrderStatusEnum.WAIT_DEMERGE, cost,
                                        DateUtils.now(), confirmAmount, confirmShare, totalSellShare, totalBuyShare, crossedShare);

                            }
                        }*/
                    } catch (Exception e) {
                        ErrorLogAndMailUtil.logErrorForTrade(log, e);
                    }
                }
            }
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
    }

    private BigDecimal getEstimatedCost(Integer fillShare) {
        return (new BigDecimal(fillShare).multiply(new BigDecimal(0.007))).max(new BigDecimal(3.99));
    }

    private BigDecimal getMockEstimatedBuyCost(Integer fillShare) {
        return (new BigDecimal(fillShare).multiply(new BigDecimal(0.007))).max(new BigDecimal(3.99));
    }

    private BigDecimal getMockEstimatedSellCost(Integer fillShare) {
        return (new BigDecimal(fillShare).multiply(new BigDecimal(0.007))).max(new BigDecimal(4.1));
    }

    private BigDecimal getMockEstimatedBuyCost(BigDecimal fillShare) {
        return (fillShare.multiply(new BigDecimal(3.99)));
    }

    private BigDecimal getMockEstimatedSellCost(BigDecimal fillShare) {
        return (fillShare.multiply(new BigDecimal(4.1)));
    }

    private Integer getMockBuyShareAmount(String productCode) {
        HashMap<String, Integer> shareMap = new HashMap();
        shareMap.put("BWX", 2946);
        shareMap.put("GLD", 80);
        shareMap.put("IEF", 291);
        shareMap.put("LQD", 7);
        shareMap.put("MUB", 2);
        shareMap.put("SHV", 372);
        shareMap.put("UUP", 709);
        shareMap.put("VCIT", 77);
        Integer share = shareMap.get(productCode.toUpperCase());
        shareMap.clear();
        return share;
    }

    public BigDecimal getMockSellPrice(String productCode) {
        HashMap<String, BigDecimal> sellPriceMap = new HashMap();

        sellPriceMap.put("VT", new BigDecimal(57.61));
        sellPriceMap.put("EEM", new BigDecimal(31.62));
        sellPriceMap.put("BNDX", new BigDecimal(56.21));
        sellPriceMap.put("SHV", new BigDecimal(110.89));
        sellPriceMap.put("EMB", new BigDecimal(95.44));
        sellPriceMap.put("VWOB", new BigDecimal(69.02));
        sellPriceMap.put("BWX", new BigDecimal(28.43));
        sellPriceMap.put("HYG", new BigDecimal(75.65));
        sellPriceMap.put("JNK", new BigDecimal(93.46));
        sellPriceMap.put("MUB", new BigDecimal(109.99));
        sellPriceMap.put("LQD", new BigDecimal(121.3));
        sellPriceMap.put("VCIT", new BigDecimal(88.07));
        sellPriceMap.put("FLOT", new BigDecimal(46.63));
        sellPriceMap.put("IEF", new BigDecimal(120.45));
        sellPriceMap.put("UUP", new BigDecimal(26.58));
        sellPriceMap.put("PDBC", new BigDecimal(11.95));
        sellPriceMap.put("GLD", new BigDecimal(141.65));
        sellPriceMap.put("VNQ", new BigDecimal(65.39));
        sellPriceMap.put("VEA", new BigDecimal(29.75));
        sellPriceMap.put("VPL", new BigDecimal(48.78));
        sellPriceMap.put("EWA", new BigDecimal(14.48));
        sellPriceMap.put("SPY", new BigDecimal(239.85));
        sellPriceMap.put("VOO", new BigDecimal(218.57));
        sellPriceMap.put("VTI", new BigDecimal(120.47));
        sellPriceMap.put("VGK", new BigDecimal(38.49));
        sellPriceMap.put("EWJ", new BigDecimal(43.24));
        sellPriceMap.put("QQQ", new BigDecimal(169.32));
        sellPriceMap.put("EWS", new BigDecimal(17.01));
        sellPriceMap.put("EWZ", new BigDecimal(22.38));
        sellPriceMap.put("ASHR", new BigDecimal(25.47));
        sellPriceMap.put("VWO", new BigDecimal(31.56));
        sellPriceMap.put("ILF", new BigDecimal(18.02));
        sellPriceMap.put("RSX", new BigDecimal(14.99));
        sellPriceMap.put("AAXJ", new BigDecimal(55.40));

        BigDecimal price = sellPriceMap.get(productCode.toUpperCase());
        sellPriceMap.clear();
        return price;
    }

    public BigDecimal getMockBuyPrice(String productCode) {
        HashMap<String, BigDecimal> buyPriceMap = new HashMap();

        buyPriceMap.put("VT", new BigDecimal(57.61));
        buyPriceMap.put("EEM", new BigDecimal(31.62));
        buyPriceMap.put("BNDX", new BigDecimal(56.21));
        buyPriceMap.put("SHV", new BigDecimal(110.89));
        buyPriceMap.put("EMB", new BigDecimal(95.44));
        buyPriceMap.put("VWOB", new BigDecimal(69.02));
        buyPriceMap.put("BWX", new BigDecimal(28.43));
        buyPriceMap.put("HYG", new BigDecimal(75.65));
        buyPriceMap.put("JNK", new BigDecimal(93.46));
        buyPriceMap.put("MUB", new BigDecimal(109.99));
        buyPriceMap.put("LQD", new BigDecimal(121.3));
        buyPriceMap.put("VCIT", new BigDecimal(88.07));
        buyPriceMap.put("FLOT", new BigDecimal(46.63));
        buyPriceMap.put("IEF", new BigDecimal(120.45));
        buyPriceMap.put("UUP", new BigDecimal(26.58));
        buyPriceMap.put("PDBC", new BigDecimal(11.95));
        buyPriceMap.put("GLD", new BigDecimal(141.65));
        buyPriceMap.put("VNQ", new BigDecimal(65.39));
        buyPriceMap.put("VEA", new BigDecimal(29.75));
        buyPriceMap.put("VPL", new BigDecimal(48.78));
        buyPriceMap.put("EWA", new BigDecimal(14.48));
        buyPriceMap.put("SPY", new BigDecimal(239.85));
        buyPriceMap.put("VOO", new BigDecimal(218.57));
        buyPriceMap.put("VTI", new BigDecimal(120.47));
        buyPriceMap.put("VGK", new BigDecimal(38.49));
        buyPriceMap.put("EWJ", new BigDecimal(43.24));
        buyPriceMap.put("QQQ", new BigDecimal(169.32));
        buyPriceMap.put("EWS", new BigDecimal(17.01));
        buyPriceMap.put("EWZ", new BigDecimal(22.38));
        buyPriceMap.put("ASHR", new BigDecimal(25.47));
        buyPriceMap.put("VWO", new BigDecimal(31.56));
        buyPriceMap.put("ILF", new BigDecimal(18.02));
        buyPriceMap.put("RSX", new BigDecimal(14.99));
        buyPriceMap.put("AAXJ", new BigDecimal(55.40));

        BigDecimal price = buyPriceMap.get(productCode.toUpperCase());
        buyPriceMap.clear();
        return price;
    }

    public BigDecimal getRebalancingBuyPrice(String productCode) {
        HashMap<String, BigDecimal> buyPriceMap = new HashMap();
        buyPriceMap.put("VT", new BigDecimal(65.2));
        buyPriceMap.put("EEM", new BigDecimal(36.14));
        buyPriceMap.put("BNDX", new BigDecimal(56.7));
        buyPriceMap.put("SHV", new BigDecimal(110.88));
        buyPriceMap.put("EMB", new BigDecimal(100.06));
        buyPriceMap.put("VWOB", new BigDecimal(71.92));
        buyPriceMap.put("BWX", new BigDecimal(28.43));
        buyPriceMap.put("HYG", new BigDecimal(80.05));
        buyPriceMap.put("JNK", new BigDecimal(99.18));
        buyPriceMap.put("MUB", new BigDecimal(111.51));
        buyPriceMap.put("LQD", new BigDecimal(121.31));
        buyPriceMap.put("VCIT", new BigDecimal(88.09));
        buyPriceMap.put("FLOT", new BigDecimal(48.19));
        buyPriceMap.put("IEF", new BigDecimal(120.45));
        buyPriceMap.put("UUP", new BigDecimal(26.56));
        buyPriceMap.put("PDBC", new BigDecimal(12.51));
        buyPriceMap.put("GLD", new BigDecimal(141.64));
        buyPriceMap.put("VNQ", new BigDecimal(79.48));
        buyPriceMap.put("VEA", new BigDecimal(33.36));
        buyPriceMap.put("VPL", new BigDecimal(53.83));
        buyPriceMap.put("EWA", new BigDecimal(17.26));
        buyPriceMap.put("SPY", new BigDecimal(269.32));
        buyPriceMap.put("VOO", new BigDecimal(247.64));
        buyPriceMap.put("VTI", new BigDecimal(135.93));
        buyPriceMap.put("VGK", new BigDecimal(43.64));
        buyPriceMap.put("EWJ", new BigDecimal(46.27));
        buyPriceMap.put("QQQ", new BigDecimal(192.34));
        buyPriceMap.put("EWS", new BigDecimal(18.85));
        buyPriceMap.put("EWZ", new BigDecimal(29.1));
        buyPriceMap.put("ASHR", new BigDecimal(28.62));
        buyPriceMap.put("VWO", new BigDecimal(35.9));
        buyPriceMap.put("ILF", new BigDecimal(21.96));
        buyPriceMap.put("RSX", new BigDecimal(17.34));
        buyPriceMap.put("AAXJ", new BigDecimal(62.52));

        BigDecimal price = buyPriceMap.get(productCode.toUpperCase());
        buyPriceMap.clear();
        return price;
    }

    private int getUp(BigDecimal b) {
        return Double.valueOf(Math.ceil(b.doubleValue())).intValue();
    }

    private int getDown(BigDecimal b) {
        return Double.valueOf(Math.floor(b.doubleValue())).intValue();
    }

}
