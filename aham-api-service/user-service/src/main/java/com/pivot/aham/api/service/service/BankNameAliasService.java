package com.pivot.aham.api.service.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.mapper.model.BankNameAlias;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;

/**
 * Created by WooiTatt
 */
public interface BankNameAliasService extends BaseService<BankNameAlias> {

    List <BankNameAlias> queryListBankNameAliasByVirtualAccount (BankNameAlias queryParam);
    
    int saveBanNameAlias(BankNameAlias bankNameAlias);
    
    Page<BankNameAlias> listPageBankNameAlias(BankNameAlias po, Page<BankNameAlias> rowBounds);
    
    BankNameAlias queryClientInfo(BankNameAlias bankNameAlias);
    
    void updateApprovedNameAlias(BankNameAlias bankNameAlias);
    
}
