package com.pivot.aham.common.core.exception;

import com.pivot.aham.common.core.base.MessageStandardCode;

/**
 * 业务层异常
 *
 * @author addison
 * @since 2018年11月16日
 */
@SuppressWarnings("serial")
public class HttpClientException extends BaseException {
	public HttpClientException() {
	}

	public HttpClientException(Throwable ex) {
		super(ex);
	}

	public HttpClientException(String message) {
		super(message);
	}

	public HttpClientException(String message, Throwable ex) {
		super(message, ex);
	}

	protected MessageStandardCode getCode() {
		return MessageStandardCode.INTERNAL_SERVER_ERROR;
	}
}