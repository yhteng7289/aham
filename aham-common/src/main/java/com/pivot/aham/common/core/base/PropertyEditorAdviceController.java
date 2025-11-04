package com.pivot.aham.common.core.base;

import com.pivot.aham.common.core.support.context.StringEscapeEditor;
import com.pivot.aham.common.core.support.DateFormat;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import com.pivot.aham.common.core.util.PropertiesUtil;

import java.util.Date;
import lombok.extern.slf4j.Slf4j;

/**
 * controller通知
 *
 * @author addison
 * @since 2018年11月15日
 */
@ControllerAdvice
@Slf4j
public class PropertyEditorAdviceController {


    /**
     * 对所有conftroller有效
     *
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        /**
         * 自动转换日期类型的字段格式
         */
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new DateFormat("yyyy-MM-dd HH:mm:ss"), true));
        /**
         * 防止XSS攻击,自定义属性编辑器
         */
        binder.registerCustomEditor(String.class,
                new StringEscapeEditor(PropertiesUtil.getBoolean("spring.mvc.htmlEscape", false),
                        PropertiesUtil.getBoolean("spring.mvc.javaScriptEscape", false)));
    }

}
