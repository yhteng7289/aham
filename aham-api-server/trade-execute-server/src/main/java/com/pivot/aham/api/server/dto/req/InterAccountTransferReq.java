package com.pivot.aham.api.server.dto.req;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by hao.tong on 2018/12/24.
 */
@Data
public class InterAccountTransferReq  implements Serializable {
    private BigDecimal applyAmount;
}
