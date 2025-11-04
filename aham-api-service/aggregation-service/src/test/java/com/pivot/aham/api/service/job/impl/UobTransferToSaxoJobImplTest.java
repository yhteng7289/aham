package com.pivot.aham.api.service.job.impl;

import com.beust.jcommander.internal.Lists;
import com.pivot.aham.api.service.bean.RechargeRefundBean;
import com.pivot.aham.common.core.support.email.Email;
import com.pivot.aham.common.core.support.file.excel.ExportExcel;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.EmailUtil;
import com.pivot.aham.common.enums.CurrencyEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

import java.io.*;
import java.math.BigDecimal;

/**
 * Created by luyang.li on 18/12/29.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UobTransferToSaxoJobImplTest {

    @Test
    public void emailTest() throws IOException, MessagingException {

        RechargeRefundBean rechargeRefund = new RechargeRefundBean();
        rechargeRefund.setAmount(new BigDecimal("200"));
        rechargeRefund.setBankOrderNumber("122");
        rechargeRefund.setBankProvidedName("abc");
        rechargeRefund.setClientId("123");
        rechargeRefund.setClientName("123");
        rechargeRefund.setCurrency(CurrencyEnum.SGD.getCode());
        rechargeRefund.setTradeTime(DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT4));
        rechargeRefund.setVirtualAccountNo("12345");

        RechargeRefundBean rechargeRefund2 = new RechargeRefundBean();
        rechargeRefund2.setAmount(new BigDecimal("200"));
        rechargeRefund2.setBankOrderNumber("122");
        rechargeRefund2.setBankProvidedName("abc");
        rechargeRefund2.setClientId("123");
        rechargeRefund2.setClientName("123");
        rechargeRefund2.setCurrency(CurrencyEnum.SGD.getCode());
        rechargeRefund2.setTradeTime(DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT4));
        rechargeRefund2.setVirtualAccountNo("12345");


        ExportExcel exportExcel = new ExportExcel(null, RechargeRefundBean.class);
        exportExcel.setDataList(Lists.newArrayList(rechargeRefund, rechargeRefund2));
        String fileName = "recharge_refund.xlsx";
        String topic = "recharge_need_refund_" + DateUtils.getDate();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        exportExcel.write(os);

        BodyPart bodyPart = new MimeBodyPart();
        ByteArrayDataSource dataSource = new ByteArrayDataSource(os.toByteArray(), "application/png");
        bodyPart.setDataHandler(new DataHandler(dataSource));
        bodyPart.setFileName(fileName);

        Email email = new Email();
//        email.setFileAffix(new String[] {fileName});
        email.setBodyPart(bodyPart);
        email.setSendTo("wooitatt.khor@ezyit.asia");
        email.setTopic(topic);


        EmailUtil.sendEmail(email);

        exportExcel.dispose();

    }

}