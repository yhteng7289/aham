package com.pivot.aham.api.service;

import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.core.util.UploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月27日
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SFtpTest {
    @Test
    public void test(){
        UploadUtil.remove2Sftp("/s","12");

    }
    @Test
    public void test01(){
        ErrorLogAndMailUtil.logNotice(log,"测试");
    }
}
