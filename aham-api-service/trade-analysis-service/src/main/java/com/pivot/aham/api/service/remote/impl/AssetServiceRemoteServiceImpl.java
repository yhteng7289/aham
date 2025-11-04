package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;
import com.pivot.aham.api.server.dto.AccountAssetReqDTO;
import com.pivot.aham.api.server.dto.AccountAssetResDTO;
import com.pivot.aham.api.server.dto.AccountTotalAssetDTO;
import com.pivot.aham.api.server.dto.BankVirtualAccountDTO;
import com.pivot.aham.api.server.dto.BankVirtualAccountResDTO;
import com.pivot.aham.api.server.dto.EtfCallbackDTO;
import com.pivot.aham.api.server.dto.ProductInfoResDTO;
import com.pivot.aham.api.server.dto.ProductStatisDTO;
import com.pivot.aham.api.server.dto.UserAssetDTO;
import com.pivot.aham.api.server.dto.UserAssetWapperDTO;
import com.pivot.aham.api.server.dto.UserAssetsDetailWrapperDTO;
import com.pivot.aham.api.server.dto.UserInfoResDTO;
import com.pivot.aham.api.server.dto.req.AccountEtfAssetReqDTO;
import com.pivot.aham.api.server.dto.req.BalanceApplyReqDTO;
import com.pivot.aham.api.server.dto.res.AccountEtfAssetResDTO;
import com.pivot.aham.api.server.dto.res.BalanceApplyResDTO;
import com.pivot.aham.api.server.dto.res.TmpOrderRecordResDTO;
import com.pivot.aham.api.server.dto.res.UserProfitInfoResDTO;
import com.pivot.aham.api.server.remoteservice.AssetServiceRemoteService;
import com.pivot.aham.api.server.remoteservice.BalanceApplyRemoteService;
import com.pivot.aham.api.server.remoteservice.DividendRemoteService;
import com.pivot.aham.api.server.remoteservice.ModelServiceRemoteService;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.service.job.interevent.AccountBalanceAdjDetailEvent;
import com.pivot.aham.api.service.job.interevent.StaticForEtfCallBackEvent;
import com.pivot.aham.api.service.job.AccountFundNavJob;
import com.pivot.aham.api.service.mapper.model.AccountAssetPO;
import com.pivot.aham.api.service.mapper.model.AccountEtfSharesStaticPO;
import com.pivot.aham.api.service.mapper.model.AccountInfoPO;
import com.pivot.aham.api.service.mapper.model.AccountRechargePO;
import com.pivot.aham.api.service.mapper.model.AccountRedeemPO;
import com.pivot.aham.api.service.mapper.model.AccountUserPO;
import com.pivot.aham.api.service.mapper.model.AhamReconPO;
import com.pivot.aham.api.service.mapper.model.ExchangeRatePO;
import com.pivot.aham.api.service.mapper.model.PivotPftAccountPO;
import com.pivot.aham.api.service.mapper.model.PivotPftAssetPO;
import com.pivot.aham.api.service.mapper.model.RedeemApplyPO;
import com.pivot.aham.api.service.mapper.model.SaxoAccountOrderPO;
import com.pivot.aham.api.service.mapper.model.TmpOrderRecordPO;
import com.pivot.aham.api.service.mapper.model.UserFundNavPO;
import com.pivot.aham.api.service.mapper.model.UserProfitInfoPO;
import com.pivot.aham.api.service.mapper.model.VirtualBankAccountPO;
import com.pivot.aham.api.service.remote.impl.wrapperbean.SquirrelsaveCashBean;
import com.pivot.aham.api.service.service.AccountAssetService;
import com.pivot.aham.api.service.service.AccountEtfSharesStaticService;
import com.pivot.aham.api.service.service.AccountInfoService;
import com.pivot.aham.api.service.service.AccountRechargeService;
import com.pivot.aham.api.service.service.AccountRedeemService;
import com.pivot.aham.api.service.service.AccountUserService;
import com.pivot.aham.api.service.service.AhamReconService;
import com.pivot.aham.api.service.service.AnalysisSupportService;
import com.pivot.aham.api.service.service.AssetFundNavService;
import com.pivot.aham.api.service.service.VirtualBankAccountService;
import com.pivot.aham.api.service.service.ExchangeRateService;
import com.pivot.aham.api.service.service.PivotPftAccountService;
import com.pivot.aham.api.service.service.PivotPftAssetService;
import com.pivot.aham.api.service.service.RedeemApplyService;
import com.pivot.aham.api.service.service.SaxoAccountOrderService;
import com.pivot.aham.api.service.service.TmpOrderRecordService;
import com.pivot.aham.api.service.service.UserFundNavService;
import com.pivot.aham.api.service.service.UserProfitInfoService;
import com.pivot.aham.api.service.support.AccountAssetStatistic;
import com.pivot.aham.api.service.support.AccountAssetStatisticBean;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.support.context.Resources;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.enums.AccountTypeEnum;
import com.pivot.aham.common.enums.AhamReconResultEnum;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.ExchangeRateTypeEnum;
import com.pivot.aham.common.enums.ProductAssetStatusEnum;
import com.pivot.aham.common.enums.ProductMainSubTypeEnum;
import com.pivot.aham.common.enums.TmpOrderActionTypeEnum;
import com.pivot.aham.common.enums.TransferStatusEnum;
import com.pivot.aham.common.enums.analysis.AssetSourceEnum;
import com.pivot.aham.common.enums.analysis.EtfExecutedStatusEnum;
import com.pivot.aham.common.enums.analysis.RechargeOrderStatusEnum;
import com.pivot.aham.common.enums.analysis.RedeemApplyStatusEnum;
import com.pivot.aham.common.enums.analysis.RedeemOrderStatusEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderActionTypeEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderTradeStatusEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderTradeTypeEnum;
import com.pivot.aham.common.enums.analysis.SaxoToUobTransferStatusEnum;
import com.pivot.aham.common.enums.analysis.TmpOrderExecuteStatusEnum;
import com.pivot.aham.common.enums.recharge.TncfStatusEnum;
import com.pivot.aham.common.enums.recharge.TpcfStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by luyang.li on 18/12/12.
 */
@Service(interfaceClass = AssetServiceRemoteService.class)
@Slf4j
public class AssetServiceRemoteServiceImpl implements AssetServiceRemoteService {

    @Resource
    private AccountAssetService accountAssetService;
    @Resource
    private TmpOrderRecordService tmpOrderRecordService;
    @Resource
    private AccountInfoService accountInfoService;
    @Resource
    private AccountUserService accountUserService;
    @Resource
    private AccountRechargeService accountRechargeService;
    @Autowired
    private AccountRedeemService accountRedeemService;
    @Autowired
    private RedeemApplyService bankVARedeemService;
    @Resource
    private ModelServiceRemoteService modelServiceRemoteService;
    @Resource
    private AssetFundNavService assetFundNavService;
    @Resource
    private UserServiceRemoteService userServiceRemoteService;
    @Resource
    private UserFundNavService userFundNavService;
    @Resource
    private AnalysisSupportService analysisSupportService;
    @Resource
    private EventBus eventBus;
    @Resource
    private UserProfitInfoService userProfitInfoService;
    @Resource
    private ExchangeRateService exchangeRateService;
    @Resource
    private AccountEtfSharesStaticService accountEtfSharesStaticService;
    @Resource
    private BalanceApplyRemoteService balanceApplyRemoteService;
    @Resource
    private VirtualBankAccountService virtualBankAccountService;

    @Resource
    private DividendRemoteService dividendRemoteService;
    @Resource
    private SaxoAccountOrderService saxoAccountOrderService;
    @Resource
    private PivotPftAccountService pivotPftAccountService;
    @Resource
    private PivotPftAssetService pivotPftAssetService;
    @Resource
    private AhamReconService ahamReconService;

    /**
     * 1.处理Etf 订单状态 2.处理成功的Etf 分配到该账户的每个人上 3.处理UnBuy 卖出中的资产流水 4.单独处理现金(原始申购的现金 +
     * 失败的Etf) 分配到该账户的每个人上
     *
     * @param params
     * @return
     */
    @Override
    public RpcMessage etfCallBack(List<EtfCallbackDTO> params) {
        List<EtfCallbackDTO> buyEtfCallbackDTOList = Lists.newArrayList();
        List<EtfCallbackDTO> sellEtfCallbackDTOList = Lists.newArrayList();

        for (EtfCallbackDTO etfCallbackDTO : params) {
            TmpOrderRecordPO tmpOrderRecordQuery = new TmpOrderRecordPO();
            tmpOrderRecordQuery.setTmpOrderId(etfCallbackDTO.getTmpOrderId());
            tmpOrderRecordQuery.setTmpOrderTradeStatus(TmpOrderExecuteStatusEnum.HANDLING);
            TmpOrderRecordPO tmpOrderRecordPO = tmpOrderRecordService.selectOne(tmpOrderRecordQuery);
            if (tmpOrderRecordPO == null) {
                log.error("未找到对应临时订单:{}", JSON.toJSON(etfCallbackDTO));
                continue;
            }

            switch (tmpOrderRecordPO.getTmpOrderTradeType()) {
                case GBA:
                    buyEtfCallbackDTOList.add(etfCallbackDTO);
                    break;
                case GSA:
                case GSP:
                    sellEtfCallbackDTOList.add(etfCallbackDTO);
                    break;
                case RBA:
                case RSA:
                case RSP:
                    if (tmpOrderRecordPO.getActionType() == TmpOrderActionTypeEnum.BUY) {
                        buyEtfCallbackDTOList.add(etfCallbackDTO);
                    } else if (tmpOrderRecordPO.getActionType() == TmpOrderActionTypeEnum.SELL) {
                        sellEtfCallbackDTOList.add(etfCallbackDTO);
                    }
                    break;
                default:
                    break;
            }
        }

        handlerBuyConfirm(buyEtfCallbackDTOList);
        handlerSellConfirm(sellEtfCallbackDTOList);

        //处理
        AccountBalanceAdjDetailEvent accountBalanceAdjDetailEvent = new AccountBalanceAdjDetailEvent();
        accountBalanceAdjDetailEvent.setEtfCallbackDTOList(params);
        eventBus.post(accountBalanceAdjDetailEvent);

        return RpcMessage.success();
    }

