package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.model.AccountUserPO;
import com.pivot.aham.api.service.service.SaxoAccountOrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * Created by luyang.li on 2018/12/24.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SaxoAccountOrderServiceImplTest {

    @Resource
    private SaxoAccountOrderService saxoAccountOrderService;

    @Test
    public void queryAccountSgdMoney() {
        AccountUserPO accountUserPO = new AccountUserPO();
        accountUserPO.setAccountId(1118786610627424258L);
//        saxoAccountOrderService.queryAccountSgdMoney(accountUserPO);
    }
}