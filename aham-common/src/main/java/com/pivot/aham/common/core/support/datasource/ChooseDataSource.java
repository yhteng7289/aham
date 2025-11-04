package com.pivot.aham.common.core.support.datasource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 决定数据源
 *
 * @author addison
 * @since 2018年11月15日
 */
public class ChooseDataSource extends AbstractRoutingDataSource {

    /**
     * 获取执行sql的数据源
     * @return
     */
    protected Object determineCurrentLookupKey() {
        return HandleDataSource.getDataSource();
    }

    // 设置方法名前缀对应的数据源
    public void setMethodType(Map<String, String> map) {
        for (String key : map.keySet()) {
            List<String> v = new ArrayList<String>();
            String[] types = map.get(key).split(",");
            for (String type : types) {
                if (StringUtils.isNotBlank(type)) {
                    v.add(type);
                }
            }
            HandleDataSource.METHODTYPE.put(key, v);
        }
    }
}
