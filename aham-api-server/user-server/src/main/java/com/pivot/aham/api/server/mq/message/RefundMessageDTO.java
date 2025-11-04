package com.pivot.aham.api.server.mq.message;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;

import java.util.List;

@Data
public class RefundMessageDTO extends BaseDTO {
//    private BankVirtualAccountOrderMsgDTO bankVirtualAccountOrderMsgPO;
    private List<RechargeRefundDTO> needRefundUsers;

}
