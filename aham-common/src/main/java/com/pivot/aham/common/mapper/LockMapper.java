package com.pivot.aham.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import com.pivot.aham.common.model.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface LockMapper extends BaseMapper<Lock> {

	void cleanExpiredLock();

}
