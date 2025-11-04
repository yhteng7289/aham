package com.pivot.aham.api.service.job.interevent;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.pivot.aham.api.server.dto.EtfCallbackDTO;
import com.pivot.aham.api.server.dto.ModelRecommendResDTO;
import com.pivot.aham.api.server.remoteservice.ModelServiceRemoteService;
import com.pivot.aham.api.service.mapper.model.AccountAssetPO;
import com.pivot.aham.api.service.mapper.model.AccountBalanceAdjDetail;
import com.pivot.aham.api.service.mapper.model.AccountBalanceHisRecord;
import com.pivot.aham.api.service.mapper.model.AccountBalanceRecord;
import com.pivot.aham.api.service.service.AccountAssetService;
import com.pivot.aham.api.service.service.AccountBalanceAdjDetailService;
import com.pivot.aham.api.service.service.AccountBalanceHisRecordService;
import com.pivot.aham.api.service.service.AccountBalanceRecordService;
import com.pivot.aham.api.service.support.AccountAssetStatistic;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.ProductAssetStatusEnum;
import com.pivot.aham.common.enums.TransferStatusEnum;
import com.pivot.aham.common.enums.analysis.AssetSourceEnum;
import com.pivot.aham.common.enums.analysis.BalStatusEnum;
import com.pivot.aham.common.enums.analysis.ExecuteStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * 统计account信息
 *
 * @author addison
 * @since 2019年01月22日
 */
@Service
@Slf4j
public class ReBalanceListener {

    @Autowired
    private AccountBalanceAdjDetailService accountBalanceAdjDetailService;
    @Autowired
    private AccountBalanceRecordService accountBalanceRecordService;
    @Autowired
    private AccountAssetService accountAssetService;
    @Autowired
    private AccountBalanceHisRecordService accountBalanceHisRecordService;
    @Resource
    private ModelServiceRemoteService modelServiceRemoteService;

