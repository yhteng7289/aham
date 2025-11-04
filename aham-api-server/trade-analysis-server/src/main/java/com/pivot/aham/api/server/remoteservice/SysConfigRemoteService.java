package com.pivot.aham.api.server.remoteservice;

import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

/**
 *
 * @author HP
 */
public interface SysConfigRemoteService extends BaseRemoteService {

    RpcMessage getStatus(String configName);
}
