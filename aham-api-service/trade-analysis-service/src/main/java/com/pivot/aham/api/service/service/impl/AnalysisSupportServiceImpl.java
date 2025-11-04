package com.pivot.aham.api.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.server.dto.resp.TradingEnableResult;
import com.pivot.aham.api.server.remoteservice.SaxoTradeRemoteService;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.service.job.wrapperbean.AccountTpcfTncfBean;
import com.pivot.aham.api.service.job.wrapperbean.BuyEtfTmpOrderBean;
import com.pivot.aham.api.service.mapper.model.*;
import com.pivot.aham.api.service.service.*;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
import com.pivot.aham.common.core.support.context.ApplicationContextHolder;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.enums.analysis.DividendHandelStatusEnum;
import com.pivot.aham.common.enums.analysis.EtfExecutedStatusEnum;
import com.pivot.aham.common.enums.analysis.RedeemOrderStatusEnum;
import com.pivot.aham.common.enums.analysis.SaxoToUobTransferStatusEnum;
import com.pivot.aham.common.enums.recharge.TncfStatusEnum;
import com.pivot.aham.common.enums.recharge.TpcfStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by luyang.li on 19/1/7.
 */
@Service
@Slf4j
public class AnalysisSupportServiceImpl implements AnalysisSupportService {

    @Resource
    private AccountRechargeService accountRechargeService;
    @Resource
    private AccountAssetService accountAssetService;
    @Resource
    private SaxoAccountOrderService saxoAccountOrderService;
    @Resource
    private TmpOrderRecordService tmpOrderRecordService;
    @Resource
    private UserAssetService userAssetService;
    @Resource
    private AssetFundNavService assetFundNavService;
    @Resource
    private UserFundNavService userFundNavService;
    @Resource
    private AccountInfoService accountInfoService;
    @Resource
    private AccountRedeemService accountRedeemService;
    @Resource
    private RedeemApplyService redeemApplyService;

