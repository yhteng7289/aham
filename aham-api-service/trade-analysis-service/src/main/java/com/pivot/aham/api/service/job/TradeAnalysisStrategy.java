package com.pivot.aham.api.service.job;

import com.pivot.aham.api.service.mapper.model.AccountInfoPO;
import com.pivot.aham.api.service.mapper.model.AccountRedeemPO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 交易分析
 *
 * @author addison
 * @since 2018年12月17日
 */
public interface TradeAnalysisStrategy {

    /**
     * 只有提现
     *
     * @param totalUnBuy
     * @param tncf
     * @param accountInfo
     * @param accountRedeemPOs
     */
    void onlyWithdrawaltradeAnalysis(BigDecimal totalUnBuy, BigDecimal tncf,
                                     AccountInfoPO accountInfo, List<AccountRedeemPO> accountRedeemPOs);

    /**
     * 只有充值
     *
     * @param totalRecharge
     * @param totalRedeem
     * @param accountInfo
     * @param accountRedeemPOs
     */
    void onlyRechargeTradeAnalysis(BigDecimal totalRecharge, BigDecimal totalRedeem,
            AccountInfoPO accountInfo, List<AccountRedeemPO> accountRedeemPOs);

    /**
     *
     * @param accountRedeemPOs
     * @param totalUnbuy
     * @param accountId
     */
    void handelUnBuyEtfAdjCash(List<AccountRedeemPO> accountRedeemPOs,
            BigDecimal totalUnbuy, Long accountId);

    /**
     *
     * @param accountRedeemPOs
     * @param totalUnbuy
     * @param accountId
     */
    void handelDirectWithdrawCash(List<AccountRedeemPO> accountRedeemPOs,
            BigDecimal totalUnbuy, Long accountId);
}
