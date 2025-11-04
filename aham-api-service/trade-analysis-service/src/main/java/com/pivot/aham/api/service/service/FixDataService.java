package com.pivot.aham.api.service.service;

import java.math.BigDecimal;

/**
 * Created by luyang.li on 2018/12/24.
 */
public interface FixDataService {
    void deleteFromTable(String tableName, Long id);

    void updateClientName(String clientName, Long id);

    void updateVACash(BigDecimal cashAmount, BigDecimal freezeAmount, BigDecimal usedAmount, Long id);

    void updateSaxoStatu(Integer status, Long id);

    void updateBankVAStatu(Integer status, Long id);

}
