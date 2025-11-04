package com.pivot.aham.api.service.client.saxo.resp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PositionDetailResp {
    private String positionId;
    private PositionDetails positionDetails;

    @Data
    public class PositionDetails {
        private CostData CloseCost;
        private CostData OpenCost;

        public class CostData {
            private BigDecimal Commission;
            private BigDecimal ExchangeFee;
            private BigDecimal ExternalCharges;
            private BigDecimal PerformanceFee;
            private BigDecimal StampDuty;

            public BigDecimal getCommission() {
                return Commission == null ? BigDecimal.ZERO : Commission;
            }

            public void setCommission(BigDecimal commission) {
                Commission = commission;
            }

            public BigDecimal getExchangeFee() {
                return ExchangeFee == null ? BigDecimal.ZERO : ExchangeFee;
            }

            public void setExchangeFee(BigDecimal exchangeFee) {
                ExchangeFee = exchangeFee;
            }

            public BigDecimal getExternalCharges() {
                return ExternalCharges == null ? BigDecimal.ZERO : ExternalCharges;
            }

            public void setExternalCharges(BigDecimal externalCharges) {
                ExternalCharges = externalCharges;
            }

            public BigDecimal getPerformanceFee() {
                return PerformanceFee == null ? BigDecimal.ZERO : PerformanceFee;
            }

            public void setPerformanceFee(BigDecimal performanceFee) {
                PerformanceFee = performanceFee;
            }

            public BigDecimal getStampDuty() {
                return StampDuty == null ? BigDecimal.ZERO : StampDuty;
            }

            public void setStampDuty(BigDecimal stampDuty) {
                StampDuty = stampDuty;
            }
        }
    }

}
