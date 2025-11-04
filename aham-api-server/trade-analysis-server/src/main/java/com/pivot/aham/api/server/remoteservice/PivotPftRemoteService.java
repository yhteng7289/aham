package com.pivot.aham.api.server.remoteservice;

import com.pivot.aham.api.server.dto.req.PivotPftAccountResDTO;
import com.pivot.aham.api.server.dto.req.PivotPftAssetResDTO;
import com.pivot.aham.api.server.dto.req.PivotPftHoldingResDTO;
import com.pivot.aham.api.server.dto.req.PivotPftProductResDTO;
import com.pivot.aham.api.server.dto.res.PivotPftAssetReqDTO;
import com.pivot.aham.api.server.dto.res.PivotPftProductReqDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;
import java.util.Date;

import java.util.List;

public interface PivotPftRemoteService extends BaseRemoteService {

    RpcMessage<PivotPftAssetResDTO> updatePftAsset(PivotPftAssetReqDTO pivotPftAssetReqDTO);

    RpcMessage<PivotPftProductResDTO> getPftProductAsset(PivotPftProductReqDTO pivotPftAccountReqDTO);

    RpcMessage<List<PivotPftAccountResDTO>> getPftAccountAssets();

    RpcMessage<List<PivotPftAssetResDTO>> getPftAssets(Date date);
    
    RpcMessage<String> savePftHolding(PivotPftHoldingResDTO pivotPftHoldingResDTO);
    
    RpcMessage<List<PivotPftHoldingResDTO>> getListOfPftHolding(PivotPftHoldingResDTO pivotPftHoldingResDTO);
    
    RpcMessage<String> updatePftHolding(Long mergeOrderId);

}
