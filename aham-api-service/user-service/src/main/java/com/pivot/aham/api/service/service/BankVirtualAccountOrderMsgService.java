package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.BankVirtualAccountOrderMsgPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;


public interface BankVirtualAccountOrderMsgService extends BaseService<BankVirtualAccountOrderMsgPO> {
    int updateByMessageId(BankVirtualAccountOrderMsgPO bankVirtualAccountOrderMsgPO);
    List<BankVirtualAccountOrderMsgPO> queryBankVirtualAccountOrderMsgList(BankVirtualAccountOrderMsgPO bankVirtualAccountOrderMsgPO);
}
