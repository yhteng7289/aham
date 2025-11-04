package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.api.service.client.saxo.resp.OrderActivitiesResp;
import com.pivot.aham.common.core.util.DateUtils;
import lombok.Data;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class SaxoOrderActivityPO {
	private Long id;
	private String logId;
	private String accountId;
	private String clientId;
	private Date activityTime;
	private String buySell;
	private Integer amount;
	//股数
	private Integer fillAmount;
	private Integer filledAmount;
	private BigDecimal averagePrice;
	private BigDecimal executionPrice;
	private BigDecimal stopLimitPrice;
	private BigDecimal price;
	private String orderId;
	private String orderType;
	private String status;
	private String subStatus;
	private Integer uic;
	private String positionId;
	private Date createTime;

	public boolean isFillActivity(){
		return (OrderActivitiesResp.OrderLogStatus.Fill.equals(this.status) || OrderActivitiesResp.OrderLogStatus.FinalFill.equals(this.status));
	}

	public static SaxoOrderActivityPO convert(OrderActivitiesResp.ActivityData activityData) throws Exception{
		DateFormat format = new SimpleDateFormat(DateUtils.DATE_TIME_FORMAT_UTC);

		SaxoOrderActivityPO orderActivity = new SaxoOrderActivityPO();
		orderActivity.setLogId(activityData.getLogId());
		orderActivity.setAccountId(activityData.getAccountId());
		orderActivity.setClientId(activityData.getClientId());
		orderActivity.setActivityTime(format.parse(activityData.getActivityTime()));
		orderActivity.setBuySell(activityData.getBuySell());
		orderActivity.setAmount(activityData.getAmount().intValue());
		orderActivity.setOrderId(activityData.getOrderId());
		orderActivity.setOrderType(activityData.getOrderType());
		orderActivity.setStatus(activityData.getStatus());
		orderActivity.setSubStatus(activityData.getSubStatus());
		orderActivity.setUic(activityData.getUic());

		if (activityData.getFillAmount() != null) {
			orderActivity.setFillAmount(activityData.getFillAmount().intValue());
		}

		if (activityData.getFilledAmount() != null) {
			orderActivity.setFilledAmount(activityData.getFilledAmount().intValue());
		}

		if (activityData.getAveragePrice() != null) {
			orderActivity.setAveragePrice(activityData.getAveragePrice());
		}

		if (activityData.getExecutionPrice() != null) {
			orderActivity.setExecutionPrice(activityData.getExecutionPrice());
		}

		if (activityData.getPrice() != null) {
			orderActivity.setPrice(activityData.getPrice());
		}

		orderActivity.setPositionId(activityData.getPositionId());

		return orderActivity;
	}

}

