package com.pivot.aham.api.service.support;

import com.google.common.collect.Lists;
import com.pivot.aham.api.service.mapper.model.AccountAssetPO;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.support.collection.IMultiTable;
import com.pivot.aham.common.core.support.collection.MultiTableFactory;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.enums.ProductAssetStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月18日
 */
@Slf4j
public class AccountAssetStatistic {

    /**
     * 日志记录器
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountAssetStatistic.class);

    /**
     * 统计账户资产 维度：productCode:productStatus:(shares or money)
     *
     * @param accountAssetList
     * @return
     */
    public static List<AccountAssetStatisticBean> statAccountAsset(List<AccountAssetPO> accountAssetList, Map<String, BigDecimal> etfClosingPriceMap) {
        IMultiTable<String, ProductAssetStatusEnum, AccountAssetPO> iMultiTable = MultiTableFactory.arrayListMultiTable();
        Long accountId = null;
        for (AccountAssetPO assetPO : accountAssetList) {
            accountId = assetPO.getAccountId();
            iMultiTable.put(assetPO.getProductCode(), assetPO.getProductAssetStatus(), assetPO);
        }

        Map<String, Map<ProductAssetStatusEnum, Collection<AccountAssetPO>>> iMultiMap = iMultiTable.rowMap();

        List<AccountAssetStatisticBean> accountAssetStatList = Lists.newArrayList();
        for (Map.Entry<String, Map<ProductAssetStatusEnum, Collection<AccountAssetPO>>> entry : iMultiMap.entrySet()) {
            List<AccountAssetStatisticBean> tmpAssetList = Lists.newArrayList();

            Map<ProductAssetStatusEnum, Collection<AccountAssetPO>> valueMap = entry.getValue();
            log.info("valueMap {} ", valueMap);

            //买入中金额
            Collection<AccountAssetPO> buyIngAsset = valueMap.get(ProductAssetStatusEnum.BUY_ING);
            BigDecimal buyIngMoney = BigDecimal.ZERO;
            if (CollectionUtils.isNotEmpty(buyIngAsset)) {
                for (AccountAssetPO buyIng : buyIngAsset) {
                    buyIngMoney = buyIngMoney.add(buyIng.getApplyMoney());
                }
            }

            //已卖出金额和份额
            Collection<AccountAssetPO> confirmSellAsset = valueMap.get(ProductAssetStatusEnum.CONFIRM_SELL);
            BigDecimal confirmSellShares = BigDecimal.ZERO;
            BigDecimal confirmSellMoney = BigDecimal.ZERO;
            if (CollectionUtils.isNotEmpty(confirmSellAsset)) {
                for (AccountAssetPO confirmSell : confirmSellAsset) {
                    confirmSellMoney = confirmSellMoney.add(confirmSell.getConfirmMoney());
                    confirmSellShares = confirmSellShares.add(confirmSell.getConfirmShare());
                }

//                if(entry.getKey().equals("cash")) {
//                    List<AccountAssetPO> accountAssetPOList = Lists.newArrayList();
//                    for (AccountAssetPO accountAssetPO : accountAssetList) {
//                        if (accountAssetPO.getProductAssetStatus() == ProductAssetStatusEnum.CONFIRM_SELL && accountAssetPO.getProductCode().equals("cash")) {
//                            accountAssetPOList.add(accountAssetPO);
//                        }
//                    }
//                    Set<Long> s1 = Sets.newHashSet();
//                    for(AccountAssetPO accountAssetPO:confirmSellAsset){
//                        s1.add(accountAssetPO.getId());
//                    }
//                    Set<Long> s2 = Sets.newHashSet();
//                    for(AccountAssetPO accountAssetPO:accountAssetPOList){
//                        s2.add(accountAssetPO.getId());
//                    }
//
//                    Set<Long> setI =  Sets.difference(s2,s1);
//
//                }
            }

            //卖出中金额
            Collection<AccountAssetPO> sellIngAsset = valueMap.get(ProductAssetStatusEnum.SELL_ING);
            BigDecimal sellIngMoney = BigDecimal.ZERO;
            if (CollectionUtils.isNotEmpty(sellIngAsset)) {
                for (AccountAssetPO sellIng : sellIngAsset) {
                    sellIngMoney = sellIngMoney.add(sellIng.getApplyMoney());
                }
            }

            //转换中金额
            Collection<AccountAssetPO> convertIngAsset = valueMap.get(ProductAssetStatusEnum.CONVERT_ING);
            BigDecimal convertMoney = BigDecimal.ZERO;
            if (CollectionUtils.isNotEmpty(convertIngAsset)) {
                for (AccountAssetPO convertIng : convertIngAsset) {
                    convertMoney = convertMoney.add(convertIng.getApplyMoney());
                }
            }

            //持有中份额和金额
            
            Collection<AccountAssetPO> holdIngAsset = valueMap.get(ProductAssetStatusEnum.HOLD_ING);
            log.info("holdIngAsset {} " , holdIngAsset);
            BigDecimal holdShares = BigDecimal.ZERO;
            BigDecimal holdMoney = BigDecimal.ZERO;
            for (AccountAssetPO holdIng : holdIngAsset) {
                holdShares = holdShares.add(holdIng.getConfirmShare());
                holdMoney = holdMoney.add(holdIng.getConfirmMoney());
            }

            BigDecimal surHoldShares;
            BigDecimal surHoldMoney;

            if (entry.getKey().equals(Constants.CASH) || entry.getKey().equals(Constants.UN_BUY_PRODUCT_CODE)) {
                //可用金额=持有中金额-转换中金额-卖出中金额
                surHoldShares = BigDecimal.ZERO;
                //surHoldMoney = holdMoney.subtract(convertMoney).subtract(sellIngMoney).subtract(confirmSellMoney);
                surHoldMoney = holdMoney.subtract(convertMoney).subtract(confirmSellMoney);
                log.info("entry.getKey() {} , accountId : {} surHoldMoney {} ", entry.getKey(), accountId, surHoldMoney);
            } else {
                //可用金额=(持有中份额-赎回确认的份额)*closePrice-转换中金额-卖出中金额
                surHoldShares = holdShares.subtract(confirmSellShares);
                BigDecimal closePrice = etfClosingPriceMap.get(entry.getKey());
                if (closePrice == null) {
                    throw new BusinessException("产品:" + entry.getKey() + ",收市价为空");
                }
                surHoldMoney = surHoldShares.multiply(closePrice).subtract(convertMoney);
                //surHoldMoney = surHoldShares.multiply(closePrice).subtract(convertMoney).subtract(sellIngMoney);
                log.info("entry.getKey() {} , accountId : {} surHoldMoney {} ", entry.getKey(), accountId, surHoldMoney);
            }

            if (surHoldShares.compareTo(BigDecimal.ZERO) > 0
                    || ((entry.getKey().equals(Constants.CASH) || entry.getKey().equals(Constants.UN_BUY_PRODUCT_CODE)) && surHoldMoney.compareTo(BigDecimal.ZERO) > 0)) {
                AccountAssetStatisticBean accountAssetStatisticBean = new AccountAssetStatisticBean();
                accountAssetStatisticBean.setAccountId(accountId);
                accountAssetStatisticBean.setProductCode(entry.getKey());
                accountAssetStatisticBean.setProductMoney(surHoldMoney);
                accountAssetStatisticBean.setProductShare(surHoldShares);
                accountAssetStatisticBean.setProductAssetStatus(ProductAssetStatusEnum.HOLD_ING);
                tmpAssetList.add(accountAssetStatisticBean);
            } else if (surHoldShares.compareTo(BigDecimal.ZERO) < 0 || surHoldMoney.compareTo(BigDecimal.ZERO) < 0) {
                ErrorLogAndMailUtil.logErrorByFormat(LOGGER, "账号:{},product:{}资产错误,请求检查,"
                        + "surHoldShares:{}，surHoldMoney:{},convertMoney:{},sellIngMoney:{}",
                        accountId, entry.getKey(),
                        surHoldShares, surHoldMoney, convertMoney, sellIngMoney);
                throw new BusinessException("账号:" + accountId + ",资产:" + entry.getKey() + "错误,请求检查");
            } else {
                tmpAssetList.clear();
                continue;
            }
            if (tmpAssetList.size() > 0) {
                accountAssetStatList.addAll(tmpAssetList);
            }
        }
        log.info("accountAssetStatList {} ", accountAssetStatList);
        return accountAssetStatList;
    }

