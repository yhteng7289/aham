package com.pivot.aham.api.service.job.impl;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.UserInfoResDTO;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.service.job.StaticUserEtfJob;
import com.pivot.aham.api.service.mapper.model.AccountUserPO;
import com.pivot.aham.api.service.mapper.model.UserAssetPO;
import com.pivot.aham.api.service.mapper.model.UserEtfSharesPO;
import com.pivot.aham.api.service.mapper.model.UserEtfSharesStaticPO;
import com.pivot.aham.api.service.service.AccountUserService;
import com.pivot.aham.api.service.service.UserAssetService;
import com.pivot.aham.api.service.service.UserEtfSharesService;
import com.pivot.aham.api.service.service.UserEtfSharesStaticService;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.stereotype.Component;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年03月05日
 */
@ElasticJobConf(name = "StaticUEtfJob_2",
        cron = "0 00 21 * * ?",
        shardingItemParameters = "0=1",
        shardingTotalCount = 1,
        description = "交易04_交易分析#统计etfshares", eventTraceRdbDataSource = "dataSource")
@Slf4j
@Component
public class StaticUEtfJobImpl implements SimpleJob, StaticUserEtfJob {

    @Resource
    private UserServiceRemoteService userServiceRemoteService;
    @Resource
    private AccountUserService accountUserService;
    @Resource
    private UserAssetService userAssetService;
    @Resource
    private UserEtfSharesService userEtfSharesService;
    @Resource
    private UserEtfSharesStaticService userEtfSharesStaticService;

    @Override
    public void staticUserEtfJob(Date date) {
//        if(date != null){
//            date = DateUtils.getDate(date,23,59,50);
//        }else {
//            date = new Date();
//        }

//        Date yesterday = DateUtils.addDateByDay(date, -1);
//        //查询所etf的收市价格
//        Map<String, BigDecimal> etfClosingPriceMap = assetFundNavService.getEtfClosingPrice(yesterday);
//        log.info("{},收市价:{}", yesterday, etfClosingPriceMap);
        //获取所有用户
        List<UserInfoResDTO> userInfoResDTOS = userServiceRemoteService.queryUserList();
        Integer totalUser = userInfoResDTOS.size();
        ExecutorService executorService = Executors.newFixedThreadPool(totalUser);
        for (UserInfoResDTO userInfoResDTO : userInfoResDTOS) {
            //获取所有用户的goal
            AccountUserPO accountUserQuery = new AccountUserPO();
            accountUserQuery.setClientId(userInfoResDTO.getClientId());
            List<AccountUserPO> accountUserPOList = accountUserService.queryList(accountUserQuery);
            for (AccountUserPO accountUser : accountUserPOList) {
                UserAssetPO userAssetPO = new UserAssetPO();
                userAssetPO.setAccountId(accountUser.getAccountId());
                userAssetPO.setClientId(accountUser.getClientId());
                userAssetPO.setGoalId(accountUser.getGoalId());
                userAssetPO.setAssetTime(new Date());
                List<UserAssetPO> userAssetPOList = userAssetService.queryListByTime(userAssetPO);
                handleEtf(userAssetPOList);
            }
        }
    }

