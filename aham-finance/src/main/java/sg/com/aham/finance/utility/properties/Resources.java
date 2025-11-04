/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.com.aham.finance.utility.properties;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 获取国际化信息
 *
 * @author addison
 * @since 2018年11月19日
 */
@Slf4j
public final class Resources {

    /**
     * 国际化信息
     */
    private static final Map<String, ResourceBundle> MESSAGES = new HashMap<String, ResourceBundle>();

    /**
     * 国际化信息
     */
    public static String getMessage(String key, Object... params) {
        try {
            Locale locale = LocaleContextHolder.getLocale();
            ResourceBundle message = MESSAGES.get(locale.getLanguage());
            if (message == null) {
                synchronized (MESSAGES) {
                    message = MESSAGES.get(locale.getLanguage());
                    if (message == null) {
                        message = ResourceBundle.getBundle("i18n/messages", locale);
                        MESSAGES.put(locale.getLanguage(), message);
                    }
                }
            }

            if (message == null) {
                return "Oh," + key;
            }
            if (params != null && params.length > 0) {
                return String.format(message.getString(key), params);
            }
            String resMsg = message.getString(key);
            if (StringUtils.isEmpty(resMsg)) {
                return "Oh,Empty," + key;
            }

            return resMsg;
        } catch (Exception e) {
            log.error("国际化异常", e);
            return "Oh,Empty," + key;
        }
    }

    /**
     * 清除国际化信息
     */
    public static void flushMessage() {
        MESSAGES.clear();
    }
}
