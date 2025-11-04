package com.pivot.aham.common.core.exception;

import com.pivot.aham.common.core.base.MessageStandardCode;

/**
 * 业务层异常
 *
 * @author addison
 * @since 2018年11月16日
 */
@SuppressWarnings("serial")
public class RemoteServiceException extends BaseException {
	public RemoteServiceException() {
	}

	public RemoteServiceException(Throwable ex) {
		super(ex);
	}

	public RemoteServiceException(String message) {
		super(message);
	}

	public RemoteServiceException(String message, Throwable ex) {
		super(message, ex);
	}

	protected MessageStandardCode getCode() {
		return MessageStandardCode.CONFLICT;
	}
}