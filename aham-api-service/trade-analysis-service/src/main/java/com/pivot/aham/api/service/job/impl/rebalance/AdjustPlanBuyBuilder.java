package com.pivot.aham.api.service.job.impl.rebalance;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.beust.jcommander.internal.Maps;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pivot.aham.api.server.dto.ModelRecommendResDTO;
import com.pivot.aham.api.server.dto.req.ReCalBuyEtfInBalReqDTO;
import com.pivot.aham.api.server.remoteservice.ModelServiceRemoteService;
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
import com.pivot.aham.common.enums.ProductAssetStatusEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 调仓方案买单构造器
 *
 * @author addison
 * @since 2019年03月24日
 */
@Component
@Scope(value = "prototype")
@Slf4j
@Data
public class AdjustPlanBuyBuilder {

    private Long accountId;
    private Long balId;
    private ModelRecommendResDTO modelRecommendDTO;
    private List<ReCalBuyEtfInBalReqDTO> reCalBuyEtfInBalReqDTOList;
    private AccountBalanceRecord accountBalanceRecord;
    @Resource
    private AccountBalanceExecute accountBalanceExecute;
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
    @Autowired
    private AccountBalanceRecordService accountBalanceRecordService;
    @Autowired
    private AccountBalanceAdjDetailService accountBalanceAdjDetailService;
    @Resource
    private ModelServiceRemoteService modelServiceRemoteService;
    @Resource
    private AdjustPlanBuyBuilderSupport adjustPlanBuyBuilderSupport;
    @Resource
    private TpcfTncfService tpcfTncfService;

    /**
     * 状态初始化
     */
    private void init() {
        //获取balid

    }