    /**
     * 处理卖单
     *
     * @param params
     */
    private void handlerSellConfirm(List<EtfCallbackDTO> params) {
        log.info("账户赎回etf回调请求参数:{}", JSON.toJSON(params));
        if (CollectionUtils.isEmpty(params)) {
            return;
        }

        //获取成功的etf
        List<EtfCallbackDTO> etfSuccessCallbacks = FluentIterable.from(params).filter(new Predicate<EtfCallbackDTO>() {
            @Override
            public boolean apply(@Nullable EtfCallbackDTO input) {
                return input.getTransferStatus() == TransferStatusEnum.SUCCESS;
            }
        }).toList();

        if (CollectionUtils.isEmpty(etfSuccessCallbacks)) {
            return;
        }

        List<ProductInfoResDTO> allProductInfo = modelServiceRemoteService.queryAllProductInfo();
        if (CollectionUtils.isEmpty(allProductInfo)) {
            throw new BusinessException("全量产品信息为空");
        }

        List<ProductInfoResDTO> subList = FluentIterable.from(allProductInfo).filter(new Predicate<ProductInfoResDTO>() {
            @Override
            public boolean apply(@Nullable ProductInfoResDTO o) {
                return o.getProductType() == ProductMainSubTypeEnum.SUB;
            }
        }).toList();
        log.info("subEtf产品列表明细:{}", JSON.toJSON(subList));

        List<ProductInfoResDTO> mainList = FluentIterable.from(allProductInfo).filter(new Predicate<ProductInfoResDTO>() {
            @Override
            public boolean apply(@Nullable ProductInfoResDTO o) {
                return o.getProductType() == ProductMainSubTypeEnum.MAIN;
            }
        }).toList();
        log.info("mainEtf产品列表明细:{}", JSON.toJSON(mainList));

        Map<String, ProductInfoResDTO> subETFMap = Maps.newHashMap();
        for (ProductInfoResDTO productInfoResDTO : subList) {
            subETFMap.put(productInfoResDTO.getProductCode(), productInfoResDTO);
        }
        Map<String, ProductInfoResDTO> mainETFMap = Maps.newHashMap();
        for (ProductInfoResDTO productInfoResDTO : mainList) {
            mainETFMap.put(productInfoResDTO.getProductCode(), productInfoResDTO);
        }

        //处理成功的etf进行资产分配
        BigDecimal totalTransCost = BigDecimal.ZERO;
        BigDecimal totalSellCash = BigDecimal.ZERO;
        Long accountId = etfSuccessCallbacks.get(0).getAccountId();
        List<TmpOrderRecordPO> tmpOrderRecordPOList = Lists.newArrayList();
//        Long totalId = Sequence.next();
        for (EtfCallbackDTO buyEtfCallback : etfSuccessCallbacks) {
            totalTransCost = totalTransCost.add(buyEtfCallback.getTransCost());
            totalSellCash = totalSellCash.add(buyEtfCallback.getConfirmMoney());
            //某个etf处理成功
            TmpOrderRecordPO orderRecordPO = tmpOrderRecordService.queryByTmpOrderId(buyEtfCallback.getTmpOrderId());
            if (orderRecordPO == null) {
                log.info("未找到临时订单:{}", JSON.toJSON(buyEtfCallback));
                continue;
            }
            if (orderRecordPO.getTmpOrderTradeStatus() != TmpOrderExecuteStatusEnum.HANDLING) {
                log.info("该订单已经处理过了:{}", JSON.toJSON(buyEtfCallback));
                continue;
            }
            tmpOrderRecordPOList.add(orderRecordPO);
            //更新tmporder状态
            orderRecordPO.setConfirmTime(buyEtfCallback.getConfirmTime());
            orderRecordPO.setConfirmMoney(buyEtfCallback.getConfirmMoney());
            orderRecordPO.setConfirmTradeShares(buyEtfCallback.getConfirmShare());
            orderRecordPO.setTmpOrderId(buyEtfCallback.getTmpOrderId());
            orderRecordPO.setTmpOrderTradeStatus(TmpOrderExecuteStatusEnum.SUCCESS);
            orderRecordPO.setTransCost(buyEtfCallback.getTransCost());

            //新增etf份额资产流水
            AccountAssetPO assetPO = new AccountAssetPO();
            assetPO.setAssetSource(AssetSourceEnum.ETFSELL);
            assetPO.setProductCode(buyEtfCallback.getProductCode());
            assetPO.setAccountId(buyEtfCallback.getAccountId());
            assetPO.setConfirmTime(DateUtils.now());
            BigDecimal clientConfirmShares = buyEtfCallback.getConfirmShare();
            assetPO.setConfirmShare(clientConfirmShares);
            BigDecimal clientConfirmMoney = buyEtfCallback.getConfirmMoney();
            assetPO.setApplyMoney(clientConfirmMoney);
            assetPO.setConfirmMoney(clientConfirmMoney);
            assetPO.setApplyTime(DateUtils.now());
            assetPO.setProductAssetStatus(ProductAssetStatusEnum.CONFIRM_SELL);
            assetPO.setTotalTmpOrderId(orderRecordPO.getTotalTmpOrderId());
            assetPO.setTmpOrderId(orderRecordPO.getTmpOrderId());

            //新增cash资产流水
            AccountAssetPO cashAsset = new AccountAssetPO();
            cashAsset.setAssetSource(AssetSourceEnum.ETFSELL);
            cashAsset.setAccountId(buyEtfCallback.getAccountId());
            cashAsset.setConfirmTime(DateUtils.now());
            cashAsset.setConfirmShare(BigDecimal.ZERO);
            cashAsset.setApplyMoney(clientConfirmMoney);
            cashAsset.setConfirmMoney(clientConfirmMoney);
            cashAsset.setApplyTime(DateUtils.now());
            cashAsset.setProductAssetStatus(ProductAssetStatusEnum.HOLD_ING);
            cashAsset.setTotalTmpOrderId(orderRecordPO.getTotalTmpOrderId());
            cashAsset.setProductCode(Constants.CASH);
            cashAsset.setTmpOrderId(orderRecordPO.getTmpOrderId());

            boolean isValidEtf = !assetPO.getProductCode().toLowerCase().equalsIgnoreCase("cash") && clientConfirmShares.compareTo(BigDecimal.ZERO) > 0;
            // Etf + shares > 0
            if (isValidEtf) {
                accountAssetService.updateOrInsert(assetPO);
            } else {
                // Cover if cash + $ > 0
                boolean isValidCash = assetPO.getProductCode().toLowerCase().equalsIgnoreCase("cash") && clientConfirmMoney.compareTo(BigDecimal.ZERO) > 0;
                if (isValidCash) {
                    accountAssetService.updateOrInsert(assetPO);
                }
            }

            boolean isValidCash = cashAsset.getProductCode().toLowerCase().equalsIgnoreCase("cash") && clientConfirmMoney.compareTo(BigDecimal.ZERO) > 0;
            // Cash + $ > 0
            if (isValidCash) {
                accountAssetService.updateOrInsert(cashAsset);
            }

            tmpOrderRecordService.updateOrInsert(orderRecordPO);
        }
        //更新每个账号和用户的提现申请
        Multimap<Long, TmpOrderRecordPO> tmpOrderRecordMultimap = ArrayListMultimap.create();
        for (TmpOrderRecordPO tmpOrderRecordPO : tmpOrderRecordPOList) {
            tmpOrderRecordMultimap.put(tmpOrderRecordPO.getTotalTmpOrderId(), tmpOrderRecordPO);
        }
        Set<Map.Entry<Long, Collection<TmpOrderRecordPO>>> tmpOrderRecordEntrys = tmpOrderRecordMultimap.asMap().entrySet();
        //按totalId进行汇总，如果全部成功就为成功，如果全部失败就为失败
        for (Map.Entry<Long, Collection<TmpOrderRecordPO>> tmpOrderRecordEntry : tmpOrderRecordEntrys) {
            updateRedeem(tmpOrderRecordEntry, RedeemOrderStatusEnum.SUCCESS, EtfExecutedStatusEnum.SUCCESS, SaxoToUobTransferStatusEnum.WAITAPPLY);
        }
        //记录过程数据
        StaticForEtfCallBackEvent staticForEtfCallBackEvent = new StaticForEtfCallBackEvent();
        staticForEtfCallBackEvent.setAccountId(accountId);
        staticForEtfCallBackEvent.setTransactionCostSell(totalTransCost);
        staticForEtfCallBackEvent.setCashBySell(totalSellCash);
        eventBus.post(staticForEtfCallBackEvent);

    }

    /**
     * 更新account提现
     *
     * @param entry
     * @param redeemOrderStatusEnum
     */
    private void updateRedeem(Map.Entry<Long, Collection<TmpOrderRecordPO>> entry,
            RedeemOrderStatusEnum redeemOrderStatusEnum,
            EtfExecutedStatusEnum etfExecutedStatusEnum,
            SaxoToUobTransferStatusEnum saxoToUobTransferStatusEnum) {
        AccountRedeemPO accountRedeem = new AccountRedeemPO();
        accountRedeem.setTotalTmpOrderId(entry.getKey());
        List<AccountRedeemPO> accountRedeemPOList = accountRedeemService.queryList(accountRedeem);

        for (AccountRedeemPO accountRedeemPO : accountRedeemPOList) {
            accountRedeemPO.setTotalTmpOrderId(entry.getKey());
            accountRedeemPO.setOrderStatus(redeemOrderStatusEnum);
            accountRedeemService.updateOrInsert(accountRedeemPO);

            RedeemApplyPO redeemApply = bankVARedeemService.queryById(accountRedeemPO.getRedeemApplyId());
            redeemApply.setTotalTmpOrderId(entry.getKey());
            redeemApply.setEtfExecutedStatus(etfExecutedStatusEnum);
            redeemApply.setSaxoToUobTransferStatus(saxoToUobTransferStatusEnum);
            bankVARedeemService.updateOrInsert(redeemApply);
        }
    }

