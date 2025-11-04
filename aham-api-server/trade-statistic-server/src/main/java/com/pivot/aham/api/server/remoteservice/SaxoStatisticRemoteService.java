package com.pivot.aham.api.server.remoteservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.SaxoOpenPositionReqDTO;
import com.pivot.aham.api.server.dto.SaxoOpenPositionResDTO;
import com.pivot.aham.api.server.dto.SaxoReconBalanceReqDTO;
import com.pivot.aham.api.server.dto.SaxoReconBalanceResDTO;
import com.pivot.aham.api.server.dto.SaxoShareOpenPositionReqDTO;
import com.pivot.aham.api.server.dto.SaxoShareOpenPositionResDTO;
import com.pivot.aham.api.server.dto.SaxoShareTradeReqDTO;
import com.pivot.aham.api.server.dto.SaxoShareTradeResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

import java.util.Date;
import java.util.List;

public interface SaxoStatisticRemoteService extends BaseRemoteService {
    void dividend(Date nowDate);
    void totalStatisEnd(Date date);
    void recordBookkeepingCash();
    void statisShareTrades();
    void statisShareOpenPositions();
    void recordCashTransactions();
    void balanceOfAccount();
    
    //added by WooiTatt
    RpcMessage<Page<SaxoReconBalanceResDTO>> saxoReconBalance(SaxoReconBalanceReqDTO saxoReconBalanceReqDTO);
    
    RpcMessage<Page<SaxoOpenPositionResDTO>> saxoOpenPositionStock(SaxoOpenPositionReqDTO saxoOpenPositionReqDTO);
    
    RpcMessage<Page<SaxoShareTradeResDTO>> saxoShareTrade(SaxoShareTradeReqDTO saxoShareTradeReqDTO);
    
    RpcMessage<Page<SaxoShareOpenPositionResDTO>> saxoShareOpenPosition(SaxoShareOpenPositionReqDTO saxoShareOpenPositionReqDTO);

    RpcMessage<List<SaxoShareTradeResDTO>> saxoShareTradeRemoteExport(SaxoShareTradeReqDTO saxoShareTradeReqDTO);
    
    RpcMessage<List<SaxoReconBalanceResDTO>> saxoReconBalanceRemoteExport(SaxoReconBalanceReqDTO saxoReconBalanceReqDTO);
    
    RpcMessage<List<SaxoOpenPositionResDTO>> saxoOpenPositionRemoteExport(SaxoOpenPositionReqDTO saxoOpenPositionReqDTO);
    
    RpcMessage<List<SaxoShareOpenPositionResDTO>> saxoShareOpenPositionRemoteExport(SaxoShareOpenPositionReqDTO saxoShareOpenPositionReqDTO);

//UBO
    void statisExport();
}
