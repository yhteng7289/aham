/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.web.in.vo;

import com.pivot.aham.common.core.support.file.excel.annotation.ExcelField;
import io.swagger.annotations.ApiModel;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 *
 * @author ASUS
 */

@Data
@ApiModel("SaxoOpenPositionResVo-SaxoOpenPositionResVo")

public class SaxoOpenPositionResVo {
    
    @ExcelField(title = "id")
    private Long id;
    @ExcelField(title = "saxoHoldMoney")
    private BigDecimal saxoHoldMoney;
    @ExcelField(title = "dasHoldMoney")
    private BigDecimal dasHoldMoney;
    @ExcelField(title = "statusDes")
    private String statusDes;
    @ExcelField(title = "fileName")
    private String fileName;
    @ExcelField(title = "transNumber")
    private String transNumber;
    @ExcelField(title = "compareTime")
    private Date compareTime; 
}
