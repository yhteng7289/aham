package com.pivot.aham.api.service;

import com.pivot.aham.api.server.dto.AccountNavDTO;
import com.pivot.aham.common.core.support.generator.Sequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitmqTest {
    @Resource
    private RabbitTemplate pubConfirmTemplate;
    @Test
    public void testSend(){
        AccountNavDTO rabbitmqTest = new AccountNavDTO();
        pubConfirmTemplate.convertAndSend("test", "",rabbitmqTest, message -> {
             message.getMessageProperties().setMessageId("test#"+ Sequence.next());
             return message;
        },new CorrelationData("test#"+ Sequence.next()));


        pubConfirmTemplate.convertAndSend("test", "","string11122333444", message -> {
            message.getMessageProperties().setMessageId("test#"+ Sequence.next());
            return message;
        },new CorrelationData("test#"+ Sequence.next()));

        System.out.println("==================");


    }


}
