package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.AccountNormalFee;
import com.pivot.aham.api.service.mapper.model.FeesConfigPO;
import com.pivot.aham.common.core.base.BaseMapper;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月22日
 */
public interface FeesConfigMapper extends BaseMapper<FeesConfigPO> {
    FeesConfigPO selectByDay(FeesConfigPO feesConfigPO);

}
