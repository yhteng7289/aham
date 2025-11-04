package com.pivot.aham.common.config;

import com.pivot.aham.common.core.util.DataUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pivot.aham.common.core.support.cache.RedissonClientImpl;
import com.pivot.aham.common.core.support.cache.RedissonHelper;
import com.pivot.aham.common.core.util.PropertiesUtil;

/**
 * Redisson配置
 *
 * @author addison
 * @since 2017年8月14日 上午10:17:29
 */
@Configuration
@ConditionalOnClass(RedissonClient.class)
public class RedissonConfig {
    @Bean
    public RedissonClient redissonClient() {
        RedissonClientImpl client = new RedissonClientImpl();
        String nodes = PropertiesUtil.getString("redis.cluster.nodes");
        String master = PropertiesUtil.getString("redis.master");
        String slaves = PropertiesUtil.getString("redis.slaves");

        String masterName = PropertiesUtil.getString("redis.mastername");
        String sentines = PropertiesUtil.getString("redis.sentines");
        boolean ifReplicated = PropertiesUtil.getBoolean("redis.ifReplicated");
        if (StringUtils.isNotBlank(nodes)) {
            client.setNodeAddresses(nodes);
        }
        else if (DataUtil.isNotEmpty(master) && DataUtil.isNotEmpty(slaves)) {
            client.setMasterAddress(master);
            client.setSlaveAddresses(slaves);
        }
        else if (DataUtil.isNotEmpty(masterName) && DataUtil.isNotEmpty(sentines)) {
            client.setSentinelMasterName(masterName);
            client.setSentinelAddresses(sentines);
        }
        else {
            String address = "redis://" + PropertiesUtil.getString("redis.host") + ":"
            + PropertiesUtil.getString("redis.port");
            client.setAddress(address);
        }
        client.setIfReplicated(ifReplicated);
        client.setPassword(PropertiesUtil.getString("redis.password"));
        client.setTimeout(PropertiesUtil.getInt("redis.timeout"));

        return client.getRedissonClient();
    }

    @Bean
    public RedissonHelper redissonHelper(RedissonClient client) {
        RedissonHelper helper = new RedissonHelper();
        helper.setRedissonClient(client);
        return helper;
    }

}
