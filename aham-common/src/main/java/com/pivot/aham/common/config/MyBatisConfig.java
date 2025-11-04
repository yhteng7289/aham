package com.pivot.aham.common.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.baomidou.mybatisplus.MybatisConfiguration;
import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.mapper.AutoSqlInjector;
import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean;
import com.pivot.aham.common.core.support.datasource.ChooseDataSource;
import com.pivot.aham.common.core.util.CacheUtil;
import com.pivot.aham.common.core.util.DataUtil;
import com.pivot.aham.common.core.util.InstanceUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;
import com.pivot.aham.common.mapper.LockMapper;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * mybatis配置
 *
 * @author addison
 * @since 2018年11月15日
 */
@Configuration
@ConditionalOnClass(value = {MapperScannerConfigurer.class, DataSourceTransactionManager.class})
@EnableTransactionManagement(proxyTargetClass = true)
@EnableScheduling
public class MyBatisConfig{
    @Bean
    public DataSource dataSource() {
        boolean single = isSingle();
        DataSource write = getDataSource(false);
        Map<Object, Object> targetDataSources = InstanceUtil.newHashMap("write", write);
        if (!single) {
            DataSource read = getDataSource(true);
            targetDataSources.put("read", read);
        }

        ChooseDataSource dataSource = new ChooseDataSource();
        dataSource.setDefaultTargetDataSource(write);
        dataSource.setTargetDataSources(targetDataSources);
        Map<String, String> method = InstanceUtil.newHashMap();
        method.put("write", ",add,insert,create,updateOrInsert,delete,remove,");
        method.put("read", ",get,select,count,list,query,");
        dataSource.setMethodType(method);
        return dataSource;
    }

    @Bean
    public ServletRegistrationBean<StatViewServlet> druidServlet() {
        ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean<StatViewServlet>();
        servletRegistrationBean.setServlet(new StatViewServlet());
        servletRegistrationBean.addUrlMappings("/druid/*");
        return servletRegistrationBean;
    }

    /**
     * 加载数据库配置
     * @param readOnly
     * @return
     */
    private DataSource getDataSource(boolean readOnly) {
        DruidDataSource datasource = new DruidDataSource();
        if (readOnly) {
            datasource.setUrl(PropertiesUtil.getString("druid.reader.url"));
            datasource.setUsername(PropertiesUtil.getString("druid.reader.username"));
            datasource.setPassword(PropertiesUtil.getString("druid.reader.password"));
        } else if (DataUtil.isNotEmpty(PropertiesUtil.getString("druid.writer.url"))
        && DataUtil.isNotEmpty(PropertiesUtil.getString("druid.writer.username"))) {
            datasource.setUrl(PropertiesUtil.getString("druid.writer.url"));
            datasource.setUsername(PropertiesUtil.getString("druid.writer.username"));
            datasource.setPassword(PropertiesUtil.getString("druid.writer.password"));
        }
        // configuration
        Properties properties = new Properties();
        properties.putAll(PropertiesUtil.getProperties());
        datasource.configFromPropety(properties);

        List<Filter> filters = new ArrayList<>();
        filters.add(statFilter());
        filters.add(wallFilter());
        datasource.setProxyFilters(filters);

        return datasource;
    }

    private StatFilter statFilter() {
        StatFilter statFilter = new StatFilter();
        statFilter.setLogSlowSql(true);
        statFilter.setMergeSql(true);
        //大于1秒为slow
        statFilter.setSlowSqlMillis(1000);

        return statFilter;
    }

    private WallFilter wallFilter() {
        WallFilter wallFilter = new WallFilter();
        // 允许执行多条SQL
        WallConfig config = new WallConfig();
        config.setMultiStatementAllow(true);
        wallFilter.setConfig(config);

        return wallFilter;
    }

    private boolean isSingle() {
        try {
            return DataUtil.isEmpty(PropertiesUtil.getString("druid.reader.url"));
        } catch (Exception e) {
            return false;
        }
    }



    private final Logger logger = LogManager.getLogger();

    private LockMapper lockMapper;

    private String get(String key) throws IOException {
        String value = PropertiesUtil.getString(key);
        return value;
    }


    /**
     * SqlSessionFactory
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean(name = "sqlSessionFactory")
    @ConditionalOnBean(DataSource.class)
    public MybatisSqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources(get("mybatis.mapperLocations")));
        sessionFactory.setTypeAliasesPackage(get("mybatis.typeAliasesPackage"));
        PaginationInterceptor page = new PaginationInterceptor();
        page.setDialectType(get("mybatis.dialectType"));
        sessionFactory.setPlugins(new Interceptor[]{page});
        sessionFactory.setTypeEnumsPackage(get("mybatis.locationConfig"));
        sessionFactory.setTypeHandlersPackage(get("type-handlers-package"));
//        sessionFactory.setTypeHandlers();

        MybatisConfiguration configuration = new MybatisConfiguration();
        if("dev".equals(get("activeProfile"))){
            configuration.setLogImpl(StdOutImpl.class);
        }else {
            configuration.setLogImpl(Slf4jImpl.class);
        }
        configuration.setCallSettersOnNulls(true);
        sessionFactory.setConfiguration(configuration);

        String idType = get("mybatis.idType");
        GlobalConfiguration config = new GlobalConfiguration();
        config.setDbColumnUnderline(true);
        config.setSqlInjector(new AutoSqlInjector());
        if (DataUtil.isEmpty(idType)) {
            config.setIdType(IdType.AUTO.getKey());
        } else {
            config.setIdType(IdType.valueOf(idType).getKey());
        }
        sessionFactory.setGlobalConfig(config);
        return sessionFactory;
    }

    @Bean
    public MapperScannerConfigurer configurer() throws IOException {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        configurer.setBasePackage(get("mybatis.mapperBasePackage"));
        return configurer;
    }

    @Bean
    @ConditionalOnBean(DataSource.class)
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 数据库实现的分布式锁
     * @param lockMapper
     * @return
     */
    @Bean
    public Object setLockMapper(LockMapper lockMapper) {
        this.lockMapper = lockMapper;
        CacheUtil.setLockMapper(lockMapper);
        return lockMapper;
    }

    /** 定时清除锁信息,一分钟*/
    @Scheduled(cron = "0 0/1 * * * *")
    public void cleanExpiredLock() {
        if (lockMapper != null) {
            logger.info("cleanExpiredLock");
            lockMapper.cleanExpiredLock();
        }
    }



}
