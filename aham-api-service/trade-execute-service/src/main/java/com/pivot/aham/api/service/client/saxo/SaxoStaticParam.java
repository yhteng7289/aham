package com.pivot.aham.api.service.client.saxo;

/**
 * Created by hao.tong on 2018/12/24.
 */
class SaxoStaticParam {
    static String DurationType = "DayOrder";
    static String FieldGroups = "Commissions,MarketDepth,PriceInfo,PriceInfoDetails,Quote";

    static class AssetType{
        static String Stock = "Stock";
        static String FxSpot = "FxSpot";
    }

    static class OrderType{
        static String Market = "Market";
        static String Limit = "Limit";
    }

    static class BuySellType{
        static String Buy = "Buy";
        static String Sell = "Sell";
    }
}
