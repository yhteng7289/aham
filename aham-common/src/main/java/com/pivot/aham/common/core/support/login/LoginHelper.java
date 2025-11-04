package com.pivot.aham.common.core.support.login;

import com.pivot.aham.common.core.exception.LoginException;
import com.pivot.aham.common.core.support.context.Resources;
import com.pivot.aham.common.model.Login;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

/**
 * 登录判断类
 *
 * @author addison
 * @since 2018年11月16日
 */
@Slf4j
public final class LoginHelper {
    private LoginHelper() {
    }

    /** 用户登录 */
    public static final Boolean login(Login user, String host) {
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword(), host);
        token.setRememberMe(user.getRememberMe());
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
            return subject.isAuthenticated();
        } catch (LockedAccountException e) {
            log.error("登录异常",e);
            throw new LoginException(Resources.getMessage("ACCOUNT_LOCKED", token.getPrincipal()));
        } catch (DisabledAccountException e) {
            log.error("登录异常",e);
            throw new LoginException(Resources.getMessage("ACCOUNT_DISABLED", token.getPrincipal()));
        } catch (ExpiredCredentialsException e) {
            log.error("登录异常",e);
            throw new LoginException(Resources.getMessage("ACCOUNT_EXPIRED", token.getPrincipal()));
        } catch (Exception e) {
            log.error("登录异常",e);
            throw new LoginException(Resources.getMessage("LOGIN_FAIL"), e);
        }
    }
}
