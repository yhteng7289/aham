package com.pivot.aham.common.config;

import java.util.List;

import javax.servlet.MultipartConfigElement;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.pivot.aham.common.core.filter.LocaleFilter;
import com.pivot.aham.common.core.filter.RequestWrapperFilter;
import com.pivot.aham.common.core.filter.ResponseWrapperFilter;
import com.pivot.aham.common.core.interceptor.BaseChainInterceptor;
import com.pivot.aham.common.core.interceptor.MaliciousRequestInterceptor;
import com.pivot.aham.common.core.util.DataUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.JstlView;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

import com.pivot.aham.common.config.requestmapping.ReturnValueHandlerFactory;
import com.pivot.aham.common.core.util.InstanceUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;

/**
 * mvc配置
 *
 * @author addison
 * @since 2018年11月16日
 */
@EnableWebMvc
public abstract class SpringMvcConfig implements WebMvcConfigurer {
    @Bean
    public ReturnValueHandlerFactory returnValueHandlerFactory() {
        return new ReturnValueHandlerFactory();
    }

    /**
     * 请求包装
     * @return
     */
    @Bean
    public FilterRegistrationBean<RequestWrapperFilter> requestWrapperFilterRegistration() {
        FilterRegistrationBean<RequestWrapperFilter> registration = new FilterRegistrationBean<>(
        new RequestWrapperFilter());
        registration.setName("requestWrapperFilter");
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    /**
     * 编码过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean<CharacterEncodingFilter> encodingFilterRegistration() {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        FilterRegistrationBean<CharacterEncodingFilter> registration = new FilterRegistrationBean<>(
                encodingFilter);
        registration.setName("encodingFilter");
        registration.addUrlPatterns("/*");
        registration.setAsyncSupported(true);
        registration.setOrder(2);
        return registration;
    }

    /**
     * 语言过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean<LocaleFilter> localeFilterRegistration() {
        FilterRegistrationBean<LocaleFilter> registration = new FilterRegistrationBean<>(
                new LocaleFilter());
        registration.setName("localeFilter");
        registration.addUrlPatterns("/*");
        registration.setOrder(3);
        return registration;
    }

//    /**
//     * 访问过滤器
//     * @return
//     */
//    @Bean
//    public FilterRegistrationBean<CsrfFilter> csrfFilterRegistration() {
//        FilterRegistrationBean<CsrfFilter> registration = new FilterRegistrationBean<>(new CsrfFilter());
//        registration.setName("csrfFilter");
//        registration.addUrlPatterns("/*");
//        registration.setOrder(4);
//        return registration;
//    }

    /**
     * 访问过滤器
     * @return
     */
//    @Bean
//    public FilterRegistrationBean<CorsFilter> corfFilterRegistration() {
//        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>(new CorsFilter());
//        registration.setName("corsFilter");
//        registration.addUrlPatterns("/api/v1/in/shiro/*");
//        registration.setOrder(5);
//        return registration;
//    }


    /**
     * 过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean<ResponseWrapperFilter> repsonseFilterRegistration() {
        FilterRegistrationBean<ResponseWrapperFilter> registration = new FilterRegistrationBean<>(new ResponseWrapperFilter());
        registration.setName("responseWrapperFilter");
        registration.addUrlPatterns("/*");
        registration.setOrder(5);
        return registration;
    }

    public abstract BaseChainInterceptor eventInterceptor();

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp("/WEB-INF/jsp/", ".jsp");
        registry.enableContentNegotiation(new JstlView());
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/index.html");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    /**
     * 添加converters
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //fastjson
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        List<MediaType> mediaTypes = InstanceUtil.newArrayList();
        mediaTypes.add(MediaType.valueOf("application/json;charset=UTF-8"));
        mediaTypes.add(MediaType.valueOf("application/vnd.spring-boot.actuator.v2+json"));
        fastJsonHttpMessageConverter.setSupportedMediaTypes(mediaTypes);
        //2:添加fastJson的配置信息;
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,SerializerFeature.QuoteFieldNames, SerializerFeature.WriteDateUseDateFormat,
        SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNonStringValueAsString,SerializerFeature.WriteMapNullValue,SerializerFeature.WriteNullNumberAsZero);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);


        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        List<MediaType> mediaTypesString = InstanceUtil.newArrayList();
        mediaTypesString.add(MediaType.valueOf("text/html"));
        mediaTypesString.add(MediaType.valueOf("text/plain"));
        stringHttpMessageConverter.setSupportedMediaTypes(mediaTypesString);
//
        converters.add(stringHttpMessageConverter);
        converters.add(fastJsonHttpMessageConverter);
    }

    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        MaliciousRequestInterceptor requestInterceptor = new MaliciousRequestInterceptor();
        if (DataUtil.isNotEmpty(PropertiesUtil.getInt("request.minInterval"))) {
            requestInterceptor.setMinRequestIntervalTime(PropertiesUtil.getInt("request.minInterval"));
        }
        requestInterceptor.setNextInterceptor(eventInterceptor());
        registry.addInterceptor(requestInterceptor).addPathPatterns("/**").excludePathPatterns("/*.ico", "/*/api-docs",
            "/swagger**", "/swagger-resources/**", "/webjars/**", "/configuration/**","/actuator/**","/upload**");
    }

    /**
     * 文件上传配置
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 文件最大
        factory.setMaxFileSize("1024Mb"); // KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize("1024Mb");
        return factory.createMultipartConfig();
    }


    /**
     * 允许访问的静态资源
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("upload/**").addResourceLocations("/WEB-INF/upload/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/**").addResourceLocations("/WEB-INF/", "classpath:/META-INF/resources/",
            "classpath:/resources/", "classpath:/static/", "classpath:/public/");
    }

    /**
     * 开启跨域的接口
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/*").allowedOrigins("*").allowCredentials(false)
        .allowedMethods("GET", "POST", "DELETE", "PUT")
        .allowedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Headers",
            "Access-Control-Allow-Methods", "Access-Control-Max-Age")
        .exposedHeaders("Access-Control-Allow-Origin").maxAge(3600);
    }


    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
