package com.pivot.aham.api.service.support;

import com.google.common.collect.Lists;
import com.pivot.aham.api.service.mapper.model.PivotPftAssetPO;
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
public class PftAccountAssetStatistic {
    /**
     * 日志记录器
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PftAccountAssetStatistic.class);

    /**
     * 统计Pft账户资产
     * 维度：productCode:productStatus:(shares or money)
     *
     * @param accountAssetList
     * @return
     */
    public static List<PftAccountAssetStatisticBean> statAccountAsset(List<PivotPftAssetPO> accountAssetList, Map<String, BigDecimal> etfClosingPriceMap) {
        IMultiTable<String, ProductAssetStatusEnum, PivotPftAssetPO> iMultiTable = MultiTableFactory.arrayListMultiTable();
        for (PivotPftAssetPO assetPO : accountAssetList) {
            iMultiTable.put(assetPO.getProductCode(), assetPO.getProductAssetStatus(), assetPO);
        }


        Map<String, Map<ProductAssetStatusEnum, Collection<PivotPftAssetPO>>> iMultiMap = iMultiTable.rowMap();

        List<PftAccountAssetStatisticBean> accountAssetStatList = Lists.newArrayList();
        for (Map.Entry<String, Map<ProductAssetStatusEnum, Collection<PivotPftAssetPO>>> entry : iMultiMap.entrySet()) {
            List<PftAccountAssetStatisticBean> tmpAssetList = Lists.newArrayList();

            Map<ProductAssetStatusEnum, Collection<PivotPftAssetPO>> valueMap = entry.getValue();

            //买入中金额
            Collection<PivotPftAssetPO> buyIngAsset = valueMap.get(ProductAssetStatusEnum.BUY_ING);
            BigDecimal buyIngMoney = BigDecimal.ZERO;
            if (CollectionUtils.isNotEmpty(buyIngAsset)) {
                for (PivotPftAssetPO buyIng : buyIngAsset) {
                    buyIngMoney = buyIngMoney.add(buyIng.getConfirmMoney());
                }
            }

            //已卖出金额和份额
            Collection<PivotPftAssetPO> confirmSellAsset = valueMap.get(ProductAssetStatusEnum.CONFIRM_SELL);
            BigDecimal confirmSellShares = BigDecimal.ZERO;
            BigDecimal confirmSellMoney = BigDecimal.ZERO;
            if (CollectionUtils.isNotEmpty(confirmSellAsset)) {
                for (PivotPftAssetPO confirmSell : confirmSellAsset) {
                    confirmSellMoney = confirmSellMoney.add(confirmSell.getConfirmMoney());
                    confirmSellShares = confirmSellShares.add(confirmSell.getConfirmShare());
                }
            }

            //卖出中金额
            Collection<PivotPftAssetPO> sellIngAsset = valueMap.get(ProductAssetStatusEnum.SELL_ING);
            BigDecimal sellIngMoney = BigDecimal.ZERO;
            if (CollectionUtils.isNotEmpty(sellIngAsset)) {
                for (PivotPftAssetPO sellIng : sellIngAsset) {
                    sellIngMoney = sellIngMoney.add(sellIng.getConfirmMoney());
                }
            }

            //转换中金额
            Collection<PivotPftAssetPO> convertIngAsset = valueMap.get(ProductAssetStatusEnum.CONVERT_ING);
            BigDecimal convertMoney = BigDecimal.ZERO;
            if (CollectionUtils.isNotEmpty(convertIngAsset)) {
                for (PivotPftAssetPO convertIng : convertIngAsset) {
                    convertMoney = convertMoney.add(convertIng.getConfirmMoney());
                }
            }

            //持有中份额和金额
            Collection<PivotPftAssetPO> holdIngAsset = valueMap.get(ProductAssetStatusEnum.HOLD_ING);
            BigDecimal holdShares = BigDecimal.ZERO;
            BigDecimal holdMoney = BigDecimal.ZERO;
            if (CollectionUtils.isNotEmpty(holdIngAsset)) {
                for (PivotPftAssetPO holdIng : holdIngAsset) {
                    holdShares = holdShares.add(holdIng.getConfirmShare());
                    holdMoney = holdMoney.add(holdIng.getConfirmMoney());
                }
            }


            BigDecimal surHoldShares;
            BigDecimal surHoldMoney;
            if (entry.getKey().equals(Constants.CASH) || entry.getKey().equals(Constants.UN_BUY_PRODUCT_CODE)) {
                //可用金额=持有中金额-转换中金额-卖出中金额
                surHoldShares = BigDecimal.ZERO;
                surHoldMoney = holdMoney.subtract(convertMoney).subtract(sellIngMoney).subtract(confirmSellMoney);
            } else {
                //可用金额=(持有中份额-赎回确认的份额)*closePrice-转换中金额-卖出中金额
                surHoldShares = holdShares.subtract(confirmSellShares);
                BigDecimal closePrice = etfClosingPriceMap.get(entry.getKey());
                if (closePrice == null) {
                    throw new BusinessException("产品:" + entry.getKey() + ",收市价为空");
                }
                surHoldMoney = surHoldShares.multiply(closePrice).subtract(convertMoney).subtract(sellIngMoney);
            }


            if (surHoldShares.compareTo(BigDecimal.ZERO) > 0 ||
                    ((entry.getKey().equals(Constants.CASH) || entry.getKey().equals(Constants.UN_BUY_PRODUCT_CODE)) && surHoldMoney.compareTo(BigDecimal.ZERO) > 0)) {
                PftAccountAssetStatisticBean PftAccountAssetStatisticBean = new PftAccountAssetStatisticBean();
//                PftAccountAssetStatisticBean.setAccountId(accountId);
                PftAccountAssetStatisticBean.setProductCode(entry.getKey());
                PftAccountAssetStatisticBean.setProductMoney(surHoldMoney);
                PftAccountAssetStatisticBean.setProductShare(surHoldShares);
                PftAccountAssetStatisticBean.setProductAssetStatus(ProductAssetStatusEnum.HOLD_ING);
                tmpAssetList.add(PftAccountAssetStatisticBean);
            } else if (surHoldShares.compareTo(BigDecimal.ZERO) < 0 || surHoldMoney.compareTo(BigDecimal.ZERO) < 0) {
                ErrorLogAndMailUtil.logErrorByFormat(LOGGER,"PFT资产错误,请求检查,surHoldShares:{}，surHoldMoney:{},convertMoney:{},sellIngMoney:{}", surHoldShares, surHoldMoney, convertMoney, sellIngMoney);
                throw new BusinessException("PFT账号资产:" + entry.getKey() + "错误,请求检查");
            } else {
                tmpAssetList.clear();
                continue;
            }
            if (tmpAssetList.size() > 0) {
                accountAssetStatList.addAll(tmpAssetList);
            }
        }
        return accountAssetStatList;
    }



}