    /**
     * 处理买单 失败的ETF全部买对应的cash(main cash 和 sub cash)
     *
     * @param params
     */
    private void handlerBuyConfirm(List<EtfCallbackDTO> params) {
        log.info("账户申购etf回调请求参数:{}", JSON.toJSON(params));
        if (CollectionUtils.isEmpty(params)) {
            return;
        }
        //处理每个account下的Etf单
        Map<Long, List<EtfCallbackDTO>> etfCallbackDTOMap = getEtfCallbackMap(params);
        for (Long accountId : etfCallbackDTOMap.keySet()) {
            //获取TNCF
            AccountRedeemPO queryParam = new AccountRedeemPO();
            queryParam.setAccountId(accountId);
            queryParam.setOrderStatus(RedeemOrderStatusEnum.PROCESSING);
            queryParam.setTncfStatus(TncfStatusEnum.TNCF);
            List<AccountRedeemPO> accountRedeemPOs = accountRedeemService.getRedeemListByTime(queryParam);
            log.info("accountId:{},当日tncf:{}", accountId, JSON.toJSONString(accountRedeemPOs));

           List<EtfCallbackDTO> etfCallbackDTOs = etfCallbackDTOMap.get(accountId);
           handelAccountBuyEtf(etfCallbackDTOs, accountRedeemPOs);
        }

    }

    private void handelAccountBuyEtf(List<EtfCallbackDTO> etfCallbackDTOs,
            List<AccountRedeemPO> accountRedeemPOs) {
        //卖出资产确认(unbuy)
        List<AccountAssetPO> sellAccountAssets = Lists.newArrayList();
        //买入资产确认(Etf + cash)
        List<AccountAssetPO> etfBuyConfirms = Lists.newArrayList();
        //etf订单确认
        List<TmpOrderRecordPO> finishEtfOrders = Lists.newArrayList();
        //该次申购操作的总订单号
        Long totalTmpOrder = 0L;
        Map<Long,BigDecimal> mpTotalTemp = Maps.newHashMap();
        BigDecimal totalTransCost = BigDecimal.ZERO;
        Long accountId = etfCallbackDTOs.get(0).getAccountId();
        BigDecimal remainMoney = BigDecimal.ZERO;
        for (EtfCallbackDTO etfCallbackDTO : etfCallbackDTOs) {
            totalTransCost = totalTransCost.add(etfCallbackDTO.getTransCost());
            //Etf订单
            TmpOrderRecordPO orderRecordPO = new TmpOrderRecordPO();
            orderRecordPO.setTmpOrderId(etfCallbackDTO.getTmpOrderId());
            orderRecordPO = tmpOrderRecordService.selectOne(orderRecordPO);
            totalTmpOrder = orderRecordPO.getTotalTmpOrderId();
            if(!mpTotalTemp.containsKey(totalTmpOrder)){
                mpTotalTemp.put(totalTmpOrder, BigDecimal.ZERO);
            }
            orderRecordPO.setUpdateTime(DateUtils.now());
            orderRecordPO.setConfirmTime(etfCallbackDTO.getConfirmTime());
            orderRecordPO.setTransCost(etfCallbackDTO.getTransCost());

            //etf资产
            AccountAssetPO etfInAsset = new AccountAssetPO();
            etfInAsset.setAssetSource(AssetSourceEnum.BUYETF);
            etfInAsset.setAccountId(etfCallbackDTO.getAccountId());
            etfInAsset.setApplyMoney(orderRecordPO.getApplyMoney());
            etfInAsset.setProductAssetStatus(ProductAssetStatusEnum.HOLD_ING);
            etfInAsset.setRechargeOrderNo(0L);
            etfInAsset.setApplyTime(orderRecordPO.getApplyTime());
            etfInAsset.setConfirmTime(orderRecordPO.getConfirmTime());
            etfInAsset.setUpdateTime(DateUtils.now());
            etfInAsset.setCreateTime(DateUtils.now());
            etfInAsset.setTotalTmpOrderId(orderRecordPO.getTotalTmpOrderId());
            etfInAsset.setTmpOrderId(orderRecordPO.getTmpOrderId());
            if (TransferStatusEnum.SUCCESS == etfCallbackDTO.getTransferStatus()) {
                //Etf订单状态为成功
                orderRecordPO.setTmpOrderTradeStatus(TmpOrderExecuteStatusEnum.SUCCESS);
                orderRecordPO.setConfirmMoney(etfCallbackDTO.getConfirmMoney());
                orderRecordPO.setConfirmTradeShares(etfCallbackDTO.getConfirmShare());

                etfInAsset.setConfirmShare(etfCallbackDTO.getConfirmShare());
                etfInAsset.setProductCode(etfCallbackDTO.getProductCode());
                etfInAsset.setConfirmMoney(etfCallbackDTO.getConfirmMoney());

                BigDecimal tmpRemainMoney = orderRecordPO.getApplyMoney().subtract(orderRecordPO.getConfirmMoney());
                //remainMoney = remainMoney.add(tmpRemainMoney);
                BigDecimal remainMoneyByTotalTmp = mpTotalTemp.get(totalTmpOrder);
                remainMoneyByTotalTmp = remainMoneyByTotalTmp.add(tmpRemainMoney);
                mpTotalTemp.put(totalTmpOrder, remainMoneyByTotalTmp);
            } else {
                orderRecordPO.setTmpOrderTradeStatus(TmpOrderExecuteStatusEnum.FAIL);
                orderRecordPO.setConfirmTime(etfCallbackDTO.getConfirmTime());
                //失败的订单申请金额记录在对应的main 或 sub的cash上
                etfInAsset.setConfirmShare(BigDecimal.ZERO);
                etfInAsset.setProductCode(Constants.CASH);
                etfInAsset.setConfirmMoney(orderRecordPO.getApplyMoney());
            }
            finishEtfOrders.add(orderRecordPO);
            etfBuyConfirms.add(etfInAsset);
        }

        //处理cash配比
        String cash = Constants.CASH;
        for(Map.Entry me: mpTotalTemp.entrySet()){
            Long newTotalTmp = (Long)me.getKey();
            //List<TmpOrderRecordPO> cashOrders = tmpOrderRecordService.listTmpCashOrderByTotalTmpOrderId(totalTmpOrder,
            List<TmpOrderRecordPO> cashOrders = tmpOrderRecordService.listTmpCashOrderByTotalTmpOrderId(newTotalTmp,
                    Lists.newArrayList(Constants.MAIN_CASH, Constants.SUB_CASH));
            for (TmpOrderRecordPO orderRecordPO : cashOrders) {
                AccountAssetPO cashInAsset = new AccountAssetPO();
                cashInAsset.setAssetSource(AssetSourceEnum.RECHARGE);
                cashInAsset.setAccountId(orderRecordPO.getAccountId());
                cashInAsset.setConfirmShare(BigDecimal.ZERO);
                cashInAsset.setConfirmMoney(orderRecordPO.getApplyMoney());
                cashInAsset.setApplyMoney(orderRecordPO.getApplyMoney());
                cashInAsset.setProductAssetStatus(ProductAssetStatusEnum.HOLD_ING);
                cashInAsset.setRechargeOrderNo(0L);
                cashInAsset.setApplyTime(orderRecordPO.getApplyTime());
                cashInAsset.setConfirmTime(DateUtils.now());
                cashInAsset.setTotalTmpOrderId(newTotalTmp);
                cashInAsset.setCreateTime(DateUtils.now());
                cashInAsset.setUpdateTime(DateUtils.now());
                cashInAsset.setTmpOrderId(orderRecordPO.getTmpOrderId());
                cashInAsset.setProductCode(cash);
                etfBuyConfirms.add(cashInAsset);

                orderRecordPO.setTmpOrderTradeStatus(TmpOrderExecuteStatusEnum.SUCCESS);
                orderRecordPO.setConfirmMoney(orderRecordPO.getApplyMoney());
                finishEtfOrders.add(orderRecordPO);
            }
        }

        //对应的卖出中的 unbuy 资产修改为确认
        for(Map.Entry me: mpTotalTemp.entrySet()){
            AccountAssetPO queryParam = new AccountAssetPO();
            queryParam.setAccountId(etfCallbackDTOs.get(0).getAccountId());
            queryParam.setTotalTmpOrderId((Long)me.getKey());
            queryParam.setProductAssetStatus(ProductAssetStatusEnum.SELL_ING);
            List<AccountAssetPO> sellingAccountAssets = accountAssetService.queryList(queryParam);
            for (AccountAssetPO sellingAccountAsset : sellingAccountAssets) {
                sellingAccountAsset.setProductAssetStatus(ProductAssetStatusEnum.CONFIRM_SELL);
                sellingAccountAsset.setUpdateTime(DateUtils.now());
                sellingAccountAsset.setConfirmTime(DateUtils.now());
                sellingAccountAsset.setConfirmMoney(sellingAccountAsset.getApplyMoney());
                sellAccountAssets.add(sellingAccountAsset);
            }
    //        //残渣金额 = 总申请金额 - 成功金额 - 对冲金额
            BigDecimal remailMoney =  (BigDecimal)me.getValue();
            if (remailMoney.compareTo(BigDecimal.ZERO) > 0) {
                AccountAssetPO remainInAsset = new AccountAssetPO();
                remainInAsset.setAssetSource(AssetSourceEnum.BUYRESIDUAL);
                remainInAsset.setAccountId(etfCallbackDTOs.get(0).getAccountId());
                remainInAsset.setConfirmShare(BigDecimal.ZERO);
                remainInAsset.setConfirmMoney(remailMoney);
                remainInAsset.setApplyMoney(remailMoney);
                remainInAsset.setProductAssetStatus(ProductAssetStatusEnum.HOLD_ING);
                remainInAsset.setRechargeOrderNo(0L);
                remainInAsset.setApplyTime(DateUtils.now());
                remainInAsset.setConfirmTime(DateUtils.now());
                remainInAsset.setTotalTmpOrderId((Long)me.getKey());
                remainInAsset.setCreateTime(DateUtils.now());
                remainInAsset.setUpdateTime(DateUtils.now());
                remainInAsset.setTmpOrderId(Sequence.next());
                remainInAsset.setProductCode(cash);
                etfBuyConfirms.add(remainInAsset);
            }
        }

        analysisSupportService.handelEtfBuyCallBack(finishEtfOrders, sellAccountAssets, etfBuyConfirms, accountRedeemPOs);

        //记录过程数据  交易成本
        StaticForEtfCallBackEvent staticForEtfCallBackEvent = new StaticForEtfCallBackEvent();
        staticForEtfCallBackEvent.setAccountId(accountId);
        staticForEtfCallBackEvent.setTransactionCostBuy(totalTransCost);
        staticForEtfCallBackEvent.setCashResidual(remainMoney);
        eventBus.post(staticForEtfCallBackEvent);
    }

