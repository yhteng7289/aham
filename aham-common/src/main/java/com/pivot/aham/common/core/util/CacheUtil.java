package com.pivot.aham.common.core.util;

import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.mapper.LockMapper;
import com.pivot.aham.common.model.Lock;
import com.pivot.aham.common.core.support.cache.CacheManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
/**
 * 缓存工具
 *
 * @author addison
 * @since 2018年11月19日
 */
public final class CacheUtil {
    private static Logger logger = LogManager.getLogger();
    private static LockMapper lockMapper;
    private static CacheManager cacheManager;
    private static CacheManager lockManager;
    private static Map<String, Thread> safeThread = InstanceUtil.newHashMap();
    private static Map<String, ReentrantLock> thread = InstanceUtil.newConcurrentHashMap();
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void setLockMapper(LockMapper lockMapper) {
        CacheUtil.lockMapper = lockMapper;
    }

    public static void setCacheManager(CacheManager cacheManager) {
        CacheUtil.cacheManager = cacheManager;
    }

    public static void setLockManager(CacheManager cacheManager) {
        CacheUtil.lockManager = cacheManager;
    }

    public static CacheManager getCache() {
        return cacheManager;
    }

    public static CacheManager getLockManager() {
        return lockManager;
    }

    /**
     * 获取锁,默认1分钟
     * @param key
     * @return
     */
    public static boolean getLock(String key) {
        return getLock(key,key);
    }

    public static boolean getLock(String key, int seconds) {
        return getLock(key, key,seconds);
    }

    /**
     * 获取锁，默认1分钟
     * @param key
     * @param name
     * @return
     */
    public static boolean getLock(String key, String name) {
        return getLock(key, name, 60);
    }

    /**
     * 锁实现
     * @param key
     * @param name
     * @param seconds
     * @return
     */
    public static boolean getLock(String key, String name, int seconds) {
        boolean success = tryLock(key, name, seconds);
        if (success) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    if (!isInterrupted()) {
                        ThreadUtil.sleep((seconds - 1) * 10);
                    }
                    while (lockManager.get(key) != null) {
                        logger.info("守护{}", key);
                        lockManager.expire(key, seconds);
                        //更新dblock
                        Lock dbLock = new Lock();
                        dbLock.setKey(key);
                        dbLock.setCreateTime(new Date());
                        lockMapper.updateById(dbLock);
                        if (!isInterrupted()) {
                            ThreadUtil.sleep(seconds * 10);
                        }
                    }
                }
            };
            thread.start();
            safeThread.put(key, thread);
        }
        return success;
    }

    /**
     * 尝试获取锁
     * @param key
     * @param name
     * @param seconds
     * @return
     */
    private static boolean tryLock(String key, String name, int seconds) {
        logger.debug("尝试获取锁 : " + key);
        try {
            //获取分布式redis缓存锁
            boolean success = lockManager.lock(key, seconds);
            //异步标记dblock
            if (success) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        getDBLock(key, name, seconds);
                    }
                });
            }
            return success;
        } catch (Exception e) {
            logger.error("从redis获取锁信息失败", e);
            //redis获取锁失败，从db获取
            return getDBLock(key, name, seconds);
        }
    }

    /**
     * 获取dblock
     * @param key
     * @param name
     * @param seconds
     * @return
     */
    private static Boolean getDBLock(String key, String name, int seconds) {
        if (!PropertiesUtil.getBoolean("dblock.open", false)) {
            return false;
        }
        try {
            if (thread.get(key) == null) {
                thread.put(key, new ReentrantLock());
            }
            //加同步锁，保证select和insert原子，防止插入多条相同key
            thread.get(key).lock();
            try {
                Lock dbLock = new Lock();
                dbLock.setKey(key);
                Lock lock = lockMapper.selectOne(dbLock);
                if (lock == null) {
                    return executorService.submit(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            logger.debug("保存锁信息" + key);
                            dbLock.setName(name);
                            dbLock.setExpireSecond(seconds);
                            return lockMapper.insert(dbLock) == 1;
                        }
                    }).get();
                }
                return false;
            } finally {
                if (thread.get(key) != null) {
                    thread.get(key).unlock();
                }
            }
        } catch (Exception e) {
            logger.error("保存锁信息失败", e);
            ThreadUtil.sleep(50);
            return getDBLock(key, name, seconds);
        }
    }

    /**
     * 解开分布式锁
     * @param key
     */
    public static void unLock(String key) {
        logger.debug("UNLOCK : " + key);
        try {
            lockManager.unlock(key);
        } catch (Exception e) {
            logger.error("从redis删除锁信息失败", e);
        }
        if (PropertiesUtil.getBoolean("dblock.open", false)) {
            deleteLock(key, 1);
        }
        safeThread.get(key).interrupt();
    }

    /**
     * 删除锁
     * @param key
     * @param times
     */
    private static void deleteLock(String key, int times) {
        boolean success = false;
        try {
            if (thread.containsKey(key)) {
                thread.get(key).lock();
                try {
                    logger.debug("从数据库删除锁信息>" + key);
                    success = executorService.submit(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            Map<String, Object> columnMap = InstanceUtil.newHashMap("key_", key);
                            return lockMapper.deleteByMap(columnMap) > 0;
                        }
                    }).get();
                } finally {
                    thread.get(key).unlock();
                }
            }
        } catch (Exception e) {
            logger.error("从数据库删除锁信息失败", e);
        }
        if (!success) {
            if (times > PropertiesUtil.getInt("deleteLock.maxTimes", 20)) {
                return;
            }
            if (thread.containsKey(key)) {
                logger.warn(key + "从数据库删除锁信息失败,稍候再次尝试...");
            }
            //随机sleep
            ThreadUtil.sleep(100, 1000);
            deleteLock(key, times + 1);
        } else {
            thread.remove(key);
        }
    }

    /**
     * 检查key
     *
     * @param key
     * @param seconds  缓存时间
     * @param frequency 最多次数
     * @param message 超出次数提示信息
     */
    public static void refreshTimes(String key, int seconds, int frequency, String message) {
        String requestId = Sequence.next().toString();
        if (getLock(key + "-LOCK", "次数限制", 10)) {
            try {
                Integer times = 1;
                String timesStr = (String)lockManager.get(key);
                if (DataUtil.isNotEmpty(timesStr)) {
                    times = Integer.valueOf(timesStr) + 1;
                    if (times > frequency) {
                        throw new BusinessException(message);
                    }
                }
                lockManager.set(key, times.toString(), seconds);
            } finally {
                unLock(key + "-LOCK");
            }
        } else {
            refreshTimes(key, seconds, frequency, message);
        }
    }
}
