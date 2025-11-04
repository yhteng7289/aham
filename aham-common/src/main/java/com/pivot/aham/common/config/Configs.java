package com.pivot.aham.common.config;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.common.core.util.DataUtil;
import com.pivot.aham.common.core.util.InstanceUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * properties处理到缓存，第一顺序加载
 *
 * @author addison
 * @since 2018年11月15日
 */
@Configuration
@Slf4j
public class Configs implements EnvironmentPostProcessor, Ordered {
    public static final String ACTIVE_PROFILE = "activeProfile";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        MutablePropertySources propertySources = environment.getPropertySources();
        String[] profiles = environment.getActiveProfiles();
        log.info("profiles,environment:{}", JSON.toJSON(profiles), JSON.toJSON(environment));
        Properties props = getConfig(profiles);
        propertySources.addLast(new PropertiesPropertySource("thirdEnv", props));
        for (PropertySource<?> propertySource : propertySources) {
            if (propertySource.getSource() instanceof Map) {
                Map map = (Map)propertySource.getSource();
                for (Object key : map.keySet()) {
                    String keyStr = key.toString();
                    Object value = map.get(key);
                    //配置放入内存
                    PropertiesUtil.getProperties().put(keyStr, value.toString());
                }
            }
        }
        log.info("=====读取配置完成==========");
    }

    @Override
    public int getOrder() {
        return ConfigFileApplicationListener.DEFAULT_ORDER + 1;
    }


    /**
     * 加载所有properties,包括jar中的
     * @param profiles
     * @return
     */
    private Properties getConfig(String[] profiles) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<Resource> resouceList = InstanceUtil.newArrayList();
        //先加载不区分环境的配置
        addResources(resolver, resouceList, "classpath*:config/*.properties");
        //再加载特定环境的配置
        if (profiles != null) {
            for (String p : profiles) {
                if (DataUtil.isNotEmpty(p)) {
                    PropertiesUtil.getProperties().put(ACTIVE_PROFILE,p);
                    p = p + "/";
                }
                log.info("加载对应环境配置:{}",p);

                addResources(resolver, resouceList, "classpath*:config/" + p + "*.properties");
            }
        }
        try {
            PropertiesFactoryBean config = new PropertiesFactoryBean();
            config.setLocations(resouceList.toArray(new Resource[]{}));
            config.afterPropertiesSet();
            return config.getObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addResources(PathMatchingResourcePatternResolver resolver, List<Resource> resouceList, String path) {
        try {
            Resource[] resources = resolver.getResources(path);
            for (Resource resource : resources) {
                resouceList.add(resource);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
