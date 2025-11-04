package com.pivot.aham.common.config;

import com.pivot.aham.common.core.util.PropertiesUtil;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 是否集成shiro
 *
 * @author addison
 * @since 2018年11月16日
 */
public class ShiroStrategyConfig {
    public static class EnableShiroService implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return "Y".equals(PropertiesUtil.getString("shiro.service"));
        }
    }

}
