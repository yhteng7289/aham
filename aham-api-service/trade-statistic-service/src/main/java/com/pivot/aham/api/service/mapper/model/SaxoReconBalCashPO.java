/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
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
@TableName(value = "t_saxo_bal_cash",resultMap = "SaxoReconBalCashPO")
public class SaxoReconBalCashPO extends BaseModel {
    
    private Long id;
    private BigDecimal saxoCash;
    private BigDecimal dasCash;
    private BigDecimal diffCash;
    private String statusDes;
    private String fileName;
    private String transNumber;
    private Date compareTime; 
    
    private Date startDate;
    private Date endDate;
    
}
