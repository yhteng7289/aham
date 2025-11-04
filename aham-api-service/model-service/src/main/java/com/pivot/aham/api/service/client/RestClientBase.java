package com.pivot.aham.api.service.client;

import com.pivot.aham.common.core.support.cache.RedissonHelper;
import com.pivot.aham.common.core.support.context.ApplicationContextHolder;

public class RestClientBase {

    protected static RedissonHelper getRedissonHelper() {
        return (RedissonHelper) ApplicationContextHolder.getBean("redissonHelper");
    }
}
