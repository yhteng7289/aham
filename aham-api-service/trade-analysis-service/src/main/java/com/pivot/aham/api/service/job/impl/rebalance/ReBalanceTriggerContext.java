package com.pivot.aham.api.service.job.impl.rebalance;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.pivot.aham.api.server.dto.ModelRecommendResDTO;
import com.pivot.aham.api.service.job.wrapperbean.EtfBean;
import com.pivot.aham.api.service.job.wrapperbean.EtfListBean;
import com.pivot.aham.api.service.mapper.model.AccountBalanceHisRecord;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * 策略环境变量
 *
 * @author addison
 * @since 2019年03月22日
 */
@Data
public class ReBalanceTriggerContext {

    private ModelRecommendResDTO modelRecommendResDTO;
    private AccountBalanceHisRecord accountBalanceHisRecord;
    private ReBalanceTriggerStrategy reBalanceTriggerStrategy;

    public ReBalanceTriggerContext(ReBalanceTriggerStrategy reBalanceTriggerStrategy) {
        this.reBalanceTriggerStrategy = reBalanceTriggerStrategy;
    }

    public static BigDecimal calEtfDiff(String lastProductWeight, String productWeight, Set<String> diffEtfSet) {

        EtfListBean lastEtfListBean = JSON.parseObject(lastProductWeight, new TypeReference<EtfListBean>() {
        });
        List<EtfBean> lastMainEtf = lastEtfListBean.getMainEtf();
        //List<EtfBean> lastSubEtf = lastEtfListBean.getSubEtf();

        EtfListBean etfListBean = JSON.parseObject(productWeight, new TypeReference<EtfListBean>() {
        });
        List<EtfBean> mainEtf = etfListBean.getMainEtf();
        //List<EtfBean> subEtf = etfListBean.getSubEtf();

        BigDecimal diffEtf = BigDecimal.ZERO;
        diffEtf = calDiff(diffEtfSet, lastMainEtf, mainEtf, diffEtf);
        //diffEtf = calDiff(diffEtfSet, lastSubEtf, subEtf, diffEtf);
        return diffEtf;
    }

    private static BigDecimal calDiff(Set<String> diffEtfSet,
            List<EtfBean> lastMainEtf,
            List<EtfBean> mainEtf,
            BigDecimal diffEtf) {
        for (EtfBean mainEtfBean : lastMainEtf) {
            BigDecimal diff = BigDecimal.ZERO;
            if (!diffEtfSet.contains(mainEtfBean.getEtf())) {
                continue;
            }
            for (EtfBean etfBean : mainEtf) {
                if (!etfBean.getEtf().equals(mainEtfBean.getEtf())) {
                    continue;
                }
                diff = etfBean.getWeight().subtract(mainEtfBean.getWeight()).abs();
                diff = diff.divide(new BigDecimal(2.0));
            }
            diffEtf = diffEtf.add(diff);
        }
        return diffEtf;
    }

    public ReBalanceTriggerResult executeStrategy() {
        return reBalanceTriggerStrategy.checkRebalance(this);
    }
}
