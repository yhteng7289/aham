package com.pivot.aham.common.config;

import java.io.IOException;
import java.util.Map;

import com.pivot.aham.common.config.shiro.*;
import com.pivot.aham.common.core.listener.SessionListener;
import com.pivot.aham.common.core.support.context.OrderedProperties;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.util.ResourceUtils;

import com.google.common.collect.Maps;

import com.pivot.aham.common.core.filter.SessionFilter;
import com.pivot.aham.common.core.util.InstanceUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;

/**
 * shiro配置
 *
 * @author addison
 * @since 2018年11月16日
 */
@Configuration
@Conditional(ShiroStrategyConfig.EnableShiroService.class)
@ConditionalOnBean(Realm.class)
@ConditionalOnClass(RememberMeManager.class)
@EnableAutoConfiguration(exclude = RedisAutoConfiguration.class)
public class ShiroConfig {
    @Bean
    public SessionListener sessionListener() {
        return new SessionListener();
    }

    /**
     * session管理器
     * @param realm
     * @return
     */
    @Bean
    public SessionDAO sessionDao(Realm realm) {
        ShiroRedisSessionDAO dao = new ShiroRedisSessionDAO();
        realm.setSessionDAO(dao);
        return dao;
    }

    @Bean
    public DefaultWebSecurityManager securityManager(AuthorizingRealm realm, SessionManager sessionManager,
        RememberMeManager rememberMeManager) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(realm);
        //缓存管理
        manager.setCacheManager(new ShiroRedisCacheManager());
        manager.setSessionManager(sessionManager);
        manager.setRememberMeManager(rememberMeManager);
        return manager;
    }

    @Bean
    public SessionManager sessionManager(SessionDAO sessionDao, SessionListener sessionListener, Cookie cookie) {
        //session管理
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(sessionDao);
        sessionManager.getSessionListeners().add(sessionListener);
        sessionManager.setSessionIdCookie(cookie);
        sessionManager.setGlobalSessionTimeout(1000 * PropertiesUtil.getLong("session.timeout", 60 * 30));
        return sessionManager;
    }

    @Bean
    public Cookie cookie() {
        SimpleCookie cookie = new SimpleCookie("SQUIRRELSAVESESSIONID");
        cookie.setSecure(PropertiesUtil.getBoolean("session.cookie.secure", false));
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        return cookie;
    }

    /**
     * 记住我
     * @return
     */
    @Bean
    public RememberMeManager rememberMeManager() {
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        String cipherKey = PropertiesUtil.getString("rememberMe.cookie.cipherKey", "HeUZ/LvgkO7nsa18ZyVxWQ==");
        rememberMeManager.setCipherKey(Base64.decode(cipherKey));
        rememberMeManager.getCookie().setMaxAge(PropertiesUtil.getInt("rememberMe.cookie.maxAge", 60 * 60 * 24 * 7));
        return rememberMeManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) throws IOException {
        ShiroFilterFactoryBean factory = new ShiroFilterFactoryBean();
        factory.setSecurityManager(securityManager);
        //未登录地址
//        factory.setLoginUrl("/api/v1/in/shiro/unauthorized");
        //未授权地址
//        factory.setUnauthorizedUrl("/api/v1/in/shiro/forbidden");
        Map<String, String> filterMap = InstanceUtil.newLinkedHashMap();
        //权限配置表
        OrderedProperties properties = new OrderedProperties(
            ResourceUtils.getFile("classpath:config/shiro.properties"));
        filterMap.putAll(Maps.fromProperties(properties));

        factory.setFilterChainDefinitionMap(filterMap);
        factory.getFilters().put("session", new SessionFilter());
        factory.getFilters().put("user", new ShiroUserFilter());
        factory.getFilters().put("perms", new ShiroPermissionsFilter());
        return factory;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
