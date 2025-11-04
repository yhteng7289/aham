package com.pivot.aham.api.service.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.mapper.model.BankNameAlias;
import com.pivot.aham.common.core.base.BaseMapper;

import java.util.List;

/**
 * Created by WooiTatt
 */
public interface BankNameAliasMapper extends BaseMapper<BankNameAlias> {
    
    List<BankNameAlias> queryListBankNameAliasByVirtualAccount(BankNameAlias param);
    
    int saveBankNameAlias(BankNameAlias bankNameAlias);
    
    List<BankNameAlias> listPageBankNameAlias(BankNameAlias po, Page<BankNameAlias> rowBounds);
    
    BankNameAlias queryClientInfo(BankNameAlias bankNameAlias);
    
    void updateApprovedNameAlias(BankNameAlias bankNameAlias);

}
