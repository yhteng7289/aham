package com.pivot.aham.api.service.core;

import cn.hutool.core.date.DateUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.beust.jcommander.Strings;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import com.google.common.eventbus.EventBus;
import com.pivot.aham.api.server.dto.AccountTotalAssetDTO;
import com.pivot.aham.api.server.dto.ModelRecommendResDTO;
import com.pivot.aham.api.server.dto.PortLevelDTO;
import com.pivot.aham.api.server.dto.UserInfoResDTO;
import com.pivot.aham.api.server.dto.req.PivotPftAssetResDTO;
import com.pivot.aham.api.server.dto.res.PivotPftAssetReqDTO;
import com.pivot.aham.api.server.remoteservice.*;
import com.pivot.aham.api.service.SaxoStatisticService;
import com.pivot.aham.api.service.TradingSupportService;
import com.pivot.aham.api.service.client.rest.AhamRestClient;
import com.pivot.aham.api.service.client.saxo.SaxoClient;
import com.pivot.aham.api.service.client.saxo.resp.AccountFundingRespV2;
import com.pivot.aham.api.service.impl.trade.Confirm;
import com.pivot.aham.api.service.impl.trade.Demerge;
import com.pivot.aham.api.service.impl.trade.Finish;
import com.pivot.aham.api.service.impl.trade.MergeOrder;
import com.pivot.aham.api.service.impl.trade.Recalculate;
import com.pivot.aham.api.service.impl.trade.Revise;
import com.pivot.aham.api.service.impl.trade.Trade;
import com.pivot.aham.api.service.job.TradeAnalysisStrategy;
import com.pivot.aham.api.service.job.custstatment.impl.CustomerStatementJobImpl;
import com.pivot.aham.api.service.job.impl.AccountStaticSgdJobImpl;
import com.pivot.aham.api.service.job.impl.AssetFundNavJobImpl;
import com.pivot.aham.api.service.job.impl.StaticAccountEtfJobImpl;
import com.pivot.aham.api.service.job.impl.StaticUEtfJobImpl;
import com.pivot.aham.api.service.job.impl.UserProfitJobImpl;
import com.pivot.aham.api.service.job.impl.WithdrawalNotifyToAham;
import com.pivot.aham.api.service.job.impl.rebalance.AccountBalanceExecute;
import com.pivot.aham.api.service.job.impl.rebalance.AdjustPlanSellBuilder;
import com.pivot.aham.api.service.job.impl.rebalance.Pool1TriggerStrategy;
import com.pivot.aham.api.service.job.impl.rebalance.Pool2TriggerStrategy;
import com.pivot.aham.api.service.job.impl.rebalance.Pool3TriggerStrategy;
import com.pivot.aham.api.service.job.impl.rebalance.ReBalanceTriggerContext;
import com.pivot.aham.api.service.job.impl.rebalance.ReBalanceTriggerResult;
import com.pivot.aham.api.service.job.interevent.CalFundNavEvent;
import com.pivot.aham.api.service.job.interevent.NormalAccountFeeCreateEvent;
import com.pivot.aham.api.service.job.interevent.NormalClientFeeReduceEvent;
import com.pivot.aham.api.service.job.interevent.StaticFortradeAnalysisEvent;
import com.pivot.aham.api.service.job.interevent.UserGoalCashFlowEvent;
import com.pivot.aham.api.service.job.interevent.UserStaticsEvent;
import com.pivot.aham.api.service.job.saxo.trade.Trade140_SyncPftJob;
import com.pivot.aham.api.service.job.wrapperbean.AccountFundNavWrapperBean;
import com.pivot.aham.api.service.job.wrapperbean.AccountRedeemWrapperBean;
import com.pivot.aham.api.service.job.wrapperbean.AccountTpcfTncfBean;
import com.pivot.aham.api.service.job.wrapperbean.AnalyTpcfTncfWrapperBean;
import com.pivot.aham.api.service.job.wrapperbean.UserGoalProfitWrapper;
import com.pivot.aham.api.service.mapper.DailyClosingPriceMapper;
import com.pivot.aham.api.service.mapper.EtfMergeOrderPftMapper;
import com.pivot.aham.api.service.mapper.PortLevelMapper;
import com.pivot.aham.api.service.mapper.SaxoAccountFundingEventMapper;
import com.pivot.aham.api.service.mapper.model.*;
//import com.pivot.aham.api.service.remote.impl.TestRemoteServiceImpl;
import com.pivot.aham.api.service.service.*;
import com.pivot.aham.common.enums.analysis.*;
import com.pivot.aham.api.service.service.impl.PortLevelServiceImpl;
import com.pivot.aham.api.service.support.AccountAssetStatistic;
import com.pivot.aham.api.service.support.AccountAssetStatisticBean;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.exception.MessageException;
import com.pivot.aham.common.core.support.file.ftp.SftpClient;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.core.util.FTPUtil;
import com.pivot.aham.common.enums.AccountTypeEnum;
import com.pivot.aham.common.enums.AgeLevelEnum;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.EtfmergeOrderTypeEnum;
import com.pivot.aham.common.enums.ExchangeRateTypeEnum;
import com.pivot.aham.common.enums.PoolingEnum;
import com.pivot.aham.common.enums.ProductAssetStatusEnum;
import com.pivot.aham.common.enums.RebalanceEnum;
import com.pivot.aham.common.enums.RedeemTypeEnum;
import com.pivot.aham.common.enums.RiskLevelEnum;
import com.pivot.aham.common.enums.SyncStatus;
import com.pivot.aham.common.enums.TradeType;
import com.pivot.aham.common.enums.recharge.TncfStatusEnum;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by luyang.li on 18/12/17.
 */
@Slf4j
@Controller
@RequestMapping("/test/")
public class CronController {

    @Autowired
    private SaxoAccountFundingEventMapper saxoAccountFundingEventMapper;

    @Resource
    private AccountInfoService accountInfoService;
    @Resource
    private TradeAnalysisStrategy tradeAnalysisStrategy;
    @Resource
    private AccountBalanceHisRecordService accountBalanceHisRecordService;
    @Resource
    private ModelServiceRemoteService modelServiceRemoteService;
    @Resource
    private AdjustPlanSellBuilder adjustPlanSellBuilder;
    @Resource
    private AccountBalanceExecute accountBalanceExecute;
    @Resource
    private EventBus eventBus;
    @Resource
    private AccountAssetService accountAssetService;
    @Resource
    private TpcfTncfService tpcfTncfService;
    @Resource
    private AccountBalanceRecordService accountBalanceRecordService;
    @Resource
    private AnalysisSupportService analysisSupportService;
    @Autowired
    private SaxoStatisticService saxoStatisticService;
    @Resource
    private AccountUserService accountUserService;
    @Resource
    private UserFundNavService userFundNavService;
    @Resource
    private ExchangeRateService exchangeRateService;
    @Resource
    private SaxoAccountOrderService saxoAccountOrderService;
    @Resource
    private UserProfitInfoService userProfitInfoService;

    @Resource
    private AssetFundNavService assetFundNavService;
    @Autowired
    private AccountRedeemService accountRedeemService;
    @Autowired
    private RedeemApplyService redeemApplyService;
    @Resource
    private AssetFundNavService accountNavService;
    @Resource
    private AccountDividendService accountDividendService;
    @Resource
    private UserDividendService userDividendService;
    @Autowired
    private AccountEtfSharesStaticService accountEtfSharesStaticService;
    @Autowired
    private AccountEtfSharesService accountEtfSharesService;
    @Resource
    private UserServiceRemoteService userServiceRemoteService;
    @Resource
    private UserAssetService userAssetService;
    @Resource
    private UserEtfSharesService userEtfSharesService;
    @Resource
    private UserEtfSharesStaticService userEtfSharesStaticService;
    @Resource
    private AccountStaticsService accountStaticsService;
    @Resource
    private RechargeServiceRemoteService rechargeServiceRemoteService;
    @Resource
    private TestRemoteService testRemoteService;
    @Autowired
    private MergeOrder mergeOrder;
    @Autowired
    private DailyClosingPriceMapper dailyClosingPriceMapper;
    @Autowired
    private TradingSupportService tradingSupportService;
    @Resource
    private AssetFundNavJobImpl assetFundNavJobImpl;
    @Resource
    private StaticAccountEtfJobImpl staticAccountEtfJobImpl;
    @Resource
    private StaticUEtfJobImpl staticUEtfJobImpl;
    @Resource
    private AccountStaticSgdJobImpl accountStaticSgdJobImpl;
    @Resource
    private UserProfitJobImpl userProfitJobImpl;
    @Resource
    private AhamRestClient ahamRestClient;
    @Resource
    private WithdrawalNotifyToAham withdrawalNotifyToAham;
    @Resource
    private Trade140_SyncPftJob trade140_SyncPftJob;
    @Resource
    private CustomerStatementJobImpl customerStatementJobImpl;
        
