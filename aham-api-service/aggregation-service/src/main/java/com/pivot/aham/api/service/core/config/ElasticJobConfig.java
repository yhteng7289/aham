package com.pivot.aham.api.service.core.config;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.pivot.aham.common.core.elasticjob.ElasticJobConfParser;
import com.pivot.aham.common.core.util.PropertiesUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 任务自动配置
 */
@Configuration
@Profile({"local", "dev", "test", "prod", "prod2"})
public class ElasticJobConfig {

    /**
     * 初始化Zookeeper注册中心
     *
     * @return
     */
    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter zookeeperRegistryCenter() {
        ZookeeperConfiguration zkConfig = new ZookeeperConfiguration(PropertiesUtil.getString("elastic.job.zk.serverLists"),
                PropertiesUtil.getString("elastic.job.zk.namespace"));
        zkConfig.setBaseSleepTimeMilliseconds(PropertiesUtil.getInt("elastic.job.zk.baseSleepTimeMilliseconds"));
        zkConfig.setConnectionTimeoutMilliseconds(PropertiesUtil.getInt("elastic.job.zk.connectionTimeoutMilliseconds"));
//		zkConfig.setDigest(zookeeperProperties.getDigest());
        zkConfig.setMaxRetries(PropertiesUtil.getInt("elastic.job.zk.maxRetries"));
        zkConfig.setMaxSleepTimeMilliseconds(PropertiesUtil.getInt("elastic.job.zk.maxSleepTimeMilliseconds"));
        zkConfig.setSessionTimeoutMilliseconds(PropertiesUtil.getInt("elastic.job.zk.sessionTimeoutMilliseconds"));
        return new ZookeeperRegistryCenter(zkConfig);
    }

    @Bean
    public ElasticJobConfParser jobConfParser() {
        return new ElasticJobConfParser();
    }

}
