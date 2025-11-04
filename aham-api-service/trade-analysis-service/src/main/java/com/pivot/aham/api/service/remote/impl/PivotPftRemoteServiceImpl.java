package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.req.PivotPftAccountResDTO;
import com.pivot.aham.api.server.dto.req.PivotPftAssetResDTO;
import com.pivot.aham.api.server.dto.req.PivotPftHoldingResDTO;
import com.pivot.aham.api.server.dto.req.PivotPftProductResDTO;
import com.pivot.aham.api.server.dto.res.PivotPftAssetReqDTO;
import com.pivot.aham.api.server.dto.res.PivotPftProductReqDTO;
import com.pivot.aham.api.server.remoteservice.PivotPftRemoteService;
import com.pivot.aham.api.service.mapper.model.PivotPftAccountPO;
import com.pivot.aham.api.service.mapper.model.PivotPftAssetPO;
import com.pivot.aham.api.service.mapper.model.PivotPftHoldingPO;
import com.pivot.aham.api.service.service.AssetFundNavService;
import com.pivot.aham.api.service.service.PivotPftAccountService;
import com.pivot.aham.api.service.service.PivotPftAssetService;
import com.pivot.aham.api.service.service.PivotPftHoldingService;
import com.pivot.aham.api.service.support.PftAccountAssetStatistic;
import com.pivot.aham.api.service.support.PftAccountAssetStatisticBean;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.PftHoldingStatusEnum;
import com.pivot.aham.common.enums.ProductAssetStatusEnum;
import com.pivot.aham.common.enums.analysis.PftAssetOperateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = PivotPftRemoteService.class)
@Slf4j
public class PivotPftRemoteServiceImpl implements PivotPftRemoteService {

    @Resource
    private PivotPftAssetService pivotPftAssetService;
    @Resource
    private PivotPftAccountService pivotPftAccountService;
    @Resource
    private AssetFundNavService assetFundNavService;
    @Resource
    private PivotPftHoldingService pivotPftHoldingService;

