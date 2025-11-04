package com.pivot.aham.common.core.exception;

import com.pivot.aham.common.core.base.MessageStandardCode;

/**
 * 业务层异常
 *
 * @author addison
 * @since 2018年11月16日
 */
@SuppressWarnings("serial")
public class FtpException extends BaseException {
	public FtpException() {
	}

	public FtpException(Throwable ex) {
		super(ex);
	}

	public FtpException(String message) {
		super(message);
	}

	public FtpException(String message, Throwable ex) {
		super(message, ex);
	}

	protected MessageStandardCode getCode() {
		return MessageStandardCode.CONFLICT;
	}
}