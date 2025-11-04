package com.pivot.aham.api.service.job.impl;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.service.job.StaticAccountEtfJob;
import com.pivot.aham.api.service.mapper.model.AccountAssetPO;
import com.pivot.aham.api.service.mapper.model.AccountEtfSharesPO;
import com.pivot.aham.api.service.mapper.model.AccountEtfSharesStaticPO;
import com.pivot.aham.api.service.mapper.model.AccountInfoPO;
import com.pivot.aham.api.service.service.*;
import com.pivot.aham.api.service.support.AccountAssetStatistic;
import com.pivot.aham.api.service.support.AccountAssetStatisticBean;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年03月05日
 */
@ElasticJobConf(name = "StaticAccountEtfJob_2",
        cron = "0 30 18 * * ?",
        shardingItemParameters = "0=1",
        shardingTotalCount = 1,
        description = "交易04_交易分析#统计Etfshares", eventTraceRdbDataSource = "dataSource")
@Slf4j
@Component
public class StaticAccountEtfJobImpl implements SimpleJob, StaticAccountEtfJob {

    @Autowired
    private AssetFundNavService assetFundNavService;
    @Autowired
    private AccountInfoService accountInfoService;
    @Autowired
    private AccountAssetService accountAssetService;
    @Autowired
    private AccountEtfSharesStaticService accountEtfSharesStaticService;
    @Autowired
    private AccountEtfSharesService accountEtfSharesService;

