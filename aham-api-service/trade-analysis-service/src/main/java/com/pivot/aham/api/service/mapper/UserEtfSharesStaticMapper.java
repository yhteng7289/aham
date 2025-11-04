package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.UserEtfSharesStaticPO;
import com.pivot.aham.common.core.base.BaseMapper;

import java.util.List;

public interface UserEtfSharesStaticMapper extends BaseMapper<UserEtfSharesStaticPO> {
     List<UserEtfSharesStaticPO> queryListByTime(UserEtfSharesStaticPO userEtfSharesStaticPO);
     UserEtfSharesStaticPO selectByStaticDate(UserEtfSharesStaticPO userEtfSharesStaticPO);

}