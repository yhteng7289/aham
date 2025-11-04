package com.pivot.aham.common.core.util;

import com.pivot.aham.common.core.support.email.Email;
import com.pivot.aham.common.core.support.email.EmailSender;
import lombok.extern.slf4j.Slf4j;

/**
 * 发送邮件
 *
 * @author addison
 * @since 2018年11月19日
 */
@Slf4j
public class EmailUtil {

    /**
     * 发送邮件
     */
    public static void sendEmail(Email email) {
        EmailSender.sendEmail(email);
    }
}
