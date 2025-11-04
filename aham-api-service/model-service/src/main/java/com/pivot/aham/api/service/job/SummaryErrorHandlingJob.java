package com.pivot.aham.api.service.job;

import java.util.Date;

public interface SummaryErrorHandlingJob {

    /**
     * 同步 errorHandlingDetail
     *
     * @param now
     */
    void summaryErrorHandlingDetail(Date now);
}
