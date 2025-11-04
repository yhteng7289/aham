package com.pivot.aham.api.web.app.controller;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.server.dto.req.ExchangeRateDTO;
import com.pivot.aham.api.server.dto.res.ExchangeRateResDTO;
import com.pivot.aham.api.server.remoteservice.ExchangeRemoteService;
import com.pivot.aham.api.web.app.dto.reqdto.ChangeRiskReqDTO;
import com.pivot.aham.api.web.app.dto.reqdto.ClientInfoDTO;
import com.pivot.aham.api.web.app.dto.reqdto.LoginDTO;
import com.pivot.aham.api.web.app.dto.reqdto.ReferDTO;
import com.pivot.aham.api.web.app.dto.resdto.ChangeRiskResDTO;
import com.pivot.aham.api.web.app.dto.resdto.ClientInfoResDTO;
import com.pivot.aham.api.web.app.dto.resdto.LoginResDTO;
import com.pivot.aham.api.web.app.dto.resdto.ReferResDTO;
import com.pivot.aham.api.web.app.febase.AppResultCode;
import com.pivot.aham.api.web.app.service.AppService;
import com.pivot.aham.api.web.app.vo.req.ChangeRiskReqVo;
import com.pivot.aham.api.web.app.vo.req.ClientInfoReqVo;
import com.pivot.aham.api.web.app.vo.req.ReferReqVo;
import com.pivot.aham.api.web.app.vo.req.SendFeedbackReqVo;
import com.pivot.aham.api.web.app.vo.res.ChangeRiskResVo;
import com.pivot.aham.api.web.app.vo.res.ClientInfoResVo;
import com.pivot.aham.api.web.app.vo.res.FriendResVo;
import com.pivot.aham.api.web.app.vo.res.ReferResVo;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.support.cache.RedissonHelper;
import com.pivot.aham.common.core.support.email.Email;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.EmailUtil;
import com.pivot.aham.common.core.util.InstanceUtil;
import com.pivot.aham.common.enums.ExchangeRateTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author YYYz
 */
