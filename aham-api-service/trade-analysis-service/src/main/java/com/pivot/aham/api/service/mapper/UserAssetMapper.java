package com.pivot.aham.api.service.mapper;


import com.pivot.aham.api.service.mapper.model.UserAssetPO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by luyang.li on 18/12/9.
 */
@Repository
public interface UserAssetMapper extends BaseMapper<UserAssetPO> {


    void saveBatch(List<UserAssetPO> userAssetPOs);

    UserAssetPO queryUserAssetPo(UserAssetPO queryPo);

    void save(UserAssetPO userAssetPO);

    void updateUserAsset(UserAssetPO userAssetPO);

    List<UserAssetPO> queryListByTime(UserAssetPO userAssetPO);

    List<UserAssetPO> litsUserAsset(@Param("accountId") Long accountId,
                                    @Param("clientIds") List<String> clientIds,
                                    @Param("lastExDate") Date lastExDate,
                                    @Param("productCode") String productCode);

}
