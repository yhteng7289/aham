package com.pivot.aham.api.service.client.uob;

import com.alibaba.fastjson.JSONObject;
import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class UobClient extends UobClientBase {

    public static void transferMoneyToSaxo(BigDecimal amount) {
        
        // Main Object is the root object on the JSON;
        JSONObject mainObject = new JSONObject();
        mainObject.put("Host", "pivot.com");
        mainObject.put("endToEndId", "1122719657501044799");
        mainObject.put("purposeCode", "OTHR");
        mainObject.put("transactionReference", "112271965750104474");
        
        // Sender Account Information
        JSONObject senderAccountObject = new JSONObject();        
        HashMap senderAccountMap = new HashMap();        
        senderAccountObject.put("accountCurrency", "SGD");
        senderAccountObject.put("accountNumber", "3523095739");
        senderAccountObject.put("accountType", "SGD");
        senderAccountMap.put("account" , senderAccountObject);        
        mainObject.put("originatorDetails", senderAccountMap);
        // Sender Account Information
        
        // Payment Information
        JSONObject paymentObject = new JSONObject();
        paymentObject.put("amount", amount);
        paymentObject.put("currency", "SGD");
        mainObject.put("paymentAmount", paymentObject);   
        // Payment Information
              
        // Receiver Account Information
        JSONObject receiverAccountObject = new JSONObject();
        HashMap receiverAccountMap = new HashMap();                
        receiverAccountObject.put("accountCurrency", "SGD");
        receiverAccountObject.put("accountNumber", "147125793003");
        receiverAccountObject.put("accountType", "SGD");
        receiverAccountMap.put("account" , senderAccountObject);     
        receiverAccountMap.put("accountName" , "Saxo Capital Markets Pte.Ltd."); 
        receiverAccountMap.put("bic" , "HSBCSGS0XXX"); 
        receiverAccountMap.put("proxyType" , ""); 
        receiverAccountMap.put("proxyValue" , "");         
        receiverAccountObject.put("receiverDetails", receiverAccountMap);
        // Receiver Account Information

    }

    public static void getAccountBalance() {
        
        JSONObject mainObject = new JSONObject();
        
        JSONObject senderAccountObject = new JSONObject();        
        HashMap senderAccountMap = new HashMap();        
        senderAccountObject.put("accountCurrency", "SGD");
        senderAccountObject.put("accountNumber", "3523095739");
        senderAccountObject.put("accountType", "SGD");
        senderAccountMap.put("accountInformation", senderAccountObject);
        senderAccountMap.put("uen" , "201716150D");        
        mainObject.put("accounts", senderAccountMap);
        

    }
    
    

    private static String readPrivateKeyContent(File file) {
        return "";
    }

    private static String readPublicKeyContent(File file) {
        return "";
    }

    private static Map getJWTHeader() {
        Map header = new HashMap();
        header.put("alg", "RS256");
        header.put("typ", "JWT");
        return header;
    }

}
