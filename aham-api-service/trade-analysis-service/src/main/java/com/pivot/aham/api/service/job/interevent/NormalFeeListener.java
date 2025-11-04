package com.pivot.aham.api.service.job.interevent;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.pivot.aham.api.server.dto.PivotFeeDetailDTO;
import com.pivot.aham.api.server.remoteservice.PivotFeeDetailRemoteService;
import com.pivot.aham.api.service.mapper.model.AccountAssetPO;
import com.pivot.aham.api.service.mapper.model.AccountFundNavPO;
import com.pivot.aham.api.service.mapper.model.AccountNormalFee;
import com.pivot.aham.api.service.mapper.model.FeesConfigPO;
import com.pivot.aham.api.service.mapper.model.UserFundNavPO;
import com.pivot.aham.api.service.service.AccountAssetService;
import com.pivot.aham.api.service.service.AccountNormalFeeService;
import com.pivot.aham.api.service.service.AssetFundNavService;
import com.pivot.aham.api.service.service.FeesConfigService;
import com.pivot.aham.api.service.service.UserFundNavService;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.ProductAssetStatusEnum;
import com.pivot.aham.common.enums.analysis.AssetSourceEnum;
import com.pivot.aham.common.enums.analysis.FeeTypeEnum;
import com.pivot.aham.common.enums.analysis.OperateTypeEnum;
import com.pivot.aham.common.enums.analysis.ReduceStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 处理普通fee
 *
 * @author addison
 * @since 2019年01月22日
 */
@Service
@Slf4j
public class NormalFeeListener {

    @Autowired
    private AccountNormalFeeService accountNormalFeeService;
    @Resource
    private AccountAssetService accountAssetService;
    @Resource
    private AssetFundNavService assetFundNavService;
    @Resource
    private UserFundNavService userFundNavService;
    @Resource
    private EventBus accountStaticsBus;
    @Resource
    private FeesConfigService feesConfigService;

    @Resource
    private PivotFeeDetailRemoteService pivotFeeDetailRemoteService;

    @Subscribe
    /**
     *
     * MGTRatio is the annual management fee in Fund Level 	MGTRatio=0.5%
     * FundMGET_Fee(t) is the accumulated Management Fee --MGT_GST(t) is the GST
     * tax for managemnt fee, assume the tax rate = GSTX (7%) 	MGT_GST (t) =
     * MGT_FEE(t) * GSTX CustodyRatio is the annual custodia fee by SAXO If AUM
     * <=USD 100,000,000 CustodyRatio =0.06% Elseif AUM <USD 500,000,000
     * CustodyRatio =0.05% Elseif AUM <USD 1000,000,000 CustodyRatio =0.04% Else
     * CustodyRatio =0.03% End Fund Cust_Fee(t) is the accumulated custodian Fee
     */
    public void createAccountFee(NormalAccountFeeCreateEvent normalFeeCreateEvent) {
        //生成fee
        BigDecimal yearDays = new BigDecimal("365");
        AccountNormalFee accountNormalFee = new AccountNormalFee();
        accountNormalFee.setAccountId(normalFeeCreateEvent.getAccountId());
        BigDecimal totalAsset = normalFeeCreateEvent.getTotalAsset();
       /* BigDecimal custodia = BigDecimal.ZERO;
        if (totalAsset.compareTo(new BigDecimal("100000000")) < 0) {
            custodia = new BigDecimal("0.0006");
        } else if (totalAsset.compareTo(new BigDecimal("500000000")) < 0) {
            custodia = new BigDecimal("0.0005");
        } else if (totalAsset.compareTo(new BigDecimal("1000000000")) < 0) {
            custodia = new BigDecimal("0.0004");
        } else {
            custodia = new BigDecimal("0.0003");
        }

        BigDecimal custFee = normalFeeCreateEvent.getTotalAsset().multiply(custodia);
        BigDecimal custFeeDay = custFee.divide(yearDays, 6, BigDecimal.ROUND_HALF_UP);
        accountNormalFee.setCustFee(custFeeDay);*/
       FeesConfigPO feesConfigPO = new FeesConfigPO();
       feesConfigPO.setFeeType(FeeTypeEnum.MGT_FEE);
       feesConfigPO.setStartDate(DateUtils.now());
       feesConfigPO.setEndDate(DateUtils.now());
       feesConfigPO.setActiveStatus("Y");
       feesConfigPO = feesConfigService.selectByDay(feesConfigPO);
       
        //BigDecimal mgtRatio = new BigDecimal("0.005");
        BigDecimal mgtRatio = BigDecimal.ZERO;
        if(feesConfigPO != null && feesConfigPO.getRateCharge().compareTo(BigDecimal.ZERO) > 0){
            mgtRatio = feesConfigPO.getRateCharge();
        }
        BigDecimal mgtFee = normalFeeCreateEvent.getTotalAsset().multiply(mgtRatio);
        BigDecimal mgtFeeDay = mgtFee.divide(yearDays, 6, BigDecimal.ROUND_HALF_UP);
        accountNormalFee.setMgtFee(mgtFeeDay);
        //BigDecimal gstx = new BigDecimal("0.07");
        FeesConfigPO sstPO = new FeesConfigPO();
        sstPO.setFeeType(FeeTypeEnum.MGT_GST);
        sstPO.setStartDate(DateUtils.now());
        sstPO.setEndDate(DateUtils.now());
        sstPO.setActiveStatus("Y");
        sstPO = feesConfigService.selectByDay(sstPO);
        BigDecimal gstx = BigDecimal.ZERO;
        if(sstPO != null && sstPO.getRateCharge().compareTo(BigDecimal.ZERO) > 0){
            gstx = sstPO.getRateCharge();
        }
        BigDecimal mgtGst = mgtFeeDay.multiply(gstx);
//        BigDecimal mgtGstDay = mgtGst.multiply(gstx);
        accountNormalFee.setMgtGst(mgtGst);
        accountNormalFee.setReduceStatus(ReduceStatusEnum.NOT_REDUCE);

        AccountNormalFee accountNormalQuery = new AccountNormalFee();
        accountNormalQuery.setAccountId(normalFeeCreateEvent.getAccountId());
        if (normalFeeCreateEvent.getDate() != null) {
            accountNormalQuery.setCreateTime(normalFeeCreateEvent.getDate());
        } else {
            accountNormalQuery.setCreateTime(DateUtils.now());
        }
        AccountNormalFee accountNormal = accountNormalFeeService.selectByDay(accountNormalQuery);

        //幂等性保证
        if (accountNormal != null) {
            accountNormalFee.setId(accountNormal.getId());
        }

        accountNormalFeeService.updateOrInsert(accountNormalFee);

        //为了能重复跑数据（按传递的日期产生数据）
        if (normalFeeCreateEvent.getDate() != null) {
            accountNormalFee.setCreateTime(normalFeeCreateEvent.getDate());
            accountNormalFeeService.updateOrInsert(accountNormalFee);
        }
    }