@RestController
@RequestMapping("/api/v1/")
@Api(value = "用户", description = "用户接口")
public class AppUserController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppUserController.class);

    public static final String CURRENT_SQUIRREL_CASH_SGD = "client_squirrelCashSGD";

    public static final String CURRENT_LOGIN_USER_PORTFOLIOID = "current_user_portfolioId";

    @Resource
    private AppService appService;
    @Resource
    private RedissonHelper redissonHelper;

    @Resource
    private ExchangeRemoteService exchangeRemoteService;


    @PostMapping("app/refer")
    @ApiOperation(value = "分享", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message<ReferResVo> refer(@RequestBody ReferReqVo referReqVo) throws Exception {
        if (!checkLogin(referReqVo.getClientId())) {
            return Message.error(AppResultCode.UNAUTHORIZED.value(), AppResultCode.UNAUTHORIZED.msg());
        }
        LOGGER.info("分享接口,请求参数,getOTPReqVo:{}", JSON.toJSON(referReqVo));
        ReferDTO clientInfoDTO = referReqVo.convertToDto(referReqVo);
        ReferResDTO referResDTO = appService.refer(clientInfoDTO);
        List<FriendResVo> friendResVos = Lists.newArrayList();
        FriendResVo friendResVo = new FriendResVo();
        friendResVo.setUrl("1231213").setFriendFirstName("123").setFriendLastName("123")
                .setStatus("123").setClientId("123");
        friendResVos.add(friendResVo);
        ReferResVo referResVo = new ReferResVo();
        referResVo.setFriendList(friendResVos);
        return Message.success(referResVo);
    }

    @PostMapping("app/changeRisk")
    @ApiOperation(value = "修改风险等级", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message<ChangeRiskResVo> changeRisk(@RequestBody ChangeRiskReqVo changeRiskReqVo) throws Exception {
        if (!checkLogin(changeRiskReqVo.getClientId())) {
            return Message.error(AppResultCode.UNAUTHORIZED.value(), AppResultCode.UNAUTHORIZED.msg());
        }
        LOGGER.info("修改风险等级,请求参数,changeRiskReqVo:{}", JSON.toJSON(changeRiskReqVo));
        ChangeRiskReqDTO changeRiskReqDTO = new ChangeRiskReqDTO();
        changeRiskReqDTO.setClientId(changeRiskReqVo.getClientId());
        String oldPortfolioId = redissonHelper.get(CURRENT_LOGIN_USER_PORTFOLIOID + "_" + changeRiskReqVo.getClientId());
        String newPortfolioId = oldPortfolioId.substring(0, 3) + changeRiskReqVo.getRisk() + oldPortfolioId.substring(4, 6);
        changeRiskReqDTO.setPortfolioId(newPortfolioId);
        ChangeRiskResDTO changeRiskResDTO = appService.changeRisk(changeRiskReqDTO);
        if (changeRiskResDTO != null) {
            if (changeRiskResDTO.getResultCode().equals(String.valueOf(AppResultCode.OK.value()))) {
                redissonHelper.set(CURRENT_LOGIN_USER_PORTFOLIOID + "_" + changeRiskReqVo.getClientId(), newPortfolioId, 3600);
                return Message.success();
            } else {
                return Message.error(changeRiskResDTO.getErrorMsg());
            }
        } else {
            return Message.error("Change Risk Failed !");
        }
    }

    @PostMapping("app/sendFeedback")
    @ApiOperation(value = "提交反馈", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message sendFeedback(@RequestBody SendFeedbackReqVo sendFeedbackReqVo) throws Exception {
        if (!checkLogin(getClientId())) {
            return Message.error(AppResultCode.UNAUTHORIZED.value(), AppResultCode.UNAUTHORIZED.msg());
        }
        LOGGER.info("提交反馈,请求参数,changeRiskReqVo:{}", JSON.toJSON(sendFeedbackReqVo));
        Email email = new Email()
                .setTemplateName("SendFeedback")
                .setTemplateVariables(InstanceUtil.newHashMap("sendFeedbackReqVo", sendFeedbackReqVo))
                .setSendTo("clientservices@aham.com.sg")
                .setTopic("Contact Us");
        EmailUtil.sendEmail(email);
        return Message.success();
    }

    @PostMapping("app/getClientInfo")
    @ApiOperation(value = "获取用户信息", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message<ClientInfoResVo> getClientInfo(@RequestBody ClientInfoReqVo clientInfoReqVo) throws Exception {
        if (!checkLogin(clientInfoReqVo.getClientId())) {
            return Message.error(AppResultCode.UNAUTHORIZED.value(), AppResultCode.UNAUTHORIZED.msg());
        }
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhonenumber(getPhoneNum(clientInfoReqVo.getClientId()));
        loginDTO.setPwd(getPwd(clientInfoReqVo.getClientId()));
        LoginResDTO loginResDTO = appService.login(loginDTO);
        BigDecimal currency = BigDecimal.ZERO;
        String currencyDate = "";
        ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO();
        exchangeRateDTO.setExchangeRateType(ExchangeRateTypeEnum.SAXO_FXRT2);
        RpcMessage<ExchangeRateResDTO> resDTORpcMessage = exchangeRemoteService.getLastExchangeRate(exchangeRateDTO);
        if (resDTORpcMessage.isSuccess()) {
            if (resDTORpcMessage.getContent() != null) {
                currency = resDTORpcMessage.getContent().getUsdToSgd();
                currencyDate = DateUtils.formatDate(resDTORpcMessage.getContent().getRateDate(), DateUtils.DATE_FORMAT3);
            }
        }
        LOGGER.info("获取用户信息,请求参数,clientInfoReqVo:{}", JSON.toJSON(clientInfoReqVo));
        ClientInfoDTO clientInfoDTO = clientInfoReqVo.convertToDto(clientInfoReqVo);
        ClientInfoResDTO clientInfoResDTO = appService.getClientInfo(clientInfoDTO);
        if (clientInfoResDTO != null && loginResDTO != null) {
            ClientInfoResVo clientInfoResVo = new ClientInfoResVo();
            clientInfoResVo.setFirstName(clientInfoResDTO.getFirstName())
                    .setLastName(clientInfoResDTO.getLastName())
                    .setGender(clientInfoResDTO.getGender())
                    .setCitizenShip(clientInfoResDTO.getCitizenShip())
                    .setNric(clientInfoResDTO.getNric())
                    .setDob(clientInfoResDTO.getDob())
                    .setSingaporePr(clientInfoResDTO.getSingaporePr())
                    .setEmail(clientInfoResDTO.getEmail())
                    .setHomeNumber(clientInfoResDTO.getHomeNumber())
                    .setMobileNumber(clientInfoResDTO.getMobileNumber())
                    .setAddressLine1(clientInfoResDTO.getAdressline1())
                    .setAddressLine2(clientInfoResDTO.getAdressline2())
                    .setPostalCode(clientInfoResDTO.getPostalCode())
                    .setClientId(clientInfoResDTO.getClientId())
                    .setRisk(loginResDTO.getPortfolioId().substring(3, 4))
                    .setPortfolioId(loginResDTO.getPortfolioId())
                    .setCurrency(currency)
                    .setCurrencyDate(currencyDate)
                    .setSquirrelCashSGD(redissonHelper.get(CURRENT_SQUIRREL_CASH_SGD + "_" + clientInfoReqVo.getClientId()));
            return Message.success(clientInfoResVo);
        } else {
            return Message.error("请求失败");
        }
    }
}
