/**
 *
 */
package com.pivot.aham.common.core.exception;

import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.MessageStandardCode;
import org.apache.commons.lang3.StringUtils;

/**
 * 基础runtimeexception
 *
 * @author addison
 * @since 2018年11月16日
 */
@SuppressWarnings("serial")
public abstract class BaseException extends RuntimeException {

    public BaseException() {
    }

    public BaseException(Throwable ex) {
        super(ex);
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable ex) {
        super(message, ex);
    }

    public void handler(Message message) {
        message.setResultCode(getCode().value());
        if (StringUtils.isNotBlank(getMessage())) {
            message.setErrMsg(getMessage());
        } else {
            message.setErrMsg(getCode().msg());
        }
    }

    protected abstract MessageStandardCode getCode();
}