    @Override
    public void staticAccountEtfJob(Date date) {
        if (date != null) {
            date = DateUtils.getDate(date, 23, 59, 50);
        } else {
            date = new Date();
        }

        List<AccountInfoPO> accountInfoPOList = accountInfoService.listAccountInfo();

        Date yesterday = DateUtils.addDateByDay(date, -1);
        //查询所etf的收市价格
        Map<String, BigDecimal> etfClosingPriceMap = assetFundNavService.getEtfClosingPrice(yesterday);
        log.info("{},收市价:{}", yesterday, etfClosingPriceMap);

        for (AccountInfoPO accountInfoPO : accountInfoPOList) {
            try {
                AccountEtfSharesStaticPO accountEtfSharesPO = new AccountEtfSharesStaticPO();
                accountEtfSharesPO.setAccountId(accountInfoPO.getId());
                //重算totalasset和totalcash
                AccountAssetPO queryParam = new AccountAssetPO();
                queryParam.setAccountId(accountInfoPO.getId());
                queryParam.setCreateEndTime(date);
                List<AccountAssetPO> accountAssetPOs = accountAssetService.listAccountUnBuyAssets(queryParam);

                if (CollectionUtils.isEmpty(accountAssetPOs)) {
                    log.info("用户资产统计,该账户没有资产,不做处理。accountId:" + accountInfoPO.getId());
                    continue;
                }

                AccountEtfSharesStaticPO accountEtfSharesQuery = new AccountEtfSharesStaticPO();
                accountEtfSharesQuery.setAccountId(accountInfoPO.getId());
                accountEtfSharesQuery.setStaticDate(date);
                AccountEtfSharesStaticPO accountEtfShares = accountEtfSharesStaticService.selectByStaticDate(accountEtfSharesQuery);

                //查询该账号上的资产明细
                List<AccountAssetStatisticBean> accountAssetStatisticBeens = AccountAssetStatistic.statAccountAsset(accountAssetPOs, etfClosingPriceMap);
                for (AccountAssetStatisticBean accountAssetStatisticBean : accountAssetStatisticBeens) {

                    AccountEtfSharesPO accountEtfQuery = new AccountEtfSharesPO();
                    accountEtfQuery.setAccountId(accountAssetStatisticBean.getAccountId());
                    accountEtfQuery.setStaticDate(DateUtils.now());
                    accountEtfQuery.setProductCode(accountAssetStatisticBean.getProductCode());
                    AccountEtfSharesPO accountEtf = accountEtfSharesService.selectByStaticDateByAccountId(accountEtfQuery);

                    if (accountAssetStatisticBean.getProductCode().equals("AAGF")) {
                        accountEtfSharesPO.setAagf(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("1ABF")) {
                        accountEtfSharesPO.setOneabf(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("DI")) {
                        accountEtfSharesPO.setDi(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("1PGF")) {
                        accountEtfSharesPO.setOnepgf(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("DF")) {
                        accountEtfSharesPO.setDf(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("1AEF")) {
                        accountEtfSharesPO.setOneaef(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("ASI")) {
                        accountEtfSharesPO.setAsi(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("AFF")) {
                        accountEtfSharesPO.setAff(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("AIF")) {
                        accountEtfSharesPO.setAif(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("1BF")) {
                        accountEtfSharesPO.setOnebf(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("EDF")) {
                        accountEtfSharesPO.setEdf(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("1EF")) {
                        accountEtfSharesPO.setOneef(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("NCTF")) {
                        accountEtfSharesPO.setNctf(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("GOF")) {
                        accountEtfSharesPO.setGof(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("SCF")) {
                        accountEtfSharesPO.setScf(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("SAPBF")) {
                        accountEtfSharesPO.setSapbf(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("SAPDF")) {
                        accountEtfSharesPO.setSapdf(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("GIF")) {
                        accountEtfSharesPO.setGif(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("BAL")) {
                        accountEtfSharesPO.setBal(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("BOND")) {
                        accountEtfSharesPO.setBond(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("SDF")) {
                        accountEtfSharesPO.setSdf(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("SIF")) {
                        accountEtfSharesPO.setSif(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("SOF")) {
                        accountEtfSharesPO.setSof(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("SGDIF")) {
                        accountEtfSharesPO.setSgdif(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("SGTF")) {
                        accountEtfSharesPO.setSgtf(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("GDIFMYRH")) {
                        accountEtfSharesPO.setGdifmyrh(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("GLIFMYRNH")) {
                        accountEtfSharesPO.setGlifmyrnh(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("GLIFMYR")) {
                        accountEtfSharesPO.setGlifmyr(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("WSGQFMYR")) {
                        accountEtfSharesPO.setWsgqfmyr(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("WSGQFMYRH")) {
                        accountEtfSharesPO.setWsgqfmyrh(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("CF")) {
                        accountEtfSharesPO.setCf(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("SJQFMYRNH")) {
                        accountEtfSharesPO.setSjqfmyrnh(accountAssetStatisticBean.getProductShare());
                    }
                    /*if (accountAssetStatisticBean.getProductCode().equals("VWO")) {
                        accountEtfSharesPO.setVwo(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("ILF")) {
                        accountEtfSharesPO.setIlf(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("RSX")) {
                        accountEtfSharesPO.setRsx(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("AAXJ")) {
                        accountEtfSharesPO.setAaxj(accountAssetStatisticBean.getProductShare());
                    }*/
//                    if (accountAssetStatisticBean.getProductCode().equals("ASX")) {
//                        accountEtfSharesPO.setAsx(accountAssetStatisticBean.getProductShare());
//                    }
//                    if (accountAssetStatisticBean.getProductCode().equals("AWC")) {
//                        accountEtfSharesPO.setAwc(accountAssetStatisticBean.getProductShare());
//                    }

                    AccountEtfSharesPO accountEtfUpdate = new AccountEtfSharesPO();
                    accountEtfUpdate.setStaticDate(DateUtils.now());
                    accountEtfUpdate.setProductCode(accountAssetStatisticBean.getProductCode());
                    accountEtfUpdate.setAccountId(accountAssetStatisticBean.getAccountId());
                    accountEtfUpdate.setShares(accountAssetStatisticBean.getProductShare());
                    if (accountAssetStatisticBean.getProductCode().equals(Constants.CASH)) {
                        accountEtfUpdate.setShares(BigDecimal.ZERO);
                    }
                    accountEtfUpdate.setMoney(accountAssetStatisticBean.getProductMoney());
                    accountEtfUpdate.setStaticDate(date);
                    if (accountEtf != null) {
                        accountEtfUpdate.setId(accountEtf.getId());
                    }
                    accountEtfSharesService.updateOrInsert(accountEtfUpdate);

                    //幂等控制
                    if (accountEtfShares != null) {
                        accountEtfSharesPO.setId(accountEtfShares.getId());
                    }
                    accountEtfSharesPO.setStaticDate(date);
                    accountEtfSharesStaticService.updateOrInsert(accountEtfSharesPO);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                log.error("用户资产统计,异常。accountId:" + accountInfoPO.getId(), ex);
            }
        }
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            staticAccountEtfJob(null);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }

    }
}
