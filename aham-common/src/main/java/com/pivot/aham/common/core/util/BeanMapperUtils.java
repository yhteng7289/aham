package com.pivot.aham.common.core.util;

import com.google.common.collect.Lists;
import com.pivot.aham.common.core.exception.DataParseException;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简单封装Dozer, 实现深度转换Bean<->Bean的Mapper.实现:
 *
 * 1. 持有Mapper的单例. 
 * 2. 返回值类型转换.
 * 3. 批量转换Collection中的所有对象.
 * 4. 区分创建新的B对象与将对象A值复制到已存在的B对象两种函数.
 *
 * @author addison
 * @version 2017-07-15
 */
public class BeanMapperUtils {

	/**
	 * 持有Dozer单例, 避免重复创建DozerMapper消耗资源.
	 */
	private static DozerBeanMapper dozer = new DozerBeanMapper();

	/**
	 * 基于Dozer转换对象的类型.
	 */
	public static <T> T map(Object source, Class<T> destinationClass) {
		return dozer.map(source, destinationClass);
	}

	/**
	 * 基于Dozer转换Collection中对象的类型.
	 */
	@SuppressWarnings("rawtypes")
	public static <T> List<T> mapList(Collection sourceList, Class<T> destinationClass) {
		List<T> destinationList = Lists.newArrayList();
		for (Object sourceObject : sourceList) {
			T destinationObject = dozer.map(sourceObject, destinationClass);
			destinationList.add(destinationObject);
		}
		return destinationList;
	}

	/**
	 * 基于Dozer将对象A的值拷贝到对象B中.
	 */
	public static void copy(Object source, Object destinationObject) {
		dozer.map(source, destinationObject);
	}

	/**
	 * 将bean转换为map
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> trans2Map(Object obj) {
		return trans2Map(obj,false);
	}

	public static Map<String, Object> trans2Map(Object obj,Boolean excludeNull) {
		if(obj == null){
			return null;
		}
		Map<String, Object> map = new HashMap<>();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();
				// 过滤class属性
				if (!key.equals("class")) {
					// 得到property对应的getter方法
					Method getter = property.getReadMethod();
					Object value = getter.invoke(obj);
					if(excludeNull){
						if (value == null) {
							continue;
						}
						if (value instanceof String) {
							if(StringUtils.isEmpty((CharSequence) value)){
								continue;
							}
						}
						map.put(key, value);
					}else {
						map.put(key, value);
					}

				}

			}
		} catch (Exception e) {
			throw new DataParseException(e);
		}
		return map;
	}


}