    //统计用户的etf持有
    public List<UserEtfSharesStaticPO> handleEtf(List<UserAssetPO> userAssetPOs) {
        List<UserEtfSharesStaticPO> userEtfSharesStaticList = Lists.newArrayList();
        for (UserAssetPO userAsset : userAssetPOs) {
            UserEtfSharesPO userEtfSharesPO = new UserEtfSharesPO();
            userEtfSharesPO.setAccountId(userAsset.getAccountId());
            userEtfSharesPO.setClientId(userAsset.getClientId());
            userEtfSharesPO.setGoalId(userAsset.getGoalId());
            userEtfSharesPO.setProductCode(userAsset.getProductCode());
            userEtfSharesPO.setMoney(userAsset.getMoney());
            userEtfSharesPO.setShares(userAsset.getShare());
            if (userAsset.getProductCode().equals(Constants.CASH)) {
                userEtfSharesPO.setShares(BigDecimal.ZERO);
            }
            userEtfSharesPO.setStaticDate(new Date());

            UserEtfSharesPO userEtfSharesQuery = new UserEtfSharesPO();
            userEtfSharesQuery.setAccountId(userAsset.getAccountId());
            userEtfSharesQuery.setClientId(userAsset.getClientId());
            userEtfSharesQuery.setGoalId(userAsset.getGoalId());
            userEtfSharesQuery.setProductCode(userAsset.getProductCode());
            userEtfSharesQuery.setStaticDate(new Date());
            UserEtfSharesPO userEtfShares = userEtfSharesService.selectByStaticDate(userEtfSharesQuery);
            if (userEtfShares != null) {
                userEtfSharesPO.setId(userEtfShares.getId());
            }
            userEtfSharesService.updateOrInsert(userEtfSharesPO);

            UserEtfSharesStaticPO userEtfSharesStaticPO = new UserEtfSharesStaticPO();
            userEtfSharesStaticPO.setAccountId(userAsset.getAccountId());
            userEtfSharesStaticPO.setClientId(userAsset.getClientId());
            userEtfSharesStaticPO.setGoalId(userAsset.getGoalId());
            userEtfSharesStaticPO.setStaticDate(new Date());
            if (userAsset.getProductCode().equals("AAGF")) {
                userEtfSharesStaticPO.setAagf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("1ABF")) {
                userEtfSharesStaticPO.setOneabf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("DI")) {
                userEtfSharesStaticPO.setDi(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("1PGF")) {
                userEtfSharesStaticPO.setOnepgf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("DF")) {
                userEtfSharesStaticPO.setDf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("1AEF")) {
                userEtfSharesStaticPO.setOneaef(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("ASI")) {
                userEtfSharesStaticPO.setAsi(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("AFF")) {
                userEtfSharesStaticPO.setAff(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("AIF")) {
                userEtfSharesStaticPO.setAif(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("1BF")) {
                userEtfSharesStaticPO.setOnebf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("EDF")) {
                userEtfSharesStaticPO.setEdf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("1EF")) {
                userEtfSharesStaticPO.setOneef(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("NCTF")) {
                userEtfSharesStaticPO.setNctf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("GOF")) {
                userEtfSharesStaticPO.setGof(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SCF")) {
                userEtfSharesStaticPO.setScf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SAPBF")) {
                userEtfSharesStaticPO.setSapbf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SAPDF")) {
                userEtfSharesStaticPO.setSapdf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("GIF")) {
                userEtfSharesStaticPO.setGif(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("BAL")) {
                userEtfSharesStaticPO.setBal(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("BOND")) {
                userEtfSharesStaticPO.setBond(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SDF")) {
                userEtfSharesStaticPO.setSdf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SIF")) {
                userEtfSharesStaticPO.setSif(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SOF")) {
                userEtfSharesStaticPO.setSof(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SGDIF")) {
                userEtfSharesStaticPO.setSgdif(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SGTF")) {
                userEtfSharesStaticPO.setSgtf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("GDIFMYRH")) {
                userEtfSharesStaticPO.setGdifmyrh(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("GLIFMYRNH")) {
                userEtfSharesStaticPO.setGlifmyrnh(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("GLIFMYR")) {
                userEtfSharesStaticPO.setGlifmyr(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("WSGQFMYR")) {
                userEtfSharesStaticPO.setWsgqfmyr(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("WSGQFMYRH")) {
                userEtfSharesStaticPO.setWsgqfmyrh(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("CF")) {
                userEtfSharesStaticPO.setCf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SJQFMYRNH")) {
                userEtfSharesStaticPO.setSjqfmyrnh(userAsset.getShare());
            }
            /*if (userAsset.getProductCode().equals("VWO")) {
                userEtfSharesStaticPO.setVwo(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("ILF")) {
                userEtfSharesStaticPO.setIlf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("RSX")) {
                userEtfSharesStaticPO.setRsx(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("AAXJ")) {
                userEtfSharesStaticPO.setAaxj(userAsset.getShare());
            }*/

            userEtfSharesStaticList.add(userEtfSharesStaticPO);

            UserEtfSharesStaticPO userEtfSharesStaticQuery = new UserEtfSharesStaticPO();
            userEtfSharesStaticQuery.setStaticDate(new Date());
            userEtfSharesStaticQuery.setAccountId(userAsset.getAccountId());
            userEtfSharesStaticQuery.setClientId(userAsset.getClientId());
            userEtfSharesStaticQuery.setGoalId(userAsset.getGoalId());
            UserEtfSharesStaticPO userEtfSharesStatic = userEtfSharesStaticService.selectByStaticDate(userEtfSharesStaticQuery);
            if (userEtfSharesStatic != null) {
                userEtfSharesStaticPO.setId(userEtfSharesStatic.getId());
            }
            userEtfSharesStaticService.updateOrInsert(userEtfSharesStaticPO);
        }
        return userEtfSharesStaticList;
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            staticUserEtfJob(null);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }

    }
}
