/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.com.aham.finance.utility.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import sg.com.aham.finance.utility.excel.data.DataUtil;

/**
 * 全局配置缓存
 *
 * @author addison
 * @since 2018年11月18日
 */
public final class PropertiesUtil {

    private final static Map<String, String> CTX_PROPERTIES_MAP = new HashMap<String, String>();

    public static Map<String, String> getProperties() {
        return CTX_PROPERTIES_MAP;
    }

    /**
     * Get a value based on key , if key does not exist , null is returned
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        try {
            return CTX_PROPERTIES_MAP.get(key);
        } catch (MissingResourceException e) {
            return null;
        }
    }

    /**
     * Get a value based on key , if key does not exist , null is returned
     *
     * @param key
     * @return
     */
    public static String getString(String key, String defaultValue) {
        try {
            String value = CTX_PROPERTIES_MAP.get(key);
            if (DataUtil.isEmpty(value)) {
                return defaultValue;
            }
            return value;
        } catch (MissingResourceException e) {
            return defaultValue;
        }
    }

    /**
     * 根据key获取值
     *
     * @param key
     * @return
     */
    public static Integer getInt(String key) {
        String value = CTX_PROPERTIES_MAP.get(key);
        if (DataUtil.isEmpty(value)) {
            return null;
        }
        return Integer.parseInt(value);
    }

    /**
     * 根据key获取值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getInt(String key, int defaultValue) {
        String value = CTX_PROPERTIES_MAP.get(key);
        if (DataUtil.isEmpty(value)) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    /**
     * 从配置文件中取得 long 值，若无（或解析异常）则返回默认值
     *
     * @param keyName 属性名
     * @param defaultValue 默认值
     * @return 属性值
     */
    public static long getLong(String keyName, long defaultValue) {
        String value = getString(keyName);
        if (DataUtil.isEmpty(value)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static boolean getBoolean(String key) {
        String value = CTX_PROPERTIES_MAP.get(key);
        if (DataUtil.isEmpty(value)) {
            return false;
        }
        return new Boolean(value.trim());
    }

    /**
     * 根据key获取值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = CTX_PROPERTIES_MAP.get(key);
        if (DataUtil.isEmpty(value)) {
            return defaultValue;
        }
        return new Boolean(value.trim());
    }

    public static boolean isProd() {
        String env = getString("env.remark");
        return "prod".equals(env);
    }

    public static boolean isDev() {
        String env = getString("env.remark");
        return "dev".equals(env);
    }

    public static boolean isTest() {
        String env = getString("env.remark");
        return "test".equals(env);
    }

}
