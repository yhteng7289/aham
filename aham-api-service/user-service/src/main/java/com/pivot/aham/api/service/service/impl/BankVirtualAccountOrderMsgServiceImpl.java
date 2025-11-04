package com.pivot.aham.api.service.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.api.service.mapper.BankVirtualAccountOrderMsgMapper;
import com.pivot.aham.api.service.mapper.model.BankVirtualAccountOrderMsgPO;
import com.pivot.aham.api.service.service.BankVirtualAccountOrderMsgService;
import com.pivot.aham.common.core.base.BaseServiceImpl;

import java.util.List;


//@CacheConfig(cacheNames = "member")
@Service
public class BankVirtualAccountOrderMsgServiceImpl extends BaseServiceImpl<BankVirtualAccountOrderMsgPO, BankVirtualAccountOrderMsgMapper> implements BankVirtualAccountOrderMsgService {

    @Override
    public int updateByMessageId(BankVirtualAccountOrderMsgPO bankVirtualAccountOrderMsgPO) {
        return mapper.updateByMessageId(bankVirtualAccountOrderMsgPO);
    }

    @Override
    public List<BankVirtualAccountOrderMsgPO> queryBankVirtualAccountOrderMsgList(BankVirtualAccountOrderMsgPO bankVirtualAccountOrderMsgPO) {
        return mapper.queryBankVirtualAccountOrderMsgList(bankVirtualAccountOrderMsgPO);
    }
}
