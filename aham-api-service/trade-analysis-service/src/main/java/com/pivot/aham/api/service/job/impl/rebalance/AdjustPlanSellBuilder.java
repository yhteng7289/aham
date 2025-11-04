package com.pivot.aham.api.service.job.impl.rebalance;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.beust.jcommander.internal.Maps;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pivot.aham.api.server.dto.ModelRecommendResDTO;
import com.pivot.aham.api.service.job.wrapperbean.AnalyTpcfTncfWrapperBean;
import com.pivot.aham.api.service.job.wrapperbean.EtfBean;
import com.pivot.aham.api.service.job.wrapperbean.EtfListBean;
import com.pivot.aham.api.service.mapper.model.*;
import com.pivot.aham.api.service.service.*;
import com.pivot.aham.common.enums.analysis.BalStatusEnum;
import com.pivot.aham.common.enums.analysis.BalTradeTypeEnum;
import com.pivot.aham.common.enums.analysis.ExecuteStatusEnum;
import com.pivot.aham.api.service.support.AccountAssetStatistic;
import com.pivot.aham.api.service.support.AccountAssetStatisticBean;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.util.DateUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年03月24日
 */
@Component
@Scope(value = "prototype")
@Slf4j
@Data
public class AdjustPlanSellBuilder {

    private AccountInfoPO accountInfoPO;
    private ModelRecommendResDTO modelRecommendDTO;
    private ReBalanceTriggerResult triggerResult;

    @Autowired
    private AssetFundNavService assetFundNavService;
    @Autowired
    private AccountAssetService accountAssetService;
    @Autowired
    private AccountRechargeService accountRechargeService;
    @Autowired
    private AccountRedeemService accountRedeemService;
    @Autowired
    private AssetFundNavService accountNavService;
    @Resource
    private AdjustPlanSellBuilderSupport adjustPlanSellBuilderSupport;
    @Autowired
    private AccountBalanceHisRecordService accountBalanceHisRecordService;
    @Resource
    private TpcfTncfService tpcfTncfService;

