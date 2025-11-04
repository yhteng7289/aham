package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.job.wrapperbean.AccountTpcfTncfBean;
import com.pivot.aham.api.service.job.wrapperbean.BuyEtfTmpOrderBean;
import com.pivot.aham.api.service.mapper.model.*;

import java.util.List;

/**
 * Created by luyang.li on 19/1/7.
 */
public interface AnalysisSupportService {

    /**
     * 用户同步UOB充值记录,保存虚拟账户资金,保存充值流水,保存充值资产(持有)
     *
     * @param accountRecharge
     * @param accountAsset
     */
    void saveUserRecharge(AccountRechargePO accountRecharge, AccountAssetPO accountAsset);

    void updateRechargeCallback(AccountAssetPO accountAssetPO, AccountRechargePO accountRechargePO);

    void handelTransferCallback(List<SaxoAccountOrderPO> saxoAccountOrderPOAdds,
                                SaxoAccountOrderPO saxoAccountOrderPOUpdates,
                                AccountRechargePO accountRecharge);

    void handelRechargeAndAsset(List<AccountAssetPO> inUnBuyList,
                                List<AccountAssetPO> outUnBuyList);

    void handelEtfBuyCallBack(List<TmpOrderRecordPO> finishEtfOrders,
                              List<AccountAssetPO> sellAccountAssets,
                              List<AccountAssetPO> etfBuyConfirms,
                              List<AccountRedeemPO> accountRedeemPOs);


    void handelFundNavAndUserAsset(AccountFundNavPO todayAccountFundNav,
                                   List<UserFundNavPO> userFundNavPOList,
                                   List<UserAssetPO> userAssetPOs,
                                   AccountInfoPO accountInfoPO,
                                   AccountTpcfTncfBean accountTpcfTncfBean);

    void handelUnBuyEtfAdjCash(List<AccountAssetPO> accountAssetPOList,
                               List<AccountRedeemPO> accountRedeemPOs);

    void handelBuyEtf(BuyEtfTmpOrderBean buyEtfTmpOrderBean,
                      List<AccountAssetPO> accountAssetPOs);

    /**
     * 检查saxo是否开市
     *
     * @return
     */
    boolean checkSaxoIsTranding();

    /**
     * 充值转Tpcf同时添加资产
     *
     * @param accountRechargePO
     * @param accountAsset
     */
    void rechargeToTpcfAddAssets(AccountRechargePO accountRechargePO, AccountAssetPO accountAsset);

    void handleTncfSuccess(List<AccountRedeemPO> accountRedeemPOS);

}