    /**
     * nav计算前扣减fee
     *
     * @param normalFeeEvent
     */
    @Subscribe
    public void reduceClientFee(NormalClientFeeReduceEvent normalFeeEvent) {
        Long accountId = normalFeeEvent.getAccountId();
        log.info("账户赎回etf回调请求参数:{}", JSON.toJSON(accountId));
        if (accountId == null) {
            return;
        }
        //查询账户资产
        Date yesterDay = DateUtils.addDateByDay(DateUtils.now(), -1);
        if (normalFeeEvent.getDate() != null) {
            yesterDay = DateUtils.addDateByDay(normalFeeEvent.getDate(), -1);
        }

//        AccountFundNavPO accountFundNav = new AccountFundNavPO();
//        accountFundNav.setAccountId(accountId);
//        accountFundNav.setNavTime(yesterDay);
//        AccountFundNavPO accountFundNavPO = assetFundNavService.selectOneByNavTime(accountFundNav);
//        if(accountFundNavPO == null || accountFundNavPO.getTotalAsset().compareTo(BigDecimal.ZERO)<=0){
//            log.info("账户{},资产为零或查不到,无法进行手续费分摊",accountId);
//            return;
//        }
        //查询该账户下的所有用户
//        UserFundNavPO userFundNavPO = new UserFundNavPO();
//        userFundNavPO.setAccountId(accountId);
//        List<UserFundNavPO> userFundNavList = userFundNavService.queryList(userFundNavPO);
//        if(userFundNavList.size()==0){
//            log.info("账户{},查不到用户资产记录,无法进行手续费分摊",accountId);
//            return;
//        }
        AccountNormalFee accountNormalQuery = new AccountNormalFee();
        accountNormalQuery.setAccountId(accountId);
        accountNormalQuery.setCreateTime(yesterDay);
        accountNormalQuery.setReduceStatus(ReduceStatusEnum.NOT_REDUCE);
        AccountNormalFee accountNormalFee = accountNormalFeeService.selectByDay(accountNormalQuery);
        log.info("accountNormalQuery {} ", accountNormalQuery);
        log.info("accountNormalFee {} ", accountNormalFee);
        if (accountNormalFee != null) {
            //新增cash资产流水
            AccountAssetPO custFeeAsset = new AccountAssetPO();
            custFeeAsset.setAssetSource(AssetSourceEnum.NORMALFEE);
            custFeeAsset.setProductCode(Constants.CASH);
            custFeeAsset.setAccountId(accountId);
            custFeeAsset.setConfirmTime(DateUtils.now());
            custFeeAsset.setConfirmShare(BigDecimal.ZERO);
            custFeeAsset.setApplyMoney(accountNormalFee.getCustFee());
            custFeeAsset.setConfirmMoney(accountNormalFee.getCustFee());
            custFeeAsset.setApplyTime(DateUtils.now());
            custFeeAsset.setProductAssetStatus(ProductAssetStatusEnum.CONFIRM_SELL);

            AccountAssetPO mgtFeeAsset = new AccountAssetPO();
            mgtFeeAsset.setAssetSource(AssetSourceEnum.NORMALFEE);
            mgtFeeAsset.setProductCode(Constants.CASH);
            mgtFeeAsset.setAccountId(accountId);
            mgtFeeAsset.setConfirmTime(DateUtils.now());
            mgtFeeAsset.setConfirmShare(BigDecimal.ZERO);
            mgtFeeAsset.setApplyMoney(accountNormalFee.getMgtFee());
            mgtFeeAsset.setConfirmMoney(accountNormalFee.getMgtFee());
            mgtFeeAsset.setApplyTime(DateUtils.now());
            mgtFeeAsset.setProductAssetStatus(ProductAssetStatusEnum.CONFIRM_SELL);

            AccountAssetPO mgtGstAsset = new AccountAssetPO();
            mgtGstAsset.setAssetSource(AssetSourceEnum.NORMALFEE);
            mgtGstAsset.setProductCode(Constants.CASH);
            mgtGstAsset.setAccountId(accountId);
            mgtGstAsset.setConfirmTime(DateUtils.now());
            mgtGstAsset.setConfirmShare(BigDecimal.ZERO);
            mgtGstAsset.setApplyMoney(accountNormalFee.getMgtGst());
            mgtGstAsset.setConfirmMoney(accountNormalFee.getMgtGst());
            mgtGstAsset.setApplyTime(DateUtils.now());
            mgtGstAsset.setProductAssetStatus(ProductAssetStatusEnum.CONFIRM_SELL);

            accountAssetService.updateOrInsert(custFeeAsset);
            accountAssetService.updateOrInsert(mgtFeeAsset);
            accountAssetService.updateOrInsert(mgtGstAsset);

            //为了能重复跑数据（按传递的日期产生数据）
            if (normalFeeEvent.getDate() != null) {
                custFeeAsset.setCreateTime(normalFeeEvent.getDate());
                accountAssetService.updateOrInsert(custFeeAsset);
                mgtFeeAsset.setCreateTime(normalFeeEvent.getDate());
                accountAssetService.updateOrInsert(mgtFeeAsset);
                mgtGstAsset.setCreateTime(normalFeeEvent.getDate());
                accountAssetService.updateOrInsert(mgtGstAsset);
            }

        }
        AccountNormalFee accountNormalUpdate = new AccountNormalFee();
        accountNormalUpdate.setId(accountNormalFee.getId());
        accountNormalUpdate.setReduceStatus(ReduceStatusEnum.HAS_REDUCE);
        accountNormalFeeService.updateOrInsert(accountNormalUpdate);

        //统计手续费
        StaticReducedNormalFeeEvent reducedNormalFeeEvent = new StaticReducedNormalFeeEvent();
        reducedNormalFeeEvent.setAccountId(accountId);
        reducedNormalFeeEvent.setCustFee(accountNormalFee.getCustFee());
        reducedNormalFeeEvent.setGstMgtFee(accountNormalFee.getMgtGst());
        reducedNormalFeeEvent.setMgtFee(accountNormalFee.getMgtFee());
        accountStaticsBus.post(reducedNormalFeeEvent);

        buildThreadSaveFee(reducedNormalFeeEvent, assetFundNavService);
//        }
    }

