package com.pivot.aham.common.core.support.cache;

import java.io.Serializable;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface CacheManager {
    Logger logger = LogManager.getLogger();

    <T> T get(final String key);

    <T> Set<T> getAll(final String pattern);

    void set(final String key, final Serializable value, int seconds);

    void set(final String key, final Serializable value);

    Boolean exists(final String key);

    void del(final String key);

    void delAll(final String pattern);

    String type(final String key);

    Boolean expire(final String key, final int seconds);

    Boolean expireAt(final String key, final long unixTime);

    Long ttl(final String key);

    <T> T getSet(final String key, final Serializable value);

    boolean lock(String key, long seconds);

    boolean unlock(String key);

    void hset(String key, Serializable field, Serializable value);

    <T> T hget(String key, Serializable field);

    void hdel(String key, Serializable field);

    boolean setnx(String key, Serializable value);

    boolean setnx(String key, Serializable value,final int seconds);

    Long incr(String key);

    void setrange(String key, long offset, String value);

    String getrange(String key, long startOffset, long endOffset);

    <T> T get(String key, Integer expire);

    <T> T getFire(String key);

    <T> Set<T> getAll(String pattern, Integer expire);
}
