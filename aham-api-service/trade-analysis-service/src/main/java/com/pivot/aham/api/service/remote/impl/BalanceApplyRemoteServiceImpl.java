package com.pivot.aham.api.service.remote.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.api.server.dto.req.BalanceApplyReqDTO;
import com.pivot.aham.api.server.dto.res.BalanceApplyResDTO;
import com.pivot.aham.api.server.remoteservice.BalanceApplyRemoteService;
import com.pivot.aham.api.service.mapper.model.PivotCharityDetailPO;
import com.pivot.aham.api.service.mapper.model.PivotErrorHandlingDetailPO;
import com.pivot.aham.api.service.mapper.model.PivotFeeDetailPO;
import com.pivot.aham.api.service.mapper.model.PivotPftAccountPO;
import com.pivot.aham.api.service.service.PivotCharityDetailService;
import com.pivot.aham.api.service.service.PivotErrorHandlingDetailService;
import com.pivot.aham.api.service.service.PivotFeeDetailService;
import com.pivot.aham.api.service.service.PivotPftAccountService;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.analysis.OperateTypeEnum;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@Service(interfaceClass = BalanceApplyRemoteService.class)
@Slf4j
public class BalanceApplyRemoteServiceImpl implements BalanceApplyRemoteService {

    @Resource
    private PivotFeeDetailService pivotFeeDetailService;
    @Resource
    private PivotErrorHandlingDetailService pivotErrorHandlingDetailService;
    @Resource
    private PivotCharityDetailService pivotCharityDetailService;
    @Resource
    private PivotPftAccountService pivotPftAccountService;


    @Override
    public RpcMessage<BalanceApplyResDTO> queryBalanceByApplyDate(BalanceApplyReqDTO balanceApplyReqDTO) {

        Date ApplyDate = DateUtils.addDateByDay(DateUtils.now(), -1);
        if (balanceApplyReqDTO.getApplyDate() != null) {
            ApplyDate = balanceApplyReqDTO.getApplyDate();
        }
        BigDecimal pivotFee = calculationPivotFee(ApplyDate);
        BigDecimal errorHandlingFee = calculationErrorHandlingFee(ApplyDate);
//        BigDecimal charityFee = calculationCharityFee(ApplyDate);
        BigDecimal pftCash = calculationPftCash();

        log.info("pivotFee:{},errorHandlingFee:{},pftCash:{}.", pivotFee, errorHandlingFee, pftCash);
        BalanceApplyResDTO balanceApplyResDTO = new BalanceApplyResDTO();
//        balanceApplyResDTO.setCharityFee(charityFee);
        balanceApplyResDTO.setErrorHandlingFee(errorHandlingFee);
        balanceApplyResDTO.setPivotFee(pivotFee);
        balanceApplyResDTO.setPftCash(pftCash);
        return RpcMessage.success(balanceApplyResDTO);
    }

    private BigDecimal calculationPftCash() {
        BigDecimal pftCash = BigDecimal.ZERO;
        PivotPftAccountPO pivotPftAccountQuery = new PivotPftAccountPO();
        pivotPftAccountQuery.setProductCode(Constants.CASH);
        PivotPftAccountPO pivotPftAccount = pivotPftAccountService.selectOne(pivotPftAccountQuery);
        if (pivotPftAccount != null) {
            pftCash = pivotPftAccount.getMoney();
        }
        return pftCash;
    }


    private BigDecimal calculationCharityFee(Date applyDate) {
        PivotCharityDetailPO queryRechargePO = new PivotCharityDetailPO();
        queryRechargePO.setOperateTime(applyDate);
        queryRechargePO.setOperateType(OperateTypeEnum.RECHARGE);
        BigDecimal rechargeFee = pivotCharityDetailService.getTotalMoneyByDateAndType(queryRechargePO);
        //入金流水为空，返回余额为0
        if (rechargeFee == null) {
            return BigDecimal.ZERO;
        }

        PivotCharityDetailPO queryWithDrawPO = new PivotCharityDetailPO();
        queryWithDrawPO.setOperateTime(applyDate);
        queryWithDrawPO.setOperateType(OperateTypeEnum.WITHDRAW);
        BigDecimal withDrawFee = pivotCharityDetailService.getTotalMoneyByDateAndType(queryWithDrawPO);
        if (withDrawFee == null) {
            withDrawFee = BigDecimal.ZERO;
        }
        return rechargeFee.subtract(withDrawFee);
    }

    private BigDecimal calculationErrorHandlingFee(Date applyDate) {
        PivotErrorHandlingDetailPO queryRechargePO = new PivotErrorHandlingDetailPO();
        queryRechargePO.setOperateDate(applyDate);
        queryRechargePO.setOperateType(OperateTypeEnum.RECHARGE);
        BigDecimal rechargeFee = pivotErrorHandlingDetailService.getTotalMoneyByDateAndType(queryRechargePO);
        //入金流水为空，返回余额为0
        if (rechargeFee == null) {
            return BigDecimal.ZERO;
        }

        PivotErrorHandlingDetailPO queryWithDrawPO = new PivotErrorHandlingDetailPO();
        queryWithDrawPO.setOperateDate(applyDate);
        queryWithDrawPO.setOperateType(OperateTypeEnum.WITHDRAW);
        BigDecimal withDrawFee = pivotErrorHandlingDetailService.getTotalMoneyByDateAndType(queryWithDrawPO);
        if (withDrawFee == null) {
            withDrawFee = BigDecimal.ZERO;
        }
        return rechargeFee.subtract(withDrawFee);
    }

    private BigDecimal calculationPivotFee(Date applyDate) {
        PivotFeeDetailPO pivotRecharge = new PivotFeeDetailPO();
        pivotRecharge.setOperateDate(applyDate);
        pivotRecharge.setOperateType(OperateTypeEnum.RECHARGE);
        BigDecimal pivotRechargeFee = pivotFeeDetailService.getTotalMoneyByDateAndType(pivotRecharge);
        //入金流水为空，返回余额为0
        if (pivotRechargeFee == null) {
            return BigDecimal.ZERO;
        }
        PivotFeeDetailPO pivotWithDraw = new PivotFeeDetailPO();
        pivotWithDraw.setOperateDate(applyDate);
        pivotWithDraw.setOperateType(OperateTypeEnum.WITHDRAW);
        BigDecimal pivotRedeemFee = pivotFeeDetailService.getTotalMoneyByDateAndType(pivotWithDraw);
        if (pivotRedeemFee == null) {
            pivotRedeemFee = BigDecimal.ZERO;
        }
        return pivotRechargeFee.subtract(pivotRedeemFee);
    }
}
