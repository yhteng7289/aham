package com.pivot.aham.api.web.h5.controller;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.server.dto.CheckOTPDTO;
import com.pivot.aham.api.server.dto.GetOTPDTO;
import com.pivot.aham.api.server.dto.NewSysUserDTO;
import com.pivot.aham.api.server.dto.PersonalInfoNotUploadDTO;
import com.pivot.aham.api.server.dto.PersonalInfoUploadDTO;
import com.pivot.aham.api.server.dto.app.resdto.RegisterResDTO;
import com.pivot.aham.api.server.dto.app.resdto.TaxResDTO;
import com.pivot.aham.api.server.remoteservice.AppRemoteService;
import com.pivot.aham.api.web.h5.vo.req.GetCostReqVo;
import com.pivot.aham.api.web.h5.vo.req.GetSuggestMoneyReqVo;
import com.pivot.aham.api.web.h5.vo.res.GetCostResVo;
import com.pivot.aham.api.web.h5.vo.res.GetSuggestMoneyResVo;
import com.pivot.aham.api.web.app.dto.reqdto.RegisterForFeReqDTO;
import com.pivot.aham.api.web.app.dto.reqdto.TaxForFeReqDTO;
import com.pivot.aham.api.web.app.dto.resdto.RegisterForFeResDTO;
import com.pivot.aham.api.web.app.febase.AppResultCode;
import com.pivot.aham.api.web.app.service.AppService;
import com.pivot.aham.api.web.web.vo.req.CheckOTPReqVo;
import com.pivot.aham.api.web.web.vo.req.GetOTPReqVo;
import com.pivot.aham.api.web.web.vo.req.PersonalInfoNotUploadReqVo;
import com.pivot.aham.api.web.web.vo.req.PersonalInfoUploadReqVo;
import com.pivot.aham.api.web.web.vo.res.GetCountriesResVo;
import com.pivot.aham.api.web.web.vo.req.LoginOTPReqVo;
import com.pivot.aham.api.web.web.vo.req.LoginCheckOTPReqVo;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
import com.pivot.aham.common.core.util.AwsUtil;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.RiskLevelEnum;
import com.pivot.aham.common.enums.app.CostCountriesTypeEnum;
import com.pivot.aham.common.enums.app.CostCourseTypeEnum;
import com.pivot.aham.common.enums.app.CostTypeEnum;
import com.pivot.aham.common.enums.app.CountriesEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * @author senyang.zheng
 * @date 19/04/12
 * <p>D
 * 提供给用户测评接口
 */
