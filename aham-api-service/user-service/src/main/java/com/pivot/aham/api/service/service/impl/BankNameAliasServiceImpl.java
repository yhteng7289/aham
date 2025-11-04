package com.pivot.aham.api.service.service.impl;


import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.mapper.BankNameAliasMapper;
import com.pivot.aham.api.service.mapper.model.BankNameAlias;
import com.pivot.aham.api.service.service.BankNameAliasService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Created by WooiTatt
 */

@Service
public class BankNameAliasServiceImpl extends BaseServiceImpl<BankNameAlias, BankNameAliasMapper> implements BankNameAliasService {

    @Override
    public List<BankNameAlias> queryListBankNameAliasByVirtualAccount(BankNameAlias queryParam) {
        return mapper.queryListBankNameAliasByVirtualAccount(queryParam);
    }
    
    @Override
    public int saveBanNameAlias(BankNameAlias bankNameAlias) {
        return mapper.saveBankNameAlias(bankNameAlias);
    }
    
    @Override
    public Page<BankNameAlias> listPageBankNameAlias(BankNameAlias po, Page<BankNameAlias> rowBounds) {
        List<BankNameAlias> bankNameAliasList = mapper.listPageBankNameAlias(po,rowBounds);
        rowBounds.setRecords(bankNameAliasList);
        return rowBounds;
    }
    
    @Override
    public BankNameAlias queryClientInfo(BankNameAlias bankNameAlias) {
        return mapper.queryClientInfo(bankNameAlias);
    }
    
    @Override
    public void updateApprovedNameAlias(BankNameAlias bankNameAlias) {
        mapper.updateApprovedNameAlias(bankNameAlias);
    }


}
