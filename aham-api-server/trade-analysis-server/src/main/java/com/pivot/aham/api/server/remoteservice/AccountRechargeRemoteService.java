package com.pivot.aham.api.server.remoteservice;

import com.pivot.aham.api.server.dto.AccountRechargeVoDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;
import java.math.BigDecimal;

import java.util.List;

public interface AccountRechargeRemoteService extends BaseRemoteService {

   RpcMessage<BigDecimal> getSumAccountRecharge();
   
   RpcMessage<BigDecimal> getSumAccRechargeByGoalClient(AccountRechargeVoDTO accountRechargeVoDTO);

}