    public List<AccountBalanceAdjDetail> build(AnalyTpcfTncfWrapperBean tncfWrapperBean,
                                               AnalyTpcfTncfWrapperBean tpcfWrapperBean) {
        if (accountInfoPO == null) {
            throw new BusinessException("请先设置账户信息");
        }
        //查询充值流水
//        List<AccountRechargePO> accountRechargePOs = tpcfTncfService.getAccountTpcf(accountInfoPO.getId());
        BigDecimal tpcf = tpcfWrapperBean.getTpcf();
        BigDecimal tncf = tncfWrapperBean.getTncf();

        //根据navtime获取昨日账户资产
        AccountFundNavPO accountNavQuery = new AccountFundNavPO();
        accountNavQuery.setAccountId(accountInfoPO.getId());
        accountNavQuery.setNavTime(DateUtils.addDateByDay(DateUtils.now(), -1));
        AccountFundNavPO accountNav = accountNavService.selectOneByNavTime(accountNavQuery);
        if (accountNav == null) {
            log.error("账户{},找不到对应账户基金净值记录", accountInfoPO.getId());
            throw new BusinessException("账户" + accountInfoPO.getId() + ",找不到对应账户基金净值记录");
        }

        BigDecimal currentAsset = accountNav.getTotalAsset().add(tpcf).subtract(tncf);
        log.info("账户{},currentAsset{},tpcf{},tncf{}", accountInfoPO.getId(), currentAsset, tpcf, tncf);
        //获取账户当前资产明细
        Map<String, AccountAssetStatisticBean> allCurrentHoldMoneys = genAssetHoldMoney();
        log.info("当前资产详情:{}", JSON.toJSONString(allCurrentHoldMoneys));
        //目标资产明细
        Map<String, BigDecimal> targetHoldMoneys = genTargetMoney(currentAsset);
        log.info("目标资产详情:{}", JSON.toJSONString(targetHoldMoneys));

        //方案制作
        AccountBalanceRecord accountBalanceRecord = new AccountBalanceRecord();
        accountBalanceRecord.setAccountId(accountInfoPO.getId());
        accountBalanceRecord.setBalStartTime(DateUtils.now());
        accountBalanceRecord.setBalStatus(BalStatusEnum.HANDLING);
        accountBalanceRecord.setBalTimeDiff(triggerResult.getBalTimeDiff());
        accountBalanceRecord.setEtfDiff(triggerResult.getEtfDiff());
        accountBalanceRecord.setModelRecommendId(modelRecommendDTO.getId());
        accountBalanceRecord.setPortfolioScore(modelRecommendDTO.getScore());
        accountBalanceRecord.setXValue(triggerResult.getXValue());
        accountBalanceRecord.setPortfolioId(modelRecommendDTO.getPortfolioId());
        adjustPlanSellBuilderSupport.setAccountBalanceRecord(accountBalanceRecord);

        List<AccountBalanceAdjDetail> accountBalanceAdjDetailSellList = Lists.newArrayList();
        List<AccountBalanceAdjDetail> accountBalanceAdjDetailBuyList = Lists.newArrayList();
        List<AccountBalanceAdjDetail> accountBalanceAdjDetailSellCash = Lists.newArrayList();
        List<AccountBalanceAdjDetail> accountBalanceAdjDetailSellNotCash = Lists.newArrayList();
        //求current和target的并集
        Set<String> allEtf = Sets.union(allCurrentHoldMoneys.keySet(), targetHoldMoneys.keySet());

        for (String etfKey : allEtf) {
            if (etfKey.equals(Constants.MAIN_CASH) || etfKey.equals(Constants.SUB_CASH) || etfKey.equals(Constants.UN_BUY_PRODUCT_CODE)) {
                continue;
            }

            AccountAssetStatisticBean accountAssetStatisticBean = allCurrentHoldMoneys.get(etfKey);
            BigDecimal currentMoney = BigDecimal.ZERO;
            BigDecimal targetMoney = targetHoldMoneys.get(etfKey);
            if (targetMoney == null) {
                targetMoney = BigDecimal.ZERO;
            }
            if (accountAssetStatisticBean != null) {
                currentMoney = accountAssetStatisticBean.getProductMoney();
            }
            BigDecimal diffMoney = targetMoney.subtract(currentMoney);

            AccountBalanceAdjDetail accountBalanceAdjDetail = new AccountBalanceAdjDetail();

            //卖出
            if (diffMoney.compareTo(BigDecimal.ZERO) < 0) {
                accountBalanceAdjDetail.setCurrentHold(currentMoney);
                accountBalanceAdjDetail.setExecuteStatus(ExecuteStatusEnum.CREATE);
                accountBalanceAdjDetail.setProductCode(etfKey);
                accountBalanceAdjDetail.setTargetHold(targetMoney);
                accountBalanceAdjDetail.setTradeAmount(diffMoney.abs());
                accountBalanceAdjDetail.setTradeType(BalTradeTypeEnum.SELL);
                accountBalanceAdjDetail.setTmpOrderId(0L);
                accountBalanceAdjDetail.setCorrectTargetHold(BigDecimal.ZERO);
                accountBalanceAdjDetailSellList.add(accountBalanceAdjDetail);
                if (etfKey.equals(Constants.CASH)) {
                    accountBalanceAdjDetailSellCash.add(accountBalanceAdjDetail);
                } else {
                    accountBalanceAdjDetailSellNotCash.add(accountBalanceAdjDetail);
                }
            } else if (diffMoney.compareTo(BigDecimal.ZERO) > 0) {
                accountBalanceAdjDetail.setCurrentHold(currentMoney);
                accountBalanceAdjDetail.setExecuteStatus(ExecuteStatusEnum.CREATE);
                accountBalanceAdjDetail.setProductCode(etfKey);
                accountBalanceAdjDetail.setTargetHold(targetMoney);
                accountBalanceAdjDetail.setTradeAmount(diffMoney.abs());
                accountBalanceAdjDetail.setTradeType(BalTradeTypeEnum.BUY);
                accountBalanceAdjDetail.setTmpOrderId(0L);
                accountBalanceAdjDetail.setCorrectTargetHold(BigDecimal.ZERO);
                accountBalanceAdjDetailBuyList.add(accountBalanceAdjDetail);
            }

        }
        //如果只有买，没有卖，直接下买单
        List<AccountBalanceAdjDetail> accountBalanceAdjDetailList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(accountBalanceAdjDetailSellNotCash)) {
            adjustPlanSellBuilderSupport.setAccountBalanceAdjDetailList(accountBalanceAdjDetailBuyList);
            accountBalanceAdjDetailList = accountBalanceAdjDetailBuyList;
            accountBalanceAdjDetailList.addAll(accountBalanceAdjDetailSellCash);
        } else {
            adjustPlanSellBuilderSupport.setAccountBalanceAdjDetailList(accountBalanceAdjDetailSellList);
            accountBalanceAdjDetailList = accountBalanceAdjDetailSellList;
        }
        AccountBalanceHisRecord accountBalanceHisRecord = new AccountBalanceHisRecord();
        accountBalanceHisRecord.setAccountId(accountInfoPO.getId());
        AccountBalanceHisRecord accountHisBalance = accountBalanceHisRecordService.selectOne(accountBalanceHisRecord);

