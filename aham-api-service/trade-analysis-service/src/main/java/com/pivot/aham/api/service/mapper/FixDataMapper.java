package com.pivot.aham.api.service.mapper;


import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * Created by luyang.li on 18/12/9.
 */
@Repository
public interface FixDataMapper {

    void deleteFromTable(@Param("tableName") String tableName, @Param("id") Long id);

    void updateClientName(@Param("clientName") String clientName, @Param("id") Long id);

    void updateVACash(@Param("cashAmount") BigDecimal cashAmount,
                      @Param("freezeAmount") BigDecimal freezeAmount,
                      @Param("usedAmount") BigDecimal usedAmount,
                      @Param("id") Long id);


    void updateSaxoStatu(@Param("status") Integer status,
                         @Param("id") Long id);

    void updateBankVAStatu(@Param("status") Integer status,
                           @Param("id") Long id);

}
