package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.UserEtfSharesPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;


public interface UserEtfSharesService extends BaseService<UserEtfSharesPO> {
    List<UserEtfSharesPO> queryListByTime(UserEtfSharesPO userEtfSharesPO);
    UserEtfSharesPO selectByStaticDate(UserEtfSharesPO userEtfSharesPO);
}
