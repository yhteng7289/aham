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
public class SaxoReconBalanceResDTO extends BaseDTO {
    
    private Long id;
    private BigDecimal saxoCash;
    private BigDecimal dasCash;
    private BigDecimal diffCash;
    private String statusDes;
    private String fileName;
    private String transNumber;
    private Date compareTime;
    
}
