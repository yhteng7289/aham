package com.pivot.aham.api.service.client.uob;

import com.pivot.aham.common.core.util.PropertiesUtil;

public class UobConstants {

    public static String getRechargeLogPath() {
        return getPivotFtpOrderBase() + "/confirm/uobOfflineConfirm/recharge/";
    }

    public static String getPaymentConfirmPath() {
        return getPivotFtpConfirmBase() + "/confirm/uobOfflineConfirm/payment/";
    }

    public static String getExchangeConfirmPath() {
        return getPivotFtpConfirmBase() + "/confirm/uobOfflineConfirm/exchange/";
    }

    public static String getPaymentOrderPath() {
        return getPivotFtpOrderBase() + "/order/uobOfflineOrder/payment/";
    }

    public static String getExchangeOrderPath() {
        return getPivotFtpOrderBase() + "/order/uobOfflineOrder/exchange/";
    }

    public static String getPivotFtpOrderBase() {
        return PropertiesUtil.getString("ftp.pivot.order");
    }

    public static String getPivotFtpConfirmBase() {
        return PropertiesUtil.getString("ftp.pivot.confirm");
    }

    public static String getTransferUrl() {
        return PropertiesUtil.getString("uob.transfer.url");
    }
}