    @Autowired
    private UserEtfSharesStaticService userEtfSharesStaticService;
    @Resource
    private AccountDividendService accountDividendService;
    @Resource
    private SaxoTradeRemoteService saxoTradeRemoteService;
    @Resource
    private UserDividendService userDividendService;
    @Resource
    private UserEtfSharesService userEtfSharesService;
    @Resource
    private UserServiceRemoteService userServiceRemoteService; //Added By WooiTatt 

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveUserRecharge(AccountRechargePO accountRecharge, AccountAssetPO accountAsset) {
        accountRechargeService.saveAccountRecharge(accountRecharge);
        accountAssetService.saveAccountAsset(accountAsset);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateRechargeCallback(AccountAssetPO accountAssetPO, AccountRechargePO accountRechargePO) {
        accountAssetService.update(accountAssetPO);
        accountRechargeService.updateAccountRecharge(accountRechargePO);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void handelTransferCallback(List<SaxoAccountOrderPO> saxoAccountOrderPOAdds,
            SaxoAccountOrderPO saxoAccountOrder,
            AccountRechargePO accountRecharge) {
        accountRechargeService.saveAccountRecharge(accountRecharge);
//        accountAssetService.saveAccountAsset(accountAsset);
        saxoAccountOrderService.saveBatch(saxoAccountOrderPOAdds);
        saxoAccountOrderService.updateSaxoAccountOrder(saxoAccountOrder);     
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void handelRechargeAndAsset(List<AccountAssetPO> inUnBuyList,
            List<AccountAssetPO> outUnBuyList) {
        accountAssetService.updateBatch(inUnBuyList);
        accountAssetService.insertBatch(outUnBuyList);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void handelEtfBuyCallBack(List<TmpOrderRecordPO> finishEtfOrders,
            List<AccountAssetPO> sellAccountAssets,
            List<AccountAssetPO> etfBuyConfirms,
            List<AccountRedeemPO> accountRedeemPOs) {
        //更新tmpOrder
        if (CollectionUtils.isNotEmpty(finishEtfOrders)) {
            tmpOrderRecordService.updateBatch(finishEtfOrders);
        }
        //更新accountUnbuy
        if (CollectionUtils.isNotEmpty(sellAccountAssets)) {
            accountAssetService.updateBatch(sellAccountAssets);
        }
        //更新对冲的提现单成功
        if (CollectionUtils.isNotEmpty(accountRedeemPOs)) {
            for (AccountRedeemPO accountRedeemPO : accountRedeemPOs) {
                accountRedeemPO.setOrderStatus(RedeemOrderStatusEnum.SUCCESS);
                accountRedeemService.updateById(accountRedeemPO);

                log.info("更新当日redeem:{}", JSON.toJSONString(accountRedeemPO));
                RedeemApplyPO redeemApplyPO = redeemApplyService.queryById(accountRedeemPO.getRedeemApplyId());
                log.info("更新当日redeemapply:{}", JSON.toJSONString(redeemApplyPO));
                redeemApplyPO.setEtfExecutedStatus(EtfExecutedStatusEnum.SUCCESS);
                redeemApplyPO.setSaxoToUobTransferStatus(SaxoToUobTransferStatusEnum.WAITAPPLY);
                //处理提现申请单状态
                redeemApplyService.updateRedeemApplyById(redeemApplyPO);
            }
        }
        //添加accountEtf (etf + cash)
        if (CollectionUtils.isNotEmpty(etfBuyConfirms)) {
            for (AccountAssetPO etfBuyConfirm : etfBuyConfirms) {
                AccountAssetPO queryPo = new AccountAssetPO();
                queryPo.setTmpOrderId(etfBuyConfirm.getTmpOrderId());
                AccountAssetPO accountAssetPO = accountAssetService.queryAccountAsset(queryPo);
                if (null == accountAssetPO) {
                    accountAssetService.saveAccountAsset(etfBuyConfirm);
                } else {
                    etfBuyConfirm.setId(accountAssetPO.getId());
                    accountAssetService.update(etfBuyConfirm);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void handelFundNavAndUserAsset(AccountFundNavPO todayAccountFundNav,
            List<UserFundNavPO> userFundNavPOList,
            List<UserAssetPO> userAssetPOs,
            AccountInfoPO accountInfoPO,
            AccountTpcfTncfBean accountTpcfTncfBean) {
        assetFundNavService.saveTodayAssetFundNav(todayAccountFundNav);
        for (UserFundNavPO todayUserFundNav : userFundNavPOList) {
            userFundNavService.saveTodayUserFundNav(todayUserFundNav);
        }
        
        userAssetService.saveOrUpdateUserTodayAsset(userAssetPOs);
//        for (UserAssetPO userAssetPO : userAssetPOs) {
//            userAssetService.saveOrUpdateUserTodayAsset(userAssetPO);
//        }
        //插入用户etf持有宽表
//        List<UserEtfSharesStaticPO> userEtfSharesStaticList = handleEtf(userAssetPOs);
//        for(UserEtfSharesStaticPO userEtfSharesStatic:userEtfSharesStaticList){
//            userEtfSharesStaticService.updateOrInsert(userEtfSharesStatic);
//        }

//        for (UserProfitInfoPO userProfitInfoPO : userProfitInfoPOs) {
//            userProfitInfoService.saveOrUpdateUserProfit(userProfitInfoPO);
//        }
        accountInfoService.updateOrInsert(accountInfoPO);
        for (AccountRechargePO accountRechargePO : accountTpcfTncfBean.getAccountRechargePOS()) {
            accountRechargePO.setTpcfStatus(TpcfStatusEnum.SUCCESS);
            accountRechargeService.updateAccountRecharge(accountRechargePO);
            userServiceRemoteService.updateUserRechargeStatusToSuccess(accountRechargePO.getId()); //Added By WooiTatt
        }
        for (AccountDividendPO accountDividendPO : accountTpcfTncfBean.getAccountDividendPOS()) {
            accountDividendPO.setHandelStatus(DividendHandelStatusEnum.SUCCESS);
            accountDividendService.updateAccountDividend(accountDividendPO);
        }
        for (AccountRedeemPO accountRedeemPO : accountTpcfTncfBean.getAccountRedeemPOs()) {
            accountRedeemPO.setTncfStatus(TncfStatusEnum.SUCCESS);
            accountRedeemService.updateById(accountRedeemPO);
        }
        for (UserDividendPO userDividendPO : accountTpcfTncfBean.getUserDividendPOS()) {
            userDividendPO.setHandelStatus(DividendHandelStatusEnum.SUCCESS);
            userDividendService.update(userDividendPO);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void handelUnBuyEtfAdjCash(List<AccountAssetPO> accountAssetPOList,
            List<AccountRedeemPO> accountRedeemPOs) {
        if (CollectionUtils.isNotEmpty(accountAssetPOList)) {
            accountAssetService.insertBatch(accountAssetPOList);
        }
        for (AccountRedeemPO accountRedeemPO : accountRedeemPOs) {
            accountRedeemPO.setOrderStatus(RedeemOrderStatusEnum.SUCCESS);
            accountRedeemService.updateById(accountRedeemPO);

            RedeemApplyPO redeemApplyPO = redeemApplyService.queryById(accountRedeemPO.getRedeemApplyId());
            redeemApplyPO.setEtfExecutedStatus(EtfExecutedStatusEnum.SUCCESS);
            redeemApplyPO.setSaxoToUobTransferStatus(SaxoToUobTransferStatusEnum.WAITAPPLY);
            //处理提现申请单状态
            redeemApplyService.updateRedeemApplyById(redeemApplyPO);
        }
    }

    @Override
    @Transactional
    public void handelBuyEtf(BuyEtfTmpOrderBean buyEtfTmpOrderBean,
                             List<AccountAssetPO> accountAssetPOs) {
        accountAssetService.insertBatch(accountAssetPOs);
        if (CollectionUtils.isNotEmpty(buyEtfTmpOrderBean.getSuccessTmpOrders())) {
            for (TmpOrderRecordPO tmpOrderRecordPO : buyEtfTmpOrderBean.getSuccessTmpOrders()) {
                tmpOrderRecordService.updateTmpOrder(tmpOrderRecordPO);
            }
        }
        if (CollectionUtils.isNotEmpty(buyEtfTmpOrderBean.getFailTmpOrders())) {
            for (TmpOrderRecordPO failTmpOrder : buyEtfTmpOrderBean.getFailTmpOrders()) {
                tmpOrderRecordService.updateTmpOrder(failTmpOrder);
            }
        }
    }

    @Override
    public boolean checkSaxoIsTranding() {
        //如果是测试环境，不进行检查
        String activeProfile = ApplicationContextHolder.getActiveProfile();
        if (!activeProfile.equals("prod") && !activeProfile.equals("prod2")) {
            return true;
        }

        log.info("充值完分析下指令转账UOB到SAXO,检查是否开市");
        RpcMessage<TradingEnableResult> tradingEnableResultRpcMessage = saxoTradeRemoteService.tradingEnable();
        log.info("充值完分析下指令转账UOB到SAXO,检查是否开市返回:{}", JSON.toJSONString(tradingEnableResultRpcMessage));
        if (RpcMessageStandardCode.OK.value() != tradingEnableResultRpcMessage.getResultCode()) {
            log.error("充值完分析下指令转账UOB到SAXO,检查是否开市,接口调用失败");
            ErrorLogAndMailUtil.logError(log, "充值完分析下指令转账UOB到SAXO,检查是否开市,接口调用失败");
            return false;
        }
        if (!tradingEnableResultRpcMessage.getContent().isTradingEnable()) {
            log.info("充值完分析下指令转账UOB到SAXO,检查是否开市,查询结果是：未开市,不做转账交易");
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public void rechargeToTpcfAddAssets(AccountRechargePO accountRechargePO, AccountAssetPO accountAsset) {
        accountRechargeService.updateAccountRecharge(accountRechargePO);
        accountAssetService.saveAccountAsset(accountAsset);
    }

    @Override
    public void handleTncfSuccess(List<AccountRedeemPO> accountRedeemPOS) {
        for (AccountRedeemPO accountRedeemPO : accountRedeemPOS) {
            accountRedeemPO.setTncfStatus(TncfStatusEnum.SUCCESS);
            accountRedeemService.updateById(accountRedeemPO);
        }
    }

}
