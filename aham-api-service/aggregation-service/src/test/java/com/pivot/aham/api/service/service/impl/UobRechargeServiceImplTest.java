package com.pivot.aham.api.service.service.impl;

import com.google.common.collect.Lists;
import com.pivot.aham.api.service.bean.RechargeRefundBean;
import com.pivot.aham.api.service.service.UobRechargeService;
import com.pivot.aham.common.core.support.cache.RedissonHelper;
import com.pivot.aham.common.core.support.email.Email;
import com.pivot.aham.common.core.support.file.excel.ExportExcel;
import com.pivot.aham.common.core.util.EmailUtil;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.PropertiesUtil;
import com.pivot.aham.common.enums.CurrencyEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.activation.DataHandler;
import javax.annotation.Resource;
import javax.mail.BodyPart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UobRechargeServiceImplTest {

    @Resource
    private UobRechargeService uobRechargeService;
    @Resource
    private RedissonHelper redissonHelper;

    @Test
    public void notifyRechargeRefund() {
        List<RechargeRefundBean> needRefundUsers = Lists.newArrayList();
        RechargeRefundBean rechargeRefundBean1 = new RechargeRefundBean();
        rechargeRefundBean1.setAmount(new BigDecimal("5000"));
        rechargeRefundBean1.setBankOrderNumber("UOBCL051 20190703");
        rechargeRefundBean1.setBankProvidedName("UOB");
        rechargeRefundBean1.setClientId("1096");
        rechargeRefundBean1.setClientName("Wong Choon Fei");
        rechargeRefundBean1.setCurrency(CurrencyEnum.SGD.getDesc());
        rechargeRefundBean1.setTradeTime(DateUtils.now().toString());
        rechargeRefundBean1.setVirtualAccountNo("86071001045");


        RechargeRefundBean rechargeRefundBean2 = new RechargeRefundBean();
        rechargeRefundBean2.setAmount(new BigDecimal("157"));
        rechargeRefundBean2.setBankOrderNumber("873L047 20190702");
        rechargeRefundBean2.setBankProvidedName("UOB");
        rechargeRefundBean2.setClientId("1084");
        rechargeRefundBean2.setClientName("Ong Dinar Tirta Suraja");
        rechargeRefundBean2.setCurrency(CurrencyEnum.SGD.getDesc());
        rechargeRefundBean2.setTradeTime(DateUtils.now().toString());
        rechargeRefundBean2.setVirtualAccountNo("86071001049");


        needRefundUsers.add(rechargeRefundBean1);
        needRefundUsers.add(rechargeRefundBean2);



        log.info("充值用户退款,date:{}", DateUtils.getDate());
        try {
            ExportExcel exportExcel = new ExportExcel(null, RechargeRefundBean.class);
            exportExcel.setDataList(needRefundUsers);
            String fileName = "recharge_refund.xlsx";
            String topic = "recharge_need_refund_" + DateUtils.getDate();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            exportExcel.write(os);

            BodyPart bodyPart = new MimeBodyPart();
            ByteArrayDataSource dataSource = new ByteArrayDataSource(os.toByteArray(), "application/png");
            bodyPart.setDataHandler(new DataHandler(dataSource));
            bodyPart.setFileName(fileName);

            Email email = new Email();
            email.setBodyPart(bodyPart);
            email.setSendTo(PropertiesUtil.getString("email.recharge.refund"));
            email.setTopic(topic);
            email.setBody(DateUtils.getDate() + ",充值退款看附件");

            EmailUtil.sendEmail(email);
            exportExcel.dispose();

        } catch (Exception e) {
            log.error("充值用户退款,异常", e);
        }
        log.info("充值用户退款成功,date:{}", DateUtils.getDate());
    }



    @Test
    public void notifyRechargeRefundByRabbitmq() {
        List<RechargeRefundBean> needRefundUsers = Lists.newArrayList();
        RechargeRefundBean rechargeRefundBean1 = new RechargeRefundBean();
        rechargeRefundBean1.setAmount(new BigDecimal("5000"));
        rechargeRefundBean1.setBankOrderNumber("UOBCL051 20190703");
        rechargeRefundBean1.setBankProvidedName("UOB");
        rechargeRefundBean1.setClientId("1096");
        rechargeRefundBean1.setClientName("Wong Choon Fei");
        rechargeRefundBean1.setCurrency(CurrencyEnum.SGD.getDesc());
        rechargeRefundBean1.setTradeTime(DateUtils.now().toString());
        rechargeRefundBean1.setVirtualAccountNo("86071001045");

        RechargeRefundBean rechargeRefundBean2 = new RechargeRefundBean();
        rechargeRefundBean2.setAmount(new BigDecimal("157"));
        rechargeRefundBean2.setBankOrderNumber("873L047 20190702");
        rechargeRefundBean2.setBankProvidedName("UOB");
        rechargeRefundBean2.setClientId("1084");
        rechargeRefundBean2.setClientName("Ong Dinar Tirta Suraja");
        rechargeRefundBean2.setCurrency(CurrencyEnum.SGD.getDesc());
        rechargeRefundBean2.setTradeTime(DateUtils.now().toString());
        rechargeRefundBean2.setVirtualAccountNo("86071001049");

        needRefundUsers.add(rechargeRefundBean1);
        needRefundUsers.add(rechargeRefundBean2);

        uobRechargeService.notifyRechargeRefund(needRefundUsers);



    }

    @Test
    public void testTry(){
        redissonHelper.setnx("test1123",0,100);
        Long tryTimes = redissonHelper.incr("test1123");
        if(tryTimes>3){
            log.error("重试次数已达到3次:{}","test1123");
        }
    }
}
