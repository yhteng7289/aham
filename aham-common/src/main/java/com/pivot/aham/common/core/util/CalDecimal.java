package com.pivot.aham.common.core.util;

import cn.hutool.core.util.ReflectUtil;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class CalDecimal<T> {
    public void handleDot(T t){
        Field[] openStaticfields = ReflectUtil.getFieldsDirectly(t.getClass(), false);
        for (Field field : openStaticfields) {
            HandleDot handleDot = field.getAnnotation(HandleDot.class);
            if (handleDot !=null && !handleDot.ifHandleDot()){
                continue;
            }
            if (field.getType() != BigDecimal.class) {
                continue;
            }
            BigDecimal fileValue = (BigDecimal) ReflectUtil.getFieldValue(t,field);
            if(fileValue != null){
                int newScale = 2;
                if(handleDot != null){
                    newScale = handleDot.newScale();
                }
                ReflectUtil.setFieldValue(t,field,fileValue.setScale(newScale,BigDecimal.ROUND_DOWN));
            }
        }
    }
}