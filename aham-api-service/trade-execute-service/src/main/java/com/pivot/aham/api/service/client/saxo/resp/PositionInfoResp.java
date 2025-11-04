package com.pivot.aham.api.service.client.saxo.resp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PositionInfoResp {
    private String positionId;
    private PositionStatic positionBase;
    private PositionDynamic positionView;

    @Data
    public class PositionStatic {
        private String AccountId;
        private String Amount;
        private String ClientId;
        private Boolean IsMarketOpen;
        private BigDecimal OpenPrice;
        private String SourceOrderId;
        private String Status;
        private Integer Uic;
        private String ValueDate;
    }

    @Data
    public class PositionDynamic {
        private BigDecimal ConversionRateCurrent;
        private BigDecimal ConversionRateClose;
        private BigDecimal ConversionRateOpen;
        private BigDecimal CurrentPrice;
        private Integer CurrentPriceDelayMinutes;
        private String CurrentPriceLastTraded;
        private String CurrentPriceType;
        private BigDecimal Exposure;
        private String ExposureCurrency;
        private BigDecimal ExposureInBaseCurrency;
        private BigDecimal InstrumentPriceDayPercentChange;
        private BigDecimal ProfitLossCurrencyConversion;
        private BigDecimal ProfitLossOnTrade;
        private BigDecimal ProfitLossOnTradeInBaseCurrency;
        private BigDecimal TradeCostsTotal;
        private BigDecimal TradeCostsTotalInBaseCurrency;
    }
}
