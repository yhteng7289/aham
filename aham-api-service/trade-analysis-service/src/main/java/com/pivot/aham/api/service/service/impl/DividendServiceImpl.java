package com.pivot.aham.api.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.DividendCallBackDTO;
import com.pivot.aham.api.service.job.wrapperbean.DividendWrapperBean;
import com.pivot.aham.api.service.mapper.model.*;
import com.pivot.aham.api.service.service.*;
import com.pivot.aham.common.enums.analysis.*;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.ProductAssetStatusEnum;
import com.pivot.aham.common.enums.RedeemTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by luyang.li on 2018/12/24.
 */
@Service
@Slf4j
public class DividendServiceImpl implements DividendService {

    @Resource
    private AccountUserService accountUserService;
    @Resource
    private UserAssetService userAssetService;
    @Resource
    private DividendSupportService dividendSupportService;
    @Resource
    private RedeemApplyService redeemApplyService;
    @Resource
    private PivotPftAccountService pivotPftAccountService;
    @Resource
    private PivotPftAssetService pivotPftAssetService;

    /**
     * 给每个账户分配分红
     *
     * @param accountEtfSharesList
     * @param dividendCallBackDTO
     * @param totalShares
     */
    public void handelAccountAndUserDividend(List<AccountEtfSharesPO> accountEtfSharesList,
                                             DividendCallBackDTO dividendCallBackDTO,
                                             BigDecimal totalShares) {
        BigDecimal netAmount = dividendCallBackDTO.getNetAmountAccountCurrency().setScale(6, BigDecimal.ROUND_DOWN);
        //分配pft账户分红
        HandlePft handlePft = new HandlePft(dividendCallBackDTO, totalShares, netAmount).invoke();
        totalShares = handlePft.getTotalShares();
        BigDecimal pftDividendMoney = handlePft.getPftDividend();

        log.info("给每个账户分配分红,netAmount:{}", netAmount);
        int successNum = 0;
        BigDecimal successMoney = pftDividendMoney;
        for (AccountEtfSharesPO accountEtfSharesPO : accountEtfSharesList) {
            try {
                successNum++;
                BigDecimal precent = accountEtfSharesPO.getShares().divide(totalShares, 6, BigDecimal.ROUND_DOWN);
                BigDecimal accountDividend = netAmount.multiply(precent).setScale(6, BigDecimal.ROUND_DOWN);
                successMoney = successMoney.add(accountDividend);
                if (accountDividend.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                log.info("给每个账户分配分红开始,accountId:{},precent:{},accountDividend:{}", accountEtfSharesPO.getAccountId(), precent, accountDividend);
                AccountDividendPO accountDividendPO = handelAccountDividend(accountEtfSharesPO, dividendCallBackDTO, accountDividend);
                log.info("给每个账户分配分红完成,precent:{},accountDividendPO:{}", precent, JSON.toJSONString(accountDividendPO));
                if (null == accountDividendPO) {
                    log.error("给每个账户分配分红,没有构造出账户的分红。accountId:{}", accountEtfSharesPO.getAccountId());
                    continue;
                }
                if (successNum == accountEtfSharesList.size()) {
                    //处理尾差
                    BigDecimal remainMoney = netAmount.subtract(successMoney);
                    if (remainMoney.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal dividendMoney = accountDividendPO.getDividendAmount().add(remainMoney).setScale(6, BigDecimal.ROUND_DOWN);
                        accountDividendPO.setDividendAmount(dividendMoney);
                    }
                }
                dividendSupportService.handelAccountDividend(accountDividendPO, accountEtfSharesPO);

                //处理该 account下的用户分红
                log.info("给每个账户下的用户分配分红开始,accountId:{},precent:{},accountDividendPO:{}", accountEtfSharesPO.getAccountId(), precent, JSON.toJSONString(accountDividendPO));
                List<UserDividendPO> userDividendPOS = handleUserDividend(accountEtfSharesPO, dividendCallBackDTO, accountDividendPO);
                log.info("给每个账户下的用户分配分红完成,accountId:{},accountDividendPO:{}", accountEtfSharesPO.getAccountId(), JSON.toJSONString(accountDividendPO));

                //分红添加进资产
                log.info("分红添加金资产，accountId:{},precent:{},accountDividend:{},accountDividendPO:{}",
                        accountEtfSharesPO.getAccountId(), precent, accountDividend, JSON.toJSONString(accountDividendPO));
                AccountAssetPO accountAssetPO = handelAccountNavDividend(accountDividendPO);
                log.info("分红添加金资产完成，accountId:{},precent:{},accountDividend:{},accountAssetPO:{}",
                        accountEtfSharesPO.getAccountId(), precent, accountDividend, JSON.toJSONString(accountAssetPO));

                dividendSupportService.handelUserDividend(userDividendPOS, accountAssetPO, accountDividendPO, accountEtfSharesPO);
            } catch (Exception ex) {
                log.error("accountId:{},处理分红异常：", accountEtfSharesPO.getAccountId(), ex);
            }
        }

    }

    /**
     * 分红添加进资产
     *
     * @param accountDividendPO
     * @return
     */
    public AccountAssetPO handelAccountNavDividend(AccountDividendPO accountDividendPO) {
        AccountAssetPO accountAssetPO = null;
        if (accountDividendPO.getNavDividendAmount().compareTo(BigDecimal.ZERO) > 0) {
            accountAssetPO = new AccountAssetPO();
            accountAssetPO.setAccountId(accountDividendPO.getAccountId());
            accountAssetPO.setApplyMoney(accountDividendPO.getNavDividendAmount());
            accountAssetPO.setApplyTime(accountDividendPO.getExDate());
            accountAssetPO.setConfirmMoney(accountDividendPO.getNavDividendAmount());
            accountAssetPO.setConfirmTime(DateUtils.now());
            accountAssetPO.setDividendOrderId(accountDividendPO.getDividendOrderId());
            accountAssetPO.setProductAssetStatus(ProductAssetStatusEnum.HOLD_ING);
            accountAssetPO.setRechargeOrderNo(0L);
            accountAssetPO.setTmpOrderId(0L);
            accountAssetPO.setTotalTmpOrderId(0L);
            if (CaEventTypeEnum.CASH.getValue().equals(accountDividendPO.getCaEventTypeID())) {
                accountAssetPO.setAssetSource(AssetSourceEnum.CASHDIVIDEND);
                accountAssetPO.setConfirmShare(BigDecimal.ZERO);
                accountAssetPO.setProductCode(Constants.CASH);
            } else {
                accountAssetPO.setAssetSource(AssetSourceEnum.STOCKDIVIDEND);
                accountAssetPO.setConfirmShare(accountDividendPO.getNavDividendAmount());
                accountAssetPO.setProductCode(accountDividendPO.getProductCode());
            }
        }
        return accountAssetPO;
    }

    /**
     * 处理该账户上的用户的分红
     *
     * @param accountEtfSharesPO
     * @param dividendCallBackDTO
     * @param accountDividendPO
     */
    public List<UserDividendPO> handleUserDividend(AccountEtfSharesPO accountEtfSharesPO,
                                                   DividendCallBackDTO dividendCallBackDTO,
                                                   AccountDividendPO accountDividendPO) {
        List<UserDividendPO> userDividendPOS = Lists.newArrayList();
        Date lastExDate = DateUtils.addDateByDay(dividendCallBackDTO.getExDate(), -1);
        //这里去exDate是为了也渠道 ExDate-1 当天的用户
        Date lastExTime = DateUtils.getDate(lastExDate, 23, 59, 59);
        AccountUserPO accountUserParam = new AccountUserPO();
        accountUserParam.setAccountId(accountEtfSharesPO.getAccountId());
        accountUserParam.setEffectTime(lastExTime);
        List<AccountUserPO> accountUserPOS = accountUserService.listAccountUserBeforeEffectTime(accountUserParam);
        if (CollectionUtils.isEmpty(accountUserPOS)) {
            log.info("处理该账户上的用户的分红,accountId:{},没有符合分红条件的用户", accountEtfSharesPO.getAccountId());
            return userDividendPOS;
        }
        List<String> clientIds = accountUserPOS.stream().map(AccountUserPO::getClientId).collect(Collectors.toList());
        List<UserAssetPO> userAssetPOS = userAssetService.litsUserAsset(accountEtfSharesPO.getAccountId(), clientIds,
                lastExDate, accountEtfSharesPO.getProductCode());
        if (CollectionUtils.isEmpty(userAssetPOS)) {
            log.error("处理该账户上的用户的分红,accountId:{},clientIds:{},没有可用的资产", accountEtfSharesPO.getAccountId(), JSON.toJSONString(clientIds));
//            throw new BusinessException("处理该账户上的用户的分红,没有可用的资产");
            return userDividendPOS;
        }
        BigDecimal totalShare = BigDecimal.ZERO;
        for (UserAssetPO userAssetPO : userAssetPOS) {
            totalShare = totalShare.add(userAssetPO.getShare()).setScale(6, BigDecimal.ROUND_HALF_UP);
        }
        //记录扣减没有领取的分红
        for (UserAssetPO userAssetPO : userAssetPOS) {
            BigDecimal precent = userAssetPO.getShare().divide(totalShare, 6, BigDecimal.ROUND_DOWN);
            BigDecimal userDividend = accountDividendPO.getDividendAmount().multiply(precent).setScale(6, BigDecimal.ROUND_DOWN);
            if (userDividend.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            //记录用户分红
            UserDividendPO userDividendPO = new UserDividendPO();
            userDividendPO.setAccountId(accountEtfSharesPO.getAccountId());
            userDividendPO.setClientId(userAssetPO.getClientId());
            userDividendPO.setDividendAmount(userDividend);
            userDividendPO.setDividendDate(DateUtils.getDate(dividendCallBackDTO.getExDate(), 0, 0, 0));
            userDividendPO.setDividendOrderId(dividendCallBackDTO.getDividendOrderId());
            userDividendPO.setHandelStatus(DividendHandelStatusEnum.DEFAULT);
            userDividendPO.setGoalId(userAssetPO.getGoalId());
            userDividendPO.setHandelType(DividendHandelTypeEnum.USED_NAV);
            userDividendPO.setProductCode(dividendCallBackDTO.getProductCode());
            userDividendPO.setCreateTime(DateUtils.now());
            userDividendPO.setUpdateTime(DateUtils.now());
            //用户payDate资产为0 就在NAV的时候用作TPCF
            BigDecimal tradeDateShare = getUserTradeDateShares(dividendCallBackDTO, userAssetPO);

            //查询这个用户是否有全部赎回订单
            RedeemApplyPO redeemApplyQuery = new RedeemApplyPO();
            redeemApplyQuery.setClientId(userAssetPO.getClientId());
            redeemApplyQuery.setGoalId(userAssetPO.getGoalId());
            redeemApplyQuery.setRedeemType(RedeemTypeEnum.ALLRedeem);
            redeemApplyQuery.setEtfExecutedStatus(EtfExecutedStatusEnum.DEFAULT);
            RedeemApplyPO redeemApply = redeemApplyService.selectOne(redeemApplyQuery);
            if(redeemApply != null){
                userDividendPO.setHandelType(DividendHandelTypeEnum.USED_COMMONWEAL);
            }

            if (tradeDateShare.compareTo(BigDecimal.ZERO) <= 0) {
                userDividendPO.setHandelType(DividendHandelTypeEnum.USED_COMMONWEAL);
            }
            userDividendPOS.add(userDividendPO);
        }
        //残渣处理记录到公益账户上
        DividendWrapperBean dividendWrapperBean = actuallyUserDividend(userDividendPOS, accountDividendPO);
        //扣减没有领取的分红
        BigDecimal remainDividend = accountDividendPO.getDividendAmount();
        if (dividendWrapperBean.getSubtractMoney().compareTo(BigDecimal.ZERO) > 0) {
            remainDividend = accountDividendPO.getDividendAmount().subtract(dividendWrapperBean.getSubtractMoney()).setScale(6, BigDecimal.ROUND_HALF_UP);
        }
        accountDividendPO.setNavDividendAmount(remainDividend);
        //该状态只在计算完NAV之后设置成功
//        accountDividendPO.setHandelStatus(DividendHandelStatusEnum.SUCCESS);

        log.info("用户分配分红，account上扣减，subtractMoney:{},userDividendPOS:{},accountDividendPO:{}",
                dividendWrapperBean.getSubtractMoney(), JSON.toJSONString(userDividendPOS), JSON.toJSONString(accountDividendPO));
        return dividendWrapperBean.getUserDividendPOList();

    }

    /**
     * 用户实际分配的分红
     *
     * @param userDividendPOS
     * @param accountDividendPO
     * @return
     */
    private DividendWrapperBean actuallyUserDividend(List<UserDividendPO> userDividendPOS,
                                                     AccountDividendPO accountDividendPO) {
        DividendWrapperBean dividendWrapperBean = new DividendWrapperBean();
        List<UserDividendPO> actuallyUserDividends = Lists.newArrayList();
        BigDecimal subtractMoney = BigDecimal.ZERO;
        int successNum = 0;
        BigDecimal successMoney = BigDecimal.ZERO;
        for (UserDividendPO userDividendPO : userDividendPOS) {
            successNum++;
            successMoney = successMoney.add(userDividendPO.getDividendAmount()).setScale(6, BigDecimal.ROUND_HALF_UP);
            if (successNum == userDividendPOS.size()) {
                //处理尾差
                BigDecimal remainMoney = accountDividendPO.getDividendAmount().subtract(successMoney);
                if (remainMoney.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal dividendMoney = userDividendPO.getDividendAmount().add(remainMoney).setScale(6, BigDecimal.ROUND_DOWN);
                    userDividendPO.setDividendAmount(dividendMoney);
                }
            }
            actuallyUserDividends.add(userDividendPO);

            if (DividendHandelTypeEnum.USED_COMMONWEAL == userDividendPO.getHandelType()) {
                subtractMoney = subtractMoney.add(userDividendPO.getDividendAmount()).setScale(6, BigDecimal.ROUND_HALF_UP);
            }
        }
        dividendWrapperBean.setSubtractMoney(subtractMoney);
        dividendWrapperBean.setUserDividendPOList(actuallyUserDividends);
        return dividendWrapperBean;
    }

    private BigDecimal getUserTradeDateShares(DividendCallBackDTO dividendCallBackDTO, UserAssetPO userAssetPO) {
        BigDecimal tradeDateShare = BigDecimal.ZERO;
        UserAssetPO userAssetParam = new UserAssetPO();
        userAssetParam.setAccountId(userAssetPO.getAccountId());
        userAssetParam.setClientId(userAssetPO.getClientId());
        userAssetParam.setProductCode(dividendCallBackDTO.getProductCode());
        userAssetParam.setGoalId(userAssetPO.getGoalId());
        userAssetParam.setAssetTime(DateUtils.dayStart(dividendCallBackDTO.getTradeDate()));
        UserAssetPO tradeDateUserAsset = userAssetService.queryUserAssetPo(userAssetParam);
        if (null != tradeDateUserAsset) {
            tradeDateShare = tradeDateUserAsset.getShare();
        }
        return tradeDateShare;
    }

    /**
     * 处理账户的分红
     *
     * @param accountEtfSharesPO
     * @param dividendCallBackDTO
     * @param accountDividend
     * @return
     */
    private AccountDividendPO handelAccountDividend(AccountEtfSharesPO accountEtfSharesPO,
                                                    DividendCallBackDTO dividendCallBackDTO,
                                                    BigDecimal accountDividend) {
        AccountDividendPO accountDividendPO = new AccountDividendPO();
        accountDividendPO.setId(Sequence.next());
        accountDividendPO.setCaEventTypeID(dividendCallBackDTO.getCaEventTypeEnum().getValue());
        accountDividendPO.setCaEventTypeName(dividendCallBackDTO.getCaEventTypeEnum().getDesc());
        accountDividendPO.setExDate(dividendCallBackDTO.getExDate());
        accountDividendPO.setTradeDate(dividendCallBackDTO.getTradeDate());
        accountDividendPO.setAccountId(accountEtfSharesPO.getAccountId());
        accountDividendPO.setDividendAmount(accountDividend);
        accountDividendPO.setHandelStatus(DividendHandelStatusEnum.DEFAULT);
        accountDividendPO.setNavDividendAmount(BigDecimal.ZERO);
        accountDividendPO.setProductCode(dividendCallBackDTO.getProductCode());
        accountDividendPO.setDividendOrderId(dividendCallBackDTO.getDividendOrderId());
        accountDividendPO.setCreateTime(DateUtils.now());
        accountDividendPO.setUpdateTime(DateUtils.now());
        return accountDividendPO;
    }

    private class HandlePft {
        private DividendCallBackDTO dividendCallBackDTO;
        private BigDecimal totalShares;
        private BigDecimal netAmount;
        private BigDecimal pftDividend;

        public HandlePft(DividendCallBackDTO dividendCallBackDTO, BigDecimal totalShares, BigDecimal netAmount) {
            this.dividendCallBackDTO = dividendCallBackDTO;
            this.totalShares = totalShares;
            this.netAmount = netAmount;
        }

        public BigDecimal getTotalShares() {
            return totalShares;
        }

        public BigDecimal getPftDividend() {
            return pftDividend;
        }

        public HandlePft invoke() {
            PivotPftAccountPO pivotPftAccountQuery = new PivotPftAccountPO();
            pivotPftAccountQuery.setProductCode(dividendCallBackDTO.getProductCode());
            PivotPftAccountPO pivotPftAccount = pivotPftAccountService.selectOne(pivotPftAccountQuery);

            if(pivotPftAccount == null){
                pftDividend = BigDecimal.ZERO;
                return this;
            }


            totalShares = totalShares.add(pivotPftAccount.getShare());
            BigDecimal pftPrecent = pivotPftAccount.getShare().divide(totalShares, 6, BigDecimal.ROUND_DOWN);
            pftDividend = netAmount.multiply(pftPrecent).setScale(6, BigDecimal.ROUND_DOWN);
            //分配pft账户资产
            PivotPftAssetPO pivotPftAssetCashUpdate = new PivotPftAssetPO();
            pivotPftAssetCashUpdate.setExecuteOrderNo(Sequence.next());
            if (CaEventTypeEnum.CASH == dividendCallBackDTO.getCaEventTypeEnum()) {
                pivotPftAssetCashUpdate.setConfirmMoney(pftDividend);
                pivotPftAssetCashUpdate.setConfirmShare(BigDecimal.ZERO);
                pivotPftAssetCashUpdate.setProductCode(Constants.CASH);
            }else{
                pivotPftAssetCashUpdate.setConfirmMoney(pftDividend);
                pivotPftAssetCashUpdate.setConfirmShare(BigDecimal.ZERO);
                pivotPftAssetCashUpdate.setProductCode(Constants.CASH);
            }
            pivotPftAssetCashUpdate.setExecuteTime(DateUtils.now());
            pivotPftAssetCashUpdate.setPftAssetSource(PftAssetSourceEnum.DIVIDEND);
            pivotPftAssetCashUpdate.setProductAssetStatus(ProductAssetStatusEnum.HOLD_ING);
            pivotPftAssetService.updateOrInsertPivotPftAsset(pivotPftAssetCashUpdate);
            return this;
        }
    }
}
