package com.pivot.aham.api.service;

import com.pivot.aham.api.service.job.custstatment.impl.CustomerStatementJobImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestCustomerStatement {

    @Resource
    private CustomerStatementJobImpl customerStatementJob;

    @Test
    public void test(){
        customerStatementJob.calculateCustomerStatement(null,null);

    }

}
