/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author HP
 */
@Data
@Accessors(chain = true)
public class OperationalRecordPO extends BaseModel {

    private Long id;

    private String activity;

}
