package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.enums.recharge.UobRechargeStatusEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class ReceivedTransferReq implements Serializable{
    private UobRechargeStatusEnum rechargeStatus;
    private String bankOrderNo;

}