    /**
     * 统计账户的etf share
     *
     * @param accountAssetPOs
     * @return
     */
    public static List<AccountAssetStatisticBean> statAccountShare(List<AccountAssetPO> accountAssetPOs) {
        IMultiTable<String, ProductAssetStatusEnum, AccountAssetPO> iMultiTable = MultiTableFactory.arrayListMultiTable();
        Long accountId = null;
        for (AccountAssetPO assetPO : accountAssetPOs) {
            accountId = assetPO.getAccountId();
            iMultiTable.put(assetPO.getProductCode(), assetPO.getProductAssetStatus(), assetPO);
        }
        Map<String, Map<ProductAssetStatusEnum, Collection<AccountAssetPO>>> iMultiMap = iMultiTable.rowMap();
        List<AccountAssetStatisticBean> accountAssetStatList = Lists.newArrayList();

        for (Map.Entry<String, Map<ProductAssetStatusEnum, Collection<AccountAssetPO>>> entry : iMultiMap.entrySet()) {
            Map<ProductAssetStatusEnum, Collection<AccountAssetPO>> valueMap = entry.getValue();

            //已卖出金份额
            Collection<AccountAssetPO> confirmSellAsset = valueMap.get(ProductAssetStatusEnum.CONFIRM_SELL);
            BigDecimal confirmSellShares = BigDecimal.ZERO;
            if (CollectionUtils.isNotEmpty(confirmSellAsset)) {
                for (AccountAssetPO confirmSell : confirmSellAsset) {
                    confirmSellShares = confirmSellShares.add(confirmSell.getConfirmShare());
                }
            }

            //持有中份额
            Collection<AccountAssetPO> holdIngAsset = valueMap.get(ProductAssetStatusEnum.HOLD_ING);
            BigDecimal holdShares = BigDecimal.ZERO;
            for (AccountAssetPO holdIng : holdIngAsset) {
                holdShares = holdShares.add(holdIng.getConfirmShare());
            }

            AccountAssetStatisticBean accountAssetStatisticBean = new AccountAssetStatisticBean();
            accountAssetStatisticBean.setAccountId(accountId);
            accountAssetStatisticBean.setProductCode(entry.getKey());
            accountAssetStatisticBean.setProductShare(holdShares.subtract(confirmSellShares));
            accountAssetStatisticBean.setProductAssetStatus(ProductAssetStatusEnum.HOLD_ING);
            accountAssetStatList.add(accountAssetStatisticBean);
        }

        return accountAssetStatList;
    }

    public static BigDecimal getAccountUnbuy(List<AccountAssetPO> accountAssetPOs) {
        BigDecimal totalUnbuy = BigDecimal.ZERO;
        for (AccountAssetPO accountAssetPO : accountAssetPOs) {
            if (ProductAssetStatusEnum.HOLD_ING == accountAssetPO.getProductAssetStatus()) {
                totalUnbuy = totalUnbuy.add(accountAssetPO.getConfirmMoney());
            }
            if (ProductAssetStatusEnum.CONFIRM_SELL == accountAssetPO.getProductAssetStatus()
                    || ProductAssetStatusEnum.SELL_ING == accountAssetPO.getProductAssetStatus()) {
                totalUnbuy = totalUnbuy.subtract(accountAssetPO.getConfirmMoney());
            }
        }
        return totalUnbuy.setScale(6, BigDecimal.ROUND_HALF_UP);
    }

}
