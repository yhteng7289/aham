package com.pivot.aham.api.server.remoteservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.PivotErrorHandlingDetailDTO;
import com.pivot.aham.api.server.dto.req.ErrorHandlingAccountReqDTO;
import com.pivot.aham.api.server.dto.res.ErrorHandlingAccountResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;
import java.math.BigDecimal;

import java.util.Date;
import java.util.List;

public interface PivotErrorDetailRemoteService extends BaseRemoteService {

    RpcMessage saveErrorHandlingDetail(List<PivotErrorHandlingDetailDTO> pivotErrorHandlingDetailDTOs);

    RpcMessage summaryErrorHandlingDetail(Date now);

    RpcMessage<BigDecimal> getTotalMoney();

    RpcMessage<Page<ErrorHandlingAccountResDTO>> getErrorHandlingPage(ErrorHandlingAccountReqDTO errorHandlingAccountReqDTO);

}
