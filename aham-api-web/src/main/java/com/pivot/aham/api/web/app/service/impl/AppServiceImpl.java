package com.pivot.aham.api.web.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.amazonaws.util.json.Jackson;
import com.pivot.aham.api.web.app.dto.reqdto.*;
import com.pivot.aham.api.web.app.dto.resdto.*;
import com.pivot.aham.api.web.app.febase.AppApiRes;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import com.pivot.aham.common.core.util.HttpResMsg;
import com.pivot.aham.common.core.util.PropertiesUtil;
import com.pivot.aham.api.web.app.service.AppRequestService;
import com.pivot.aham.api.web.app.service.AppService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * @author YYYz
 */
@Service("paymentFacade")
@Slf4j
public class AppServiceImpl implements AppService {

    private final String APP_LOGIN_URL = PropertiesUtil.getString("APP_LOGIN_URL");
    private final String APP_FORGETPWD_URL = PropertiesUtil.getString("APP_FORGETPWD_URL");
    private final String APP_GETCLIENTINFO_URL = PropertiesUtil.getString("APP_GETCLIENTINFO_URL");
    private final String APP_ADDGOAL_URL = PropertiesUtil.getString("APP_ADDGOAL_URL");
    private final String APP_WITHDRAW_URL = PropertiesUtil.getString("APP_WITHDRAW_URL");
    private final String APP_WITHDRAWALSC_URL = PropertiesUtil.getString("APP_WITHDRAWALSC_URL");
    private final String APP_GETBANKLIST_URL = PropertiesUtil.getString("APP_GETBANKLIST_URL");
    private final String APP_FUNDMYGOALLIST_URL = PropertiesUtil.getString("APP_FUNDMYGOALLIST_URL");
    private final String APP_FUNDMYGOAL_URL = PropertiesUtil.getString("APP_FUNDMYGOAL_URL");
    private final String APP_REFER_URL = PropertiesUtil.getString("APP_REFER_URL");
    private final String APP_REGISTER_URL = PropertiesUtil.getString("APP_REGISTER_URL");
    private final String APP_CHANGERISK_URL = PropertiesUtil.getString("APP_CHANGERISK_URL");
    private final String APP_DELETEGOAL_URL = PropertiesUtil.getString("APP_DELETEGOAL_URL");
    private final String APP_STATEMENTLIST_URL = PropertiesUtil.getString("APP_STATEMENTLIST_URL");
    private final String APP_PDFSTATEMENT_URL = PropertiesUtil.getString("APP_PDFSTATEMENT_URL");

    @Resource
    private AppRequestService appRequestService;

    @Override
    public LoginResDTO login(LoginDTO loginDTO) {
        Map<String, Object> beanMap = BeanMapperUtils.trans2Map(loginDTO);
        Map<String, Object> paramMap = checkNull(beanMap);
        LoginResDTO loginResDTO = new LoginResDTO();
        HttpResMsg httpResMsg = appRequestService.callAppApi(Jackson.toJsonString(paramMap), APP_LOGIN_URL);
        if (httpResMsg != null) {
            AppApiRes appApiRes = JSON.parseObject(httpResMsg.getResponseStr(), AppApiRes.class);
            if (appApiRes != null && appApiRes.isSuccess()) {
                loginResDTO = JSON.parseObject(appApiRes.getContent(), LoginResDTO.class);
                loginResDTO.setErrorMsg(appApiRes.getErrMsg());
                loginResDTO.setResultCode(appApiRes.getResultCode());
            } else {
                loginResDTO.setErrorMsg(appApiRes.getErrMsg());
                loginResDTO.setResultCode(appApiRes.getResultCode());
            }
            return loginResDTO;
        } else {
            return null;
        }
    }

    @Override
    public Integer forgetPwd(ForgetPwdDTO forgetPwdDTO) {
        Map<String, Object> beanMap = BeanMapperUtils.trans2Map(forgetPwdDTO);
        Map<String, Object> paramMap = checkNull(beanMap);
        HttpResMsg httpResMsg = appRequestService.callAppApi(Jackson.toJsonString(paramMap), APP_FORGETPWD_URL);
        if (httpResMsg != null) {
            AppApiRes appApiRes = JSON.parseObject(httpResMsg.getResponseStr(), AppApiRes.class);
            Integer resultCode = Integer.valueOf(appApiRes.getResultCode());
            return resultCode;
        } else {
            return null;
        }
    }

