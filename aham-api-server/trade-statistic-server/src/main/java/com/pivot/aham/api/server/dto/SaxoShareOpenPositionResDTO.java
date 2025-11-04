/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author ASUS
 */
@Data
@Accessors(chain = true)
public class SaxoShareOpenPositionResDTO extends BaseDTO {
    
    private Long id;
    private String productCode;
    private BigDecimal saxoHoldShare;
    private BigDecimal dasHoldShare;
    private BigDecimal saxoHoldAmount;
    private BigDecimal dasHoldAmount;
    private String statusDes;
    private String fileName;
    private String transNumber;
    private Date compareTime; 
    
}
