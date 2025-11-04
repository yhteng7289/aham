package com.pivot.aham.api.service.job.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BankRefundGuaranteeJobImplTest {


    @Resource
    private BankRefundGuaranteeJobImpl bankRefundGuaranteeJob;

    @Test
    public void testExecute(){
        bankRefundGuaranteeJob.execute(null);
    }
}