    /**
     * 构造方案详情
     *
     * @return
     */
    public List<AccountBalanceAdjDetail> build() {
        if (CollectionUtils.isEmpty(reCalBuyEtfInBalReqDTOList)) {
            throw new BusinessException("请先设置卖单结果");
        }

        List balIdList = new ArrayList();
        List<AccountBalanceAdjDetail> _accountBalanceAdjDetailList = Lists.newArrayList();

        // Assuming 27 records here
        for (int i = 0; i < reCalBuyEtfInBalReqDTOList.size(); i++) {
            List<AccountBalanceAdjDetail> accountBalanceAdjDetailList = Lists.newArrayList();

            ReCalBuyEtfInBalReqDTO reCalBuyEtfInBalReq = reCalBuyEtfInBalReqDTOList.get(i);
            AccountBalanceAdjDetail accountBalanceAdjDetailQuery = new AccountBalanceAdjDetail();
            accountBalanceAdjDetailQuery.setTmpOrderId(reCalBuyEtfInBalReq.getTmpOrderId());
            AccountBalanceAdjDetail balDetailForBalId = accountBalanceAdjDetailService.selectOne(accountBalanceAdjDetailQuery);
            Long rebalancingId = balDetailForBalId.getBalId();

            // Avoid duplication on the rebalancing ID
            if (!balIdList.contains(rebalancingId)) {
                balIdList.add(rebalancingId);
                balId = rebalancingId;
                accountBalanceRecord = accountBalanceRecordService.queryById(rebalancingId);
                modelRecommendDTO = modelServiceRemoteService.getModelRecommendById(accountBalanceRecord.getModelRecommendId());
                accountId = reCalBuyEtfInBalReq.getAccountId();

                if (accountBalanceRecord.getBalStatus() == BalStatusEnum.BUYING) {
                    log.info("该方案卖已执行,不进行买单重算:{}", JSON.toJSONString(accountBalanceRecord));
                    break;
                }

                //查询充值流水
                List<AccountRechargePO> accountRechargePOs = tpcfTncfService.getAccountTpcf(accountId);
                BigDecimal tpcf = tpcfTncfService.getAccountTpcfMoney(accountRechargePOs);

                BigDecimal tncf = tpcfTncfService.getAccountHandlingTncfMoney(accountId);
                //根据navtime获取昨日账户资产
                AccountFundNavPO accountNavQuery = new AccountFundNavPO();
                accountNavQuery.setAccountId(accountId);
                accountNavQuery.setNavTime(DateUtils.addDateByDay(DateUtils.now(), -1));
                AccountFundNavPO accountNav = accountNavService.selectOneByNavTime(accountNavQuery);
                if (accountNav == null) {
                    log.error("账户{},找不到对应账户基金净值记录", accountId);
                    throw new BusinessException("账户" + accountId + ",找不到对应账户基金净值记录");
                }

                BigDecimal currentAsset = accountNav.getTotalAsset().add(tpcf).subtract(tncf);
                log.info("账户{},currentAsset{},tpcf{},tncf{}", accountId, currentAsset, tpcf, tncf);
                //获取账户当前资产明细
                Map<String, AccountAssetStatisticBean> allCurrentHoldMoneys = genAssetHoldMoney();
                log.info("当前资产详情:{}", JSON.toJSONString(allCurrentHoldMoneys));

                //目标资产明细
                Map<String, BigDecimal> targetHoldMoneys = genTargetMoney(currentAsset);
                log.info("目标资产详情:{}", JSON.toJSONString(targetHoldMoneys));

                //原始方案制作
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
                    //买入
                    if (diffMoney.compareTo(BigDecimal.ZERO) > 0) {
                        accountBalanceAdjDetail.setBalId(balId);
                        accountBalanceAdjDetail.setCurrentHold(currentMoney);
                        accountBalanceAdjDetail.setExecuteStatus(ExecuteStatusEnum.CREATE);
                        accountBalanceAdjDetail.setProductCode(etfKey);
                        accountBalanceAdjDetail.setTargetHold(targetMoney);
                        accountBalanceAdjDetail.setTradeAmount(diffMoney.abs());
                        accountBalanceAdjDetail.setTradeType(BalTradeTypeEnum.BUY);
                        accountBalanceAdjDetailList.add(accountBalanceAdjDetail);
                        _accountBalanceAdjDetailList.add(accountBalanceAdjDetail);
                    }
                }
                //根据卖单交易结果重算当前资产
                String accountIdStr = "" + accountId;
                for (ReCalBuyEtfInBalReqDTO reCalBuyEtfInBalReqDTO : reCalBuyEtfInBalReqDTOList) {
                    String reCalBuyEtfInBalAccountIdStr = "" + reCalBuyEtfInBalReqDTO.getAccountId();
                    if (accountIdStr.equalsIgnoreCase(reCalBuyEtfInBalAccountIdStr)) {
                        AccountAssetStatisticBean accountAssetStatisticBean = allCurrentHoldMoneys.get(reCalBuyEtfInBalReqDTO.getProductCode());
                        if (accountAssetStatisticBean == null) {
                            continue;
                        }
                        BigDecimal curShares = accountAssetStatisticBean.getProductShare();
                        BigDecimal confirmPrice = reCalBuyEtfInBalReqDTO.getConfirmPrice();
                        BigDecimal translateShares = curShares.subtract(reCalBuyEtfInBalReqDTO.getConfirmShare());
                        BigDecimal translateMoney = translateShares.multiply(confirmPrice);

                        accountAssetStatisticBean.setProductShare(translateShares);
                        accountAssetStatisticBean.setProductMoney(translateMoney);

                        AccountAssetStatisticBean cashAssetBean = allCurrentHoldMoneys.get(Constants.CASH);
                        if (cashAssetBean == null) {
                            AccountAssetStatisticBean cashAssetNew = new AccountAssetStatisticBean();
                            cashAssetNew.setAccountId(reCalBuyEtfInBalReqDTO.getAccountId());
                            cashAssetNew.setProductMoney(reCalBuyEtfInBalReqDTO.getConfirmMoney());
                            cashAssetNew.setProductShare(BigDecimal.ZERO);
                            cashAssetNew.setProductAssetStatus(ProductAssetStatusEnum.HOLD_ING);
                            cashAssetNew.setProductCode(Constants.CASH);
                            allCurrentHoldMoneys.put(Constants.CASH, cashAssetNew);
                            log.info("账户{},NONE ETF增加的cash是{},原有cash是0", reCalBuyEtfInBalReqDTO.getAccountId(), reCalBuyEtfInBalReqDTO.getConfirmMoney());
                        } else {
                            BigDecimal curCash = cashAssetBean.getProductMoney();
                            cashAssetBean.setProductMoney(curCash.add(reCalBuyEtfInBalReqDTO.getConfirmMoney()));
                            log.info("账户{},ETF {} 增加的cash是{},原有cash是0", reCalBuyEtfInBalReqDTO.getProductCode(), reCalBuyEtfInBalReqDTO.getAccountId(), reCalBuyEtfInBalReqDTO.getConfirmMoney(), curCash);
                        }
                    }
                }

                //计算修正后的总资产金额
                BigDecimal correctTotalAsset = BigDecimal.ZERO;
                for (Map.Entry<String, AccountAssetStatisticBean> entry : allCurrentHoldMoneys.entrySet()) {
                    AccountAssetStatisticBean accountAssetStatisticBean = entry.getValue();
                    log.info("correctTotalAsset ETF {} , SHARE {} , ProductMoney {} ", accountAssetStatisticBean.getProductCode(), accountAssetStatisticBean.getProductShare(), accountAssetStatisticBean.getProductMoney());
                    correctTotalAsset = correctTotalAsset.add(accountAssetStatisticBean.getProductMoney());
                }
                log.info("账号{} Before correctTotalAsset {} ", accountId, correctTotalAsset);
                correctTotalAsset = correctTotalAsset.subtract(tncf);
                log.info("账号{} After correctTotalAsset {}", accountId, correctTotalAsset);

                //修正后目标资产明细
                Map<String, BigDecimal> targetHoldMoneysAfter = genTargetMoney(correctTotalAsset);
                log.info("targetHoldMoneysAfter {} ", targetHoldMoneysAfter);
                List<AccountBalanceAdjDetail> accountBalanceAdjDetailBuyList = Lists.newArrayList();
                //修正买入金额
                for (AccountBalanceAdjDetail accountBalanceAdjDetail : accountBalanceAdjDetailList) {
                    BigDecimal targetMoney = targetHoldMoneysAfter.get(accountBalanceAdjDetail.getProductCode());
                    log.info("targetMoney {} ,  ", targetMoney);
                    if (targetMoney != null) {
                        accountBalanceAdjDetail.setCorrectTargetHold(targetMoney);
                        BigDecimal targetHold = accountBalanceAdjDetail.getTargetHold();
//                        BigDecimal correctTradeAmount = targetMoney.subtract(accountBalanceAdjDetail.getCurrentHold());
                        BigDecimal correctTradeAmount = targetHold.subtract(accountBalanceAdjDetail.getCurrentHold());
                        accountBalanceAdjDetail.setTradeAmount(correctTradeAmount);

                        //若修正后的目标金额小了，不进行操作
                        if (correctTradeAmount.compareTo(BigDecimal.ZERO) > 0) {
                            accountBalanceAdjDetailBuyList.add(accountBalanceAdjDetail);
                        }
                    }
                }
                log.info("accountBalanceAdjDetailBuyList {} ", accountBalanceAdjDetailBuyList);
                if (!accountBalanceAdjDetailBuyList.isEmpty()) {
                    adjustPlanBuyBuilderSupport.setAccountBalanceAdjDetailList(accountBalanceAdjDetailBuyList);
                    adjustPlanBuyBuilderSupport.genBuyAdj();
                    accountBalanceExecute.setAccountBalanceAdjDetails(accountBalanceAdjDetailList);
                    accountBalanceExecute.executePlanDetailBuy(BalTradeTypeEnum.BUY, rebalancingId);
                }
            }
        }
        return _accountBalanceAdjDetailList;
    }

    private Map<String, BigDecimal> genTargetMoney(BigDecimal currentAsset) {
        String currentWeight = modelRecommendDTO.getProductWeight();
        EtfListBean etfListBean = JSON.parseObject(currentWeight, new TypeReference<EtfListBean>() {
        });
        List<EtfBean> mainEtf = etfListBean.getMainEtf();
//        List<EtfBean> subEtf = etfListBean.getSubEtf();

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
        assetQuery.setAccountId(accountId);
        List<AccountAssetPO> accountAssetList = accountAssetService.queryList(assetQuery);
        log.info("账户{},统计前资产明细{}", accountId, JSON.toJSON(accountAssetList));
        //昨日收盘价
        Date closePriceYesterday = DateUtils.addDateByDay(DateUtils.now(), -1);
        Map<String, BigDecimal> etfClosingPriceMap = assetFundNavService.getEtfClosingPrice();
        log.info("date:{},etf收市价:{}=======", closePriceYesterday, JSON.toJSON(etfClosingPriceMap));
        if (etfClosingPriceMap == null) {
            throw new BusinessException("昨日收市价为空");
        }
        List<AccountAssetStatisticBean> accountAssetStatisticBeans = AccountAssetStatistic.statAccountAsset(accountAssetList, etfClosingPriceMap);
        log.info("账户{},统计后资产明细{}", accountId, JSON.toJSON(accountAssetStatisticBeans));
        if (CollectionUtils.isEmpty(accountAssetStatisticBeans)) {
            log.info("账户" + accountId + "该投资账户无持有中资产");
            throw new BusinessException("账户" + accountId + "该投资账户无持有中资产");
        }
        //当前资产比例
        if (CollectionUtils.isEmpty(accountAssetStatisticBeans)) {
            log.info("账户" + accountId + "该投资账户无持有中资产");
            throw new BusinessException("账户" + accountId + "该投资账户无持有中资产");
        }
        Map<String, AccountAssetStatisticBean> allHoldMultimap = Maps.newHashMap();
        for (AccountAssetStatisticBean accountAssetStatisticBean : accountAssetStatisticBeans) {
            allHoldMultimap.put(accountAssetStatisticBean.getProductCode(), accountAssetStatisticBean);
        }
        return allHoldMultimap;
    }

}
