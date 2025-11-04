package com.pivot.aham.api.service.service;

import com.pivot.aham.api.server.dto.BankVirtualAccountDTO;
import com.pivot.aham.api.service.mapper.model.BankVirtualAccount;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;
import java.util.Map;

/**
 * Created by luyang.li on 18/12/3.
 */
public interface BankVirtualAccountService extends BaseService<BankVirtualAccount> {

    /**
     * 保存用户虚拟账户信息
     *
     * @param bankVirtualAccount
     * @return
     */
    int saveBankVirtualAccount(BankVirtualAccount bankVirtualAccount);

    /**
     * 批量查询虚拟账户
     *
     * @param virtualAccountIds
     * @return
     */
    List<BankVirtualAccount> queryListByVirtualAccounts(List<String> virtualAccountIds);

    /**
     * 批量插入
     *
     * @param virtualAccountList
     */
    int saveBatch(List<BankVirtualAccountDTO> virtualAccountList);

    void statisticsAmount(BankVirtualAccount bankVirtualAccount);

    /**
     * 根据用户clientId查询虚拟账户
     *
     * @param virtualAccount
     * @return
     */
    List<BankVirtualAccount> queryListByClient(BankVirtualAccount virtualAccount);

    BankVirtualAccount quaryBankVirtualAccount(BankVirtualAccount queryParam);

    List<BankVirtualAccount> queryListBankVirtualAccount(BankVirtualAccount queryParam);
    List<BankVirtualAccount> getListByTradeTime(Map<String,Object> params);
    
    void executeCreateVirtualAccount(String currency, String numberOfCreation);
}
