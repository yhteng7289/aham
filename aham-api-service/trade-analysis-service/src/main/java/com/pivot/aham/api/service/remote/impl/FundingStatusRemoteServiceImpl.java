package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.req.FundingStatusReqDTO;
import com.pivot.aham.api.server.dto.res.FundingStatusResDTO;
import com.pivot.aham.api.server.remoteservice.FundingStatusRemoteService;
import com.pivot.aham.api.service.mapper.model.FundingStatusPO;
import com.pivot.aham.api.service.service.FundingStatusService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author bjoon
 */
@Service(interfaceClass = FundingStatusRemoteService.class)
@Slf4j
public class FundingStatusRemoteServiceImpl implements FundingStatusRemoteService{
    
    @Resource
    private FundingStatusService fundingStatusService;

    @Override
    public RpcMessage<Page<FundingStatusResDTO>> getFundingStatusPage(FundingStatusReqDTO fundingStatusReqDTO) {
        Page<FundingStatusResDTO> paginationRes = new Page<>();
        Date startCreateTime = fundingStatusReqDTO.getStartCreateTime();
        Date endCreateTime = fundingStatusReqDTO.getEndCreateTime();
        String clientId = fundingStatusReqDTO.getClientId();
        Page<FundingStatusPO> rowBounds = new Page<>(fundingStatusReqDTO.getPageNo(), fundingStatusReqDTO.getPageSize());
        FundingStatusPO fundingStatusPO = BeanMapperUtils.map(fundingStatusReqDTO, FundingStatusPO.class);
        
        List<FundingStatusPO> fundingStatusPOList = fundingStatusService.getFundingStatusPageList(rowBounds, fundingStatusPO, clientId, startCreateTime, endCreateTime);
        List<FundingStatusResDTO> fundingStatusResDTOList = BeanMapperUtils.mapList(fundingStatusPOList, FundingStatusResDTO.class);
        paginationRes = BeanMapperUtils.map(rowBounds, paginationRes.getClass());
        paginationRes.setRecords(fundingStatusResDTOList);
        return RpcMessage.success(paginationRes);
    }

}
