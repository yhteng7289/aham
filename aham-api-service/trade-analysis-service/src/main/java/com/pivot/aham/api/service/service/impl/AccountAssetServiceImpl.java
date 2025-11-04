package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.AccountAssetMapper;
import com.pivot.aham.api.service.mapper.model.AccountAssetPO;
import com.pivot.aham.api.service.mapper.model.AccountRechargePO;
import com.pivot.aham.api.service.service.AccountAssetService;
import com.pivot.aham.api.service.service.AccountRedeemService;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.ProductAssetStatusEnum;
import com.pivot.aham.common.enums.analysis.AssetSourceEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author addison
 */
@Service
public class AccountAssetServiceImpl extends BaseServiceImpl<AccountAssetPO, AccountAssetMapper> implements AccountAssetService {

    @Resource
    private AccountRedeemService accountRedeemService;

    @Override
    public List<AccountAssetPO> listAccountUnBuyAssets(AccountAssetPO accountAssetPO) {
        return mapper.listAccountUnBuyAssets(accountAssetPO);
    }

    @Override
    public void insertBatch(List<AccountAssetPO> accountAssetList) {
        mapper.insertBatch(accountAssetList);
    }

    @Override
    public List<AccountAssetPO> listAccountAssetBeforeDate(Long accountId, Date endTime) {
        return mapper.listAccountAssetBeforeDate(accountId, endTime);
    }

    @Override
    public void saveAccountAsset(AccountAssetPO accountAsset) {
        mapper.saveAccountAsset(accountAsset);
    }

    @Override
    public AccountAssetPO queryAccountAsset(AccountAssetPO queryParam) {
        return mapper.queryAccountAsset(queryParam);
    }

    @Override
    public void update(AccountAssetPO accountAssetPO) {
        mapper.update(accountAssetPO);
    }

    @Override
    public AccountAssetPO genUnBuyHoldAccountAssetInfo(AccountRechargePO accountRecharge) {
        AccountAssetPO accountAsset = new AccountAssetPO();
        accountAsset.setAssetSource(AssetSourceEnum.RECHARGE);
        accountAsset.setAccountId(accountRecharge.getAccountId());
        accountAsset.setApplyMoney(accountRecharge.getRechargeAmount());
        accountAsset.setApplyTime(accountRecharge.getRechargeTime());
        accountAsset.setConfirmMoney(accountRecharge.getRechargeAmount());
        accountAsset.setConfirmShare(BigDecimal.ZERO);
        accountAsset.setRechargeOrderNo(accountRecharge.getRechargeOrderNo());
        accountAsset.setProductAssetStatus(ProductAssetStatusEnum.HOLD_ING);
        accountAsset.setProductCode(Constants.UN_BUY_PRODUCT_CODE);
        accountAsset.setId(Sequence.next());
        accountAsset.setConfirmTime(DateUtils.now());
        return accountAsset;
    }

    @Override
    public List<AccountAssetPO> listByRechargeOrderNos(List<Long> recahrgeOrderNos) {
        return mapper.listByRechargeOrderNos(recahrgeOrderNos);
    }


}
