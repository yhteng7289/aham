package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.PivotFeeDetailDTO;
import com.pivot.aham.api.server.dto.req.TotalFeeAccountReqDTO;
import com.pivot.aham.api.server.dto.res.TotalFeeAccountResDTO;
import com.pivot.aham.api.server.remoteservice.PivotFeeDetailRemoteService;
import com.pivot.aham.api.service.mapper.model.PivotFeeDetailPO;
import com.pivot.aham.api.service.service.impl.PivotFeeDetailServiceImpl;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import com.pivot.aham.common.enums.analysis.OperateTypeEnum;
import java.math.BigDecimal;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;

@Service(interfaceClass = PivotFeeDetailRemoteService.class)
@Slf4j
public class PivotFeeDetailRemoteServiceImpl implements PivotFeeDetailRemoteService {

    @Autowired
    private PivotFeeDetailServiceImpl pivotFeeDetailService;

    @Override
    public RpcMessage savePivotFeeDetail(List<PivotFeeDetailDTO> pivotFeeDetailDTOs) {
        List<PivotFeeDetailPO> insertList = BeanMapperUtils.mapList(pivotFeeDetailDTOs, PivotFeeDetailPO.class);
        PivotFeeDetailPO queryPO = new PivotFeeDetailPO();
        queryPO.setAccountId(pivotFeeDetailDTOs.get(0).getAccountId());
        queryPO.setOperateDate(pivotFeeDetailDTOs.get(0).getOperateDate());
        pivotFeeDetailService.disableAll(queryPO);
        if (CollectionUtils.isNotEmpty(insertList)) {
            pivotFeeDetailService.batchInsert(insertList);
        }
        return RpcMessage.success();
    }

    @Override
    public RpcMessage<Page<TotalFeeAccountResDTO>> getTotalFeePageByFeeType(TotalFeeAccountReqDTO totalFeeAccountReqDTO, Integer feeType) {
        Page<PivotFeeDetailPO> rowBounds;
        if (totalFeeAccountReqDTO.getPageNo() == null || totalFeeAccountReqDTO.getPageSize() == null) {
            rowBounds = new Page<>(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
        } else {
            rowBounds = new Page<>(totalFeeAccountReqDTO.getPageNo(), totalFeeAccountReqDTO.getPageSize());
        }
        Date startCreateTime = totalFeeAccountReqDTO.getStartCreateTime();
        Date endCreateTime = totalFeeAccountReqDTO.getEndCreateTime();
        PivotFeeDetailPO pivotFeeDetailPO = BeanMapperUtils.map(totalFeeAccountReqDTO, PivotFeeDetailPO.class);
        // FeeType, kindly refer to FeeTypeEnum
        Page<PivotFeeDetailPO> pivotFeeDetailPOPage = pivotFeeDetailService.queryTotalAccountFeePage(pivotFeeDetailPO, rowBounds, feeType, OperateTypeEnum.RECHARGE.getValue(),
                startCreateTime, endCreateTime);
        Page<TotalFeeAccountResDTO> totalFeeAccountResDTOPage = new Page<>();
        totalFeeAccountResDTOPage = BeanMapperUtils.map(pivotFeeDetailPOPage, totalFeeAccountResDTOPage.getClass());
        List<PivotFeeDetailPO> pivotFeeDetailPOList = pivotFeeDetailPOPage.getRecords();
        List<TotalFeeAccountResDTO> totalFeeAccountResDTOList = BeanMapperUtils.mapList(pivotFeeDetailPOList, TotalFeeAccountResDTO.class);
        totalFeeAccountResDTOPage.setRecords(totalFeeAccountResDTOList);
        return RpcMessage.success(totalFeeAccountResDTOPage);
    }

    @Override
    public RpcMessage<BigDecimal> getTotalMoneyByFeeType(Integer feeType) {
        return RpcMessage.success(pivotFeeDetailService.getTotalMoneyByFeeType(feeType));
    }

    @Override
    public RpcMessage<BigDecimal> getTotalMoneyByDateAndType(PivotFeeDetailDTO pivotFeeDetailDTO) {
        PivotFeeDetailPO pivotFeeDetailPO = new PivotFeeDetailPO();
        pivotFeeDetailPO.setOperateDate(pivotFeeDetailDTO.getOperateDate());
        pivotFeeDetailPO.setOperateType(pivotFeeDetailDTO.getOperateType());
        return RpcMessage.success(pivotFeeDetailService.getTotalMoneyByDateAndType(pivotFeeDetailPO));
    }

    @Override
    public RpcMessage<BigDecimal> getTotalMoneyByDateAndFeeType(PivotFeeDetailDTO pivotFeeDetailDTO) {
        PivotFeeDetailPO pivotFeeDetailPO = new PivotFeeDetailPO();
        pivotFeeDetailPO.setOperateDate(pivotFeeDetailDTO.getOperateDate());
        pivotFeeDetailPO.setFeeType(pivotFeeDetailDTO.getFeeType());
        return RpcMessage.success(pivotFeeDetailService.getTotalMoneyByDateAndFeeType(pivotFeeDetailPO));
    }

}
