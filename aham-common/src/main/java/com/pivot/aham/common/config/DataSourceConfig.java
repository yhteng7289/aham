//package com.pivot.aham.common.config;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//
//import javax.sql.DataSource;
//
//import DataUtil;
//import InstanceUtil;
//import PropertiesUtil;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.web.servlet.ServletRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Condition;
//import org.springframework.context.annotation.ConditionContext;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.type.AnnotatedTypeMetadata;
//
//import com.alibaba.druid.filter.Filter;
//import com.alibaba.druid.filter.stat.StatFilter;
//import com.alibaba.druid.pool.DruidDataSource;
//import com.alibaba.druid.support.http.StatViewServlet;
//import com.alibaba.druid.wall.WallConfig;
//import com.alibaba.druid.wall.WallFilter;
//import ChooseDataSource;
//
//@Configuration
//@ConditionalOnClass(DruidDataSource.class)
//public class DataSourceConfig {
//    @Bean
//    public DataSource dataSource() {
//        boolean single = isSingle();
//        DataSource write = getDataSource(false);
//        Map<Object, Object> targetDataSources = InstanceUtil.newHashMap("write", write);
//        if (!single) {
//            DataSource read = getDataSource(true);
//            targetDataSources.put("read", read);
//        }
//
//        ChooseDataSource dataSource = new ChooseDataSource();
//        dataSource.setDefaultTargetDataSource(write);
//        dataSource.setTargetDataSources(targetDataSources);
//        Map<String, String> method = InstanceUtil.newHashMap();
//        method.put("write", ",add,insert,create,updateOrInsert,delete,remove,");
//        method.put("read", ",get,select,count,list,query,");
//        dataSource.setMethodType(method);
//        return dataSource;
//    }
//
//    @Bean
//    public ServletRegistrationBean<StatViewServlet> druidServlet() {
//        ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean<StatViewServlet>();
//        servletRegistrationBean.setServlet(new StatViewServlet());
//        servletRegistrationBean.addUrlMappings("/druid/*");
//        return servletRegistrationBean;
//    }
//
//    /**
//     * 加载数据库配置
//     * @param readOnly
//     * @return
//     */
//    private DataSource getDataSource(boolean readOnly) {
//        DruidDataSource datasource = new DruidDataSource();
//        if (readOnly) {
//            datasource.setUrl(PropertiesUtil.getString("druid.reader.url"));
//            datasource.setUsername(PropertiesUtil.getString("druid.reader.username"));
//            datasource.setPassword(PropertiesUtil.getString("druid.reader.password"));
//        } else if (DataUtil.isNotEmpty(PropertiesUtil.getString("druid.writer.url"))
//                && DataUtil.isNotEmpty(PropertiesUtil.getString("druid.writer.username"))) {
//            datasource.setUrl(PropertiesUtil.getString("druid.writer.url"));
//            datasource.setUsername(PropertiesUtil.getString("druid.writer.username"));
//            datasource.setPassword(PropertiesUtil.getString("druid.writer.password"));
//        }
//        // configuration
//        Properties properties = new Properties();
//        properties.putAll(PropertiesUtil.getProperties());
//        datasource.configFromPropety(properties);
//
//        List<Filter> filters = new ArrayList<>();
//        filters.add(statFilter());
//        filters.add(wallFilter());
//        datasource.setProxyFilters(filters);
//
//        return datasource;
//    }
//
//    private StatFilter statFilter() {
//        StatFilter statFilter = new StatFilter();
//        statFilter.setLogSlowSql(true);
//        statFilter.setMergeSql(true);
//        //大于1秒为slow
//        statFilter.setSlowSqlMillis(1000);
//
//        return statFilter;
//    }
//
//    private WallFilter wallFilter() {
//        WallFilter wallFilter = new WallFilter();
//        // 允许执行多条SQL
//        WallConfig config = new WallConfig();
//        config.setMultiStatementAllow(true);
//        wallFilter.setConfig(config);
//
//        return wallFilter;
//    }
//
//    private boolean isSingle() {
//        try {
//            return DataUtil.isEmpty(PropertiesUtil.getString("druid.reader.url"));
//        } catch (Exception e) {
//            return false;
//        }
//    }
//}
