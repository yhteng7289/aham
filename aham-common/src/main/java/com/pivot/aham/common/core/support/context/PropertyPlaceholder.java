package com.pivot.aham.common.core.support.context;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.pivot.aham.common.core.util.PropertiesUtil;

/**
 * 手工加载配置
 *
 * @author addison
 * @since 2018年11月18日
 */
public class PropertyPlaceholder extends PropertyPlaceholderConfigurer implements ApplicationContextAware {
    private List<String> decryptProperties;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.applicationContext = applicationContext;
    }

    @Override
    protected void loadProperties(Properties props) throws IOException {
        super.loadProperties(props);
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
            PropertiesUtil.getProperties().put(keyStr, value);
        }
        logger.info("loadProperties ok.");
    }

    /**
     * @param decryptProperties
     *            the decryptPropertiesMap to set
     */
    public void setDecryptProperties(List<String> decryptProperties) {
        this.decryptProperties = decryptProperties;
    }


}
