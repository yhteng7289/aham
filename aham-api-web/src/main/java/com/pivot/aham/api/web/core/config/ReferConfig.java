package com.pivot.aham.api.web.core.config;

import com.pivot.aham.common.config.RpcStrategyConfig;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;

/**
 * 注册消费者（区分环境）
 *
 * @author addison
 * @since 2018年12月01日
 */
public class ReferConfig {

    @Configuration
    @Profile("prod")
    @Conditional({RpcStrategyConfig.EnableDubboReference.class})
    @ImportResource("classpath:refer/dubbo-prod.xml")
    static class DubboProdReferConfig {
    }

    @Configuration
    @Profile("prod2")
    @Conditional({RpcStrategyConfig.EnableDubboReference.class})
    @ImportResource("classpath:refer/dubbo-prod2.xml")
    static class DubboProd2ReferConfig {
    }

    @Configuration
    @Profile("dev")
    @Conditional({RpcStrategyConfig.EnableDubboReference.class})
    @ImportResource("classpath:refer/dubbo-dev.xml")
    static class DubboDevReferConfig {
    }

    @Configuration
    @Profile("local")
    @Conditional({RpcStrategyConfig.EnableDubboReference.class})
    @ImportResource("classpath:refer/dubbo-local.xml")
    static class DubboLocalReferConfig {
    }

    @Configuration
    @Profile("test")
    @Conditional({RpcStrategyConfig.EnableDubboReference.class})
    @ImportResource("classpath:refer/dubbo-test.xml")
    static class DubboTestReferConfig {
    }

    @Configuration
    @Profile("test02")
    @Conditional({RpcStrategyConfig.EnableDubboReference.class})
    @ImportResource("classpath:refer/dubbo-test02.xml")
    static class DubboTest02ReferConfig {
    }

}