@RestController
@CrossOrigin(value = "*")
@RequestMapping("/api/v1/")
@Api(value = "上传用户个人信息", description = "上传用户个人信息接口")
public class AppPersonalInfoUploadController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppPersonalInfoUploadController.class);

    @Resource
    private AppRemoteService appRemoteService;
    @Resource
    private AppService appService;

    @PostMapping(value = "h5/uploadPersonalInfo")
    @ApiOperation(value = "上传开户信息", produces = MediaType.APPLICATION_JSON_VALUE, notes = "上传开户信息接口")
    public Message uploadPersonalInfo(@RequestBody PersonalInfoUploadReqVo personalInfoUploadReqVo) throws Exception {

        LOGGER.info("uploadPersonalInfo,请求参数,data:{}", JSON.toJSON(personalInfoUploadReqVo));
        PersonalInfoUploadDTO personalInfoUploadDTO = personalInfoUploadReqVo.convertToDto(personalInfoUploadReqVo);
        RpcMessage<String> rpcMessage = appRemoteService.uploadPersonalInfo(personalInfoUploadDTO);
        if (RpcMessageStandardCode.OK.value() == rpcMessage.getResultCode()) {
            return Message.success(rpcMessage.getContent());
        } else {
            return Message.error(rpcMessage.getErrMsg());
        }
    }

    @PostMapping("h5/notUploadPersonalInfo")
    @ApiOperation(value = "开户问题信息", produces = MediaType.APPLICATION_JSON_VALUE, notes = "开户问题信息接口")
    public Message notUploadPersonalInfo(@RequestBody PersonalInfoNotUploadReqVo personalInfoNotUploadReqVo) throws Exception {

        LOGGER.info("notUploadPersonalInfo,请求参数,data:{}", JSON.toJSON(personalInfoNotUploadReqVo));
        PersonalInfoNotUploadDTO personalInfoNotUploadDTO = personalInfoNotUploadReqVo.convertToDto(personalInfoNotUploadReqVo);
        RpcMessage<RegisterResDTO> rpcMessage = appRemoteService.savePersonalInfo(personalInfoNotUploadDTO);
        if (RpcMessageStandardCode.OK.value() == rpcMessage.getResultCode()) {
            RegisterForFeResDTO resDTO = appService.register(buildRegisterForFeReqDTO(rpcMessage.getContent()));
            LOGGER.info("注册请求返回接口:{}", resDTO);
            if (resDTO != null) {
                if (resDTO.getResultCode().equals(String.valueOf(AppResultCode.OK))) {
                    return Message.success(resDTO.getErrorMsg());
                }
                if (resDTO.getResultCode().equals(String.valueOf(AppResultCode.CONFLICT))) {
                    return Message.error(AppResultCode.CONFLICT.value(), resDTO.getErrorMsg());
                }
                return Message.error(resDTO.getErrorMsg());
            } else {
                return Message.error("request error !");
            }
        } else {
            return Message.error("request error !");
        }
    }

    private RegisterForFeReqDTO buildRegisterForFeReqDTO(RegisterResDTO registerResDTO) throws ParseException {
        RegisterForFeReqDTO registerForFeReqDTO = new RegisterForFeReqDTO();
        Date dob = DateUtils.parseDate(registerResDTO.getDob(), DateUtils.DATE_FORMAT3);
        registerForFeReqDTO.setAddress1(registerResDTO.getAddress1()).setAddress2(registerResDTO.getAddress2()).setCitizenship(registerResDTO.getCitizenship()).setCka1(registerResDTO.getCka1())
                .setCka2(registerResDTO.getCka2()).setCka3(registerResDTO.getCka3()).setCountry(registerResDTO.getCountry()).setDob(DateUtils.formatDate(dob, "ddMMyyyy"))
                .setEmail(registerResDTO.getEmail()).setFirstname(registerResDTO.getFirstName()).setHomeno(registerResDTO.getHomeNo()).setImg1(registerResDTO.getImg1()).setImg2(registerResDTO.getImg2())
                .setImg3(registerResDTO.getImg3()).setLastname(registerResDTO.getLastName()).setMobileno(registerResDTO.getMobileNo()).setNationalid(registerResDTO.getNationalId()).setPcountry(registerResDTO.getPCountry())
                .setPdesignaton(registerResDTO.getPDesignation()).setPname(registerResDTO.getPName()).setPortfolioid(registerResDTO.getPortfolioId()).setPostalcode(registerResDTO.getPostalCode()).setPr(registerResDTO.getPr()).setPrelation(registerResDTO.getPRelation())
                .setQ1(registerResDTO.getQ1()).setQ2(registerResDTO.getQ2()).setQ3(registerResDTO.getQ3()).setQ4(registerResDTO.getQ4()).setQ5(registerResDTO.getQ5()).setQ6(registerResDTO.getQ6())
                .setR1(registerResDTO.getR1()).setR2(registerResDTO.getR2()).setR3(registerResDTO.getR3()).setR4(registerResDTO.getR4()).setR5(registerResDTO.getR5()).setR6(registerResDTO.getR6())
                .setRiskprofile(registerResDTO.getRiskProfile()).setS1(registerResDTO.getS1()).setS2(registerResDTO.getS2()).setS3(registerResDTO.getS3()).setSalutation(registerResDTO.getSalutation())
                .setTaxnoList(buildTaxFeReaDTO(registerResDTO.getTaxNoList()));
        return registerForFeReqDTO;
    }

    private List<TaxForFeReqDTO> buildTaxFeReaDTO(List<TaxResDTO> taxNoList) {
        List<TaxForFeReqDTO> taxForFeReqDTOList = Lists.newArrayList();
        for (TaxResDTO taxResDTO : taxNoList) {
            TaxForFeReqDTO taxForFeReqDTO = new TaxForFeReqDTO();
            taxForFeReqDTO.setCountry(taxResDTO.getCountry());
            taxForFeReqDTO.setTaxno(taxResDTO.getTaxno());
            taxForFeReqDTOList.add(taxForFeReqDTO);
        }
        return taxForFeReqDTOList;
    }

    @PostMapping("h5/getCountries")
    @ApiOperation(value = "获取国家信息", produces = MediaType.APPLICATION_JSON_VALUE, notes = "上传开户信息接口")
    public Message getCountries() throws Exception {
        GetCountriesResVo getCountriesResVo = new GetCountriesResVo();
        List<String> countries = Lists.newArrayList();
        for (CountriesEnum countriesEnum : CountriesEnum.values()) {
            countries.add(countriesEnum.getDesc());
        }
        getCountriesResVo.setCountries(countries);
        return Message.success(getCountriesResVo);
    }

    @PostMapping("h5/uploadPersonalImgInfo")
    public Message uploadPersonalImgInfo(MultipartFile frontImg, MultipartFile backImg, MultipartFile passPortImg,
                                         String firstName, String lastName, String mobileNum) throws Exception {

        if (frontImg != null) {
            String uploadFrontImgPath = DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT) + "/" +
                    firstName + lastName + mobileNum + "/" + frontImg.getOriginalFilename();
            AwsUtil.uploadFile(uploadFrontImgPath, frontImg.getInputStream());
        }
        if (backImg != null) {
            String uploadBackImgPath = DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT) + "/" +
                    firstName + lastName + mobileNum + "/" + backImg.getOriginalFilename();
            AwsUtil.uploadFile(uploadBackImgPath, backImg.getInputStream());
        }
        if (passPortImg != null) {
            String uploadPassPortImgPath = DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT) + "/" +
                    firstName + lastName + mobileNum + "/" + passPortImg.getOriginalFilename();
            AwsUtil.uploadFile(uploadPassPortImgPath, passPortImg.getInputStream());
        }
        return Message.success();
    }

    @PostMapping("app/getOTP")
    @ApiOperation(value = "获取OTP", produces = MediaType.APPLICATION_JSON_VALUE, notes = "获取OTP")
    public Message getOTP(@RequestBody GetOTPReqVo getOTPReqVo) throws Exception {

        LOGGER.info("游戏前测评接口,请求参数,data:{}", JSON.toJSON(getOTPReqVo));
        GetOTPDTO getOTPDTO = getOTPReqVo.convertToDto(getOTPReqVo);
        RpcMessage<String> rpcMessage = appRemoteService.getOTP(getOTPDTO);
        if (RpcMessageStandardCode.OK.value() == rpcMessage.getResultCode()) {
            return Message.success(rpcMessage.getContent());
        } else {
            return Message.error(rpcMessage.getErrMsg());
        }
    }
	
    @PostMapping("app/loginGetOTP")
    @ApiOperation(value = "获取OTP", produces = MediaType.APPLICATION_JSON_VALUE, notes = "获取OTP")
    public Message loginGetOTP(@RequestBody LoginOTPReqVo loginOTPReqVo) throws Exception {

        LOGGER.info("游戏前测评接口,请求参数,data:{}", JSON.toJSON(loginOTPReqVo));
        NewSysUserDTO newSysUserDTO = loginOTPReqVo.convertToDto(loginOTPReqVo);
        
        RpcMessage<String> rpcMessage = appRemoteService.loginGetOTP(newSysUserDTO);
        if (RpcMessageStandardCode.OK.value() == rpcMessage.getResultCode()) {
            return Message.success(rpcMessage.getContent());
        } else {
            return Message.error(rpcMessage.getErrMsg());
        }
        
    }
    
    @PostMapping("app/loginCheckOTP")
    @ApiOperation(value = "获取OTP", produces = MediaType.APPLICATION_JSON_VALUE, notes = "获取OTP")
    public Message loginCheckOTP(@RequestBody LoginCheckOTPReqVo loginCheckOTPReqVo) throws Exception {

        LOGGER.info("游戏前测评接口,请求参数,data:{}", JSON.toJSON(loginCheckOTPReqVo));
        CheckOTPDTO checkOTPDTO = loginCheckOTPReqVo.convertToDto(loginCheckOTPReqVo);
        
        RpcMessage<String> rpcMessage = appRemoteService.loginCheckOTP(checkOTPDTO);
        if (RpcMessageStandardCode.OK.value() == rpcMessage.getResultCode()) {
            return Message.success(rpcMessage.getContent());
        } else {
            return Message.error(rpcMessage.getErrMsg());
        }
        
    }

    @PostMapping("app/checkOTP")
    @ApiOperation(value = "验证OTP", produces = MediaType.APPLICATION_JSON_VALUE, notes = "校验OTP")
    public Message checkOTP(@RequestBody CheckOTPReqVo checkOTPReqVo) throws Exception {

        CheckOTPDTO checkOTPDTO = checkOTPReqVo.convertToDto(checkOTPReqVo);
        RpcMessage<String> rpcMessage = appRemoteService.checkOTP(checkOTPDTO);
        if (RpcMessageStandardCode.OK.value() == rpcMessage.getResultCode()) {
            return Message.success(rpcMessage.getContent());
        } else {
            return Message.error(rpcMessage.getErrMsg());
        }
    }

    @PostMapping("h5/getCost")
    @ApiOperation(value = "子女教育获取cost", produces = MediaType.APPLICATION_JSON_VALUE, notes = "子女教育获取cost")
    public Message getCost(@RequestBody GetCostReqVo getCostReqVo) throws Exception {
        CostTypeEnum costTypeEnum = CostTypeEnum.getCostByCourseAndCountry(CostCountriesTypeEnum.forValue(getCostReqVo.getCostCountries()), CostCourseTypeEnum.forValue(getCostReqVo.getCostCourse()));
        GetCostResVo getCostResVo = new GetCostResVo();
        getCostResVo.setCost(costTypeEnum.getCost());
        return Message.success(getCostResVo);
    }

    @PostMapping("h5/getSuggestMoney")
    @ApiOperation(value = "获取建议投资金额", produces = MediaType.APPLICATION_JSON_VALUE, notes = "获取建议投资金额")
    public Message getSuggestMoney(@RequestBody GetSuggestMoneyReqVo getSuggestMoneyReqVo) throws Exception {
        GetSuggestMoneyResVo getSuggestMoneyResVo = new GetSuggestMoneyResVo();
        RiskLevelEnum riskLevel = RiskLevelEnum.forValue(getSuggestMoneyReqVo.getRiskLevel());
        BigDecimal suggestMoney;
        BigDecimal t = getSuggestMoneyReqVo.getTargetMoney().multiply(new BigDecimal("1.03").pow(getSuggestMoneyReqVo.getTotalYear()));
        BigDecimal inflation = BigDecimal.ONE.add(riskLevel.getRiskProfile());
        BigDecimal monthlyInflation = new BigDecimal(Math.pow(inflation.doubleValue(), new Double("0.0833333333")));
        if (getSuggestMoneyReqVo.getFrequency() == 1) {
            suggestMoney = t.divide(inflation.pow(getSuggestMoneyReqVo.getTotalYear()), 0, BigDecimal.ROUND_HALF_UP);
            getSuggestMoneyResVo.setRecommendAmt(suggestMoney);
        }
        if (getSuggestMoneyReqVo.getFrequency() == 2) {
            suggestMoney = t.multiply(riskLevel.getRiskProfile()).divide(inflation.multiply(inflation.pow(getSuggestMoneyReqVo.getTotalYear()).subtract(BigDecimal.ONE)), 0, BigDecimal.ROUND_HALF_UP);
            getSuggestMoneyResVo.setRecommendAmt(suggestMoney);
        }
        if (getSuggestMoneyReqVo.getFrequency() == 3) {
            suggestMoney = t.multiply(monthlyInflation.subtract(BigDecimal.ONE)).divide(monthlyInflation.multiply(inflation.pow(getSuggestMoneyReqVo.getTotalYear()).subtract(BigDecimal.ONE)), 0, BigDecimal.ROUND_HALF_UP);
            getSuggestMoneyResVo.setRecommendAmt(suggestMoney);
        }
        getSuggestMoneyResVo.setFrequency(String.valueOf(getSuggestMoneyReqVo.getFrequency()))
                .setGoalsType(String.valueOf(getSuggestMoneyReqVo.getGoalsType()))
                .setPortfolioId(getSuggestMoneyReqVo.getPortfolioId().substring(0, 3) + riskLevel.getValue() + getSuggestMoneyReqVo.getPortfolioId().substring(4, 6))
                .setRisk(String.valueOf(riskLevel.getValue()))
                .setChildName(getSuggestMoneyReqVo.getChildName())
                .setClientId(getClientId());
        return Message.success(getSuggestMoneyResVo);
    }

}
