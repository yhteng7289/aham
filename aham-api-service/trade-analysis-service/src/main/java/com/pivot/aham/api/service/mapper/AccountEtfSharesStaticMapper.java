package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.AccountEtfSharesStaticPO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface AccountEtfSharesStaticMapper extends BaseMapper<AccountEtfSharesStaticPO> {
     AccountEtfSharesStaticPO selectByStaticDate(AccountEtfSharesStaticPO accountEtfSharesPO);
     AccountEtfSharesStaticPO getListByDate(@Param("nowDate") Date nowDate);

     List<AccountEtfSharesStaticPO> selectListByStaticDate(AccountEtfSharesStaticPO accountEtfSharesPO);

}