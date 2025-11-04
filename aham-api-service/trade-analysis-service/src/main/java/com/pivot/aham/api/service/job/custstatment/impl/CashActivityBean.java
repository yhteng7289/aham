package com.pivot.aham.api.service.job.custstatment.impl;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * 资金记录
 */
@Data
public class CashActivityBean {
    /**
     * 投资账户资金记录
     */
    List<CashActivityForGoalBean> cashActivityForGoalBeanList = Lists.newArrayList();

    /**
     * 银行账户资金记录
     */
   CashActivityForSquirrelSaveBean cashActivityForSquirrelSaveBean;

}