    private static final BigDecimal COMPARE_DOT = new BigDecimal("0.0001");

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public void calculateAssetFundNav() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        calculateAssetFundNav(DateUtils.now(), null);
    }

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
                        
                    /*if (accountAssetStatisticBean.getProductCode().equals("VT")) {
                        accountEtfSharesPO.setVt(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("EEM")) {
                        accountEtfSharesPO.setEem(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("BNDX")) {
                        accountEtfSharesPO.setBndx(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("SHV")) {
                        accountEtfSharesPO.setShv(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("EMB")) {
                        accountEtfSharesPO.setEmb(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("VWOB")) {
                        accountEtfSharesPO.setVwob(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("BWX")) {
                        accountEtfSharesPO.setBwx(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("HYG")) {
                        accountEtfSharesPO.setHyg(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("JNK")) {
                        accountEtfSharesPO.setJnk(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("MUB")) {
                        accountEtfSharesPO.setMub(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("LQD")) {
                        accountEtfSharesPO.setLqd(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("VCIT")) {
                        accountEtfSharesPO.setVcit(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("FLOT")) {
                        accountEtfSharesPO.setFlot(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("IEF")) {
                        accountEtfSharesPO.setIef(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("UUP")) {
                        accountEtfSharesPO.setUup(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("PDBC")) {
                        accountEtfSharesPO.setPdbc(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("GLD")) {
                        accountEtfSharesPO.setGld(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("VNQ")) {
                        accountEtfSharesPO.setVnq(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("VEA")) {
                        accountEtfSharesPO.setVea(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("VPL")) {
                        accountEtfSharesPO.setVpl(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("EWA")) {
                        accountEtfSharesPO.setEwa(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("SPY")) {
                        accountEtfSharesPO.setSpy(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("VOO")) {
                        accountEtfSharesPO.setVoo(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("VTI")) {
                        accountEtfSharesPO.setVti(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("VGK")) {
                        accountEtfSharesPO.setVgk(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("EWJ")) {
                        accountEtfSharesPO.setEwj(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("QQQ")) {
                        accountEtfSharesPO.setQqq(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("EWS")) {
                        accountEtfSharesPO.setEws(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("EWZ")) {
                        accountEtfSharesPO.setEwz(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("ASHR")) {
                        accountEtfSharesPO.setAshr(accountAssetStatisticBean.getProductShare());
                    }
                    if (accountAssetStatisticBean.getProductCode().equals("VWO")) {
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
                    }
                    */
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
                log.error("用户资产统计,异常。accountId:" + accountInfoPO.getId(), ex);
            }
        }
    }

    /**
     * 计算自建基金的净值,针对于每一个Account
     * <p>
     * 0、initial day ==0.1、计算资产 ==0.2、基金净值是 1 ==0.3、计算份额
     * <p>
     * 1、正负现金流都为0 ==1.1、当日总资产: Fund_Asset(t) = Total Cash + Total Equity
     * ==1.2、当日总份额: FundShares(t) = ADJ_FundShares(t-1) ==1.3、当日净值: FundNAV(t)=
     * Fund_Asset(t) / FundShares(t)
     * <p>
     * 2、只有正现金流: TPCF ==2.1、当日总资产: Fund_Asset(t) = Total Cash + Total Equity
     * ==2.2、当日净值: FundNAV(t)= (Fund_Asset(t)-TPCF(t)) / ADJ_FundShares(t-1)
     * ==2.3、放入份额: FundShares(t) = ADJ_FundShares(t-1)+TPCF(t) / FundNAV(t)
     * <p>
     * 3、只有负现金流: TNCF(该值为负值) ==3.1、临时资产: Fund_Asset(t) = Total Cash + Total
     * Equity ==3.2、临时份额: FundShares(t) = ADJ_FundShares(t-1) ==3.2、当日净值:
     * FundNAV(t)= Fund_Asset(t) / FundShares(t) ==3.3、当日份额: ADJ_FundShares(t)=
     * FundShares(t)+ TNCF(t)/ FundNAV(t-1) ==3.4、当日现金: ADJ_Cash_SAXO_USD(t)=
     * Cash_SAXO_USD(t) + Cash_Wihdraw(t) ==3.5、当日资产: ADJ_Fund_Asset(t) =
     * ADJ_Cash_SAXO_USD(t) + Current_(EV_USD(t) )
     * <p>
     * 4、既有正现金流和负现金流
     * <p>
     * 1、计算当日资产: Fund_Asset(t) = Total Cash + Total Equity 2、当日计算净值 = (当日资产 -
     * 正向资金流) / 昨日份额: (Fund_Asset(t)-TPCF(t)) / ADJ_FundShares(t-1) 3、临时份额 =
     * 昨日份额 + (正像现金流 / 当日净值) : FundShares(t) = ADJ_FundShares(t-1)+TPCF(t) /
     * FundNAV(t) 4、当日份额 = 临时份额 + (负现金流(该值为负数) / 昨日净值); ADJ_FundShares(t)=
     * FundShares(t)+ TNCF(t)/ FundNAV(t-1) 5、当日资产 = cash + 提现资产流(该值为负值) +
     * Current_(EV_USD(t) ) ***(这个值理论和统记的总资产相同)
     *
     * @param date
     */
    public void calculateAssetFundNav(Date date, Long accountId) {
        log.info("accountId:{},start NAV Calculation", accountId);
        Date assetEndTime = DateUtils.dayEnd(date);
        Date yesterday = DateUtils.addDateByDay(date, -1);
        //查询所etf的收市价格
        log.info("yesterday {} ", yesterday);
        log.info("assetFundNavService {} ", assetFundNavService);
        Map<String, BigDecimal> etfClosingPriceMap = assetFundNavService.getEtfClosingPrice(yesterday);
        log.info("{},收市价:{}", yesterday, etfClosingPriceMap);
        if (null == etfClosingPriceMap || etfClosingPriceMap.isEmpty()) {
            ErrorLogAndMailUtil.logError(log, "本日收市价为空");
            return;
        }
        //统计每个assetFund上的基金净值
        AccountInfoPO po = new AccountInfoPO();
        po.setCreateTime(assetEndTime);
        List<AccountInfoPO> accountInfoPOList = accountInfoService.listAccountInfos(po);
        for (AccountInfoPO accountInfoPO : accountInfoPOList) {
            try {
                if (accountId != null && !accountId.equals(accountInfoPO.getId())) {
                    continue;
                }
                //发事件,获取昨日fee记录，增加各种fee的资产流水(扣减:管理费、监管费、附加税费)
                NormalClientFeeReduceEvent normalFeeReduceEvent = new NormalClientFeeReduceEvent();
                normalFeeReduceEvent.setAccountId(accountInfoPO.getId());
                normalFeeReduceEvent.setDate(date);
                eventBus.post(normalFeeReduceEvent);

                log.info("accountId:{},fee calculation done，Start NAV Calculation", accountInfoPO.getId());
                AccountAssetPO queryParam = new AccountAssetPO();
                queryParam.setAccountId(accountInfoPO.getId());
                queryParam.setCreateEndTime(assetEndTime);
                List<AccountAssetPO> accountAssetPOs = accountAssetService.listAccountUnBuyAssets(queryParam);
                if (CollectionUtils.isEmpty(accountAssetPOs)) {
                    if (accountInfoPO.getId() == Long.valueOf("")) {
                        log.info("进行基金净值计算,用户资产统计,该账户没有资产,不做处理。accountId:{}", accountInfoPO.getId());
                    }
                    continue;
                }
                //查询该账号上的总资产
                BigDecimal totalAssetBeforeWithdrawal = BigDecimal.ZERO;
                BigDecimal cashHolding = BigDecimal.ZERO;
                BigDecimal totalEquit = BigDecimal.ZERO;
                List<AccountAssetStatisticBean> accountAssetStatisticBeans = AccountAssetStatistic.statAccountAsset(accountAssetPOs, etfClosingPriceMap);
                //过滤出持有中的资产
                for (AccountAssetStatisticBean accountAssetStatisticBean : accountAssetStatisticBeans) {
                    if (accountAssetStatisticBean.getProductAssetStatus() == ProductAssetStatusEnum.HOLD_ING) {
                        totalAssetBeforeWithdrawal = totalAssetBeforeWithdrawal.add(accountAssetStatisticBean.getProductMoney()).setScale(6, BigDecimal.ROUND_DOWN);
                        if (accountAssetStatisticBean.getProductCode().equals(Constants.CASH)) {
                            cashHolding = cashHolding.add(accountAssetStatisticBean.getProductMoney()).setScale(6, BigDecimal.ROUND_DOWN);
                        } else {
                            totalEquit = totalEquit.add(accountAssetStatisticBean.getProductMoney()).setScale(6, BigDecimal.ROUND_DOWN);
                        }
                    }
                }

                log.info("accountId:{},计算NAV，总资产:{}, cashHolding:{}, totalEquit:{}", accountInfoPO.getId(), totalAssetBeforeWithdrawal, cashHolding, totalEquit);
                if (totalAssetBeforeWithdrawal.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                boolean initDay = InitDayEnum.INIT_DAY == accountInfoPO.getInitDay();

                //非首单
                AccountFundNavPO queryFundNav = new AccountFundNavPO();
                queryFundNav.setAccountId(accountInfoPO.getId());
                queryFundNav.setNavTime(DateUtils.dayStart(yesterday));
                AccountFundNavPO yesterdayFundNavPO = assetFundNavService.selectOne(queryFundNav);
                if (!initDay && yesterdayFundNavPO == null) {
                    log.error("账户{},昨日净值为空", accountInfoPO.getId());
                    continue;
                }

                log.info("===========account开始计算自建基金净值:{}===========", accountInfoPO.getId());
                AccountFundNavPO todayAccountFundNav = handelAccountFundNav(accountInfoPO, totalAssetBeforeWithdrawal, initDay,
                        yesterdayFundNavPO, date, cashHolding, totalEquit, etfClosingPriceMap);
                log.info("===========account计算自建基金净值结束:{},todayAccountFundNav:{}===========", accountInfoPO.getId(), JSON.toJSONString(todayAccountFundNav));
                if (todayAccountFundNav.getTotalShare().compareTo(BigDecimal.ZERO) <= 0) {
                    //账户资产为0 表示就是initDay  设置状态为首单
                    accountInfoPO.setInitDay(InitDayEnum.INIT_DAY);
                    accountInfoService.updateOrInsert(accountInfoPO);
                    List<AccountRedeemPO> accountRedeemPOS = tpcfTncfService.getAccountTncf(accountInfoPO.getId());
                    analysisSupportService.handleTncfSuccess(accountRedeemPOS);
                    //没有资产的时候不进行fee创建
//                    NormalAccountFeeCreateEvent normalAccountFeeCreateEvent = new NormalAccountFeeCreateEvent();
//                    normalAccountFeeCreateEvent.setAccountId(accountInfoPO.getId());
//                    normalAccountFeeCreateEvent.setTotalAsset(todayAccountFundNav.getTotalAsset());
//                    normalAccountFeeCreateEvent.setDate(date);
//                    eventBus.post(normalAccountFeeCreateEvent);
                    continue;
                }

                //用户计算NAV的时候也要计算分红
                log.info("===========account开始计算用户基金净值:{}===========", accountInfoPO.getId());
                List<UserFundNavPO> userFundNavPOList = null;
                if (initDay) {
                    userFundNavPOList = handelUserFundShareInitDay(date, todayAccountFundNav);
                } else {
                    userFundNavPOList = handelUserFundShare(date, todayAccountFundNav.getFundNav(), yesterdayFundNavPO);
                }
                log.info("===========account计算用户基金净值:{},userFundNavPOList:{}===========", accountInfoPO.getId(), JSON.toJSONString(userFundNavPOList));

                if (CollectionUtils.isEmpty(userFundNavPOList)) {
                    continue;
                }
                log.info("===========account开始计算用户资产:{}===========", accountInfoPO.getId());
                List<UserAssetPO> userAssetPOs = handelUserAsset(assetEndTime, todayAccountFundNav, userFundNavPOList, etfClosingPriceMap);
                log.info("===========account计算用户资产结束:{},userAssetPOs:{}===========", accountInfoPO.getId(), JSON.toJSONString(userAssetPOs));

                AccountTpcfTncfBean accountTpcfTncfBean = getsAccountTpcfTncf(accountInfoPO.getId());
                accountInfoPO.setInitDay(InitDayEnum.UN_INIT_DAY);
                analysisSupportService.handelFundNavAndUserAsset(todayAccountFundNav, userFundNavPOList, userAssetPOs,
                        accountInfoPO, accountTpcfTncfBean);

                //发事件  监管费(0.0006),管理费(0.005),附加税费(0.07)
                NormalAccountFeeCreateEvent normalAccountFeeCreateEvent = new NormalAccountFeeCreateEvent();
                normalAccountFeeCreateEvent.setAccountId(accountInfoPO.getId());
                normalAccountFeeCreateEvent.setTotalAsset(todayAccountFundNav.getTotalAsset());
                normalAccountFeeCreateEvent.setDate(date);
                eventBus.post(normalAccountFeeCreateEvent);

            } catch (Exception ex) {
                ex.printStackTrace();
                log.error("账户:{},日期:{},计算计算自建基金净值和总份额、用户持有自建基金份额、用户持有etf份额异常:", accountInfoPO.getId(), DateUtils.getDate(), ex);
            }

        }
    }

    /**
     * 查询账户的Tpcf(recharge + dividend)和tncf
     *
     * @param accountId
     * @return
     */
    private AccountTpcfTncfBean getsAccountTpcfTncf(Long accountId) {
        AccountTpcfTncfBean accountTpcfTncfBean = new AccountTpcfTncfBean();
        //tncf
        List<AccountRedeemPO> accountRedeemPOS = tpcfTncfService.getAccountTncf(accountId);
        //Tpcf
        List<AccountRechargePO> accountRechargePOS = tpcfTncfService.getAccountTpcf(accountId);
        //account_dividend
        AccountDividendPO accountDividendParam = new AccountDividendPO();
        accountDividendParam.setAccountId(accountId);
        accountDividendParam.setHandelStatus(DividendHandelStatusEnum.DEFAULT);
        List<AccountDividendPO> accountDividendPOS = accountDividendService.listAccountDividend(accountDividendParam);
        //user_dividend
        UserDividendPO userDividendParam = new UserDividendPO();
        userDividendParam.setAccountId(accountId);
        userDividendParam.setHandelStatus(DividendHandelStatusEnum.DEFAULT);
        List<UserDividendPO> userDividendPOS = userDividendService.listUserDividend(userDividendParam);

        accountTpcfTncfBean.setAccountDividendPOS(accountDividendPOS);
        accountTpcfTncfBean.setAccountRechargePOS(accountRechargePOS);
        accountTpcfTncfBean.setAccountRedeemPOs(accountRedeemPOS);
        accountTpcfTncfBean.setUserDividendPOS(userDividendPOS);
        return accountTpcfTncfBean;
    }

    public void calculateAssetFundNavByDate(String date, Long accountId) {
        calculateAssetFundNav(DateUtils.parseDate(date), accountId);
    }

    /**
     * initDay处理用户在自建基金上的占比。
     *
     * @param date
     * @param todayFundNav
     * @return
     */
    private List<UserFundNavPO> handelUserFundShareInitDay(Date date, AccountFundNavPO todayFundNav) {
        List<UserFundNavPO> userFundNavPOList = Lists.newArrayList();

        BigDecimal totalRechargeMoney = BigDecimal.ZERO;
        Map<String, BigDecimal> userRechargeMoneyMap = Maps.newHashMap();
        AccountUserPO po = new AccountUserPO();
        po.setAccountId(todayFundNav.getAccountId());
        List<AccountUserPO> accountUserPOList = accountUserService.listByAccountUserPo(po);
        for (AccountUserPO accountUserPO : accountUserPOList) {
            BigDecimal userTpcf = getUserTdpcf(accountUserPO);
            if (userTpcf.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            BigDecimal money = userRechargeMoneyMap.get(accountUserPO.getClientId());
            if (null == money) {
                money = userTpcf;
            } else {
                money = money.add(userTpcf);
            }

            String key = accountUserPO.getClientId() + "_" + accountUserPO.getGoalId();
            userRechargeMoneyMap.put(key, money);

            totalRechargeMoney = totalRechargeMoney.add(money);
        }

        //分配基金份额占比
        for (String clientIdGoalId : userRechargeMoneyMap.keySet()) {
            BigDecimal money = userRechargeMoneyMap.get(clientIdGoalId);
            BigDecimal percent = money.divide(totalRechargeMoney, 6, BigDecimal.ROUND_DOWN);
            if (percent.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("用户clientId_goalId:{},充值金额money:{},比总金额totalMoney:{},百分比小于6位小时,不记录share",
                        clientIdGoalId, money, totalRechargeMoney);
                continue;
            }

            List<String> clientIdGoalIds = Splitter.on("_").splitToList(clientIdGoalId);
            BigDecimal rechargeShare = percent.multiply(todayFundNav.getTotalShare()).setScale(6, BigDecimal.ROUND_DOWN);
            UserFundNavPO todayUserFundNav = constractUserFundNav(rechargeShare, todayFundNav.getFundNav(),
                    todayFundNav.getAccountId(), clientIdGoalIds.get(0), clientIdGoalIds.get(1), date);
            userFundNavPOList.add(todayUserFundNav);
        }

        return userFundNavPOList;
    }

    private BigDecimal getUserTdpcf(AccountUserPO accountUserPO) {
        BigDecimal userTpcf = tpcfTncfService.getUserTpcfMoney(accountUserPO);

        UserDividendPO userDividendParam = new UserDividendPO();
        userDividendParam.setAccountId(accountUserPO.getAccountId());
        userDividendParam.setGoalId(accountUserPO.getGoalId());
        userDividendParam.setClientId(accountUserPO.getClientId());
        userDividendParam.setHandelType(DividendHandelTypeEnum.USED_NAV);
        userDividendParam.setHandelStatus(DividendHandelStatusEnum.DEFAULT);
        BigDecimal userDividendMoney = userDividendService.getUserDividendMoney(userDividendParam);
        BigDecimal navTpcfMoney = userTpcf.add(userDividendMoney).setScale(6, BigDecimal.ROUND_HALF_UP);
        log.info("getUserTdpcf,userTpcf:{},userDividendMoney:{},navTpcfMoney:{}", userTpcf, userDividendMoney, navTpcfMoney);
        return navTpcfMoney;
    }

    /**
     * 统计用户资产 : 用户的持有share占总share的百分比乘以账户持有的etf,即用户持有的etf
     *
     * @param assetEndTime
     * @param accountFundNav
     * @param userFundNavPOList
     * @param etfClosingPriceMap
     */
    private List<UserAssetPO> handelUserAsset(Date assetEndTime,
            AccountFundNavPO accountFundNav,
            List<UserFundNavPO> userFundNavPOList,
            Map<String, BigDecimal> etfClosingPriceMap) {
        AccountAssetPO queryParam = new AccountAssetPO();
        queryParam.setAccountId(accountFundNav.getAccountId());
        queryParam.setCreateEndTime(assetEndTime);
        List<AccountAssetPO> accountAssetPOs = accountAssetService.listAccountUnBuyAssets(queryParam);
        List<AccountAssetStatisticBean> accountAssetStatisticBeens = AccountAssetStatistic.statAccountAsset(accountAssetPOs, etfClosingPriceMap);
        //过滤出持有中的资产
        Map<String, AccountAssetStatisticBean> allHoldProductMap = Maps.newHashMap();
        for (AccountAssetStatisticBean accountAssetStatisticBean : accountAssetStatisticBeens) {
            if (accountAssetStatisticBean.getProductAssetStatus() == ProductAssetStatusEnum.HOLD_ING) {
                allHoldProductMap.put(accountAssetStatisticBean.getProductCode(), accountAssetStatisticBean);
            }
        }

        List<UserAssetPO> userAssetPOs = Lists.newArrayList();
        for (UserFundNavPO userFundNav : userFundNavPOList) {
            BigDecimal percent = userFundNav.getTotalShare().divide(accountFundNav.getTotalShare(), 6, BigDecimal.ROUND_DOWN);

            //统计账户层面的etf 计算用户层面的etf
            for (String productCode : allHoldProductMap.keySet()) {
                AccountAssetStatisticBean productAsset = allHoldProductMap.get(productCode);
                BigDecimal etfPrice = BigDecimal.ZERO;
                BigDecimal etfAmount = BigDecimal.ZERO;
                if (productCode.equals(Constants.CASH) || productCode.equals(Constants.UN_BUY_PRODUCT_CODE)) {
                    etfAmount = productAsset.getProductMoney().multiply(percent).setScale(6, BigDecimal.ROUND_DOWN);
                    etfPrice = BigDecimal.ONE;
                } else {
                    etfAmount = productAsset.getProductShare().multiply(percent).setScale(6, BigDecimal.ROUND_DOWN);
                    etfPrice = etfClosingPriceMap.get(productCode);
                }
                if (etfAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                UserAssetPO userAssetPO = new UserAssetPO();
                userAssetPO.setId(Sequence.next());
                userAssetPO.setAccountId(accountFundNav.getAccountId());
                userAssetPO.setAssetTime(DateUtils.dayStart(assetEndTime));
                userAssetPO.setClientId(userFundNav.getClientId());
                userAssetPO.setProductCode(productCode);
                userAssetPO.setShare(etfAmount);
                userAssetPO.setMoney(etfAmount.multiply(etfPrice).setScale(6, BigDecimal.ROUND_DOWN));
                userAssetPO.setCreateTime(DateUtils.now());
                userAssetPO.setUpdateTime(DateUtils.now());
                userAssetPO.setGoalId(userFundNav.getGoalId());
                userAssetPOs.add(userAssetPO);
            }
        }

        return userAssetPOs;
    }

    /**
     * 计算用户层面的基金净值
     *
     * @param date
     * @param todayNav
     * @param yesAccountFundNav
     */
    private List<UserFundNavPO> handelUserFundShare(Date date, BigDecimal todayNav, AccountFundNavPO yesAccountFundNav) {
        Date yesDate = DateUtils.addDays(date, -1);
        List<UserFundNavPO> userFundNavPOList = Lists.newArrayList();

        AccountUserPO po = new AccountUserPO();
        po.setAccountId(yesAccountFundNav.getAccountId());
        List<AccountUserPO> accountUserPOList = accountUserService.listByAccountUserPo(po);
        log.info("accountId:{},计算用户自建基金净值查询的用户账户关系:{}", yesAccountFundNav.getAccountId(), JSON.toJSONString(accountUserPOList));
        for (AccountUserPO accountUserPO : accountUserPOList) {
//            BigDecimal recahrgeMoney = rechargeService.getUserRechargeMoney(accountUserPO, startTime, endTime);
            BigDecimal userTpcf = getUserTdpcf(accountUserPO);
            BigDecimal rechargeShare = userTpcf.divide(todayNav, 6, BigDecimal.ROUND_DOWN);

            //获取TNCF
            BigDecimal tncfShares = tpcfTncfService.getUserTncfShares(accountUserPO);

            UserFundNavPO userFundNavPO = new UserFundNavPO();
            userFundNavPO.setClientId(accountUserPO.getClientId());
            userFundNavPO.setNavTime(yesDate);
            userFundNavPO.setAccountId(accountUserPO.getAccountId());
            userFundNavPO.setGoalId(accountUserPO.getGoalId());
            UserFundNavPO yesUserFundNav = userFundNavService.selectOneByNavTime(userFundNavPO);
            BigDecimal todayShare = BigDecimal.ZERO;
            if (null == yesUserFundNav) {
                todayShare = rechargeShare.subtract(tncfShares).setScale(6, BigDecimal.ROUND_DOWN);
            } else {
                todayShare = rechargeShare.subtract(tncfShares).add(yesUserFundNav.getTotalShare()).setScale(6, BigDecimal.ROUND_DOWN);
            }

            log.info("todayShare:{},accountId:{},clientId:{},goalId:{},统计用户净值,recahrgeMoney:{},rechargeShare:{},"
                    + "redeemShare:{}, todayNav:{}, yesUserFundNav:{}", todayShare, yesAccountFundNav.getAccountId(),
                    accountUserPO.getClientId(), accountUserPO.getGoalId(), userTpcf, rechargeShare,
                    tncfShares, todayNav, JSON.toJSONString(yesUserFundNav));

            //因为用户的totalasset是份额乘以净值，这个时候有小数舍弃，反算（totalasset除以净值）回不来了。所以有以下逻辑
            //计算用户剩余份额时候，如果剩余份额小于0.0001，全部清空,小数前四位都为0直接置0
            if (todayShare.compareTo(COMPARE_DOT) <= 0) {
                todayShare = BigDecimal.ZERO;
            }

            if (todayShare.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            UserFundNavPO todayUserFundNav = constractUserFundNav(todayShare, todayNav, accountUserPO.getAccountId(),
                    accountUserPO.getClientId(), accountUserPO.getGoalId(), date);
            userFundNavPOList.add(todayUserFundNav);
        }

        return userFundNavPOList;
    }

    private UserFundNavPO constractUserFundNav(BigDecimal todayShare, BigDecimal todayNav, Long accountId,
            String clientId, String goalId, Date date) {
        BigDecimal totalAsset = todayNav.multiply(todayShare).setScale(6, BigDecimal.ROUND_DOWN);
        UserFundNavPO userFundNavPO = new UserFundNavPO();
        userFundNavPO.setClientId(clientId);
        userFundNavPO.setFundNav(todayNav);
        userFundNavPO.setTotalAsset(totalAsset);
        userFundNavPO.setTotalShare(todayShare);
        userFundNavPO.setNavTime(DateUtils.dayStart(date));
        userFundNavPO.setAccountId(accountId);
        userFundNavPO.setCreateTime(DateUtils.now());
        userFundNavPO.setUpdateTime(DateUtils.now());
        userFundNavPO.setId(Sequence.next());
        userFundNavPO.setGoalId(goalId);
        return userFundNavPO;
    }

    private BigDecimal getAccountRedeemMoney(AccountUserPO accountUser, Date startTime, Date endTime) {
        AccountRedeemPO accountRedeemParam = new AccountRedeemPO();
        accountRedeemParam.setAccountId(accountUser.getAccountId());
        accountRedeemParam.setStartRedeemApplyTime(startTime);
        accountRedeemParam.setEndRedeemApplyTime(endTime);
        accountRedeemParam.setGoalId(accountUser.getGoalId());
        accountRedeemParam.setClientId(accountUser.getClientId());
        List<AccountRedeemPO> accountRedeemList = accountRedeemService.getRedeemListByTime(accountRedeemParam);

        BigDecimal totalApplyRedeem = BigDecimal.ZERO;
        for (AccountRedeemPO accountRedeem : accountRedeemList) {
            totalApplyRedeem = totalApplyRedeem.add(accountRedeem.getApplyMoney());
        }

        return totalApplyRedeem;
    }

    /**
     * 计算账户的自建基金净值
     *
     * @param accountInfoPO
     * @param date
     * @param etfClosingPriceMap
     */
    private AccountFundNavPO handelAccountFundNav(AccountInfoPO accountInfoPO,
            BigDecimal totalAssetBeforeWithdrawal,
            boolean isFirst,
            AccountFundNavPO yesterdayFundNavPO,
            Date date,
            BigDecimal cashHolding,
            BigDecimal totalEquit,
            Map<String, BigDecimal> etfClosingPriceMap) {
        AccountFundNavWrapperBean accountFundNavWrapperBean = new AccountFundNavWrapperBean();
        BigDecimal navTpcfMoney = getTcpf(accountInfoPO.getId());
        if (isFirst) {
            /**
             * 1、第一天的自建基金净值计算: 净值就是 1 ,份额就是总资产除以净值. 计算share = rechargeMoney
             * initDay 这里根据需求公式计算不成立即: nav = totalAssetBeforeWithdrawal / share
             * 不成立
             */
            BigDecimal share = navTpcfMoney;
            accountFundNavWrapperBean = handelInitialDay(totalAssetBeforeWithdrawal, accountInfoPO, date, cashHolding, share);
        } else {
            /**
             * 2.非首日 : 计算非首次自建基金净值
             */
            BigDecimal tncf = tpcfTncfService.getAccountTncfMoney(accountInfoPO.getId());
            if (navTpcfMoney.compareTo(BigDecimal.ZERO) == 0 && tncf.compareTo(BigDecimal.ZERO) == 0) {
                //1、正负现金流都为0
                accountFundNavWrapperBean = handelNoCashFlow(totalAssetBeforeWithdrawal, yesterdayFundNavPO, date, cashHolding);
            }
            if (navTpcfMoney.compareTo(BigDecimal.ZERO) > 0 && tncf.compareTo(BigDecimal.ZERO) <= 0) {
                //2、只有正现金流: TPCF
                accountFundNavWrapperBean = handelOnlyRechargeCashFlow(totalAssetBeforeWithdrawal, navTpcfMoney, yesterdayFundNavPO, date, cashHolding, totalEquit);
            }
            if (navTpcfMoney.compareTo(BigDecimal.ZERO) <= 0 && tncf.compareTo(BigDecimal.ZERO) > 0) {
                //3、只有负现金流: TNCF(该值为负值)
                accountFundNavWrapperBean = handelOnlyWithdrawalCashFlow(totalAssetBeforeWithdrawal, accountInfoPO, yesterdayFundNavPO, date, cashHolding, tncf);
            }
            if (navTpcfMoney.compareTo(BigDecimal.ZERO) > 0 && tncf.compareTo(BigDecimal.ZERO) > 0) {
                //4、既有正现金流和负现金流
                accountFundNavWrapperBean = handelRechargeAndRedeemCashFlow(totalAssetBeforeWithdrawal, navTpcfMoney, yesterdayFundNavPO, date, cashHolding, totalEquit, tncf, accountInfoPO);
            }

        }

        //统计中间数据处理
        handelCalFundNav(accountFundNavWrapperBean, etfClosingPriceMap, date, accountInfoPO);
        return accountFundNavWrapperBean.getAccountFundNavPO();
    }

    /**
     * 统计数据处理
     *
     * @param accountFundNavWrapperBean
     * @param etfClosingPriceMap
     * @param date
     * @param accountInfoPO
     */
    private void handelCalFundNav(AccountFundNavWrapperBean accountFundNavWrapperBean,
            Map<String, BigDecimal> etfClosingPriceMap,
            Date date,
            AccountInfoPO accountInfoPO) {
        //重算totalasset和totalcash
        AccountAssetPO queryParam = new AccountAssetPO();
        queryParam.setAccountId(accountInfoPO.getId());
        queryParam.setCreateEndTime(DateUtils.dayEnd(date));
        List<AccountAssetPO> accountAssetPOs = accountAssetService.listAccountUnBuyAssets(queryParam);
        if (CollectionUtils.isEmpty(accountAssetPOs)) {
            log.info("进行基金净值计算,用户资产统计,该账户没有资产,不做处理。accountId:{}", accountInfoPO.getId());
            throw new BusinessException("用户资产统计,该账户没有资产,不做处理。accountId:" + accountInfoPO.getId());
        }
        //查询该账号上的总资产
        List<AccountAssetStatisticBean> accountAssetStatisticBeens = AccountAssetStatistic.statAccountAsset(accountAssetPOs, etfClosingPriceMap);
        //过滤出持有中的资产
        BigDecimal totalAssetAfterFund = BigDecimal.ZERO;
        Map<String, AccountAssetStatisticBean> allHoldProductMap = Maps.newHashMap();
        for (AccountAssetStatisticBean accountAssetStatisticBean : accountAssetStatisticBeens) {
            if (accountAssetStatisticBean.getProductAssetStatus() == ProductAssetStatusEnum.HOLD_ING) {
                allHoldProductMap.put(accountAssetStatisticBean.getProductCode(), accountAssetStatisticBean);
                totalAssetAfterFund = totalAssetAfterFund.add(accountAssetStatisticBean.getProductMoney()).setScale(6, BigDecimal.ROUND_DOWN);
            }
        }
        //账户资产为0 表示就是initDay
        accountFundNavWrapperBean.getAccountFundNavPO().setTotalAsset(totalAssetAfterFund);

        //统计totalCash
        BigDecimal totalCash = BigDecimal.ZERO;
        if (allHoldProductMap.get(Constants.CASH) != null) {
            BigDecimal productMoney = allHoldProductMap.get(Constants.CASH).getProductMoney();
            totalCash = totalCash.add(productMoney);
        }
        if (allHoldProductMap.get(Constants.UN_BUY_PRODUCT_CODE) != null) {
            BigDecimal productMoney = allHoldProductMap.get(Constants.UN_BUY_PRODUCT_CODE).getProductMoney();
            totalCash = totalCash.add(productMoney);
        }
        accountFundNavWrapperBean.getAccountFundNavPO().setTotalCash(totalCash);
        accountFundNavWrapperBean.setAdjCashHolding(totalCash);

        CalFundNavEvent calFundNavEvent = new CalFundNavEvent();
        calFundNavEvent.setAccountId(accountInfoPO.getId());
        calFundNavEvent.setAdjCashHolding(totalCash);
        calFundNavEvent.setAdjFundAsset(totalAssetAfterFund);
        calFundNavEvent.setAdjFundShares(accountFundNavWrapperBean.getAdjFundShares());
        calFundNavEvent.setCashHolding(accountFundNavWrapperBean.getCashHolding());
        calFundNavEvent.setCashWithdraw(accountFundNavWrapperBean.getCashWithdraw());
        calFundNavEvent.setNavInUsd(accountFundNavWrapperBean.getNavInUsd());

        BigDecimal lastTotalAsset = BigDecimal.ZERO;
        for (AccountAssetStatisticBean accountAssetStatisticBean : accountAssetStatisticBeens) {
            if (accountAssetStatisticBean.getProductAssetStatus() == ProductAssetStatusEnum.HOLD_ING
                    && !accountAssetStatisticBean.getProductCode().equals(Constants.CASH)
                    && !accountAssetStatisticBean.getProductCode().equals(Constants.UN_BUY_PRODUCT_CODE)) {
                lastTotalAsset = lastTotalAsset.add(accountAssetStatisticBean.getProductMoney()).setScale(6, BigDecimal.ROUND_DOWN);
            }
        }
        //记录中间值
        calFundNavEvent.setTotalEquity(lastTotalAsset);
        calFundNavEvent.setTotalFundValue(accountFundNavWrapperBean.getTotalFundValue());
        calFundNavEvent.setFundShares(accountFundNavWrapperBean.getFundShares());
        eventBus.post(calFundNavEvent);
    }

    /**
     * 获取TPCF 今日充值 + 分红
     *
     * @param accountId
     * @return
     */
    private BigDecimal getTcpf(Long accountId) {
        List<AccountRechargePO> accountRechargePOS = tpcfTncfService.getAccountTpcf(accountId);
        BigDecimal accountTpcf = tpcfTncfService.getAccountTpcfMoney(accountRechargePOS);

        //获取分红
        AccountDividendPO accountDividendParam = new AccountDividendPO();
        accountDividendParam.setAccountId(accountId);
        accountDividendParam.setHandelStatus(DividendHandelStatusEnum.DEFAULT);
        BigDecimal accountDividendMoney = accountDividendService.getAccountDividendMoney(accountDividendParam);

        BigDecimal navTpcfMoney = accountTpcf.add(accountDividendMoney).setScale(6, BigDecimal.ROUND_HALF_UP);
        log.info("获取NAV的TPCF：accountId:{},navTpcfMoney:{},accountTpcf:{}", accountId, navTpcfMoney, accountTpcf);
        return navTpcfMoney;
    }

    /**
     * 既有正现金流和负现金流
     * <p>
     * 1、计算当日资产: Fund_Asset(t) = Total Cash + Total Equity 2、当日计算净值 = (当日资产 -
     * 正向资金流) / 昨日份额: (Fund_Asset(t)-TPCF(t)) / ADJ_FundShares(t-1) 3、临时份额 =
     * 昨日份额 + (正像现金流 / 当日净值) : FundShares(t) = ADJ_FundShares(t-1)+TPCF(t) /
     * FundNAV(t) 4、当日份额 = 临时份额 + (负现金流(该值为负数) / 昨日净值); ADJ_FundShares(t)=
     * FundShares(t)+ TNCF(t)/ FundNAV(t-1) 5、当日资产 = cash + 提现资产流(该值为负值) +
     * Current_(EV_USD(t) ) ***(这个值理论和统记的总资产相同)
     *
     * @param totalAsset
     * @param todayRechargeMoney
     * @param yesterdayFundNavPO
     * @param date
     */
    private AccountFundNavWrapperBean handelRechargeAndRedeemCashFlow(BigDecimal totalAsset,
            BigDecimal todayRechargeMoney,
            AccountFundNavPO yesterdayFundNavPO,
            Date date, BigDecimal cashHolding,
            BigDecimal totalEquit,
            BigDecimal tncf,
            AccountInfoPO accountInfoPO) {
        //今日净值
        BigDecimal todayNav = totalAsset.subtract(todayRechargeMoney)
                .divide(yesterdayFundNavPO.getTotalShare(), BigDecimal.ROUND_HALF_UP).setScale(6, BigDecimal.ROUND_DOWN);
        //临时份额
        //FundShares(t) = ADJ_FundShares(t-1)+TPCF(t) / FundNAV(t)
        BigDecimal tmpShare = yesterdayFundNavPO.getTotalShare().add(todayRechargeMoney.divide(todayNav, BigDecimal.ROUND_HALF_DOWN))
                .setScale(4, BigDecimal.ROUND_HALF_UP);

        //获取TNCF
        AccountRedeemPO queryParam = new AccountRedeemPO();
        queryParam.setAccountId(yesterdayFundNavPO.getAccountId());
        queryParam.setOrderStatus(RedeemOrderStatusEnum.SUCCESS);
        queryParam.setTncfStatus(TncfStatusEnum.TNCF);
        List<AccountRedeemPO> accountRedeemPOs = accountRedeemService.getRedeemListByTime(queryParam);

        //计算每个account下用户提现比例
        AccountRedeemWrapperBean redeemWrapperBean = disUserWithdraw(accountRedeemPOs, todayNav, date, accountInfoPO, totalAsset);

        BigDecimal todayShare = tmpShare.subtract(redeemWrapperBean.getTotalConfirmShares())
                .setScale(6, BigDecimal.ROUND_DOWN);

        AccountFundNavPO accountFundNavPO = constractTodayAssetFundNav(yesterdayFundNavPO.getAccountId(), todayNav, todayShare, totalAsset, date);

        AccountFundNavWrapperBean accountFundNavWrapperBean = new AccountFundNavWrapperBean();
        accountFundNavWrapperBean.setAccountFundNavPO(accountFundNavPO);
        accountFundNavWrapperBean.setTotalEquity(totalEquit);
        accountFundNavWrapperBean.setCashHolding(cashHolding);
        accountFundNavWrapperBean.setCashWithdraw(redeemWrapperBean.getAccountCashWithdrawal());
        accountFundNavWrapperBean.setFundShares(tmpShare);
        accountFundNavWrapperBean.setNavInUsd(accountFundNavPO.getFundNav());
        accountFundNavWrapperBean.setTotalFundValue(accountFundNavPO.getTotalAsset());
        accountFundNavWrapperBean.setAdjFundShares(accountFundNavPO.getTotalShare());
        accountFundNavWrapperBean.setAdjFundAsset(accountFundNavPO.getTotalAsset());

        return accountFundNavWrapperBean;

    }

    /**
     * 分配最终的提现金额
     *
     * @param accountRedeemPOListList
     * @param todayNav
     */
    private AccountRedeemWrapperBean disUserWithdraw(List<AccountRedeemPO> accountRedeemPOListList,
            BigDecimal todayNav,
            Date date,
            AccountInfoPO accountInfoPO,
            BigDecimal totalAsset) {
        BigDecimal accountTotalConfirmRedeemMoney = BigDecimal.ZERO;
        BigDecimal accountTotalApplyRedeemMoney = BigDecimal.ZERO;
        BigDecimal accountTotalConfirmShares = BigDecimal.ZERO;

        //按用户分组
        Multimap<String, AccountRedeemPO> goalAccountRedeemMap = ArrayListMultimap.create();
        for (AccountRedeemPO accountRedeem : accountRedeemPOListList) {
            String key = accountRedeem.getClientId() + "_" + accountRedeem.getGoalId();
            goalAccountRedeemMap.put(key, accountRedeem);
        }
        Set<String> goalKeySet = goalAccountRedeemMap.keySet();
        for (String goal : goalKeySet) {
            List<AccountRedeemPO> accountRedeemPOList = (List<AccountRedeemPO>) goalAccountRedeemMap.get(goal);
            //将accountredeem分为非全赎和全赎两个列表
            Iterable<AccountRedeemPO> accountRedeemNotAllRedeemList = Iterables.filter(accountRedeemPOList, new Predicate<AccountRedeemPO>() {
                @Override
                public boolean apply(@Nullable AccountRedeemPO input) {
                    return input.getRedeemType() != RedeemTypeEnum.ALLRedeem;
                }
            });
            log.info("部分提现:{}", JSON.toJSONString(accountRedeemNotAllRedeemList));
            BigDecimal goalConfirmSharesNotAll = BigDecimal.ZERO;
            for (AccountRedeemPO accountRedeem : accountRedeemNotAllRedeemList) {
                accountTotalApplyRedeemMoney = accountTotalApplyRedeemMoney.add(accountRedeem.getApplyMoney());
                //Cash_Wihdraw(t) =TNCF(t)/ FundNAV(t-1) * FundNAV(t)
                AccountFundNavPO accountNavQuery = new AccountFundNavPO();
                accountNavQuery.setAccountId(accountRedeem.getAccountId());
                accountNavQuery.setNavTime(accountRedeem.getNavDate());
                AccountFundNavPO redeemFundNav = accountNavService.selectOneByNavTime(accountNavQuery);
                BigDecimal confirmShares = accountRedeem.getApplyMoney()
                        .divide(redeemFundNav.getFundNav(), 6, BigDecimal.ROUND_DOWN);
                goalConfirmSharesNotAll = goalConfirmSharesNotAll.add(confirmShares);
                BigDecimal confirmMoney = handleAccountRedeem(accountRedeem, confirmShares, null, todayNav);
                accountTotalConfirmRedeemMoney = accountTotalConfirmRedeemMoney.add(confirmMoney);
                accountTotalConfirmShares = accountTotalConfirmShares.add(confirmShares);
            }

            Iterable<AccountRedeemPO> accountRedeemAllRedeemList = Iterables.filter(accountRedeemPOList, new Predicate<AccountRedeemPO>() {
                @Override
                public boolean apply(@Nullable AccountRedeemPO input) {
                    return input.getRedeemType() == RedeemTypeEnum.ALLRedeem;
                }
            });
            log.info("全部提现:{}", JSON.toJSONString(accountRedeemAllRedeemList));
            for (AccountRedeemPO accountRedeem : accountRedeemAllRedeemList) {
                accountTotalApplyRedeemMoney = accountTotalApplyRedeemMoney.add(accountRedeem.getApplyMoney());
                Date yesDate = DateUtils.addDays(date, -1);
                UserFundNavPO userFundNavPO = new UserFundNavPO();
                userFundNavPO.setClientId(accountRedeem.getClientId());
                userFundNavPO.setNavTime(yesDate);
                userFundNavPO.setAccountId(accountRedeem.getAccountId());
                userFundNavPO.setGoalId(accountRedeem.getGoalId());
                UserFundNavPO yesUserFundNav = userFundNavService.selectOneByNavTime(userFundNavPO);
                BigDecimal confirmShares = yesUserFundNav.getTotalShare().subtract(goalConfirmSharesNotAll);
//                BigDecimal confirmMoney = yesUserFundNav.getTotalAsset().subtract(accountTotalConfirmRedeemMoney);

                BigDecimal confirmMoney = BigDecimal.ZERO;
                //如果是tailor,全部从计算nav中资产出钱
                if (accountInfoPO.getInvestType() == AccountTypeEnum.TAILOR) {
                    totalAsset = totalAsset.subtract(accountTotalConfirmRedeemMoney);
                    confirmMoney = handleAccountRedeem(accountRedeem, confirmShares, totalAsset, todayNav);
                } else {
                    confirmMoney = handleAccountRedeem(accountRedeem, confirmShares, null, todayNav);
                }
                accountTotalConfirmRedeemMoney = accountTotalConfirmRedeemMoney.add(confirmMoney);
                accountTotalConfirmShares = accountTotalConfirmShares.add(confirmShares);
            }
        }

//        //更新accountRedeem
//        for (AccountRedeemPO accountRedeem : accountRedeemPOListList) {
//            accountTotalApplyRedeemMoney = accountTotalApplyRedeemMoney.add(accountRedeem.getApplyMoney());
//            //Cash_Wihdraw(t) =TNCF(t)/ FundNAV(t-1) * FundNAV(t)
//            AccountFundNavPO accountNavQuery = new AccountFundNavPO();
//            accountNavQuery.setAccountId(accountRedeem.getAccountId());
//            accountNavQuery.setNavTime(accountRedeem.getNavDate());
//            AccountFundNavPO redeemFundNav = accountNavService.selectOneByNavTime(accountNavQuery);
//            BigDecimal confirmShares = BigDecimal.ZERO;
//            if(accountRedeem.getRedeemType() == RedeemTypeEnum.ALLRedeem){
//                Date yesDate = DateUtils.addDays(date, -1);
//                UserFundNavPO userFundNavPO = new UserFundNavPO();
//                userFundNavPO.setClientId(accountRedeem.getClientId());
//                userFundNavPO.setNavTime(yesDate);
//                userFundNavPO.setAccountId(accountRedeem.getAccountId());
//                userFundNavPO.setGoalId(accountRedeem.getGoalId());
//                UserFundNavPO yesUserFundNav = userFundNavService.selectOneByNavTime(userFundNavPO);
//                confirmShares = yesUserFundNav.getTotalShare();
//            }else{
//                confirmShares = accountRedeem.getApplyMoney().divide(redeemFundNav.getFundNav(), 6, BigDecimal.ROUND_DOWN);
//            }
//
//            BigDecimal confirmMoney = confirmShares.multiply(todayNav).setScale(6, BigDecimal.ROUND_DOWN);
//
//            accountRedeem.setConfirmMoney(confirmMoney);
//            accountRedeem.setConfirmShares(confirmShares);
//            accountRedeem.setRedeemConfirmTime(DateUtils.now());
//            accountRedeemService.updateOrInsert(accountRedeem);
//
//            RedeemApplyPO redeemApplyPO = redeemApplyService.queryById(accountRedeem.getRedeemApplyId());
//            redeemApplyPO.setConfirmAmount(confirmMoney);
//            redeemApplyService.updateOrInsert(redeemApplyPO);
//
//            accountTotalConfirmRedeemMoney = accountTotalConfirmRedeemMoney.add(confirmMoney);
//
//            //新增cash资产流水
//            AccountAssetPO cashAsset = new AccountAssetPO();
//            cashAsset.setAssetSource(AssetSourceEnum.CASHWITHDRAWAL);
//            cashAsset.setAccountId(accountRedeem.getAccountId());
//            cashAsset.setConfirmTime(DateUtils.now());
//            cashAsset.setConfirmShare(BigDecimal.ZERO);
//            cashAsset.setApplyMoney(confirmMoney);
//            cashAsset.setConfirmMoney(confirmMoney);
//            cashAsset.setApplyTime(DateUtils.now());
//            cashAsset.setProductAssetStatus(ProductAssetStatusEnum.CONFIRM_SELL);
//            cashAsset.setProductCode(Constants.CASH);
//            cashAsset.setCreateTime(accountRedeem.getCreateTime());
//            accountAssetService.updateOrInsert(cashAsset);
//        }
//        Date yesDate = DateUtils.addDays(date, -1);
//        Date endTime = DateUtils.getDate(yesDate, 10, 0, 0);
//        Date startTime = DateUtils.addDays(endTime, -1);
//        RedeemApplyPO vaRedeemApplyPO = new RedeemApplyPO();
//        vaRedeemApplyPO.setAccountId(yesterdayFundNavPO.getAccountId());
//        vaRedeemApplyPO.setStartApplyTime(startTime);
//        vaRedeemApplyPO.setEndApplyTime(endTime);
//        vaRedeemApplyPO.setWithdrawalSourceType(WithdrawalSourceTypeEnum.FROMGOAL);
//        List<RedeemApplyPO> redeemApplyPOList = redeemApplyService.queryByApplyTime(vaRedeemApplyPO);
//        if (CollectionUtils.isEmpty(redeemApplyPOList)) {
//            log.info("{}该账号没有提现申请记录", yesterdayFundNavPO.getAccountId());
//        }
//        for (RedeemApplyPO redeemApplyPO : redeemApplyPOList) {
//            BigDecimal clientApplyRedeem = redeemApplyPO.getApplyMoney();
//            BigDecimal clientApplyPrecent = clientApplyRedeem.divide(totalApplyRedeem, 6, BigDecimal.ROUND_HALF_DOWN);
//            BigDecimal confirmMoney = accountTotalConfirmRedeemMoney.multiply(clientApplyPrecent);
//            redeemApplyPO.setConfirmAmount(confirmMoney);
//            redeemApplyService.updateOrInsert(redeemApplyPO);
//        }
        AccountRedeemWrapperBean accountRedeemWrapperBean = new AccountRedeemWrapperBean();
        accountRedeemWrapperBean.setAccountCashWithdrawal(accountTotalConfirmRedeemMoney.setScale(6, BigDecimal.ROUND_HALF_UP));
        accountRedeemWrapperBean.setTotalApplyRedeem(accountTotalApplyRedeemMoney.setScale(6, BigDecimal.ROUND_HALF_UP));
        accountRedeemWrapperBean.setTotalConfirmShares(accountTotalConfirmShares);
        return accountRedeemWrapperBean;
    }

    private BigDecimal handleAccountRedeem(AccountRedeemPO accountRedeem,
            BigDecimal confirmShares,
            BigDecimal tailorAllRedeemMoney,
            BigDecimal todayNav) {
        BigDecimal confirmMoney = BigDecimal.ZERO;
        if (tailorAllRedeemMoney != null) {
            confirmMoney = tailorAllRedeemMoney;
        } else {
            confirmMoney = confirmShares.multiply(todayNav).setScale(6, BigDecimal.ROUND_DOWN);
        }

        accountRedeem.setConfirmMoney(confirmMoney);
        accountRedeem.setConfirmShares(confirmShares);
        accountRedeem.setRedeemConfirmTime(DateUtils.now());
        accountRedeemService.updateOrInsert(accountRedeem);

        RedeemApplyPO redeemApplyPO = redeemApplyService.queryById(accountRedeem.getRedeemApplyId());
        redeemApplyPO.setConfirmAmount(confirmMoney);
        redeemApplyService.updateOrInsert(redeemApplyPO);

//        accountTotalConfirmRedeemMoney = accountTotalConfirmRedeemMoney.add(confirmMoney);
        //新增cash资产流水
        AccountAssetPO cashAsset = new AccountAssetPO();
        cashAsset.setAssetSource(AssetSourceEnum.CASHWITHDRAWAL);
        cashAsset.setAccountId(accountRedeem.getAccountId());
        cashAsset.setConfirmTime(DateUtils.now());
        cashAsset.setConfirmShare(BigDecimal.ZERO);
        cashAsset.setApplyMoney(confirmMoney);
        cashAsset.setConfirmMoney(confirmMoney);
        cashAsset.setApplyTime(DateUtils.now());
        cashAsset.setProductAssetStatus(ProductAssetStatusEnum.CONFIRM_SELL);
        cashAsset.setProductCode(Constants.CASH);
        cashAsset.setCreateTime(accountRedeem.getCreateTime());
        accountAssetService.updateOrInsert(cashAsset);
        return confirmMoney;
    }

    /**
     * 只有正向现金流
     * <p>
     * 1、计算当日资产: Fund_Asset(t) = Total Cash + Total Equity 2、计算当日净值 = (总资产 -
     * 今日cash) / 昨日份额; FundNAV(t)= (Fund_Asset(t)-TPCF(t)) / ADJ_FundShares(t-1)
     * 3、计算当日份额 = 昨日资产 + (正向资产 / 当日净值); FundShares(t) =
     * ADJ_FundShares(t-1)+TPCF(t) / FundNAV(t)
     *
     * @param totalAssetBeforeWithdrawal
     * @param rechargeAmount
     * @param yesterdayFundNavPO
     * @param date
     */
    private AccountFundNavWrapperBean handelOnlyRechargeCashFlow(BigDecimal totalAssetBeforeWithdrawal, BigDecimal rechargeAmount,
            AccountFundNavPO yesterdayFundNavPO, Date date,
            BigDecimal cashHolding,
            BigDecimal totalEquit) {

        BigDecimal todayNav = totalAssetBeforeWithdrawal.subtract(rechargeAmount)
                .divide(yesterdayFundNavPO.getTotalShare(), 6, BigDecimal.ROUND_DOWN);
        BigDecimal todayShare = yesterdayFundNavPO.getTotalShare()
                .add(rechargeAmount.divide(todayNav, 6, BigDecimal.ROUND_DOWN));

        AccountFundNavPO accountFundNavPO = constractTodayAssetFundNav(yesterdayFundNavPO.getAccountId(), todayNav, todayShare, totalAssetBeforeWithdrawal, date);

        AccountFundNavWrapperBean accountFundNavWrapperBean = new AccountFundNavWrapperBean();
        accountFundNavWrapperBean.setAccountFundNavPO(accountFundNavPO);
        accountFundNavWrapperBean.setTotalEquity(totalEquit);
        accountFundNavWrapperBean.setCashHolding(cashHolding);
        accountFundNavWrapperBean.setCashWithdraw(BigDecimal.ZERO);
        accountFundNavWrapperBean.setFundShares(accountFundNavPO.getTotalShare());
        accountFundNavWrapperBean.setNavInUsd(accountFundNavPO.getFundNav());
        accountFundNavWrapperBean.setTotalFundValue(accountFundNavPO.getTotalAsset());
        accountFundNavWrapperBean.setAdjFundShares(accountFundNavPO.getTotalShare());
        accountFundNavWrapperBean.setAdjFundAsset(accountFundNavPO.getTotalAsset());

        return accountFundNavWrapperBean;
    }

    /**
     * 3、只有负现金流: TNCF(该值为负值) ==3.1、临时资产: Fund_Asset(t) = Total Cash + Total
     * Equity ==3.2、临时份额: FundShares(t) = ADJ_FundShares(t-1) ==3.2、当日净值:
     * FundNAV(t)= Fund_Asset(t) / FundShares(t) ==3.3、当日份额: ADJ_FundShares(t)=
     * FundShares(t)+ TNCF(t)/ FundNAV(t-1) ==3.4、当日现金: ADJ_Cash_SAXO_USD(t)=
     * Cash_SAXO_USD(t) + Cash_Wihdraw(t) ==3.5、当日资产: ADJ_Fund_Asset(t) =
     * ADJ_Cash_SAXO_USD(t) + Current_(EV_USD(t) )
     */
    private AccountFundNavWrapperBean handelOnlyWithdrawalCashFlow(
            BigDecimal totalAsset,
            AccountInfoPO accountInfoPO,
            AccountFundNavPO yesterdayFundNavPO,
            Date date, BigDecimal cashHolding,
            BigDecimal tncf) {
        //获取TNCF
        Date yesDate = DateUtils.addDays(date, -1);
        //查询所有提现
        AccountRedeemPO AccountRedeemParam = new AccountRedeemPO();
        AccountRedeemParam.setAccountId(accountInfoPO.getId());
        AccountRedeemParam.setOrderStatus(RedeemOrderStatusEnum.SUCCESS);
        AccountRedeemParam.setTncfStatus(TncfStatusEnum.TNCF);
        List<AccountRedeemPO> accountRedeemList = accountRedeemService.getRedeemListByTime(AccountRedeemParam);

        BigDecimal totalApplyRedeem = BigDecimal.ZERO;
        for (AccountRedeemPO accountRedeem : accountRedeemList) {
            totalApplyRedeem = totalApplyRedeem.add(accountRedeem.getApplyMoney());
        }

        //FundNAV(t)= Fund_Asset(t) / FundShares(t)
        BigDecimal todayNav = totalAsset.divide(yesterdayFundNavPO.getTotalShare(), 6, BigDecimal.ROUND_DOWN);

        //计算account下用户提现比例
        //按账户汇总提现记录
        AccountRedeemWrapperBean redeemWrapperBean = disUserWithdraw(accountRedeemList, todayNav, date, accountInfoPO, totalAsset);

        BigDecimal todayShare = null;
        log.info("计算自建基金nav:{},{},{}", yesterdayFundNavPO.getTotalShare(), totalApplyRedeem, yesterdayFundNavPO.getFundNav());
        //是tailor的话通过accountredeem的确认份额进行计算
        todayShare = yesterdayFundNavPO.getTotalShare().subtract(redeemWrapperBean.getTotalConfirmShares()).setScale(6, BigDecimal.ROUND_DOWN);

        //只有tncf需要判断是否全赎
        if (yesterdayFundNavPO.getTotalAsset().setScale(2, BigDecimal.ROUND_DOWN).equals(totalApplyRedeem.setScale(2, BigDecimal.ROUND_DOWN))) {
            todayShare = BigDecimal.ZERO;
        }

        AccountFundNavPO accountFundNavPO = constractTodayAssetFundNav(yesterdayFundNavPO.getAccountId(), todayNav, todayShare, totalAsset, date);

        AccountFundNavWrapperBean accountFundNavWrapperBean = new AccountFundNavWrapperBean();
        accountFundNavWrapperBean.setAccountFundNavPO(accountFundNavPO);
//        accountFundNavWrapperBean.setTotalEquity(totalFundValue);
        accountFundNavWrapperBean.setCashHolding(cashHolding);
        accountFundNavWrapperBean.setCashWithdraw(redeemWrapperBean.getAccountCashWithdrawal());
        accountFundNavWrapperBean.setFundShares(yesterdayFundNavPO.getTotalShare());
        accountFundNavWrapperBean.setNavInUsd(accountFundNavPO.getFundNav());
        accountFundNavWrapperBean.setTotalFundValue(accountFundNavPO.getTotalAsset());
        accountFundNavWrapperBean.setAdjFundShares(accountFundNavPO.getTotalShare());
        accountFundNavWrapperBean.setAdjFundAsset(accountFundNavPO.getTotalAsset());

        return accountFundNavWrapperBean;
    }

    /**
     * 无现金流:1、正负现金流都为0
     * <p>
     * 1.计算当日总资产 : Fund_Asset(t) = Total Cash + Total Equity 2.当日份额即是昨日份额 :
     * FundShares(t) = ADJ_FundShares(t-1) 3.当日进制按照公式计算 : FundNAV(t)=
     * Fund_Asset(t) / FundShares(t)
     *
     * @param totalAssetBeforeWithdrawal
     * @param yesterdayFundNavPO
     * @param date
     */
    private AccountFundNavWrapperBean handelNoCashFlow(BigDecimal totalAssetBeforeWithdrawal, AccountFundNavPO yesterdayFundNavPO, Date date, BigDecimal cashHolding) {
        BigDecimal yesterdayShare = yesterdayFundNavPO.getTotalShare();
        BigDecimal todayNav = totalAssetBeforeWithdrawal.divide(yesterdayShare, 6, BigDecimal.ROUND_DOWN);
        AccountFundNavPO accountFundNavPO = constractTodayAssetFundNav(yesterdayFundNavPO.getAccountId(), todayNav, yesterdayShare, totalAssetBeforeWithdrawal, date);

        AccountFundNavWrapperBean accountFundNavWrapperBean = new AccountFundNavWrapperBean();
        accountFundNavWrapperBean.setAccountFundNavPO(accountFundNavPO);
//        accountFundNavWrapperBean.setTotalEquity(totalFundValue);
        accountFundNavWrapperBean.setCashHolding(cashHolding);
        accountFundNavWrapperBean.setCashWithdraw(BigDecimal.ZERO);
        accountFundNavWrapperBean.setFundShares(yesterdayFundNavPO.getTotalShare());
        accountFundNavWrapperBean.setNavInUsd(accountFundNavPO.getFundNav());
        accountFundNavWrapperBean.setTotalFundValue(accountFundNavPO.getTotalAsset());
        accountFundNavWrapperBean.setAdjFundShares(accountFundNavPO.getTotalShare());
        accountFundNavWrapperBean.setAdjFundAsset(accountFundNavPO.getTotalAsset());
        return accountFundNavWrapperBean;
    }

    /**
     * initial day 计算基金净值
     *
     * @param totalAssetBeforeWithdrawal
     * @param accountInfoPO
     * @param date
     */
    private AccountFundNavWrapperBean handelInitialDay(BigDecimal totalAssetBeforeWithdrawal,
            AccountInfoPO accountInfoPO,
            Date date,
            BigDecimal cashHolding,
            BigDecimal share) {
        AccountFundNavWrapperBean accountFundNavWrapperBean = new AccountFundNavWrapperBean();
        BigDecimal todayNav = Constants.INIT_FUND_NAV;

        AccountFundNavPO accountFundNavPO = constractTodayAssetFundNav(accountInfoPO.getId(), todayNav, share, totalAssetBeforeWithdrawal, date);

        accountFundNavWrapperBean.setAccountFundNavPO(accountFundNavPO);
//        accountFundNavWrapperBean.setTotalEquity(totalFundValue);
        accountFundNavWrapperBean.setCashHolding(cashHolding);
        accountFundNavWrapperBean.setCashWithdraw(BigDecimal.ZERO);
        accountFundNavWrapperBean.setFundShares(accountFundNavPO.getTotalShare());
        accountFundNavWrapperBean.setNavInUsd(accountFundNavPO.getFundNav());
        accountFundNavWrapperBean.setTotalFundValue(accountFundNavPO.getTotalAsset());
        accountFundNavWrapperBean.setAdjFundShares(accountFundNavPO.getTotalShare());
        accountFundNavWrapperBean.setAdjFundAsset(accountFundNavPO.getTotalAsset());
        return accountFundNavWrapperBean;
    }

    private BigDecimal getAccountRecharge(List<AccountAssetPO> accountAssetPOs) {
        BigDecimal confirmMoney = BigDecimal.ZERO;
        for (AccountAssetPO accountAssetPO : accountAssetPOs) {
            if (accountAssetPO.getConfirmMoney().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            confirmMoney = confirmMoney.add(accountAssetPO.getConfirmMoney()).setScale(4, BigDecimal.ROUND_HALF_UP);
        }
        return confirmMoney;
    }

    private AccountFundNavPO constractTodayAssetFundNav(Long accountId, BigDecimal nav, BigDecimal totalShare, BigDecimal totalAssetBeforeWithdrawal, Date date) {
        AccountFundNavPO todayFundNav = new AccountFundNavPO();
        todayFundNav.setNavTime(DateUtils.dayStart(date));
        todayFundNav.setAccountId(accountId);
        todayFundNav.setTotalShare(totalShare);
        todayFundNav.setFundNav(nav);
        todayFundNav.setCreateTime(DateUtils.now());
        todayFundNav.setUpdateTime(DateUtils.now());
        todayFundNav.setTotalAsset(totalAssetBeforeWithdrawal);
        todayFundNav.setId(Sequence.next());
        return todayFundNav;
    }

    public void staticUserEtfJobByDate(Date date) {

        //获取所有用户
        List<UserInfoResDTO> userInfoResDTOS = userServiceRemoteService.queryUserList();
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
                if (date == null) {
                    userAssetPO.setAssetTime(new Date());
                } else {
                    userAssetPO.setAssetTime(date);
                }
                List<UserAssetPO> userAssetPOList = userAssetService.queryListByTime(userAssetPO);
                handleEtf(userAssetPOList, date);
            }
        }
    }

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
                handleEtf(userAssetPOList, date);
            }

        }
    }

    //统计用户的etf持有
    public List<UserEtfSharesStaticPO> handleEtf(List<UserAssetPO> userAssetPOs, Date date) {
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

            if (date == null) {
                userEtfSharesPO.setStaticDate(new Date());
            } else {
                userEtfSharesPO.setStaticDate(date);
                userEtfSharesPO.setStaticDate(date);
            }

            UserEtfSharesPO userEtfSharesQuery = new UserEtfSharesPO();
            userEtfSharesQuery.setAccountId(userAsset.getAccountId());
            userEtfSharesQuery.setClientId(userAsset.getClientId());
            userEtfSharesQuery.setGoalId(userAsset.getGoalId());
            userEtfSharesQuery.setProductCode(userAsset.getProductCode());
            if (date == null) {
                userEtfSharesQuery.setStaticDate(new Date());
            } else {
                userEtfSharesPO.setStaticDate(date);
                userEtfSharesQuery.setStaticDate(date);
            }

            UserEtfSharesPO userEtfShares = userEtfSharesService.selectByStaticDate(userEtfSharesQuery);
            if (userEtfShares != null) {
                userEtfSharesPO.setId(userEtfShares.getId());
            }
            userEtfSharesService.updateOrInsert(userEtfSharesPO);

            UserEtfSharesStaticPO userEtfSharesStaticPO = new UserEtfSharesStaticPO();
            userEtfSharesStaticPO.setAccountId(userAsset.getAccountId());
            userEtfSharesStaticPO.setClientId(userAsset.getClientId());
            userEtfSharesStaticPO.setGoalId(userAsset.getGoalId());
            if (date == null) {
                userEtfSharesStaticPO.setStaticDate(new Date());
            } else {
                userEtfSharesPO.setStaticDate(date);
                userEtfSharesStaticPO.setStaticDate(date);
            }

            /*if (userAsset.getProductCode().equals("VT")) {
                userEtfSharesStaticPO.setVt(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("EEM")) {
                userEtfSharesStaticPO.setEem(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("BNDX")) {
                userEtfSharesStaticPO.setBndx(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SHV")) {
                userEtfSharesStaticPO.setShv(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("EMB")) {
                userEtfSharesStaticPO.setEmb(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("VWOB")) {
                userEtfSharesStaticPO.setVwob(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("BWX")) {
                userEtfSharesStaticPO.setBwx(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("HYG")) {
                userEtfSharesStaticPO.setHyg(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("JNK")) {
                userEtfSharesStaticPO.setJnk(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("MUB")) {
                userEtfSharesStaticPO.setMub(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("LQD")) {
                userEtfSharesStaticPO.setLqd(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("VCIT")) {
                userEtfSharesStaticPO.setVcit(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("FLOT")) {
                userEtfSharesStaticPO.setFlot(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("IEF")) {
                userEtfSharesStaticPO.setIef(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("UUP")) {
                userEtfSharesStaticPO.setUup(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("PDBC")) {
                userEtfSharesStaticPO.setPdbc(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("GLD")) {
                userEtfSharesStaticPO.setGld(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("VNQ")) {
                userEtfSharesStaticPO.setVnq(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("VEA")) {
                userEtfSharesStaticPO.setVea(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("VPL")) {
                userEtfSharesStaticPO.setVpl(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("EWA")) {
                userEtfSharesStaticPO.setEwa(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SPY")) {
                userEtfSharesStaticPO.setSpy(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("VOO")) {
                userEtfSharesStaticPO.setVoo(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("VTI")) {
                userEtfSharesStaticPO.setVti(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("VGK")) {
                userEtfSharesStaticPO.setVgk(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("EWJ")) {
                userEtfSharesStaticPO.setEwj(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("QQQ")) {
                userEtfSharesStaticPO.setQqq(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("EWS")) {
                userEtfSharesStaticPO.setEws(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("EWZ")) {
                userEtfSharesStaticPO.setEwz(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("ASHR")) {
                userEtfSharesStaticPO.setAshr(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("VWO")) {
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
            }
*/
            userEtfSharesStaticList.add(userEtfSharesStaticPO);

            UserEtfSharesStaticPO userEtfSharesStaticQuery = new UserEtfSharesStaticPO();
            if (date == null) {
                userEtfSharesStaticQuery.setStaticDate(new Date());
            } else {
                userEtfSharesPO.setStaticDate(date);
                userEtfSharesStaticQuery.setStaticDate(date);
            }
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

    //统计用户的etf持有
    public List<UserEtfSharesStaticPO> handleEtf(List<UserAssetPO> userAssetPOs) {
        return handleEtf(userAssetPOs, null);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PortLevelServiceImpl.class);

    @Autowired
    private PortLevelMapper portLevelMapper;
    @Resource
    private ModelRecommendService modelRecommendService;

    private static final Splitter SPLITTER = Splitter.on(".").trimResults();

    public List<PortLevel> getPortLevels(PortLevelDTO portLevelDTO) {
        PortLevel portLevel = new PortLevel();
        portLevel.setPortfolioId(portLevelDTO.getPortfolioId());
        return portLevelMapper.getPortLevels(portLevel);
    }

    public void synchroPortLevel() {
        String fileUrl = Constants.FTP_BASE_FOLDER + "/PORTLEVEL/";
        SftpClient sftpClient = SftpClient.connect("3.0.163.17", 22, "ftpuser", "OmMsi93DBcNo", 5000, 10);
        List<String> errorFiles = com.beust.jcommander.internal.Lists.newArrayList();
        InputStream stream = null;
        try {
            for (PoolingEnum poolingEnum : PoolingEnum.values()) {
                for (RiskLevelEnum riskEnum : RiskLevelEnum.values()) {
                    for (AgeLevelEnum ageEnum : AgeLevelEnum.values()) {
                        LOGGER.info("#####pooling:{},riskLevel:{},ageLevel:{},开始进行走势图的同步",
                                poolingEnum.getValue(), riskEnum.getValue(), ageEnum.getValue());
                        String fileName = poolingEnum.getName() + "_NAV_Tracking_" + riskEnum.getName() + "_" + ageEnum.getName() + ".csv";
                        String portfolioId = "P" + poolingEnum.getValue() + "R" + riskEnum.getValue() + "A" + ageEnum.getValue();
                        try {
                            Date now = DateUtils.now();
                            Date yesterDay = DateUtils.dayStart(DateUtils.addDays(now, -1));
                            String filePath = fileUrl + fileName;
//                            filePath = PropertiesUtil.getString("ftp.jimubox") + File.separator + filePath;
                            LOGGER.info("####PortLevel_filePath:{}", filePath);
//                            List<String> lines = FTPClientUtil.readFileContent(filePath);

                            List<String> lines = new ArrayList();
                            try {
                                stream = sftpClient.get(filePath);
                                String thisLine = "";
                                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                                while ((thisLine = br.readLine()) != null) {
                                    lines.add(thisLine);
                                }
                            } finally {

                            }
                            //获取最新的记录
                            String[] cols = lines.get(lines.size() - 1).split(",");
                            Date lastModelDate = DateUtils.parseDate(cols[0], DateUtils.DATE_FORMAT2);
                            if (yesterDay.compareTo(lastModelDate) != 0) {
                                String fileNotFound = "请检查ftp服务器文件:" + filePath + ",中是否更新数据";
                                LOGGER.error(fileNotFound);
                                throw new BusinessException(fileNotFound);
                            }
                            PortLevel portLevel = new PortLevel();
                            Boolean vooTenDays = false;
                            if (cols[7].equals("1")) {
                                vooTenDays = true;
                            }
                            portLevel.setModelDate(lastModelDate)
                                    .setPortfolioLevel(new BigDecimal(cols[1]))
                                    .setMaxDD(new BigDecimal(cols[2]))
                                    .setVol(new BigDecimal(cols[3]))
                                    .setReturnVol(new BigDecimal(cols[4]))
                                    .setRebalance(RebalanceEnum.forValue(Integer.parseInt(cols[5])))
                                    .setBenchmarkData(new BigDecimal(cols[6]))
                                    .setVooTenDays(vooTenDays)
                                    .setPortfolioId(portfolioId)
                                    .setCreateTime(now)
                                    .setUpdateTime(now)
                                    .setId(Sequence.next());
                            PortLevel queryParam = new PortLevel();
                            queryParam.setPortfolioId(portLevel.getPortfolioId());
                            queryParam.setModelDate(portLevel.getModelDate());
                            PortLevel oldPortLevel = portLevelMapper.getPortLevel(queryParam);
                            if (null == oldPortLevel) {
                                portLevelMapper.insertBatch(com.beust.jcommander.internal.Lists.newArrayList(portLevel));
                            } else {
                                portLevel.setId(oldPortLevel.getId());
                                portLevelMapper.updatePortLevel(portLevel);
                            }
                            lines = null;
                        } catch (Exception ex) {
                            LOGGER.error("fileUrl:{},fileName:{}, 同步走势图异常", fileUrl, fileName, ex);
                            errorFiles.add(ex.getMessage());
//                            modelRecommendService.sendFtpFileNotFound(ExceptionUtils.getMessage(ex));
                        }
                        LOGGER.info("#####fileUrl:{},fileName:{},进行走势图的同步完成", fileUrl, fileName);

                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error("#####同步用户收益取消异常:", ex);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
                if (sftpClient != null) {
                    sftpClient.disconnect();
                }
            } catch (Exception e) {

            }
        }
        if (CollectionUtils.isNotEmpty(errorFiles)) {
            ErrorLogAndMailUtil.logError(LOGGER, errorFiles);
        }

    }

    public List<String> readLine(String file) {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        List<String> lines = new ArrayList();

        int counter = 0;
        try {
            br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                if (counter == 0) {
                    counter++;
                    continue;
                }
                // use comma as separator
                counter++;
                lines.add(line);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return lines;
    }

    public PortLevel getPortLevel(PortLevel portLevelParam) {
        return portLevelMapper.getPortLevel(portLevelParam);
    }

    public PortLevel getLastPortLevel(String portfolioId) {
        return portLevelMapper.getLastPortLevel(portfolioId);
    }

    public void portLevelInit() {
        FTPUtil ftpUtil = new FTPUtil();
        String fileUrl = Constants.FTP_BASE_FOLDER + "/PORTLEVEL/";
        try {
            for (PoolingEnum poolingEnum : PoolingEnum.values()) {
                for (RiskLevelEnum riskEnum : RiskLevelEnum.values()) {
                    for (AgeLevelEnum ageEnum : AgeLevelEnum.values()) {
                        LOGGER.info("#####pooling:{},riskLevel:{},ageLevel:{},开始进行走势图的init",
                                poolingEnum.getDesc(), riskEnum.getDesc(), ageEnum.getDesc());
                        String fileName = poolingEnum.getName() + "_NAV_Tracking_" + riskEnum.getName() + "_" + ageEnum.getName() + ".csv";
                        String portfolioId = "P" + poolingEnum.getValue() + "R" + riskEnum.getValue() + "A" + ageEnum.getValue();
                        try {
                            Date now = DateUtils.now();
                            String filePath = fileUrl + fileName;
//                            List<String> lines = FTPClientUtil.readFileContent(filePath);

                            List<String> lines = ftpUtil.readFileContent(filePath);

                            if (CollectionUtils.isEmpty(lines)) {
                                continue;
                            }
                            List<PortLevel> portLevels = com.beust.jcommander.internal.Lists.newArrayList();
                            for (String line : lines) {
                                String[] cols = line.split(",");
                                Date lastModelDate = DateUtils.parseDate(cols[0], DateUtils.DATE_FORMAT2);

                                PortLevel portLevel = new PortLevel();
                                Boolean vooTenDays = false;
                                if (cols[7].equals("1")) {
                                    vooTenDays = true;
                                }
                                portLevel.setModelDate(lastModelDate)
                                        .setPortfolioLevel(new BigDecimal(cols[1]))
                                        .setMaxDD(new BigDecimal(cols[2]))
                                        .setVol(new BigDecimal(cols[3]))
                                        .setReturnVol(new BigDecimal(cols[4]))
                                        .setRebalance(RebalanceEnum.forValue(Integer.parseInt(cols[5])))
                                        .setBenchmarkData(new BigDecimal(cols[6]))
                                        .setPortfolioId(portfolioId)
                                        .setVooTenDays(vooTenDays)
                                        .setCreateTime(now)
                                        .setUpdateTime(now)
                                        .setId(Sequence.next());
                                PortLevel queryParam = new PortLevel();
                                queryParam.setPortfolioId(portLevel.getPortfolioId());
                                queryParam.setModelDate(portLevel.getModelDate());
                                PortLevel oldPortLevel = portLevelMapper.getPortLevel(queryParam);
                                if (null == oldPortLevel) {
                                    portLevels.add(portLevel);
                                }
                            }
                            portLevelMapper.insertBatch(portLevels);
                        } catch (Exception ex) {
                            LOGGER.error("fileUrl:{},fileName:{}, 同步走势图异常", fileUrl, fileName, ex);
                            modelRecommendService.sendFtpFileNotFound(ExceptionUtils.getMessage(ex));
                        }
                        LOGGER.info("#####fileUrl:{},fileName:{},进行走势图的同步完成", fileUrl, fileName);
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error("#####同步用户收益取消异常:", ex);
        } finally {
            ftpUtil.free();
        }
    }

    public void updateSgd(Long accountId, Date date) {
        Date calDate = null;
        if (date == null) {
            calDate = DateUtils.now();
        } else {
            calDate = date;
        }

        AccountInfoPO accountInfoPO = new AccountInfoPO();
        List<AccountInfoPO> accountInfoPOList = accountInfoService.queryList(accountInfoPO);
        for (AccountInfoPO accountInfo : accountInfoPOList) {
            if (accountId != null && !accountInfo.getId().equals(accountId)) {
                continue;
            }

            ExchangeRatePO exchangeRateParam = new ExchangeRatePO();
            exchangeRateParam.setRateDate(DateUtils.dayStart(calDate));
            exchangeRateParam.setExchangeRateType(ExchangeRateTypeEnum.SAXO_FXRT2);
            ExchangeRatePO exchangeRatePO = exchangeRateService.getExchangeRate(exchangeRateParam);
            AccountStaticsPO accountStaticsQuery = new AccountStaticsPO();
            accountStaticsQuery.setAccountId(accountInfo.getId());
            Date yesterDay = DateUtils.addDateByDay(calDate, -1);
            accountStaticsQuery.setStaticDate(yesterDay);
            AccountStaticsPO accountStaticsPO = accountStaticsService.selectByStaticDate(accountStaticsQuery);
            log.info("accountStaticsPO {} ", accountStaticsPO);
            if (null != exchangeRatePO && accountStaticsPO != null) {
                AccountStaticsPO accountStaticsUpdate = new AccountStaticsPO();
                BigDecimal fxRateUsd = exchangeRatePO.getUsdToSgd();
                BigDecimal navInSgd = accountStaticsPO.getNavInUsd().multiply(fxRateUsd);
                BigDecimal adjFundAssetInSgd = accountStaticsPO.getAdjFundShares().multiply(navInSgd);
                BigDecimal cashWithdrawInSgd = accountStaticsPO.getCashWithdraw().multiply(fxRateUsd);

                accountStaticsUpdate.setId(accountStaticsPO.getId());
                accountStaticsUpdate.setNavInSgd(navInSgd);
                accountStaticsUpdate.setAdjFundAssetInSgd(adjFundAssetInSgd);
                accountStaticsUpdate.setCashWithdrawInSgd(cashWithdrawInSgd);
                accountStaticsUpdate.setFxRateForClearing(fxRateUsd);
                accountStaticsUpdate.setFxRateForFundOut(fxRateUsd);

                AccountStaticsPO accountStaticsAfterUpdate = accountStaticsService.updateOrInsert(accountStaticsUpdate);
                log.info("accountStaticsAfterUpdate {} ", accountStaticsAfterUpdate);
                UserStaticsEvent userStaticsEvent = new UserStaticsEvent();
                userStaticsEvent.setAccountStaticsPO(accountStaticsAfterUpdate);
                eventBus.post(userStaticsEvent);

            } else {
                log.error("Account{},Cannot find T2 Exchange Rate Or Yesterday statics empty", accountInfo.getId());
            }

            if (accountInfo.getInitDay() == InitDayEnum.INIT_DAY) {
                continue;
            }
        }

    }

    public PortLevel getFirstPortLevel(String portfolioId) {
        return portLevelMapper.getFirstPortLevel(portfolioId);
    }

    /**
     * 1.	Total Return: Current Asset Value (USD) – sum of each investment (USD)
     * + sum of each withdraw(USD) 2.	Portfolio Return: Current Asset Value
     * (USD) – sum of each investment (USD) + sum of each withdraw(USD) 3.	FX
     * Impact =0
     * <p>
     * 这里的新币的提现和充值在 saxo_account_order 里查询，新币查询：
     * 1、新币的充值：SaxoOrderTradeTypeEnum：1 + SaxoOrderActionTypeEnum：6
     * 2、新币的提现：SaxoOrderTradeTypeEnum：2 + SaxoOrderActionTypeEnum：7
     *
     * @param userFundNavPO
     * @param accountUserPO
     * @return
     */
    private UserGoalProfitWrapper getSgdGoalProfitInfo(UserFundNavPO userFundNavPO, AccountUserPO accountUserPO) {
        return getSgdGoalProfitInfo(userFundNavPO, accountUserPO, null);
    }

    private UserGoalProfitWrapper getSgdGoalProfitInfo(UserFundNavPO userFundNavPO, AccountUserPO accountUserPO, Date date) {
        UserGoalProfitWrapper wrapper = new UserGoalProfitWrapper();
        ExchangeRatePO exchangeRateParam = new ExchangeRatePO();
        if (date == null) {
            exchangeRateParam.setRateDate(DateUtils.dayStart(DateUtils.now()));
        } else {
            exchangeRateParam.setRateDate(DateUtils.dayStart(date));
        }
        exchangeRateParam.setExchangeRateType(ExchangeRateTypeEnum.SAXO_FXRT2);
        ExchangeRatePO exchangeRatePO = exchangeRateService.getExchangeRate(exchangeRateParam);
        BigDecimal sgdTotalMoney = userFundNavPO.getTotalAsset().multiply(exchangeRatePO.getUsdToSgd()).setScale(6, BigDecimal.ROUND_HALF_UP);
        log.info("userProfit:accountId:{},goalId:{},clientId:{},sgdTotalMoney:{}", accountUserPO.getAccountId(), userFundNavPO.getGoalId(), userFundNavPO.getClientId(), sgdTotalMoney);
        //新币的入金
        SaxoAccountOrderPO sgdRechargeParam = new SaxoAccountOrderPO();
        sgdRechargeParam.setAccountId(accountUserPO.getAccountId());
        sgdRechargeParam.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
        sgdRechargeParam.setOperatorType(SaxoOrderTradeTypeEnum.COME_INTO);
        sgdRechargeParam.setActionType(SaxoOrderActionTypeEnum.UOBTOSAXO);
        sgdRechargeParam.setCurrency(CurrencyEnum.SGD);
        sgdRechargeParam.setClientId(accountUserPO.getClientId());
        sgdRechargeParam.setGoalId(accountUserPO.getGoalId());
        BigDecimal sgdRechargeMoney = saxoAccountOrderService.getClientGoalMoney(sgdRechargeParam);
        log.info("userProfit:accountId:{},goalId:{},clientId:{},rechargeMoney:{}", accountUserPO.getAccountId(), userFundNavPO.getGoalId(), userFundNavPO.getClientId(), sgdRechargeMoney);
        //新币的提现
        SaxoAccountOrderPO sgdRedeemParam = new SaxoAccountOrderPO();
        sgdRedeemParam.setAccountId(accountUserPO.getAccountId());
        sgdRedeemParam.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
        sgdRedeemParam.setOperatorType(SaxoOrderTradeTypeEnum.COME_OUT);
        sgdRedeemParam.setActionType(SaxoOrderActionTypeEnum.SAXOTOUOB);
        sgdRedeemParam.setCurrency(CurrencyEnum.SGD);
        sgdRedeemParam.setGoalId(accountUserPO.getGoalId());
        sgdRedeemParam.setClientId(accountUserPO.getClientId());
        BigDecimal sgdRedeemMoney = saxoAccountOrderService.getClientGoalMoney(sgdRedeemParam);
        log.info("userProfit:accountId:{},goalId:{},clientId:{},redeemMoney:{}", accountUserPO.getAccountId(), userFundNavPO.getGoalId(), userFundNavPO.getClientId(), sgdRedeemMoney);
        BigDecimal totalProfit = sgdTotalMoney.add(sgdRedeemMoney).subtract(sgdRechargeMoney).setScale(2, BigDecimal.ROUND_HALF_UP);
        UserGoalProfitWrapper usdWrapper = getUsdGoalProfitInfo(userFundNavPO);
        BigDecimal fixImpact = totalProfit.subtract(usdWrapper.getPortfolioProfit()).setScale(2, BigDecimal.ROUND_HALF_UP);

        wrapper.setTotalProfit(totalProfit);
        wrapper.setPortfolioProfit(usdWrapper.getPortfolioProfit());
        wrapper.setFxImpact(fixImpact);
        log.info("userProfit:accountId:{},goalId:{},clientId:{},wrapper:{}", accountUserPO.getAccountId(), userFundNavPO.getGoalId(), userFundNavPO.getClientId(), JSON.toJSONString(wrapper));
        return wrapper;
    }

    /**
     * 1.	Total Return: Current Asset Value (SGD) – sum of each investment (SGD)
     * + sum of each withdraw(SGD) 2.	Portfolio Return: Current Asset Value
     * (USD) – sum of each investment (USD) + sum of each withdraw(USD) 3.	FX
     * Impact = Total Return - Portfolio Return
     * <p>
     * 这里的美金的提现和充值在 saxo_account_order 里查询，美金查询：
     * 1、美金的充值：SaxoOrderTradeTypeEnum：1 + SaxoOrderActionTypeEnum：4
     * 2、美金的提现：SaxoOrderTradeTypeEnum：2 + SaxoOrderActionTypeEnum：1
     *
     * @param userAssetPO
     * @return
     */
    private UserGoalProfitWrapper getUsdGoalProfitInfo(UserFundNavPO userAssetPO) {
        //总提现  (美金)
        SaxoAccountOrderPO usdRedeemParam = new SaxoAccountOrderPO();
        usdRedeemParam.setAccountId(userAssetPO.getAccountId());
        usdRedeemParam.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
        usdRedeemParam.setOperatorType(SaxoOrderTradeTypeEnum.COME_OUT);
        usdRedeemParam.setActionType(SaxoOrderActionTypeEnum.REDEEM_EXCHANGE);
        usdRedeemParam.setCurrency(CurrencyEnum.USD);
        usdRedeemParam.setClientId(userAssetPO.getClientId());
        usdRedeemParam.setGoalId(userAssetPO.getGoalId());
        BigDecimal redeemMoney = saxoAccountOrderService.getClientGoalMoney(usdRedeemParam);

        //总入金  (美金)
        SaxoAccountOrderPO usdRechargeParam = new SaxoAccountOrderPO();
        usdRechargeParam.setAccountId(userAssetPO.getAccountId());
        usdRechargeParam.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
        usdRechargeParam.setOperatorType(SaxoOrderTradeTypeEnum.COME_INTO);
        usdRechargeParam.setActionType(SaxoOrderActionTypeEnum.RECHARGE_EXCHANGE);
        usdRechargeParam.setCurrency(CurrencyEnum.USD);
        usdRechargeParam.setGoalId(userAssetPO.getGoalId());
        usdRechargeParam.setClientId(userAssetPO.getClientId());
        BigDecimal rechargeMoney = saxoAccountOrderService.getClientGoalMoney(usdRechargeParam);

        BigDecimal profit = userAssetPO.getTotalAsset().add(redeemMoney).subtract(rechargeMoney).setScale(2, BigDecimal.ROUND_HALF_UP);

        UserGoalProfitWrapper wrapper = new UserGoalProfitWrapper();
        wrapper.setTotalProfit(profit);
        wrapper.setPortfolioProfit(profit);
        wrapper.setFxImpact(profit.subtract(profit).setScale(6, BigDecimal.ROUND_HALF_UP));
        return wrapper;
    }

    private void calculateUserProfit(Date date) {
        //1、查新当天的UserFundNAv
        UserFundNavPO userFundNavParam = new UserFundNavPO();
        userFundNavParam.setNavTime(DateUtils.dayStart(date));
        List<UserFundNavPO> userFundNavPOS = userFundNavService.listUserFundNav(userFundNavParam);
        log.info("计算用户收益信息,userFundNavPOS:{}", JSON.toJSONString(userFundNavPOS));
        //2、计算用户收益
        for (UserFundNavPO userFundNavPO : userFundNavPOS) {
            AccountUserPO accountUserParam = new AccountUserPO();
            accountUserParam.setAccountId(userFundNavPO.getAccountId());
            accountUserParam.setClientId(userFundNavPO.getClientId());
            accountUserParam.setGoalId(userFundNavPO.getGoalId());
            AccountUserPO accountUserPO = accountUserService.queryAccountUser(accountUserParam);
            UserGoalProfitWrapper wrapper = null;
            if (CurrencyEnum.USD == accountUserPO.getFirstRechargeCurrency()) {
                //美金的处理profit收益公式
//                wrapper = getUsdGoalProfitInfo(userFundNavPO);
                wrapper = getSgdGoalProfitInfo(userFundNavPO, accountUserPO, date);
            } else {
                //新币的公式
                wrapper = getSgdGoalProfitInfo(userFundNavPO, accountUserPO, date);
            }
            UserProfitInfoPO userProfitInfoPO = new UserProfitInfoPO();
            userProfitInfoPO.setId(Sequence.next());
            userProfitInfoPO.setAccountId(userFundNavPO.getAccountId());
            userProfitInfoPO.setClientId(userFundNavPO.getClientId());
            userProfitInfoPO.setGoalId(userFundNavPO.getGoalId());
            userProfitInfoPO.setProfitDate(DateUtils.dayStart(date));
            userProfitInfoPO.setCreateTime(DateUtils.now());
            userProfitInfoPO.setUpdateTime(DateUtils.now());
            userProfitInfoPO.setTotalProfit(wrapper.getTotalProfit());
            userProfitInfoPO.setPortfolioProfit(wrapper.getPortfolioProfit());
            userProfitInfoPO.setFxImpact(wrapper.getFxImpact());
            userProfitInfoService.saveOrUpdateUserProfit(userProfitInfoPO);
        }
    }

    /**
     * 遍历所有account 汇总T日所有recharge 汇总T日所有redeem 比较充值和提现，并执行相应策略
     */
    public void tradeAnalysis(Long accountId) {
        log.info("=====交易分析下Etf单开始======");
        //1、检查是否开市
        boolean saxoIsTrading = analysisSupportService.checkSaxoIsTranding();
        if (!saxoIsTrading) {
            log.info("===交易分析开始，check SAXO 是否开市为false,不做交易");
            return;
        }

        List<AccountInfoPO> accountInfoPOList = accountInfoService.listAccountInfo();
        for (AccountInfoPO accountInfoPO : accountInfoPOList) {
            if (accountId != null && !accountInfoPO.getId().equals(accountId)) {
                continue;
            }
            try {
                //获取昨日ubuy的逻辑必须在tpcf计算之前
                BigDecimal unbuy = getTotalUnbuy(accountInfoPO.getId());
                //处理提现申请为TNCF 并 获取TNCF
                AnalyTpcfTncfWrapperBean tncfWrapperBean = tpcfTncfService.handelTncfFromProcessing(accountInfoPO);
                //处理充值为TPCF 并 获取TPCF
                AnalyTpcfTncfWrapperBean tpcfWrapperBean = tpcfTncfService.handelTpcfFromProcessing(accountInfoPO);
                //调仓逻辑

                String accountInfoPOId = "" + accountInfoPO.getId();

                if (accountInfoPO.getInitDay().equals(InitDayEnum.UN_INIT_DAY)) {
                    if (!(accountInfoPOId.equalsIgnoreCase("1134343766341234690") || accountInfoPOId.equalsIgnoreCase("1189426754330210305")
                            || accountInfoPOId.equalsIgnoreCase("1201747956021243906") || accountInfoPOId.equalsIgnoreCase("1152100813212135426"))) {
                        if (reBalanceHandler(accountInfoPO, tncfWrapperBean, tpcfWrapperBean)) {
                            continue;
                        }
                    }
                }
                //非调仓逻辑
                if (accountInfoPOId.equalsIgnoreCase("1134343766341234690") || accountInfoPOId.equalsIgnoreCase("1189426754330210305")
                        || accountInfoPOId.equalsIgnoreCase("1201747956021243906") || accountInfoPOId.equalsIgnoreCase("1152100813212135426")) {
                    notRebalanceHandler(accountInfoPO, tncfWrapperBean, tpcfWrapperBean, unbuy);
                }
            } catch (Exception e) {
                log.error("accountid:{},账户交易分析，处理异常", accountInfoPO.getId(), e);
            }
        }
    }

    private void notRebalanceHandler(AccountInfoPO accountInfoPO, AnalyTpcfTncfWrapperBean tncfWrapperBean,
            AnalyTpcfTncfWrapperBean tpcfWrapperBean, BigDecimal unbuy) {
        //记录过程数据
        StaticFortradeAnalysisEvent staticFortradeAnalysisEvent = new StaticFortradeAnalysisEvent();
        staticFortradeAnalysisEvent.setAccountId(accountInfoPO.getId());
        eventBus.post(staticFortradeAnalysisEvent);
        //tpcf,tncf
        BigDecimal tpcf = tpcfWrapperBean.getTpcf();
        BigDecimal tncf = tncfWrapperBean.getTncf();
        log.info("交易分析，查询T-1的，Unbuy:{}", unbuy);
        BigDecimal totalUnbuy = unbuy.add(tpcf);

        List<AccountRedeemPO> accountRedeemPOs = tncfWrapperBean.getAccountRedeemPOs();
        //统计每个goal现金流
        UserGoalCashFlowEvent userGoalCashFlowEvent = new UserGoalCashFlowEvent();
        userGoalCashFlowEvent.setAccountId(accountInfoPO.getId());
        userGoalCashFlowEvent.setAccountRechargePOS(tpcfWrapperBean.getAccountRechargePOS());
        userGoalCashFlowEvent.setAccountRedeemPOs(tncfWrapperBean.getAccountRedeemPOs());
        eventBus.post(userGoalCashFlowEvent);

        log.info("账户:{},所有UnBuy:{},TNCF:{},TPCF:{},totalUnbuy:{}", accountInfoPO.getId(), unbuy, tncf, tpcf, totalUnbuy);
        if (totalUnbuy.compareTo(BigDecimal.ZERO) <= 0 && tncf.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("账户accountId:{},进行策略定制的时候totalUnbuy和提现单为0，不处理", accountInfoPO.getId());
            return;
        }
        //1.tpcf=0 && tncf=0
        if (tpcf.compareTo(BigDecimal.ZERO) <= 0 && tncf.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("账户accountId:{},进行策略定制的时候无充值和提现单不处理,cash调整在NAV计算", accountInfoPO.getId());
            return;
        }
        //2.tcpf>0 && tncf=0
        if (tpcf.compareTo(BigDecimal.ZERO) > 0 && tncf.compareTo(BigDecimal.ZERO) <= 0) {
            tradeAnalysisStrategy.onlyRechargeTradeAnalysis(totalUnbuy, BigDecimal.ZERO, accountInfoPO, Lists.newArrayList());
        }
        //3.tpcf=0 && |tncf|>0
        if (tpcf.compareTo(BigDecimal.ZERO) <= 0 && tncf.compareTo(BigDecimal.ZERO) > 0) {
            tradeAnalysisStrategy.onlyWithdrawaltradeAnalysis(totalUnbuy, tncf, accountInfoPO, accountRedeemPOs);
        }
        //4.tpcf > 0 && tncf < 0
        if (tpcf.compareTo(BigDecimal.ZERO) > 0 && tncf.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal diff = totalUnbuy.subtract(tncf).setScale(6, BigDecimal.ROUND_HALF_UP);
            if (diff.compareTo(BigDecimal.ZERO) > 0) {
                //充值的策略
                tradeAnalysisStrategy.onlyRechargeTradeAnalysis(totalUnbuy, tncf, accountInfoPO, accountRedeemPOs);
            } else if (diff.compareTo(BigDecimal.ZERO) <= 0) {
                //提现的策略
                tradeAnalysisStrategy.onlyWithdrawaltradeAnalysis(totalUnbuy, tncf, accountInfoPO, accountRedeemPOs);
            }
        }
    }

    private BigDecimal getTotalUnbuy(Long accountId) {
        AccountAssetPO accountAssetParam = new AccountAssetPO();
        accountAssetParam.setAccountId(accountId);
        accountAssetParam.setProductCode(Constants.UN_BUY_PRODUCT_CODE);
        List<AccountAssetPO> accountAssetPOs = accountAssetService.listAccountUnBuyAssets(accountAssetParam);
        return AccountAssetStatistic.getAccountUnbuy(accountAssetPOs);
    }

    /**
     * 调仓逻辑处理
     *
     * @param accountInfoPO
     * @return
     */
    private boolean reBalanceHandler(AccountInfoPO accountInfoPO, AnalyTpcfTncfWrapperBean tncfWrapperBean,
            AnalyTpcfTncfWrapperBean tpcfWrapperBean) {

        //ab测试
//        boolean abTestSwitch = PropertiesUtil.getBoolean("ab.test.account.switch", false);
//        if (abTestSwitch) {
//            String abTestAccountId = PropertiesUtil.getString("ab.test.account.id");
//            if (abTestAccountId != null && !abTestAccountId.equals(accountInfoPO.getId().toString())) {
//                return false;
//            }
//            log.info("触发白名单执行 ab.test.account.id:{}.", abTestAccountId);
//        }
        //如果有未处理完的调仓，不进行触发
        Long accountId = accountInfoPO.getId();
        AccountBalanceRecord accountBalanceRecord = new AccountBalanceRecord();
        accountBalanceRecord.setAccountId(accountId);
        accountBalanceRecord.setBalStatusList(ImmutableList.of(BalStatusEnum.HANDLING, BalStatusEnum.BUYING, BalStatusEnum.SELLING));
        List<AccountBalanceRecord> accountBalanceRecordList = accountBalanceRecordService.queryAccountBalance(accountBalanceRecord);
        if (!CollectionUtils.isEmpty(accountBalanceRecordList)) {
            return true;
        }
        //检查调仓，生成方案
        AccountBalanceHisRecord accountBalanceHisRecordQuery = new AccountBalanceHisRecord();
        accountBalanceHisRecordQuery.setAccountId(accountInfoPO.getId());
        AccountBalanceHisRecord accountBalanceHisRecord = accountBalanceHisRecordService.selectOne(accountBalanceHisRecordQuery);

        //根据模型标识获取当日目标模型
        ModelRecommendResDTO modelRecommendResDTO = modelServiceRemoteService.getValidRecommendByPortfolioId(accountInfoPO.getPortfolioId());
        PoolingEnum poolingEnum = modelRecommendResDTO.getPool();

        if (accountBalanceHisRecord == null) {
            log.info("插入调仓历史:accountId:{},模型:{}", accountInfoPO.getId(), JSON.toJSONString(modelRecommendResDTO));
            //若没有，插入一条his
            AccountBalanceHisRecord accountBalanceHisRecordUpdate = new AccountBalanceHisRecord();
            accountBalanceHisRecordUpdate.setAccountId(accountInfoPO.getId());
            accountBalanceHisRecordUpdate.setBalId(0L);
            accountBalanceHisRecordUpdate.setLastBalTime(DateUtils.now());
            accountBalanceHisRecordUpdate.setLastProductWeight(modelRecommendResDTO.getProductWeight());
            accountBalanceHisRecordUpdate.setPortfolioScore(modelRecommendResDTO.getScore());
            accountBalanceHisRecordService.updateOrInsert(accountBalanceHisRecordUpdate);
            accountBalanceHisRecord = accountBalanceHisRecordUpdate;
        }

        ReBalanceTriggerContext reBalanceTriggerContext = null;
        if (poolingEnum == PoolingEnum.P1) {
            reBalanceTriggerContext = new ReBalanceTriggerContext(new Pool1TriggerStrategy());
        }
        if (poolingEnum == PoolingEnum.P2) {
            reBalanceTriggerContext = new ReBalanceTriggerContext(new Pool2TriggerStrategy());
        }
        if (poolingEnum == PoolingEnum.P3) {
            reBalanceTriggerContext = new ReBalanceTriggerContext(new Pool3TriggerStrategy());
        }
        //没有对应处理策略
        if (reBalanceTriggerContext == null) {
            throw new BusinessException("没有对应的处理策略:" + JSON.toJSONString(modelRecommendResDTO));
        }
        reBalanceTriggerContext.setAccountBalanceHisRecord(accountBalanceHisRecord);
        reBalanceTriggerContext.setModelRecommendResDTO(modelRecommendResDTO);
        ReBalanceTriggerResult reBalanceTriggerResult = reBalanceTriggerContext.executeStrategy();
        log.info("账号{},调仓判断结果{}", accountId, JSON.toJSONString(reBalanceTriggerResult));

        if (true) {
            //调仓逻辑
            adjustPlanSellBuilder.setModelRecommendDTO(modelRecommendResDTO);
            adjustPlanSellBuilder.setAccountInfoPO(accountInfoPO);
            adjustPlanSellBuilder.setTriggerResult(reBalanceTriggerResult);
            List<AccountBalanceAdjDetail> accountBalanceAdjDetailList = adjustPlanSellBuilder.build(tncfWrapperBean, tpcfWrapperBean);
            accountBalanceExecute.setAccountBalanceAdjDetails(accountBalanceAdjDetailList);
            accountBalanceExecute.setAnalyTpcfTncfWrapperBean(tncfWrapperBean);
            accountBalanceExecute.executePlanDetailSell(BalTradeTypeEnum.SELL);

            return true;
        }
        return false;
    }

    public void calculaterUserProfit(Date date) {
        calculateUserProfit(date);
    }

    @RequestMapping(value = "/1-1")
    @ResponseBody
    public void accountEtf(@RequestParam(required = false) String date) {
        try {
            staticAccountEtfJob(DateUtils.parseDate(date));
//            calculateAssetFundNavByDate(date, null);
//            staticUserEtfJobByDate(DateUtils.parseDate(date));
//            calculateUserProfit(DateUtils.parseDate(date));
//            updateSgd(null, DateUtils.parseDate(date));
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
    }

    @RequestMapping(value = "/2")
    @ResponseBody
    public void nav() {
        try {
            calculateAssetFundNav();
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
    }

    @RequestMapping(value = "/2-1")
    @ResponseBody
    public void calculateAssetFundNavByDate(@RequestParam(required = false) String date) {
        try {
            calculateAssetFundNavByDate(date, null);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
    }

    @RequestMapping(value = "/3")
    @ResponseBody
    public void clientEtf() {
        try {
            staticUserEtfJob(null);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
    }

    @RequestMapping(value = "/3-1")
    @ResponseBody
    public void clientEtfByDate(@RequestParam(required = false) String date) {
        try {
            staticUserEtfJobByDate(DateUtils.parseDate(date));
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
    }

    @RequestMapping(value = "/4")
    @ResponseBody
    public void profit() {
        try {
            calculateUserProfit(DateUtils.now());
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
    }

    @RequestMapping(value = "/4-1")
    @ResponseBody
    public void profitByDate(@RequestParam(required = false) String date) {
        try {
            calculateUserProfit(DateUtils.parseDate(date));
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
    }

    @RequestMapping(value = "/5")
    @ResponseBody
    public void userStatic() {
        try {
            updateSgd(null, null);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
    }

    @RequestMapping(value = "/5-1")
    @ResponseBody
    public void userStaticByDateId(@RequestParam(required = false) String date) {
        try {
            updateSgd(null, DateUtils.parseDate(date));
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
    }

    @Autowired
    private Trade trade;

    @Autowired
    private Confirm confirm;

    @Autowired
    private Revise revise;

    @Autowired
    private Demerge demerge;

    @Autowired
    private Finish finish;

    @Autowired
    private EtfMergeOrderPftMapper etfMergeOrderPftMapper;

    @Autowired
    private PivotPftRemoteService pivotPftRemoteService;

    @RequestMapping(value = "/6-1")
    @ResponseBody
    public void tradeWithType(@RequestParam(required = false) String action) {
        log.info("Action {} ", action);
        try {
            if ("1".equals(action)) {
                log.info("Action 1");
                tradeAnalysis(null);
                log.info("Action 2");
                demerge.demergeOrderSellOrBuy();
                log.info("Action 3");
                mergeOrder.mergeEtfOrderForOrderType(true, false);
                log.info("Action 4");
                trade.sellOrBuy();
                log.info("Action 5");
                confirm.tradeConfirmSellOrBuy();
                log.info("Action 6");
                demerge.demergeOrderSellOrBuy();
                log.info("Action 7");
                recalculate.recalculate();
                log.info("Action 8");
                mergeOrder.mergeEtfOrderForOrderType(true, true);
                log.info("Action 9");
                //trade.buy(true);
                log.info("Action 10");
                confirm.tradeRebalancingConfirmBuy();
                log.info("Action 11");
                demerge.demergeOrder(EtfmergeOrderTypeEnum.BUY);

            }
            if ("2".equals(action)) {
            }
            if ("3".equals(action)) {
            }
            if ("4".equals(action)) {
            }
            if ("5".equals(action)) {
            }
            if ("6".equals(action)) {
            }

            if ("7".equals(action)) {
            }
            if ("8".equals(action)) {
            }
            if ("9".equals(action)) {
            }
            if ("10".equals(action)) {
            }
            if ("11".equals(action)) {
            }

            if ("12".equals(action)) {
                log.info("开始执行 =======>>> SyncPftJob");
                List<EtfMergePftOrderPO> iniList = etfMergeOrderPftMapper.listBySyncStatus(SyncStatus.INIT);                
                for (EtfMergePftOrderPO etfMergePftOrderPO : iniList) {

                    etfMergeOrderPftMapper.updateSyncStatus(SyncStatus.SEND, etfMergePftOrderPO.getMergeOrderId());
                    PivotPftAssetReqDTO pivotPftAssetReqDTO = new PivotPftAssetReqDTO();
                    pivotPftAssetReqDTO.setProductCode(etfMergePftOrderPO.getProductCode());
                    pivotPftAssetReqDTO.setConfirmMoney(etfMergePftOrderPO.getAmount());
                    pivotPftAssetReqDTO.setConfirmShare(etfMergePftOrderPO.getShare());
                    pivotPftAssetReqDTO.setCostFee(etfMergePftOrderPO.getCost());
                    pivotPftAssetReqDTO.setExecuteOrderNo(etfMergePftOrderPO.getId());
                    pivotPftAssetReqDTO.setPftAssetOperateType(etfMergePftOrderPO.getTradeType() == TradeType.BUY
                            ? PftAssetOperateTypeEnum.NEEDCASH
                            : PftAssetOperateTypeEnum.NEEDETFSHARES);
                    pivotPftAssetReqDTO.setPftAssetSource(etfMergePftOrderPO.getSourceType());

                    RpcMessage<PivotPftAssetResDTO> pivotPftAssetResDTORpcMessage = pivotPftRemoteService.updatePftAsset(pivotPftAssetReqDTO);

                    if (!pivotPftAssetResDTORpcMessage.isSuccess()) {
                        log.error("SyncPftJob error : {}.", pivotPftAssetResDTORpcMessage.getErrMsg());
                        etfMergeOrderPftMapper.updateSyncStatus(SyncStatus.FAIL, etfMergePftOrderPO.getMergeOrderId());
                        continue;
                    }
                    etfMergeOrderPftMapper.updateSyncStatus(SyncStatus.SUCCESS, etfMergePftOrderPO.getMergeOrderId());
                }
                log.info("执行结束 =======>>> SyncPftJob");
            }

            if ("13".equals(action)) {
                finish.finishNotify();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @RequestMapping(value = "/6")
//    @ResponseBody
//    public void trade6() {
//        try {
//            tradeAnalysis(null);
//
//        } catch (Exception e) {
//            ErrorLogAndMailUtil.logError(log, e);
//        }
//    }
    @RequestMapping(value = "/sync-port")
    @ResponseBody
    public void syncPort() {
        try {
            synchroPortLevel();
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
    }

    @RequestMapping(value = "/dividend")
    @ResponseBody
    public void dividend() {
        try {
            saxoStatisticService.shareDividEnd(null);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
    }

    @Autowired
    private AssetServiceRemoteService assetServiceRemoteService;

    @RequestMapping(value = "/accountStatus")
    @ResponseBody
    public void accountStatus() {
        try {
            RpcMessage<AccountTotalAssetDTO> rpcMessage = assetServiceRemoteService.queryAccountTotalInfo(DateUtils.now());
            if (!rpcMessage.isSuccess()) {
                throw new MessageException(rpcMessage.getErrMsg());
            }
            AccountTotalAssetDTO accountTotalAssetDTO = rpcMessage.getContent();
            log.info("accountTotalAssetDTO {} , ", accountTotalAssetDTO);
            accountTotalAssetDTO.getTotalCash();
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
    }

    @RequestMapping(value = "/etf-recon")
    @ResponseBody
    public void etf_recon() {
        log.info("开始执行 =======>>> SaxoCashTransactionsJob");

        try {
            saxoStatisticService.recordCashTransactions();
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
        log.info("执行结束 =======>>> SaxoCashTransactionsJob");
    }

    @Autowired
    private Recalculate recalculate;

    @RequestMapping(value = "/rebalance-buy")
    @ResponseBody
    public void rebalancingBuy() {
        recalculate.recalculate();
    }

    @RequestMapping(value = "/sftp")
    @ResponseBody
    public void sftpClient() {
        log.info("Starting sftp...");
        try {
            SftpClient sftpClient = SftpClient.connect("3.0.163.17", 22, "ftpuser", "OmMsi93DBcNo", 5000, 10);
            List<DailyClosingPricePO> result = Lists.newArrayList();

            InputStream stream = null;
            List<String> lines = new ArrayList();
            try {
                stream = sftpClient.get("/home/ftpuser/pivot/marketData/dailyClose.csv");
                String thisLine = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                while ((thisLine = br.readLine()) != null) {
                    lines.add(thisLine);
                }

            } finally {
                if (stream != null) {
                    stream.close();
                }
            }

            if (CollectionUtils.isNotEmpty(lines)) {
                log.info("ftp查询成功，result.size() --> " + result.size() + ", 开始入库处理");

            } else {
            }
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
    }

    @RequestMapping(value = "/sftpuob")
    @ResponseBody
    public void sftpClientUOB() {
        log.info("Starting sftpUOB...");
        try {
            SftpClient sftpClient = SftpClient.connectUOB();
            sftpClient.getUOBFile("test", "test2");

            SftpClient sftpClient2 = SftpClient.connectUOB();
            sftpClient2.putUOBFile("test", "test");
            /* List<DailyClosingPricePO> result = Lists.newArrayList();

            InputStream stream = null;
            List<String> lines = new ArrayList();
            try {
                stream = sftpClient.get("/SG/OUT/VA5_3523095739_110220.pgp");

                log.info("stream >>" + stream.toString());
                String thisLine = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                while ((thisLine = br.readLine()) != null) {
                    lines.add(thisLine);
                    log.info("thisLine >>" + thisLine);
                }

            } finally {
                if (stream != null) {
                    stream.close();
                }
            }

            if (CollectionUtils.isNotEmpty(lines)) {
                log.info("ftp查询成功，result.size() --> " + result.size() + ", 开始入库处理");

            } else {
            }*/

        } catch (Exception e) {
            // ErrorLogAndMailUtil.logError(log, e);
        }
    }

    @RequestMapping(value = "/saxo-account-usd")
    @ResponseBody
    public Message<AccountFundingRespV2> saxoBalance(@RequestParam(required = false) String type, @RequestParam(required = false) String dayBefore) {
        AccountFundingRespV2 result = new AccountFundingRespV2();
        log.info("开始 saxoBalance type: {} , dayBefore {} ", type, dayBefore);
        try {
            result = SaxoClient.queryAccountAllEvent(type, dayBefore);
            log.info("result {} ", result);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
        log.info("结束 saxoBalance");
        return Message.success(result);
    }

    @RequestMapping(value = "/price")
    @ResponseBody
    public void price() {
        try {
            String csvFile = "D:/price.csv";
            BufferedReader br = null;
            String line = "";
            String cvsSplitBy = ",";
            List<DailyClosingPricePO> result = Lists.newArrayList();

            InputStream stream = null;
            List<String> lines = new ArrayList();
            try {
                br = new BufferedReader(new FileReader(csvFile));
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }

                // remove header;
                lines.remove(0);
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }

            if (CollectionUtils.isNotEmpty(lines)) {
                Map<String, Integer> productMap = CronController.getProductMap();
                SimpleDateFormat _sdf = new SimpleDateFormat("yyyyMMdd");
                for (int i = 0; i < lines.size(); i++) {
                    String _line = lines.get(i);
                    String strBsnDt = _line.split("\\,")[0];
                    List<String> lineItem = Lists.newArrayList(_line.split(","));

                    for (String productCode : productMap.keySet()) {
                        DailyClosingPricePO dailyClosingPricePO = new DailyClosingPricePO();
                        dailyClosingPricePO.setEtfCode(productCode);
                        dailyClosingPricePO.setBsnDt(_sdf.parse(strBsnDt));
                        dailyClosingPricePO.setPrice(new BigDecimal(lineItem.get(productMap.get(productCode))));
                        dailyClosingPriceMapper.save(dailyClosingPricePO);
                    }

                }
            }
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        } finally {

        }
    }

    private void createDailyClosingPricePO(String etf, int idx, List<DailyClosingPricePO> result, List<String> lineItem, Date bsnDt) {
        DailyClosingPricePO dailyClosingPricePO = new DailyClosingPricePO();
        dailyClosingPricePO.setEtfCode(etf);
        dailyClosingPricePO.setBsnDt(bsnDt);
        dailyClosingPricePO.setPrice(new BigDecimal(lineItem.get(idx)));
        result.add(dailyClosingPricePO);
    }

    public static Map<String, Integer> getProductMap() {
        Map<String, Integer> productMap = Maps.newHashMap();
        productMap.put("VT", 1);
        productMap.put("EEM", 2);
        productMap.put("BNDX", 3);
        productMap.put("SHV", 4);
        productMap.put("EMB", 5);
        productMap.put("VWOB", 6);
        productMap.put("BWX", 7);
        productMap.put("HYG", 8);
        productMap.put("JNK", 9);
        productMap.put("MUB", 10);
        productMap.put("LQD", 11);
        productMap.put("VCIT", 12);
        productMap.put("FLOT", 13);
        productMap.put("IEF", 14);
        productMap.put("UUP", 15);
        productMap.put("PDBC", 16);
        productMap.put("GLD", 17);
        productMap.put("VNQ", 18);
        productMap.put("VEA", 19);
        productMap.put("VPL", 20);
        productMap.put("EWA", 21);
        productMap.put("SPY", 22);
        productMap.put("VOO", 23);
        productMap.put("VTI", 24);
        productMap.put("VGK", 25);
        productMap.put("EWJ", 26);
        productMap.put("QQQ", 27);
        productMap.put("EWS", 28);
        productMap.put("EWZ", 29);
        productMap.put("ASHR", 30);
        productMap.put("VWO", 31);
        productMap.put("ILF", 32);
        productMap.put("RSX", 33);
        productMap.put("AAXJ", 34);
        return productMap;
    }

    @RequestMapping(value = "/ModelRecommendJob_2")
    @ResponseBody
    public void ModelRecommendJob_2() {
        try {
        	log.info("========start of ModelRecommendJob_2======");
        	modelRecommendService.saveNewPortfolioRecommend();
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
        log.info("========end of ModelRecommendJob_2======");
    }
    
    @RequestMapping(value = "/SaveClosingPriceJob_2")
    @ResponseBody
    public void SaveClosingPriceJob_2(@RequestParam(required = false) String bsnDtStr) {
        try {
        	log.info("========start of SaveClosingPriceJob_2======");

                if(StringUtil.isEmpty(bsnDtStr)) {
        		tradingSupportService.saveAhamClosingPrice(null);
        	}else {
        		tradingSupportService.saveAhamClosingPrice(bsnDtStr);
        	}
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
        log.info("========end of SaveClosingPriceJob_2======");
    }
    
    @RequestMapping("/tradeAnalysisJob.api")
    @ResponseBody
    public void tradeAnalysisJob(@RequestParam(required = false) String accountId) {
        try {
        	log.info("========start of TradeAnalysisJob_2======");
        	rechargeServiceRemoteService.tradeAnalysisJob(accountId);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
        log.info("========end of TradeAnalysisJob_2======");
    }
    
    @RequestMapping(value = "/Trade20_SellOrBuyJob_1")
    @ResponseBody
    public void Trade20_SellOrBuyJob_1() {
        trade.sellOrBuy();
    }
    
    @RequestMapping(value = "/Trade90_BuyJob_2")
    @ResponseBody
    public void Trade90_BuyJob_2() {
        Long orderId = Sequence.next();
        trade.buyTrade90(orderId);
    }
    
    @RequestMapping(value = "/Trade10_MergeOrderTypeJob_1")
    @ResponseBody
    public void Trade10_MergeOrderTypeJob_1() {
        mergeOrder.mergeEtfOrderForOrderType(true, false);
    }
    
    @RequestMapping(value = "/Trade80_MergeRbaOrderJob_2")
    @ResponseBody
    public void Trade80_MergeRbaOrderJob_2() {
        mergeOrder.mergeEtfOrderForOrderType(true, true);
    }
    
    @RequestMapping(value = "/Trade130_FinishNotifyJob_1")
    @ResponseBody
    public void Trade130_FinishNotifyJob_1(){
        finish.finishNotify();
        
    }
    
    @RequestMapping(value = "/AssetFundNavJob_2")
    @ResponseBody
    public void AssetFundNavJob_2(@RequestParam(required = false) String date, @RequestParam(required = false) String accountId){
        if(accountId!=null){
            assetFundNavJobImpl.calculateAssetFundNav(DateUtils.parseDate(date), Long.parseLong(accountId));
        }else{
            assetFundNavJobImpl.calculateAssetFundNav(DateUtils.parseDate(date), null);
        }
        
    }               
    
    @RequestMapping(value = "/Trade50_DemergeSellOrBuyOrderJob_1")
    @ResponseBody
    public void Trade50_DemergeSellOrBuyOrderJob_1(){
        demerge.demergeOrderSellOrBuy();
        
    }  
            
    @RequestMapping(value = "/StaticAccountEtfJob_2")
    @ResponseBody
    public void StaticAccountEtfJob_2(@RequestParam(required = false) String date){ //10am
        staticAccountEtfJobImpl.staticAccountEtfJob(DateUtils.parseDate(date));
        
    } 
    
    @RequestMapping(value = "/StaticUEtfJob_2") //4.30
    @ResponseBody
    public void StaticUEtfJob_2(){
        staticUEtfJobImpl.staticUserEtfJob(null);
        
    } 
    
    @RequestMapping(value = "/AccountStaticSgdJob_2")//4pm
    @ResponseBody
    public void AccountStaticSgdJob_2(){
        accountStaticSgdJobImpl.updateSgd(null, null);
        
    } 
            
    @RequestMapping(value = "/UserProfitJob_2")//4pm
    @ResponseBody
    public void UserProfitJob_2(){
        userProfitJobImpl.calculaterUserProfit(DateUtils.now());
        
    }
    
    @RequestMapping(value = "/restClient")
    @ResponseBody
    public void restClient(@RequestParam(required = false) String prdId, @RequestParam(required = false) String transType,
            @RequestParam(required = false) String orderId, @RequestParam(required = false) Integer unit){
        Date now = DateUtils.now();
        ahamRestClient.placeAhamNewOrder(prdId,transType, unit, orderId, now);
        
    }
    
    @RequestMapping(value = "/WithdrawalNotifyToAham_2")
    @ResponseBody
    public void WithdrawalNotifyToAham(){
        withdrawalNotifyToAham.withdrawalSaxoToUob();
    }
    
    @RequestMapping(value = "/Trade140_SyncPftJob_2")
    @ResponseBody
    public void Trade140_SyncPftJob_2(){
        ShardingContext shardingContext =null;
        trade140_SyncPftJob.execute(shardingContext);
    }
    
    @RequestMapping(value = "/CustomerStatementJob_2")
    @ResponseBody
    public void CustomerStatementJob_2(String clientId, Integer month){
        
        if(clientId.equalsIgnoreCase("") || clientId == null){
            clientId = null;
        }
        
        if(month == 0){
            month = null;
        }
        
        customerStatementJobImpl.calculateCustomerStatement(clientId, month);
    }
}
