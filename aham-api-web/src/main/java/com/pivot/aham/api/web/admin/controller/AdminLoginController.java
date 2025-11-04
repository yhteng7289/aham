package com.pivot.aham.api.web.admin.controller;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.admin.server.dto.SysSessionDTO;
import com.pivot.aham.admin.server.remoteservice.SysAuthorizeRemoteService;
import com.pivot.aham.admin.server.remoteservice.SysSessionRemoteService;
import com.pivot.aham.admin.server.remoteservice.SysUserRemoteService;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.MessageStandardCode;
import com.pivot.aham.common.core.exception.LoginException;
import com.pivot.aham.common.core.support.context.Resources;
import com.pivot.aham.common.core.support.login.LoginHelper;
import com.pivot.aham.common.core.support.validate.ValidationResult;
import com.pivot.aham.common.core.support.validate.ValidationUtils;
import com.pivot.aham.common.core.util.WebUtil;
import com.pivot.aham.common.model.Login;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 登录接口
 *
 * @author addison
 * @since 2018年11月15日
 */
@CrossOrigin()
@RestController
@RequestMapping(value = "/api/v1/in/shiro")
@Api(value = "登录接口", description = "登录相关接口")
public class AdminLoginController extends AbstractController {

    @Resource
    private SysUserRemoteService sysUserService;
    @Resource
    private SysAuthorizeRemoteService sysAuthorizeService;
    @Resource
    private SysSessionRemoteService sysSessionRemoteService;

    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public Message login(@ApiParam(required = true, value = "登录帐号和密码") @RequestBody Login user,
            HttpServletRequest request) {
        //验证入参
        ValidationResult validationResult = ValidationUtils.validateEntity(user);
        if (validationResult.isHasErrors()) {
            throw new IllegalArgumentException(JSON.toJSONString(validationResult.getErrorMsg()));
        }
        String clientIp = WebUtil.getHost(request);
        if (LoginHelper.login(user, clientIp)) {
            // Login operation_activityLog
            return Message.success();
        }
        throw new LoginException(Resources.getMessage("LOGIN_FAIL"));
    }

    @ApiOperation(value = "用户登出")
    @PostMapping("/logout")
    public Message logout() {
        //删除session
        SysSessionDTO sysSessionDTO = new SysSessionDTO();
        sysSessionDTO.setSessionId((String) SecurityUtils.getSubject().getSession().getId());
        sysSessionRemoteService.deleteBySessionId(sysSessionDTO);
        SecurityUtils.getSubject().logout();
        // Logout operation_activityLog
        return Message.success();
    }

//    @ApiOperation(value = "用户注册")
//    @PostMapping("/regin")
//    public Message regin(@RequestBody @Valid ReginSysUserReqVo sysUser) {
//        sysUser.setPassword(DigestUtil.md5Hex(sysUser.getPassword()));
//        SysUserDTO sysUserDTO = BeanMapperUtils.map(sysUser,SysUserDTO.class);
//        SysUserDTO dto = sysUserService.updateOrInsert(sysUserDTO);
//
//        List<Long> roleIdList = sysUser.getRoleIdList();
//        List<SysUserRoleDTO> listDto = Lists.newArrayList();
//        for(Long roleId:roleIdList){
//            SysUserRoleDTO sysUserRoleDTO = new SysUserRoleDTO();
//            sysUserRoleDTO.setRoleId(roleId);
//            sysUserRoleDTO.setUserId(dto.getId());
//            listDto.add(sysUserRoleDTO);
//        }
//        sysAuthorizeService.updateUserRole(listDto);
//
//
//        return Message.success();
//    }
    /**
     * 未登录
     *
     * @return
     * @throws Exception
     */
    @ApiIgnore
    @RequestMapping(value = "/unauthorized", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
    public Message unauthorized() throws Exception {
        return Message.error(MessageStandardCode.UNAUTHORIZED, null);
    }

    /**
     * 没有权限
     *
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/forbidden", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
    public Message forbidden() {
        return Message.error(MessageStandardCode.FORBIDDEN, null);
    }
}
