package com.pivot.aham.api.service.client.rest;

import com.pivot.aham.common.core.util.PropertiesUtil;

public class AhamRestConstant {

    public static String AccessFundTokenKey = "aham_fund_access_token";
    public static String AccessCifTokenKey = "aham_cif_access_token";
    public static String AccessTransactionTokenKey = "aham_transaction_access_token";
    //modelPortfolioConstant
    public static String ModelPortfolios = "model_portfolios";
    public static String Id = "id";
    public static String Name = "name";
    //modelPortfolioDetailParamConstant
    public static String IdParam = "id";
    public static String DateParam = "pasofdate";
    //modelPortfolioDetailResponseConstant
    public static String PortfolioDetails = "model_portfolios_details";
    public static String Items = "items";
    public static String PortfolioId = "modelportfolioid";
    public static String PortfolioName = "modelportfolioname";
    public static String Scheme = "Scheme";
    public static String ProductCode = "BloombergTicker";
    public static String Weightage = "weightage";
    public static String ValidFrom = "EffectiveFrom";
    public static String ValidTo = "EffectiveTo";
    public static String Score = "score";
    
    //Product Closing Price
    public static String FundCode = "FundCode";
    public static String FundName = "FundName";
    public static String FundType = "FundType";
    public static String SchemeCode = "SchemeCode";
    public static String ClassName = "ClassName";
    public static String Currency = "Currency";
    public static String NAVDate = "NAVDate";
    public static String NAV = "NAV";
    
    //Order Confirmation
    public static String PAccountId = "paccountid";
    public static String PDateFrom = "pdatefrom";
    public static String PDateTo = "pdateto";
    public static String PScheme = "pscheme";
    public static String PPlan = "pplan";
    public static String PTrantype = "ptrantype";
    public static String confirmTransResult = "daasconfirmedtransactionResult";
    


    public static String getBaseUrl(){
        return PropertiesUtil.getString("aham.api.base.url");
    }

    public static String getTokenAppKey(){
        return PropertiesUtil.getString("aham.api.token.appKey");
    }

    public static String getTokenAppSecret(){
        return PropertiesUtil.getString("aham.api.token.appSecret");
    }

    public static String getTokenGrantType(){
        return PropertiesUtil.getString("aham.api.token.grantType");
    }

    public static String getFundTokenAuthUrl(){
        return PropertiesUtil.getString("aham.api.token.fund");
    }

    public static String getCifTokenAuthUrl(){
        return getBaseUrl()+PropertiesUtil.getString("saxo.openApi.token.cif");
    }
    public static String getTransactionTokenAuthUrl(){
        return getBaseUrl()+PropertiesUtil.getString("saxo.openApi.token.transaction");
    }

}