    private Map<Long, List<EtfCallbackDTO>> getEtfCallbackMap(List<EtfCallbackDTO> params) {
        Map<Long, List<EtfCallbackDTO>> map = Maps.newHashMap();
        for (EtfCallbackDTO etfCallbackDTO : params) {
            List<EtfCallbackDTO> list = map.get(etfCallbackDTO.getAccountId());
            if (CollectionUtils.isEmpty(list)) {
                list = Lists.newArrayList();
                map.put(etfCallbackDTO.getAccountId(), list);
            }
            list.add(etfCallbackDTO);
        }
        return map;
    }

    /**
     * 1、首先查松鼠账户余额 2、在查询投资账户余额
     *
     * @param userAssetDTO
     * @return
     */
    @Override
    public RpcMessage<UserAssetWapperDTO> queryUserAssets(UserAssetDTO userAssetDTO) {
        if (StringUtils.isEmpty(userAssetDTO.getClientId())) {
            throw new BusinessException(Resources.getMessage("CLIENTID_NOT_EXISTS"));
        }
//        UserInfoDTO userInfoDTO = new UserInfoDTO();
//        userInfoDTO.setClientId(userAssetDTO.getClientId());
        UserInfoResDTO userInfo = userServiceRemoteService.queryByClientId(userAssetDTO.getClientId());
//        if (null == userInfo) {
//            throw new BusinessException("不存在的用户");
//        }
        Date today = DateUtils.now();
        UserAssetWapperDTO userAssetWapperDTO = new UserAssetWapperDTO();
        userAssetWapperDTO.setClientId(userAssetDTO.getClientId());

        //获取汇率
        //ExchangeRatePO exchangeRateParam = new ExchangeRatePO();
        //exchangeRateParam.setExchangeRateType(ExchangeRateTypeEnum.SAXO_FXRT2);
        //ExchangeRatePO exchangeRatePO = exchangeRateService.queryLastExchangeRate(exchangeRateParam);
        //UserAssetStatistic  统计每个账户上的资产(每个goal上的资产)
        BigDecimal totalInvestmentSgd = BigDecimal.ZERO;
        BigDecimal totalInvestmentUsd = BigDecimal.ZERO;
        List<UserAssetsDetailWrapperDTO> assetsDetailWrapperDTOs = Lists.newArrayList();
        //首先先查询该用户有多少个账户
        AccountUserPO accountUser = new AccountUserPO();
        accountUser.setClientId(userAssetDTO.getClientId());
        List<AccountUserPO> accountUserPOList = accountUserService.listByAccountUserPo(accountUser);
        if (!CollectionUtils.isEmpty(accountUserPOList)) {
            for (AccountUserPO accountUserPO : accountUserPOList) {
                log.info("accountUserPO goalID {} ", accountUserPO.getGoalId());
                UserAssetsDetailWrapperDTO assetsDetailWrapperDTO = new UserAssetsDetailWrapperDTO();
                //资产统计
                UserFundNavPO userFundNavParam = new UserFundNavPO();
                userFundNavParam.setClientId(userAssetDTO.getClientId());
                userFundNavParam.setAccountId(accountUserPO.getAccountId());
                userFundNavParam.setGoalId(accountUserPO.getGoalId());
                userFundNavParam.setNavTime(today);
                UserFundNavPO userFundNav = userFundNavService.selectOneByNavTime(userFundNavParam);
                //用户收益
                UserProfitInfoPO userProfitInfoParam = new UserProfitInfoPO();
                userProfitInfoParam.setAccountId(accountUserPO.getAccountId());
                userProfitInfoParam.setClientId(accountUserPO.getClientId());
                userProfitInfoParam.setGoalId(accountUserPO.getGoalId());
                userProfitInfoParam.setProfitDate(today);
                UserProfitInfoPO userProfitInfoPO = userProfitInfoService.selectOneByTime(userProfitInfoParam);
                if (null == userFundNav) {
                    userFundNavParam.setNavTime(DateUtils.addDateByDay(today, -1));
                    userFundNav = userFundNavService.selectOneByNavTime(userFundNavParam);
                    userProfitInfoParam.setProfitDate(DateUtils.addDateByDay(today, -1));
                    userProfitInfoPO = userProfitInfoService.selectOneByTime(userProfitInfoParam);
                }
                boolean noHasAssets = userFundNav == null || userFundNav.getTotalAsset().compareTo(BigDecimal.ZERO) <= 0;
                if (noHasAssets) {
                    continue;
                }
                //USD总充值
//            BigDecimal totalDepositUsd = getUserGoalUsdRechargeMoney(accountUserPO);
                //USD总提现
//            BigDecimal totalWithdrawalUsd = getUserGoalUsdRedeemMoney(accountUserPO);
                //SGD总充值
                BigDecimal totalDepositSgd = getUserGoalSgdRechargeMoney(accountUserPO);
                //SGD总提现
                BigDecimal totalWithdrawalSgd = getUserGoalRedeemMoney(accountUserPO);
                //SGD投资资产 = SGD总充值 - SGD总提现
                BigDecimal investedAmountSgd = totalDepositSgd.subtract(totalWithdrawalSgd).setScale(6, BigDecimal.ROUND_HALF_UP);
                //SGD持有资产
                assetsDetailWrapperDTO.setPortfolioId(accountUserPO.getPortfolioId());
                assetsDetailWrapperDTO.setGoalId(accountUserPO.getGoalId());
                assetsDetailWrapperDTO.setTotalDepositMYR(totalDepositSgd.setScale(2, BigDecimal.ROUND_HALF_UP));
                assetsDetailWrapperDTO.setTotalWithdrawalMYR(totalWithdrawalSgd.setScale(2, BigDecimal.ROUND_HALF_UP));
                assetsDetailWrapperDTO.setInvestedAmountMYR(investedAmountSgd.setScale(2, BigDecimal.ROUND_HALF_UP));
                //assetsDetailWrapperDTO.setAssetValueSGD(userFundNav.getTotalAsset().multiply(exchangeRatePO.getUsdToSgd()).setScale(2, BigDecimal.ROUND_HALF_UP));
                //assetsDetailWrapperDTO.setAssetValueSGD(userFundNav.getTotalAsset().multiply(exchangeRatePO.getUsdToSgd()).setScale(2, BigDecimal.ROUND_DOWN)); // Edit By WooiTatt
                if (null != userProfitInfoPO) {
                    //assetsDetailWrapperDTO.setTotalReturnSGD(userProfitInfoPO.getTotalProfit().setScale(2, BigDecimal.ROUND_HALF_UP));
                    assetsDetailWrapperDTO.setTotalReturnMYR(userProfitInfoPO.getTotalProfit().setScale(2, BigDecimal.ROUND_DOWN)); //Edit By WooiTatt
                    assetsDetailWrapperDTO.setPortfolioReturn(userProfitInfoPO.getPortfolioProfit().setScale(2, BigDecimal.ROUND_HALF_UP));
                    //BigDecimal fxImpactSgd = assetsDetailWrapperDTO.getTotalReturnSGD().subtract(assetsDetailWrapperDTO.getPortfolioReturn());
                    //assetsDetailWrapperDTO.setFxImpactSGD(fxImpactSgd);
                }
                                //Added by WooiTatt
                BigDecimal pendingGoalDeposit = getUserGoalPendingDeposit(accountUserPO);//userServiceRemoteService.getPendingDeposit(userFundNavParam.getClientId(), userFundNavParam.getGoalId());
                RedeemApplyPO redeemApplyPO = new RedeemApplyPO();
                redeemApplyPO.setClientId(userAssetWapperDTO.getClientId());
                redeemApplyPO.setGoalId(userFundNavParam.getGoalId());
                redeemApplyPO.setRedeemApplyStatus(RedeemApplyStatusEnum.HANDLING);
                List<RedeemApplyPO> lRedeemApply = bankVARedeemService.listRedeemApply(redeemApplyPO);
                BigDecimal pendingGoalWithdraw = BigDecimal.ZERO;
                for (RedeemApplyPO redeemApply : lRedeemApply) {
                    pendingGoalWithdraw = redeemApply.getApplyMoney().add(pendingGoalWithdraw);
                }
                assetsDetailWrapperDTO.setPendingDeposit(pendingGoalDeposit);
                assetsDetailWrapperDTO.setPendingWithdraw(pendingGoalWithdraw);
                assetsDetailWrapperDTOs.add(assetsDetailWrapperDTO);

                totalInvestmentUsd = totalInvestmentUsd.add(userFundNav.getTotalAsset());
                //totalInvestmentSgd = totalInvestmentSgd.add(assetsDetailWrapperDTO.getAssetValueSGD());
            }

        }

        //查询用户松鼠账户余额
        //SquirrelsaveCashBean squirrelsaveCashBean = getSquirrelsaveCash(userAssetDTO.getClientId(), exchangeRatePO);
        BigDecimal bigDecimalClientId = new BigDecimal(userAssetDTO.getClientId());
        log.info("assetsDetailWrapperDTOs size {} ", assetsDetailWrapperDTOs.size());
        userAssetWapperDTO.setClientId(userAssetDTO.getClientId());
        userAssetWapperDTO.setAssetDetails(assetsDetailWrapperDTOs);
        //userAssetWapperDTO.setSquireelCashSGD(squirrelsaveCashBean.getTotalCashSgd().setScale(2, BigDecimal.ROUND_HALF_UP));
        //userAssetWapperDTO.setSquireelCashUsd(squirrelsaveCashBean.getTotalCashUsd().setScale(2, BigDecimal.ROUND_HALF_UP));
        //userAssetWapperDTO.setTotalWealthSGD(totalInvestmentSgd.add(squirrelsaveCashBean.getTotalCashSgd().setScale(2, BigDecimal.ROUND_HALF_UP)));
        //userAssetWapperDTO.setTotalWealthUsd(totalInvestmentUsd.add(squirrelsaveCashBean.getTotalCashUsd()).setScale(2, BigDecimal.ROUND_HALF_UP));
        userAssetWapperDTO.setTotalWealthMYR(totalInvestmentUsd.setScale(2, BigDecimal.ROUND_HALF_UP));
        //userAssetWapperDTO.setTotalInvestmentSGD(totalInvestmentSgd.setScale(2, BigDecimal.ROUND_HALF_UP));
        userAssetWapperDTO.setTotalInvestmentMYR(totalInvestmentUsd.setScale(2, BigDecimal.ROUND_HALF_UP));

        //get USD and SGD freeze amount from t_bank_virtual_account
        /*VirtualBankAccountPO queryParamUSD = new VirtualBankAccountPO();
        queryParamUSD.setClientId(Integer.parseInt(userAssetDTO.getClientId()));
        queryParamUSD.setCurrency(CurrencyEnum.USD);

        VirtualBankAccountPO bankVirtualAccountUSD = virtualBankAccountService.queryBankVirtualAccountById(queryParamUSD);
        BigDecimal bigFreezeUSD = bankVirtualAccountUSD.getFreezeAmount();
        userAssetWapperDTO.setFreezeAmountUSD(bigFreezeUSD.setScale(2, BigDecimal.ROUND_HALF_UP));

        VirtualBankAccountPO queryParamSGD = new VirtualBankAccountPO();
        queryParamSGD.setClientId(Integer.parseInt(userAssetDTO.getClientId()));
        queryParamSGD.setCurrency(CurrencyEnum.SGD);

        VirtualBankAccountPO bankVirtualAccountSGD = virtualBankAccountService.queryBankVirtualAccountById(queryParamSGD);
        BigDecimal bigFreezeSGD = bankVirtualAccountSGD.getFreezeAmount();
        userAssetWapperDTO.setFreezeAmountSGD(bigFreezeSGD.setScale(2, BigDecimal.ROUND_HALF_UP));
        */
        //Added By WooiTatt = PendingTotalDepositAmount & PendingTotalWithdrawAmount
        /*BigDecimal pendingDeposit = userServiceRemoteService.getPendingDeposit(userAssetWapperDTO.getClientId(), null);
        userAssetWapperDTO.setPendingTotalDeposit(pendingDeposit.setScale(2, BigDecimal.ROUND_HALF_DOWN));
    
        RedeemApplyPO redeemApplyPO = new RedeemApplyPO();
        redeemApplyPO.setClientId(userAssetWapperDTO.getClientId());
        redeemApplyPO.setRedeemApplyStatus(RedeemApplyStatusEnum.HANDLING);
        List<RedeemApplyPO> lRedeemApply = bankVARedeemService.listRedeemApply(redeemApplyPO);
        BigDecimal pendingWithdraw = BigDecimal.ZERO;
        for (RedeemApplyPO redeemApply : lRedeemApply) {
            pendingWithdraw = redeemApply.getApplyMoney().add(pendingWithdraw);
        }
        userAssetWapperDTO.setPendingTotalWithdraw(pendingWithdraw);*/
        return RpcMessage.success(userAssetWapperDTO);
    }

