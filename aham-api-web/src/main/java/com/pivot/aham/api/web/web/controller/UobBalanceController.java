package com.pivot.aham.api.web.web.controller;

import com.alibaba.fastjson.JSONArray;
import com.pivot.aham.common.core.base.AbstractController;
import io.swagger.annotations.Api;

import com.alibaba.fastjson.JSONObject;
import com.pivot.aham.api.server.dto.req.UobBalanceReq;
import com.pivot.aham.api.server.remoteservice.UobTradeRemoteService;
import com.pivot.aham.common.core.base.Message;
import java.io.BufferedReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/v1/uob")
@Api(value = "UOB Get Balance API", description = "UOB Get Balance API")
@Slf4j

public class UobBalanceController extends AbstractController {

    @Resource
    private UobTradeRemoteService uobTradeRemoteService;

    private final static String BALANCE = "1";
    private final static String RECHARGE = "2";

    @Autowired
    private Environment env;

    @GetMapping(value = "/balance")
    public Message getUobBalance(@RequestParam("passcode") String passCode) throws IOException {

        StringBuilder sb = new StringBuilder();

        Runtime rt = Runtime.getRuntime();
        String profile = "";
        String[] activeProfiles = env.getActiveProfiles();
        if (activeProfiles.length > 0) {
            if (activeProfiles.length == 1) {
                profile = activeProfiles[0];
            }
        } else {
            throw new RuntimeException("None profile found");
        }

        StringBuilder cmdBuilder = new StringBuilder();
        cmdBuilder.append("java -jar /root/deployment/UOB.jar ").append(profile).append(" ").append(BALANCE);

        log.info("commands : " + cmdBuilder.toString());
        Process process = rt.exec(cmdBuilder.toString());
        InputStream stdIn = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(stdIn);
        BufferedReader br = new BufferedReader(isr);

        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        process.destroy();

        log.info("returnResp {} ", sb.toString());
        try {
            JSONObject jsonObject = JSONObject.parseObject(sb.toString());
            if (jsonObject.containsKey("accounts")) {
                JSONArray accountArray = jsonObject.getJSONArray("accounts");
                if (accountArray.size() > 0) {
                    for (int i = 0; i < accountArray.size(); i++) {
                        UobBalanceReq uobBalanceReq = new UobBalanceReq();
                        JSONObject accountObj = accountArray.getJSONObject(i);
                        if (accountObj != null) {
                            BigDecimal odDrawingLimit = (BigDecimal) accountObj.getBigDecimal("odDrawingLimit");
                            String accountName = (String) accountObj.get("accountName");
                            BigDecimal subAccountAllocatedBalance = (BigDecimal) accountObj.getBigDecimal("subAccountAllocatedBalance");
                            BigDecimal todayDebit = (BigDecimal) accountObj.getBigDecimal("todayDebit");
                            String samcPrimaryAccountIndicator = (String) accountObj.get("samcPrimaryAccountIndicator");
                            String accountType = (String) accountObj.get("accountType");
                            String accountCurrency = (String) accountObj.get("accountCurrency");
                            String accountNumber = (String) accountObj.get("accountNumber");
                            Integer branch = accountObj.getInteger("branch");
                            String masterAccountNumberForSubAccount = (String) accountObj.get("masterAccountNumberForSubAccount");
                            BigDecimal totalAvailabilityFloat = (BigDecimal) accountObj.getBigDecimal("totalAvailabilityFloat");
                            BigDecimal todayCredit = (BigDecimal) accountObj.getBigDecimal("todayCredit");

                            JSONObject availableBalanceObj = accountObj.getJSONObject("availableBalance");
                            BigDecimal availableBalanceAmount = (BigDecimal) availableBalanceObj.getBigDecimal("amount");
                            String availableBalanceCurrency = (String) availableBalanceObj.get("currency");

                            JSONObject ledgerBalanceObj = accountObj.getJSONObject("ledgerBalance");
                            BigDecimal ledgerBalanceAmount = (BigDecimal) ledgerBalanceObj.getBigDecimal("amount");
                            String ledgerBalanceCurrency = (String) ledgerBalanceObj.get("currency");

                            JSONObject accountBalanceObj = accountObj.getJSONObject("accountBalance");
                            BigDecimal accountBalanceAmount = (BigDecimal) accountBalanceObj.getBigDecimal("amount");
                            String accountBalanceCurrency = (String) accountBalanceObj.get("currency");

                            uobBalanceReq.setOdDrawingLimit(odDrawingLimit);
                            uobBalanceReq.setAccountName(accountName);
                            uobBalanceReq.setSubAccountAllocatedBalance(subAccountAllocatedBalance);
                            uobBalanceReq.setTodayDebit(todayDebit);
                            uobBalanceReq.setSamcPrimaryAccountIndicator(samcPrimaryAccountIndicator);
                            uobBalanceReq.setAccountType(accountType);
                            uobBalanceReq.setAccountCurrency(accountCurrency);
                            uobBalanceReq.setAccountNumber(accountNumber);
                            uobBalanceReq.setBranch(branch);
                            uobBalanceReq.setTotalAvailabilityFloat(totalAvailabilityFloat);
                            uobBalanceReq.setMasterAccountNumberForSubAccount(masterAccountNumberForSubAccount);
                            uobBalanceReq.setTodayCredit(todayCredit);
                            uobBalanceReq.setAvailableBalanceAmount(availableBalanceAmount);
                            uobBalanceReq.setAvailableBalanceCurrency(availableBalanceCurrency);
                            uobBalanceReq.setLedgerBalanceAmount(ledgerBalanceAmount);
                            uobBalanceReq.setLedgerBalanceCurrency(ledgerBalanceCurrency);
                            uobBalanceReq.setAccountBalanceAmount(accountBalanceAmount);
                            uobBalanceReq.setAccountBalanceCurrency(accountBalanceCurrency);
                            log.info("uobBalanceReq Insert: {} ", uobBalanceReq);
                            uobTradeRemoteService.insertUobBalanceRecords(uobBalanceReq);
                        }
                    }
                } else {
                    return Message.error("No record found");
                }
            } else {
                return Message.error("No record found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.error("Exception found");
        }
        return Message.success(sb.toString());
    }

    @PostMapping(value = "/recharge")
    public JSONObject recharge(@RequestParam("amount") String amount) throws IOException {

        StringBuilder sb = new StringBuilder();
        Runtime rt = Runtime.getRuntime();
        String profile = "";
        String[] activeProfiles = env.getActiveProfiles();
        if (activeProfiles.length > 0) {
            if (activeProfiles.length == 1) {
                profile = activeProfiles[0];
            }
        } else {
            throw new RuntimeException("None profile found");
        }

        StringBuilder cmdBuilder = new StringBuilder();
        cmdBuilder.append(" java -jar /root/deployment/UOB.jar ");
        cmdBuilder.append(profile).append(" ").append(RECHARGE).append(" ").append(amount);

        log.info("commands : " + cmdBuilder.toString());
        Process process = rt.exec(cmdBuilder.toString());
        InputStream stdIn = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(stdIn);
        BufferedReader br = new BufferedReader(isr);

        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        process.destroy();

        return JSONObject.parseObject(sb.toString());
    }

    @PostMapping(value = "/recharge.api")
    public JSONObject rechargeApi(@RequestParam("amount") String amount) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.postForObject("http://localhost:9040/v1/uob/recharge?amount=" + amount, null, String.class);
            return JSONObject.parseObject(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