    @Override
    public ClientInfoResDTO getClientInfo(ClientInfoDTO clientInfoDTO) {
        Map<String, Object> beanMap = BeanMapperUtils.trans2Map(clientInfoDTO);
        Map<String, Object> paramMap = checkNull(beanMap);
        HttpResMsg httpResMsg = appRequestService.callAppApi(Jackson.toJsonString(paramMap), APP_GETCLIENTINFO_URL);
        if (httpResMsg != null) {
            AppApiRes appApiRes = JSON.parseObject(httpResMsg.getResponseStr(), AppApiRes.class);
            ClientInfoResDTO clientInfoResDTO = JSON.parseObject(appApiRes.getContent(), ClientInfoResDTO.class);
            clientInfoResDTO.setResultCode(appApiRes.getResultCode());
            clientInfoResDTO.setErrorMsg(appApiRes.getErrMsg());
            return clientInfoResDTO;
        } else {
            return null;
        }
    }

    @Override
    public AddGoalResDTO addGoal(AddGoalDTO addGoalDTO) {
        Map<String, Object> beanMap = BeanMapperUtils.trans2Map(addGoalDTO);
        Map<String, Object> paramMap = checkNull(beanMap);
        HttpResMsg httpResMsg = appRequestService.callAppApi(Jackson.toJsonString(paramMap), APP_ADDGOAL_URL);
        if (httpResMsg != null) {
            AppApiRes appApiRes = JSON.parseObject(httpResMsg.getResponseStr(), AppApiRes.class);
            AddGoalResDTO addGoalResDTO = JSON.parseObject(appApiRes.getContent(), AddGoalResDTO.class);
            addGoalResDTO.setResultCode(appApiRes.getResultCode());
            addGoalResDTO.setErrorMsg(appApiRes.getErrMsg());
            return addGoalResDTO;
        } else {
            return null;
        }
    }

    @Override
    public DelGoalResDTO delGoal(DelGoalDTO deleteGoalDTO) {
        Map<String, Object> beanMap = BeanMapperUtils.trans2Map(deleteGoalDTO);
        Map<String, Object> paramMap = checkNull(beanMap);
        HttpResMsg httpResMsg = appRequestService.callAppApi(Jackson.toJsonString(paramMap), APP_DELETEGOAL_URL);
        if (httpResMsg != null) {
            AppApiRes appApiRes = JSON.parseObject(httpResMsg.getResponseStr(), AppApiRes.class);
            DelGoalResDTO delGoalResDTO = JSON.parseObject(appApiRes.getContent(), DelGoalResDTO.class);
            delGoalResDTO.setResultCode(appApiRes.getResultCode());
            delGoalResDTO.setErrorMsg(appApiRes.getErrMsg());
            return delGoalResDTO;
        } else {
            return null;
        }
    }

    @Override
    public WithdrawResDTO withdraw(WithdrawAlscDTO withdrawAlscDTO) {
        Map<String, Object> beanMap = BeanMapperUtils.trans2Map(withdrawAlscDTO);
        Map<String, Object> paramMap = checkNull(beanMap);
        log.info("withdraw JSONString {} " + Jackson.toJsonString(paramMap));
        HttpResMsg httpResMsg = appRequestService.callAppApi(Jackson.toJsonString(paramMap), APP_WITHDRAW_URL);
        if (httpResMsg != null) {
            AppApiRes appApiRes = JSON.parseObject(httpResMsg.getResponseStr(), AppApiRes.class);
            WithdrawResDTO withdrawResDTO = JSON.parseObject(appApiRes.getContent(), WithdrawResDTO.class);
            withdrawResDTO.setResultCode(appApiRes.getResultCode());
            withdrawResDTO.setErrorMsg(appApiRes.getErrMsg());
            return withdrawResDTO;
        } else {
            return null;
        }
    }

