package com.pivot.aham.api.service.job.custstatment.impl;

import com.google.common.collect.Lists;
import com.pivot.aham.api.service.mapper.model.CashActivityForGoalPO;
import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class CashActivityForGoalBean extends BaseModel {
    private String goalId;
    private String goalName;
    private List<CashActivityForGoalPO> cashActivityForGoalList = Lists.newArrayList();

}
