package com.pivot.aham.api.web.app.dto.resdto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class WithdrawAlscResDTO extends BaseDTO{

    private String orderId;

    private String clientId;

    private BigDecimal amt;

    private String bankName;

    private String bankAcctNo;

    private String bankCode;

    private String date;

    private String time;

    private String resultCode;
    private String errorMsg;
}