    @Subscribe
    @AllowConcurrentEvents
    public void modifyExecuteStatus(AccountBalanceAdjDetailEvent accountBalanceAdjDetailEvent) {

        List<EtfCallbackDTO> etfCallbackDTOList = accountBalanceAdjDetailEvent.getEtfCallbackDTOList();

        Set<Long> balIds = Sets.newHashSet();
        for (EtfCallbackDTO etfCallbackDTO : etfCallbackDTOList) {
            AccountBalanceAdjDetail accountBalanceAdjDetailQuery = new AccountBalanceAdjDetail();
            accountBalanceAdjDetailQuery.setTmpOrderId(etfCallbackDTO.getTmpOrderId());
            AccountBalanceAdjDetail accountBalanceAdjDetail = accountBalanceAdjDetailService.selectOne(accountBalanceAdjDetailQuery);

            if (etfCallbackDTO.getTransferStatus() == TransferStatusEnum.SUCCESS
                    && accountBalanceAdjDetail != null) {
                accountBalanceAdjDetail.setExecuteStatus(ExecuteStatusEnum.SUCCESS);
                accountBalanceAdjDetailService.updateOrInsert(accountBalanceAdjDetail);
                balIds.add(accountBalanceAdjDetail.getBalId());
            }
        }

        Predicate<AccountBalanceAdjDetail> detailSuccess = new Predicate<AccountBalanceAdjDetail>() {
            @Override
            public boolean apply(@Nullable AccountBalanceAdjDetail input) {
                if (input.getProductCode().equals(Constants.CASH)) {
                    return true;
                } else {
                    Boolean res = input.getExecuteStatus() == ExecuteStatusEnum.SUCCESS;
                    return res;
                }
            }
        };

        for (Long balId : balIds) {
            //处理tncf

            //按balId查询所有detail
            AccountBalanceAdjDetail accountBalanceAdjDetailQuery = new AccountBalanceAdjDetail();
            accountBalanceAdjDetailQuery.setBalId(balId);
            List<AccountBalanceAdjDetail> accountBalanceAdjDetailList = accountBalanceAdjDetailService.queryList(accountBalanceAdjDetailQuery);

            AccountBalanceRecord accountBalanceRecord = accountBalanceRecordService.queryById(balId);
//                AccountBalanceRecord accountBalanceRecord = new AccountBalanceRecord();
            accountBalanceRecord.setId(balId);
            accountBalanceRecord.setBalStatus(BalStatusEnum.SUCCESS);
            accountBalanceRecordService.updateOrInsert(accountBalanceRecord);

            //更新历史调仓数据
            ModelRecommendResDTO modelRecommendResDTO = modelServiceRemoteService.getModelRecommendById(accountBalanceRecord.getModelRecommendId());
            AccountBalanceHisRecord accountBalanceHisRecord = new AccountBalanceHisRecord();
            accountBalanceHisRecord.setAccountId(accountBalanceRecord.getAccountId());
            accountBalanceHisRecord.setBalId(accountBalanceRecord.getId());
            accountBalanceHisRecord.setLastProductWeight(modelRecommendResDTO.getProductWeight());
            accountBalanceHisRecord.setLastBalTime(DateUtils.now());
            accountBalanceHisRecord.setPortfolioScore(modelRecommendResDTO.getScore());
            accountBalanceHisRecordService.updateByAccountId(accountBalanceHisRecord);

            Boolean ifAllSuccess = Iterables.all(accountBalanceAdjDetailList, detailSuccess);

            if (ifAllSuccess) {
                //处理unbuy，将所有unbuy转为cash
                AccountAssetPO accountAssetParam = new AccountAssetPO();
                accountAssetParam.setAccountId(accountBalanceRecord.getAccountId());
                accountAssetParam.setProductCode(Constants.UN_BUY_PRODUCT_CODE);
                List<AccountAssetPO> accountAssetPOs = accountAssetService.listAccountUnBuyAssets(accountAssetParam);
                //查询充值流水
                BigDecimal totalUnbuy = AccountAssetStatistic.getAccountUnbuy(accountAssetPOs);

                if (totalUnbuy.compareTo(BigDecimal.ZERO) > 0) {
                    Long totalTmpOrderId = Sequence.next();
                    AccountAssetPO outAsset = new AccountAssetPO();
                    outAsset.setAssetSource(AssetSourceEnum.SELLCROSS);
                    outAsset.setAccountId(accountBalanceRecord.getAccountId());
                    outAsset.setProductCode(Constants.UN_BUY_PRODUCT_CODE);
                    outAsset.setConfirmShare(BigDecimal.ZERO);
                    outAsset.setConfirmMoney(totalUnbuy);
                    outAsset.setApplyMoney(totalUnbuy);
                    outAsset.setProductAssetStatus(ProductAssetStatusEnum.CONFIRM_SELL);
                    outAsset.setRechargeOrderNo(0L);
                    outAsset.setApplyTime(DateUtils.now());
                    outAsset.setConfirmTime(DateUtils.now());
                    outAsset.setTotalTmpOrderId(totalTmpOrderId);
                    outAsset.setUpdateTime(DateUtils.now());
                    outAsset.setCreateTime(DateUtils.now());
                    outAsset.setTmpOrderId(0L);
                    accountAssetService.updateOrInsert(outAsset);

                    AccountAssetPO inAsset = new AccountAssetPO();
                    inAsset.setAssetSource(AssetSourceEnum.BUYCROSS);
                    inAsset.setAccountId(accountBalanceRecord.getAccountId());
                    inAsset.setProductCode(Constants.CASH);
                    inAsset.setConfirmShare(BigDecimal.ZERO);
                    inAsset.setConfirmMoney(totalUnbuy);
                    inAsset.setApplyMoney(totalUnbuy);
                    inAsset.setProductAssetStatus(ProductAssetStatusEnum.HOLD_ING);
                    inAsset.setRechargeOrderNo(0L);
                    inAsset.setApplyTime(DateUtils.now());
                    inAsset.setConfirmTime(DateUtils.now());
                    inAsset.setTotalTmpOrderId(totalTmpOrderId);
                    inAsset.setUpdateTime(DateUtils.now());
                    inAsset.setCreateTime(DateUtils.now());
                    inAsset.setTmpOrderId(0L);
                    accountAssetService.updateOrInsert(inAsset);
                }
            }
        }
    }
}
