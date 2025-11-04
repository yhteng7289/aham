package com.pivot.aham.api.server.remoteservice;

import com.pivot.aham.api.server.dto.BeforeGameTestDTO;
import com.pivot.aham.api.server.dto.VersionInfoDTO;
import com.pivot.aham.api.server.dto.VersionInfoResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

public interface UserTestRemoteService extends BaseRemoteService {

    RpcMessage<String> getPortfolioId(BeforeGameTestDTO beforeGameTestDTO);

    RpcMessage<VersionInfoResDTO> getVersionInfo(VersionInfoDTO versionInfoDTO);
}
