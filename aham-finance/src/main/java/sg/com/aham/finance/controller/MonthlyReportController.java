/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.com.aham.finance.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sg.com.aham.finance.excel.UserAssetExcelData;
import sg.com.aham.finance.excel.UserFundNavExcelData;
import sg.com.aham.finance.impl.UserAssetReportingImpl;
import sg.com.aham.finance.impl.UserFundNavReportingImpl;
import sg.com.aham.finance.model.UserAssetReport;
import sg.com.aham.finance.model.UserFundNavReport;
import sg.com.aham.finance.utility.email.Email;
import sg.com.aham.finance.utility.email.EmailUtil;
import sg.com.aham.finance.utility.excel.ExportExcel;

/**
 *
 * @author HP
 */
@Controller
@Slf4j
public class MonthlyReportController {

    @Autowired
    private Environment env;

    @Autowired
    private UserAssetReportingImpl userAssetReportingImpl;

    @Autowired
    private UserFundNavReportingImpl userFundNavReportingImpl;

    @Value("${app.filename.report.one}")
    String userAssetReportName;

    @Value("${app.filename.report.two}")
    String userFundNavReportName;

    @Value("${app.topic.report.one}")
    String userAssetTopicName;

    @Value("${app.topic.report.two}")
    String userFundNavTopicName;

    @Value("${app.report.receiver.email}")
    String reportReceiverEmail;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

    @Scheduled(cron = "00 00 09 3 * ?")
    @RequestMapping(value = "/finance/report", method = RequestMethod.GET)
    @ResponseBody
    public String sendReport() {
        userAssetReport();
        userFundNavReport();
        return "done";
    }

    private void userAssetReport() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        log.info("calendar time {} ", calendar.getTime());
        String reportDate = sdf.format(calendar.getTime());
        log.info("report date {} ", reportDate);

        List<UserAssetReport> userAssetList = userAssetReportingImpl.getUserAssetReportingByDate(reportDate);
        ExportExcel exportExcel = new ExportExcel(null, UserAssetExcelData.class);
        exportExcel.setDataList(userAssetList);
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            log.info("Start Writing data into excel file ...");
            exportExcel.write(os);
            log.info("Done writing data into excel file ...");
            BodyPart bodyPart = new MimeBodyPart();
            ByteArrayDataSource dataSource = new ByteArrayDataSource(os.toByteArray(), "application/vnd.ms-excel");
            bodyPart.setDataHandler(new DataHandler(dataSource));
            bodyPart.setFileName(userAssetReportName);

            Email email = new Email();
            email.setEnv(env);
            email.setSSL(true);
            email.setBodyPart(bodyPart);
            email.setSendTo(reportReceiverEmail);
            email.setTopic(reportDate + "-" + userAssetTopicName);
            email.setBody(reportDate + ", Kindly find attachment");
            log.info("Start Sending userAssetReport excel file to {} ", reportReceiverEmail);
            EmailUtil.sendEmail(email);
            log.info("Done Sending userAssetReport excel file to {} ", reportReceiverEmail);
        } catch (IOException | MessagingException e) {

        } finally {
            exportExcel.dispose();
        }
    }

    private void userFundNavReport() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        log.info("calendar time {} ", calendar.getTime());
        String reportDate = sdf.format(calendar.getTime());
        log.info("report date {} ", reportDate);

        List<UserFundNavReport> userFundNavList = userFundNavReportingImpl.getUserFundNavReportingByDate(reportDate);
        ExportExcel exportExcel = new ExportExcel(null, UserFundNavExcelData.class);
        exportExcel.setDataList(userFundNavList);
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            log.info("Start Writing data into excel file ...");
            exportExcel.write(os);
            log.info("Done writing data into excel file ...");
            BodyPart bodyPart = new MimeBodyPart();
            ByteArrayDataSource dataSource = new ByteArrayDataSource(os.toByteArray(), "application/vnd.ms-excel");
            bodyPart.setDataHandler(new DataHandler(dataSource));
            bodyPart.setFileName(userFundNavReportName);

            Email email = new Email();
            email.setEnv(env);
            email.setSSL(true);
            email.setBodyPart(bodyPart);
            email.setSendTo(reportReceiverEmail);
            email.setTopic(reportDate + "-" + userFundNavTopicName);
            email.setBody(reportDate + ", Kindly find attachment");
            log.info("Start Sending userFundNavReport excel file ...");
            EmailUtil.sendEmail(email);
            log.info("Done Sending userFundNavReport excel file ...");
        } catch (IOException | MessagingException e) {

        } finally {
            exportExcel.dispose();
        }
    }

}
