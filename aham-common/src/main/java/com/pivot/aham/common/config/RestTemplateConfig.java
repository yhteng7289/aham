package com.pivot.aham.common.config;

import com.pivot.aham.common.core.util.HttpclientUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月26日
 */
@Configuration
@ConditionalOnClass(RestTemplate.class)
public class RestTemplateConfig {
    @Bean
    public RestTemplate build(){
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setHttpClient(HttpclientUtils.getHttpBuilder("UTF-8").build());
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
//        restTemplate.setErrorHandler(new RestResponseErrorHandler());
        return restTemplate;
    }
}
