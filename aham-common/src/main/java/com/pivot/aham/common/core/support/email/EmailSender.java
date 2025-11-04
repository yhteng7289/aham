package com.pivot.aham.common.core.support.email;

import com.pivot.aham.common.core.support.context.Resources;
import com.pivot.aham.common.core.util.PropertiesUtil;
import com.pivot.aham.common.core.support.context.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Date;
import java.util.Properties;

/**
 * 邮件实现类
 *
 * @author addison
 * @since 2018年11月19日
 */
@Slf4j
public class EmailSender {

    private MimeMessage mimeMsg; // MIME邮件对象
    private Session session; // 邮件会话对象
    private Properties props; // 系统属性
    private final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    private String username; // smtp认证用户名和密码
    private String password;
    private boolean isSSL;
    private String templeteName;
    private Context context;

    private Multipart mp; // Multipart对象,邮件内容,标题,附件等内容均添加到其中后再生成MimeMessage对象

    /**

     * @param isSSL
     */
    public EmailSender(boolean isSSL) {
        try {
            this.isSSL = isSSL;
            String host = PropertiesUtil.getString("email.smtp.host");
//            log.info(Resources.getMessage("EMAIL.SET_HOST"), host);
            if (props == null) {
                props = System.getProperties(); // 获得系统属性对象
            }
            props.put("mail.smtp.host", host); // 设置SMTP主机
            String port = PropertiesUtil.getString("email.smtp.port");
            if (port == null || port.trim().equals("")) {
                props.put("mail.smtp.port", "25");
            } else {
                props.put("mail.smtp.port", port);
            }
            if (isSSL) {
                props.put("mail.smtp.port", "465");
                props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
                props.setProperty("mail.smtp.socketFactory.fallback", "false");
                props.setProperty("mail.smtp.socketFactory.port", "465");
            }
            if (!createMimeMessage()) {
                throw new RuntimeException("创建MIME邮件对象和会话失败");
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    public void init(Email email){
        this.templeteName=email.getTemplateName();
        Context context = new Context();
        context.setVariables(email.getTemplateVariables());
        this.context = context;
    }

    /**
     * @return
     */
    public boolean setNamePass() {
        String name = PropertiesUtil.getString("email.user.name");
        String pass = PropertiesUtil.getString("email.user.password");
        username = name;
        password = pass;
        setNeedAuth();
        return createMimeMessage();
    }

    /**
     *
     */
    private void setNeedAuth() {
        if (props == null) {
            props = System.getProperties();
        }
        if (password == null || password.trim().equals("")) {
            props.setProperty("mail.smtp.auth", "false");
            log.info(Resources.getMessage("EMAIL.SET_AUTH"), "false");
        } else {
            props.setProperty("mail.smtp.auth", "true");
            log.info(Resources.getMessage("EMAIL.SET_AUTH"), "true");
        }
    }

    /**
     * 创建MIME邮件对象
     *
     * @return boolean
     */
    private boolean createMimeMessage() {
        if (session == null) {
            try {
                // 获得邮件会话对象
                Authenticator authenticator = new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        if (password == null || "".equals(password.trim())) {
                            return null;
                        }
                            return new PasswordAuthentication(username, password);
                    }
                };
                session = Session.getInstance(props, authenticator);
            } catch (Exception e) {
                log.error(Resources.getMessage("EMAIL.ERROR_TALK"), e.getLocalizedMessage());
                return false;
            }
        }
        if (mimeMsg == null) {
            try {
                mimeMsg = new MimeMessage(session); // 创建MIME邮件对象
                mp = new MimeMultipart();
                return true;
            } catch (Exception e) {
                log.error(Resources.getMessage("EMAIL.ERROR_MIME"), e.getLocalizedMessage());
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * 设置主题
     *
     * @param mailSubject String
     * @return boolean
     */
    public boolean setSubject(String mailSubject) {
        log.info(Resources.getMessage("EMAIL.SET_SUBJECT"), mailSubject);
        try {
            mimeMsg.setSubject(mailSubject, "UTF-8");
            return true;
        } catch (Exception e) {
            log.error(Resources.getMessage("EMAIL.ERROR_SUBJECT"), e);
            return false;
        }
    }

    /**
     * 设置内容
     *
     * @param mailBody String
     */
    public boolean setBody(String mailBody) {
        String body = mailBody;
        try {
            if(StringUtils.isNotEmpty(templeteName) && context != null){
                TemplateEngine templateEngine = (TemplateEngine) ApplicationContextHolder.getBean("templateEngine");
                body = templateEngine.process(templeteName,context);
            }

            BodyPart bp = new MimeBodyPart();
            bp.setContent("" + body, "text/html;charset=UTF-8");
            mp.addBodyPart(bp);
            return true;
        } catch (Exception e) {
            log.error(Resources.getMessage("EMAIL.ERROR_BODY"), e);
            return false;
        }
    }

    /**
     * 设置附件
     *
     * @param filename
     * @return
     */
    public boolean addFileAffix(String filename) {
        log.info(Resources.getMessage("EMAIL.ADD_ATTEND"), filename);
        try {
            BodyPart bp = new MimeBodyPart();
            FileDataSource fileds = new FileDataSource(filename);
            bp.setDataHandler(new DataHandler(fileds));
            bp.setFileName(MimeUtility.encodeText(fileds.getName()));
            mp.addBodyPart(bp);
            return true;
        } catch (Exception e) {
            log.error(filename, e);
            return false;
        }
    }

    /**
     * 设置附件
     *
     * @param bodyPart
     * @return
     */
    public boolean addFileAffix(BodyPart bodyPart) {
        log.info("流式方式发送附件");
        try {
            mp.addBodyPart(bodyPart);
            return true;
        } catch (Exception e) {
            log.error("流式方式发送附件异常", e);
            return false;
        }
    }

    /**
     * 设置发信人
     *
     * @return
     */
    public boolean setFrom() {
           String from = PropertiesUtil.getString("email.send.from");
        try {
            String[] f = from.split(",");
            if (f.length > 1) {
                from = MimeUtility.encodeText(f[0]) + "<" + f[1] + ">";
            }
            mimeMsg.setFrom(new InternetAddress(from)); // 设置发信人
            return true;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * 设置收信人
     *
     * @param to
     * @return
     */
    public boolean setTo(String to) {
        if (to == null) {
            return false;
        }
        log.info(Resources.getMessage("EMAIL.SET_TO"), to);
        try {
            mimeMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            return true;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * 设置抄送人
     * @param copyto
     * @return
     */
    public boolean setCopyTo(String copyto) {
        if (copyto == null) {
            return false;
        }
        log.info(Resources.getMessage("EMAIL.SET_COPYTO"), copyto);
        try {
            mimeMsg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(copyto));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 发送邮件
     */
    public boolean sendout() {
        try {

            mimeMsg.setContent(mp);
            // 设置发送日期
            mimeMsg.setSentDate(new Date());
            mimeMsg.saveChanges();

            log.info(Resources.getMessage("EMAIL.SENDING"));
            // 发送
            Transport.send(mimeMsg);
            log.info(Resources.getMessage("EMAIL.SEND_SUCC"));
            return true;
        } catch (Exception e) {
            log.error(Resources.getMessage("EMAIL.SEND_ERR"), e);
            return false;
        }
    }

    public static void sendEmail(Email email){
        // 初始化邮件引擎
        EmailSender sender = new EmailSender(email.isSSL());
        sender.init(email);
        if (!sender.setNamePass()) {
            return;
        }
        if (!sender.setFrom()) {
            return;
        }
        if (!sender.setTo(email.getSendTo())) {
            return;
        }
        if (email.getCopyTo() != null && !sender.setCopyTo(email.getCopyTo())) {
            return;
        }
        if (!sender.setSubject(email.getTopic())) {
            return;
        }
        if (!sender.setBody(email.getBody())) {
            return;
        }
        if (email.getFileAffix() != null) {
            for (int i = 0; i < email.getFileAffix().length; i++) {
                if (!sender.addFileAffix(email.getFileAffix()[i])) {
                    return;
                }
            }
        }
        if (email.getBodyPart() != null) {
            sender.addFileAffix(email.getBodyPart());
        }
        // 发送
        sender.sendout();
    }
}
