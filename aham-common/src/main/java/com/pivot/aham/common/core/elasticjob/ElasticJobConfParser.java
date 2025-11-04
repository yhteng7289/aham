package com.pivot.aham.common.core.elasticjob;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.executor.handler.JobProperties.JobPropertiesEnum;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.pivot.aham.common.core.elasticjob.check.MessageSend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Job解析类
 *
 * <p>
 * 从注解中解析任务信息初始化<p>
 */
public class ElasticJobConfParser implements ApplicationContextAware {

    private Logger logger = LoggerFactory.getLogger(ElasticJobConfParser.class);
    @Autowired
    private ZookeeperRegistryCenter zookeeperRegistryCenter;
    @Autowired
    private MessageSend messageSend;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        //从context获取有注解的所有bean
        Map<String, Object> beanMap = ctx.getBeansWithAnnotation(ElasticJobConf.class);
        for (Object confBean : beanMap.values()) {
            Class<?> clz = confBean.getClass();
            String jobTypeName = confBean.getClass().getInterfaces()[0].getSimpleName();
            ElasticJobConf conf = clz.getAnnotation(ElasticJobConf.class);

            String jobClass = clz.getName();
            String jobName = conf.name();
            String cron = conf.cron();
            String shardingItemParameters = conf.shardingItemParameters();
            String description = conf.description();
            String jobParameter = conf.jobParameter();
            String jobExceptionHandler = conf.jobExceptionHandler();
            String executorServiceHandler = conf.executorServiceHandler();

            String jobShardingStrategyClass = conf.jobShardingStrategyClass();
            String eventTraceRdbDataSource = conf.eventTraceRdbDataSource();
            String scriptCommandLine = conf.scriptCommandLine();

            boolean failover = conf.failover();
            boolean misfire = conf.misfire();
            boolean overwrite = conf.overwrite();
            boolean disabled = conf.disabled();
            boolean monitorExecution = conf.monitorExecution();
            boolean streamingProcess = conf.streamingProcess();

            int shardingTotalCount = conf.shardingTotalCount();
            int monitorPort = conf.monitorPort();
            int maxTimeDiffSeconds = conf.maxTimeDiffSeconds();
            int reconcileIntervalMinutes = conf.reconcileIntervalMinutes();

            // 核心配置
            JobCoreConfiguration coreConfig
                    = JobCoreConfiguration.newBuilder(jobName, cron, shardingTotalCount)
                            .shardingItemParameters(shardingItemParameters)
                            .description(description)
                            .failover(failover)
                            .jobParameter(jobParameter)
                            .misfire(misfire)
                            .jobProperties(JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(), jobExceptionHandler)
                            .jobProperties(JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(), executorServiceHandler)
                            .build();

            // 不同类型的任务配置处理
            LiteJobConfiguration jobConfig = null;
            JobTypeConfiguration typeConfig = null;
            if (jobTypeName.equals("SimpleJob")) {
                typeConfig = new SimpleJobConfiguration(coreConfig, jobClass);
            }

            if (jobTypeName.equals("DataflowJob")) {
                typeConfig = new DataflowJobConfiguration(coreConfig, jobClass, streamingProcess);
            }

            if (jobTypeName.equals("ScriptJob")) {
                typeConfig = new ScriptJobConfiguration(coreConfig, scriptCommandLine);
            }

            jobConfig = LiteJobConfiguration.newBuilder(typeConfig)
                    .overwrite(overwrite)
                    .disabled(disabled)
                    .monitorPort(monitorPort)
                    .monitorExecution(monitorExecution)
                    .maxTimeDiffSeconds(maxTimeDiffSeconds)
                    .jobShardingStrategyClass(jobShardingStrategyClass)
                    .reconcileIntervalMinutes(reconcileIntervalMinutes)
                    .build();

            List<BeanDefinition> elasticJobListeners = getTargetElasticJobListeners(conf);

            // 构建SpringJobScheduler对象来初始化任务
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(SpringJobScheduler.class);
            factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
            if ("ScriptJob".equals(jobTypeName)) {
                factory.addConstructorArgValue(null);
            } else {
                factory.addConstructorArgValue(confBean);
            }
            factory.addConstructorArgValue(zookeeperRegistryCenter);
            factory.addConstructorArgValue(jobConfig);

            // 任务执行日志数据源，以名称获取
            if (StringUtils.hasText(eventTraceRdbDataSource)) {
                BeanDefinitionBuilder rdbFactory = BeanDefinitionBuilder.rootBeanDefinition(JobEventRdbConfiguration.class);
                rdbFactory.addConstructorArgReference(eventTraceRdbDataSource);
                factory.addConstructorArgValue(rdbFactory.getBeanDefinition());
            }

            factory.addConstructorArgValue(elasticJobListeners);
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) ctx.getAutowireCapableBeanFactory();
            defaultListableBeanFactory.registerBeanDefinition(jobName + "ElasticJobScheduler", factory.getBeanDefinition());
            SpringJobScheduler springJobScheduler = (SpringJobScheduler) ctx.getBean(jobName + "ElasticJobScheduler");
            springJobScheduler.init();
            logger.info("【" + jobName + "】\t" + jobClass + "\tinit success");
        }

        //TODO 可以用bean加载
//        new JobDelayManageImpl(zookeeperRegistryCenter, messageSend).init();
    }

    /**
     * 获取监听
     *
     * @param conf
     * @return
     */
    private List<BeanDefinition> getTargetElasticJobListeners(ElasticJobConf conf) {
        List<BeanDefinition> result = new ManagedList<BeanDefinition>(2);
        String listeners = conf.listener();
        if (StringUtils.hasText(listeners)) {
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(listeners);
            factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
            result.add(factory.getBeanDefinition());
        }

        String distributedListeners = conf.distributedListener();
        long startedTimeoutMilliseconds = conf.startedTimeoutMilliseconds();
        long completedTimeoutMilliseconds = conf.completedTimeoutMilliseconds();

        if (StringUtils.hasText(distributedListeners)) {
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(distributedListeners);
            factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
            factory.addConstructorArgValue(startedTimeoutMilliseconds);
            factory.addConstructorArgValue(completedTimeoutMilliseconds);
            result.add(factory.getBeanDefinition());
        }
        return result;
    }
}
