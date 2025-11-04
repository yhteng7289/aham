package com.pivot.aham.api.service.mapper.model;

import lombok.Data;

@Data
public class EtfInfoPO {
    private Long id;
    private String etfCode;
    private Integer uic;
    private String exchangeCode;
}
