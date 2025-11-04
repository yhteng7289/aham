package com.pivot.aham.api.service.job.impl.rebalance;

import com.google.common.collect.Sets;
import com.pivot.aham.api.server.dto.ModelRecommendResDTO;
import com.pivot.aham.api.service.mapper.model.AccountBalanceHisRecord;
import com.pivot.aham.common.core.util.DateUtils;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Set;

/**
 * pool1检查策略
 *
 * @author addison
 * @since 2019年03月22日
 */
@Slf4j
public class PoolAhamTriggerStrategy implements ReBalanceTriggerStrategy {

    @Override
    public ReBalanceTriggerResult checkRebalance(ReBalanceTriggerContext triggerContext) {
        ReBalanceTriggerResult triggerResult = new ReBalanceTriggerResult();
        ModelRecommendResDTO modelRecommendResDTO = triggerContext.getModelRecommendResDTO();
        AccountBalanceHisRecord accountBalanceHisRecord = triggerContext.getAccountBalanceHisRecord();

        BigDecimal modelScore = modelRecommendResDTO.getScore();
        Boolean vooTenDays = modelRecommendResDTO.getVooTenDays();

        /*long adjt;
        if(modelScore.compareTo(new BigDecimal("-2")) < 0) {
            adjt = 30;
        } else if(modelScore.compareTo(new BigDecimal("-2")) >= 0 && modelScore.compareTo(new BigDecimal("3")) <= 0) {
            adjt = 45;
        } else {
            adjt = 60;
        }
        if(vooTenDays){
            adjt=30;
        }

        //计算模型偏差
        String lastProductWeight = accountBalanceHisRecord.getLastProductWeight();
        String productWeight = modelRecommendResDTO.getProductWeight();
        Set<String> diffEtfSet = Sets.newHashSet("GLD","VWO","VEA","VTI","QQQ","VNQ");
        BigDecimal diffEtf = ReBalanceTriggerContext.calEtfDiff(lastProductWeight, productWeight, diffEtfSet);

        //计算X值
        BigDecimal xValue = new BigDecimal("0.15");
        BigDecimal risk = new BigDecimal(modelRecommendResDTO.getRisk().getValue());
        BigDecimal age = new BigDecimal(modelRecommendResDTO.getAge().getValue());
        if(risk.compareTo(new BigDecimal("5"))<0){
            xValue = xValue.add(risk.subtract(new BigDecimal(5)).multiply(new BigDecimal("0.015")));
        }
        if(age.compareTo(new BigDecimal("1"))>0){
            xValue = xValue.subtract(age.subtract(new BigDecimal(1)).multiply(new BigDecimal("0.01")));

        }
        if(xValue.compareTo(new BigDecimal("0.075"))<0){
            xValue = new BigDecimal("0.075");
        }
        //计算时间差
        long pastDays = DateUtils.pastDays(accountBalanceHisRecord.getLastBalTime());
        //满足触发调仓
        triggerResult.setIfAdj(false);
        if(pastDays>adjt && diffEtf.compareTo(xValue)>0){
            //按调仓方式进行下单
            triggerResult.setIfAdj(true);

        }*/
        triggerResult.setIfAdj(true);
        triggerResult.setBalTimeDiff(Long.parseLong("0"));
        triggerResult.setEtfDiff(BigDecimal.ZERO);
        triggerResult.setXValue(BigDecimal.ZERO);
        triggerResult.setAdjt(Long.parseLong("0"));
        return triggerResult;
    }

}
