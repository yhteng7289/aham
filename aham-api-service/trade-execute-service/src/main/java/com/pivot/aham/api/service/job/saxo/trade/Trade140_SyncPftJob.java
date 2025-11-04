package com.pivot.aham.api.service.job.saxo.trade;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.server.dto.req.PivotPftAssetResDTO;
import com.pivot.aham.api.server.dto.req.PivotPftHoldingResDTO;
import com.pivot.aham.api.server.dto.res.PivotPftAssetReqDTO;
import com.pivot.aham.api.server.remoteservice.PivotPftRemoteService;
import com.pivot.aham.api.service.mapper.EtfMergeOrderMapper;
import com.pivot.aham.api.service.mapper.EtfMergeOrderPftMapper;
import com.pivot.aham.api.service.mapper.model.EtfMergeOrderPO;
import com.pivot.aham.api.service.mapper.model.EtfMergePftOrderPO;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.enums.EtfMergeOrderStatusEnum;
import com.pivot.aham.common.enums.PftHoldingStatusEnum;
import com.pivot.aham.common.enums.SyncStatus;
import com.pivot.aham.common.enums.TradeType;
import com.pivot.aham.common.enums.analysis.PftAssetOperateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import org.springframework.stereotype.Component;

@ElasticJobConf(name = "Trade140_SyncPftJob_2", cron = "0 30 16 * * ?", description = "SaxoTrade_pft同步", eventTraceRdbDataSource = "dataSource")
@Slf4j
@Component
public class Trade140_SyncPftJob implements SimpleJob {

    @Autowired
    private EtfMergeOrderPftMapper etfMergeOrderPftMapper;

    @Autowired
    private PivotPftRemoteService pivotPftRemoteService;
    
    @Autowired
    private EtfMergeOrderMapper etfMergeOrderMapper;

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("开始执行 =======>>> SyncPftJob");
        List<EtfMergePftOrderPO> iniList = etfMergeOrderPftMapper.listBySyncStatus(SyncStatus.INIT);
        for (EtfMergePftOrderPO etfMergePftOrderPO : iniList) {

            etfMergeOrderPftMapper.updateSyncStatus(SyncStatus.SEND, etfMergePftOrderPO.getMergeOrderId());
            PivotPftAssetReqDTO pivotPftAssetReqDTO = new PivotPftAssetReqDTO();
            pivotPftAssetReqDTO.setProductCode(etfMergePftOrderPO.getProductCode());
            pivotPftAssetReqDTO.setConfirmMoney(etfMergePftOrderPO.getAmount());
            pivotPftAssetReqDTO.setConfirmShare(etfMergePftOrderPO.getShare());
            pivotPftAssetReqDTO.setCostFee(etfMergePftOrderPO.getCost());
            pivotPftAssetReqDTO.setExecuteOrderNo(etfMergePftOrderPO.getId());
            pivotPftAssetReqDTO.setPftAssetOperateType(etfMergePftOrderPO.getTradeType() == TradeType.BUY
                    ? PftAssetOperateTypeEnum.NEEDCASH
                    : PftAssetOperateTypeEnum.NEEDETFSHARES);
            pivotPftAssetReqDTO.setPftAssetSource(etfMergePftOrderPO.getSourceType());


            RpcMessage<PivotPftAssetResDTO> pivotPftAssetResDTORpcMessage = pivotPftRemoteService.updatePftAsset(pivotPftAssetReqDTO);

            if (!pivotPftAssetResDTORpcMessage.isSuccess()) {
                log.error("SyncPftJob error : {}.", pivotPftAssetResDTORpcMessage.getErrMsg());
                etfMergeOrderPftMapper.updateSyncStatus(SyncStatus.FAIL, etfMergePftOrderPO.getMergeOrderId());
                continue;
            }
            etfMergeOrderPftMapper.updateSyncStatus(SyncStatus.SUCCESS, etfMergePftOrderPO.getMergeOrderId());
        }
        PivotPftHoldingResDTO pivotPftHoldingResDTO = new PivotPftHoldingResDTO();
        pivotPftHoldingResDTO.setStatus(PftHoldingStatusEnum.HOLDING);
        RpcMessage<List<PivotPftHoldingResDTO>> pivotPftHoldResDTORpcMessage = pivotPftRemoteService.getListOfPftHolding(pivotPftHoldingResDTO);
        List<PivotPftHoldingResDTO> listPivotPftHoldingResDTO = pivotPftHoldResDTORpcMessage.getContent();
        if(listPivotPftHoldingResDTO.size() > 0){
            for(PivotPftHoldingResDTO PivotPftHoldingResDTO:listPivotPftHoldingResDTO){
                EtfMergeOrderPO etfMergeOrderPO = etfMergeOrderMapper.getById(PivotPftHoldingResDTO.getMerdeOrderId());
                if(etfMergeOrderPO.getOrderStatus() == EtfMergeOrderStatusEnum.FINISH){
                    //update Holding PFT
                    pivotPftRemoteService.updatePftHolding(etfMergeOrderPO.getId());
                }
            }
        }
        log.info("执行结束 =======>>> SyncPftJob");
    }
}
