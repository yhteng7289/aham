package com.pivot.aham.api.service.service;

import com.pivot.aham.api.server.dto.DividendCallBackDTO;
import com.pivot.aham.api.service.mapper.model.AccountEtfSharesPO;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 2018/12/24.
 */
public interface DividendService {

    /**
     * 处理分红
     *
     * @param accountEtfSharesList
     * @param dividendCallBackDTO
     * @param totalShares
     */
    void handelAccountAndUserDividend(List<AccountEtfSharesPO> accountEtfSharesList,
                                      DividendCallBackDTO dividendCallBackDTO,
                                      BigDecimal totalShares);
}
