/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.com.aham.finance.utility.email;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.mail.BodyPart;
import java.io.Serializable;
import java.util.Map;
import org.springframework.core.env.Environment;

@Data
@Accessors(chain = true)
public class Email implements Serializable {
    
    private Environment env;

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
    private Map<String, Object> templateVariables;
}
