package com.pivot.aham.api.web.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import com.pivot.aham.common.config.SpringMvcConfig;
import com.pivot.aham.common.core.interceptor.EventLogInterceptor;
import com.pivot.aham.common.core.interceptor.TokenInterceptor;


@Configuration
@ComponentScan("com.pivot.aham.api.web.web")
public class WebConfig extends SpringMvcConfig {


    @Override
    @Bean
    public EventLogInterceptor eventInterceptor() {
        return new EventLogInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(new TokenInterceptor()).addPathPatterns("/**").excludePathPatterns("/*.ico",
            "/*/api-docs", "/swagger**", "/swagger-resources/**", "/webjars/**", "/configuration/**,/actuator/**");
    }
}
