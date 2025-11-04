/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.web.in.vo;

import io.swagger.annotations.ApiModel;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import com.pivot.aham.common.core.support.file.excel.annotation.ExcelField;

/**
 *
 * @author ASUS
 */

@Data
@ApiModel("SaxoShareOpenPositionResVo-SaxoShareOpenPositionResVo")

public class SaxoShareOpenPositionResVo {
    
    @ExcelField(title = "id")
    private Long id;
    @ExcelField(title = "productCode")
    private String productCode;
    @ExcelField(title = "saxoHoldShare")
    private BigDecimal saxoHoldShare;
    @ExcelField(title = "dasHoldShare")
    private BigDecimal dasHoldShare;
    @ExcelField(title = "saxoHoldAmount")
    private BigDecimal saxoHoldAmount;
    @ExcelField(title = "dasHoldAmount")
    private BigDecimal dasHoldAmount;
    @ExcelField(title = "statusDes")
    private String statusDes;
    @ExcelField(title = "fileName")
    private String fileName;
    @ExcelField(title = "transNumber")
    private String transNumber;
    @ExcelField(title = "compareTime")
    private Date compareTime; 
}
