package com.pivot.aham.api.service.job;

import java.util.Date;

/**
 * Created by luyang.li on 18/12/17.
 *
 * 自建基金净值计算
 */
public interface AnnualPerformanceFeeJob {

    void calculatePerformanceFee(Date date, Long accountId);

}
