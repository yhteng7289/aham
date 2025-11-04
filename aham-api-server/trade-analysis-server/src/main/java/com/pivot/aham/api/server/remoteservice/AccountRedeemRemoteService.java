package com.pivot.aham.api.server.remoteservice;

import com.pivot.aham.api.server.dto.AccountRedeemVoDTO;
import com.pivot.aham.api.server.dto.req.AccountetfSharesReqDTO;
import com.pivot.aham.api.server.dto.res.AccountetfSharesResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;
import java.math.BigDecimal;

import java.util.List;

public interface AccountRedeemRemoteService extends BaseRemoteService {

   RpcMessage<BigDecimal> getSumRedeemConfirmAmount();
   
   RpcMessage<BigDecimal> getSumRedeemConfirmAmtByGoalClient(AccountRedeemVoDTO accountRedeemVoDTO);
   
}
