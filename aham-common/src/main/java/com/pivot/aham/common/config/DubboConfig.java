package com.pivot.aham.common.config;

import com.pivot.aham.common.core.util.PropertiesUtil;
import org.springframework.context.annotation.Bean;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;

/**
 * dubbo配置
 *
 * @author addison
 * @since 2018年11月15日
 */
public class DubboConfig {

    @Bean
    public ApplicationConfig application() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setQosPort(PropertiesUtil.getInt("rpc.protocol.port", 22222) + 10);
        applicationConfig.setName(PropertiesUtil.getString("rpc.registry.name"));
        applicationConfig.setLogger("slf4j");
        return applicationConfig;
    }

    @Bean
    public RegistryConfig registry() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(PropertiesUtil.getString("rpc.address"));
        registryConfig.setProtocol(PropertiesUtil.getString("rpc.registry"));
        registryConfig.setGroup(PropertiesUtil.getString("env.remark"));
        //服务缓存文件，注册中心不可用是，从该文件中读取
        registryConfig.setFile(PropertiesUtil.getString("rpc.cache.dir") + "/Aham-dubbo-"
                + PropertiesUtil.getString("rpc.registry.name") + ".cache");
        return registryConfig;
    }

}
