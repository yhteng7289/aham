/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.com.aham.finance.utility.email;

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
     *
     * @param email
     */
    public static void sendEmail(Email email) {
        EmailSender.sendEmail(email);
    }
}
