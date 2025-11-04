package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;

import java.math.BigDecimal;


@Data
//@TableName()
public class SaxoToUobOrderMock extends BaseModel{
    private String transactionId;
    private BigDecimal amount;
}
