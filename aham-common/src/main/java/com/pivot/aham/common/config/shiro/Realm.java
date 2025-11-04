package com.pivot.aham.common.config.shiro;

/**
 * 授权接口
 *
 * @author addison
 * @since 2018年11月18日
 */
public interface Realm {
    void setSessionDAO(ShiroRedisSessionDAO sessionDAO);
}