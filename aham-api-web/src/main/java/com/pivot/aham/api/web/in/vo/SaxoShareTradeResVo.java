/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.web.in.vo;

import io.swagger.annotations.ApiModel;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import lombok.Data;
import com.pivot.aham.common.core.support.file.excel.annotation.ExcelField;

/**
 *
 * @author ASUS
 */

@Data
@ApiModel("SaxoShareTradeResVo-SaxoShareTradeResVo")

public class SaxoShareTradeResVo {
    
    @ExcelField(title = "id")
    private Long id;
    @ExcelField(title = "productCode")
    private String productCode;
    @ExcelField(title = "saxoTradeShare")
    private BigDecimal saxoTradeShare;
    @ExcelField(title = "dasTradeShare")
    private BigDecimal dasTradeShare;
    @ExcelField(title = "saxoCommission")
    private BigDecimal saxoCommission;
    @ExcelField(title = "dasCommission")
    private BigDecimal dasCommission;
    @ExcelField(title = "statusDes")
    private String statusDes;
    @ExcelField(title = "fileName")
    private String fileName;
    @ExcelField(title = "orderNumber")
    private BigInteger orderNumber;
    @ExcelField(title = "transNumber")
    private String transNumber;
    @ExcelField(title = "compareTime")
    private Date compareTime; 
}
