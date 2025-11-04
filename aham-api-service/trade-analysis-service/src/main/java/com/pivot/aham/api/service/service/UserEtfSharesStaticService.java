package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.UserEtfSharesStaticPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;


public interface UserEtfSharesStaticService extends BaseService<UserEtfSharesStaticPO> {
    List<UserEtfSharesStaticPO> queryListByTime(UserEtfSharesStaticPO userEtfSharesStaticPO);
    UserEtfSharesStaticPO selectByStaticDate(UserEtfSharesStaticPO userEtfSharesStaticPO);
}
