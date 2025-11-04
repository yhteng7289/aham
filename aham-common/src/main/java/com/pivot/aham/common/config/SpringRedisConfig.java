package com.pivot.aham.common.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.pivot.aham.common.core.util.DataUtil;
import com.pivot.aham.common.core.util.InstanceUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * redispool配置
 * @author addison
 * @since 2018年11月16日
 */
@Configuration
@ConditionalOnClass(RedisCacheConfiguration.class)
@Slf4j
public class SpringRedisConfig {
    @Bean
    public GenericObjectPoolConfig redisPoolConfig() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMinIdle(PropertiesUtil.getInt("redis.minIdle"));
        config.setMaxIdle(PropertiesUtil.getInt("redis.maxIdle"));
        config.setMaxTotal(PropertiesUtil.getInt("redis.maxTotal"));
        config.setMaxWaitMillis(PropertiesUtil.getInt("redis.maxWaitMillis"));
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        // Idle时进行连接扫描
        config.setTestWhileIdle(true);
        // 表示idle object evitor两次扫描之间要sleep的毫秒数
        config.setTimeBetweenEvictionRunsMillis(30000);
        // 表示idle object evitor每次扫描的最多的对象数
        config.setNumTestsPerEvictionRun(10);
        // 表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐
        // 这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
        config.setMinEvictableIdleTimeMillis(60000);
        return config;
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(ClientResources.class)
    public ClientResources clientResources() {
        return DefaultClientResources.create();
    }

    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public RedisConnectionFactory redisConnectionFactory(GenericObjectPoolConfig redisPoolConfig,
                                                         ClientResources clientResources) {
        LettuceConnectionFactory connectionFactory;
        String nodes = PropertiesUtil.getString("redis.cluster.nodes");
        String master = PropertiesUtil.getString("redis.master");
        String sentinels = PropertiesUtil.getString("redis.sentinels");
        Duration commandTimeout = Duration.ofMillis(PropertiesUtil.getInt("redis.commandTimeout", 60000));
        Duration shutdownTimeout = Duration.ofMillis(PropertiesUtil.getInt("redis.shutdownTimeout", 5000));
        LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder()
                .poolConfig(redisPoolConfig).commandTimeout(commandTimeout).shutdownTimeout(shutdownTimeout)
                .clientResources(clientResources);
        Boolean isreplicate = PropertiesUtil.getBoolean("redis.ifReplicated", false);
        if(isreplicate) {
            builder.useSsl();
        }
        LettuceClientConfiguration clientConfiguration = builder.build();
        RedisPassword password = RedisPassword.of(PropertiesUtil.getString("redis.password"));
        String host = PropertiesUtil.getString("redis.host", "localhost");
        Integer port = PropertiesUtil.getInt("redis.port", 6379);


        if(isreplicate){
            RedisStaticMasterReplicaConfiguration redisStaticMasterReplicaConfiguration = new RedisStaticMasterReplicaConfiguration(host);
            redisStaticMasterReplicaConfiguration.setPassword(password);
            connectionFactory = new LettuceConnectionFactory(redisStaticMasterReplicaConfiguration, clientConfiguration);
        }else{
            if (DataUtil.isNotEmpty(nodes)) {
                List<String> list = InstanceUtil.newArrayList(nodes.split(","));
                RedisClusterConfiguration configuration = new RedisClusterConfiguration(list);
                configuration.setMaxRedirects(PropertiesUtil.getInt("redis.cluster.max-redirects"));
                configuration.setPassword(password);
                connectionFactory = new LettuceConnectionFactory(configuration, clientConfiguration);
            } else if (DataUtil.isNotEmpty(master) && DataUtil.isNotEmpty(sentinels)) {
                Set<String> set = InstanceUtil.newHashSet(sentinels.split(","));
                RedisSentinelConfiguration configuration = new RedisSentinelConfiguration(master, set);
                configuration.setPassword(password);
                connectionFactory = new LettuceConnectionFactory(configuration, clientConfiguration);
            } else {
                RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
                configuration.setPassword(password);
                configuration.setHostName(host);
                configuration.setPort(port);
                connectionFactory = new LettuceConnectionFactory(configuration, clientConfiguration);
            }
        }


        return connectionFactory;
    }

    @Bean
    public RedisTemplate<Serializable, Serializable> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Serializable, Serializable> redisTemplate = new RedisTemplate<Serializable, Serializable>();
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericFastJsonRedisSerializer valueSerializer = new GenericFastJsonRedisSerializer();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(keySerializer);
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashKeySerializer(keySerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);
        redisTemplate.setEnableTransactionSupport(new Boolean(PropertiesUtil.getString("redis.enableTransaction")));
        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(PropertiesUtil.getInt("redis.cache.ttl", 200)));
        RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(configuration);
        if (new Boolean(PropertiesUtil.getString("redis.cache.enableTransaction"))) {
            builder.transactionAware();
        }
        RedisCacheManager cacheManager = builder.build();
        return cacheManager;
    }

//    /**
//     * 初始化cacheutil
//     * @param redisConnectionFactory
//     * @return
//     */
//    @Bean
//    public RedisHelper redisHelper(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate redisTemplate = redisTemplate(redisConnectionFactory);
//        return new RedisHelper(redisTemplate);
//    }
}
