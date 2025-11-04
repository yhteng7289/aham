package com.pivot.aham.api.service.client.saxo.resp;

import lombok.Data;

import java.util.List;

/**
 * Created by hao.tong on 2018/12/26.
 */
@Data
public class ExchangeInfoResp {
    private Boolean AllDay;
    private String CountryCode;
    private String Currency;
    private String ExchangeId;
    private String Mic;
    private String Name;
    private Integer TimeZone;
    private String TimeZoneAbbreviation;
    private String TimeZoneOffset;
    private List<ExchangeSession> exchangeSessions;

    @Data
    public class ExchangeSession{
        private String EndTime;
        private String StartTime;
        private String State;
    }
}
