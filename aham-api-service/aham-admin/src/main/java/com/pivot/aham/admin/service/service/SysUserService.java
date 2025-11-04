package com.pivot.aham.admin.service.service;

import com.pivot.aham.admin.service.mapper.model.SysUserPO;
import com.pivot.aham.common.core.base.BaseService;
/**
 * SysUser服务接口
 *
 * @author addison
 * @since 2018年11月19日
 */
public interface SysUserService extends BaseService<SysUserPO> {
    /**
     * 加载所有用户信息
     */
    void init();
}
