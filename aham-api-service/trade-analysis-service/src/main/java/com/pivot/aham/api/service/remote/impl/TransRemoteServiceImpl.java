package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.req.*;
import com.pivot.aham.api.server.dto.res.*;
import com.pivot.aham.api.server.remoteservice.TransRemoteService;
import com.pivot.aham.api.service.mapper.model.*;
import com.pivot.aham.api.service.service.*;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;


/**
 * 用户统计信息
 *
 * @author addison
 * @since 2018年12月10日
 */
@Service(interfaceClass = TransRemoteService.class)
@Slf4j
public class TransRemoteServiceImpl implements TransRemoteService {
    @Resource
    private AccountRechargeService accountRechargeService;
    @Resource
    private AccountRedeemService accountRedeemService;
    @Resource
    private UserDividendService userDividendService;
    @Resource
    private TransOrderService transOrderService;
    @Resource
    private TmpOrderRecordService tmpOrderRecordService;
    @Resource
    private AccountDividendService accountDividendService;

    @Override
    public RpcMessage<List<AccountRechargeResDTO>> getAccountRecharges(AccountRechargeReqDTO accountRechargeReqDTO) {
        AccountRechargePO accountRechargePO = BeanMapperUtils.map(accountRechargeReqDTO,AccountRechargePO.class);

        List<AccountRechargePO> accountRechargePOList = accountRechargeService.queryList(accountRechargePO);

        List<AccountRechargeResDTO> accountRechargeResDTOList
                = BeanMapperUtils.mapList(accountRechargePOList,AccountRechargeResDTO.class);

        return RpcMessage.success(accountRechargeResDTOList);
    }

    @Override
    public RpcMessage<List<AccountRedeemResDTO>> getAccountRedeems(AccountRedeemReqDTO accountRedeemReqDTO) {
        AccountRedeemPO accountRedeemPO = BeanMapperUtils.map(accountRedeemReqDTO,AccountRedeemPO.class);

        List<AccountRedeemPO> accountRedeemPOList = accountRedeemService.queryList(accountRedeemPO);

        List<AccountRedeemResDTO> accountRedeemResDTOList
                = BeanMapperUtils.mapList(accountRedeemPOList,AccountRedeemResDTO.class);


        return RpcMessage.success(accountRedeemResDTOList);
    }

    @Override
    public RpcMessage<List<UserDividendResDTO>> getUserDividends(UserDividendReqDTO userDividendReqDTO) {
        UserDividendPO userDividendPO = BeanMapperUtils.map(userDividendReqDTO,UserDividendPO.class);

        List<UserDividendPO> userDividendPOList = userDividendService.queryList(userDividendPO);

        List<UserDividendResDTO> userDividendResDTOList = BeanMapperUtils.mapList(userDividendPOList,UserDividendResDTO.class);

        return RpcMessage.success(userDividendResDTOList);
    }

    @Override
    public RpcMessage<Page<TransOrderResDTO>> getTransOrders(TransOrderReqDTO transOrderReqDTO) {
        TransOrderPO transOrderPO = BeanMapperUtils.map(transOrderReqDTO,TransOrderPO.class);

        Page<TransOrderPO> poPagination = new Page(transOrderReqDTO.getPageNo(),transOrderReqDTO.getPageSize());
        Page<TransOrderPO> poPaginationRes = transOrderService.queryTransOrder(poPagination,transOrderPO);

        Page<TransOrderResDTO> pagination = new Page();
        pagination = BeanMapperUtils.map(poPaginationRes,pagination.getClass());

        List<TransOrderPO> orderPOS = poPaginationRes.getRecords();
        List<TransOrderResDTO> transOrderResDTOS = BeanMapperUtils.mapList(orderPOS,TransOrderResDTO.class);
        pagination.setRecords(transOrderResDTOS);

        return RpcMessage.success(pagination);
    }

    @Override
    public RpcMessage<List<TransOrderResDTO>> getTransOrdersList(TransOrderReqDTO transOrderReqDTO) {

        TransOrderPO transOrderPO = new TransOrderPO();
        transOrderPO.setClientId(transOrderReqDTO.getClientId());
        transOrderPO.setStartTranscationTime(transOrderReqDTO.getStartTranscationTime());
        transOrderPO.setEndTranscationTime(transOrderReqDTO.getEndTranscationTime());
        List<TransOrderPO> transOrderPOS = transOrderService.queryTransOrderList(transOrderPO);

        List<TransOrderResDTO> transOrderResDTOList = BeanMapperUtils.mapList(transOrderPOS,TransOrderResDTO.class);

        return RpcMessage.success(transOrderResDTOList);
    }

    @Override
    public RpcMessage<Page<TmpOrderRecordResDTO>> getTmpOrders(TmpOrderRecordReqDTO tmpOrderRecordReqDTO) {

        Page<TmpOrderRecordPO> rowBounds = new Page<>(
                tmpOrderRecordReqDTO.getPageNo(),tmpOrderRecordReqDTO.getPageSize());
        TmpOrderRecordPO tmpOrderRecordPO = BeanMapperUtils.map(tmpOrderRecordReqDTO,TmpOrderRecordPO.class);
        Page<TmpOrderRecordPO> paginationTmpOrder = tmpOrderRecordService.listTmpOrderRecord(rowBounds,tmpOrderRecordPO);


        Page<TmpOrderRecordResDTO> resDTOPagination = new Page<>();
        resDTOPagination = BeanMapperUtils.map(paginationTmpOrder,resDTOPagination.getClass());
        List<TmpOrderRecordPO> tmpOrderRecordPOS = paginationTmpOrder.getRecords();
        List<TmpOrderRecordResDTO> tmpOrderRecordResDTOS = BeanMapperUtils.mapList(tmpOrderRecordPOS,TmpOrderRecordResDTO.class);
        resDTOPagination.setRecords(tmpOrderRecordResDTOS);

        return RpcMessage.success(resDTOPagination);
    }

    @Override
    public RpcMessage<Page<AccountDividendResDTO>> getAccountDividend(AccountDividendReqDTO accountDividendReqDTO) {
        Page<AccountDividendPO> rowBounds = new Page<>(
                accountDividendReqDTO.getPageNo(),accountDividendReqDTO.getPageSize());
        AccountDividendPO accountDividendPO = BeanMapperUtils.map(accountDividendReqDTO,AccountDividendPO.class);

        Page<AccountDividendPO> poPagination = accountDividendService.listAccountDividendPage(rowBounds,accountDividendPO);

        Page<AccountDividendResDTO> paginationRes = new Page<>();
        paginationRes=BeanMapperUtils.map(poPagination,paginationRes.getClass());
        List<AccountDividendPO> accountDividendPOS = poPagination.getRecords();
        List<AccountDividendResDTO> accountDividendResDTOS = BeanMapperUtils.mapList(accountDividendPOS,AccountDividendResDTO.class);
        paginationRes.setRecords(accountDividendResDTOS);

        return RpcMessage.success(paginationRes);
    }
}
