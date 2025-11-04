package com.pivot.aham.api.server.dto.req;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;

import java.util.Date;

@Data
public class BalanceApplyReqDTO extends BaseDTO {

    private Date applyDate;

    public BalanceApplyReqDTO(Date applyDate) {
        this.applyDate = applyDate;
    }


}

