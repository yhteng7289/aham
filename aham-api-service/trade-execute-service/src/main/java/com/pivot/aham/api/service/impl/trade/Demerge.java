package com.pivot.aham.api.service.impl.trade;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.pivot.aham.api.server.dto.req.AccountEtfAssetReqDTO;
import com.pivot.aham.api.server.dto.res.AccountEtfAssetResDTO;
import com.pivot.aham.api.server.remoteservice.AssetServiceRemoteService;
import com.pivot.aham.api.service.mapper.*;
import com.pivot.aham.api.service.mapper.model.*;
import com.pivot.aham.common.enums.*;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.support.cache.RedissonHelper;
import com.pivot.aham.common.core.support.email.Email;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.EmailUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;
import com.pivot.aham.common.enums.analysis.PftAssetSourceEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;

/**
 * @program: aham
 * @description:
 * @author: zhang7
 * @create: 2019-07-02 16:01
 *
 */
@Component
@Slf4j
public class Demerge {

    @Resource
    private RedissonHelper redissonHelper;

    @Autowired
    private EtfOrderMapper etfOrderMapper;

    @Autowired
    private EtfMergeOrderMapper etfMergeOrderMapper;

    @Autowired
    private EtfMergeOrderExtendMapper etfMergeOrderExtendMapper;

    @Autowired
    private AssetServiceRemoteService assetServiceRemoteService;

    @Autowired
    private EtfMergeOrderPftMapper etfMergeOrderPftMapper;

    @Autowired
    private DailyClosingPriceMapper dailyClosingPriceMapper;

    static final String NOTICE_TO_ADD = PropertiesUtil.getString("pivot.error.alert.email");

    public void _demergeOrderSellOrBuy() {
        List<EtfMergeOrderPO> mergeOrderList = etfMergeOrderMapper.getMergeOrder(
                Lists.newArrayList(EtfmergeOrderTypeEnum.SELL, EtfmergeOrderTypeEnum.BUY, EtfmergeOrderTypeEnum.DO_NOTHING),
                EtfMergeOrderStatusEnum.WAIT_DEMERGE);

//        //sell 和 doNothing 使用同一个拆分逻辑
//        List<EtfMergeOrderPO> etfOrderListSell = mergeOrderList.stream().filter((EtfMergeOrderPO e)
//                -> Lists.newArrayList(EtfMergeOrderTypeEnum.SELL, EtfMergeOrderTypeEnum.DO_NOTHING).contains(e.getOrderType())).collect(Collectors.toList());
//
//        List<EtfMergeOrderPO> etfOrderListBuy = mergeOrderList.stream().filter((EtfMergeOrderPO e)
//                -> EtfMergeOrderTypeEnum.BUY == e.getOrderType()).collect(Collectors.toList());
//        log.info("mergeOrderList size {} ", mergeOrderList.size());
        for (EtfMergeOrderPO etfMergeOrderPO : mergeOrderList) {

            // For Nothing , their Confirm Share = 0.00
            if (etfMergeOrderPO.getConfirmShare().doubleValue() > 0.00) {
                BigDecimal totalSellShare = etfMergeOrderPO.getTotalSellShare();
                BigDecimal totalBuyShare = etfMergeOrderPO.getTotalBuyShare();
                BigDecimal price = calculateAvgPriceFromOrderType(etfMergeOrderPO);
                log.info("_demergeOrderSellOrBuy , product_code {} , totalSellShare {} , totalBuyShare {} , price {} ", etfMergeOrderPO.getProductCode(), totalSellShare, totalBuyShare, price);
                if (totalSellShare.compareTo(totalBuyShare) > 0) {
                    handleSellBiggerBuy(etfMergeOrderPO, totalSellShare, totalBuyShare, price);
                } else {
                    handleBuyBiggerSell(etfMergeOrderPO, totalSellShare, totalBuyShare, price);
                }

                etfMergeOrderMapper.updateStatusAndSetPrice(etfMergeOrderPO.getId(), EtfMergeOrderStatusEnum.FINISH, price);
                demergeForOrderType(etfMergeOrderPO.getId());
            } else if (etfMergeOrderPO.getConfirmShare().doubleValue() <= 0.00) {
                log.info("_demergeOrderSellOrBuy , product_code {} - No handle it. Confirm Share = 0 ", etfMergeOrderPO.getProductCode());
                etfMergeOrderMapper.updateStatusAndSetPrice(etfMergeOrderPO.getId(), EtfMergeOrderStatusEnum.FINISH, BigDecimal.ZERO);
                demergeForOrderType(etfMergeOrderPO.getId());
                List<EtfOrderPO> etfOrderList = etfOrderMapper.getListByMergeOrderId(etfMergeOrderPO.getId());
                for (EtfOrderPO etfOrderPO : etfOrderList) {
                    etfOrderMapper.confirm(etfOrderPO.getId(), EtfOrderStatusEnum.WAIT_NOTIFY, BigDecimal.ZERO, DateUtils.now(), BigDecimal.ZERO, BigDecimal.ZERO, "distributeBuy_DoNothing");
                }
            } else {
                log.error("_demergeOrderSellOrBuy etfMergeOrderPO.getConfirmShare() error {} ", etfMergeOrderPO);
            }

        }
    }

    public void handleSellBiggerBuy(EtfMergeOrderPO etfMergeOrderPO, BigDecimal totalSellShare, BigDecimal totalBuyShare, BigDecimal price) {
        BigDecimal totalTransactShare = totalSellShare.add(totalBuyShare);
        //BigDecimal transactionCost = new BigDecimal(4.1);
        BigDecimal transactionCost = etfMergeOrderPO.getCostFee(); //Edit By WooiTatt
        final List<EtfOrderPO> etfOrderList = etfOrderMapper.getListByMergeOrderId(etfMergeOrderPO.getId());
        distributeSell_HandleSellBiggerBuy(totalTransactShare, transactionCost, price, etfOrderList, etfMergeOrderPO, totalSellShare, totalBuyShare);
        distributeBuy_HandleSellBiggerBuy(totalTransactShare, transactionCost, price, etfOrderList, etfMergeOrderPO, totalSellShare, totalBuyShare);

        //ptf数据直接更新完成
//        List<Long> ftpEtfOrderListId = etfOrderList.stream()
//                .filter((EtfOrderPO e) -> e.getOrderType() == EtfOrderTypeEnum.PFT)
//                .map(EtfOrderPO::getId).collect(Collectors.toList());
//        if (CollectionUtils.isNotEmpty(ftpEtfOrderListId)) {
//            etfOrderMapper.updateStatus(ftpEtfOrderListId, EtfOrderStatusEnum.FINISH);
//        }
//
//        //新增pft数据
//        List<EtfOrderExtendPO> etfEtfOrderExtendPOList = etfOrderUpdateMap.get(EtfOrderTypeEnum.PFT);
//        if (CollectionUtils.isNotEmpty(etfEtfOrderExtendPOList)) {
//            etfEtfOrderExtendPOList.forEach(etfOrderExtendPO -> {
//                etfMergeOrderPftMapper.save(new EtfMergePftOrderPO()
//                        .setMergeOrderId(order.getId())
//                        .setSyncStatus(SyncStatus.INIT)
//                        .setProductCode(etfOrderExtendPO.getProductCode())
//                        .setAmount(etfOrderExtendPO.getConfirmAmount())
//                        .setShare(etfOrderExtendPO.getConfirmShare())
//                        .setCost(etfOrderExtendPO.getCostFee())
//                        .setTradeType(etfOrderExtendPO.getTradeType())
//                        .setSourceType(etfOrderExtendPO.getSourceEnum())
//                        .setCreateTime(now)
//                );
//            });
//        }
//        //更新etfMergeOrder数据
//        etfMergeOrderMapper.updateStatusAndSetPrice(order.getId(), EtfMergeOrderStatusEnum.FINISH, avgPrice);
//        demergeForOrderType(order.getId());
    }

    public void handleBuyBiggerSell(EtfMergeOrderPO etfMergeOrderPO, BigDecimal totalSellShare, BigDecimal totalBuyShare, BigDecimal price) {
        BigDecimal totalTransactShare = totalSellShare.add(totalBuyShare);
        //BigDecimal transactionCost = new BigDecimal(3.99);
        BigDecimal transactionCost = etfMergeOrderPO.getCostFee(); //Edit By WooiTatt
        final List<EtfOrderPO> etfOrderList = etfOrderMapper.getListByMergeOrderId(etfMergeOrderPO.getId());
        distributeSell_HandleBuyBiggerSell(totalTransactShare, transactionCost, price, etfOrderList, etfMergeOrderPO, totalSellShare, totalBuyShare);
        distributeBuy_HandleBuyBiggerSell(totalTransactShare, transactionCost, price, etfOrderList, etfMergeOrderPO, totalSellShare, totalBuyShare);
    }

