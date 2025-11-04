package com.pivot.aham.api.web.app.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.amazonaws.util.json.Jackson;
import com.pivot.aham.api.web.app.dto.reqdto.ForgetPwdDTO;
import com.pivot.aham.api.web.app.dto.reqdto.LoginDTO;
import com.pivot.aham.api.web.app.dto.reqdto.RegisterForFeReqDTO;
import com.pivot.aham.api.web.app.dto.resdto.GoalDetailResDTO;
import com.pivot.aham.api.web.app.dto.resdto.LoginResDTO;
import com.pivot.aham.api.web.app.dto.resdto.RegisterForFeResDTO;
import com.pivot.aham.api.web.app.febase.AppResultCode;
import com.pivot.aham.api.web.app.service.AppService;
import com.pivot.aham.api.web.app.vo.req.ForgetPwdReqVo;
import com.pivot.aham.api.web.app.vo.req.LoginReqVo;
import com.pivot.aham.api.web.app.vo.res.GoalDetailResVo;
import com.pivot.aham.api.web.app.vo.res.LoginResVo;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.support.cache.RedissonHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.ToString;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author YYYz
 */
@ToString
@RestController
@RequestMapping("/api/v1/")
@Api(value = "登录", description = "登录接口")
public class AppLoginController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppLoginController.class);

    public static final String CURRENT_LOGIN_USER_GOALLIST = "current_user_goalList";

    @Resource
    private RedissonHelper redissonHelper;
    @Resource
    private AppService appService;

    @PostMapping("app/register")
    @ApiOperation(value = "登录接口", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message<Void> register(@RequestBody RegisterForFeReqDTO registerForFeReqDTO, HttpServletRequest request) throws Exception {
        LOGGER.info("registration,请求参数,registerForFeReqDTO:{}", JSON.toJSON(registerForFeReqDTO));        
        if (registerForFeReqDTO.getPr().toLowerCase().startsWith("y")) {
            registerForFeReqDTO.setPr("Yes");
        } else {
            registerForFeReqDTO.setPr("No");
        }        
        RegisterForFeResDTO registerForFeResDTO = appService.register(registerForFeReqDTO);
        if (registerForFeResDTO != null) {
            if (registerForFeResDTO.getResultCode() != null && String.valueOf(AppResultCode.OK.value()).equals(registerForFeResDTO.getResultCode())) {
                return Message.success("Registration successful");
            } else {
                return Message.error(registerForFeResDTO.getErrorMsg());
            }
        } else {
            return Message.error("Registration fail");
        }
    }

    @PostMapping("app/login")
    @ApiOperation(value = "登录接口", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message<LoginResVo> login(@RequestBody LoginReqVo loginReqVo, HttpServletRequest request) throws Exception {
        LOGGER.info("登录获取OTP,请求参数,getOTPReqVo:{}", JSON.toJSON(loginReqVo));
        LoginDTO loginDTO = loginReqVo.convertToDto(loginReqVo);
        LoginResDTO loginResDTO = appService.login(loginDTO);
        if (loginResDTO != null) {
            if (loginResDTO.getResultCode() != null && String.valueOf(AppResultCode.OK.value()).equals(loginResDTO.getResultCode())) {
                LoginResVo loginResVo = new LoginResVo();
                loginResVo.setClientId(loginResDTO.getClientId().trim())
                        .setFirstName(loginResDTO.getFirstName().trim())
                        .setLastName(loginResDTO.getLastName().trim())
                        .setVirtualAcctNoSgd(loginResDTO.getVirtualAcctNoSgd().trim())
                        .setGoalList(convertToVo(loginResDTO.getGoalList()))
                        .setPortfolioId(loginResDTO.getPortfolioId().trim())
                        .setRisk(loginResDTO.getPortfolioId().substring(3, 4).trim())
                        .setToken(DigestUtil.md5Hex(loginResDTO.getClientId()).trim());
                super.setLoginUser(request, loginResDTO.getClientId(), loginResDTO.getVirtualAcctNoSgd(),
                        loginReqVo.getPassword(), loginReqVo.getPhoneNumber(), loginResDTO.getPortfolioId());
                String listJson = Jackson.toJsonString(loginResDTO.getGoalList());
                redissonHelper.set(CURRENT_LOGIN_USER_GOALLIST + "_" + loginResDTO.getClientId(), listJson, 3600);
                return Message.success(loginResVo);
            } else {
                if (String.valueOf(AppResultCode.OK.value()).equals(loginResDTO.getResultCode())) {
                    return Message.error("");
                }
                if (String.valueOf(AppResultCode.OK.value()).equals(loginResDTO.getResultCode())) {
                    return Message.error("");
                }
                return Message.error(loginResDTO.getErrorMsg());
            }
        } else {
            return Message.error("login失败");
        }
    }

    @PostMapping("app/forgetPwd")
    @ApiOperation(value = "忘记密码", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message forgetPwd(@RequestBody ForgetPwdReqVo forgetPwdReqVo) throws Exception {
        LOGGER.info("登录获取OTP,请求参数,getOTPReqVo:{}", JSON.toJSON(forgetPwdReqVo));
        ForgetPwdDTO forgetPwdDTO = forgetPwdReqVo.convertToDto(forgetPwdReqVo);
        Integer resultCode = appService.forgetPwd(forgetPwdDTO);
        if (resultCode != null && AppResultCode.OK.value().equals(resultCode)) {
            return Message.success();
        } else {
            return Message.error("请求失败");
        }
    }

    public List<GoalDetailResVo> convertToVo(List<GoalDetailResDTO> goalDetailResDTOs) {
        List<GoalDetailResVo> goalDetailResVoList = Lists.newArrayList();
        for (GoalDetailResDTO goalDetailResDTO : goalDetailResDTOs) {
            GoalDetailResVo goalDetailResVo = new GoalDetailResVo();
            goalDetailResVo.setGoalId(goalDetailResDTO.getGoalId().trim())
                    .setRefCode(goalDetailResDTO.getRefCode().trim());
            goalDetailResVoList.add(goalDetailResVo);
        }
        return goalDetailResVoList;
    }
}
