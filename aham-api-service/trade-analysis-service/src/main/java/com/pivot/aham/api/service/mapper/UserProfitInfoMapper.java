package com.pivot.aham.api.service.mapper;


import com.pivot.aham.api.service.mapper.model.UserProfitInfoPO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


/**
 * Created by luyang.li on 18/12/9.
 */
@Repository
public interface UserProfitInfoMapper extends BaseMapper<UserProfitInfoPO> {


    void saveBatch(UserProfitInfoPO userProfitInfoPO);

    void saveUserProfit(UserProfitInfoPO userProfitInfoPO);

    void updateUserProfit(UserProfitInfoPO profitParam);

    UserProfitInfoPO selectOneByTime(UserProfitInfoPO userProfitInfoPO);

    List<UserProfitInfoPO> listByGoalds(@Param("goalIds") List<String> goalIds, @Param("profitDate") Date profitDate);

    List<UserProfitInfoPO> queryProfitList(UserProfitInfoPO userProfitInfoPO);

}
