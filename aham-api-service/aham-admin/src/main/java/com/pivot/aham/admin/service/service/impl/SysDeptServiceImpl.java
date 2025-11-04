package com.pivot.aham.admin.service.service.impl;

import com.pivot.aham.admin.service.service.SysDeptService;
import com.pivot.aham.admin.service.mapper.SysDeptMapper;
import com.pivot.aham.admin.service.mapper.model.SysDept;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;


@CacheConfig(cacheNames = "sysDept")
@Service
public class SysDeptServiceImpl extends BaseServiceImpl<SysDept, SysDeptMapper> implements SysDeptService {
    @Override
    public List<SysDept> queryList(SysDept params) {
        List<SysDept> list = super.queryList(params);
        for (SysDept sysDept : list) {
            if (sysDept != null) {
                if (sysDept.getParentId() != null) {
                    SysDept parent = super.queryById(sysDept.getParentId());
                    if (parent != null) {
                        sysDept.setParentName(parent.getDeptName());
                    } else {
                        sysDept.setParentId(null);
                    }
                }
            }
        }
        return list;
    }
}
