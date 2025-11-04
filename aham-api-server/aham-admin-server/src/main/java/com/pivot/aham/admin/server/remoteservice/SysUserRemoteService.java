package com.pivot.aham.admin.server.remoteservice;

import com.pivot.aham.admin.server.BaseAdminService;
import com.pivot.aham.admin.server.dto.SysUserDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;

/**
 * SysUser服务接口
 *
 * @author addison
 * @since 2018年11月19日
 */
public interface SysUserRemoteService extends BaseRemoteService, BaseAdminService<SysUserDTO> {
    /**
     * 加载所有用户信息
     */
    void init();
}
