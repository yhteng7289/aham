package com.pivot.aham.api.server.remoteservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.DividendCallBackDTO;
import com.pivot.aham.api.server.dto.req.UserDividendReqDTO;
import com.pivot.aham.api.server.dto.res.UserDividendResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

import java.math.BigDecimal;
import java.util.Date;


public interface DividendRemoteService extends BaseRemoteService {
    RpcMessage<String> dividendCallBack(DividendCallBackDTO dividendCallBackDTO);
    RpcMessage<BigDecimal> getDividendMoney(Date date);
    
    RpcMessage<Page<UserDividendResDTO>> getUserDividendPage(UserDividendReqDTO userDividendReqDTO);
}
