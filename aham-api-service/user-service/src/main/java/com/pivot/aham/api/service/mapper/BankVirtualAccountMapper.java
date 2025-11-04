package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.BankVirtualAccount;
import com.pivot.aham.common.core.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by luyang.li on 18/12/3.
 */
public interface BankVirtualAccountMapper extends BaseMapper<BankVirtualAccount> {

    int saveBankVirtualAccount(BankVirtualAccount bankVirtualAccount);

    List<BankVirtualAccount> queryListByVirtualAccounts(@Param("virtualAccountIds")List<String> virtualAccountIds);

    int saveBatch(List<BankVirtualAccount> bankVirtualAccounts);

    BankVirtualAccount quaryBankVirtualAccount(BankVirtualAccount queryParam);

    void updateBankVirtualAccount(BankVirtualAccount bankVirtualAccount);

    List<BankVirtualAccount> queryListBankVirtualAccount(BankVirtualAccount queryParam);

    List<BankVirtualAccount> getListByTradeTime(Map<String,Object> params);
    
    BankVirtualAccount queryBankVirtualAccDescByCurrency(BankVirtualAccount queryParam);

}
