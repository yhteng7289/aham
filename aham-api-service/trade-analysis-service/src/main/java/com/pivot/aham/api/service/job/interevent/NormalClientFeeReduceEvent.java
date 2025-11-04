package com.pivot.aham.api.service.job.interevent;

import lombok.Data;

import java.util.Date;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月22日
 */
@Data
public class NormalClientFeeReduceEvent {
    private Long accountId;

    /**
     * 指定扣减哪天的，默认是当前日前的前一天
     */
    private Date date;

}