    private SquirrelsaveCashBean getSquirrelsaveCash(String clientId, ExchangeRatePO exchangeRatePO) {
        SquirrelsaveCashBean squirrelsaveCashBean = new SquirrelsaveCashBean();
        BankVirtualAccountDTO bankVirtualAccountDTO = new BankVirtualAccountDTO();
        bankVirtualAccountDTO.setClientId(clientId);
        List<BankVirtualAccountResDTO> virtualAccountResDTOs = userServiceRemoteService.queryListBankVirtualAccount(bankVirtualAccountDTO);
        if (CollectionUtils.isEmpty(virtualAccountResDTOs)) {
            return squirrelsaveCashBean;
        }
        BigDecimal usdMoney = BigDecimal.ZERO;
        BigDecimal sgdMoney = BigDecimal.ZERO;
        for (BankVirtualAccountResDTO virtualAccountResDTO : virtualAccountResDTOs) {
            if (CurrencyEnum.USD == virtualAccountResDTO.getCurrency()) {
                usdMoney = virtualAccountResDTO.getCashAmount();
            } else {
                sgdMoney = virtualAccountResDTO.getCashAmount();
            }
        }
        BigDecimal totalSgdMoney = sgdMoney.add(usdMoney.multiply(exchangeRatePO.getUsdToSgd())).setScale(6, BigDecimal.ROUND_HALF_UP);
        BigDecimal totalUsdMoney = usdMoney.add(sgdMoney.divide(exchangeRatePO.getUsdToSgd(), 6, BigDecimal.ROUND_HALF_UP));
        squirrelsaveCashBean.setCashSgd(sgdMoney);
        squirrelsaveCashBean.setCashUsd(usdMoney);
        squirrelsaveCashBean.setTotalCashSgd(totalSgdMoney);
        squirrelsaveCashBean.setTotalCashUsd(totalUsdMoney);
        return squirrelsaveCashBean;
    }

    private BigDecimal getUserGoalRedeemMoney(AccountUserPO accountUserPO) {
        /*SaxoAccountOrderPO sgdRedeemParam = new SaxoAccountOrderPO();
        sgdRedeemParam.setAccountId(accountUserPO.getAccountId());
        sgdRedeemParam.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
        sgdRedeemParam.setOperatorType(SaxoOrderTradeTypeEnum.COME_OUT);
        sgdRedeemParam.setActionType(SaxoOrderActionTypeEnum.SAXOTOUOB);
        sgdRedeemParam.setCurrency(CurrencyEnum.SGD);
        sgdRedeemParam.setGoalId(accountUserPO.getGoalId());
        sgdRedeemParam.setClientId(accountUserPO.getClientId());
        return saxoAccountOrderService.getClientGoalMoney(sgdRedeemParam);*/
        
        AccountRedeemPO accountRedeemPO = new AccountRedeemPO();
        accountRedeemPO.setClientId(accountUserPO.getClientId());
        accountRedeemPO.setGoalId(accountUserPO.getGoalId());
        accountRedeemPO.setTncfStatus(TncfStatusEnum.SUCCESS);
        List<AccountRedeemPO> listAccountRedeem = accountRedeemService.listAccountRedeem(accountRedeemPO);
        BigDecimal totalRedeem = BigDecimal.ZERO;
        for(AccountRedeemPO accRedeemPO:listAccountRedeem){
            totalRedeem = totalRedeem.add(accRedeemPO.getConfirmMoney()).setScale(6, BigDecimal.ROUND_HALF_UP);
        }
        return totalRedeem;
        
        
    }

    private BigDecimal getUserGoalSgdRechargeMoney(AccountUserPO accountUserPO) {
        /*SaxoAccountOrderPO sgdRechargeParam = new SaxoAccountOrderPO();
        sgdRechargeParam.setAccountId(accountUserPO.getAccountId());
        sgdRechargeParam.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
        sgdRechargeParam.setOperatorType(SaxoOrderTradeTypeEnum.COME_INTO);
        sgdRechargeParam.setActionType(SaxoOrderActionTypeEnum.UOBTOSAXO);
        sgdRechargeParam.setCurrency(CurrencyEnum.SGD);
        sgdRechargeParam.setClientId(accountUserPO.getClientId());
        sgdRechargeParam.setGoalId(accountUserPO.getGoalId());
        return saxoAccountOrderService.getClientGoalMoney(sgdRechargeParam);*/
        
        AccountRechargePO accountRechargePO = new AccountRechargePO();
        accountRechargePO.setClientId(accountUserPO.getClientId());
        accountRechargePO.setGoalId(accountUserPO.getGoalId());
        accountRechargePO.setTpcfStatus(TpcfStatusEnum.SUCCESS);
        List<AccountRechargePO> listAccountRecharge = accountRechargeService.listAccountRecharge(accountRechargePO);
        BigDecimal totalRecharge = BigDecimal.ZERO;
        for(AccountRechargePO accRechargePO:listAccountRecharge){
            totalRecharge = totalRecharge.add(accRechargePO.getRechargeAmount()).setScale(6, BigDecimal.ROUND_HALF_UP);
        }
        return totalRecharge;
    }
    
