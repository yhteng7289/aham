package com.pivot.aham.api.server.remoteservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.PivotFeeDetailDTO;
import com.pivot.aham.api.server.dto.req.TotalFeeAccountReqDTO;
import com.pivot.aham.api.server.dto.res.TotalFeeAccountResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;
import java.math.BigDecimal;

import java.util.List;

public interface PivotFeeDetailRemoteService extends BaseRemoteService {

    RpcMessage savePivotFeeDetail(List<PivotFeeDetailDTO> pivotFeeDetailDTOs);

    RpcMessage<BigDecimal> getTotalMoneyByFeeType(Integer feeType);

    RpcMessage<BigDecimal> getTotalMoneyByDateAndType(PivotFeeDetailDTO pivotFeeDetailDTO);
    
    RpcMessage<BigDecimal> getTotalMoneyByDateAndFeeType(PivotFeeDetailDTO pivotFeeDetailDTO);

    RpcMessage<Page<TotalFeeAccountResDTO>> getTotalFeePageByFeeType(TotalFeeAccountReqDTO totalFeeAccountReqDTO, Integer feeType);

}
