/**
 * 
 */
package com.pivot.aham.common.core.exception;

import com.pivot.aham.common.core.base.MessageStandardCode;

/**
 * 反射异常
 *
 * @author addison
 * @since 2018年11月16日
 */
@SuppressWarnings("serial")
public class InstanceException extends BaseException {
    public InstanceException() {
        super();
    }

    public InstanceException(Throwable t) {
        super(t);
    }

    @Override
    protected MessageStandardCode getCode() {
        return MessageStandardCode.INTERNAL_SERVER_ERROR;
    }
}
