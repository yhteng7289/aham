package com.pivot.aham.api.service.mapper.model;

import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class TradeOrderTypePO {
    
    private Long id;
    private String saxoOrderCode;
    private String isRebalanceOrder;
    private Date createTime;
    private Date updateTime;

}
