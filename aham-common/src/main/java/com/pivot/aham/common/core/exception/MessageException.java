package com.pivot.aham.common.core.exception;

import com.pivot.aham.common.core.base.MessageStandardCode;

/**
 * 参数校验异常
 *
 * @author addison
 * @since 2018年11月16日
 */
@SuppressWarnings("serial")
public class MessageException extends BaseException {
    public MessageException() {
    }

    public MessageException(Throwable ex) {
        super(ex);
    }

    public MessageException(String message) {
        super(message);
    }

    public MessageException(String message, Throwable ex) {
        super(message, ex);
    }

    @Override
    protected MessageStandardCode getCode() {
        return MessageStandardCode.PRECONDITION_FAILED;
    }
}
