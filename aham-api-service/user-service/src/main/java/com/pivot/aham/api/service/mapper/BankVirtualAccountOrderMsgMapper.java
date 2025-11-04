package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.BankVirtualAccountOrderMsgPO;
import com.pivot.aham.common.core.base.BaseMapper;

import java.util.List;


/**
 * @author addison
 */
public interface BankVirtualAccountOrderMsgMapper extends BaseMapper<BankVirtualAccountOrderMsgPO> {
    int updateByMessageId(BankVirtualAccountOrderMsgPO bankVirtualAccountOrderMsgPO);
    List<BankVirtualAccountOrderMsgPO> queryBankVirtualAccountOrderMsgList(BankVirtualAccountOrderMsgPO bankVirtualAccountOrderMsgPO);

}