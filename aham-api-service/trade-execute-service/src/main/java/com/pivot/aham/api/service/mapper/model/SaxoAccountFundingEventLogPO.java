package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.api.service.client.saxo.resp.AccountFundingResp;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ExceptionUtil;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * ens的log
 */
@Data
public class SaxoAccountFundingEventLogPO {
    private Long id;
    private String clientId;
    private String accountId;
    private String sequenceId;
    private String activityType;
    private Date activityTime;
    private BigDecimal amount;
    private BigDecimal conversionRate;
    private String currencyCode;
    private String fundingEvent;
    private String fundingType;
    private String positionId;
    private Date registrationTime;
    private Date valueDate;
    private Date createTime;

    public static SaxoAccountFundingEventLogPO convert(AccountFundingResp resp){
        try {
            SaxoAccountFundingEventLogPO eventPO = new SaxoAccountFundingEventLogPO();
            eventPO.setClientId(resp.getClientId());
            eventPO.setAccountId(resp.getAccountId());
            eventPO.setSequenceId(resp.getSequenceId());
            eventPO.setActivityType(resp.getActivityType());
            eventPO.setActivityTime(DateUtils.parseDate(resp.getActivityTime(), DateUtils.DATE_TIME_FORMAT_UTC));
            eventPO.setAmount(resp.getAmount());
            eventPO.setConversionRate(resp.getConversionRate());
            eventPO.setCurrencyCode(resp.getCurrencyCode());
            eventPO.setFundingEvent(resp.getFundingEvent());
            eventPO.setFundingType(resp.getFundingType());
            eventPO.setPositionId(resp.getPositionId());
            eventPO.setRegistrationTime(DateUtils.parseDate(resp.getRegistrationTime(), DateUtils.DATE_TIME_FORMAT_UTC));
            eventPO.setValueDate(DateUtils.parseDate(resp.getValueDate(), DateUtils.DATE_FORMAT));
            return eventPO;
        } catch (Exception e) {
            throw new BusinessException(ExceptionUtil.getStackTraceAsString(e));
        }
    }
}
