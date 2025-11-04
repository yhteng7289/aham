package com.pivot.aham.api.web.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.pivot.aham.api.server.dto.req.UobNotificationReq;
import com.pivot.aham.api.server.remoteservice.UobTradeRemoteService;
import com.pivot.aham.api.web.uob.UobConstant;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.support.security.SecuredGCMUsage;
import static com.pivot.aham.common.core.support.security.SecuredGCMUsage.TAG_BIT_LENGTH;
import com.pivot.aham.common.core.support.security.SecuredRSAUsage;
import com.pivot.aham.common.core.util.SecurityKeyUtils;
import io.swagger.annotations.Api;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.pivot.aham.common.core.exception.BusinessException;
import java.math.BigDecimal;
import javax.annotation.Resource;

@RestController
@RequestMapping("/notification")
@Api(value = "UOB Notification Callback", description = "UOB Notification Callback")
@Slf4j
public class UobNotifyController extends AbstractController {

    @Resource
    private UobTradeRemoteService uobTradeRemoteService;

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity notification(@RequestParam("encryptedPayload") String encryptedPayload,
            @RequestParam("encryptedSessionKey") String encryptedSessionKey,
            @RequestParam("iv") String iv,
            @RequestParam("payloadSignature") String payloadSignature) {

        Long seqId = new Sequence().nextId();
        String uuid = UUID.randomUUID().toString();
        log.info("seqId : {} , uuid : {}, encryptedPayload : {} , encryptedSessionKey : {} , iv : {} , payloadSignature : {} ", seqId, uuid, encryptedPayload, encryptedSessionKey, iv, payloadSignature);

        HashMap hashMap = new HashMap();
        hashMap.put("instructionId", "" + seqId);
        hashMap.put("notificationId", uuid);
        String privateKeyCert = "";
        try {
            String payloadCertPath = UobConstant.getPayloadKeyFile();
            log.info("payloadCertPath {} ", payloadCertPath);
            privateKeyCert = loadPrivateKeyContent(payloadCertPath);
        } catch (IOException e) {
            throw new BusinessException("Fail to load Payload Private Key from [UobNotifyController]");
        }

        try {
            final PrivateKey privateKey = SecurityKeyUtils.parseRSAPrivateKey(privateKeyCert);
            String decryptedSessionKey = SecuredRSAUsage.rsaDecrypt(java.util.Base64.getDecoder().decode(encryptedSessionKey.getBytes()), privateKey);
            log.info("decryptedSessionKey : " + decryptedSessionKey);
            byte[] decodedSessionKey = Base64.getDecoder().decode(decryptedSessionKey);
            SecretKey secretKey = new SecretKeySpec(decodedSessionKey, 0, decodedSessionKey.length, "AES");
            System.out.println("secretKey : " + secretKey.toString());

            byte[] ivBytes = Base64.getDecoder().decode(iv);

            GCMParameterSpec gcmParamSpec = new GCMParameterSpec(TAG_BIT_LENGTH, ivBytes);
            log.info("gcmParamSpec : " + gcmParamSpec);

            // Errors Here
            byte[] returnStr = SecuredGCMUsage.aesDecrypt(Base64.getDecoder().decode(encryptedPayload), secretKey, gcmParamSpec, "aham.ai".getBytes());
            log.info("returnStr {} ", new String(returnStr));

            JSONObject jsonObject = JSONObject.parseObject(new String(returnStr));
            if (jsonObject != null) {
                String event = (String) jsonObject.get("event");
                if (jsonObject.containsKey("data")) {
                    JSONObject dataObj = jsonObject.getJSONObject("data");
                    String accountName = (String) dataObj.get("accountName");
                    String accountType = (String) dataObj.get("accountType");
                    String accountNumber = (String) dataObj.get("accountNumber");
                    String accountCurrency = (String) dataObj.get("accountCurrency");
                    BigDecimal amount = (BigDecimal) dataObj.getBigDecimal("amount");
                    String transactionType = (String) dataObj.get("transactionType");
                    String ourReference = (String) dataObj.get("ourReference");
                    String yourReference = (String) dataObj.get("yourReference");
                    String transactionText = (String) dataObj.get("transactionText");
                    String transactionDateTime = (String) dataObj.get("transactionDateTime");
                    String businessDate = (String) dataObj.get("businessDate");
                    String effectiveDate = (String) dataObj.get("effectiveDate");
                    String subAccountIndicator = (String) dataObj.get("subAccountIndicator");
                    String payNowIndicator = (String) dataObj.get("payNowIndicator");
                    String instructionId = (String) dataObj.get("instructionId");
                    String notificationId = (String) dataObj.get("notificationId");
                    String remittanceInformation = (String) dataObj.get("remittanceInformation");
                    String originatorAccountName = (String) dataObj.get("originatorAccountName");

                    UobNotificationReq uobNotificationReq = new UobNotificationReq();
                    uobNotificationReq.setAccountCurrency(accountCurrency);
                    uobNotificationReq.setAccountName(accountName);
                    uobNotificationReq.setAccountNumber(accountNumber);
                    uobNotificationReq.setAccountType(accountType);
                    uobNotificationReq.setAmount(amount);
                    uobNotificationReq.setBusinessDate(businessDate);
                    uobNotificationReq.setEffectiveDate(effectiveDate);
                    uobNotificationReq.setEvent(event);
                    uobNotificationReq.setInstructionId(instructionId);
                    uobNotificationReq.setNotificationId(notificationId);
                    uobNotificationReq.setOriginatorAccountName(originatorAccountName);
                    uobNotificationReq.setOurReference(ourReference);
                    uobNotificationReq.setPayNowIndicator(payNowIndicator);
                    uobNotificationReq.setRemittanceInformation(remittanceInformation);
                    uobNotificationReq.setSubAccountIndicator(subAccountIndicator);
                    uobNotificationReq.setTransactionDateTime(transactionDateTime);
                    uobNotificationReq.setTransactionText(transactionText);
                    uobNotificationReq.setTransactionType(transactionType);
                    uobNotificationReq.setYourReference(yourReference);
                    log.info("uobNotificationReq Insert: {} ", uobNotificationReq);
                    uobTradeRemoteService.uobNotificationCallback(uobNotificationReq);

                } else {
                    log.info("Fail to get Notification {} ", new String(returnStr));
                }
            }

        } catch (Exception e) {

        }

        return new ResponseEntity<>(hashMap, HttpStatus.OK);
    }

    private String loadPrivateKeyContent(String keyFilePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(keyFilePath)));
    }

}
