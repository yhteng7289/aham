package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.AccountRechargePO;

import java.math.BigDecimal;

/**
 * Created by luyang.li on 19/1/23.
 */
public interface RechargeService {

    /**
     * 查询用户充值金额
     *
     * @param accountRechargePO
     * @return
     */
    BigDecimal getUserRechargeMoney(AccountRechargePO accountRechargePO);

    /**
     * 分析UOB的入金下指令转入SAXO
     */
    void handelUobTransferToSaxo();

}
