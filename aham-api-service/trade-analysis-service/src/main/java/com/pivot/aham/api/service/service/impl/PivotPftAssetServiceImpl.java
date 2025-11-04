package com.pivot.aham.api.service.service.impl;

import com.google.common.collect.Lists;
import com.pivot.aham.api.service.mapper.PivotPftAssetMapper;
import com.pivot.aham.api.service.mapper.model.PivotPftAccountPO;
import com.pivot.aham.api.service.mapper.model.PivotPftAssetPO;
import com.pivot.aham.api.service.service.AssetFundNavService;
import com.pivot.aham.api.service.service.PivotPftAccountService;
import com.pivot.aham.api.service.service.PivotPftAssetService;
import com.pivot.aham.api.service.support.PftAccountAssetStatistic;
import com.pivot.aham.api.service.support.PftAccountAssetStatisticBean;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import com.pivot.aham.common.core.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PivotPftAssetServiceImpl extends BaseServiceImpl<PivotPftAssetPO, PivotPftAssetMapper> implements PivotPftAssetService {

    @Resource
    private AssetFundNavService assetFundNavService;
    @Resource
    private PivotPftAccountService pivotPftAccountService;

    @Override
    public void updateOrInsertPivotPftAsset(PivotPftAssetPO pivotPftAssetPO) {
        this.updateOrInsert(pivotPftAssetPO);

        List<PivotPftAccountPO> pivotPftAccountPOList = pivotPftAccountService.queryList(new PivotPftAccountPO());

        PivotPftAssetPO pivotPftAssetQuery = new PivotPftAssetPO();
        List<PivotPftAssetPO> pivotPftAssetList = this.queryList(pivotPftAssetQuery);

        Date yesterday = DateUtils.addDateByDay(new Date(), -2);
        Map<String, BigDecimal> etfClosingPriceMap = assetFundNavService.getEtfClosingPrice(yesterday);
        log.info("{},收市价:{}", yesterday, etfClosingPriceMap);
        List<PftAccountAssetStatisticBean> pftAccountAssetStatisticBeans
                = PftAccountAssetStatistic.statAccountAsset(pivotPftAssetList, etfClosingPriceMap);

        //更新pft账号
        List<PivotPftAccountPO> pivotPftAccountPOS = Lists.newArrayList();
        for (PftAccountAssetStatisticBean pftAccountAssetStatisticBean : pftAccountAssetStatisticBeans) {
            PivotPftAccountPO pivotPftAccountPO = new PivotPftAccountPO();
            pivotPftAccountPO.setMoney(pftAccountAssetStatisticBean.getProductMoney());
            pivotPftAccountPO.setProductCode(pftAccountAssetStatisticBean.getProductCode());
            pivotPftAccountPO.setShare(pftAccountAssetStatisticBean.getProductShare());
            pivotPftAccountPOS.add(pivotPftAccountPO);
        }
        if (CollectionUtils.isNotEmpty(pivotPftAccountPOS)) {
            //查询所有的id
//            List<PivotPftAccountPO> pivotPftAccountPOList = pivotPftAccountService.queryList(new PivotPftAccountPO());
            List<Long> ids = Lists.newArrayList();
//            Long dataVersion = null;
            for (PivotPftAccountPO pivotPftAccount : pivotPftAccountPOList) {
//                if(dataVersion == null){
//                    dataVersion = pivotPftAccount.getDataVersion();
//                }
                ids.add(pivotPftAccount.getId());
            }
            pivotPftAccountService.updateAccount(ids, pivotPftAccountPOS);
        }

    }

    @Override
    public List<PivotPftAssetPO> queryListByTime(PivotPftAssetPO pivotPftAssetPO) {
        return mapper.queryListByTime(pivotPftAssetPO);
    }
}
