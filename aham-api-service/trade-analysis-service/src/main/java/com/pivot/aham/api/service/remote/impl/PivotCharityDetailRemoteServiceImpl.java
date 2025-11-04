package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.PivotCharityDetailDTO;
import com.pivot.aham.api.server.dto.req.RoundingAccountReqDTO;
import com.pivot.aham.api.server.dto.res.RoundingAccountResDTO;
import com.pivot.aham.api.server.remoteservice.PivotCharityDetailRemoteService;
import com.pivot.aham.api.service.mapper.model.PivotCharityDetailPO;
import com.pivot.aham.api.service.service.PivotCharityDetailService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import java.math.BigDecimal;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service(interfaceClass = PivotCharityDetailRemoteService.class)
@Slf4j
public class PivotCharityDetailRemoteServiceImpl implements PivotCharityDetailRemoteService {

    @Resource
    private PivotCharityDetailService pivotCharityDetailService;

    @Override
    public RpcMessage savePivotCharityDetail(List<PivotCharityDetailDTO> pivotCharityDetailDTOs) {
        List<PivotCharityDetailPO> insertList = BeanMapperUtils.mapList(pivotCharityDetailDTOs, PivotCharityDetailPO.class);

        PivotCharityDetailPO deletePO = new PivotCharityDetailPO();
        deletePO.setRedeemId(pivotCharityDetailDTOs.get(0).getRedeemId());
        pivotCharityDetailService.deleteByRedeemId(deletePO);

        if (CollectionUtils.isNotEmpty(insertList)) {
            pivotCharityDetailService.batchInsert(insertList);
        }
        return RpcMessage.success();
    }

    @Override
    public RpcMessage<BigDecimal> getTotalMoney() {
        return RpcMessage.success(pivotCharityDetailService.getTotalMoney());
    }

    @Override
    public RpcMessage<Page<RoundingAccountResDTO>> getRoundingAccountPage(RoundingAccountReqDTO roundingAccountReqDTO) {
        Page<RoundingAccountResDTO> paginationRes = new Page<>();
        Date startCreateTime = roundingAccountReqDTO.getStartCreateTime();
        Date endCreateTime = roundingAccountReqDTO.getEndCreateTime();
        Page<PivotCharityDetailPO> rowBounds = new Page<>(roundingAccountReqDTO.getPageNo(), roundingAccountReqDTO.getPageSize());
        PivotCharityDetailPO pivotCharityDetailPO = BeanMapperUtils.map(roundingAccountReqDTO, PivotCharityDetailPO.class);
        List<PivotCharityDetailPO> pivotCharityDetailPOList = pivotCharityDetailService.getRoundingPageList(rowBounds, pivotCharityDetailPO, startCreateTime, endCreateTime);
        List<RoundingAccountResDTO> roundingAccountResDTOList = BeanMapperUtils.mapList(pivotCharityDetailPOList, RoundingAccountResDTO.class);
        paginationRes = BeanMapperUtils.map(rowBounds, paginationRes.getClass());
        paginationRes.setRecords(roundingAccountResDTOList);
        return RpcMessage.success(paginationRes);
    }

}
