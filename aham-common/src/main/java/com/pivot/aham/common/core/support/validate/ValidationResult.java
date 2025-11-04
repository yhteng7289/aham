package com.pivot.aham.common.core.support.validate;

import java.util.Map;

/**
 * 校验结果集
 */
public class ValidationResult {
	/**
	 * 校验是否有错
	 */
    private boolean hasErrors;
	
	/**
	 * 校验错误集合
	 */
	private Map<String,String> errorMsg;

	public boolean isHasErrors() {
		return hasErrors;
	}

	public void setHasErrors(boolean hasErrors) {
		this.hasErrors = hasErrors;
	}

	public Map<String, String> getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(Map<String, String> errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public String toString() {
		return "ValidationResult [hasErrors=" + hasErrors + ", errorMsg="
				+ errorMsg + "]";
	}

}