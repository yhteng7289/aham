package com.pivot.aham.api.server.remoteservice;

import com.pivot.aham.api.server.dto.UobExchangeCallbackDTO;
import com.pivot.aham.api.server.dto.req.ExchangeRateDTO;
import com.pivot.aham.api.server.dto.res.ExchangeRateResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月25日
 */
public interface ExchangeRemoteService extends BaseRemoteService{
    RpcMessage uobExchangeCallBack(UobExchangeCallbackDTO uobExchangeCallback);

    RpcMessage<ExchangeRateResDTO> getExchangeRate(ExchangeRateDTO exchangeRateDTO);

    RpcMessage<ExchangeRateResDTO> getLastExchangeRate(ExchangeRateDTO exchangeRateDTO);

}
