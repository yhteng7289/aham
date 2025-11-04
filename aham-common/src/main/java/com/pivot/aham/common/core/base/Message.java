package com.pivot.aham.common.core.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 响应消息
 *
 * @author addison
 * @since 2018年09月05日
 */
@Data
@ApiModel(value = "统一返回对象说明")
public class Message<T> {

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

    /**
     * 创建成功消息
     *
     * @return 消息
     */
    public static <T> Message<T> success() {
        return Message.success(null);
    }

    /**
     * 创建成功消息
     *
     * @param data 数据
     * @return 消息
     */
    public static <T> Message<T> success(T data) {
        return Message.success(null, data);
    }

    /**
     * 创建成功消息
     *
     * @param msg 消息
     * @return 消息
     */
    public static <T> Message<T> success(String msg) {
        return Message.success(msg, null);
    }

    /**
     * 创建成功消息
     *
     * @param msg 成功提示
     * @param data 数据
     * @return 消息
     */
    public static <T> Message<T> success(String msg, T data) {
        Message<T> message = new Message<>();
        message.setResultCode(MessageStandardCode.OK.value());
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
    public static Message error(String errMsg) {
        Message message = new Message();
        MessageStandardCode code = MessageStandardCode.INTERNAL_SERVER_ERROR;
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
    public static Message error(final MessageStandardCode code, String errMsg) {
        Message message = new Message();
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
    public static Message error(int resultCode, String errMsg) {
        Message message = new Message();
        message.setResultCode(resultCode);
        message.setErrMsg(errMsg);
        return message;
    }

    public static Boolean isSuccess(Message message) {
        if (message == null) {
            return false;
        }
        int resultCode = message.getResultCode();
        return Objects.equals(resultCode, MessageStandardCode.OK.value());
    }
}
