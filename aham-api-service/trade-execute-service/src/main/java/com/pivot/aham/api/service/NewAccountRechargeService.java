package com.pivot.aham.api.service;

import com.pivot.aham.api.service.mapper.model.TAccountRechargePO;

import java.util.List;

/**
 * 只用作测试
 *
 * @author addison
 * @since 2018年12月10日
 */
public interface NewAccountRechargeService {

    public List<TAccountRechargePO> listByAccountId(TAccountRechargePO po);


}
