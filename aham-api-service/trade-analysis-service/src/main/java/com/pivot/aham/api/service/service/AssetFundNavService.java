package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.AccountFundNavPO;
import com.pivot.aham.common.core.base.BaseService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
public interface AssetFundNavService extends BaseService<AccountFundNavPO> {

    /**
     * 该账户是否是首次进行净值计算(首次投资 或者 全部体现后在投资)
     *
     * @param now
     * @param accountId
     * @return
     */
    boolean fundNavIsFirst(Date now, Long accountId);

    /**
     * 获取productCode的收市价
     *
     * @param now
     * @return
     */
    Map<String,BigDecimal> getEtfClosingPrice(Date now);

    Map<String,BigDecimal> getEtfClosingPrice();


    AccountFundNavPO selectOneByNavTime(AccountFundNavPO accountFundNavPO);

    void saveTodayAssetFundNav(AccountFundNavPO todayFundNav);

    List<AccountFundNavPO> queryListByNavTime(AccountFundNavPO accountFundNavPO);

}
