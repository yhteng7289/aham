package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.PivotPftAccountMapper;
import com.pivot.aham.api.service.mapper.model.PivotPftAccountPO;
import com.pivot.aham.api.service.service.PivotPftAccountService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class PivotPftAccountServiceImpl extends BaseServiceImpl<PivotPftAccountPO, PivotPftAccountMapper> implements PivotPftAccountService {


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateAccount(List<Long> ids,List<PivotPftAccountPO> pivotPftAccountPOList) {
        //先删除
        for(Long id:ids) {
            this.delete(id);
        }
//        for(PivotPftAccountPO pivotPftAccountPO:pivotPftAccountPOList){
//            pivotPftAccountPO.setDataVersion(dataVersion+1);
//        }
        //再插入
        this.updateBatch(pivotPftAccountPOList);
    }
}