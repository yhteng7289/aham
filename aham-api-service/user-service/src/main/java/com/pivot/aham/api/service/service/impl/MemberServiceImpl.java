package com.pivot.aham.api.service.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.api.service.mapper.TMemberMapper;
import com.pivot.aham.api.service.mapper.model.TMember;
import com.pivot.aham.api.service.service.MemberService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.cache.annotation.CacheConfig;


@CacheConfig(cacheNames = "member")
@Service(interfaceClass = MemberService.class)
public class MemberServiceImpl extends BaseServiceImpl<TMember, TMemberMapper> implements MemberService {

}
