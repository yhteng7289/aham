package com.pivot.aham.api.service;
import com.pivot.aham.common.core.support.jwt.JwtUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月11日
 */
@RunWith(SpringRunner.class)
//@SpringBootTest
public class jwtUtilTest {
    @Test
    public void Test() throws Exception {

        JwtUtil.createJwtToken();
    }
}
