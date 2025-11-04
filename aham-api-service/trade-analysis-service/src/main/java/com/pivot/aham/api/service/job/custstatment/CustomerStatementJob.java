package com.pivot.aham.api.service.job.custstatment;

public interface CustomerStatementJob {
    void calculateCustomerStatement(String clientId,Integer monthOffset);
}
