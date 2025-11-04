package com.pivot.aham.api.web.admin.core;

import cn.hutool.crypto.digest.DigestUtil;
import com.pivot.aham.admin.server.dto.SysSessionDTO;
import com.pivot.aham.admin.server.dto.SysUserDTO;
import com.pivot.aham.admin.server.remoteservice.SysAuthorizeRemoteService;
import com.pivot.aham.admin.server.remoteservice.SysSessionRemoteService;
import com.pivot.aham.admin.server.remoteservice.SysUserRemoteService;
import com.pivot.aham.common.config.shiro.Realm;
import com.pivot.aham.common.config.shiro.ShiroRedisSessionDAO;
import com.pivot.aham.common.model.SessionUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 权限实现
 *
 * @author addison
 * @since 2018年11月15日
 */
@Component
public class AuthorizeRealm extends AuthorizingRealm implements Realm {

    private final Logger logger = LogManager.getLogger();
    @Resource
    private SysAuthorizeRemoteService sysAuthorizeRemoteService;
    @Resource
    private SysUserRemoteService sysUserRemoteService;
    @Resource
    private SysSessionRemoteService sysSessionRemoteService;

    private ShiroRedisSessionDAO sessionDAO;

    @Override
    public void setSessionDAO(ShiroRedisSessionDAO sessionDAO) {
        this.sessionDAO = sessionDAO;
    }

    /**
     * 权限分配
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        SessionUser user = (SessionUser) principals.getPrimaryPrincipal();
        List<?> list = sysAuthorizeRemoteService.queryPermissionByUserId(user.getId());
        for (Object permission : list) {
            if (StringUtils.isNotBlank((String) permission)) {
                // 添加基于Permission的权限信息
                info.addStringPermission((String) permission);
            }
        }
        // 添加用户权限
        info.addStringPermission("user");
        return info;
    }

    /**
     * 登录校验
     *
     * @param authcToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
            throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;

        SysUserDTO sysUserDTO = new SysUserDTO();
        sysUserDTO.setUserName(token.getUsername());
//        sysUserDTO.setEnable(1);

        List<SysUserDTO> list = sysUserRemoteService.queryList(sysUserDTO);
        if (list.size() == 1) {
            SysUserDTO user = list.get(0);

            String passwordStr = new String(token.getPassword());
            if (user.getPassword().equals(DigestUtil.md5Hex(passwordStr))) {
                SessionUser sessionUser = new SessionUser(user.getId(), user.getRealName(), user.getMobile(),
                        token.isRememberMe());
                saveSession(user.getRealName(), token.getHost());
                AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(sessionUser, passwordStr,
                        user.getRealName());
                return authcInfo;
            }
            logger.error("用户 [{}] 密码错误: {}", token.getUsername(), passwordStr);
            return null;
        } else {
            logger.error("用户不存在: {}", token.getUsername());
            return null;
        }
    }

    /**
     * 保存session
     *
     * @param account
     * @param host
     */
    private void saveSession(String account, String host) {
        // 踢出用户
        SysSessionDTO record = new SysSessionDTO();
        record.setAccount(account);
        List<?> sessionIds = sysSessionRemoteService.querySessionIdByAccount(record);
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        String currentSessionId = session.getId().toString();
        if (sessionIds != null) {
            for (Object sessionId : sessionIds) {
                record.setSessionId((String) sessionId);
                sysSessionRemoteService.deleteBySessionId(record);
                if (!currentSessionId.equals(sessionId)) {
                    sessionDAO.delete((String) sessionId);
                }
            }
        }
        // 保存用户session到数据库
        record.setSessionId(currentSessionId);
        record.setIp(StringUtils.isBlank(host) ? session.getHost() : host);
        record.setStartTime(session.getStartTimestamp());
        sysSessionRemoteService.updateOrInsert(record);
    }

    public static void main(String[] args) {
        String hex = DigestUtil.md5Hex("123456");
        System.out.println(hex);
    }
}