    private BigDecimal getUserGoalPendingDeposit(AccountUserPO accountUserPO) {
        /*SaxoAccountOrderPO sgdRechargeParam = new SaxoAccountOrderPO();
        sgdRechargeParam.setAccountId(accountUserPO.getAccountId());
        sgdRechargeParam.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
        sgdRechargeParam.setOperatorType(SaxoOrderTradeTypeEnum.COME_INTO);
        sgdRechargeParam.setActionType(SaxoOrderActionTypeEnum.UOBTOSAXO);
        sgdRechargeParam.setCurrency(CurrencyEnum.SGD);
        sgdRechargeParam.setClientId(accountUserPO.getClientId());
        sgdRechargeParam.setGoalId(accountUserPO.getGoalId());
        return saxoAccountOrderService.getClientGoalMoney(sgdRechargeParam);*/
        
        AccountRechargePO accountRechargePO = new AccountRechargePO();
        accountRechargePO.setClientId(accountUserPO.getClientId());
        accountRechargePO.setGoalId(accountUserPO.getGoalId());
        //accountRechargePO.setTpcfStatus(TpcfStatusEnum.SUCCESS);
        List<AccountRechargePO> listAccountRecharge = accountRechargeService.listAccountRecharge(accountRechargePO);
        BigDecimal totalRecharge = BigDecimal.ZERO;
        for(AccountRechargePO accRechargePO:listAccountRecharge){
            if(accRechargePO.getTpcfStatus() != TpcfStatusEnum.SUCCESS){
                totalRecharge = totalRecharge.add(accRechargePO.getRechargeAmount()).setScale(6, BigDecimal.ROUND_HALF_UP);
            }
        }
        return totalRecharge;
    }

//    private BigDecimal getUserGoalUsdRedeemMoney(AccountUserPO accountUserPO) {
//        AccountRedeemPO accountRedeemParam = new AccountRedeemPO();
//        accountRedeemParam.setAccountId(accountUserPO.getAccountId());
//        accountRedeemParam.setClientId(accountUserPO.getClientId());
//        accountRedeemParam.setGoalId(accountUserPO.getGoalId());
//        accountRedeemParam.setOrderStatus(RedeemOrderStatusEnum.SUCCESS);
//        List<AccountRedeemPO> accountRedeemPOs = accountRedeemService.listAccountRedeem(accountRedeemParam);
//        BigDecimal redeemMoney = BigDecimal.ZERO;
//        for (AccountRedeemPO accountRedeemPO : accountRedeemPOs) {
//            if (accountRedeemPO.getConfirmMoney().compareTo(BigDecimal.ZERO) <= 0) {
//                continue;
//            }
//            redeemMoney = redeemMoney.add(accountRedeemPO.getConfirmMoney()).setScale(6, BigDecimal.ROUND_HALF_UP);
//        }
//        return redeemMoney;
//    }
    private BigDecimal getUserGoalUsdRechargeMoney(AccountUserPO accountUserPO) {
        AccountRechargePO accountRechargeParam = new AccountRechargePO();
        accountRechargeParam.setAccountId(accountUserPO.getAccountId());
        accountRechargeParam.setClientId(accountUserPO.getClientId());
        accountRechargeParam.setGoalId(accountUserPO.getGoalId());
        accountRechargeParam.setOrderStatus(RechargeOrderStatusEnum.SUCCESS);
        List<AccountRechargePO> accountRechargePOs = accountRechargeService.listAccountRecharge(accountRechargeParam);
        BigDecimal rechargeMoney = BigDecimal.ZERO;
        for (AccountRechargePO po : accountRechargePOs) {
            if (po.getRechargeAmount().compareTo(BigDecimal.ZERO) > 0) {
                rechargeMoney = rechargeMoney.add(po.getRechargeAmount()).setScale(6, BigDecimal.ROUND_HALF_UP);
            }
        }
        return rechargeMoney;
    }

    @Override
    public void etfCallBackMock(Long totalTmpOrderId) {
        //查询所etf的收市价格
        Date yesterday = DateUtils.addDateByDay(DateUtils.now(), -1);
        Map<String, BigDecimal> etfClosingPriceMap = assetFundNavService.getEtfClosingPrice(yesterday);
        List<EtfCallbackDTO> params = Lists.newArrayList();
        List<TmpOrderRecordPO> pos = tmpOrderRecordService.listByTotalTmpOrderId(totalTmpOrderId);
        for (TmpOrderRecordPO po : pos) {
            if (Constants.MAIN_CASH.equals(po.getProductCode()) || Constants.SUB_CASH.equals(po.getProductCode())
                    || Constants.CASH.equals(po.getProductCode())) {
                continue;
            }
            EtfCallbackDTO dto = new EtfCallbackDTO();
            dto.setAccountId(po.getAccountId());
            dto.setTransferStatus(TransferStatusEnum.SUCCESS);
            dto.setConfirmMoney(po.getApplyMoney().subtract(new BigDecimal(10)));
            BigDecimal etfClosePrice = etfClosingPriceMap.get(po.getProductCode());
            dto.setConfirmShare(dto.getConfirmMoney().divide(etfClosePrice, 6, BigDecimal.ROUND_HALF_DOWN));
            dto.setConfirmTime(DateUtils.now());
            dto.setProductCode(po.getProductCode());
            dto.setTmpOrderId(po.getTmpOrderId());
            dto.setTransCost(new BigDecimal("10.01"));
            params.add(dto);
        }
        etfCallBack(params);
    }

    @Resource
    private AccountFundNavJob accountFundNavJob;

    @Override
    public void assetsFundNav(String date, Long accountId) {
        if (StringUtils.isEmpty(date)) {
            accountFundNavJob.calculateAssetFundNav(DateUtils.now(), accountId);
        } else {
            log.info("#######账户自建基金净值计算手动触发，date:{},accountId:{}#######", date, accountId);
            accountFundNavJob.calculateAssetFundNavByDate(date, accountId);
            log.info("#######账户自建基金净值计算手动完成，date:{},accountId:{}#######", date, accountId);
        }
    }

    @Override
    public RpcMessage<AccountEtfAssetResDTO> queryAccountEtfShare(AccountEtfAssetReqDTO accountEtfAssetReqDTO) {
        Map<String, BigDecimal> dataMap = Maps.newHashMap();
        AccountEtfAssetResDTO accountEtfAssetResDTO = new AccountEtfAssetResDTO();
        accountEtfAssetResDTO.setDataMap(dataMap);

        AccountAssetPO queryParam = new AccountAssetPO();
        queryParam.setAccountId(accountEtfAssetReqDTO.getAccountId());
        List<AccountAssetPO> accountAssetPOs = accountAssetService.listAccountUnBuyAssets(queryParam);
        if (CollectionUtils.isEmpty(accountAssetPOs)) {
            return RpcMessage.success(accountEtfAssetResDTO);
        }

        //查询该账号上的总资产
        List<AccountAssetStatisticBean> accountAssetStatisticBeens = AccountAssetStatistic.statAccountShare(accountAssetPOs);
        //过滤出持有中的资产
        for (AccountAssetStatisticBean accountAssetStatisticBean : accountAssetStatisticBeens) {
            if (accountAssetStatisticBean.getProductAssetStatus() == ProductAssetStatusEnum.HOLD_ING) {
                dataMap.put(accountAssetStatisticBean.getProductCode(), accountAssetStatisticBean.getProductShare());
            }
        }

        return RpcMessage.success(accountEtfAssetResDTO);
    }

    @Override
    public RpcMessage<List<AccountAssetResDTO>> queryAccountAssets(AccountAssetReqDTO accountAssetDTO) {
        List<AccountAssetResDTO> accountAssetResDTOList = Lists.newArrayList();

        AccountAssetPO queryParam = new AccountAssetPO();
        queryParam.setAccountId(accountAssetDTO.getAccountId());
        List<AccountAssetPO> accountAssetPOs = accountAssetService.listAccountUnBuyAssets(queryParam);
        if (CollectionUtils.isEmpty(accountAssetPOs)) {
            return RpcMessage.success(accountAssetResDTOList);
        }

        //查询该账号上的总资产
        Map<String, BigDecimal> etfClosingPriceMap = assetFundNavService.getEtfClosingPrice(DateUtils.addDays(DateUtils.now(), -1));
        if(etfClosingPriceMap.isEmpty()){
            etfClosingPriceMap = assetFundNavService.getEtfClosingPrice(DateUtils.addDays(DateUtils.now(), -2));
        }
        log.info("{},收市价:{}", DateUtils.addDays(DateUtils.now(), -1), etfClosingPriceMap);

        List<AccountAssetStatisticBean> accountAssetStatisticBeans = AccountAssetStatistic.statAccountAsset(accountAssetPOs, etfClosingPriceMap);
        accountAssetResDTOList = BeanMapperUtils.mapList(accountAssetStatisticBeans, AccountAssetResDTO.class);

        return RpcMessage.success(accountAssetResDTOList);
    }

    @Override
    public RpcMessage<List<AccountAssetResDTO>> queryAccountAssets(AccountAssetReqDTO accountAssetDTO, Date endDate) {
        List<AccountAssetResDTO> accountAssetResDTOList = Lists.newArrayList();

        AccountAssetPO queryParam = new AccountAssetPO();
        queryParam.setAccountId(accountAssetDTO.getAccountId());
        queryParam.setCreateEndTime(endDate);
        List<AccountAssetPO> accountAssetPOs = accountAssetService.listAccountUnBuyAssets(queryParam);
        if (CollectionUtils.isEmpty(accountAssetPOs)) {
            return RpcMessage.success(accountAssetResDTOList);
        }

        //查询该账号上的总资产
        Map<String, BigDecimal> etfClosingPriceMap = assetFundNavService.getEtfClosingPrice(DateUtils.addDays(endDate, -1));
        if(etfClosingPriceMap.isEmpty()){
            etfClosingPriceMap = assetFundNavService.getEtfClosingPrice(DateUtils.addDays(DateUtils.now(), -2));
        }
        log.info("{},收市价:{}", DateUtils.addDays(DateUtils.now(), -1), etfClosingPriceMap);

        List<AccountAssetStatisticBean> accountAssetStatisticBeans = AccountAssetStatistic.statAccountAsset(accountAssetPOs, etfClosingPriceMap);
        accountAssetResDTOList = BeanMapperUtils.mapList(accountAssetStatisticBeans, AccountAssetResDTO.class);

        return RpcMessage.success(accountAssetResDTOList);
    }

    @Override
    public RpcMessage<List<UserProfitInfoResDTO>> queryPortfolioReturn(String portfolioId) {
        AccountUserPO queryParam = new AccountUserPO();
        queryParam.setPortfolioId(portfolioId);
        List<AccountUserPO> accountUserPOList = accountUserService.listByAccountUserPo(queryParam);
        if (CollectionUtils.isEmpty(accountUserPOList)) {
            return RpcMessage.success(Lists.newArrayList());
        }
        List<String> goalIds = accountUserPOList.stream().map(AccountUserPO::getGoalId).collect(Collectors.toList());
        List<UserProfitInfoPO> userProfitInfoPOs = userProfitInfoService.listByGoalds(goalIds, DateUtils.now());
        if (CollectionUtils.isEmpty(userProfitInfoPOs)) {
            return RpcMessage.success(Lists.newArrayList());
        }
        return RpcMessage.success(BeanMapperUtils.mapList(userProfitInfoPOs, UserProfitInfoResDTO.class));

    }

    private AccountTypeEnum getAccountTypeByAccountId(Long accountId) {
        AccountInfoPO accountInfoPO = accountInfoService.queryById(accountId);
        return accountInfoPO.getInvestType();
    }

