package com.pivot.aham.api.service.job.impl;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.server.remoteservice.SaxoTradeRemoteService;
import com.pivot.aham.api.service.job.ReconciliationAccountJob;
import com.pivot.aham.api.service.service.AccountAssetService;
import com.pivot.aham.api.service.service.AccountInfoService;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * Created by luyang.li on 19/2/15.
 */
/*@ElasticJobConf(name = "ReconciliationAccount_2",
cron = "0 0 12 * * ?",
shardingItemParameters = "0=1",
shardingTotalCount=1,
description = "交易分析#SAXO总账对账",eventTraceRdbDataSource = "dataSource")
@Slf4j
public class ReconciliationAccountJobImpl implements SimpleJob, ReconciliationAccountJob {

//    static Logger LOGGER = LogManager.getLogger(ReconciliationAccountJobImpl.class);

    @Resource
    private AccountAssetService accountAssetService;
    @Resource
    private SaxoTradeRemoteService saxoTradeRemoteService;
    @Resource
    private AccountInfoService accountInfoService;

    @Override
    public void balanceAccountWithSaxoEtf() {

//        Date now = DateUtils.dayStart(DateUtils.now());
//        Set<String> etfCodes = Sets.newHashSet();
//        Map<String, BigDecimal> etfShareMap = Maps.newHashMap();
//        Map<String, Integer> saxoEtfShareMap = Maps.newHashMap();
//
//        //1.查询所有的账户
//        List<AccountInfoPO> accountInfoPOs = accountInfoService.listAccountInfo();
//        for (AccountInfoPO accountInfoPO : accountInfoPOs) {
//            AccountAssetPO queryParam = new AccountAssetPO();
//            queryParam.setAccountId(accountInfoPO.getId());
//            List<AccountAssetPO> accountAssetPOs = accountAssetService.listAccountUnBuyAssets(queryParam);
//            if (CollectionUtils.isEmpty(accountAssetPOs)) {
//                continue;
//            }
//            //查询该账号上的总资产
//            List<AccountAssetStatisticBean> accountAssetStatisticBeens = AccountAssetStatistic.statAccountShare(accountAssetPOs);
//            for (AccountAssetStatisticBean accountAssetStatisticBeen : accountAssetStatisticBeens) {
//                BigDecimal holdShare = etfShareMap.get(accountAssetStatisticBeen.getProductCode());
//                if (null == holdShare) {
//                    holdShare = accountAssetStatisticBeen.getProductShare();
//                } else {
//                    holdShare = holdShare.add(accountAssetStatisticBeen.getProductShare());
//                }
//                etfShareMap.put(accountAssetStatisticBeen.getProductCode(), holdShare);
//
//                etfCodes.add(accountAssetStatisticBeen.getProductCode());
//            }
//        }
//
//        //2.查询当天的SAXO的etf份额
//        for (String etfCode : etfCodes) {
//            EtfHoldingReq etfHoldingReq = new EtfHoldingReq();
//            etfHoldingReq.setEtfCode(etfCode);
//            RpcMessage<EtfHoldingResult> etfHoldingResult = saxoTradeRemoteService.queryEtfHolding(etfHoldingReq);
//            if (RpcMessageStandardCode.OK.value() == etfHoldingResult.getResultCode()) {
//                saxoEtfShareMap.put(etfCode, etfHoldingResult.getContent().getAmount());
//            }
//        }
//
//        List<String> etfDifferShares = Lists.newArrayList();
//        for (String etfCode : etfShareMap.keySet()) {
//            BigDecimal pivotEtfShare = etfShareMap.get(etfCode);
//            Integer saxoEtfShare = saxoEtfShareMap.get(etfCode);
//            if (pivotEtfShare.intValue() != saxoEtfShare) {
//                //邮件内容
//                String differContent = "pivot记录的份额和SAXO的持有份额不等,pivot:" + pivotEtfShare.intValue() + ",saxo:" + saxoEtfShare;
//                etfDifferShares.add(differContent);
//            }
//        }
//
//        if (CollectionUtils.isNotEmpty(etfDifferShares)) {
//            ErrorLogAndMailUtil.logError(log, JSON.toJSON(etfDifferShares));
//        }

    }

    @Override
    public void execute(ShardingContext shardingContext) {
        try{
            balanceAccountWithSaxoEtf();
        }catch (Exception e){
            ErrorLogAndMailUtil.logError(log,e);
        }
    }
}
*/