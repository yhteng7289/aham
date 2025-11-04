package com.pivot.aham.api.server.remoteservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.PivotCharityDetailDTO;
import com.pivot.aham.api.server.dto.req.RoundingAccountReqDTO;
import com.pivot.aham.api.server.dto.res.RoundingAccountResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;
import java.math.BigDecimal;

import java.util.List;

public interface PivotCharityDetailRemoteService extends BaseRemoteService {

    RpcMessage savePivotCharityDetail(List<PivotCharityDetailDTO> pivotCharityDetailDTOs);

    RpcMessage<BigDecimal> getTotalMoney();

    RpcMessage<Page<RoundingAccountResDTO>> getRoundingAccountPage(RoundingAccountReqDTO roundingAccountReqDTO);

}
