/**
 * 
 */
package com.pivot.aham.common.core.exception;

import com.pivot.aham.common.core.base.MessageStandardCode;

/**
 * 参数异常
 *
 * @author addison
 * @since 2018年11月16日
 */
@SuppressWarnings("serial")
public class IllegalParameterException extends BaseException {
	public IllegalParameterException() {
	}

	public IllegalParameterException(Throwable ex) {
		super(ex);
	}

	public IllegalParameterException(String message) {
		super(message);
	}

	public IllegalParameterException(String message, Throwable ex) {
		super(message, ex);
	}

	protected MessageStandardCode getCode() {
		return MessageStandardCode.BAD_REQUEST;
	}
}