    private void buildThreadSaveFee(StaticReducedNormalFeeEvent reducedNormalFeeEvent, AssetFundNavService assetFundNavService) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("开始异步调用保存feeDetail:{}", JSON.toJSON(reducedNormalFeeEvent));
                savePivotFeeDetail(reducedNormalFeeEvent, assetFundNavService);
            }
        });
        thread.start();
    }

    private void savePivotFeeDetail(StaticReducedNormalFeeEvent reducedNormalFeeEvent, AssetFundNavService assetFundNavService) {

        try {//挂了不影响其他业务

            AccountFundNavPO accountFundNav = new AccountFundNavPO();
            accountFundNav.setAccountId(reducedNormalFeeEvent.getAccountId());
            accountFundNav.setNavTime(DateUtils.addDateByDay(DateUtils.now(), -1));
            AccountFundNavPO accountFundNavPO = assetFundNavService.selectOneByNavTime(accountFundNav);
            if (accountFundNavPO == null || accountFundNavPO.getTotalAsset().compareTo(BigDecimal.ZERO) <= 0) {
                log.info("账户{},资产为零或查不到,无法进行手续费分摊", reducedNormalFeeEvent.getAccountId());
                return;
            }

            //查询该账户下的所有用户
            UserFundNavPO userFundNavPO = new UserFundNavPO();
            userFundNavPO.setAccountId(reducedNormalFeeEvent.getAccountId());
            userFundNavPO.setNavTime(DateUtils.dayStart(DateUtils.addDateByDay(DateUtils.now(), -1)));
            List<UserFundNavPO> userFundNavList = userFundNavService.queryList(userFundNavPO);
            if (userFundNavList.size() == 0) {
                log.info("账户{},查不到用户资产记录,无法进行手续费分摊", reducedNormalFeeEvent.getAccountId());
                return;
            }

            BigDecimal totalShare = calculationTotalMoney(userFundNavList);

            List<PivotFeeDetailDTO> pivotFeeDetailDTOList = Lists.newArrayList();
            for (UserFundNavPO fundNavPO : userFundNavList) {
                BigDecimal percent = calculationPersent(fundNavPO, totalShare);

                PivotFeeDetailDTO mgtGst = new PivotFeeDetailDTO();
                mgtGst.setAccountId(fundNavPO.getAccountId());
                mgtGst.setClientId(Long.valueOf(fundNavPO.getClientId()));
                mgtGst.setGoalId(fundNavPO.getGoalId());
                mgtGst.setFeeType(FeeTypeEnum.MGT_GST);
                mgtGst.setOperateType(OperateTypeEnum.RECHARGE);
                mgtGst.setOperateDate(DateUtils.dayStart(DateUtils.now()));
                mgtGst.setMoney(reducedNormalFeeEvent.getGstMgtFee().multiply(percent));
                pivotFeeDetailDTOList.add(mgtGst);

                PivotFeeDetailDTO mgtFee = new PivotFeeDetailDTO();
                mgtFee.setAccountId(fundNavPO.getAccountId());
                mgtFee.setClientId(Long.valueOf(fundNavPO.getClientId()));
                mgtFee.setGoalId(fundNavPO.getGoalId());
                mgtFee.setFeeType(FeeTypeEnum.MGT_FEE);
                mgtFee.setOperateType(OperateTypeEnum.RECHARGE);
                mgtFee.setOperateDate(DateUtils.dayStart(DateUtils.now()));
                mgtFee.setMoney(reducedNormalFeeEvent.getMgtFee().multiply(percent));
                pivotFeeDetailDTOList.add(mgtFee);

                PivotFeeDetailDTO custFee = new PivotFeeDetailDTO();
                custFee.setAccountId(fundNavPO.getAccountId());
                custFee.setClientId(Long.valueOf(fundNavPO.getClientId()));
                custFee.setGoalId(fundNavPO.getGoalId());
                custFee.setFeeType(FeeTypeEnum.CUST_FEE);
                custFee.setOperateType(OperateTypeEnum.RECHARGE);
                custFee.setOperateDate(DateUtils.dayStart(DateUtils.now()));
                custFee.setMoney(reducedNormalFeeEvent.getCustFee().multiply(percent));
                pivotFeeDetailDTOList.add(custFee);
            }
            pivotFeeDetailRemoteService.savePivotFeeDetail(pivotFeeDetailDTOList);
        } catch (Exception e) {
            log.error("保存pivotFeeDetail错误 {}", e.getMessage(), e);
        }

    }

    private BigDecimal calculationTotalMoney(List<UserFundNavPO> userFundNavList) {
        BigDecimal totalShare = BigDecimal.ZERO;
        for (UserFundNavPO userFundNavPO : userFundNavList) {
            totalShare = totalShare.add(userFundNavPO.getTotalShare());
        }
        return totalShare;
    }

    private BigDecimal calculationPersent(UserFundNavPO fundNavPO, BigDecimal totalShare) {
        return fundNavPO.getTotalShare().divide(totalShare, 8, BigDecimal.ROUND_HALF_UP);
    }

}
