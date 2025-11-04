package com.pivot.aham.api.server.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by luyang.li on 19/1/9.
 */
@Data
@Accessors(chain = true)
public class ClassfiyPrecentage implements Serializable {
    private String classfiyName;
    private BigDecimal percentage;
}
