package com.pivot.aham.common.config.shiro;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.pivot.aham.common.core.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

/**
 * shiro的rediscache
 *
 * @author addison
 * @since 2018年11月16日
 */
public class ShiroRedisCacheManager implements CacheManager {
    private final Logger logger = LogManager.getLogger();

    /**
     * cache实例缓存
     */
    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>();
    /**
     * The Redis key prefix for caches 
     */
    private String keyPrefix = Constants.REDIS_SHIRO_CACHE;

    /**
     * Returns the Redis session keys
     * prefix.
     * @return The prefix
     */
    public String getKeyPrefix() {
        return keyPrefix;
    }

    /**
     * Sets the Redis sessions key 
     * prefix.
     * @param keyPrefix The prefix
     */
    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        logger.debug("获取名称为: " + name + " 的RedisCache实例");

        //线程缓存中获取
        Cache c = caches.get(name);

        if (c == null) {
            //创建cahe实例
            ShiroRedisCache cache = new ShiroRedisCache<K, V>(keyPrefix);
            //加入缓存
            caches.put(name, cache);
        }
        return c;
    }

}
