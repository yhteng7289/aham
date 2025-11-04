package com.pivot.aham.api.service.job.interevent;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月22日
 */
@Data
public class NormalAccountFeeCreateEvent {
    private BigDecimal totalAsset;
    private Long accountId;
    /**
     * 指定fee日期，默认是当天
     */
    private Date date;
}
