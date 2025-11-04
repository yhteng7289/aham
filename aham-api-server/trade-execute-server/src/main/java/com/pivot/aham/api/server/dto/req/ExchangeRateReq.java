package com.pivot.aham.api.server.dto.req;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by hao.tong on 2018/12/24.
 */
@Data
public class ExchangeRateReq  implements Serializable {
    private Date date;
}
