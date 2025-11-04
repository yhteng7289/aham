package com.pivot.aham.api.server.remoteservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.req.AccountBalanceAdjDetailReqDTO;
import com.pivot.aham.api.server.dto.req.AccountBalanceHisRecordReqDTO;
import com.pivot.aham.api.server.dto.req.BalanceRecordReqDTO;
import com.pivot.aham.api.server.dto.req.ReCalBuyEtfInBalReqDTO;
import com.pivot.aham.api.server.dto.res.AccountBalanceAdjDetailResDTO;
import com.pivot.aham.api.server.dto.res.AccountBalanceHisRecordResDTO;
import com.pivot.aham.api.server.dto.res.BalanceRecordResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

import java.util.List;


public interface AccountReBalanceRemoteService extends BaseRemoteService {

    RpcMessage reCalBuyEtfInBal(List<ReCalBuyEtfInBalReqDTO> reCalBuyEtfInBalReqDTOList);

    RpcMessage checkReBalance(Long accountId);

    RpcMessage<Page<BalanceRecordResDTO>> getBalanceRecords(BalanceRecordReqDTO balanceRecordReqDTO);

    RpcMessage<List<AccountBalanceAdjDetailResDTO>> getBalanceAdjDetails(AccountBalanceAdjDetailReqDTO accountBalanceAdjDetailResDTO);

    RpcMessage<Page<AccountBalanceHisRecordResDTO>> getAccBalanceHisPage(AccountBalanceHisRecordReqDTO accountBalanceHisRecordReqDTO);

}