    @Override
    public RpcMessage<PivotPftAssetResDTO> updatePftAsset(PivotPftAssetReqDTO pivotPftAssetReqDTO) {
        //幂等校验
        PivotPftAssetPO pivotPftAssetQuery = new PivotPftAssetPO();
        pivotPftAssetQuery.setExecuteOrderNo(pivotPftAssetReqDTO.getExecuteOrderNo());
        List<PivotPftAssetPO> pivotPftAssetOld = pivotPftAssetService.queryList(pivotPftAssetQuery);

//        //数据版本校验
        List<PivotPftAccountPO> pivotPftAccountPOList = pivotPftAccountService.queryList(new PivotPftAccountPO());
//        Long  dataVersion = pivotPftAccountPOList.get(0).getDataVersion();

        if (CollectionUtils.isNotEmpty(pivotPftAssetOld)) {
            return RpcMessage.error("该单号已受理");
        }

//        if(!dataVersion.equals(pivotPftAssetReqDTO.getDataVersion())){
//            return RpcMessage.error("数据已发生变化");
//        }
        if (PftAssetOperateTypeEnum.NEEDETFSHARES == pivotPftAssetReqDTO.getPftAssetOperateType()) {
            List<PivotPftAssetPO> pivotPftAssetPOS = Lists.newArrayList();
            PivotPftAssetPO pivotPftAssetCashUpdate = new PivotPftAssetPO();
            pivotPftAssetCashUpdate.setExecuteOrderNo(pivotPftAssetReqDTO.getExecuteOrderNo());
            pivotPftAssetCashUpdate.setConfirmMoney(pivotPftAssetReqDTO.getConfirmMoney());
            pivotPftAssetCashUpdate.setExecuteTime(DateUtils.now());
            pivotPftAssetCashUpdate.setPftAssetSource(pivotPftAssetReqDTO.getPftAssetSource());
            pivotPftAssetCashUpdate.setProductCode(Constants.CASH);
            pivotPftAssetCashUpdate.setProductAssetStatus(ProductAssetStatusEnum.HOLD_ING);

            PivotPftAssetPO pivotPftAssetETFUpdate = new PivotPftAssetPO();
            pivotPftAssetETFUpdate.setExecuteOrderNo(pivotPftAssetReqDTO.getExecuteOrderNo());
            pivotPftAssetETFUpdate.setConfirmShare(pivotPftAssetReqDTO.getConfirmShare());
            pivotPftAssetETFUpdate.setExecuteTime(DateUtils.now());
            pivotPftAssetETFUpdate.setPftAssetSource(pivotPftAssetReqDTO.getPftAssetSource());
            pivotPftAssetETFUpdate.setProductCode(pivotPftAssetReqDTO.getProductCode());
            pivotPftAssetETFUpdate.setProductAssetStatus(ProductAssetStatusEnum.CONFIRM_SELL);

            pivotPftAssetPOS.add(pivotPftAssetCashUpdate);
            pivotPftAssetPOS.add(pivotPftAssetETFUpdate);
            pivotPftAssetService.updateBatch(pivotPftAssetPOS);
        } else {
            List<PivotPftAssetPO> pivotPftAssetPOS = Lists.newArrayList();
            PivotPftAssetPO pivotPftAssetCashUpdate = new PivotPftAssetPO();
            pivotPftAssetCashUpdate.setExecuteOrderNo(pivotPftAssetReqDTO.getExecuteOrderNo());
            pivotPftAssetCashUpdate.setConfirmMoney(pivotPftAssetReqDTO.getConfirmMoney());
            pivotPftAssetCashUpdate.setExecuteTime(DateUtils.now());
            pivotPftAssetCashUpdate.setPftAssetSource(pivotPftAssetReqDTO.getPftAssetSource());
            pivotPftAssetCashUpdate.setProductCode(Constants.CASH);
            pivotPftAssetCashUpdate.setProductAssetStatus(ProductAssetStatusEnum.CONFIRM_SELL);

            PivotPftAssetPO pivotPftAssetETFUpdate = new PivotPftAssetPO();
            pivotPftAssetETFUpdate.setExecuteOrderNo(pivotPftAssetReqDTO.getExecuteOrderNo());
            pivotPftAssetETFUpdate.setConfirmShare(pivotPftAssetReqDTO.getConfirmShare());
            pivotPftAssetETFUpdate.setExecuteTime(DateUtils.now());
            pivotPftAssetETFUpdate.setPftAssetSource(pivotPftAssetReqDTO.getPftAssetSource());
            pivotPftAssetETFUpdate.setProductCode(pivotPftAssetReqDTO.getProductCode());
            pivotPftAssetETFUpdate.setProductAssetStatus(ProductAssetStatusEnum.HOLD_ING);

            pivotPftAssetPOS.add(pivotPftAssetCashUpdate);
            pivotPftAssetPOS.add(pivotPftAssetETFUpdate);
            pivotPftAssetService.updateBatch(pivotPftAssetPOS);

        }

        //重新统计pft账号余额
        PivotPftAssetPO pivotPftAssetPO = new PivotPftAssetPO();
        List<PivotPftAssetPO> pivotPftAssetList = pivotPftAssetService.queryList(pivotPftAssetPO);

        Date yesterday = DateUtils.addDateByDay(new Date(), -2);
        Map<String, BigDecimal> etfClosingPriceMap = assetFundNavService.getEtfClosingPrice();
        log.info("{},收市价:{}", yesterday, etfClosingPriceMap);
        List<PftAccountAssetStatisticBean> pftAccountAssetStatisticBeans
                = PftAccountAssetStatistic.statAccountAsset(pivotPftAssetList, etfClosingPriceMap);

        //更新pft账号
        List<PivotPftAccountPO> pivotPftAccountPOS = Lists.newArrayList();
        for (PftAccountAssetStatisticBean pftAccountAssetStatisticBean : pftAccountAssetStatisticBeans) {
            PivotPftAccountPO pivotPftAccountPO = new PivotPftAccountPO();
            pivotPftAccountPO.setMoney(pftAccountAssetStatisticBean.getProductMoney());
            pivotPftAccountPO.setProductCode(pftAccountAssetStatisticBean.getProductCode());
            pivotPftAccountPO.setShare(pftAccountAssetStatisticBean.getProductShare());
            pivotPftAccountPOS.add(pivotPftAccountPO);
        }
        if (CollectionUtils.isNotEmpty(pivotPftAccountPOS)) {
            //查询所有的id
//            List<PivotPftAccountPO> pivotPftAccountPOList = pivotPftAccountService.queryList(new PivotPftAccountPO());
            List<Long> ids = Lists.newArrayList();
//            Long dataVersion = null;
            for (PivotPftAccountPO pivotPftAccount : pivotPftAccountPOList) {
//                if(dataVersion == null){
//                    dataVersion = pivotPftAccount.getDataVersion();
//                }
                ids.add(pivotPftAccount.getId());
            }
            pivotPftAccountService.updateAccount(ids, pivotPftAccountPOS);
        }

        return RpcMessage.success();
    }

    @Override
    public RpcMessage<PivotPftProductResDTO> getPftProductAsset(PivotPftProductReqDTO pivotPftAccountReqDTO) {
        PivotPftAccountPO pivotPftAccountQuery = new PivotPftAccountPO();
        pivotPftAccountQuery.setProductCode(pivotPftAccountReqDTO.getProductCode());
        PivotPftAccountPO pivotPftAccount = pivotPftAccountService.selectOne(pivotPftAccountQuery);

        if (pivotPftAccount == null) {
            RpcMessage.error(pivotPftAccountReqDTO.getProductCode() + "该资产不存在");
        }

        PivotPftProductResDTO pivotPftProductRes = BeanMapperUtils.map(pivotPftAccount, PivotPftProductResDTO.class);
        return RpcMessage.success(pivotPftProductRes);
    }

