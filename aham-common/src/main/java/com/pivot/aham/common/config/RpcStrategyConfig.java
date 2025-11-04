package com.pivot.aham.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;

import com.pivot.aham.common.core.util.PropertiesUtil;

/**
 * rpc服务实现策略环境类，为将来更换服务实现准备
 *
 * @author addison
 * @since 2018年11月16日
 */
public class RpcStrategyConfig {
    /**
     * 生产者
     */
    public static class EnableDubboService implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return "dubbo".equals(PropertiesUtil.getString("rpc.type"))
                    && "Y".equals(PropertiesUtil.getString("rpc.dubbo.service"));
        }
    }

    public static class EnableDubboReference implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return "dubbo".equals(PropertiesUtil.getString("rpc.type"))
                    && "Y".equals(PropertiesUtil.getString("rpc.dubbo.reference"));
        }
    }

    @Configuration
    @Conditional(EnableDubboService.class)
    @DubboComponentScan("${rpc.package}")
    static class DubboServiceConfig extends DubboConfig {
        @Bean
        public ProtocolConfig protocol() {
            ProtocolConfig protocolConfig = new ProtocolConfig();
            protocolConfig.setPort(PropertiesUtil.getInt("rpc.protocol.port", 20880));
            protocolConfig.setThreadpool("cached");
            protocolConfig.setThreads(PropertiesUtil.getInt("rpc.protocol.maxThread", 100));
            protocolConfig.setPayload(PropertiesUtil.getInt("rpc.protocol.maxContentLength", 1048576));
            return protocolConfig;
        }
    }

    /**
     * 消费者配置
     */
    @Configuration
    @Conditional(EnableDubboReference.class)
    static class DubboConsumerConfig extends DubboConfig {
        @Bean
        public ConsumerConfig consumer() {
            ConsumerConfig consumerConfig = new ConsumerConfig();
            consumerConfig.setLoadbalance("leastactive");
            consumerConfig.setTimeout(PropertiesUtil.getInt("rpc.request.timeout", 120000));
            consumerConfig.setRetries(PropertiesUtil.getInt("rpc.consumer.retries", 0));
            consumerConfig.setCheck(false);
            return consumerConfig;
        }
    }
}
