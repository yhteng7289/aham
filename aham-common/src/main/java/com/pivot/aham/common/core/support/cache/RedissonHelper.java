package com.pivot.aham.common.core.support.cache;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.pivot.aham.common.core.util.CacheUtil;
import com.pivot.aham.common.core.util.InstanceUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RType;

import org.redisson.api.RedissonClient;

/**
 *
 * Redis缓存辅助类
 *
 */
public class RedissonHelper implements CacheManager {

    private org.redisson.api.RedissonClient redissonClient;
    private final Integer EXPIRE = PropertiesUtil.getInt("redis.expiration");

    public void setClient(RedissonClientImpl Client) {
        redissonClient = Client.getRedissonClient();
        CacheUtil.setLockManager(this);
        CacheUtil.setCacheManager(this);
    }

    public void setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        CacheUtil.setLockManager(this);
        CacheUtil.setCacheManager(this);
    }

    private <T> RBucket<T> getRedisBucket(String key) {
        return redissonClient.getBucket(key);
    }

    @Override
    public final <T> T get(final String key) {
        RBucket<T> temp = getRedisBucket(key);
        return temp.get();
    }

    @Override
    public <T> T get(String key, Integer expire) {
        RBucket<T> temp = getRedisBucket(key);
        expire(temp, expire);
        return temp.get();
    }

    @Override
    public <T> T getFire(String key) {
        RBucket<T> temp = getRedisBucket(key);
        expire(temp, EXPIRE);
        return temp.get();
    }

    @Override
    public final void set(final String key, final Serializable value) {
        RBucket<Object> temp = getRedisBucket(key);
        temp.set(value);
        expire(temp, EXPIRE);
    }

    @Override
    public final void set(final String key, final Serializable value, int seconds) {
        RBucket<Object> temp = getRedisBucket(key);
        temp.set(value);
        expire(temp, seconds);
    }

    public final void multiSet(final Map<String, Object> temps) {
        redissonClient.getBuckets().set(temps);
    }

    @Override
    public final Boolean exists(final String key) {
        RBucket<Object> temp = getRedisBucket(key);
        return temp.isExists();
    }

    @Override
    public final void del(final String key) {
        redissonClient.getKeys().delete(key);
    }

    @Override
    public final void delAll(final String pattern) {
        redissonClient.getKeys().deleteByPattern(pattern);
    }

    @Override
    public final String type(final String key) {
        RType type = redissonClient.getKeys().getType(key);
        if (type == null) {
            return null;
        }
        return type.getClass().getName();
    }

    /**
     * 在某段时间后失效
     *
     * @return
     */
    private final <T> void expire(final RBucket<T> bucket, final int seconds) {
        bucket.expire(seconds, TimeUnit.SECONDS);
    }

    /**
     * 在某个时间点失效
     *
     * @param key
     * @param unixTime
     * @return
     *
     */
    @Override
    public final Boolean expireAt(final String key, final long unixTime) {
        return redissonClient.getBucket(key).expireAt(new Date(unixTime));
    }

    @Override
    public final Long ttl(final String key) {
        RBucket<Object> rBucket = getRedisBucket(key);
        return rBucket.remainTimeToLive();
    }

    @Override
    public final <T> T getSet(final String key, final Serializable value) {
        RBucket<T> rBucket = getRedisBucket(key);
        return rBucket.getAndSet((T) value);
    }

    @Override
    public <T> Set<T> getAll(String pattern) {
        Set<T> set = InstanceUtil.newHashSet();
        Iterable<String> keys = redissonClient.getKeys().getKeysByPattern(pattern);
        for (String key : keys) {
            set.add((T) getRedisBucket(key).get());
        }
        return set;
    }

    @Override
    public <T> Set<T> getAll(String pattern, Integer expire) {
        Set<T> set = InstanceUtil.newHashSet();
        Iterable<String> keys = redissonClient.getKeys().getKeysByPattern(pattern);
        for (String key : keys) {
            RBucket<T> bucket = getRedisBucket(key);
            expire(bucket, expire);
            set.add(bucket.get());
        }
        return set;
    }

    @Override
    public Boolean expire(String key, int seconds) {
        RBucket<Object> bucket = getRedisBucket(key);
        expire(bucket, seconds);
        return true;
    }

    @Override
    public void hset(String key, Serializable field, Serializable value) {
        redissonClient.getMap(key).put(field, value);
    }

    @Override
    public Object hget(String key, Serializable field) {
        return redissonClient.getMap(key).get(field);
    }

    @Override
    public void hdel(String key, Serializable field) {
        redissonClient.getMap(key).remove(field);
    }

    public void sadd(String key, Serializable value) {
        redissonClient.getSet(key).add(value);
    }

    public Set<Object> sall(String key) {
        return redissonClient.getSet(key).readAll();
    }

    public boolean sdel(String key, Serializable value) {
        return redissonClient.getSet(key).remove(value);
    }

    @Override
    public boolean lock(String key, long seconds) {
        RLock rLock = redissonClient.getLock(key);
        rLock.lock(seconds, TimeUnit.SECONDS);
//        return redissonClient.getBucket(key).trySet(requestId, seconds, TimeUnit.SECONDS);
        return rLock.isLocked();
    }

    @Override
    public boolean unlock(String key) {
//        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//        return redissonClient.getScript().eval(Mode.READ_WRITE, script, ReturnType.BOOLEAN,
//            InstanceUtil.newArrayList(key), requestId);
        RLock rLock = redissonClient.getLock(key);
        rLock.unlock();
        return !rLock.isLocked();
    }

    @Override
    public boolean setnx(String key, Serializable value) {
        return redissonClient.getBucket(key).trySet(value);
    }

    @Override
    public boolean setnx(String key, Serializable value, final int seconds) {
        boolean res = redissonClient.getBucket(key).trySet(value);
        if (res) {
            expire(key, seconds);
        }
        return res;
    }

    @Override
    public Long incr(String key) {
        return redissonClient.getAtomicLong(key).incrementAndGet();
    }

    @Override
    public void setrange(String key, long offset, String value) {
    }

    @Override
    public String getrange(String key, long startOffset, long endOffset) {
        return null;
    }
}