    private void distributeSell_HandleSellBiggerBuy(BigDecimal totalTransactShare, BigDecimal transactionCost, BigDecimal price, List<EtfOrderPO> etfOrderList,
            EtfMergeOrderPO etfMergeOrderPO, BigDecimal totalSellShare, BigDecimal totalBuyShare) {
        try {
            BigDecimal totalConfirmAmount = BigDecimal.ZERO;
            BigDecimal _totalSellShare = BigDecimal.ZERO;

            for (EtfOrderPO etfOrderPO : etfOrderList) {
                if (etfOrderPO.getOrderType() == EtfOrderTypeEnum.GSP || etfOrderPO.getOrderType() == EtfOrderTypeEnum.RSA
                        || etfOrderPO.getOrderType() == EtfOrderTypeEnum.GSA) {
                    BigDecimal totalShares = new BigDecimal(0).subtract(etfOrderPO.getApplyShare()); // Make it to become negative value
                    Double doubleShareWithCost = transactionCost.doubleValue() * totalShares.doubleValue() / totalTransactShare.doubleValue();
                    //BigDecimal shareWithCost = BigDecimal.valueOf(doubleShareWithCost).setScale(2, BigDecimal.ROUND_DOWN);//round(doubleShareWithCost);
                    BigDecimal shareWithCost = round(doubleShareWithCost);
                    BigDecimal confirmAmount = totalShares.multiply(new BigDecimal(-1)).multiply(price).add(shareWithCost);
                    // Sum up all the sell share amount only
                    totalConfirmAmount = totalConfirmAmount.add(confirmAmount).setScale(2, BigDecimal.ROUND_DOWN);
                    _totalSellShare = _totalSellShare.add(totalShares);
                    log.info("distributeSell_HandleSellBiggerBuy, totalShares {} , shareWithCost {} , confirmAmount {} ,  totalConfirmAmount {} ", totalShares, shareWithCost, confirmAmount, totalConfirmAmount);
                    etfOrderMapper.confirm(etfOrderPO.getId(), EtfOrderStatusEnum.WAIT_NOTIFY, shareWithCost.abs(), DateUtils.now(), totalShares.abs(), confirmAmount.abs().setScale(2, BigDecimal.ROUND_DOWN), "distributeSell_HandleSellBiggerBuy");
                }
            }

            // The total
            BigDecimal rs = etfMergeOrderPO.getTotalSellShare().add(_totalSellShare);
            log.info("distributeSell_HandleSellBiggerBuy, totalConfirmAmount {} ", totalConfirmAmount);
            log.info("distributeSell_HandleSellBiggerBuy, rs {} ", rs);
            // Get All sum amount rebalancing sell and PFT
            BigDecimal totalSellAmount = BigDecimal.ZERO;
            boolean isHasPFT = false;
            //List<EtfOrderPO> etfOrderListSelected = Lists.newArrayList();
            for (EtfOrderPO etfOrderPO : etfOrderList) {
                if (etfOrderPO.getOrderType() == EtfOrderTypeEnum.RSP || etfOrderPO.getOrderType() == EtfOrderTypeEnum.PFT) {
                    BigDecimal applyAmountByEtfOrder = BigDecimal.ZERO.subtract(etfOrderPO.getTmpApplyAmount());
                    totalSellAmount = totalSellAmount.add(applyAmountByEtfOrder);
                    //Added by WooiTatt
                    //etfOrderListSelected.add(etfOrderPO);
                    if (etfOrderPO.getOrderType() == EtfOrderTypeEnum.PFT) {
                        isHasPFT = true;
                    }
                }
            }
            log.info("distributeSell_HandleSellBiggerBuy, totalSellAmount {}  ", totalSellAmount);

            BigDecimal _rs = rs;

            log.info("distributeSell_HandleSellBiggerBuy etfOrderList size {} ", etfOrderList.size());
            log.info("distributeSell_HandleSellBiggerBuy, etfOrderList {} ", etfOrderList);

            for (EtfOrderPO etfOrderPO : etfOrderList) {
                //for (EtfOrderPO etfOrderPO : etfOrderListSelected) {
                log.info("distributeSell_HandleSellBiggerBuy, etfOrderPO.getProductCode() {} , etfOrderPO.getOrderType() {} ", etfOrderPO.getProductCode(), etfOrderPO.getOrderType());
                // Handling rebalancing sell;
                log.info("distributeSell_HandleSellBiggerBuy, etfOrderPO.getOrderType() == EtfOrderTypeEnum.RSP , {} ", etfOrderPO.getOrderType() == EtfOrderTypeEnum.RSP);
                if (etfOrderPO.getOrderType() == EtfOrderTypeEnum.RSP) {
                    log.info("Why no Running here 1 ?");
                    BigDecimal sellAmount = BigDecimal.ZERO.subtract(etfOrderPO.getTmpApplyAmount());
                    log.info("distributeSell_HandleSellBiggerBuy, rs {} , sellAmount{} , totalSellAmount {} ", rs, sellAmount, totalSellAmount);
                    Double doubleSharedSd = rs.doubleValue() * sellAmount.doubleValue() / totalSellAmount.doubleValue();
                    BigDecimal sharedSd = round(doubleSharedSd);

                    //BigDecimal sharedSd = BigDecimal.ZERO;
                    //sharedSd = new BigDecimal(doubleSharedSd).setScale(2, RoundingMode.UP);
                    /*if(isHasPFT){
                        sharedSd = new BigDecimal(doubleSharedSd).setScale(2, RoundingMode.UP);
                    }else{
                        //sharedSd = round(doubleSharedSd);
                        sharedSd = new BigDecimal(doubleSharedSd).setScale(2, RoundingMode.UP);
                        if(etfOrderListSelected.indexOf(etfOrderPO) == etfOrderListSelected.size()-1){
                            sharedSd = _rs;
                        }
                    }*/
                    log.info("distributeSell_HandleSellBiggerBuy, confirmRebalanceSellShare {} ", sharedSd);
                    log.info("distributeSell_HandleSellBiggerBuy, totalTransactShare {} ", totalTransactShare);
                    Double doubleShareWithCost = transactionCost.doubleValue() * sharedSd.doubleValue() / totalTransactShare.doubleValue();
                    //BigDecimal shareWithCost = BigDecimal.valueOf(doubleShareWithCost).setScale(2, BigDecimal.ROUND_DOWN);//round(doubleShareWithCost);
                    BigDecimal shareWithCost = round(doubleShareWithCost);
                    BigDecimal confirmRebalanceSellAmount = sharedSd.multiply(price).subtract(shareWithCost);
                    //_rs = _rs.add(sharedSd);
                    log.info("distributeSell_HandleSellBiggerBuy, _rs {}", _rs);
                    _rs = _rs.subtract(sharedSd);
                    etfOrderMapper.confirm(etfOrderPO.getId(), EtfOrderStatusEnum.WAIT_NOTIFY, shareWithCost.abs(), DateUtils.now(), sharedSd.abs(), confirmRebalanceSellAmount.abs().setScale(2, BigDecimal.ROUND_DOWN), "distributeSell_HandleSellBiggerBuy");
                    log.info("distributeSell_HandleSellBiggerBuy, sellAmount {} , sharedSd {} , shareWithCost {} ,  confirmRebalanceSellAmount {} ", sellAmount, sharedSd, shareWithCost, confirmRebalanceSellAmount);
                } else {
                    log.info("distributeSell_HandleSellBiggerBuy else statement PFT ? _rs {} ", _rs);
                    if (_rs.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal applyPftShare;
                        if (etfOrderPO.getOrderType() == EtfOrderTypeEnum.PFT) {
                            applyPftShare = etfOrderPO.getApplyShare();
                            if (_rs.compareTo(applyPftShare) > 0) {
                                _rs = _rs.subtract(applyPftShare);
                                totalBuyShare = totalBuyShare.subtract(_rs);
                                log.info("distributeSell_HandleSellBiggerBuy -->PFT _rs.compareTo(applyPftShare)  ? _rs {}, applyPftShare{}, totalBuyShare{} ", _rs, applyPftShare, totalBuyShare);
                            } else {
                                log.info("distributeSell_HandleSellBiggerBuy -->PFT else ? _rs {}, applyPftShare{} ", _rs, applyPftShare);
                                applyPftShare = _rs;
                                _rs = BigDecimal.ZERO;
                            }
                            applyPftShare = applyPftShare.multiply(new BigDecimal(-1));
                            BigDecimal pftShareWithCost = transactionCost.multiply(applyPftShare).divide(totalTransactShare, 2, BigDecimal.ROUND_DOWN);
                            BigDecimal confirmPftAmount = applyPftShare.multiply(new BigDecimal(-1)).multiply(price).add(pftShareWithCost);
                            log.info("distributeSell_HandleSellBiggerBuy, if applyPftShare {} , pftShareWithCost {} , confirmPftAmount {}  ", applyPftShare, pftShareWithCost, confirmPftAmount);
                            String key = "if_pft:distributeSell_HandleSellBiggerBuy:" + etfMergeOrderPO.getId() + ":" + etfMergeOrderPO.getProductCode();
                            String redisValue = (String) redissonHelper.get(key);
                            if (redisValue == null) {
                                etfMergeOrderPftMapper.save(new EtfMergePftOrderPO()
                                        .setMergeOrderId(etfMergeOrderPO.getId())
                                        .setSyncStatus(SyncStatus.INIT)
                                        .setProductCode(etfMergeOrderPO.getProductCode())
                                        .setAmount(confirmPftAmount.abs())
                                        .setShare(applyPftShare.abs())
                                        .setCost(pftShareWithCost.abs())
                                        .setTradeType(TradeType.SELL)
                                        .setSourceType(PftAssetSourceEnum.NORMALSELL)
                                        .setCreateTime(DateUtils.now()));
                                redissonHelper.set(key, "1", 600);
                            }

                        }
                    }
//                    else {
//                        BigDecimal applyPftShare = _rs.multiply(new BigDecimal(-1));
//                        BigDecimal pftShareWithCost = transactionCost.multiply(applyPftShare).divide(totalTransactShare, 2, BigDecimal.ROUND_DOWN);
//                        BigDecimal confirmPftAmount = applyPftShare.multiply(price).subtract(pftShareWithCost);
//                        confirmPftAmount = confirmPftAmount.multiply(new BigDecimal(-1)); // Sheet9.Cells(i, j + 14) = Round(Sharresd * Sheet8.Cells(i, 26) - CS * Sharresd / TS, 2) * -1
//                        log.info("distributeSell_HandleSellBiggerBuy, else applyPftShare {} , pftShareWithCost {} , confirmPftAmount {}  ", applyPftShare, pftShareWithCost, confirmPftAmount);
//                        String key = "else_pft:distributeSell_HandleSellBiggerBuy:" + etfMergeOrderPO.getId() + ":" + etfMergeOrderPO.getProductCode();
//                        String redisValue = (String) redissonHelper.get(key);
//                        if (redisValue == null) {
//                            etfMergeOrderPftMapper.save(new EtfMergePftOrderPO()
//                                    .setMergeOrderId(etfMergeOrderPO.getId())
//                                    .setSyncStatus(SyncStatus.INIT)
//                                    .setProductCode(etfMergeOrderPO.getProductCode())
//                                    .setAmount(confirmPftAmount.abs())
//                                    .setShare(applyPftShare.abs())
//                                    .setCost(pftShareWithCost.abs())
//                                    .setTradeType(TradeType.BUY)
//                                    .setSourceType(PftAssetSourceEnum.NORMALBUY)
//                                    .setCreateTime(DateUtils.now()));
//                            redissonHelper.set(key, "1", 600);
//                        }
//                    }
                }
            }
            //Added By WooiTatt
            if (totalBuyShare.compareTo(BigDecimal.ZERO) < 0 && etfMergeOrderPO.getOrderType() != EtfmergeOrderTypeEnum.DO_NOTHING) {
                BigDecimal applyPftShare = totalBuyShare.multiply(new BigDecimal(-1));
                BigDecimal pftShareWithCost = transactionCost.multiply(applyPftShare).divide(totalTransactShare, 2, BigDecimal.ROUND_DOWN);
                BigDecimal confirmPftAmount = applyPftShare.multiply(price).subtract(pftShareWithCost);
                confirmPftAmount = confirmPftAmount.multiply(new BigDecimal(-1)); // Sheet9.Cells(i, j + 14) = Round(Sharresd * Sheet8.Cells(i, 26) - CS * Sharresd / TS, 2) * -1
                log.info("distributeSell_HandleSellBiggerBuy, --> TotalBuySharePFT applyPftShare {} , pftShareWithCost {} , confirmPftAmount {}  ", applyPftShare, pftShareWithCost, confirmPftAmount);
                String key = "else_pft:distributeSell_HandleSellBiggerBuy_BUYBALANCE:" + etfMergeOrderPO.getId() + ":" + etfMergeOrderPO.getProductCode();
                String redisValue = (String) redissonHelper.get(key);
                if (redisValue == null) {
                    etfMergeOrderPftMapper.save(new EtfMergePftOrderPO()
                            .setMergeOrderId(etfMergeOrderPO.getId())
                            .setSyncStatus(SyncStatus.INIT)
                            .setProductCode(etfMergeOrderPO.getProductCode())
                            .setAmount(confirmPftAmount.abs())
                            .setShare(applyPftShare.abs())
                            .setCost(pftShareWithCost.abs())
                            .setTradeType(TradeType.BUY)
                            .setSourceType(PftAssetSourceEnum.NORMALBUY)
                            .setCreateTime(DateUtils.now()));
                    redissonHelper.set(key, "1", 600);
                }
            }

            if (etfMergeOrderPO.getOrderType() == EtfmergeOrderTypeEnum.DO_NOTHING) {
                BigDecimal applyPftShare = rs.subtract(totalBuyShare.abs()).add(_totalSellShare.abs());//.multiply(new BigDecimal(-1));
                BigDecimal pftShareWithCost = transactionCost.multiply(applyPftShare).divide(totalTransactShare, 2, BigDecimal.ROUND_DOWN);
                BigDecimal confirmPftAmount = applyPftShare.multiply(price).subtract(pftShareWithCost);
                //confirmPftAmount = confirmPftAmount.multiply(new BigDecimal(-1)); // Sheet9.Cells(i, j + 14) = Round(Sharresd * Sheet8.Cells(i, 26) - CS * Sharresd / TS, 2) * -1
                log.info("distributeSell_HandleSellBiggerBuy, --> DO_NOTHING BUY BACK applyPftShare {} , pftShareWithCost {} , confirmPftAmount {}  ", applyPftShare, pftShareWithCost, confirmPftAmount);
                String key = "else_pft:distributeSell_HandleSellBiggerBuy_DONOTHING:" + etfMergeOrderPO.getId() + ":" + etfMergeOrderPO.getProductCode();
                String redisValue = (String) redissonHelper.get(key);
                if (redisValue == null) {
                    etfMergeOrderPftMapper.save(new EtfMergePftOrderPO()
                            .setMergeOrderId(etfMergeOrderPO.getId())
                            .setSyncStatus(SyncStatus.INIT)
                            .setProductCode(etfMergeOrderPO.getProductCode())
                            .setAmount(confirmPftAmount.abs())
                            .setShare(applyPftShare.abs())
                            .setCost(pftShareWithCost.abs())
                            .setTradeType(TradeType.BUY)
                            .setSourceType(PftAssetSourceEnum.NORMALBUY)
                            .setCreateTime(DateUtils.now()));
                    redissonHelper.set(key, "1", 600);
                }
            }

//            if(!isHasPFT && (_rs.compareTo(BigDecimal.ZERO) <0 )){
            if (_rs.compareTo(BigDecimal.ZERO) < 0) {
                BigDecimal applyPftShare = _rs.abs();//.multiply(new BigDecimal(-1));
                BigDecimal pftShareWithCost = transactionCost.multiply(applyPftShare).divide(totalTransactShare, 2, BigDecimal.ROUND_DOWN);
                BigDecimal confirmPftAmount = applyPftShare.multiply(price).subtract(pftShareWithCost);
                //confirmPftAmount = confirmPftAmount.multiply(new BigDecimal(-1)); // Sheet9.Cells(i, j + 14) = Round(Sharresd * Sheet8.Cells(i, 26) - CS * Sharresd / TS, 2) * -1
                log.info("distributeSell_HandleSellBiggerBuy, --> DO_NOTHING BUY BACK applyPftShare {} , pftShareWithCost {} , confirmPftAmount {}  ", applyPftShare, pftShareWithCost, confirmPftAmount);
                String key = "else_pft:distributeSell_HandleSellBiggerBuy_SellOver:" + etfMergeOrderPO.getId() + ":" + etfMergeOrderPO.getProductCode();
                String redisValue = (String) redissonHelper.get(key);
                if (redisValue == null) {
                    etfMergeOrderPftMapper.save(new EtfMergePftOrderPO()
                            .setMergeOrderId(etfMergeOrderPO.getId())
                            .setSyncStatus(SyncStatus.INIT)
                            .setProductCode(etfMergeOrderPO.getProductCode())
                            .setAmount(confirmPftAmount.abs())
                            .setShare(applyPftShare.abs())
                            .setCost(pftShareWithCost.abs())
                            .setTradeType(TradeType.BUY)
                            .setSourceType(PftAssetSourceEnum.NORMALBUY)
                            .setCreateTime(DateUtils.now()));
                    redissonHelper.set(key, "1", 600);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void distributeBuy_HandleSellBiggerBuy(BigDecimal totalTransactShare, BigDecimal transactionCost, BigDecimal price, List<EtfOrderPO> etfOrderList,
            EtfMergeOrderPO etfMergeOrderPO, BigDecimal totalSellShare, BigDecimal totalBuyShare) {

        try {
            BigDecimal totalBuyAmount = BigDecimal.ZERO;
            for (EtfOrderPO etfOrderPO : etfOrderList) {
                if (etfOrderPO.getOrderType() == EtfOrderTypeEnum.GBA) {
                    BigDecimal applyAmountByEtfOrder = BigDecimal.ZERO.subtract(etfOrderPO.getTmpApplyAmount());
                    totalBuyAmount = totalBuyAmount.add(applyAmountByEtfOrder);
                }
            }

            BigDecimal rs = totalBuyShare;
            for (EtfOrderPO etfOrderPO : etfOrderList) {
                if (etfOrderPO.getOrderType() == EtfOrderTypeEnum.GBA) {
                    BigDecimal applyAmountByEtfOrder = BigDecimal.ZERO.subtract(etfOrderPO.getTmpApplyAmount());
                    Double doubleConfirmBuyShare = totalBuyAmount.doubleValue() / totalBuyAmount.doubleValue() * totalBuyShare.doubleValue();
                    BigDecimal confirmBuyShare = round(doubleConfirmBuyShare);

                    BigDecimal tmpTransactionCost = transactionCost.multiply(confirmBuyShare).divide(totalTransactShare, 2, BigDecimal.ROUND_DOWN);
                    BigDecimal tmpConfirmBuyAmount = confirmBuyShare.multiply(price).add(tmpTransactionCost);

                    BigDecimal _tmpTransactionCost = BigDecimal.ZERO;
                    log.info("distributeBuy_HandleSellBiggerBuy, tmpConfirmBuyAmount {} , applyAmountByEtfOrder {} ", tmpConfirmBuyAmount, applyAmountByEtfOrder);
                    if (tmpConfirmBuyAmount.abs().compareTo(applyAmountByEtfOrder.abs()) > 0) {
                        _tmpTransactionCost = tmpTransactionCost;
                        confirmBuyShare = applyAmountByEtfOrder.subtract(_tmpTransactionCost).divide(price, 2, BigDecimal.ROUND_DOWN);
                    }
                    BigDecimal rs1 = rs;
                    rs = rs.subtract(confirmBuyShare);
                    if (rs.compareTo(BigDecimal.ZERO) < 0) {
                        confirmBuyShare = rs1;
                    }
                    BigDecimal confirmBuyAmount = confirmBuyShare.multiply(price).add(_tmpTransactionCost).multiply(new BigDecimal(-1));
                    log.info("distributeBuy_HandleSellBiggerBuy, if tmpTransactionCost {} ,applyAmountByEtfOrder {} , confirmBuyShare {} , confirmBuyAmount {}  ", tmpTransactionCost, applyAmountByEtfOrder, confirmBuyShare, confirmBuyAmount);
                    etfOrderMapper.confirm(etfOrderPO.getId(), EtfOrderStatusEnum.WAIT_NOTIFY, _tmpTransactionCost.abs(), DateUtils.now(), confirmBuyShare.abs(), confirmBuyAmount.abs().setScale(2, BigDecimal.ROUND_DOWN), "distributeBuy_HandleSellBiggerBuy");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void distributeSell_HandleBuyBiggerSell(BigDecimal totalTransactShare, BigDecimal transactionCost, BigDecimal price, List<EtfOrderPO> etfOrderList,
            EtfMergeOrderPO etfMergeOrderPO, BigDecimal totalSellShare, BigDecimal totalBuyShare) {
        try {
            BigDecimal totalConfirmAmount = BigDecimal.ZERO;
            BigDecimal _totalSellShare = BigDecimal.ZERO;
            BigDecimal totalTradeShare = BigDecimal.ZERO;

            for (EtfOrderPO etfOrderPO : etfOrderList) {
                if (etfOrderPO.getOrderType() == EtfOrderTypeEnum.GSP || etfOrderPO.getOrderType() == EtfOrderTypeEnum.RSA
                        || etfOrderPO.getOrderType() == EtfOrderTypeEnum.GSA) {
                    BigDecimal totalShares = new BigDecimal(0).subtract(etfOrderPO.getApplyShare()); // Make it to become negative value
                    BigDecimal shareWithCost = transactionCost.multiply(totalShares).divide(totalTransactShare, 2, BigDecimal.ROUND_DOWN);
                    BigDecimal confirmAmount = totalShares.multiply(new BigDecimal(-1)).multiply(price).add(shareWithCost);
                    // Sum up all the sell share amount only
                    totalConfirmAmount = totalConfirmAmount.add(confirmAmount).setScale(2, BigDecimal.ROUND_DOWN);
                    _totalSellShare = _totalSellShare.add(totalShares);
                    log.info("distributeSell_HandleBuyBiggerSell, totalShares {} , shareWithCost {} , confirmAmount {} ,  totalConfirmAmount {} ", totalShares, shareWithCost, confirmAmount, totalConfirmAmount);
                    totalTradeShare = totalTradeShare.add(totalShares.abs());
                    etfOrderMapper.confirm(etfOrderPO.getId(), EtfOrderStatusEnum.WAIT_NOTIFY, shareWithCost.abs(), DateUtils.now(), totalShares.abs(), confirmAmount.abs().setScale(2, BigDecimal.ROUND_DOWN), "distributeSell_HandleBuyBiggerSell");
                }
            }
            // The total
            BigDecimal rs = etfMergeOrderPO.getTotalSellShare().add(_totalSellShare);
            log.info("distributeSell_HandleBuyBiggerSell, totalConfirmAmount {} ", totalConfirmAmount);
            log.info("distributeSell_HandleBuyBiggerSell, rs {} ", rs);
            // Get All sum amount rebalancing sell and PFT
            BigDecimal totalSellAmount = BigDecimal.ZERO;
            for (EtfOrderPO etfOrderPO : etfOrderList) {
                if (etfOrderPO.getOrderType() == EtfOrderTypeEnum.RSP || etfOrderPO.getOrderType() == EtfOrderTypeEnum.PFT) {
                    BigDecimal applyAmountByEtfOrder = BigDecimal.ZERO.subtract(etfOrderPO.getTmpApplyAmount());
                    totalSellAmount = totalSellAmount.add(applyAmountByEtfOrder);
                }
            }
            log.info("distributeSell_HandleBuyBiggerSell, totalSellAmount {}  ", totalSellAmount);

            BigDecimal _rs = rs;
            BigDecimal pftApplyShare = BigDecimal.ZERO;
            BigDecimal totalRspShare = BigDecimal.ZERO;
            log.info("distributeSell_HandleBuyBiggerSell etfOrderList size {} ", etfOrderList.size());
            log.info("distributeSell_HandleBuyBiggerSell, etfOrderList {} ", etfOrderList);
            for (EtfOrderPO etfOrderPO : etfOrderList) {
                log.info("distributeSell_HandleBuyBiggerSell, etfOrderPO.getProductCode() {} , etfOrderPO.getOrderType() {} ", etfOrderPO.getProductCode(), etfOrderPO.getOrderType());
                // Handling rebalancing sell;
                log.info("distributeSell_HandleBuyBiggerSell, etfOrderPO.getOrderType() == EtfOrderTypeEnum.RSP , {} ", etfOrderPO.getOrderType() == EtfOrderTypeEnum.RSP);
                if (etfOrderPO.getOrderType() == EtfOrderTypeEnum.RSP) {
                    log.info("Why no Running here 1 ?");
                    BigDecimal sellAmount = BigDecimal.ZERO.subtract(etfOrderPO.getTmpApplyAmount());
                    log.info("distributeSell_HandleBuyBiggerSell, rs {} , sellAmount{} , totalSellAmount {} ", rs, sellAmount, totalSellAmount);
                    Double doubleSharedSd = rs.doubleValue() * sellAmount.doubleValue() / totalSellAmount.doubleValue();
                    BigDecimal sharedSd = round(doubleSharedSd);
                    log.info("distributeSell_HandleBuyBiggerSell, confirmRebalanceSellShare {} ", sharedSd);
                    log.info("distributeSell_HandleBuyBiggerSell, totalTransactShare {} ", totalTransactShare);
                    BigDecimal shareWithCost = transactionCost.multiply(sharedSd).divide(totalTransactShare, 2, BigDecimal.ROUND_DOWN);
                    BigDecimal confirmRebalanceSellAmount = sharedSd.multiply(price).subtract(shareWithCost);
                    _rs = _rs.add(sharedSd);
                    etfOrderMapper.confirm(etfOrderPO.getId(), EtfOrderStatusEnum.WAIT_NOTIFY, shareWithCost.abs(), DateUtils.now(), sharedSd.abs(), confirmRebalanceSellAmount.abs().setScale(2, BigDecimal.ROUND_DOWN), "distributeSell_HandleBuyBiggerSell");
                    totalRspShare = totalRspShare.add(sharedSd);
                    log.info("distributeSell_HandleBuyBiggerSell, sellAmount {} , sharedSd {} , shareWithCost {} ,  confirmRebalanceSellAmount {} ", sellAmount, sharedSd, shareWithCost, confirmRebalanceSellAmount);
                }
                if (etfOrderPO.getOrderType() == EtfOrderTypeEnum.PFT) { //Added By WooiTatt
                    pftApplyShare = pftApplyShare.add(etfOrderPO.getApplyShare());
                }
            }

            //Edit By WooiTatt
            boolean isCrossShared = false;
            //BigDecimal pftSellShare = BigDecimal.ZERO;
            //BigDecimal pftBuyShare = BigDecimal.ZERO;
            BigDecimal actualTotalSellShare = BigDecimal.ZERO;
            actualTotalSellShare = actualTotalSellShare.add(totalTradeShare).add(totalRspShare)
                    .add(pftApplyShare).add(etfMergeOrderPO.getConfirmShare());
            log.info("distributeSell_HandleBuyBiggerSell, if actualTotalSellShare {} , pftApplyShare {} ", actualTotalSellShare, pftApplyShare);
            redissonHelper.set("HBuy_TotalSellShare_" + etfMergeOrderPO.getId().toString(), actualTotalSellShare);
            redissonHelper.set("HBuy_PftApplyShare_" + etfMergeOrderPO.getId().toString(), pftApplyShare);

            /* if(totalSellShare.compareTo(BigDecimal.ZERO) > 0 && totalBuyShare.compareTo(BigDecimal.ZERO) > 0 ){
                isCrossShared = true;
            }
            if(isCrossShared){
               actualTotalSellShare =  actualTotalSellShare.add(totalTradeShare).add(totalRspShare)
                       .add(pftApplyShare).add(etfMergeOrderPO.getConfirmShare());
               BigDecimal balanceShare = BigDecimal.ZERO;
               balanceShare = actualTotalSellShare.subtract(totalBuyShare);
               if(balanceShare.compareTo(BigDecimal.ZERO) > 0){
                   pftBuyShare = pftApplyShare.subtract(balanceShare.abs()).abs();
               }else{
                   pftSellShare = pftApplyShare;
               }
               
            }else{ //if only buy
                System.out.println("Should be not happen");
                if(etfMergeOrderPO.getConfirmShare().compareTo(totalBuyShare) > 0){
                    pftBuyShare = etfMergeOrderPO.getConfirmShare().subtract(totalBuyShare);
                }
            }
            
            System.out.println("pftSellShare >>"+ pftSellShare +"pftBuyShare >>" + pftBuyShare);    
             */
 /* if(pftSellShare.compareTo(BigDecimal.ZERO) > 0){
                BigDecimal pftShareWithCost = transactionCost.multiply(pftSellShare).divide(totalTransactShare, 2, BigDecimal.ROUND_DOWN);
                            BigDecimal confirmPftAmount = pftSellShare.multiply(new BigDecimal(-1)).multiply(price).add(pftShareWithCost);
                            log.info("distributeSell_HandleBuyBiggerSell, if applyPftShare {} , pftShareWithCost {} , confirmPftAmount {}  ", pftSellShare, pftShareWithCost, confirmPftAmount);
                            redissonHelper.set("HBuy_SellPFT", pftSellShare.abs());
                            String key = "if_pft:distributeSell_HandleBuyBiggerSell:" + etfMergeOrderPO.getId() + ":" + etfMergeOrderPO.getProductCode();
                            String redisValue = (String) redissonHelper.get(key);
                            if (redisValue == null) {
                                etfMergeOrderPftMapper.save(new EtfMergePftOrderPO()
                                        .setMergeOrderId(etfMergeOrderPO.getId())
                                        .setSyncStatus(SyncStatus.INIT)
                                        .setProductCode(etfMergeOrderPO.getProductCode())
                                        .setAmount(confirmPftAmount.abs())
                                        .setShare(pftSellShare.abs())
                                        .setCost(pftShareWithCost.abs())
                                        .setTradeType(TradeType.SELL)
                                        .setSourceType(PftAssetSourceEnum.NORMALSELL)
                                        .setCreateTime(DateUtils.now()));
                                redissonHelper.set(key, "1", 600);
                            }
            }*/
 /*if(pftBuyShare.compareTo(BigDecimal.ZERO) > 0){
                BigDecimal pftShareWithCost = transactionCost.multiply(pftBuyShare).divide(totalTransactShare, 2, BigDecimal.ROUND_DOWN);
                        BigDecimal confirmPftAmount = pftBuyShare.multiply(price).subtract(pftShareWithCost);
                        confirmPftAmount = confirmPftAmount.multiply(new BigDecimal(-1)); // Sheet9.Cells(i, j + 14) = Round(Sharresd * Sheet8.Cells(i, 26) - CS * Sharresd / TS, 2) * -1
                        log.info("distributeSell_HandleBuyBiggerSell, else applyPftShare {} , pftShareWithCost {} , confirmPftAmount {}  ", pftBuyShare, pftShareWithCost, confirmPftAmount);
                        if (pftBuyShare.abs().compareTo(BigDecimal.ZERO) > 0) {
                            redissonHelper.set("HBuy_BuyPFT", pftSellShare.abs());
                           String key = "else_pft:distributeSell_HandleBuyBiggerSell:" + etfMergeOrderPO.getId() + ":" + etfMergeOrderPO.getProductCode();
                            String redisValue = (String) redissonHelper.get(key);
                            if (redisValue == null) {
                                etfMergeOrderPftMapper.save(new EtfMergePftOrderPO()
                                        .setMergeOrderId(etfMergeOrderPO.getId())
                                        .setSyncStatus(SyncStatus.INIT)
                                        .setProductCode(etfMergeOrderPO.getProductCode())
                                        .setAmount(confirmPftAmount.abs())
                                        .setShare(pftBuyShare.abs())
                                        .setCost(pftShareWithCost.abs())
                                        .setTradeType(TradeType.BUY)
                                        .setSourceType(PftAssetSourceEnum.NORMALBUY)
                                        .setCreateTime(DateUtils.now()));
                                redissonHelper.set(key, "1", 600);
                            }
                        }
            }*/
 /*else {
                    log.info("Why no Running here 2 ?");
                    log.info("distributeSell_HandleBuyBiggerSell else statement PFT ? _rs {} ", _rs);
                    if (_rs.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal applyPftShare;
                        if (etfOrderPO.getOrderType() == EtfOrderTypeEnum.PFT) {
                            applyPftShare = etfOrderPO.getApplyShare();
                            if (_rs.compareTo(applyPftShare) > 0) {
                                _rs = _rs.subtract(applyPftShare);
                                totalBuyShare = totalBuyShare.subtract(_rs);

                            } else {
                                applyPftShare = _rs;
                                _rs = BigDecimal.ZERO;
                            }

                            Double diff = rs.doubleValue() - totalTradeShare.doubleValue();
                            if ((applyPftShare.doubleValue() - diff) > 0) {
                                applyPftShare = new BigDecimal(diff);
                            }

                            applyPftShare = applyPftShare.multiply(new BigDecimal(-1));
                            BigDecimal pftShareWithCost = transactionCost.multiply(applyPftShare).divide(totalTransactShare, 2, BigDecimal.ROUND_DOWN);
                            BigDecimal confirmPftAmount = applyPftShare.multiply(new BigDecimal(-1)).multiply(price).add(pftShareWithCost);
                            log.info("distributeSell_HandleBuyBiggerSell, if applyPftShare {} , pftShareWithCost {} , confirmPftAmount {}  ", applyPftShare, pftShareWithCost, confirmPftAmount);
                            String key = "if_pft:distributeSell_HandleBuyBiggerSell:" + etfMergeOrderPO.getId() + ":" + etfMergeOrderPO.getProductCode();
                            String redisValue = (String) redissonHelper.get(key);
                            if (redisValue == null) {
                                etfMergeOrderPftMapper.save(new EtfMergePftOrderPO()
                                        .setMergeOrderId(etfMergeOrderPO.getId())
                                        .setSyncStatus(SyncStatus.INIT)
                                        .setProductCode(etfMergeOrderPO.getProductCode())
                                        .setAmount(confirmPftAmount.abs())
                                        .setShare(applyPftShare.abs())
                                        .setCost(pftShareWithCost.abs())
                                        .setTradeType(TradeType.SELL)
                                        .setSourceType(PftAssetSourceEnum.NORMALSELL)
                                        .setCreateTime(DateUtils.now()));
                                redissonHelper.set(key, "1", 600);
                            }
                        }
                    } else {
                        Double diff = rs.doubleValue() - totalTradeShare.doubleValue();
                        if ((_rs.doubleValue() - diff) > 0) {
                            _rs = new BigDecimal(diff);
                        }
                        BigDecimal applyPftShare = _rs.multiply(new BigDecimal(-1));
                        BigDecimal pftShareWithCost = transactionCost.multiply(applyPftShare).divide(totalTransactShare, 2, BigDecimal.ROUND_DOWN);
                        BigDecimal confirmPftAmount = applyPftShare.multiply(price).subtract(pftShareWithCost);
                        confirmPftAmount = confirmPftAmount.multiply(new BigDecimal(-1)); // Sheet9.Cells(i, j + 14) = Round(Sharresd * Sheet8.Cells(i, 26) - CS * Sharresd / TS, 2) * -1
                        log.info("distributeSell_HandleBuyBiggerSell, else applyPftShare {} , pftShareWithCost {} , confirmPftAmount {}  ", applyPftShare, pftShareWithCost, confirmPftAmount);
                        if (applyPftShare.abs().compareTo(BigDecimal.ZERO) > 0) {
                            String key = "else_pft:distributeSell_HandleBuyBiggerSell:" + etfMergeOrderPO.getId() + ":" + etfMergeOrderPO.getProductCode();
                            String redisValue = (String) redissonHelper.get(key);
                            if (redisValue == null) {
                                etfMergeOrderPftMapper.save(new EtfMergePftOrderPO()
                                        .setMergeOrderId(etfMergeOrderPO.getId())
                                        .setSyncStatus(SyncStatus.INIT)
                                        .setProductCode(etfMergeOrderPO.getProductCode())
                                        .setAmount(confirmPftAmount.abs())
                                        .setShare(applyPftShare.abs())
                                        .setCost(pftShareWithCost.abs())
                                        .setTradeType(TradeType.BUY)
                                        .setSourceType(PftAssetSourceEnum.NORMALBUY)
                                        .setCreateTime(DateUtils.now()));
                                redissonHelper.set(key, "1", 600);
                            }
                        }
                    }
                }
            }end for loop*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void distributeBuy_HandleBuyBiggerSell(BigDecimal totalTransactShare, BigDecimal transactionCost, BigDecimal price, List<EtfOrderPO> etfOrderList,
            EtfMergeOrderPO etfMergeOrderPO, BigDecimal totalSellShare, BigDecimal totalBuyShare) {

        try {
            BigDecimal totalBuyAmount = BigDecimal.ZERO;
            for (EtfOrderPO etfOrderPO : etfOrderList) {
                if (etfOrderPO.getOrderType() == EtfOrderTypeEnum.GBA) {
                    BigDecimal applyAmountByEtfOrder = etfOrderPO.getTmpApplyAmount();
                    totalBuyAmount = totalBuyAmount.add(applyAmountByEtfOrder);
                }
            }
            List<EtfOrderPO> etfOrderPOList = Lists.newArrayList();
            totalBuyShare = totalBuyShare.abs();
            BigDecimal rs = totalBuyShare;
            BigDecimal totalConfirmBuyShare = BigDecimal.ZERO;
            log.info("distributeBuy_HandleBuyBiggerSell , rs {} ,  totalBuyAmount {} ", rs, totalBuyAmount);
            for (EtfOrderPO etfOrderPO : etfOrderList) {
                BigDecimal _tmpTransactionCost = BigDecimal.ZERO;
                if (etfOrderPO.getOrderType() == EtfOrderTypeEnum.GBA) {
                    BigDecimal applyAmountByEtfOrder = etfOrderPO.getTmpApplyAmount();
                    Double doubleConfirmBuyShare = applyAmountByEtfOrder.doubleValue() / totalBuyAmount.doubleValue() * totalBuyShare.doubleValue();
                    BigDecimal confirmBuyShare = round(doubleConfirmBuyShare);
                    BigDecimal tmpTransactionCost = transactionCost.multiply(confirmBuyShare).divide(totalTransactShare, 2, BigDecimal.ROUND_DOWN);
                    log.info("distributeBuy_HandleBuyBiggerSell, confirmBuyShare {} , price {} , tmpTransactionCost {} ", confirmBuyShare, price, tmpTransactionCost);
                    BigDecimal tmpConfirmBuyAmount = confirmBuyShare.multiply(price).add(tmpTransactionCost);

                    log.info("distributeBuy_HandleBuyBiggerSell, confirmBuyShare {} , tmpTransactionCost {} , tmpConfirmBuyAmount {} ", confirmBuyShare, tmpTransactionCost, tmpConfirmBuyAmount);
                    log.info("distributeBuy_HandleBuyBiggerSell, tmpConfirmBuyAmount {} , applyAmountByEtfOrder {} ", tmpConfirmBuyAmount, applyAmountByEtfOrder);
                    _tmpTransactionCost = tmpTransactionCost; //Edit WooiTatt (Move from line 653).
                    if (tmpConfirmBuyAmount.compareTo(applyAmountByEtfOrder) > 0) {
                        //_tmpTransactionCost = tmpTransactionCost;
                        Double _doubleConfirmBuyShare = (applyAmountByEtfOrder.doubleValue() - _tmpTransactionCost.doubleValue()) / price.doubleValue();
                        confirmBuyShare = round(_doubleConfirmBuyShare);
                    }
                    BigDecimal rs1 = rs;
                    log.info("distributeBuy_HandleBuyBiggerSell, rs {} , confirmBuyShare {}  ", rs, confirmBuyShare);
                    rs = rs.subtract(confirmBuyShare);
                    if (rs.compareTo(BigDecimal.ZERO) < 0) {
                        confirmBuyShare = rs1;
                    }
                    totalConfirmBuyShare = totalConfirmBuyShare.add(confirmBuyShare.abs());
                    log.info("distributeBuy_HandleBuyBiggerSell, confirmBuyShare {}  ", confirmBuyShare);
                    BigDecimal confirmBuyAmount = confirmBuyShare.multiply(price).add(_tmpTransactionCost).multiply(new BigDecimal(-1));
                    if (etfMergeOrderPO.getOrderType() == EtfmergeOrderTypeEnum.DO_NOTHING) {
                        EtfOrderPO etfOrderPOInner = new EtfOrderPO();
                        etfOrderPOInner.setId(etfOrderPO.getId());
                        etfOrderPOInner.setOrderStatus(EtfOrderStatusEnum.WAIT_NOTIFY);
                        etfOrderPOInner.setCostFee(_tmpTransactionCost.abs());
                        etfOrderPOInner.setConfirmTime(DateUtils.now());
                        etfOrderPOInner.setConfirmShare(confirmBuyShare.abs());
                        etfOrderPOInner.setConfirmAmount(confirmBuyAmount.abs().setScale(2, BigDecimal.ROUND_DOWN));
                        etfOrderPOInner.setTmpApplyAmount(etfOrderPO.getTmpApplyAmount());
                        etfOrderPOList.add(etfOrderPOInner);
                    } else {
                        etfOrderMapper.confirm(etfOrderPO.getId(), EtfOrderStatusEnum.WAIT_NOTIFY, _tmpTransactionCost.abs(), DateUtils.now(), confirmBuyShare.abs(), confirmBuyAmount.abs().setScale(2, BigDecimal.ROUND_DOWN), "distributeBuy_HandleBuyBiggerSell");
                    }
                    log.info("distributeBuy_HandleBuyBiggerSell, if etfOrderPO.getId() {} , applyAmountByEtfOrder {} , confirmBuyShare {} , confirmBuyAmount {}  ", etfOrderPO.getId(), applyAmountByEtfOrder, confirmBuyShare, confirmBuyAmount);

                    if (rs.abs().compareTo(BigDecimal.ZERO) > 0) {
                        // Handle PFT
                        rs = rs.setScale(2, BigDecimal.ROUND_DOWN);
                        if (rs.compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal pftConfirmBuyShare = rs;
                            BigDecimal pftConfirmBuyAmount = pftConfirmBuyShare.multiply(price).add(_tmpTransactionCost).multiply(new BigDecimal(-1));//                        
                            log.info("distributeBuy_HandleBuyBiggerSell, else PFT etfOrderPO.getId() {} , pftConfirmBuyShare {} , pftConfirmBuyAmount {}  ", etfOrderPO.getId(), pftConfirmBuyShare, pftConfirmBuyAmount);
                        }
                    }
                }
            }
            //Added By WooiTatt
            BigDecimal pftApplyShare = BigDecimal.ZERO;
            pftApplyShare = redissonHelper.get("HBuy_PftApplyShare_" + etfMergeOrderPO.getId().toString());
            BigDecimal actualTotalSellShare = BigDecimal.ZERO;
            actualTotalSellShare = redissonHelper.get("HBuy_TotalSellShare_" + etfMergeOrderPO.getId().toString());
            boolean isCrossShared = false;
            //boolean isEndOfProceed = false;
            BigDecimal pftSellShare = BigDecimal.ZERO;
            BigDecimal pftBuyShare = BigDecimal.ZERO;
            BigDecimal sumTotalConfirmShare = BigDecimal.ZERO;

            //Handle DO NOTHING AND CROSS SHARE SCENARIO
            if (actualTotalSellShare.compareTo(BigDecimal.ZERO) > 0 && totalConfirmBuyShare.compareTo(BigDecimal.ZERO) > 0) {
                isCrossShared = true;
                if (etfMergeOrderPO.getOrderType() == EtfmergeOrderTypeEnum.DO_NOTHING && etfOrderPOList.size() > 0) {
                    if (actualTotalSellShare.compareTo(totalConfirmBuyShare) > 0) {
                        for (EtfOrderPO etfOrderPO : etfOrderPOList) {
                            etfOrderMapper.confirm(etfOrderPO.getId(), EtfOrderStatusEnum.WAIT_NOTIFY, etfOrderPO.getCostFee(), DateUtils.now(), etfOrderPO.getConfirmShare(), etfOrderPO.getConfirmAmount(), "distributeBuy_HandleBuyBiggerSell_Proceed");
                        }
                    } else {
                        BigDecimal sumTmpApplyAmt = BigDecimal.ZERO;
                        for (EtfOrderPO etfOrderPO : etfOrderPOList) {
                            BigDecimal applyAmountByEtfOrder = etfOrderPO.getTmpApplyAmount();
                            sumTmpApplyAmt = sumTmpApplyAmt.add(applyAmountByEtfOrder);
                        }
                        for (EtfOrderPO etfOrderPO : etfOrderPOList) {
                            //BigDecimal confirmShare = pftApplyShare.multiply(etfOrderPO.getTmpApplyAmount()).divide(sumTmpApplyAmt);
                            BigDecimal confirmShare = actualTotalSellShare.multiply(etfOrderPO.getTmpApplyAmount()).divide(sumTmpApplyAmt);
                            confirmShare = round(confirmShare.doubleValue());
                            BigDecimal confirmAmount = confirmShare.multiply(price);
                            sumTotalConfirmShare = sumTotalConfirmShare.add(confirmShare.abs());
                            totalConfirmBuyShare = sumTotalConfirmShare;
                            etfOrderMapper.confirm(etfOrderPO.getId(), EtfOrderStatusEnum.WAIT_NOTIFY, BigDecimal.ZERO, DateUtils.now(), confirmShare, confirmAmount.abs().setScale(2, BigDecimal.ROUND_DOWN), "distributeBuy_HandleBuyBiggerSell_DoNTH");
                        }
                        //isEndOfProceed = true;
                    }
                }
            }
            log.info("distributeSell_HandleBuyBiggerSell_BUY, if pftApplyShare {} , actualTotalSellShare {} , isCrossShare {}  ", pftSellShare, actualTotalSellShare, isCrossShared);
            // if(!isEndOfProceed){

            //Handle ALL PFT BUY OR SELL
            if (isCrossShared) {
                /* BigDecimal balanceShare = BigDecimal.ZERO;
                   balanceShare = actualTotalSellShare.subtract(totalConfirmBuyShare);
                   if(balanceShare.compareTo(BigDecimal.ZERO) > 0){
                       pftBuyShare = pftApplyShare.subtract(balanceShare.abs()).abs();
                   }else{
                       pftSellShare = pftApplyShare;
                   }*/
                if (actualTotalSellShare.compareTo(totalConfirmBuyShare) > 0) {
                    BigDecimal balanceShare = BigDecimal.ZERO;
                    balanceShare = actualTotalSellShare.subtract(totalConfirmBuyShare);
                    if (pftApplyShare.compareTo(BigDecimal.ZERO) > 0) {
                        if (pftApplyShare.compareTo(balanceShare) > 0) {
                            pftSellShare = pftApplyShare.subtract(balanceShare.abs());
                        } else {
                            pftBuyShare = balanceShare.abs().subtract(pftApplyShare);
                        }
                    } else {
                        pftBuyShare = balanceShare.abs();
                    }
                } else {
                    BigDecimal balanceShare = BigDecimal.ZERO;
                    balanceShare = totalConfirmBuyShare.subtract(actualTotalSellShare);
                    if (pftApplyShare.compareTo(BigDecimal.ZERO) > 0) {
                        pftSellShare = pftApplyShare;
                        pftBuyShare = balanceShare.abs();
                    } else {
                        pftBuyShare = balanceShare.abs();
                    }

                }

                //log.info("CrossShare, if balanceShare {} , pftBuyShare {} , pftSellShare {}  ", balanceShare, pftBuyShare, pftSellShare);   
            } else { //if only buy
                if (etfMergeOrderPO.getConfirmShare().compareTo(totalConfirmBuyShare) > 0) {
                    pftBuyShare = etfMergeOrderPO.getConfirmShare().subtract(totalConfirmBuyShare);
                } else {
                    log.info("##Unlogical Handling >> confirmShare:" + etfMergeOrderPO.getConfirmShare() + "totalConfirmShare>>" + totalConfirmBuyShare);
                }
            }
            // }

            if (pftSellShare.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal pftShareWithCost = transactionCost.multiply(pftSellShare).divide(totalTransactShare, 2, BigDecimal.ROUND_DOWN);
                BigDecimal confirmPftAmount = pftSellShare.multiply(new BigDecimal(-1)).multiply(price).add(pftShareWithCost);
                log.info("distributeBuy_HandleBuyBiggerSell, if applyPftShare {} , pftShareWithCost {} , confirmPftAmount {}  ", pftSellShare, pftShareWithCost, confirmPftAmount);
                //redissonHelper.set("HBuy_SellPFT", pftSellShare.abs());
                String key = "if_pft:distributeBuy_HandleBuyBiggerSell:" + etfMergeOrderPO.getId() + ":" + etfMergeOrderPO.getProductCode();
                String redisValue = (String) redissonHelper.get(key);
                if (redisValue == null) {
                    etfMergeOrderPftMapper.save(new EtfMergePftOrderPO()
                            .setMergeOrderId(etfMergeOrderPO.getId())
                            .setSyncStatus(SyncStatus.INIT)
                            .setProductCode(etfMergeOrderPO.getProductCode())
                            .setAmount(confirmPftAmount.abs())
                            .setShare(pftSellShare.abs())
                            .setCost(pftShareWithCost.abs())
                            .setTradeType(TradeType.SELL)
                            .setSourceType(PftAssetSourceEnum.NORMALSELL)
                            .setCreateTime(DateUtils.now()));
                    redissonHelper.set(key, "1", 600);
                }
            }

            if (pftBuyShare.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal pftShareWithCost = transactionCost.multiply(pftBuyShare).divide(totalTransactShare, 2, BigDecimal.ROUND_DOWN);
                BigDecimal confirmPftAmount = pftBuyShare.multiply(price).subtract(pftShareWithCost);
                confirmPftAmount = confirmPftAmount.multiply(new BigDecimal(-1)); // Sheet9.Cells(i, j + 14) = Round(Sharresd * Sheet8.Cells(i, 26) - CS * Sharresd / TS, 2) * -1
                log.info("distributeBuy_HandleBuyBiggerSell, else applyPftShare {} , pftShareWithCost {} , confirmPftAmount {}  ", pftBuyShare, pftShareWithCost, confirmPftAmount);
                if (pftBuyShare.abs().compareTo(BigDecimal.ZERO) > 0) {
                    //redissonHelper.set("HBuy_BuyPFT", pftSellShare.abs());
                    String key = "else_pft:distributeBuy_HandleBuyBiggerSell:" + etfMergeOrderPO.getId() + ":" + etfMergeOrderPO.getProductCode();
                    String redisValue = (String) redissonHelper.get(key);
                    if (redisValue == null) {
                        etfMergeOrderPftMapper.save(new EtfMergePftOrderPO()
                                .setMergeOrderId(etfMergeOrderPO.getId())
                                .setSyncStatus(SyncStatus.INIT)
                                .setProductCode(etfMergeOrderPO.getProductCode())
                                .setAmount(confirmPftAmount.abs())
                                .setShare(pftBuyShare.abs())
                                .setCost(pftShareWithCost.abs())
                                .setTradeType(TradeType.BUY)
                                .setSourceType(PftAssetSourceEnum.NORMALBUY)
                                .setCreateTime(DateUtils.now()));
                        redissonHelper.set(key, "1", 600);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void demergeOrderSellOrBuy() {

        List<EtfMergeOrderPO> mergeOrderList = etfMergeOrderMapper.getMergeOrder(
                Lists.newArrayList(EtfmergeOrderTypeEnum.SELL, EtfmergeOrderTypeEnum.BUY, EtfmergeOrderTypeEnum.DO_NOTHING),
                EtfMergeOrderStatusEnum.WAIT_DEMERGE);

        //sell 和 doNothing 使用同一个拆分逻辑
        List<EtfMergeOrderPO> etfOrderListSell = mergeOrderList.stream().filter((EtfMergeOrderPO e)
                -> Lists.newArrayList(EtfmergeOrderTypeEnum.SELL, EtfmergeOrderTypeEnum.DO_NOTHING).contains(e.getOrderType())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(etfOrderListSell)) {
            this.demergeOrder(EtfmergeOrderTypeEnum.SELL);
        }

        List<EtfMergeOrderPO> etfOrderListBuy = mergeOrderList.stream().filter((EtfMergeOrderPO e)
                -> EtfmergeOrderTypeEnum.BUY == e.getOrderType()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(etfOrderListBuy)) {
            this.demergeOrder(EtfmergeOrderTypeEnum.BUY);
        }
    }

    //case 1
    final List<EtfmergeOrderTypeEnum> sellCase = Lists.newArrayList(EtfmergeOrderTypeEnum.SELL);
    //case 2 case3
    final List<EtfmergeOrderTypeEnum> buyCase = Lists.newArrayList(EtfmergeOrderTypeEnum.BUY);

    final List<EtfmergeOrderTypeEnum> doNothingCase = Lists.newArrayList(EtfmergeOrderTypeEnum.DO_NOTHING);

    public void demergeOrder(EtfmergeOrderTypeEnum orderType) {

        List<EtfmergeOrderTypeEnum> orderTypeList = Lists.newArrayList();
        //Same case do nothing == sell
        if (orderType == EtfmergeOrderTypeEnum.SELL) {
            orderTypeList = sellCase;
        }
        final List<EtfMergeOrderPO> mergeOrderPOList = etfMergeOrderMapper.getMergeOrder(orderTypeList, EtfMergeOrderStatusEnum.WAIT_DEMERGE);

        for (EtfMergeOrderPO order : mergeOrderPOList) {
            log.info("order {} ", order);
            this.demergeOrderForOrderType(order);
        }
    }

    private BigDecimal calculateAvgPriceFromOrderType(EtfMergeOrderPO order) {
        Map<String, BigDecimal> dailyClosingPriceMap = getDailyClosingPriceMapByMergeOrder(Lists.newArrayList(order));
        BigDecimal avgPrice = BigDecimal.ZERO;
        if (order.getOrderType() == EtfmergeOrderTypeEnum.DO_NOTHING) {
            avgPrice = dailyClosingPriceMap.get(order.getProductCode());
        } else {
            if (order.getConfirmAmount().compareTo(BigDecimal.ZERO) > 0) {
                if (order.getOrderType() == EtfmergeOrderTypeEnum.BUY) {
                    avgPrice = (order.getConfirmAmount().subtract(order.getCostFee())).divide(order.getConfirmShare(), 8, RoundingMode.DOWN);
                }
                if (order.getOrderType() == EtfmergeOrderTypeEnum.SELL) {
                    avgPrice = (order.getConfirmAmount().add(order.getCostFee())).divide(order.getConfirmShare(), 8, RoundingMode.DOWN);
                }
            } else {
                avgPrice = dailyClosingPriceMap.get(order.getProductCode());
                sendMail("**Important Demerge CalculateAvgPrice Confirm Amount is 0", "Product:" + order.getProductCode()
                        + "Confirm Amount:" + order.getConfirmAmount() + "Confirm Share:" + order.getConfirmShare() + "Order Type:" + order.getOrderType());
                log.info("CalculateAvgPrice ConfirmAmt is 0 : {},CfmAmt : {},CfmUnit : {}.", order.getProductCode(), order.getConfirmAmount(), order.getConfirmShare());
                //if (order.getOrderType() == EtfMergeOrderTypeEnum.BUY) {
                //   List<EtfOrderPO> listEtfOrder =  etfOrderMapper.getListByMergeOrderId(order.getId());
                //}
            }
        }
        return avgPrice;
    }

    private void demergeOrderForOrderType(EtfMergeOrderPO order) {
        try {
            Date now = DateUtils.now();
            final List<EtfOrderPO> etfOrderList = etfOrderMapper.getListByMergeOrderId(order.getId());

            final BigDecimal avgPrice = this.calculateAvgPriceFromOrderType(order);
            log.info("demerge avgPrice : {}.", avgPrice);

            final List<EtfOrderTypeEnum> conditionOtherOrder = Lists.newArrayList(EtfOrderTypeEnum.RSA,
                    EtfOrderTypeEnum.GSA, EtfOrderTypeEnum.RSP, EtfOrderTypeEnum.GSP, EtfOrderTypeEnum.GBA, EtfOrderTypeEnum.RBA);
            List<EtfOrderPO> otherEtfOrderList = etfOrderList.stream().filter((EtfOrderPO e) -> conditionOtherOrder.contains(e.getOrderType())).collect(Collectors.toList());

            //没有其他订单则处理pft
            if (CollectionUtils.isEmpty(otherEtfOrderList)) {
                log.info("handle pft sell.");
                if (etfOrderList.size() > 1) {
                    log.error("pft EtfOrderPO etfOrderList.size() > 1,size:{}.", etfOrderList.size());
                    return;
                }
                EtfOrderPO etfOrderPO = etfOrderList.stream().findFirst().get();
                this.handlePft(order, etfOrderPO, avgPrice);
                return;
            }

            //计算所有下单汇总
            final List<EtfMergeOrderExtendPO> etfMergeOrderExtendPOList = etfMergeOrderExtendMapper.getByEtfMergeOrderId(order.getId())
                    .stream().filter((EtfMergeOrderExtendPO e) -> e.getOrderExtendStatus() == EtfOrderExtendStatusEnum.WAIT_CONFIRM)
                    .collect(Collectors.toList());
            final Map<EtfOrderTypeEnum, EtfMergeOrderExtendPO> mergeOrderExtendPOListMap = Maps.uniqueIndex(etfMergeOrderExtendPOList, input -> input.getOrderType());

            if (CollectionUtils.isEmpty(etfMergeOrderExtendPOList)) {
                log.info("etfMergeOrderExtendPOList is empty, return.");
                return;
            }

            final BigDecimal sellShare = getEtfMergeOrderExtendPOApplyShare.apply(etfMergeOrderExtendPOList
                    .stream().filter((EtfMergeOrderExtendPO e) -> conditionSell.contains(e.getOrderType())).collect(Collectors.toList()));
            final BigDecimal buyShare = getEtfMergeOrderExtendPOApplyShare.apply(etfMergeOrderExtendPOList
                    .stream().filter((EtfMergeOrderExtendPO e) -> conditionBuy.contains(e.getOrderType())).collect(Collectors.toList()));
            final BigDecimal sellAmount = getEtfMergeOrderExtendPOApplyAmount.apply(etfMergeOrderExtendPOList
                    .stream().filter((EtfMergeOrderExtendPO e) -> conditionSell.contains(e.getOrderType())).collect(Collectors.toList()));
            final BigDecimal buyAmount = getEtfMergeOrderExtendPOApplyAmount.apply(etfMergeOrderExtendPOList
                    .stream().filter((EtfMergeOrderExtendPO e) -> conditionBuy.contains(e.getOrderType())).collect(Collectors.toList()));
            log.info("demerge sellShare : {},buyShare : {},sellAmount : {},buyAmount : {}.", sellShare, buyShare, sellAmount, buyAmount);

            //更新数据的存储容器
            Map<EtfOrderTypeEnum, List<EtfOrderExtendPO>> etfOrderUpdateMap = Maps.newHashMap();
            for (EtfOrderTypeEnum investTypeEnum : EtfOrderTypeEnum.values()) {
                etfOrderUpdateMap.put(investTypeEnum, Lists.newArrayList());
            }
            log.info("Run 1");
            List<EtfOrderPO> etfOrderPOList = etfOrderList.stream().
                    filter((EtfOrderPO e) -> conditionSell.contains(e.getOrderType())).collect(Collectors.toList());
            BigDecimal sellApplyShare = getEtfOrderPOShare.apply(etfOrderPOList);
            log.info("Run 2");

            BigDecimal rsSellCost = BigDecimal.ZERO;
            BigDecimal reBuyCost = BigDecimal.ZERO;
            log.info("Run 3, buy Share {} ", buyShare);
            if (buyShare != null) {
                if (buyShare.doubleValue() > 0.00) {
                    log.info("Run 3.1, sellApplyShare {} , sellShare {}, buyShare {} , order.getCostFee() ", sellApplyShare, sellShare, buyShare, order.getCostFee());
                    rsSellCost = sellApplyShare.divide(sellShare.add(buyShare), 6, RoundingMode.DOWN)
                            .multiply(order.getCostFee())
                            .setScale(6, BigDecimal.ROUND_DOWN);
                    reBuyCost = order.getCostFee().subtract(rsSellCost);
                }
            }
            log.info("Run 4 buyCase {} , order {} ", buyCase, order);

            if (sellCase.contains(order.getOrderType())) {
                Context context = new Context().setNow(now)
                        .setRealCost(order.getCostFee())
                        .setOrder(order)
                        .setEtfOrderPOList(etfOrderList)
                        .setMergeOrderExtendPOListMap(mergeOrderExtendPOListMap)
                        .setAvgPrice(avgPrice)
                        .setSellApplyAmount(sellAmount)
                        .setBuyApplyAmount(buyAmount)
                        .setSellApplyShare(sellShare)
                        .setBuyApplyShare(buyShare);
                this.getSellCs(context);
                context.setRsBaseSellShare(order.getConfirmShare().add(context.getRealCs()))
                        .setRsBaseSellCost(rsSellCost)
                        .setRsSellShare(order.getConfirmShare().add(context.getRealCs()))
                        .setRsSellCost(rsSellCost);
                context.setRsBaseBuyShare(context.getRealCs())
                        .setRsBaseBuyCost(reBuyCost)
                        .setRsBuyShare(context.getRealCs())
                        .setRsBuyCost(reBuyCost);
                log.info("demerge sell rsSellCost : {},rsSellShare : {}.", context.getRsSellCost(), context.getRsSellShare());
                log.info("demerge buy rsBuyCost : {},rsBuyShare : {}.", context.getRsBuyCost(), context.getRsBuyShare());
                this.handleCase(context, etfOrderUpdateMap);
            }

            log.info("getProductCode {} ", order.getProductCode());
            if (buyCase.contains(order.getOrderType())) {
                log.info("etfOrderUpdateMap {} order {} ", etfOrderUpdateMap, order);
                try {
                    Context context = new Context().setNow(now)
                            .setRealCost(order.getCostFee())
                            .setOrder(order)
                            .setEtfOrderPOList(etfOrderList)
                            .setMergeOrderExtendPOListMap(mergeOrderExtendPOListMap)
                            .setAvgPrice(avgPrice)
                            .setSellApplyAmount(sellAmount)
                            .setBuyApplyAmount(buyAmount)
                            .setSellApplyShare(sellShare)
                            .setBuyApplyShare(buyShare);
                    this.getBuyCs(context);
                    context.setRsBaseSellShare(context.getRealCs())
                            .setRsBaseSellCost(rsSellCost)
                            .setRsSellShare(context.getRealCs())
                            .setRsSellCost(rsSellCost);
                    context.setRsBaseBuyShare(order.getConfirmShare().add(context.getRealCs()))
                            .setRsBaseBuyCost(reBuyCost)
                            .setRsBuyShare(order.getConfirmShare().add(context.getRealCs()))
                            .setRsBuyCost(reBuyCost);
                    log.info("demerge sell rsSellCost : {},rsSellShare : {}.", context.getRsSellCost(), context.getRsSellShare());
                    log.info("demerge buy rsBuyCost : {},rsBuyShare : {}.", context.getRsBuyCost(), context.getRsBuyShare());
                    this.handleCase(context, etfOrderUpdateMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (doNothingCase.contains(order.getOrderType())) {
                if (order.getTotalBuyShare().doubleValue() > order.getTotalSellShare().doubleValue()) {
                    Context context = new Context().setNow(now)
                            .setRealCost(order.getCostFee())
                            .setOrder(order)
                            .setEtfOrderPOList(etfOrderList)
                            .setMergeOrderExtendPOListMap(mergeOrderExtendPOListMap)
                            .setAvgPrice(avgPrice)
                            .setSellApplyAmount(sellAmount)
                            .setBuyApplyAmount(buyAmount)
                            .setSellApplyShare(sellShare)
                            .setBuyApplyShare(buyShare);
                    this.getSellCs(context);
                    context.setRsBaseSellShare(order.getConfirmShare().add(context.getRealCs()))
                            .setRsBaseSellCost(rsSellCost)
                            .setRsSellShare(order.getConfirmShare().add(context.getRealCs()))
                            .setRsSellCost(rsSellCost);
                    context.setRsBaseBuyShare(context.getRealCs())
                            .setRsBaseBuyCost(reBuyCost)
                            .setRsBuyShare(context.getRealCs())
                            .setRsBuyCost(reBuyCost);
                    log.info("demerge sell rsSellCost : {},rsSellShare : {}.", context.getRsSellCost(), context.getRsSellShare());
                    log.info("demerge buy rsBuyCost : {},rsBuyShare : {}.", context.getRsBuyCost(), context.getRsBuyShare());
                    this.handleCase(context, etfOrderUpdateMap);
                } else if (order.getTotalBuyShare().doubleValue() < order.getTotalSellShare().doubleValue()) {
                    log.info("etfOrderUpdateMap {} order {} ", etfOrderUpdateMap, order);
                    Context context = new Context().setNow(now)
                            .setRealCost(order.getCostFee())
                            .setOrder(order)
                            .setEtfOrderPOList(etfOrderList)
                            .setMergeOrderExtendPOListMap(mergeOrderExtendPOListMap)
                            .setAvgPrice(avgPrice)
                            .setSellApplyAmount(sellAmount)
                            .setBuyApplyAmount(buyAmount)
                            .setSellApplyShare(sellShare)
                            .setBuyApplyShare(buyShare);
                    this.getBuyCs(context);
                    context.setRsBaseSellShare(context.getRealCs())
                            .setRsBaseSellCost(rsSellCost)
                            .setRsSellShare(context.getRealCs())
                            .setRsSellCost(rsSellCost);
                    context.setRsBaseBuyShare(order.getConfirmShare().add(context.getRealCs()))
                            .setRsBaseBuyCost(reBuyCost)
                            .setRsBuyShare(order.getConfirmShare().add(context.getRealCs()))
                            .setRsBuyCost(reBuyCost);
                    log.info("demerge sell rsSellCost : {},rsSellShare : {}.", context.getRsSellCost(), context.getRsSellShare());
                    log.info("demerge buy rsBuyCost : {},rsBuyShare : {}.", context.getRsBuyCost(), context.getRsBuyShare());
                    this.handleCase(context, etfOrderUpdateMap);
                } else {
                    try {
                        log.info("Really Do Nothing");
                        log.info("etfOrderUpdateMap {} order {} ", etfOrderUpdateMap, order);
                        Context context = new Context().setNow(now)
                                .setRealCost(order.getCostFee())
                                .setOrder(order)
                                .setEtfOrderPOList(etfOrderList)
                                .setMergeOrderExtendPOListMap(mergeOrderExtendPOListMap)
                                .setAvgPrice(avgPrice)
                                .setSellApplyAmount(sellAmount)
                                .setBuyApplyAmount(buyAmount)
                                .setSellApplyShare(sellShare)
                                .setBuyApplyShare(buyShare);
                        this.getBuyCs(context);
                        context.setRsBaseSellShare(context.getRealCs())
                                .setRsBaseSellCost(rsSellCost)
                                .setRsSellShare(context.getRealCs())
                                .setRsSellCost(rsSellCost);
                        context.setRsBaseBuyShare(order.getConfirmShare().add(context.getRealCs()))
                                .setRsBaseBuyCost(reBuyCost)
                                .setRsBuyShare(order.getConfirmShare().add(context.getRealCs()))
                                .setRsBuyCost(reBuyCost);
                        log.info("demerge sell rsSellCost : {},rsSellShare : {}.", context.getRsSellCost(), context.getRsSellShare());
                        log.info("demerge buy rsBuyCost : {},rsBuyShare : {}.", context.getRsBuyCost(), context.getRsBuyShare());
                        this.handleCase(context, etfOrderUpdateMap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            //更新etf数据
            log.info("etfOrderUpdateMap {} ", etfOrderUpdateMap);
            etfOrderUpdateMap.forEach((etfOrderTypeEnum, etfOrderExtendPOList) -> {
                log.info("etfOrderTypeEnum {} , etfOrderExtendPOList {} ", etfOrderTypeEnum, etfOrderExtendPOList);
                etfOrderExtendPOList.forEach(etfOrderExtendPO -> {
                    log.info("etfOrderExtendPO {} ", etfOrderExtendPO);
                    etfOrderMapper.confirm(etfOrderExtendPO.getId(), EtfOrderStatusEnum.WAIT_NOTIFY, etfOrderExtendPO.getCostFee(), now,
                            etfOrderExtendPO.getConfirmShare(),
                            etfOrderExtendPO.getConfirmAmount(), "demergeOrderForOrderType");
                });
            });
            //ptf数据直接更新完成
            List<Long> ftpEtfOrderListId = etfOrderList.stream()
                    .filter((EtfOrderPO e) -> e.getOrderType() == EtfOrderTypeEnum.PFT)
                    .map(EtfOrderPO::getId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(ftpEtfOrderListId)) {
                etfOrderMapper.updateStatus(ftpEtfOrderListId, EtfOrderStatusEnum.FINISH);
            }

            //新增pft数据
            List<EtfOrderExtendPO> etfEtfOrderExtendPOList = etfOrderUpdateMap.get(EtfOrderTypeEnum.PFT);
            if (CollectionUtils.isNotEmpty(etfEtfOrderExtendPOList)) {
                etfEtfOrderExtendPOList.forEach(etfOrderExtendPO -> {
                    etfMergeOrderPftMapper.save(new EtfMergePftOrderPO()
                            .setMergeOrderId(order.getId())
                            .setSyncStatus(SyncStatus.INIT)
                            .setProductCode(etfOrderExtendPO.getProductCode())
                            .setAmount(etfOrderExtendPO.getConfirmAmount())
                            .setShare(etfOrderExtendPO.getConfirmShare())
                            .setCost(etfOrderExtendPO.getCostFee())
                            .setTradeType(etfOrderExtendPO.getTradeType())
                            .setSourceType(etfOrderExtendPO.getSourceEnum())
                            .setCreateTime(now)
                    );
                });
            }
            //更新etfMergeOrder数据
            etfMergeOrderMapper.updateStatusAndSetPrice(order.getId(), EtfMergeOrderStatusEnum.FINISH, avgPrice);
            demergeForOrderType(order.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void demergeForOrderType(Long mergeOrderId) {
        Date now = DateUtils.now();

        List<EtfMergeOrderExtendPO> mergeOrderExtendPOList = etfMergeOrderExtendMapper.getByEtfMergeOrderId(mergeOrderId);

        Map<EtfOrderTypeEnum, List<EtfOrderPO>> etfOrderTypeEnumListMap = Multimaps.asMap(Multimaps.index(etfOrderMapper.getListByMergeOrderId(mergeOrderId),
                input -> input.getOrderType()));
        mergeOrderExtendPOList.forEach(etfMergeOrderExtendPO -> {
            List<EtfOrderPO> etfOrderPOList = etfOrderTypeEnumListMap.get(etfMergeOrderExtendPO.getOrderType());
            etfMergeOrderExtendPO
                    .setConfirmAmount(getEtfOrderPOConfirmAmount.apply(etfOrderPOList))
                    .setConfirmShare(getEtfOrderPOConfirmShare.apply(etfOrderPOList))
                    .setCostFee(getEtfOrderPOCost.apply(etfOrderPOList))
                    .setOrderExtendStatus(EtfOrderExtendStatusEnum.FINISH)
                    .setConfirmTime(now)
                    .setUpdateTime(now);

        });
        mergeOrderExtendPOList.forEach(etfMergeOrderExtendPO -> etfMergeOrderExtendMapper.updateForConfirm(etfMergeOrderExtendPO));

    }

    private void handlePft(EtfMergeOrderPO order, EtfOrderPO etfOrderPO, BigDecimal avgPrice) {
        BigDecimal amount = etfOrderPO.getApplyShare().multiply(avgPrice).subtract(order.getCostFee()).setScale(6, RoundingMode.DOWN);
        etfOrderMapper.updateStatus(Lists.newArrayList(etfOrderPO.getId()), EtfOrderStatusEnum.FINISH);
        etfMergeOrderMapper.updateStatus(order.getId(), EtfMergeOrderStatusEnum.FINISH);
        etfMergeOrderPftMapper.save(new EtfMergePftOrderPO()
                .setMergeOrderId(order.getId())
                .setSyncStatus(SyncStatus.INIT)
                .setProductCode(etfOrderPO.getProductCode())
                .setAmount(amount)
                .setShare(etfOrderPO.getApplyShare())
                .setCost(order.getCostFee())
                .setTradeType(TradeType.SELL)
                .setSourceType(PftAssetSourceEnum.ONLYSELL)
                .setCreateTime(DateUtils.now())
        );

    }

    @Data
    @Accessors(chain = true)
    private class Context {

        /**
         * 基础数据参数
         */
        Date now;
        EtfMergeOrderPO order;
        List<EtfOrderPO> etfOrderPOList;
        BigDecimal sellApplyShare;
        BigDecimal buyApplyShare;
        BigDecimal sellApplyAmount;
        BigDecimal buyApplyAmount;
        BigDecimal avgPrice;

        /**
         * 动态变更参数 一个参数变更需要全部变更
         */
        BigDecimal realCs;
        BigDecimal realCost;

        BigDecimal rsBaseBuyCost;
        BigDecimal rsBaseBuyShare;
        BigDecimal rsBuyCost;
        BigDecimal rsBuyShare;

        BigDecimal rsBaseSellCost;
        BigDecimal rsBaseSellShare;
        BigDecimal rsSellCost;
        BigDecimal rsSellShare;

        /**
         * 更新容器
         */
        Map<EtfOrderTypeEnum, EtfMergeOrderExtendPO> mergeOrderExtendPOListMap;

        /**
         * 记录容器
         */
        Map<ContextStep, Context> ContextStepMap = Maps.newHashMap();
    }

    private enum ContextStep {
        FIRST,
        SECOND,
        THIRD,
    }

    private void handleCase(Context context, Map<EtfOrderTypeEnum, List<EtfOrderExtendPO>> etfOrderUpdateMap) {
        this.putContextStepMap(ContextStep.FIRST, context);
        this.handleCaseFirstPhase(context, etfOrderUpdateMap);
        this.againCalculateSurplusShareAndCost(context, etfOrderUpdateMap);

        this.putContextStepMap(ContextStep.SECOND, context);
        this.handleCaseSecondPhase(context, etfOrderUpdateMap);
        this.againCalculateSurplusShareAndCost(context, etfOrderUpdateMap);

        this.putContextStepMap(ContextStep.THIRD, context);
        this.handleCaseThirdPhase(context, etfOrderUpdateMap);
        this.againCalculateSurplusShareAndCost(context, etfOrderUpdateMap);
    }

    private void putContextStepMap(ContextStep step, Context context) {
        context.getContextStepMap().put(step, new Context()
                .setRealCs(context.getRealCs())
                .setRealCost(context.getRealCost())
                .setRsBuyCost(context.getRsBuyCost())
                .setRsBuyShare(context.getRsBuyShare())
                .setRsSellCost(context.getRsSellCost())
                .setRsSellShare(context.getRsSellShare())
                .setRsBaseBuyCost(context.getRsBaseBuyCost())
                .setRsBaseBuyShare(context.getRsBaseBuyShare())
                .setRsBaseSellCost(context.getRsBaseSellCost())
                .setRsBaseSellShare(context.getRsBaseSellShare()));
    }

    private void getSellCs(Context context) {
        final EtfMergeOrderPO order = context.order;
        final BigDecimal avgPrice = context.avgPrice;
        //S_SAXO
        final BigDecimal confirmShare = order.getConfirmShare();
        //TCS TCB
        final BigDecimal costFee = order.getCostFee();
        //BuyX == CS == ZS2
        BigDecimal buyShare = context.getBuyApplyShare();
        //SellX
        BigDecimal sellShare = context.getSellApplyShare();

        final BigDecimal buyAmount = context.getBuyApplyAmount();
        BigDecimal buyShareRate = buyShare.divide(sellShare.add(buyShare), 6, RoundingMode.DOWN);
        BigDecimal buyCostFee = costFee.multiply(buyShareRate).setScale(6, BigDecimal.ROUND_DOWN);
        BigDecimal buyAmountWithOutCost = buyAmount.subtract(buyCostFee);
        BigDecimal buyShareByRealCostFee = buyAmountWithOutCost.divide(avgPrice, 6, BigDecimal.ROUND_DOWN);
        BigDecimal realCs = buyShareByRealCostFee.setScale(2, BigDecimal.ROUND_DOWN);
        //S_SAXO + CS >= SellX  CS = SellX - S_SAXO
        if (confirmShare.add(realCs).compareTo(sellShare) >= 0) {
            realCs = sellShare.subtract(confirmShare);
            log.info("update cs realCs : {},sellShare : {},confirmShare : {}.", realCs, sellShare, confirmShare);
        }
        context.setRealCs(realCs.setScale(2, BigDecimal.ROUND_DOWN));
    }

    private void getBuyCs(Context context) {
        log.info("getBuyCs {} ", context);
        try {
            EtfMergeOrderPO order = context.getOrder();
            log.info("order {} ", order);
            BigDecimal avgPrice = context.getAvgPrice();
            log.info("avgPrice {} ", avgPrice);
            //TCS TCB
            BigDecimal costFee = order.getCostFee();
            log.info("costFee {} ", costFee);
            //收盘价判断
            //SellX
            BigDecimal sellShare = context.getSellApplyShare();
            log.info("sellShare {} ", sellShare);
            //BuyX
            BigDecimal buyShare = context.getBuyApplyShare();
            log.info("buyShare {} ", buyShare);

            BigDecimal diff = sellShare.add(buyShare);
            BigDecimal realCs = BigDecimal.ZERO;

            if (diff.doubleValue() > 0) {
                realCs = context.getSellApplyAmount()
                        .subtract(costFee.multiply(sellShare.divide(diff, 6, BigDecimal.ROUND_HALF_DOWN)))
                        .divide(avgPrice, 2, BigDecimal.ROUND_DOWN);
                log.info("realCs {} ", realCs);
                if (realCs.compareTo(sellShare) >= 0) {
                    realCs = sellShare;
                    log.info("update cs realCs : {} ", realCs);
                }
            }
            log.info("getBuyCs context {} ", context);
            context.setRealCs(realCs.setScale(2, BigDecimal.ROUND_DOWN));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCaseFirstPhase(Context context, Map<EtfOrderTypeEnum, List<EtfOrderExtendPO>> etfOrderUpdateMap) {
        //SellX
        BigDecimal sellShare = context.getSellApplyShare();
        //BuyX == CS == ZS2
        BigDecimal costFee = context.getRsSellCost();

        List<EtfOrderPO> etfOrderPOFilterList = context.getEtfOrderPOList()
                .stream().filter((EtfOrderPO e) -> conditionAllRedeem.contains(e.getOrderType())).collect(Collectors.toList());
        this.demergeEtfOrderListFill(context,
                etfOrderPOFilterList,
                etfOrderUpdateMap,
                sellShare, costFee);
    }

    private void handleAllRedeemReplyCostToAnyOrder(BigDecimal avgPrice, List<EtfOrderExtendPO> etfOrderExtendPOList, BigDecimal rsCost) {
        if (CollectionUtils.isEmpty(etfOrderExtendPOList)) {
            log.info("etfOrderExtendPOList is empty.");
            return;
        }
        EtfOrderExtendPO anyOrder = etfOrderExtendPOList.stream().findAny().get();
        BigDecimal baseCost = anyOrder.getCostFee();
        BigDecimal addCost = baseCost.add(rsCost);
        anyOrder.setCostFee(addCost);
        //全赎订单的applyShare和confirmShare是一样的
        BigDecimal amount = anyOrder.getConfirmShare().multiply(avgPrice).subtract(anyOrder.getCostFee()).setScale(6, RoundingMode.DOWN);
        anyOrder.setConfirmAmount(amount);
        log.info("reply cost to anyOrder baseCost:{},addCost:{},final amount:{}.", baseCost, addCost, amount);
    }

    private void handleReplyCostToAnyOrder(Context context, Map<EtfOrderTypeEnum, List<EtfOrderExtendPO>> etfOrderUpdateMap) {
        //剩余未处理的cost
        BigDecimal rsCost = context.getRsSellCost();
        List<EtfOrderExtendPO> etfOrderExtendPOList = Lists.newArrayList();
        etfOrderUpdateMap.forEach((k, v) -> {
            if (conditionAllRedeem.contains(k)) {
                etfOrderExtendPOList.addAll(v);
            }
        });
        if (CollectionUtils.isEmpty(etfOrderExtendPOList)) {
            log.info("etfOrderExtendPOList is empty.");
            return;
        }
        EtfOrderExtendPO anyOrder = etfOrderExtendPOList.stream().findAny().get();
        BigDecimal baseCost = anyOrder.getCostFee();
        BigDecimal addCost = baseCost.add(rsCost);
        anyOrder.setCostFee(addCost);
        //全赎订单的applyShare和confirmShare是一样的
        BigDecimal amount = anyOrder.getConfirmShare().multiply(context.getAvgPrice()).subtract(anyOrder.getCostFee()).setScale(6, RoundingMode.DOWN);
        anyOrder.setConfirmAmount(amount);
        log.info("reply cost to anyOrder baseCost:{},addCost:{},final amount:{}.", baseCost, addCost, amount);
        //需要重新计算re
        this.againCalculateSurplusShareAndCost(context, etfOrderUpdateMap);
    }

    private void againCalculateSurplusShareAndCost(Context context, Map<EtfOrderTypeEnum, List<EtfOrderExtendPO>> etfOrderUpdateMap) {

        List<EtfOrderExtendPO> sellList = Lists.newArrayList();
        List<EtfOrderExtendPO> buyList = Lists.newArrayList();

        for (EtfOrderTypeEnum etfOrderTypeEnum : etfOrderUpdateMap.keySet()) {
            //不计算pft
            if (EtfOrderTypeEnum.PFT == etfOrderTypeEnum) {
                continue;
            }
            if (conditionSell.contains(etfOrderTypeEnum)) {
                sellList.addAll(etfOrderUpdateMap.get(etfOrderTypeEnum));
            }
            if (conditionBuy.contains(etfOrderTypeEnum)) {
                buyList.addAll(etfOrderUpdateMap.get(etfOrderTypeEnum));
            }
        }

        BigDecimal sellCost = getEtfOrderExtendPOCost.apply(sellList);
        BigDecimal sellShare = getEtfOrderExtendPOConfirmShare.apply(sellList);
        BigDecimal buyCost = getEtfOrderExtendPOCost.apply(buyList);
        BigDecimal buyShare = getEtfOrderExtendPOConfirmShare.apply(buyList);

        BigDecimal rsSellCost = context.getRsBaseSellCost().subtract(sellCost);
        BigDecimal rsSellShare = context.getRsBaseSellShare().subtract(sellShare);
        BigDecimal rsBuyCost = context.getRsBaseBuyCost().subtract(buyCost);
        BigDecimal rsBuyShare = context.getRsBaseBuyShare().subtract(buyShare);
        context.setRsSellCost(rsSellCost);
        context.setRsSellShare(rsSellShare);
        context.setRsBuyCost(rsBuyCost);
        context.setRsBuyShare(rsBuyShare);
        log.info("againCalculateSurplusShareAndCost 1 realCs:{},realCost:{}.", context.getRealCs(), context.getRealCost());
        log.info("againCalculateSurplusShareAndCost 2 rsBaseSellCost:{},rsBaseSellShare:{},rsSellCost:{},rsSellShare:{}.", context.getRsBaseSellCost(), context.getRsBaseSellShare(), context.getRsSellCost(), context.getRsSellShare());
        log.info("againCalculateSurplusShareAndCost 3 rsBaseBuyCost:{},rsBaseBuyShare:{},rsBuyCost:{},rsBuyShare:{}.", context.getRsBaseBuyCost(), context.getRsBaseBuyShare(), context.getRsBuyCost(), context.getRsBuyShare());
    }

    private void handleCaseSecondPhase(Context context, Map<EtfOrderTypeEnum, List<EtfOrderExtendPO>> etfOrderUpdateMap) {
        this.handleCaseSecondPhaseChildOne(context, etfOrderUpdateMap);
        this.handleCaseSecondPhaseChildTwo(context, etfOrderUpdateMap);
    }

    private void handleCaseSecondPhaseChildOne(Context context, Map<EtfOrderTypeEnum, List<EtfOrderExtendPO>> etfOrderUpdateMap) {
        EtfMergeOrderPO order = context.getOrder();

        BigDecimal avgPrice = context.avgPrice;
        BigDecimal rsShare = context.getRsSellShare();
        List<EtfOrderPO> etfOrderPOList = context.getEtfOrderPOList();

        //处理RSP GSP PFT
        if (rsShare.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("rsShare.compareTo(BigDecimal.ZERO) <= 0.");

            //不卖只买，买shares=|RS|
            if (rsShare.abs().compareTo(BigDecimal.ZERO) != 0) {
                etfOrderUpdateMap.get(EtfOrderTypeEnum.PFT).add(new EtfOrderExtendPO()
                        //PFT Cost = 0
                        .setCostFee(BigDecimal.ZERO)
                        .setConfirmShare(rsShare.abs())
                        .setConfirmAmount(rsShare.abs().multiply(avgPrice).setScale(2, BigDecimal.ROUND_DOWN))
                        .setTradeType(TradeType.BUY)
                        .setProductCode(order.getProductCode())
                        .setSourceEnum(PftAssetSourceEnum.NORMALBUY));
            }
            //重新计算gsa rsa
            handleReplyCostToAnyOrder(context, etfOrderUpdateMap);

            //将其他订单的amount、share、cost置为0
            List<EtfOrderPO> gspAndRspEtfOrderPOList = etfOrderPOList.stream()
                    .filter((EtfOrderPO e) -> conditionPartRedeem.contains(e.getOrderType()) && e.getOrderType() != EtfOrderTypeEnum.PFT).collect(Collectors.toList());
            gspAndRspEtfOrderPOList.stream().forEach(etfOrderPO
                    -> etfOrderUpdateMap.get(etfOrderPO.getOrderType()).add(new EtfOrderExtendPO()
                            .setId(etfOrderPO.getId())
                            .setConfirmShare(BigDecimal.ZERO)
                            .setConfirmAmount(BigDecimal.ZERO)
                            .setCostFee(BigDecimal.ZERO))
            );

            List<EtfOrderPO> gbaAndRbaEtfOrderPOList = etfOrderPOList.stream()
                    .filter((EtfOrderPO e) -> conditionBuy.contains(e.getOrderType())).collect(Collectors.toList());
            gbaAndRbaEtfOrderPOList.stream().forEach(etfOrderPO
                    -> etfOrderUpdateMap.get(etfOrderPO.getOrderType()).add(new EtfOrderExtendPO()
                            .setId(etfOrderPO.getId())
                            .setConfirmShare(BigDecimal.ZERO)
                            .setConfirmAmount(BigDecimal.ZERO)
                            .setCostFee(BigDecimal.ZERO))
            );

            return;
        }
    }

    private void handleCaseSecondPhaseChildTwo(Context context, Map<EtfOrderTypeEnum, List<EtfOrderExtendPO>> etfOrderUpdateMap) {
        BigDecimal rsSellCost = context.getRsSellCost();
        BigDecimal rsSellShare = context.getRsSellShare();
        List<EtfOrderPO> etfOrderPOList = context.getEtfOrderPOList().stream().
                filter((EtfOrderPO e) -> conditionPartRedeem.contains(e.getOrderType())).collect(Collectors.toList());

        //处理RSP GSP PFT
        if (rsSellShare.compareTo(BigDecimal.ZERO) > 0) {
            log.info("rsShare.compareTo(BigDecimal.ZERO) > 0,rsSellShare:{}.", rsSellShare);
            //将pft放在最后处理
            etfOrderPOList.sort(Comparator.comparingInt(e -> e.getOrderType().getSeq()));
            demergeEtfOrderListCorrect(context.getAvgPrice(),
                    etfOrderPOList,
                    etfOrderUpdateMap,
                    getEtfOrderPOAmount.apply(etfOrderPOList), rsSellShare, rsSellCost);

            this.againCalculateSurplusShareAndCost(context, etfOrderUpdateMap);
            rsSellShare = context.getRsSellShare();
            rsSellCost = context.getRsSellCost();

            log.info("realCs : {},rsSellCost : {},rsSellShare : {}.", context.getRealCs(), context.getRsSellCost(), context.getRsSellShare());

            //对剩余的rsShare进行处理
            List<EtfOrderPO> pftEtfOrderPO = etfOrderPOList.stream().
                    filter((EtfOrderPO e) -> e.getOrderType() == EtfOrderTypeEnum.PFT).collect(Collectors.toList());
            BigDecimal pftShare = Optional.ofNullable(getEtfOrderPOShare.apply(pftEtfOrderPO)).orElse(BigDecimal.ZERO);
            if (rsSellShare.compareTo(BigDecimal.ZERO) > 0 && rsSellShare.compareTo(pftShare) <= 0) {
                log.info("rsShare.compareTo(BigDecimal.ZERO) > 0 && rsShare.compareTo(pftShare) <= 0,rsSellShare:{},pftShare:{}.", rsSellShare, pftShare);

                BigDecimal amount = rsSellShare.multiply(context.getAvgPrice()).subtract(rsSellCost).setScale(6, RoundingMode.DOWN);

                //卖shares=RS
                etfOrderUpdateMap.get(EtfOrderTypeEnum.PFT).add(new EtfOrderExtendPO()
                        //PFT Cost = 0
                        .setCostFee(rsSellCost)
                        .setConfirmShare(rsSellShare)
                        .setConfirmAmount(amount)
                        .setTradeType(TradeType.SELL)
                        .setProductCode(context.getOrder().getProductCode())
                        .setSourceEnum(PftAssetSourceEnum.NORMALSELL));
                return;
            }
            if (rsSellShare.compareTo(BigDecimal.ZERO) > 0 && rsSellShare.compareTo(pftShare) > 0) {
                log.info("rsSellShare.compareTo(BigDecimal.ZERO) > 0 && rsSellShare.compareTo(pftShare) > 0.");

                BigDecimal amount = pftShare.multiply(context.getAvgPrice()).subtract(rsSellCost).setScale(6, RoundingMode.DOWN);
                if (pftShare.compareTo(BigDecimal.ZERO) != 0) {
                    //卖shares=RS
                    etfOrderUpdateMap.get(EtfOrderTypeEnum.PFT).add(new EtfOrderExtendPO()
                            .setCostFee(rsSellCost)
                            .setConfirmShare(pftShare)
                            .setConfirmAmount(amount)
                            .setTradeType(TradeType.SELL)
                            .setProductCode(context.getOrder().getProductCode())
                            .setSourceEnum(PftAssetSourceEnum.NORMALSELL));
                }

                //未处理的share，作为adjsShare在cs中减去，减少cs持有
                BigDecimal adjsShare = rsSellShare.subtract(pftShare);

                log.info("adjsShare:{}.", adjsShare);
                if (adjsShare.compareTo(BigDecimal.ZERO) > 0) {
                    //没有买单和其他卖单处理adjsShare则重新分配部分赎回的单子
                    List<EtfOrderPO> otherEtfOrderPOList = context.getEtfOrderPOList().stream().
                            filter((EtfOrderPO e) -> !conditionPartRedeem.contains(e.getOrderType())).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(otherEtfOrderPOList)) {
                        //清空部分赎回的单子
                        conditionPartRedeem.stream().forEach(etfOrderTypeEnum -> {
                            if (etfOrderTypeEnum != EtfOrderTypeEnum.PFT) {
                                etfOrderUpdateMap.put(etfOrderTypeEnum, Lists.newArrayList());
                            }
                        });
                        //恢复到做部分赎回处理前的状态并取出pft的占用
                        rsSellShare = context.getContextStepMap().get(ContextStep.SECOND).getRsSellShare().subtract(pftShare);
                        rsSellCost = context.getContextStepMap().get(ContextStep.SECOND).getRsSellCost().subtract(rsSellCost);

                        List<EtfOrderPO> etfOrderPOListWithOut = etfOrderPOList.stream().filter(etfOrderPO -> etfOrderPO.getOrderType() != EtfOrderTypeEnum.PFT).collect(Collectors.toList());
                        //重新分配部分赎回
                        demergeEtfOrderListCorrect(context.getAvgPrice(),
                                etfOrderPOListWithOut,
                                etfOrderUpdateMap,
                                getEtfOrderPOAmount.apply(etfOrderPOListWithOut), rsSellShare, rsSellCost);
                    } else {
                        context.setRsBaseBuyShare(context.getRsBaseBuyShare().subtract(adjsShare));
                    }
                }

                return;
            }

        }
    }

    private void handleCaseThirdPhase(Context context, Map<EtfOrderTypeEnum, List<EtfOrderExtendPO>> etfOrderUpdateMap) {

        BigDecimal rsBuyCost = context.getRsBuyCost();
        BigDecimal rsBuyShare = context.getRsBuyShare();
        //GBA RBA
        List<EtfOrderPO> etfOrderPOList = context.getEtfOrderPOList().stream().
                filter((EtfOrderPO e) -> conditionBuy.contains(e.getOrderType())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(etfOrderPOList)) {
            return;
        }
        log.info("handleCaseThirdPhase, Handle gba or rba order , {} ", etfOrderUpdateMap);
        demergeEtfOrderListCorrect(context.getAvgPrice(),
                etfOrderPOList,
                etfOrderUpdateMap,
                getEtfOrderPOAmount.apply(etfOrderPOList), rsBuyShare, rsBuyCost);

        this.againCalculateSurplusShareAndCost(context, etfOrderUpdateMap);
        log.info("handleCaseThirdPhase, Before context {} , etfOrderUpdateMap {}  ", context, etfOrderUpdateMap);
        rsBuyShare = context.getRsBuyShare();
        log.info("handleCaseThirdPhase, After context {} , etfOrderUpdateMap {}  ", context, etfOrderUpdateMap);
        log.info("handleCaseThirdPhase, rsBuyShare {} ", rsBuyShare);
        if (rsBuyShare.compareTo(BigDecimal.ZERO) > 0) {
            log.info("reShare.compareTo(BigDecimal.ZERO) > 0,rsBuyShare:{}.", rsBuyShare);

            //不卖只买，买shares=|RS|
            etfOrderUpdateMap.get(EtfOrderTypeEnum.PFT).add(new EtfOrderExtendPO()
                    //PFT Cost = 0
                    .setCostFee(BigDecimal.ZERO)
                    .setConfirmAmount(rsBuyShare.abs().multiply(context.getAvgPrice().setScale(6, BigDecimal.ROUND_DOWN)))
                    .setConfirmShare(rsBuyShare)
                    .setTradeType(TradeType.BUY)
                    .setSourceEnum(PftAssetSourceEnum.NORMALBUY)
                    .setProductCode(context.getOrder().getProductCode()));
            return;
        }

    }

    private BigDecimal handleOverSell(EtfOrderPO etfOrder, BigDecimal share) {
        AccountEtfAssetReqDTO reqDTO = new AccountEtfAssetReqDTO();
        reqDTO.setAccountId(etfOrder.getAccountId());
        RpcMessage<AccountEtfAssetResDTO> rpcMessage = assetServiceRemoteService.queryAccountEtfShare(reqDTO);
        BigDecimal holdingShare = rpcMessage.getContent().getDataMap().get(etfOrder.getProductCode());

        if (share.compareTo(holdingShare) > 0) {
            return holdingShare;
        }
        return share;
    }

    private BigDecimal handleOverBuy(EtfOrderPO etfOrder, BigDecimal share, BigDecimal avgPrice, BigDecimal cost) {
        BigDecimal amount = share.multiply(avgPrice).add(cost).setScale(6, RoundingMode.DOWN);
        //确认amount 大于了要购买的钱
        if (amount.compareTo(etfOrder.getApplyAmount()) > 0) {
            return etfOrder.getApplyAmount().subtract(cost).divide(avgPrice, 2, BigDecimal.ROUND_DOWN);
        }
        if (amount.compareTo(etfOrder.getApplyAmount()) <= 0) {
            return share;
        }
        return share;
    }

    private void demergeEtfOrderListCorrect(BigDecimal avgPrice,
            List<EtfOrderPO> etfOrderList,
            Map<EtfOrderTypeEnum, List<EtfOrderExtendPO>> etfOrderUpdateMap,
            BigDecimal totalApplyAmount, BigDecimal totalConfirmShare, BigDecimal totalCostFee) {

        if (CollectionUtils.isNotEmpty(etfOrderList)) {
            BigDecimal cost = totalCostFee;
            BigDecimal shareConfirm = totalConfirmShare;
            BigDecimal amountApply = totalApplyAmount;

            BigDecimal useShare = BigDecimal.ZERO;
            BigDecimal useCost = BigDecimal.ZERO;
            log.info("cost:{},shareConfirm:{},amountApply:{},avgPrice:{}.", cost, shareConfirm, amountApply, avgPrice);
            for (int i = 0; i < etfOrderList.size(); i++) {

                EtfOrderPO etfOrderPO = etfOrderList.get(i);
                //pft 只有在amountApply计算的时候参加
                if (etfOrderPO.getOrderType() == EtfOrderTypeEnum.PFT) {
                    continue;
                }
                BigDecimal rateTemp = etfOrderPO.getApplyAmount().divide(amountApply, 6, RoundingMode.DOWN);
                BigDecimal shareTemp = shareConfirm.multiply(rateTemp).setScale(2, BigDecimal.ROUND_DOWN);
                BigDecimal costTemp = cost.multiply(rateTemp).setScale(6, BigDecimal.ROUND_DOWN);
                log.info("normal rateTemp:{},shareTemp:{},costTemp:{}.", rateTemp, shareTemp, costTemp);

                //-1 处理余值
                if (i == etfOrderList.size() - 1) {
                    shareTemp = shareConfirm.subtract(useShare).setScale(2, BigDecimal.ROUND_DOWN);
                    costTemp = cost.subtract(useCost).setScale(6, BigDecimal.ROUND_DOWN);
                    log.info("-1 shareTemp:{},costTemp:{}.", shareTemp, costTemp);
                }

                //超卖超买不参与 -1 计算中
                if (i != etfOrderList.size() - 1) {
                    useShare = useShare.add(shareTemp);
                    useCost = useCost.add(costTemp);
                }

                //超卖超卖处理
                if (conditionSell.contains(etfOrderPO.getOrderType())) {
                    shareTemp = this.handleOverSell(etfOrderPO, shareTemp);
                }
                if (conditionBuy.contains(etfOrderPO.getOrderType())) {
                    shareTemp = this.handleOverBuy(etfOrderPO, shareTemp, avgPrice, costTemp);
                }
                log.info("handleOver shareTemp:{}.", shareTemp);

                //按类型计算amount
                BigDecimal amount = shareTemp.multiply(avgPrice);
                if (conditionSell.contains(etfOrderPO.getOrderType())) {
                    amount = amount.subtract(costTemp).setScale(6, RoundingMode.DOWN);
                }
                if (conditionBuy.contains(etfOrderPO.getOrderType())) {
                    amount = amount.add(costTemp).setScale(6, RoundingMode.DOWN);
                }
                log.info("amount:{}.", amount);

                etfOrderUpdateMap.get(etfOrderPO.getOrderType()).add(new EtfOrderExtendPO()
                        .setId(etfOrderPO.getId())
                        .setConfirmShare(shareTemp)
                        .setConfirmAmount(amount)
                        .setCostFee(costTemp)
                        .setProductCode(etfOrderPO.getProductCode())
                        .setTradeType(TradeType.SELL));

            }
        }
    }

    private void demergeEtfOrderListFill(Context context,
            List<EtfOrderPO> etfOrderList,
            Map<EtfOrderTypeEnum, List<EtfOrderExtendPO>> etfOrderUpdateMap,
            BigDecimal totalApplyShare, BigDecimal totalCostFee) {
        if (CollectionUtils.isEmpty(etfOrderList)) {
            return;
        }
        etfOrderList.forEach(input -> {
            BigDecimal rate = input.getApplyShare().divide(totalApplyShare, 6, RoundingMode.DOWN);
            BigDecimal cost = totalCostFee.multiply(rate).setScale(6, RoundingMode.DOWN);
            BigDecimal amount = input.getApplyShare().multiply(context.getAvgPrice()).subtract(cost).setScale(6, RoundingMode.DOWN);
            log.info("rate:{},cost:{},amount:{}.", rate, cost, amount);
            etfOrderUpdateMap.get(input.getOrderType()).add(new EtfOrderExtendPO().setId(input.getId()).setConfirmShare(input.getApplyShare()).setConfirmAmount(amount).setCostFee(cost));
        });
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

    final Function<List<EtfOrderPO>, BigDecimal> getEtfOrderPOAmount = input -> Optional.ofNullable(input)
            .orElse(Lists.newArrayList()).stream().map(EtfOrderPO::getApplyAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    final Function<List<EtfOrderPO>, BigDecimal> getEtfOrderPOShare = input -> Optional.ofNullable(input)
            .orElse(Lists.newArrayList()).stream().map(EtfOrderPO::getApplyShare)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    final Function<List<EtfOrderPO>, BigDecimal> getEtfOrderPOCost = input -> Optional.ofNullable(input)
            .orElse(Lists.newArrayList()).stream().map(EtfOrderPO::getCostFee)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    final Function<List<EtfOrderPO>, BigDecimal> getEtfOrderPOConfirmAmount = input -> Optional.ofNullable(input)
            .orElse(Lists.newArrayList()).stream().map(EtfOrderPO::getConfirmAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    final Function<List<EtfOrderPO>, BigDecimal> getEtfOrderPOConfirmShare = input -> Optional.ofNullable(input)
            .orElse(Lists.newArrayList()).stream().map(EtfOrderPO::getConfirmShare)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    final Function<List<EtfOrderExtendPO>, BigDecimal> getEtfOrderExtendPOCost = input -> Optional.ofNullable(input)
            .orElse(Lists.newArrayList()).stream().map(EtfOrderExtendPO::getCostFee)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    final Function<List<EtfOrderExtendPO>, BigDecimal> getEtfOrderExtendPOConfirmShare = input -> Optional.ofNullable(input)
            .orElse(Lists.newArrayList()).stream().map(EtfOrderExtendPO::getConfirmShare)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    final Function<List<EtfMergeOrderExtendPO>, BigDecimal> getEtfMergeOrderExtendPOApplyAmount = input -> Optional.ofNullable(input)
            .orElse(Lists.newArrayList()).stream().map(EtfMergeOrderExtendPO::getApplyAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    final Function<List<EtfMergeOrderExtendPO>, BigDecimal> getEtfMergeOrderExtendPOApplyShare = input -> Optional.ofNullable(input)
            .orElse(Lists.newArrayList()).stream().map(EtfMergeOrderExtendPO::getApplyShare)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    final List<EtfOrderTypeEnum> conditionAllRedeem = Lists.newArrayList(EtfOrderTypeEnum.RSA,
            EtfOrderTypeEnum.GSA);
    final List<EtfOrderTypeEnum> conditionPartRedeem = Lists.newArrayList(EtfOrderTypeEnum.RSP, EtfOrderTypeEnum.GSP,
            EtfOrderTypeEnum.PFT);
    final List<EtfOrderTypeEnum> conditionSell = Lists.newArrayList(EtfOrderTypeEnum.RSA,
            EtfOrderTypeEnum.GSA, EtfOrderTypeEnum.RSP, EtfOrderTypeEnum.GSP, EtfOrderTypeEnum.PFT);
    final List<EtfOrderTypeEnum> conditionBuy = Lists.newArrayList(EtfOrderTypeEnum.GBA, EtfOrderTypeEnum.RBA);

    private int getUp(BigDecimal b) {
        return Double.valueOf(Math.ceil(b.doubleValue())).intValue();
    }

    private int getDown(BigDecimal b) {
        return Double.valueOf(Math.floor(b.doubleValue())).intValue();
    }

    private BigDecimal round(Double input) {
        String strInput = "" + input;
        BigDecimal output = BigDecimal.ZERO;
        if (strInput.contains(".")) {
            try {
                String decimalPoint = "0." + strInput.split("\\.")[1];
                Double decimalDouble = Double.valueOf(decimalPoint);
                if (decimalDouble >= 0.50) {
                    output = new BigDecimal(input).setScale(2, RoundingMode.UP);
                } else {
                    output = new BigDecimal(input).setScale(2, RoundingMode.DOWN);
                }
            } catch (NumberFormatException e) {

            }
        }
        return output;
    }

    public void sendMail(String topic, String body) {
        Email email = new Email();
        email.setBody(body);
        email.setTopic(topic);
        email.setSendTo(NOTICE_TO_ADD);
        email.setSSL(true);
        EmailUtil.sendEmail(email);
    }
}
