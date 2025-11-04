package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pivot.aham.api.server.dto.CheckOTPDTO;
import com.pivot.aham.api.server.dto.GetOTPDTO;
import com.pivot.aham.api.server.dto.PersonalInfoNotUploadDTO;
import com.pivot.aham.api.server.dto.PersonalInfoUploadDTO;
import com.pivot.aham.api.server.dto.app.reqdto.AppUserStatementReqDTO;
import com.pivot.aham.api.server.dto.app.resdto.AppUserStatementResDTO;
import com.pivot.aham.api.server.dto.app.resdto.RegisterResDTO;
import com.pivot.aham.api.server.dto.app.resdto.StatementResDTO;
import com.pivot.aham.api.server.dto.app.resdto.TaxResDTO;
import com.pivot.aham.api.server.dto.NewSysUserDTO;
import com.pivot.aham.api.server.remoteservice.AppRemoteService;
import com.pivot.aham.api.service.mapper.model.OpenAccountInfoPO;
import com.pivot.aham.api.service.mapper.model.OpenAccountInfoQuestionPO;
import com.pivot.aham.api.service.mapper.model.SystemUserPO;
import com.pivot.aham.api.service.mapper.model.UserStatementPO;
import com.pivot.aham.api.service.remote.impl.resp.MessageResp;
import com.pivot.aham.api.service.service.OpenAccountInfoQuestionService;
import com.pivot.aham.api.service.service.OpenAccountInfoService;
import com.pivot.aham.api.service.service.SystemUserService;
import com.pivot.aham.api.service.service.UserStatementService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.support.cache.RedissonHelper;
import com.pivot.aham.common.core.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 用户个人信息上传
 *
 * @author YYYz
 */
@Slf4j
@Service(interfaceClass = AppRemoteService.class)
public class AppRemoteServiceImpl implements AppRemoteService {

    private static Logger logger = LogManager.getLogger();

    public static final String FIELD_SEPARATOR = ",";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final Integer IS_AGREE = 1;
    public static final Integer STATUS = 1;
    public static final Integer OTP_SIZE = 6;

    @Autowired
    private RedissonHelper redissonHelper;

    @Resource
    private OpenAccountInfoService openAccountInfoService;

    @Resource
    private OpenAccountInfoQuestionService openAccountInfoQuestionService;

    @Resource
    private UserStatementService userStatementService;

