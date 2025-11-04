/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.NameAliasManagePO;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.session.RowBounds;

/**
 *
 * @author HP
 */
public interface NameAliasManageService {

    void insertNameAliasManage(NameAliasManagePO nameAliasManagePO);

    List<NameAliasManagePO> getNameAliasManageByDate(RowBounds rowBound, Date startTime, Date endTime);

}
