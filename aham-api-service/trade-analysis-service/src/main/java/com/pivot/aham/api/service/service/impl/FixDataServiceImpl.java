package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.FixDataMapper;
import com.pivot.aham.api.service.service.FixDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * Created by luyang.li on 2018/12/24.
 */
@Service
public class FixDataServiceImpl implements FixDataService {

    @Resource
    private FixDataMapper fixDataMapper;

    @Override
    public void deleteFromTable(String tableName, Long id) {
        fixDataMapper.deleteFromTable(tableName, id);
    }

    @Override
    public void updateClientName(String clientName, Long id) {
        fixDataMapper.updateClientName(clientName, id);
    }

    @Override
    public void updateVACash(BigDecimal cashAmount, BigDecimal freezeAmount, BigDecimal usedAmount, Long id) {
        fixDataMapper.updateVACash(cashAmount, freezeAmount, usedAmount, id);
    }

    @Override
    public void updateSaxoStatu(Integer status, Long id) {
        fixDataMapper.updateSaxoStatu(status, id);
    }

    @Override
    public void updateBankVAStatu(Integer status, Long id) {
        fixDataMapper.updateBankVAStatu(status, id);
    }

}