    @Override
    public RpcMessage<List<PivotPftAccountResDTO>> getPftAccountAssets() {
        PivotPftAccountPO pivotPftAccountQuery = new PivotPftAccountPO();
        List<PivotPftAccountPO> pivotPftAccountPOS = pivotPftAccountService.queryList(pivotPftAccountQuery);

        List<PivotPftAccountResDTO> pivotPftAccountResDTOList = BeanMapperUtils.mapList(pivotPftAccountPOS, PivotPftAccountResDTO.class);
        return RpcMessage.success(pivotPftAccountResDTOList);
    }

    @Override
    public RpcMessage<List<PivotPftAssetResDTO>> getPftAssets(Date date) {
        PivotPftAssetPO pivotPftAssetPO = new PivotPftAssetPO();
        pivotPftAssetPO.setExecuteTime(date);
        List<PivotPftAssetPO> pivotPftAssetList = pivotPftAssetService.queryListByTime(pivotPftAssetPO);
        log.info("PivotPftRemoteServiceImpl , getPftAssets [pivotPftAssetList] {} ", pivotPftAssetList);
        Map<String, BigDecimal> etfClosingPriceMap = assetFundNavService.getEtfClosingPrice(date);

        log.info("{},收市价:{}", date, etfClosingPriceMap);
        List<PftAccountAssetStatisticBean> pftAccountAssetStatisticBeans = PftAccountAssetStatistic.statAccountAsset(pivotPftAssetList, etfClosingPriceMap);

        //更新pft账号
        List<PivotPftAssetResDTO> pivotPftAccountList = Lists.newArrayList();
        for (PftAccountAssetStatisticBean pftAccountAssetStatisticBean : pftAccountAssetStatisticBeans) {
            PivotPftAssetResDTO pivotPftAssetResDTO = new PivotPftAssetResDTO();
            pivotPftAssetResDTO.setConfirmMoney(pftAccountAssetStatisticBean.getProductMoney());
            pivotPftAssetResDTO.setProductCode(pftAccountAssetStatisticBean.getProductCode());
            pivotPftAssetResDTO.setConfirmShare(pftAccountAssetStatisticBean.getProductShare());
            pivotPftAccountList.add(pivotPftAssetResDTO);
        }
        return RpcMessage.success(pivotPftAccountList);
    }

    @Override
    public RpcMessage<String> savePftHolding(PivotPftHoldingResDTO pivotPftHoldingResDTO) {
        PivotPftHoldingPO pivotPftHoldingPO = BeanMapperUtils.map(pivotPftHoldingResDTO, PivotPftHoldingPO.class);
        pivotPftHoldingService.savePftHolding(pivotPftHoldingPO);
        return RpcMessage.success("Save Complete");
    }
    
    @Override
    public RpcMessage<List<PivotPftHoldingResDTO>> getListOfPftHolding(PivotPftHoldingResDTO pivotPftHoldingResDTO) {
        PivotPftHoldingPO pivotPftHoldingPO = BeanMapperUtils.map(pivotPftHoldingResDTO, PivotPftHoldingPO.class);
        List<PivotPftHoldingPO> listPftHolding = pivotPftHoldingService.getListOfPftHolding(pivotPftHoldingPO);
        List<PivotPftHoldingResDTO> listPftHoldingRes =  BeanMapperUtils.mapList(listPftHolding, PivotPftHoldingResDTO.class);
        return RpcMessage.success(listPftHoldingRes);
    }
    
    @Override
    public RpcMessage<String> updatePftHolding(Long mergeOrderId) {
        //PivotPftHoldingPO pivotPftHoldingPO = BeanMapperUtils.map(pivotPftHoldingResDTO, PivotPftHoldingPO.class);
        //pivotPftHoldingService.savePftHolding(pivotPftHoldingPO);
        PivotPftHoldingPO pivotPftHoldingPO = new PivotPftHoldingPO();
        pivotPftHoldingPO.setMerdeOrderId(mergeOrderId);
        pivotPftHoldingPO.setStatus(PftHoldingStatusEnum.HOLDING);
        PivotPftHoldingPO pivotPftHolding = pivotPftHoldingService.getPftHolding(pivotPftHoldingPO);
        if(pivotPftHolding != null){
            pivotPftHolding.setStatus(PftHoldingStatusEnum.COMPLETE);
            pivotPftHoldingService.updatePftHolding(pivotPftHolding);
        }
        
        return RpcMessage.success("Save Complete");
    }
}
