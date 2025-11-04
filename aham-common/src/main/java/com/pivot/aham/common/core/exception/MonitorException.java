package com.pivot.aham.common.core.exception;

import com.pivot.aham.common.core.base.MessageStandardCode;

/**
 * 业务层异常
 *
 * @author addison
 * @since 2018年11月16日
 */
@SuppressWarnings("serial")
public class MonitorException extends BaseException {
	public MonitorException() {
	}

	public MonitorException(Throwable ex) {
		super(ex);
	}

	public MonitorException(String message) {
		super(message);
	}

	public MonitorException(String message, Throwable ex) {
		super(message, ex);
	}

	protected MessageStandardCode getCode() {
		return MessageStandardCode.INTERNAL_SERVER_ERROR;
	}
}