    @Override
    public RpcMessage<AccountTotalAssetDTO> queryAccountTotalInfo(Date nowDate) {
        //查询所etf总的价格
        BigDecimal totalHoldMoney = BigDecimal.ZERO;
        //das的总现金资产
        BigDecimal totalCash;
        try {
            Date calDate = DateUtils.getDate(nowDate, 15, 10, 0);
            Map<String, BigDecimal> etfClosingPriceMap = assetFundNavService.getEtfClosingPrice(DateUtils.addDays(calDate, -1));
            log.info("{},收市价:{}", DateUtils.addDays(DateUtils.now(), -1), etfClosingPriceMap);
            List<AccountAssetPO> accountAssetPOs = accountAssetService.listAccountUnBuyAssets(new AccountAssetPO(calDate));

            //查询某个账号上的总资产
            List<AccountAssetStatisticBean> accountAssetStatisticBeens = AccountAssetStatistic.statAccountAsset(accountAssetPOs, etfClosingPriceMap);

            log.info("accountAssetStatisticBeens {} ", accountAssetStatisticBeens);

            totalCash = accountAssetStatisticBeens.stream().filter(input
                    -> input.getProductCode().equals(Constants.CASH) || input.getProductCode().equals(Constants.UN_BUY_PRODUCT_CODE))
                    .map(AccountAssetStatisticBean::getProductMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
            AccountEtfSharesStaticPO etfs = accountEtfSharesStaticService.getListByDate(DateUtils.dayEnd(DateUtils.now()));

            if (etfs != null) {
                Field[] fields = etfs.getClass().getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field f = fields[i];
                    f.setAccessible(true);
                    if (f.get(etfs) instanceof BigDecimal) {
                        BigDecimal closePrice = etfClosingPriceMap.get(f.getName().toUpperCase());
                        if (closePrice == null) {
                            closePrice = BigDecimal.ZERO;
                        }
                        totalHoldMoney = totalHoldMoney.add(((BigDecimal) f.get(etfs)).multiply(closePrice));

                    }
                }
            }
            log.info("totalCash after account asset {} ", totalCash);
            log.info("etf totalHoldMoney:{}.", totalHoldMoney);

            //总持仓加入pft部分的资产
            PivotPftAccountPO pivotPftAccountQuery = new PivotPftAccountPO();
            List<PivotPftAccountPO> pivotPftAccountPOS = pivotPftAccountService.queryList(pivotPftAccountQuery);
            for (PivotPftAccountPO pivotPftAccountPO : pivotPftAccountPOS) {
                if (pivotPftAccountPO.getProductCode().equals(Constants.CASH) || pivotPftAccountPO.getProductCode().equals(Constants.UN_BUY_PRODUCT_CODE)) {
                    continue;
                }
                BigDecimal closePrice = etfClosingPriceMap.get(pivotPftAccountPO.getProductCode().toUpperCase());
                if (closePrice == null) {
                    closePrice = BigDecimal.ZERO;
                }
                BigDecimal share = pivotPftAccountPO.getShare();
                if (pivotPftAccountPO.getShare() == null) {
                    share = BigDecimal.ZERO;
                }
                totalHoldMoney = totalHoldMoney.add(share.multiply(closePrice));
            }

            log.info("addpft totalHoldMoney:{}.", totalHoldMoney);

            BigDecimal totalOverMoney;
            try {
                RpcMessage<BalanceApplyResDTO> resDTORpcMessage = balanceApplyRemoteService.queryBalanceByApplyDate(new BalanceApplyReqDTO(calDate));
                BalanceApplyResDTO balanceApplyResDTO = resDTORpcMessage.getContent();
                totalOverMoney
                        = //                        balanceApplyResDTO.getCharityFee()
                        balanceApplyResDTO.getErrorHandlingFee()
                                .add(balanceApplyResDTO.getPivotFee())
                                .add(balanceApplyResDTO.getPftCash());
                totalCash = totalCash.add(totalOverMoney);
            } catch (Exception e) {
                log.error("获取三种余额异常,e:{}", e);
                ErrorLogAndMailUtil.logError(log, e);
            }

            log.info("totalCash after totalOverMoney {} , ", totalCash);

            try {
                RpcMessage<BigDecimal> rpcMessage = dividendRemoteService.getDividendMoney(calDate);
                BigDecimal totalDividend = rpcMessage.getContent();
                if (totalDividend == null) {
                    totalDividend = BigDecimal.ZERO;
                }
                totalCash = totalCash.add(totalDividend);
            } catch (Exception e) {
                log.error("获取分红余额,e:{}", e);
                ErrorLogAndMailUtil.logError(log, e);
            }
            log.info("totalCash after dividend {} , ", totalCash);

//            List<UserAssetPO> userAssetPOS=userAssetService.queryListByCond(new UserAssetPO(nowDate));
//            totalHoldMoney=handleHoldData(etfClosingPriceMap,userAssetPOS);
//            totalCash = assetFundNavService.statisticTotalCash(nowDate);
        } catch (Exception e) {
            log.error("获取总cash资产或etf总持有失败,e:{}", e);
            return RpcMessage.error(e.getMessage());
        }
        return RpcMessage.success(new AccountTotalAssetDTO(totalCash, totalHoldMoney));
    }

