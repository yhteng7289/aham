package com.pivot.aham.api.web.web.controller;

import com.pivot.aham.api.web.web.vo.req.InsertRechargeLogReqVo;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.monitor.PrometheusMethodMonitor;
import com.pivot.aham.common.core.support.email.Email;
import com.pivot.aham.common.core.util.EmailUtil;
import com.pivot.aham.api.server.dto.req.UobRechargeReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.pivot.aham.api.server.remoteservice.UobSaveRechargeLogRemoteService;

import java.io.IOException;
import java.util.List;
import javax.annotation.Resource;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.DateUtils;
import javax.validation.Valid;
import org.springframework.messaging.MessagingException;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("api/Recharge")
@Slf4j
public class RechargeController extends AbstractController {
    
    @Resource
    private UobSaveRechargeLogRemoteService uobSaveRechargeLogRemoteService;
    
    @RequestMapping(value = "rechargeLog")
    @ResponseBody
    @PrometheusMethodMonitor
    public Message getLog() {
        
        return Message.success(uobSaveRechargeLogRemoteService.runRechargeLog());
    }
    
    @RequestMapping(value = "insertRechargeLog")
    @ResponseBody
    @PrometheusMethodMonitor
    public Message saveLog(@RequestBody @Valid InsertRechargeLogReqVo insertRechargeLogReqVo) {
        
        List<UobRechargeReq> uobRechargeReqs = insertRechargeLogReqVo.convertToReq();
        
        RpcMessage<String> statusSave =  uobSaveRechargeLogRemoteService.insertRechargeLog(uobRechargeReqs); // Edit By WooiTatt
        if(statusSave.isSuccess()){
            return Message.success("Successfully");
        }else{
            return Message.error(statusSave.getErrMsg());
        }
        
    }
    
    @RequestMapping(value = "insertOneRechargeLog")
    @ResponseBody
    @PrometheusMethodMonitor
    public Message saveOneLog(@RequestBody @Valid InsertRechargeLogReqVo insertRechargeLogReqVo) {
        
        List<UobRechargeReq> uobRechargeReqs = insertRechargeLogReqVo.convertToReq();
        return Message.success(uobSaveRechargeLogRemoteService.insertRechargeLog(uobRechargeReqs));
    }
    
    public void emailTest(StringBuffer body) throws IOException, MessagingException {

        String topic = "test email" + DateUtils.getDate();

        Email email = new Email();
        email.setSSL(true);
        email.setSendTo("dexter6855@hotmail.com");
        email.setTopic(topic);
        email.setBody(body.toString());

        EmailUtil.sendEmail(email);

    }

    
}
