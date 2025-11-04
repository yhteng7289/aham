/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.NameAliasManageMapper;
import com.pivot.aham.api.service.mapper.model.NameAliasManagePO;
import com.pivot.aham.api.service.service.NameAliasManageService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author HP
 */
@Service
@Slf4j
public class NameAliasManageServiceImpl extends BaseServiceImpl<NameAliasManagePO, NameAliasManageMapper> implements NameAliasManageService {

    @Autowired
    private NameAliasManageMapper nameAliasManageMapper;

    @Override
    public void insertNameAliasManage(NameAliasManagePO nameAliasManagePO) {
        nameAliasManageMapper.insertNameAliasManage(nameAliasManagePO);
    }

    @Override
    public List<NameAliasManagePO> getNameAliasManageByDate(RowBounds rowBound, Date startTime, Date endTime) {
        return nameAliasManageMapper.getNameAliasManageByDate(rowBound, startTime, endTime);
    }

}