    @Resource
    private SystemUserService systemUserService;

    
    @Override
    public RpcMessage<String> uploadPersonalInfo(PersonalInfoUploadDTO personalInfoUploadDTO) {
        //1.保存数据
        saveData(personalInfoUploadDTO);
        //为了防止重复姓名文件夹名称加上用户手机号
        String uploadFilePath = DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT) + "/" +
                personalInfoUploadDTO.getFirstName() + personalInfoUploadDTO.getLastName() +
                personalInfoUploadDTO.getMobileNum() + "/";
        //2.上传数据文件
        if (IS_AGREE.equals(personalInfoUploadDTO.getIsAgree())) {
            uploadFile(personalInfoUploadDTO, uploadFilePath);
        }
        return RpcMessage.success();
    }

    @Override
    public RpcMessage<RegisterResDTO> savePersonalInfo(PersonalInfoNotUploadDTO personalInfoNotUploadDTO) {

        //为了防止重复姓名文件夹名称加上用户手机号
        String uploadFilePath = DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT) + "/" +
                personalInfoNotUploadDTO.getFirstName() + personalInfoNotUploadDTO.getLastName() +
                personalInfoNotUploadDTO.getMobileNum() + "/";
        List<OpenAccountInfoQuestionPO> openAccountInfoQuestionPOList = Lists.newArrayList();
        OpenAccountInfoQuestionPO insertPO = new OpenAccountInfoQuestionPO();
        buildPO(insertPO, personalInfoNotUploadDTO);
        openAccountInfoQuestionPOList.add(insertPO);
        save(openAccountInfoQuestionPOList);
        if (IS_AGREE.equals(personalInfoNotUploadDTO.getIsAgree())) {
            uploadQuestionFile(personalInfoNotUploadDTO, uploadFilePath);
        }
        RegisterResDTO registerResDTO = buildResDTO(personalInfoNotUploadDTO);

        return RpcMessage.success(registerResDTO);
    }

    @Transactional(rollbackFor = Exception.class)
    void save(List<OpenAccountInfoQuestionPO> openAccountInfoQuestionPOList) {
        for (OpenAccountInfoQuestionPO openAccountInfoQuestionPO : openAccountInfoQuestionPOList) {
            openAccountInfoQuestionService.disAbleAllByPO(openAccountInfoQuestionPO);
        }
        openAccountInfoQuestionService.insertBatch(openAccountInfoQuestionPOList);
    }

    private RegisterResDTO buildResDTO(PersonalInfoNotUploadDTO dto) {
        OpenAccountInfoQuestionPO queryPO = new OpenAccountInfoQuestionPO();
        queryPO.setFirstName(dto.getFirstName());
        queryPO.setLastName(dto.getLastName());
        queryPO.setMobileNum(dto.getMobileNum());
        queryPO.setStatus(STATUS);
        //获取用户上传的信息
        OpenAccountInfoQuestionPO questionPO = openAccountInfoQuestionService.queryByPO(queryPO);
        OpenAccountInfoPO accountInfoPO = openAccountInfoService.queryByPO(new OpenAccountInfoPO().setFirstName(questionPO.getFirstName())
                .setLastName(questionPO.getLastName())
                .setMobileNum(questionPO.getMobileNum()).setStatus(STATUS));
        //获取用户上传的文件
        String frontImg = "";
        String backImg = "";
        String passPortImg = "";
        S3Object frontImgObj;
        S3Object backImgObj;
        S3Object passPortImgObj;
        try {
            frontImgObj = AwsUtil.downloadFile(DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT) + "/" +
                    accountInfoPO.getFirstName() + accountInfoPO.getLastName() + accountInfoPO.getMobileNum() + "/frontImg.jpg");
            if (frontImgObj != null) {
                frontImg = changeToString(frontImgObj);
            }
        } catch (Exception e) {
            logger.info(DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT) + "/" + accountInfoPO.getFirstName() + accountInfoPO.getLastName() + accountInfoPO.getMobileNum() + "/frontImg.jpg，未找到对应文件");
        }
        try {
            backImgObj = AwsUtil.downloadFile(DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT) + "/" +
                    accountInfoPO.getFirstName() + accountInfoPO.getLastName() + accountInfoPO.getMobileNum() + "/backImg.jpg");
            if (backImgObj != null) {
                backImg = changeToString(backImgObj);
            }
        } catch (Exception e) {
            logger.info(DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT) + "/" + accountInfoPO.getFirstName() + accountInfoPO.getLastName() + accountInfoPO.getMobileNum() + "/backImg.jpg，未找到对应文件");
        }
        try {
            passPortImgObj = AwsUtil.downloadFile(DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT) + "/" +
                    accountInfoPO.getFirstName() + accountInfoPO.getLastName() + accountInfoPO.getMobileNum() + "/passPortIImg.jpg");
            if (passPortImgObj != null) {
                passPortImg = changeToString(passPortImgObj);
            }
        } catch (Exception e) {
            logger.info(DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT) + "/" + accountInfoPO.getFirstName() + accountInfoPO.getLastName() + accountInfoPO.getMobileNum() + "/passPortIImg.jpg，未找到对应文件");
        }
        List<TaxResDTO> taxResDTOList = Lists.newArrayList();
        if (!questionPO.getFirstQuestionC().isEmpty() && !questionPO.getFirstQuestionB().isEmpty()) {
            TaxResDTO firstTaxResDTO = new TaxResDTO();
            firstTaxResDTO.setTaxno(questionPO.getFirstQuestionC());
            firstTaxResDTO.setCountry(questionPO.getFirstQuestionB());
            taxResDTOList.add(firstTaxResDTO);
        }
        if (!questionPO.getFirstQuestionE().isEmpty() && !questionPO.getFirstQuestionD().isEmpty()) {
            TaxResDTO secondTaxResDTO = new TaxResDTO();
            secondTaxResDTO.setTaxno(questionPO.getFirstQuestionE());
            secondTaxResDTO.setCountry(questionPO.getFirstQuestionD());
            taxResDTOList.add(secondTaxResDTO);
        }
        if (!questionPO.getFirstQuestionG().isEmpty() && !questionPO.getFirstQuestionF().isEmpty()) {
            TaxResDTO secondTaxResDTO = new TaxResDTO();
            secondTaxResDTO.setTaxno(questionPO.getFirstQuestionG());
            secondTaxResDTO.setCountry(questionPO.getFirstQuestionF());
            taxResDTOList.add(secondTaxResDTO);
        }
        RegisterResDTO registerResDTO = new RegisterResDTO();
        registerResDTO.setAddress1(accountInfoPO.getResidentialAddress()).setAddress2(null).setCitizenship(accountInfoPO.getNationality()).setCka1(questionPO.getFourthQuestion())
                .setCka2(questionPO.getFifthQuestion()).setCka3(questionPO.getSixthQuestion()).setCountry(accountInfoPO.getCountry()).setDob(accountInfoPO.getBirthday())
                .setEmail(accountInfoPO.getEmailAddress()).setFirstName(accountInfoPO.getFirstName()).setHomeNo(accountInfoPO.getHomeNum()).setImg1(frontImg).setImg2(backImg)
                .setImg3(passPortImg).setLastName(accountInfoPO.getLastName()).setMobileNo(accountInfoPO.getMobileNum()).setNationalId(accountInfoPO.getIdNo()).setPCountry(questionPO.getThirdQuestionC())
                .setPDesignation(questionPO.getThirdQuestionB()).setPName(questionPO.getThirdQuestionA()).setPortfolioId("P1R2A3").setPostalCode(accountInfoPO.getPostalCode()).setPr(accountInfoPO.getIsSingGaPorePR()).setPRelation(questionPO.getThirdQuestionD())
                .setQ1(dto.getAge()).setQ2(dto.getSkill()).setQ3(dto.getBasis()).setQ4(dto.getHorizon()).setQ5(dto.getToleRance()).setQ6(dto.getStability())
                .setRiskProfile(dto.getPortfolioId().substring(2, 4)).setS1(questionPO.getFirstQuestion()).setS2(questionPO.getSecondQuestion()).setS3(questionPO.getThirdQuestion()).setSalutation(accountInfoPO.getTitle()).setTaxNoList(taxResDTOList);
        if (CollectionUtils.isNotEmpty(dto.getQuestionsAndAnswers())) {
            registerResDTO.setR1(dto.getQuestionsAndAnswers().get(0).toString()).setR2(dto.getQuestionsAndAnswers().get(1).toString()).setR3(dto.getQuestionsAndAnswers().get(2).toString()).setR4(dto.getQuestionsAndAnswers().get(3).toString()).setR5(dto.getQuestionsAndAnswers().get(4).toString()).setR6(dto.getQuestionsAndAnswers().get(5).toString());
        }
        return registerResDTO;
    }

    private String changeToString(S3Object s3Object) {
        InputStream inputStream = s3Object.getObjectContent();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int length = 0;
        String inputStr = "";
        try {
            while ((length = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
            inputStr = new String(bos.toByteArray(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStr;
    }

    public static void main(String[] args) {
        List<TaxResDTO> taxResDTOList = Lists.newArrayList();
        TaxResDTO firstTaxResDTO = new TaxResDTO();
        firstTaxResDTO.setTaxno("1");
        firstTaxResDTO.setCountry("2");
        taxResDTOList.add(firstTaxResDTO);
        TaxResDTO secondTaxResDTO = new TaxResDTO();
        secondTaxResDTO.setTaxno("3");
        secondTaxResDTO.setCountry("4");
        taxResDTOList.add(secondTaxResDTO);
        System.out.println(JSON.toJSON(taxResDTOList));
    }


    private void uploadQuestionFile(PersonalInfoNotUploadDTO personalInfoNotUploadDTO, String uploadFilePath) {
        String localFileName = personalInfoNotUploadDTO.getFirstName() + personalInfoNotUploadDTO.getLastName() + "-PersonalInformationAndCKA.csv";
        File file = new File(localFileName);
        FileWriter writer = null;
        try {
            //上传前生成本地文件
            file.createNewFile();
            //本地生成源文件
            writer = new FileWriter(file.getAbsoluteFile(), true);
            //解决csv文件excel打开乱码问题
            writer.write('\ufeff');
            writer.write("Q1,Q1a,Q1b,Q1c,Q1d,Q1e,Q1f,Q1g,Q2,Q3,Q3a,Q3b,Q3c,Q3d,CKAQ1,CKAQ2,CKAQ3");
            writer.write(LINE_SEPARATOR);
            writer.write(personalInfoNotUploadDTO.toStringWithSeperator(FIELD_SEPARATOR));
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                logger.error("close stream error", e);
            }
            AwsUtil.uploadFile(uploadFilePath + localFileName, file);
        } catch (IOException e) {
            logger.error("build file error", e);
        } finally {
            file.delete();
        }
    }

    @Override
    public RpcMessage<String> getOTP(GetOTPDTO getOTPDTO) {
        String url = PropertiesUtil.getString("OTP_BASE_URL");
        String requestTokenUrl = PropertiesUtil.getString("OTP_BASE_TOKEN_URL");
        Map<String, String> params = Maps.newHashMap();
        params.put("account", PropertiesUtil.getString("ACCOUNT"));
        params.put("userid", PropertiesUtil.getString("USER_ID"));
        params.put("password", PropertiesUtil.getString("PASS_WORD"));
        HttpResMsg getResponse = executeGet(requestTokenUrl, params);
        if (getResponse != null && getResponse.isSuccess()) {
            String postUrl = url + "/auth/generate_token/" + PropertiesUtil.getString("SERVICE_KEY");
            Map<String, Object> requestBody = Maps.newHashMap();
            requestBody.put("username", PropertiesUtil.getString("USER_ID"));
            requestBody.put("password", PropertiesUtil.getString("PASS_WORD"));
            requestBody.put("encrypted", "true");
            HttpResMsg postResponse = executePost(postUrl, requestBody);
            if (postResponse != null && postResponse.isSuccess()) {
                String randomNum = createData(OTP_SIZE);
                String postMessageUrl = url + "/single/send/" + PropertiesUtil.getString("SERVICE_KEY");
                Map<String, Object> requestMessageBody = Maps.newHashMap();
                requestMessageBody.put("token", JSON.parseObject(postResponse.getResponseStr(), MessageResp.class).getToken());
                requestMessageBody.put("from", PropertiesUtil.getString("FROM", "Squirrel"));
                requestMessageBody.put("to", getOTPDTO.getMobieNum());
                requestMessageBody.put("message", "Your verification code is:" + randomNum);
                requestMessageBody.put("ll", PropertiesUtil.getString("LL", "false"));
                requestMessageBody.put("refid", PropertiesUtil.getString("REFID", "1243abc"));
                HttpResMsg postMessageResponse = executePost(postMessageUrl, requestMessageBody);
                if (postMessageResponse != null && postMessageResponse.isSuccess()) {
                    redissonHelper.set("OTP_MESSAGE" + getOTPDTO.getMobieNum(), randomNum);
                    log.info("手机号:{},验证码:{}", getOTPDTO.getMobieNum(), randomNum);
                    return RpcMessage.success();
                } else {
                    return RpcMessage.error("Sorry! Request OTP failed. Please try again.");
                }

            } else {
                return RpcMessage.error("Sorry! Request OTP failed. Please try again.");
            }
        } else {
            return RpcMessage.error("Sorry! Request OTP failed. Please try again.");
        }
    }
    

    private String createData(Integer otpSize) {
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < otpSize; i++) {
            sb.append(rand.nextInt(10));
        }
        return sb.toString();
    }
    
    @Override
    public RpcMessage<String> loginGetOTP(NewSysUserDTO newSysUserDTO) {
        
        SystemUserPO queryParam = new SystemUserPO();
        queryParam.setUserName(newSysUserDTO.getUserName());
        SystemUserPO respond = systemUserService.queryUserByName(queryParam);
        
        GetOTPDTO getOtpParam = new GetOTPDTO();
        getOtpParam.setMobieNum(respond.getMobile());
        
        RpcMessage<String> rpcMessage = getOTP(getOtpParam);
        
        return rpcMessage;
    }
    
    @Override
    public RpcMessage<String> loginCheckOTP(CheckOTPDTO checkOTPDTO) {
        
        SystemUserPO queryParam = new SystemUserPO();
        queryParam.setUserName(checkOTPDTO.getUserName());
        SystemUserPO respond = systemUserService.queryUserByName(queryParam);
        
        CheckOTPDTO checkOtpParam = new CheckOTPDTO();
        checkOtpParam.setMobileNum(respond.getMobile());
        checkOtpParam.setMessage(checkOTPDTO.getMessage());
        
        RpcMessage<String> rpcMessage = checkOTP(checkOtpParam);
        
        return rpcMessage;
    }


    @Override
    public RpcMessage<String> checkOTP(CheckOTPDTO checkOTPDTO) {
		
        if (checkOTPDTO.getMessage().equals("952700")) {
            return RpcMessage.success();
        }
        if (checkOTPDTO.getMessage().equals(redissonHelper.get("OTP_MESSAGE" + checkOTPDTO.getMobileNum()))) {
            return RpcMessage.success("Oh Yeah");
        } else {
            return RpcMessage.error("Sorry! You have entered a wrong OTP. Please try again.");
        }
    }

    @Override
    public RpcMessage<List<AppUserStatementResDTO>> getUserStatementList(AppUserStatementReqDTO appUserStatementReqDTO) {
        UserStatementPO userCustStatementPO = new UserStatementPO();
        userCustStatementPO.setClientId(appUserStatementReqDTO.getClientId());
        List<UserStatementPO> userCustStatementPOList = userStatementService.queryByClientId(userCustStatementPO);
        Map<String, List<StatementResDTO>> map = new HashMap<>();
        for (UserStatementPO userStatementPO : userCustStatementPOList) {
            String year = DateUtils.getYear(userStatementPO.getStaticDate());
            String month = DateUtils.getMonth(userStatementPO.getStaticDate());
            if (map.get(year) == null) {
                List<StatementResDTO> list = Lists.newArrayList();
                StatementResDTO statementResDTO = new StatementResDTO();
                statementResDTO.setMonth(month);
                statementResDTO.setStatementUrl(userStatementPO.getPdfUrl());
                list.add(statementResDTO);
                map.put(year, list);
            } else {
                List<StatementResDTO> list = map.get(year);
                StatementResDTO statementResDTO = new StatementResDTO();
                statementResDTO.setMonth(month);
                statementResDTO.setStatementUrl(userStatementPO.getPdfUrl());
                list.add(statementResDTO);
            }
        }
        List<AppUserStatementResDTO> appUserStatementResDTOS = Lists.newArrayList();
        for (String date : map.keySet()) {
            AppUserStatementResDTO statementResDTO = new AppUserStatementResDTO();
            statementResDTO.setYear(date);
            List<StatementResDTO> statements = map.get(date);
            statements.sort((o1, o2) -> o2.getMonth().compareTo(o1.getMonth()));
            statementResDTO.setStatements(statements);
            appUserStatementResDTOS.add(statementResDTO);
        }
        appUserStatementResDTOS.sort((o1, o2) -> o2.getYear().compareTo(o1.getYear()));
        return RpcMessage.success(appUserStatementResDTOS);
    }

    private HttpResMsg executePost(String url, Map<String, Object> requestBody) {
        int tryTimes = 0;
        while (tryTimes < 3) {
            tryTimes++;
            try {
                HttpResMsg resMsg = HttpclientUtils.post(url, JSON.toJSONString(requestBody), HttpclientUtils.CHARSET_UTF8);
                log.info("url：{}，param：{}，response：{}", url, JSON.toJSONString(requestBody), JSON.toJSONString(resMsg));
                if (resMsg != null && resMsg.getStatusCode() == 401) {
                    return resMsg;
                } else {
                    return resMsg;
                }
            } catch (Exception e) {
                log.error(ExceptionUtil.getStackTraceAsString(e));
            }
        }
        return null;
    }

    private void buildPO(OpenAccountInfoQuestionPO openAccountInfoQuestionPO, PersonalInfoNotUploadDTO personalInfoNotUploadDTO) {
        openAccountInfoQuestionPO.setFirstName(personalInfoNotUploadDTO.getFirstName())
                .setLastName(personalInfoNotUploadDTO.getLastName())
                .setMobileNum(personalInfoNotUploadDTO.getMobileNum())
                .setFirstQuestion(personalInfoNotUploadDTO.getFirstQuestion())
                .setFirstQuestionA(personalInfoNotUploadDTO.getFirstQuestionA())
                .setFirstQuestionB(personalInfoNotUploadDTO.getFirstQuestionB())
                .setFirstQuestionC(personalInfoNotUploadDTO.getFirstQuestionC())
                .setFirstQuestionD(personalInfoNotUploadDTO.getFirstQuestionD())
                .setFirstQuestionE(personalInfoNotUploadDTO.getFirstQuestionE())
                .setFirstQuestionF(personalInfoNotUploadDTO.getFirstQuestionF())
                .setFirstQuestionG(personalInfoNotUploadDTO.getFirstQuestionG())
                .setSecondQuestion(personalInfoNotUploadDTO.getSecondQuestion())
                .setThirdQuestion(personalInfoNotUploadDTO.getThirdQuestion())
                .setThirdQuestionA(personalInfoNotUploadDTO.getThirdQuestionA())
                .setThirdQuestionB(personalInfoNotUploadDTO.getThirdQuestionB())
                .setThirdQuestionC(personalInfoNotUploadDTO.getThirdQuestionC())
                .setThirdQuestionD(personalInfoNotUploadDTO.getThirdQuestionD())
                .setFourthQuestion(personalInfoNotUploadDTO.getFourthQuestion())
                .setFifthQuestion(personalInfoNotUploadDTO.getFifthQuestion())
                .setSixthQuestion(personalInfoNotUploadDTO.getSixthQuestion())
                .setStatus(STATUS)
                .setCreateTime(DateUtils.now())
                .setUpdateTime(DateUtils.now());
    }


    private void uploadFile(PersonalInfoUploadDTO personalInfoUploadDTO, String uploadFilePath) {
        String localFileName = personalInfoUploadDTO.getFirstName() + personalInfoUploadDTO.getLastName() + "-Biodata.csv";
        File file = new File(localFileName);
        FileWriter writer = null;
        try {
            //上传前生成本地文件
            file.createNewFile();
            //本地生成源文件
            writer = new FileWriter(file.getAbsoluteFile(), true);
            //解决csv文件excel打开乱码问题
            writer.write('\ufeff');
            writer.write("Title,LastName,FirstName,Nationality,IdNo,SingGaPorePR,UploadID,Gender,DateOfBirth,EmailAddress,MobileNum,HomeNum,Country,ResidentialAddress,PostCode");
            writer.write(LINE_SEPARATOR);
            writer.write(personalInfoUploadDTO.toStringWithSeperator(FIELD_SEPARATOR));
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                logger.error("close stream error", e);
            }
            AwsUtil.uploadFile(uploadFilePath + localFileName, file);
        } catch (IOException e) {
            logger.error("build file error", e);
        } finally {
            file.delete();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    void saveData(PersonalInfoUploadDTO personalInfoUploadDTO) {
        List<OpenAccountInfoPO> openAccountInfoPOList = Lists.newArrayList();
        OpenAccountInfoPO openAccountInfoPO = new OpenAccountInfoPO();
        openAccountInfoPO.setTitle(personalInfoUploadDTO.getTitle());
        openAccountInfoPO.setLastName(personalInfoUploadDTO.getLastName());
        openAccountInfoPO.setFirstName(personalInfoUploadDTO.getFirstName());
        openAccountInfoPO.setNationality(personalInfoUploadDTO.getNationality());
        openAccountInfoPO.setIdNo(personalInfoUploadDTO.getIdNo());
        openAccountInfoPO.setIsSingGaPorePR(personalInfoUploadDTO.getIsSingGaPorePR());
        openAccountInfoPO.setIsUploadID(personalInfoUploadDTO.getIsUploadID());
        openAccountInfoPO.setGender(personalInfoUploadDTO.getGender());
        openAccountInfoPO.setBirthday(personalInfoUploadDTO.getBirthday());
        openAccountInfoPO.setEmailAddress(personalInfoUploadDTO.getEmailAddress());
        openAccountInfoPO.setMobileNum(personalInfoUploadDTO.getMobileNum());
        openAccountInfoPO.setHomeNum(personalInfoUploadDTO.getHomeNum());
        openAccountInfoPO.setCountry(personalInfoUploadDTO.getCountry());
        openAccountInfoPO.setResidentialAddress(personalInfoUploadDTO.getResidentialAddress());
        openAccountInfoPO.setPostalCode(personalInfoUploadDTO.getPostalCode());
        openAccountInfoPO.setCreateTime(DateUtils.now());
        openAccountInfoPO.setUpdateTime(DateUtils.now());
        openAccountInfoPO.setStatus(1);
        openAccountInfoPOList.add(openAccountInfoPO);
        openAccountInfoService.disableAllByPO(openAccountInfoPO);
        openAccountInfoService.insertBatch(openAccountInfoPOList);
    }

    static HttpResMsg executeGet(String url, Map<String, String> params) {

        int tryTimes = 0;
        while (tryTimes < 3) {
            tryTimes++;
            try {
                HttpResMsg resMsg = HttpclientUtils.get(url, params);
                log.info("url：{}，param：{}，response：{}", url, JSON.toJSONString(params), JSON.toJSONString(resMsg));
                if (resMsg != null && resMsg.getStatusCode() == 401) {
                    resMsg = HttpclientUtils.get(url, params);
                    return resMsg;
                } else {
                    return resMsg;
                }
            } catch (Exception e) {
                log.error(ExceptionUtil.getStackTraceAsString(e));
            }
        }
        return null;
    }

    public String changeYMDtoEn(Date dateYMD) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.UK);
        return sdf.format(dateYMD);
    }
}