    @Override
    public RpcMessage<List<ProductStatisDTO>> querySpecificData(Date nowDate) {
        try {
//            List<ProductStatisDTO> productStatisDTOS = userAssetService.querySpecificData(nowDate);
            Map<String, BigDecimal> etfClosingPriceMap = assetFundNavService.getEtfClosingPrice(DateUtils.addDays(nowDate, -1));
            List<ProductStatisDTO> productStatisDTOList = Lists.newArrayList();

            PivotPftAccountPO pivotPftAccountQuery = new PivotPftAccountPO();
            List<PivotPftAccountPO> pivotPftAccountPOS = pivotPftAccountService.queryList(pivotPftAccountQuery);
            Map<String, PivotPftAccountPO> pivotPftAccountPOMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(pivotPftAccountPOS)) {
                pivotPftAccountPOMap = Maps.uniqueIndex(pivotPftAccountPOS, new Function<PivotPftAccountPO, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable PivotPftAccountPO input) {
                        return input.getProductCode();
                    }
                });
            }

            AccountEtfSharesStaticPO etfs = accountEtfSharesStaticService.getListByDate(nowDate);

            Field[] fields = etfs.getClass().getDeclaredFields();

            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                f.setAccessible(true);
                if (f.get(etfs) instanceof BigDecimal) {
                    BigDecimal closePrice = etfClosingPriceMap.get(f.getName().toUpperCase());
                    BigDecimal pftShares = BigDecimal.ZERO;
                    PivotPftAccountPO pivotPftAccountPO = pivotPftAccountPOMap.get(f.getName().toUpperCase());
                    if (pivotPftAccountPO != null) {
                        pftShares = pivotPftAccountPO.getShare();
                    }

                    if (closePrice == null) {
                        closePrice = BigDecimal.ZERO;
                    }
                    BigDecimal etfStatis = (BigDecimal) f.get(etfs);
                    if (etfStatis == null) {
                        etfStatis = BigDecimal.ZERO;
                    }
                    BigDecimal pftProductMoney = pftShares.multiply(closePrice);
                    BigDecimal totalProductMoney = etfStatis.multiply(closePrice);
                    totalProductMoney = totalProductMoney.add(pftProductMoney);
                    BigDecimal totalProductShares = ((BigDecimal) f.get(etfs)).add(pftShares);
                    productStatisDTOList.add(new ProductStatisDTO(f.getName().toUpperCase(), totalProductMoney, totalProductShares));
                }
            }
            return RpcMessage.success(productStatisDTOList);
        } catch (Exception e) {
            log.error("获取每只ETF的份额和金额失败，e:{}", e);
            return RpcMessage.error(MessageFormat.format("获取每只ETF的份额和金额失败，e:{0}", e));
        }

    }
    
    
    @Override
    public RpcMessage<List<TmpOrderRecordResDTO>> getTmpOrderRecord(TmpOrderRecordResDTO tmpOrderRecordResDTO) {
        TmpOrderRecordPO tmpOrderRecordQuery = new TmpOrderRecordPO();
        tmpOrderRecordQuery.setTmpOrderTradeStatus(TmpOrderExecuteStatusEnum.HANDLING);
        List<TmpOrderRecordPO> listTempOrderPO = tmpOrderRecordService.listTmpOrderRecord(tmpOrderRecordQuery);
        List<TmpOrderRecordResDTO> listTmpOrderResDto = BeanMapperUtils.mapList(listTempOrderPO,TmpOrderRecordResDTO.class);
        
        return RpcMessage.success(listTmpOrderResDto);
    }
    
    @Override
    public void updateTpcfTncf(Long totalTmpOrderId) {
        try{
            AccountRechargePO accountRechargePO = new AccountRechargePO();
            accountRechargePO.setTotalTmpOrderId(totalTmpOrderId);
            accountRechargePO.setTpcfStatus(TpcfStatusEnum.TPCF);
            List<AccountRechargePO> listAccountRecharge = accountRechargeService.listAccountRecharge(accountRechargePO);
            for(AccountRechargePO accRechargePO : listAccountRecharge){
                accRechargePO.setTpcfStatus(TpcfStatusEnum.ASSETBUYCOMPLETE);
                accountRechargeService.updateAccountRecharge(accRechargePO);
            }

            AccountRedeemPO accountRedeemPO = new AccountRedeemPO();
            accountRedeemPO.setTotalTmpOrderId(totalTmpOrderId);
            accountRedeemPO.setTncfStatus(TncfStatusEnum.TNCF);
            List<AccountRedeemPO> listAccountRedeemPO = accountRedeemService.listAccountRedeem(accountRedeemPO);
            for(AccountRedeemPO accRedeemPO : listAccountRedeemPO){
                accRedeemPO.setTncfStatus(TncfStatusEnum.ASSETSELLCOMPLETE);
                accountRedeemService.updateByTotalTmpOrderId(accRedeemPO);
            }
        }catch(Exception e){log.error(e.toString());}
        
    }

	@Override
	public RpcMessage<String> getDasUnit() {
		BigDecimal result = BigDecimal.ZERO;
		try {
			List<AccountAssetPO> accountAssetPOs = accountAssetService.listAccountUnBuyAssets(new AccountAssetPO());
			log.info("====accountAssetPOs.size():{}", accountAssetPOs.size());
	        log.info("=====accountAssetPOs:{}", JSON.toJSONString(accountAssetPOs));
			for(AccountAssetPO accountAsset : accountAssetPOs) {
				if(accountAsset.getProductAssetStatus().equals(ProductAssetStatusEnum.HOLD_ING)) {
					result = result.add(accountAsset.getConfirmShare());
				}
				if(accountAsset.getProductAssetStatus().equals(ProductAssetStatusEnum.CONFIRM_SELL)) {
					result = result.subtract(accountAsset.getConfirmShare());
				}
			}

	        List<PivotPftAssetPO> list = pivotPftAssetService.queryListByTime(new PivotPftAssetPO());
	        log.info("====list.size():{}", list.size());
	        for(PivotPftAssetPO asset : list) {
	        	if(asset.getProductAssetStatus().equals(ProductAssetStatusEnum.HOLD_ING)) {
	        		result = result.add(asset.getConfirmShare());
	        	}
	        	if(asset.getProductAssetStatus().equals(ProductAssetStatusEnum.CONFIRM_SELL)) {
					result = result.subtract(asset.getConfirmShare());
				}
	        }
	        
			return RpcMessage.success(result+"");
		}catch (Exception e) {
			log.info("=====e:{}",e);
		}
		return RpcMessage.success("0.00");
	}

	@Override
	public RpcMessage<String> saveAhamRecon(List<AccountAssetResDTO> listAhamReconReq) {
		try {
                    
                    for(AccountAssetResDTO accountAssetResDTO:listAhamReconReq){
                        BigDecimal dasUnit = accountAssetResDTO.getProductShare();
			BigDecimal inputUnit = accountAssetResDTO.getReconShareUnit();
                        
                        AhamReconPO newAhamRecon = new AhamReconPO();
                        newAhamRecon.setDasUnit(dasUnit);
			newAhamRecon.setInputUnit(inputUnit);
			newAhamRecon.setDiffUnit(dasUnit.subtract(inputUnit));
                        newAhamRecon.setProductCode(accountAssetResDTO.getProductCode());
                        if(dasUnit.compareTo(inputUnit) == 0) {
				newAhamRecon.setReconResult(AhamReconResultEnum.BALANCED);
			}else {
				newAhamRecon.setReconResult(AhamReconResultEnum.IMBALANCED);
			}
                        //Date dasTime = new SimpleDateFormat("yyyy-MM-dd").parse(DateUtils.now());
                        newAhamRecon.setDasTime(DateUtils.parseDate(DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT)));
			log.info("====newAhamRecon:{}", JSON.toJSONString(newAhamRecon));
			ahamReconService.add(newAhamRecon);
                    }
			/*BigDecimal dasUnit = new BigDecimal(map.get("dasUnit"));
			BigDecimal inputUnit = new BigDecimal(map.get("inputUnit"));
			Date dasTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(map.get("dasTime"));
			log.info("===dasUnit:{},===inputUnit:{},===dasTime:{}", dasUnit, inputUnit, dasTime);
			AhamReconPO newAhamRecon = new AhamReconPO();
			newAhamRecon.setDasUnit(dasUnit);
			newAhamRecon.setInputUnit(inputUnit);
			newAhamRecon.setDiffUnit(dasUnit.subtract(inputUnit));
			if(dasUnit.compareTo(inputUnit) == 0) {
				newAhamRecon.setReconResult(AhamReconResultEnum.BALANCED);
			}else {
				newAhamRecon.setReconResult(AhamReconResultEnum.IMBALANCED);
			}*/
			//newAhamRecon.setDasTime(dasTime);
			//log.info("====newAhamRecon:{}", JSON.toJSONString(newAhamRecon));
			//ahamReconService.add(newAhamRecon);
			return RpcMessage.success("SUCCESS");
		}catch (Exception e) {
			return RpcMessage.error(e.toString());
		}
	}

	@Override
	public RpcMessage<JSONArray> findAhamReconPage(Date startCreateTime, Date endCreateTime) {
		log.info("startCreateTime:{}", startCreateTime);
		log.info("endCreateTime:{}", endCreateTime);
		JSONArray result = new JSONArray();
		AhamReconPO query = new AhamReconPO();
		query.setStartCreateTime(startCreateTime);
		query.setEndCreateTime(endCreateTime);
		List<AhamReconPO> ahamReconList = ahamReconService.findAhamRecon(query);
		log.info("==========ahamReconList.size()", ahamReconList.size());
		for(AhamReconPO ahamRecon : ahamReconList) {
			Map<String, String> object = new HashMap<String, String>();
                        object.put("scheme", ahamRecon.getProductCode()+"");
			object.put("dasUnit", ahamRecon.getDasUnit()+"");
			object.put("inputUnit", ahamRecon.getInputUnit()+"");
			object.put("diffUnit", ahamRecon.getDiffUnit()+"");
			if(ahamRecon.getReconResult().equals(AhamReconResultEnum.BALANCED)) {
				object.put("reconResult", "BALANCED");
			}else {
				object.put("reconResult", "IMBALANCED");
			}
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			object.put("createTime", dateFormat.format(ahamRecon.getCreateTime()));
			result.add(object);
		}
		return RpcMessage.success(result);
	}
        
        @Override
	public RpcMessage<List<AccountAssetResDTO>> getDasProdUnit() {

            BigDecimal result = BigDecimal.ZERO;
            List<AccountAssetResDTO> listAccountAssetResDTO = Lists.newArrayList();
            try {
                AhamReconPO query = new AhamReconPO();
                query.setStartCreateTime(DateUtils.parseDate(DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT)));
                List<AhamReconPO> ahamReconList = ahamReconService.findAhamRecon(query);
                if(ahamReconList.size() >= 1){
                    RpcMessage.error("Today has done reconciliation");
                }
                List<ProductInfoResDTO> allProductInfo = modelServiceRemoteService.queryAllProductInfo();
                for(ProductInfoResDTO productInfoResDTO : allProductInfo) {
                    AccountAssetPO accountAssetPO = new AccountAssetPO();
                    accountAssetPO.setProductCode(productInfoResDTO.getProductCode());
                    List<AccountAssetPO> listAccountAssetPOs = accountAssetService.listAccountUnBuyAssets(accountAssetPO);
                    BigDecimal totalBuyUnit = BigDecimal.ZERO;
                    BigDecimal totalSellUnit = BigDecimal.ZERO;
                    for(AccountAssetPO accAssetPO:listAccountAssetPOs){
                        if(accAssetPO.getProductAssetStatus().equals(ProductAssetStatusEnum.HOLD_ING)) {
                            totalBuyUnit = totalBuyUnit.add(accAssetPO.getConfirmShare());
                        }else if(accAssetPO.getProductAssetStatus().equals(ProductAssetStatusEnum.CONFIRM_SELL)){
                            totalSellUnit = totalSellUnit.add(accAssetPO.getConfirmShare());
                        }
                    }
                    PivotPftAssetPO pivotPftAssetPO = new PivotPftAssetPO();
                    pivotPftAssetPO.setProductCode(productInfoResDTO.getProductCode());
                    List<PivotPftAssetPO> listPftAsset = pivotPftAssetService.queryListByTime(pivotPftAssetPO);
                    BigDecimal totalPftBuyUnit = BigDecimal.ZERO;
                    BigDecimal totalPftSellUnit = BigDecimal.ZERO;
                    for(PivotPftAssetPO pftAssetPO:listPftAsset){
                        if(pftAssetPO.getProductAssetStatus().equals(ProductAssetStatusEnum.HOLD_ING)) {
                            totalPftBuyUnit = totalPftBuyUnit.add(pftAssetPO.getConfirmShare());
                        }else if(pftAssetPO.getProductAssetStatus().equals(ProductAssetStatusEnum.CONFIRM_SELL)){
                            totalPftSellUnit = totalPftSellUnit.add(pftAssetPO.getConfirmShare());
                        }
                    }
                    BigDecimal totalHoldingShare = BigDecimal.ZERO;
                    totalHoldingShare = totalHoldingShare.add(totalBuyUnit).add(totalPftBuyUnit).subtract(totalSellUnit).subtract(totalPftSellUnit);
                    AccountAssetResDTO accountAssetResDTO = new AccountAssetResDTO();
                    accountAssetResDTO.setProductCode(productInfoResDTO.getProductCode());
                    accountAssetResDTO.setProductShare(totalHoldingShare);
                    listAccountAssetResDTO.add(accountAssetResDTO);
                }
            

                /*List<AccountAssetPO> accountAssetPOs = accountAssetService.listAccountUnBuyAssets(new AccountAssetPO());
                log.info("====accountAssetPOs.size():{}", accountAssetPOs.size());
                log.info("=====accountAssetPOs:{}", JSON.toJSONString(accountAssetPOs));
                    for(AccountAssetPO accountAsset : accountAssetPOs) {
                            if(accountAsset.getProductAssetStatus().equals(ProductAssetStatusEnum.HOLD_ING)) {
                                    result = result.add(accountAsset.getConfirmShare());
                            }
                            if(accountAsset.getProductAssetStatus().equals(ProductAssetStatusEnum.CONFIRM_SELL)) {
                                    result = result.subtract(accountAsset.getConfirmShare());
                            }
                    }

            List<PivotPftAssetPO> list = pivotPftAssetService.queryListByTime(new PivotPftAssetPO());
            log.info("====list.size():{}", list.size());
            for(PivotPftAssetPO asset : list) {
                    if(asset.getProductAssetStatus().equals(ProductAssetStatusEnum.HOLD_ING)) {
                            result = result.add(asset.getConfirmShare());
                    }
                    if(asset.getProductAssetStatus().equals(ProductAssetStatusEnum.CONFIRM_SELL)) {
                                    result = result.subtract(asset.getConfirmShare());
                            }
            }*/

                    //return RpcMessage.success(result+"");
            
            }catch (Exception e) {
                    log.info("=====e:{}",e);
                    RpcMessage.error("Failed");
            }
          
            return RpcMessage.success(listAccountAssetResDTO);
	}

}
