package com.pivot.aham.api.service.client.saxo.resp;

import com.google.common.collect.Lists;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderActivitiesResp {

    private List<ActivityData> data;

    @Data
    public class ActivityData{
        private String logId;
        private String accountId;
        private String clientId;
        private String activityTime;
        private String buySell;
        private BigDecimal amount;
        private BigDecimal fillAmount;
        private BigDecimal filledAmount;
        private BigDecimal averagePrice;
        private BigDecimal executionPrice;
        private String positionId;
        private BigDecimal price;
        private String orderId;
        private String orderType;
        private String status;
        private String subStatus;
        private Integer uic;
    }

    @Data
    public class OrderLogStatus{
        public static final String Cancelled = "Cancelled";
        public static final String Changed = "Changed";
        public static final String DoneForDay = "DoneForDay";
        public static final String Expired = "Expired";
        public static final String Fill = "Fill";
        public static final String FinalFill = "FinalFill";
        public static final String Placed = "Placed";
        public static final String Working = "Working";
    }

    @Data
    public class OrderSubStatus{
        public static final String Confirmed = "Confirmed";
        public static final String Rejected = "Rejected";
        public static final String Requested = "Requested";
        public static final String RouteRequested = "RouteRequested";
        public static final String RouteRequestPending = "RouteRequestPending";
    }

    public boolean haveFill() {
        if (data == null) {
            return false;
        }

        for (ActivityData activityData : data) {
            if (OrderLogStatus.Fill.equals(activityData.getStatus())) {
                return true;
            }
        }

        return false;
    }

    public boolean haveFinalFill() {
        if (data == null) {
            return false;
        }

        for (ActivityData activityData : data) {
            if (OrderLogStatus.FinalFill.equals(activityData.getStatus())) {
                return true;
            }
        }

        return false;
    }

    public List<ActivityData> getFillActivity(){
        List<ActivityData> result = Lists.newArrayList();
        if (data == null) {
            return result;
        }

        for (ActivityData activityData : data) {
            if (OrderLogStatus.FinalFill.equals(activityData.getStatus()) || OrderLogStatus.Fill.equals(activityData.getStatus())) {
                result.add(activityData);
            }
        }

        return result;
    }

    public int getFilledShare(){
        if (data == null) {
            return 0;
        }

        int filled = 0;
        for (ActivityData activityData : data) {
            if (OrderLogStatus.FinalFill.equals(activityData.getStatus()) || OrderLogStatus.Fill.equals(activityData.getStatus())) {
                filled+=activityData.getFillAmount().intValue();
            }
        }

        return filled;
    }

}