    @Override
    public WithdrawAlscResDTO withdrawalsc(WithdrawAlscDTO withdrawAlscDTO) {
        Map<String, Object> beanMap = BeanMapperUtils.trans2Map(withdrawAlscDTO);
        Map<String, Object> paramMap = checkNull(beanMap);
        log.info("withdrawalsc JSONString {} " + Jackson.toJsonString(paramMap));
        HttpResMsg httpResMsg = appRequestService.callAppApi(Jackson.toJsonString(paramMap), APP_WITHDRAWALSC_URL);
        if (httpResMsg != null) {
            AppApiRes appApiRes = JSON.parseObject(httpResMsg.getResponseStr(), AppApiRes.class);
            WithdrawAlscResDTO withdrawAlscResDTO = JSON.parseObject(appApiRes.getContent(), WithdrawAlscResDTO.class);
            withdrawAlscResDTO.setResultCode(appApiRes.getResultCode());
            withdrawAlscResDTO.setErrorMsg(appApiRes.getErrMsg());
            return withdrawAlscResDTO;
        } else {
            return null;
        }
    }

    @Override
    public BankInfoResDTO getBankList(BankInfoDTO bankInfoDTO) {
        Map<String, Object> beanMap = BeanMapperUtils.trans2Map(bankInfoDTO);
        Map<String, Object> paramMap = checkNull(beanMap);
        HttpResMsg httpResMsg = appRequestService.callAppApi(Jackson.toJsonString(paramMap), APP_GETBANKLIST_URL);
        if (httpResMsg != null) {
            AppApiRes appApiRes = JSON.parseObject(httpResMsg.getResponseStr(), AppApiRes.class);
            BankInfoResDTO bankInfoResDTO = JSON.parseObject(appApiRes.getContent().trim(), BankInfoResDTO.class);
            bankInfoResDTO.setResultCode(appApiRes.getResultCode());
            bankInfoResDTO.setErrorMsg(appApiRes.getErrMsg());
            return bankInfoResDTO;
        } else {
            return null;
        }
    }

    @Override
    public FundMyGoalListResDTO fundMyGoalList(FundMyGoalListDTO fundMyGoalListDTO) {
        Map<String, Object> beanMap = BeanMapperUtils.trans2Map(fundMyGoalListDTO);
        Map<String, Object> paramMap = checkNull(beanMap);
        HttpResMsg httpResMsg = appRequestService.callAppApi(Jackson.toJsonString(paramMap), APP_FUNDMYGOALLIST_URL);
        if (httpResMsg != null) {
            AppApiRes appApiRes = JSON.parseObject(httpResMsg.getResponseStr(), AppApiRes.class);
            FundMyGoalListResDTO fundMyGoalListResDTO = JSON.parseObject(appApiRes.getContent(), FundMyGoalListResDTO.class);
            fundMyGoalListResDTO.setResultCode(appApiRes.getResultCode());
            fundMyGoalListResDTO.setErrorMsg(appApiRes.getErrMsg());
            return fundMyGoalListResDTO;
        } else {
            return null;
        }
    }

    @Override
    public FundMyGoalResDTO fundMyGoal(GoalReqDTO goalReqDTO) {
        Map<String, Object> beanMap = BeanMapperUtils.trans2Map(goalReqDTO);
        Map<String, Object> paramMap = checkNull(beanMap);
        HttpResMsg httpResMsg = appRequestService.callAppApi(Jackson.toJsonString(paramMap), APP_FUNDMYGOAL_URL);
        if (httpResMsg != null) {
            AppApiRes appApiRes = JSON.parseObject(httpResMsg.getResponseStr(), AppApiRes.class);
            FundMyGoalResDTO fundMyGoalResDTO = JSON.parseObject(appApiRes.getContent(), FundMyGoalResDTO.class);
            fundMyGoalResDTO.setResultCode(appApiRes.getResultCode());
            fundMyGoalResDTO.setErrorMsg(appApiRes.getErrMsg());
            return fundMyGoalResDTO;
        } else {
            return null;
        }
    }

