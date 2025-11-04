package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.VirtualBankAccountMapper;
import com.pivot.aham.api.service.mapper.model.VirtualBankAccountPO;
import com.pivot.aham.api.service.service.VirtualBankAccountService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;


/**
 * Created by dexter on 11/4/2020
 * 
 */

@Service
public class VirtualBankAccountServiceImpl extends BaseServiceImpl<VirtualBankAccountPO, VirtualBankAccountMapper> implements VirtualBankAccountService {

    @Override
    public VirtualBankAccountPO queryBankVirtualAccountById(VirtualBankAccountPO queryParam) {
        return mapper.queryBankVirtualAccountById(queryParam);
    }
    

}
