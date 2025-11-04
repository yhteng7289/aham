package com.pivot.aham.common.core.support.validate;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * hibernate 校验工具类
 *
 * @author addison(jinling.cui@pintec.com)
 * @since 2017年08月18日
 */
public class ValidationUtils {
	/**
	 * 校验器
	 */
	private static Validator validator = Validation.byProvider( HibernateValidator.class )
	.configure()
	.failFast( true )
	.buildValidatorFactory().getValidator();
//	private static Validator validator =  Validation.byProvider( HibernateValidator.class).buildDefaultValidatorFactory().getValidator();
	/**
	 * default组校验
	 * @param obj
	 * @param <T>
	 * @return
	 */
	public static <T> ValidationResult validateEntity(T obj){
		ValidationResult result = new ValidationResult();
		Set<ConstraintViolation<T>> set = validator.validate(obj,Default.class);
		genValRes(result, set);
		return result;
	}

	private static <T> void genValRes(ValidationResult result, Set<ConstraintViolation<T>> set) {
		if( CollectionUtils.isNotEmpty(set) ){
			result.setHasErrors(true);
			Map<String,String> errorMsg = new HashMap<>();
			for(ConstraintViolation<T> cv : set){
				errorMsg.put(cv.getPropertyPath().toString(), cv.getMessage());
			}
			result.setErrorMsg(errorMsg);
		}
	}

	/**
	 * default组校验指定属性
	 * @param obj
	 * @param propertyName
	 * @param <T>
	 * @return
	 */
	public static <T> ValidationResult validateProperty(T obj,String propertyName){
		ValidationResult result = new ValidationResult();
		Set<ConstraintViolation<T>> set = validator.validateProperty(obj,propertyName,Default.class);
		genValRes(result, set);
		return result;
	}


	/**
	 * default组校验
	 * @param obj
	 * @param <T>
	 * @return
	 */
	public static <T> ValidationResult validateEntity(T obj, Class<?>... groups){
		ValidationResult result = new ValidationResult();
		Set<ConstraintViolation<T>> set = validator.validate(obj,Default.class);
		genValRes(result, set);
		return result;
	}

	/**
	 * default组校验指定属性
	 * @param obj
	 * @param propertyName
	 * @param <T>
	 * @return
	 */
	public static <T> ValidationResult validateProperty(T obj, String propertyName, Class<?>... groups){
		ValidationResult result = new ValidationResult();
		Set<ConstraintViolation<T>> set = validator.validateProperty(obj,propertyName,Default.class);
		genValRes(result, set);
		return result;
	}
}