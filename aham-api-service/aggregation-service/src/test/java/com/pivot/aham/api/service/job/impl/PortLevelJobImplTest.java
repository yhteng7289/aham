package com.pivot.aham.api.service.job.impl;

import com.pivot.aham.api.service.job.PortLevelJob;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * Created by luyang.li on 18/12/28.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PortLevelJobImplTest {

    @Resource
    private PortLevelJob portLevelJob;

    @Test
    public void synchroPortLevel() throws Exception {
        portLevelJob.synchroPortLevel();
    }

}