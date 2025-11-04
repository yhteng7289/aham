package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by dexter on 20/4/2020
 */
@Data
@Accessors(chain = true)
public class UobRechargeLogDTO extends BaseDTO {

    private String bankOrderNo;
    private String clientName;
    private String virtualAccountNo;
    private String currency;
    private String referenceCode;
    private String cashAmount;
    private String tradeTime;
    private String rechargeStatus;

}
