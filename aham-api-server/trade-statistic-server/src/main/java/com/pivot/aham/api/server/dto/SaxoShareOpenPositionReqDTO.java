/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author ASUS
 */
@Data
@Accessors(chain = true)
public class SaxoShareOpenPositionReqDTO extends BaseDTO {
    
    private Date startCreateTime;

    private Date endCreateTime;

    private Integer pageNo;

    private Integer pageSize;
}
