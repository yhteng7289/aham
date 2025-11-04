package com.pivot.aham.common.core.support.email;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import java.io.Serializable;
import java.util.Map;

@Data
@Accessors(chain = true)
public class Email implements Serializable {
    /**
     * 是否有安全认证
     */
    private boolean isSSL = true;
    /**
     * //接收人
     */
    private String sendTo;

    /**
     * //抄送人
     */
    private String copyTo;

    /**
     * //主题
     */
    private String topic;

    /**
     * //内容
     */
    private String body;

    /**
     * //附件
     */
    private String[] fileAffix;

    //模板名字
    private String templateName;

    private BodyPart bodyPart; //附件
    //附件名
    private String fileName;

    //模板内容
//    private Context templateContext;
    private Map<String,Object> templateVariables;
}

