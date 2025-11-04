package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.VirtualBankAccountPO;
import com.pivot.aham.common.core.base.BaseMapper;

/**
 * Created by dexter on 11/4/2020
 */
public interface VirtualBankAccountMapper extends BaseMapper<VirtualBankAccountPO> {

    VirtualBankAccountPO queryBankVirtualAccountById(VirtualBankAccountPO queryParam);

}
