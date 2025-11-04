package com.pivot.aham.api.server.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by dexter on 20/4/2020
 */
@Data
public class UobRechargeReq implements Serializable {

    private String bankOrderNo;
    private String clientName;
    private String virtualAccountNo;
    private String currency;
    private String referenceCode;
    private String cashAmount;
    private String tradeTime;
    private String rechargeStatus;

}
