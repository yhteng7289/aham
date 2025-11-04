package com.pivot.aham.api.service.impl.trade;

import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.EtfCallbackDTO;
import com.pivot.aham.api.server.dto.res.TmpOrderRecordResDTO;
import com.pivot.aham.api.server.remoteservice.AssetServiceRemoteService;
import com.pivot.aham.api.service.mapper.EtfOrderMapper;
import com.pivot.aham.api.service.mapper.model.EtfOrderPO;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.enums.EtfOrderStatusEnum;
import com.pivot.aham.common.enums.TransferStatusEnum;
import com.pivot.aham.common.enums.analysis.TmpOrderExecuteStatusEnum;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: aham
 * @description:
 * @author: zhang7
 * @create: 2019-07-02 15:41
 *
 */
@Component
@Slf4j
public class Finish {

    @Autowired
    private EtfOrderMapper etfOrderMapper;

    @Autowired
    private AssetServiceRemoteService assetServiceRemoteService;
    
    
    
    public void finishNotify() {
        try {
            
            TmpOrderRecordResDTO tmpOrderRecordResDTO = new TmpOrderRecordResDTO();
            tmpOrderRecordResDTO.setTmpOrderTradeStatus(TmpOrderExecuteStatusEnum.HANDLING);
            RpcMessage <List<TmpOrderRecordResDTO>> rpcMessageTmpRcd = assetServiceRemoteService.getTmpOrderRecord(tmpOrderRecordResDTO);
            List<TmpOrderRecordResDTO> listTmpOrder = rpcMessageTmpRcd.getContent();
            
            List<EtfOrderPO> etfOrderList = etfOrderMapper.getListByStatus(EtfOrderStatusEnum.WAIT_NOTIFY);
            HashMap<Long, List<EtfOrderPO>> hmap = new HashMap<Long, List<EtfOrderPO>>();
            if(listTmpOrder.size() > 0){
                //HashMap<Long, List<EtfOrderPO>> hmap = new HashMap<Long, List<EtfOrderPO>>();
                for(TmpOrderRecordResDTO tmpRecordResDTO:listTmpOrder){
                    List<EtfOrderPO> list = Lists.newArrayList();
                    hmap.put(tmpRecordResDTO.getTotalTmpOrderId(), list);
                }
                
                for(TmpOrderRecordResDTO tmpRecordResDTO:listTmpOrder){
                    boolean isFoundId = false;
                    for(EtfOrderPO etfOrderPO:etfOrderList){
                        if(tmpRecordResDTO.getExecuteOrderId().equals(etfOrderPO.getId())){
                            if(hmap.containsKey(tmpRecordResDTO.getTotalTmpOrderId())){
                                List<EtfOrderPO> listETFOrder = hmap.get(tmpRecordResDTO.getTotalTmpOrderId());
                                listETFOrder.add(etfOrderPO);
                                hmap.put(tmpRecordResDTO.getTotalTmpOrderId(), listETFOrder);
                                isFoundId = true;
                                break;
                            }else{
                                break;
                            }
                        }
                    }
                    
                    if(!isFoundId){
                        hmap.remove(tmpRecordResDTO.getTotalTmpOrderId());
                    }
                }
            }
            etfOrderList = Lists.newArrayList();
            for(Map.Entry me: hmap.entrySet()){
                List<EtfOrderPO> list = (List)me.getValue();
                etfOrderList.addAll(list);
            }

            if (!CollectionUtils.isEmpty(etfOrderList)) {
                List<EtfCallbackDTO> params = Lists.newArrayList();
                List<Long> orderIdList = Lists.newArrayList();

                for (EtfOrderPO etfOrder : etfOrderList) {
                    if (etfOrder.getProductCode().toLowerCase().equalsIgnoreCase("cash")) {
                        EtfCallbackDTO etfCallbackDTO = new EtfCallbackDTO();
                        etfCallbackDTO.setTmpOrderId(etfOrder.getOutBusinessId());
                        etfCallbackDTO.setAccountId(etfOrder.getAccountId());
                        etfCallbackDTO.setProductCode(etfOrder.getProductCode());
                        etfCallbackDTO.setConfirmMoney(etfOrder.getConfirmAmount());
                        etfCallbackDTO.setConfirmShare(etfOrder.getConfirmShare());
                        etfCallbackDTO.setTransCost(etfOrder.getCostFee());
                        etfCallbackDTO.setTransferStatus(TransferStatusEnum.SUCCESS);
                        etfCallbackDTO.setConfirmTime(DateUtils.now());
                        params.add(etfCallbackDTO);
                        orderIdList.add(etfOrder.getId());
                    } else {
                        BigDecimal confirmShare = etfOrder.getConfirmShare();
                        if (confirmShare.compareTo(BigDecimal.ZERO) > 0) {
                            EtfCallbackDTO etfCallbackDTO = new EtfCallbackDTO();
                            etfCallbackDTO.setTmpOrderId(etfOrder.getOutBusinessId());
                            etfCallbackDTO.setAccountId(etfOrder.getAccountId());
                            etfCallbackDTO.setProductCode(etfOrder.getProductCode());
                            etfCallbackDTO.setConfirmMoney(etfOrder.getConfirmAmount());
                            etfCallbackDTO.setConfirmShare(etfOrder.getConfirmShare());
                            etfCallbackDTO.setTransCost(etfOrder.getCostFee());
                            etfCallbackDTO.setTransferStatus(TransferStatusEnum.SUCCESS);
                            etfCallbackDTO.setConfirmTime(DateUtils.now());
                            params.add(etfCallbackDTO);
                            orderIdList.add(etfOrder.getId());
                        }
//                        //Added By WooiTatt
                        if (confirmShare.compareTo(BigDecimal.ZERO) == 0) {
                            EtfCallbackDTO etfCallbackDTO = new EtfCallbackDTO();
                            etfCallbackDTO.setTmpOrderId(etfOrder.getOutBusinessId());
                            etfCallbackDTO.setAccountId(etfOrder.getAccountId());
                            etfCallbackDTO.setProductCode(etfOrder.getProductCode());
                            etfCallbackDTO.setConfirmMoney(etfOrder.getConfirmAmount());
                            etfCallbackDTO.setConfirmShare(etfOrder.getConfirmShare());
                            etfCallbackDTO.setTransCost(etfOrder.getCostFee());
                            etfCallbackDTO.setTransferStatus(TransferStatusEnum.SUCCESS);
                            etfCallbackDTO.setConfirmTime(DateUtils.now());
                            params.add(etfCallbackDTO);
                            orderIdList.add(etfOrder.getId());
                        }

                    }
                }

                RpcMessage rpcMessage = assetServiceRemoteService.etfCallBack(params);
                if (rpcMessage.isSuccess()) {
                    etfOrderMapper.notifySuccess(orderIdList, EtfOrderStatusEnum.FINISH);
                    
                    for(Map.Entry me: hmap.entrySet()){
                        assetServiceRemoteService.updateTpcfTncf((Long)me.getKey());
                    }
                }
            }
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
    }
}
