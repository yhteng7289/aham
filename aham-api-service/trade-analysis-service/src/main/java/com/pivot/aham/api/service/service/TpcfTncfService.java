package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.job.wrapperbean.AnalyTpcfTncfWrapperBean;
import com.pivot.aham.api.service.mapper.model.AccountInfoPO;
import com.pivot.aham.api.service.mapper.model.AccountRechargePO;
import com.pivot.aham.api.service.mapper.model.AccountRedeemPO;
import com.pivot.aham.api.service.mapper.model.AccountUserPO;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 2018/12/24.
 */
public interface TpcfTncfService {

    /**
     * 获取Account的Tpcf
     *
     * @param accountId
     * @return
     */
    List<AccountRechargePO> getAccountTpcf(Long accountId);

    /**
     * 获取Account的Tncf
     *
     * @return
     * @param accountId
     */
    BigDecimal getAccountTncfMoney(Long accountId);

    /**
     * 获取Account交易回调之前的Tncf
     *
     * @return
     * @param accountId
     */
    BigDecimal getAccountHandlingTncfMoney(Long accountId);

    /**
     * 查询TPCF money
     *
     * @param accountRechargePOS
     * @return
     */
    BigDecimal getAccountTpcfMoney(List<AccountRechargePO> accountRechargePOS);

    /**
     * 处理提现申请为TNCF
     *
     * @param accountInfoPO
     * @return
     */
    AnalyTpcfTncfWrapperBean handelTncfFromProcessing(AccountInfoPO accountInfoPO);

    /**
     * 获取用户Tpcf
     *
     * @return
     */
    BigDecimal getUserTpcfMoney(AccountUserPO accountUserPO);

    List<AccountRedeemPO> getAccountTncf(Long accountId);

    List<AccountRedeemPO> getAccountHandlingTncf(Long accountId);

    /**
     * 处理充值为TPCF
     *
     * @param accountInfoPO
     * @return
     */
    AnalyTpcfTncfWrapperBean handelTpcfFromProcessing(AccountInfoPO accountInfoPO);

    BigDecimal getUserTncfMoney(AccountUserPO accountUserPO);
    BigDecimal getUserTncfShares(AccountUserPO accountUserPO);
    
    List<AccountRechargePO> getAccountTpcfWithAssetComplete(Long accountId);

}
