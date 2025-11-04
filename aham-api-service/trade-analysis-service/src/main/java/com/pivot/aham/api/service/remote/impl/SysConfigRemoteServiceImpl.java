package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.api.server.dto.res.SysConfigResDTO;
import com.pivot.aham.api.server.remoteservice.SysConfigRemoteService;
import com.pivot.aham.api.service.mapper.model.SysConfigPO;
import com.pivot.aham.api.service.service.SysConfigService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author HP
 */
@Service(interfaceClass = SysConfigRemoteService.class)
@Slf4j
public class SysConfigRemoteServiceImpl implements SysConfigRemoteService {

    @Resource
    private SysConfigService sysConfigService;

    @Override
    public RpcMessage<SysConfigResDTO> getStatus(String configName) {
        SysConfigPO sysConfigPO = sysConfigService.getStatus(configName);
        SysConfigResDTO sysConfigResDTO = BeanMapperUtils.map(sysConfigPO, SysConfigResDTO.class);
        return RpcMessage.success(sysConfigResDTO);
    }
}
