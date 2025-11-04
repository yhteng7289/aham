/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import com.pivot.aham.common.enums.NameAliasManageStatusEnum;

import lombok.experimental.Accessors;

/**
 *
 * @author HP
 */
@Data
@Accessors(chain = true)
public class NameAliasManagePO extends BaseModel {

    private Long id;

    private Long clientId;

    private NameAliasManageStatusEnum status;

    private String systemClientName;

    private String bankClientName;

    private String file1;

    private String file2;

    private String file3;

}
