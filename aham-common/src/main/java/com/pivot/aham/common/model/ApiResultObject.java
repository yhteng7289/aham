package com.pivot.aham.common.model;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;

/**
 * @author YYYz
 */
public class ApiResultObject<T> implements Serializable {

    private String code;

    private String msg;

    private T body;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this).toString();
    }
}