        accountHisBalance.setLastBalTime(DateUtils.now());
        accountHisBalance.setLastProductWeight(modelRecommendDTO.getProductWeight());
        accountHisBalance.setPortfolioScore(modelRecommendDTO.getScore());
        adjustPlanSellBuilderSupport.setAccountBalanceHisRecord(accountHisBalance);

        adjustPlanSellBuilderSupport.genAdj();

        return accountBalanceAdjDetailList;
    }

    private Map<String, BigDecimal> genTargetMoney(BigDecimal currentAsset) {
        String currentWeight = modelRecommendDTO.getProductWeight();
        EtfListBean etfListBean = JSON.parseObject(currentWeight, new TypeReference<EtfListBean>() {
        });
        List<EtfBean> mainEtf = etfListBean.getMainEtf();

        Map<String, BigDecimal> targetProductMoneys = Maps.newHashMap();
        for (EtfBean etfBean : mainEtf) {
            BigDecimal targetMoney = currentAsset.multiply(etfBean.getWeight());

            if (etfBean.getEtf().equals(Constants.MAIN_CASH)) {
                targetProductMoneys.put(Constants.CASH, targetMoney);
            } else {
                targetProductMoneys.put(etfBean.getEtf(), targetMoney);
            }

        }
        return targetProductMoneys;
    }

    private Map<String, AccountAssetStatisticBean> genAssetHoldMoney() {
        AccountAssetPO assetQuery = new AccountAssetPO();
        assetQuery.setAccountId(accountInfoPO.getId());
        List<AccountAssetPO> accountAssetList = accountAssetService.queryList(assetQuery);
        log.info("账户{},统计前资产明细{}", accountInfoPO.getId(), JSON.toJSON(accountAssetList));
        //昨日收盘价
        Date closePriceYesterday = DateUtils.addDateByDay(DateUtils.now(), -2);
        Map<String, BigDecimal> etfClosingPriceMap = assetFundNavService.getEtfClosingPrice(closePriceYesterday);
        log.info("date:{},etf收市价:{}=======", closePriceYesterday, JSON.toJSON(etfClosingPriceMap));
        if (etfClosingPriceMap == null) {
            throw new BusinessException("昨日收市价为空");
        }
        List<AccountAssetStatisticBean> accountAssetStatisticBeans = AccountAssetStatistic.statAccountAsset(accountAssetList, etfClosingPriceMap);
        log.info("账户{},统计后资产明细{}", accountInfoPO.getId(), JSON.toJSON(accountAssetStatisticBeans));
        if (CollectionUtils.isEmpty(accountAssetStatisticBeans)) {
            log.info("账户" + accountInfoPO.getId() + "该投资账户无持有中资产");
            throw new BusinessException("账户" + accountInfoPO.getId() + "该投资账户无持有中资产");
        }
        //当前资产比例
        if (CollectionUtils.isEmpty(accountAssetStatisticBeans)) {
            log.info("账户" + accountInfoPO.getId() + "该投资账户无持有中资产");
            throw new BusinessException("账户" + accountInfoPO.getId() + "该投资账户无持有中资产");
        }
        Map<String, AccountAssetStatisticBean> allHoldMultimap = Maps.newHashMap();
        for (AccountAssetStatisticBean accountAssetStatisticBean : accountAssetStatisticBeans) {
            allHoldMultimap.put(accountAssetStatisticBean.getProductCode(), accountAssetStatisticBean);
        }
        return allHoldMultimap;
    }

}
