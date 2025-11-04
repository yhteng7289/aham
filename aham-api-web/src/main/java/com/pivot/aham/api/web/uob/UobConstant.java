/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.web.uob;

import com.pivot.aham.common.core.util.PropertiesUtil;

/**
 *
 * @author HP
 */
public class UobConstant {

    public static String getUobMakePaymentApi() {
        return PropertiesUtil.getString("uob.openApi.make.payment.api");
    }

    public static String getUobGetBalanceApi() {
        return PropertiesUtil.getString("uob.openApi.balance.api");
    }

    public static String getUobApplicationId() {
        return PropertiesUtil.getString("uob.openApi.application.id");
    }

    public static String getUobClientId() {
        return PropertiesUtil.getString("uob.openApi.client.id");
    }

    public static String getUobApiKey() {
        return PropertiesUtil.getString("uob.openApi.api.key");
    }

    public static String getPivotSgdAccountNumber() {
        return PropertiesUtil.getString("pivot.account.sgd.number");
    }

    public static String getPivotUsdAccountNumber() {
        return PropertiesUtil.getString("pivot.account.usd.number");
    }

    public static String getPivotAccountReference() {
        return PropertiesUtil.getString("pivot.account.reference");
    }

    public static String getPivotAccountPurposeCode() {
        return PropertiesUtil.getString("pivot.account.purpose.code");
    }

    public static String getPivotAccountSgdBillerCode() {
        return PropertiesUtil.getString("pivot.account.sgd.biller.code");
    }

    public static String getPivotAccountUsdBillerCode() {
        return PropertiesUtil.getString("pivot.account.usd.biller.code");
    }

    public static String getPivotAccountCompanyId() {
        return PropertiesUtil.getString("pivot.account.company.id");
    }

    public static String getPivotAccountType() {
        return PropertiesUtil.getString("pivot.account.type");
    }

    public static String getPivotAccountCurrency() {
        return PropertiesUtil.getString("pivot.account.currency");
    }

    public static String getPivotAccountHost() {
        return PropertiesUtil.getString("pivot.account.host");
    }

    public static String getPivotAccountUen() {
        return PropertiesUtil.getString("pivot.account.uen");
    }

    public static String getPivotAad() {
        return PropertiesUtil.getString("pivot.account.aad");
    }

    public static String getAuthKeyFile() {
        return PropertiesUtil.getString("uob.auth.key.path");
    }

    public static String getJwtKeyFile() {
        return PropertiesUtil.getString("uob.jwt.key.path");
    }

    public static String getPayloadKeyFile() {
        return PropertiesUtil.getString("uob.payload.key.path");
    }

    public static String getSaxoAccountName() {
        return PropertiesUtil.getString("saxo.account.name");
    }

    public static String getSaxoAccountType() {
        return PropertiesUtil.getString("saxo.account.type");
    }

    public static String getSaxoAccountBic() {
        return PropertiesUtil.getString("saxo.account.bic");
    }

    public static String getSaxoAccountCurrency() {
        return PropertiesUtil.getString("saxo.account.currency");
    }

    public static String getSaxoAccountNumber() {
        return PropertiesUtil.getString("saxo.account.number");
    }

    public static String getSaxoAccountReference() {
        return PropertiesUtil.getString("saxo.account.reference");
    }

    public static String getPassCode() {
        return PropertiesUtil.getString("app.passcode");
    }

    public static String getTransferUrl() {
        return PropertiesUtil.getString("uob.transfer.url");
    }

}
