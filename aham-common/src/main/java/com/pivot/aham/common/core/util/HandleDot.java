package com.pivot.aham.common.core.util;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface HandleDot {
    boolean ifHandleDot() default false;
    int newScale() default 2;
}
