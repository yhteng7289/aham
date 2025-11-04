package com.pivot.aham.api.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.service.mapper.UserAssetMapper;
import com.pivot.aham.api.service.mapper.model.UserAssetPO;
import com.pivot.aham.api.service.service.UserAssetService;
import com.pivot.aham.common.core.base.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author addison
 */
@Service
@Slf4j
public class UserAssetServiceImpl extends BaseServiceImpl<UserAssetPO, UserAssetMapper> implements UserAssetService {


    @Override
    public void saveBatch(List<UserAssetPO> userAssetPOs) {
        mapper.saveBatch(userAssetPOs);
    }

    @Override
    public void save(UserAssetPO userAssetPO) {
        mapper.save(userAssetPO);
    }

    @Override
    public void update(UserAssetPO userAssetPO) {
        mapper.updateUserAsset(userAssetPO);
    }

    @Override
    public UserAssetPO queryUserAssetPo(UserAssetPO queryPo) {
        return mapper.queryUserAssetPo(queryPo);
    }

    @Override
    public void saveOrUpdateUserTodayAsset(UserAssetPO userAssetPO) {
        UserAssetPO queryPo = new UserAssetPO();
        queryPo.setClientId(userAssetPO.getClientId());
        queryPo.setAccountId(userAssetPO.getAccountId());
        queryPo.setAssetTime(userAssetPO.getAssetTime());
        queryPo.setProductCode(userAssetPO.getProductCode());
        queryPo.setGoalId(userAssetPO.getGoalId());
        UserAssetPO alreadyExist = mapper.queryUserAssetPo(queryPo);
        if (null == alreadyExist) {
            mapper.save(userAssetPO);
        } else {
            userAssetPO.setId(alreadyExist.getId());
            mapper.updateUserAsset(userAssetPO);
        }
    }

    @Override
    public List<UserAssetPO> queryListByTime(UserAssetPO userAssetPO) {
        return mapper.queryListByTime(userAssetPO);
    }

    @Override
    public List<UserAssetPO> litsUserAsset(Long accountId, List<String> clientIds, Date lastExDate, String productCode) {
        return mapper.litsUserAsset(accountId, clientIds, lastExDate, productCode);
    }

    @Override
	public void saveOrUpdateUserTodayAsset(List<UserAssetPO> userAssetPOList) {
		
		UserAssetPO queryPo = new UserAssetPO();
		queryPo.setAccountId(userAssetPOList.get(0).getAccountId());
        queryPo.setAssetTime(userAssetPOList.get(0).getAssetTime());
        log.info("--queryPo:{}", queryPo);
        List<UserAssetPO> UserAssetDb = mapper.queryListByTime(queryPo);
        log.info("--UserAssetDb.size():{}", UserAssetDb.size());
        for(UserAssetPO userAsset : userAssetPOList) {
        	Boolean notExisted = true;
        	for(UserAssetPO userAssetDb : UserAssetDb) {
        		if(userAsset.getClientId().equals(userAssetDb.getClientId()) && userAsset.getProductCode().equals(userAssetDb.getProductCode()) && userAsset.getGoalId().equals(userAssetDb.getGoalId())) {
        			userAsset.setId(userAssetDb.getId());
                    mapper.updateUserAsset(userAsset);
                    notExisted = false;
                    break;
        		}
        	}
        	if(notExisted) {
        		mapper.save(userAsset);
        	}
        	log.info("--userAsset:{}", JSON.toJSONString(userAsset));
        	
        }
	}
}
