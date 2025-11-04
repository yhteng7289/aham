package com.pivot.aham.api.service.populator;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.pivot.aham.api.service.client.rest.resp.ModelPortfolioDetailResp;
import com.pivot.aham.api.service.mapper.model.ModelRecommend;
import com.pivot.aham.api.service.service.bean.EtfBean;
import com.pivot.aham.api.service.service.bean.EtfListBean;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.AgeLevelEnum;
import com.pivot.aham.common.enums.ModelStatusEnum;
import com.pivot.aham.common.enums.PoolingEnum;
import com.pivot.aham.common.enums.RiskLevelEnum;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;

public class ModelRecommendPopulator {

    public static void populateModelRecommendFromModelPortfolio(ModelRecommend modelRecommend, List<ModelPortfolioDetailResp> modelPortfolioDetailResps){
        EtfListBean etfListBean = new EtfListBean();
        List<EtfBean> mainEtfBeanList = Lists.newArrayList();
        ModelPortfolioDetailResp firstModelPortfolio = modelPortfolioDetailResps.get(0);
        for (ModelPortfolioDetailResp portfolioDetail: modelPortfolioDetailResps){

            EtfBean etfBean = new EtfBean();
            etfBean.setEtf(portfolioDetail.getScheme());
            if (StringUtils.isEmpty(portfolioDetail.getValidTo()) || DateUtils.now().before(DateUtils.parseDate(portfolioDetail.getValidTo()))){
                etfBean.setWeight(portfolioDetail.getWeightage().divide(BigDecimal.valueOf(100),4, BigDecimal.ROUND_DOWN));
            } else {
                etfBean.setWeight(new BigDecimal("0.0000"));
            }
            mainEtfBeanList.add(etfBean);
        }
        etfListBean.setMainEtf(mainEtfBeanList);

        modelRecommend.setPortfolioId(Integer.toString(firstModelPortfolio.getId()));
        modelRecommend.setModelTime(DateUtils.now());
        modelRecommend.setModelStatus(ModelStatusEnum.Effective);
        modelRecommend.setRisk(RiskLevelEnum.DEFAULT);
        modelRecommend.setAge(AgeLevelEnum.LEVEL_0);
        if(firstModelPortfolio.getScore() != null){
            modelRecommend.setScore(firstModelPortfolio.getScore());
        }else{
             modelRecommend.setScore(BigDecimal.ZERO);
        }
        modelRecommend.setPool(PoolingEnum.P1);
        modelRecommend.setProductWeight(JSON.toJSONString(etfListBean));
        modelRecommend.setClassfiyWeight("NONE");
        modelRecommend.setId(Sequence.next());
        modelRecommend.setCreateTime(DateUtils.now());
        modelRecommend.setUpdateTime(DateUtils.now());

    }
}