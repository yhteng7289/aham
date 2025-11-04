package com.pivot.aham.api.web.app.dto.reqdto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class WithdrawAlscDTO extends BaseDTO {

    private String clientid;

    private String goalid;

    // local or OverSea
    private String type;

    private String amt;

    private String bankname;

    private String bankacctno;

    private String bankcode;

    private String date;

    private String time;

    // ID or SG - Indonesia / Singapore
    private String country;

    // SquirrelCashAccount / BankAccount
    private String targetType;

}