    @Override
    public ReferResDTO refer(ReferDTO clientInfoDTO) {
        Map<String, Object> beanMap = BeanMapperUtils.trans2Map(clientInfoDTO);
        Map<String, Object> paramMap = checkNull(beanMap);
        HttpResMsg httpResMsg = appRequestService.callAppApi(Jackson.toJsonString(paramMap), APP_REFER_URL);
        if (httpResMsg != null && httpResMsg.isSuccess()) {
            AppApiRes appApiRes = JSON.parseObject(httpResMsg.getResponseStr(), AppApiRes.class);
            ReferResDTO referResDTO = JSON.parseObject(appApiRes.getContent(), ReferResDTO.class);
            return referResDTO;
        } else {
            return null;
        }
    }

    @Override
    public RegisterForFeResDTO register(RegisterForFeReqDTO registerReqDTO) {
        RegisterForFeResDTO registerForFeResDTO = new RegisterForFeResDTO();
        Map<String, Object> beanMap = BeanMapperUtils.trans2Map(registerReqDTO);
        Map<String, Object> paramMap = checkNull(beanMap);
        HttpResMsg httpResMsg = appRequestService.callAppApi(Jackson.toJsonString(paramMap), APP_REGISTER_URL);
        if (httpResMsg != null) {
            AppApiRes appApiRes = JSON.parseObject(httpResMsg.getResponseStr(), AppApiRes.class);
            registerForFeResDTO.setResultCode(appApiRes.getResultCode());
            registerForFeResDTO.setErrorMsg(appApiRes.getErrMsg());
            return registerForFeResDTO;
        } else {
            return null;
        }
    }

    @Override
    public UserStatementListResDTO getStatementList(UserStatementReqDTO userStatementReqDTO) {
        Map<String, Object> beanMap = BeanMapperUtils.trans2Map(userStatementReqDTO);
        Map<String, Object> paramMap = checkNull(beanMap);
        HttpResMsg httpResMsg = appRequestService.callAppApi(Jackson.toJsonString(paramMap), APP_STATEMENTLIST_URL);
        if (httpResMsg != null) {
            AppApiRes appApiRes = JSON.parseObject(httpResMsg.getResponseStr(), AppApiRes.class);
            UserStatementListResDTO statementListResDTO = JSON.parseObject(appApiRes.getContent(), UserStatementListResDTO.class);
            statementListResDTO.setResultCode(appApiRes.getResultCode());
            statementListResDTO.setErrorMsg(appApiRes.getErrMsg());
            return statementListResDTO;
        } else {
            return null;
        }
    }

    @Override
    public String getPdfStatement(PdfStatementReqDTO pdfStatementReqDTO) {
        Map<String, Object> beanMap = BeanMapperUtils.trans2Map(pdfStatementReqDTO);
        Map<String, Object> paramMap = checkNull(beanMap);
        HttpResMsg httpResMsg = appRequestService.callAppApi(Jackson.toJsonString(paramMap), APP_PDFSTATEMENT_URL);
        return httpResMsg.getResponseStr();
    }

    @Override
    public ChangeRiskResDTO changeRisk(ChangeRiskReqDTO changeRiskReqDTO) {
        ChangeRiskResDTO changeRiskResDTO = new ChangeRiskResDTO();
        Map<String, Object> beanMap = BeanMapperUtils.trans2Map(changeRiskReqDTO);
        Map<String, Object> paramMap = checkNull(beanMap);
        HttpResMsg httpResMsg = appRequestService.callAppApi(Jackson.toJsonString(paramMap), APP_CHANGERISK_URL);
        if (httpResMsg != null) {
            AppApiRes appApiRes = JSON.parseObject(httpResMsg.getResponseStr(), AppApiRes.class);
            changeRiskResDTO.setResultCode(appApiRes.getResultCode());
            changeRiskResDTO.setErrorMsg(appApiRes.getErrMsg());
            return changeRiskResDTO;
        } else {
            return null;
        }
    }

    private Map<String, Object> checkNull(Map<String, Object> beanMap) {
        Map<String, Object> paramMap = new HashMap<>();
        for (String key : beanMap.keySet()) {
            if (beanMap.get(key) != null) {
                paramMap.put(key, beanMap.get(key));
            }
        }
        return paramMap;
    }

}
