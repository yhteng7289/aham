package com.pivot.aham.api.server.remoteservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.req.*;
import com.pivot.aham.api.server.dto.res.*;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

import java.util.List;

public interface TransRemoteService extends BaseRemoteService {
    RpcMessage<List<AccountRechargeResDTO>> getAccountRecharges(AccountRechargeReqDTO accountRechargeReqDTO);

    RpcMessage<List<AccountRedeemResDTO>> getAccountRedeems(AccountRedeemReqDTO accountRedeemReqDTO);

    RpcMessage<List<UserDividendResDTO>> getUserDividends(UserDividendReqDTO userDividendReqDTO);

    RpcMessage<Page<TransOrderResDTO>> getTransOrders(TransOrderReqDTO transOrderReqDTO);
    RpcMessage<List<TransOrderResDTO>> getTransOrdersList(TransOrderReqDTO transOrderReqDTO);

    RpcMessage<Page<TmpOrderRecordResDTO>> getTmpOrders(TmpOrderRecordReqDTO tmpOrderRecordReqDTO);

    RpcMessage<Page<AccountDividendResDTO>> getAccountDividend(AccountDividendReqDTO accountDividendReqDTO);

}
