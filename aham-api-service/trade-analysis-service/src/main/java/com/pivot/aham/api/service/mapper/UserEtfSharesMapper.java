package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.UserEtfSharesPO;
import com.pivot.aham.common.core.base.BaseMapper;

import java.util.List;

public interface UserEtfSharesMapper extends BaseMapper<UserEtfSharesPO> {
     List<UserEtfSharesPO> queryListByTime(UserEtfSharesPO userEtfSharesPO);
     UserEtfSharesPO selectByStaticDate(UserEtfSharesPO userEtfSharesPO);

}