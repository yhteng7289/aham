//package com.pivot.aham.api.web.web;
//
//import cn.hutool.crypto.digest.DigestUtil;
//import com.google.common.collect.Maps;
//import MemberDTO;
//import MemberServiceRemoteService;
//import com.pivot.aham.api.web.web.vo.*;
//import Constants;
//import AbstractController;
//import Message;
//import LoginException;
//import com.pivot.aham.common.core.util.*;
//import SessionUser;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
///**
// * 用户登录
// *
// * @author addison
// * @version 2016年5月20日 下午3:11:21
// */
//@RestController
//@RequestMapping("/web/")
//@Api(value = "APP登录注册接口", description = "APP-登录注册接口")
//public class LoginController extends AbstractController {
//
//    @Resource
//    private MemberServiceRemoteService memberServiceRemoteService;
//
//
//    @PostMapping("reginit.api")
//    @ApiOperation(value = "注册", produces = MediaType.APPLICATION_JSON_VALUE, notes = "" + "使用手机号+验证码进行注册或登录\n"
//    + "注册接口需要以下四个参数：\n" + "1. token: 客户端生成的唯一ID，用作用户令牌，服务端用之识别用户，验证权限，每接口必传\n"
//    + "2. sign: 请求参数关键字自然排序后的MD5HEX签名，用于防止请求参数被第三方拦截篡改，每接口必传。签名算法查看密钥接口\n"
//    + "3. username: 用户名，目前只支持手机号\n" + "4. password: 密码\n"
//    + "注意：所有接口都需要传token、sign、timestamp参数，token用作令牌，sign用作签名")
//    public Message register(@RequestBody MemberRegReqVo memberVo) throws Exception {
//        Map<String, Object> params = Maps.newHashMap();
//        params.put("username",memberVo.getUsername());
//        List<MemberDTO> members = null;
//        MemberDTO member = members.isEmpty() ? null : members.get(0);
//
//        if (member == null) {
//            MemberDTO param = new MemberDTO();
//            param.setMobile(memberVo.getUsername());
//            param.setPassword(DigestUtil.md5Hex(memberVo.getPassword()));
//            memberServiceRemoteService.updateOrInsert(param);
//            return Message.success();
//        } else {
//            throw new IllegalArgumentException("手机号已注册.");
//        }
//    }
//
//    @PostMapping("login.api")
//    @ApiOperation(value = "登录", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Message<MemberLoginResVo> login(@RequestBody MemberLoginReqVo memberVo) {
//        Map<String, Object> params = Maps.newHashMap();
//        params.put("username", memberVo.getUsername());
//        List<MemberDTO> members = null;
//        MemberDTO member = members.isEmpty() ? null : members.get(0);
//
//        if (member == null) {
//            throw new LoginException("手机号或密码错误.");
//        } else {
//            if (DigestUtil.md5Hex(memberVo.getPassword()).equals(member.getPassword())) {
////                memberServiceRemoteService.updateOrInsert(member);
//
//                SessionUser sessionUser = new SessionUser(member.getId(),member.getRealName(),member.getMobile(),false);
//                String token = UUID.randomUUID().toString();
//                String tokenKey = DigestUtil.md5Hex(token);
//                CacheUtil.getCache().set(Constants.TOKEN_KEY + tokenKey, sessionUser,
//                PropertiesUtil.getInt("APP-TOKEN-EXPIRE", 60 * 60 * 24 * 5));
//                MemberLoginResVo memberLoginVo = new MemberLoginResVo();
//                memberLoginVo.setToken(token);
//                return Message.success(memberLoginVo);
//            } else {
//                throw new LoginException("手机号或密码错误");
//            }
//        }
//    }
//
//    @PostMapping("logout.api")
//    @ApiOperation(value = "APP会员登出", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Message logout(HttpServletRequest request) {
//        String token = request.getHeader(Constants.TOKEN_NAME);
//        AssertUtil.notNull(token, "TOKEN");
//        if (StringUtils.isNotBlank(token)) {
//            String tokenKey = DigestUtil.md5Hex(token);
//            CacheUtil.getCache().del(Constants.TOKEN_KEY + tokenKey);
//        }
//        Long id = getCurrUser(request);
//        if (DataUtil.isNotEmpty(id)) {
//            MemberDTO member = new MemberDTO();
//            member.setId(getCurrUser(request));
//            memberServiceRemoteService.updateOrInsert(member);
//        }
//        return Message.success();
//    }
//
//    @PostMapping("updatePwd.api")
//    @ApiOperation(value = "修改密码", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Message updatePwd(@RequestBody MemberUpdatePwdReqVo memberVo
//    ) throws Exception {
//        Map<String, Object> params = Maps.newHashMap();
//        params.put("username", memberVo.getUsername());
//        List<?> members = null;
//        MemberDTO member = members.isEmpty() ? null : (MemberDTO)members.get(0);
//
//        if (member == null) {
//            throw new IllegalArgumentException("手机号还没有注册.");
//        } else {
//            member.setPassword(DigestUtil.md5Hex(memberVo.getPassword()));
//            memberServiceRemoteService.updateOrInsert(member);
//            return Message.success();
//        }
//    }
//}
