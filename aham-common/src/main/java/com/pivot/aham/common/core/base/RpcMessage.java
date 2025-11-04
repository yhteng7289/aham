package com.pivot.aham.common.core.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * 响应消息
 *
 * @author addison
 * @since 2018年09月05日
 */
@Data
@ApiModel(value = "返回对象说明")
public class RpcMessage<T> implements Serializable {

    // 响应码
    @ApiModelProperty(value = "响应码")
    private int resultCode;
    // 错误信息
    @ApiModelProperty(value = "响应错误信息")
    private String errMsg;
    // 数据体
    @ApiModelProperty(value = "响应数据体")
    private T content;
    @ApiModelProperty(value = "响应时间戳")
    private Long timestamp = System.currentTimeMillis();

    public boolean isSuccess() {
        return this.resultCode == RpcMessageStandardCode.OK.value();
    }

    /**
     * 创建成功消息
     *
     * @return 消息
     */
    public static <T> RpcMessage<T> success() {
        return RpcMessage.success(null);
    }

    /**
     * 创建成功消息
     *
     * @param data 数据
     * @return 消息
     */
    public static <T> RpcMessage<T> success(T data) {
        return RpcMessage.success(null, data);
    }

    /**
     * 创建成功消息
     *
     * @param msg 成功提示
     * @param data 数据
     * @return 消息
     */
    public static <T> RpcMessage<T> success(String msg, T data) {
        RpcMessage<T> message = new RpcMessage<>();
        message.setResultCode(RpcMessageStandardCode.OK.value());
        message.setErrMsg(msg);
        message.setContent(data);
        return message;
    }

    /**
     * 创建失败消息
     *
     * @param errMsg 错误提示
     * @return 消息
     */
    public static RpcMessage error(String errMsg) {
        RpcMessage message = new RpcMessage();
        RpcMessageStandardCode code = RpcMessageStandardCode.INTERNAL_SERVER_ERROR;
        message.setResultCode(code.value());
        if (StringUtils.isNotEmpty(errMsg)) {
            message.setErrMsg(errMsg);
        } else {
            message.setErrMsg(code.msg());
        }
        return message;
    }

    /**
     * 创建失败消息
     *
     * @param code 错误码
     * @param errMsg 错误提示
     * @return 消息
     */
    public static RpcMessage error(final RpcMessageStandardCode code, String errMsg) {
        RpcMessage message = new RpcMessage();
        message.setResultCode(code.value());
        if (StringUtils.isNotEmpty(errMsg)) {
            message.setErrMsg(errMsg);
        } else {
            message.setErrMsg(code.msg());
        }
        return message;
    }

    /**
     * 创建失败消息
     *
     * @param resultCode 错误码
     * @param errMsg 错误提示
     * @return 消息
     */
    public static RpcMessage error(int resultCode, String errMsg) {
        RpcMessage message = new RpcMessage();
        message.setResultCode(resultCode);
        message.setErrMsg(errMsg);
        return message;
    }

    public static Boolean isSuccess(RpcMessage message) {
        if (message == null) {
            return false;
        }
        int resultCode = message.getResultCode();
        return Objects.equals(resultCode, RpcMessageStandardCode.OK.value());
    }
}
