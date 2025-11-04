package com.pivot.aham.api.server.remoteservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.req.FundingStatusReqDTO;
import com.pivot.aham.api.server.dto.res.FundingStatusResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

/**
 *
 * @author bjoon
 */
public interface FundingStatusRemoteService extends BaseRemoteService{
    RpcMessage<Page<FundingStatusResDTO>> getFundingStatusPage(FundingStatusReqDTO fundingStatusReqDTO);
}
