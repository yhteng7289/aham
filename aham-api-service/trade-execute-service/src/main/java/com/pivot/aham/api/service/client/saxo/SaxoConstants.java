package com.pivot.aham.api.service.client.saxo;

import com.pivot.aham.common.core.util.PropertiesUtil;

public class SaxoConstants {

    public static int MaxRefreshTokenTimes = 3;

    public static Long ExchangeOpenTime = 390L;//390分钟

    public static String AccessTokenKey = "saxo_access_token";

    public static String RefreshTokenKey = "saxo_refresh_token";

    public static String getBaseUrl(){
        return PropertiesUtil.getString("saxo.openApi.base.url");
    }

    public static String getStreamingUrl(){
        return PropertiesUtil.getString("saxo.openApi.streaming.url");
    }

    public static String getTokenAppKey(){
        return PropertiesUtil.getString("saxo.openApi.token.appKey");
    }

    public static String getTokenAppSecret(){
        return PropertiesUtil.getString("saxo.openApi.token.appSecret");
    }

    public static String getTokenAppUrl(){
        return PropertiesUtil.getString("saxo.openApi.token.appUrl");
    }

    public static String getTokenAuthUrl(){
        return PropertiesUtil.getString("saxo.openApi.token.authUrl");
    }

    public static String getTokenKeyStoreFilePath(){
        return PropertiesUtil.getString("saxo.openApi.token.keyStoreFile.path");
    }

    public static String getTokenKeyStoreFilePassword(){
        return PropertiesUtil.getString("saxo.openApi.token.keyStoreFile.password");
    }

    public static String getTokenGrantType(){
        return PropertiesUtil.getString("saxo.openApi.token.grantType");
    }

    public static String getUserId(){
        return PropertiesUtil.getString("saxo.openApi.user.id");
    }

    public static String getUserKey(){
        return PropertiesUtil.getString("saxo.openApi.user.key");
    }

    public static String getClientId(){
        return PropertiesUtil.getString("saxo.openApi.client.id");
    }

    public static String getClientKey(){
        return PropertiesUtil.getString("saxo.openApi.client.key");
    }

    public static String getUSDAccountId(){
        return PropertiesUtil.getString("saxo.openApi.usd.accountId");
    }

    public static String getUSDAccountKey(){
        return PropertiesUtil.getString("saxo.openApi.usd.accountKey");
    }

    public static String getSGDAccountId(){
        return PropertiesUtil.getString("saxo.openApi.sgd.accountId");
    }

    public static String getSGDAccountKey(){
        return PropertiesUtil.getString("saxo.openApi.sgd.accountKey");
    }


}
