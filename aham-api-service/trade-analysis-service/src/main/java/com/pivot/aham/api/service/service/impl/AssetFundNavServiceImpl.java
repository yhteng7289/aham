package com.pivot.aham.api.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pivot.aham.api.server.dto.ProductInfoResDTO;
import com.pivot.aham.api.server.dto.req.ClosingPriceReq;
import com.pivot.aham.api.server.dto.resp.ClosingPriceItem;
import com.pivot.aham.api.server.dto.resp.ClosingPriceResult;
import com.pivot.aham.api.server.remoteservice.ModelServiceRemoteService;
import com.pivot.aham.api.server.remoteservice.SaxoTradeRemoteService;
import com.pivot.aham.api.service.mapper.AccountFundNavMapper;
import com.pivot.aham.api.service.mapper.model.AccountAssetPO;
import com.pivot.aham.api.service.mapper.model.AccountFundNavPO;
import com.pivot.aham.api.service.service.AccountAssetService;
import com.pivot.aham.api.service.service.AssetFundNavService;
import com.pivot.aham.api.service.support.AccountAssetStatistic;
import com.pivot.aham.api.service.support.AccountAssetStatisticBean;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.ProductAssetStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
@Service
@Slf4j
public class AssetFundNavServiceImpl extends BaseServiceImpl<AccountFundNavPO, AccountFundNavMapper> implements AssetFundNavService {

    @Resource
    private SaxoTradeRemoteService saxoTradeRemoteService;
    @Resource
    private ModelServiceRemoteService modelServiceRemoteService;
    @Resource
    private AssetFundNavService assetFundNavService;

    //有一下状态说明有在途资产
    private static final List<ProductAssetStatusEnum> HAVING_ASSET_STATUS = Lists.newArrayList(
            ProductAssetStatusEnum.BUY_ING, ProductAssetStatusEnum.CONVERT_ING, ProductAssetStatusEnum.HOLD_ING,
            ProductAssetStatusEnum.SELL_ING
    );

    @Resource
    private AccountAssetService accountAssetService;

    @Override
    public boolean fundNavIsFirst(Date endTime, Long accountId) {
        List<AccountAssetPO> accountAssetPOs = accountAssetService.listAccountAssetBeforeDate(accountId, endTime);

        if (CollectionUtils.isEmpty(accountAssetPOs)) {
            return true;
        }
        //查询所etf的收市价格
        Date yesterday = DateUtils.addDateByDay(DateUtils.now(), -1);
        Map<String, BigDecimal> etfClosingPriceMap = assetFundNavService.getEtfClosingPrice(yesterday);
        List<AccountAssetStatisticBean> accountAssetStatisticBeens = AccountAssetStatistic.statAccountAsset(accountAssetPOs, etfClosingPriceMap);
        if (CollectionUtils.isEmpty(accountAssetStatisticBeens)) {
            return true;
        }
        for (AccountAssetStatisticBean accountAssetStatisticBeen : accountAssetStatisticBeens) {
            if (HAVING_ASSET_STATUS.contains(accountAssetStatisticBeen.getProductAssetStatus())) {
                return false;
            }
        }
        return false;
    }

    @Override
    public Map<String, BigDecimal> getEtfClosingPrice(Date now) {
        List<ProductInfoResDTO> productInfoDTOs = modelServiceRemoteService.queryAllProductInfo();
        Map<String, BigDecimal> etfClosingPriceMap = Maps.newHashMap();
        List<String> productCode = productInfoDTOs.stream().map(ProductInfoResDTO::getProductCode).collect(Collectors.toList());
        ClosingPriceReq req = new ClosingPriceReq();
        req.setEtfCodeList(productCode);
        Date date = DateUtils.parseDate(DateUtils.formatDate(now, DateUtils.DATE_FORMAT));
        req.setDate(date);

        RpcMessage<ClosingPriceResult> resultRpcMessage = saxoTradeRemoteService.queryClosingPrice(req);
        log.info("查询收市价,入参{},出参{}:", JSON.toJSON(req), JSON.toJSON(resultRpcMessage));
        if (RpcMessageStandardCode.OK.value() == resultRpcMessage.getResultCode()) {
            List<ClosingPriceItem> closingPriceItemList = resultRpcMessage.getContent().getClosingPriceItemList();
            etfClosingPriceMap = closingPriceItemList.stream().collect(Collectors.toMap(ClosingPriceItem::getEtfCode, ClosingPriceItem::getPrice));
        }

        return etfClosingPriceMap;
    }

    @Override
    public Map<String, BigDecimal> getEtfClosingPrice() {
        List<ProductInfoResDTO> productInfoDTOs = modelServiceRemoteService.queryAllProductInfo();
        List<String> productCode = productInfoDTOs.stream().map(ProductInfoResDTO::getProductCode).collect(Collectors.toList());

        Set<String> etfCodeList = Sets.newHashSet(productCode);
        Map<String, BigDecimal> dailyClosingPriceMap = Maps.newHashMap();
        for (String etfCode : etfCodeList) {
            RpcMessage<BigDecimal> resultRpcMessage = saxoTradeRemoteService.queryClosingPrice(etfCode);
            if (RpcMessageStandardCode.OK.value() == resultRpcMessage.getResultCode()) {
                dailyClosingPriceMap.put(etfCode, resultRpcMessage.getContent());
            }
        }
        log.info("查询收市价,出参{}:", JSON.toJSON(dailyClosingPriceMap));
        return dailyClosingPriceMap;
    }

    @Override
    public AccountFundNavPO selectOneByNavTime(AccountFundNavPO accountFundNavPO) {
        return mapper.selectOneByNavTime(accountFundNavPO);
    }

    @Override
    public void saveTodayAssetFundNav(AccountFundNavPO todayFundNav) {
        AccountFundNavPO queryPo = new AccountFundNavPO();
        queryPo.setAccountId(todayFundNav.getAccountId());
        queryPo.setNavTime(todayFundNav.getNavTime());
        AccountFundNavPO alreadyExistPo = mapper.selectOneByNavTime(queryPo);
        if (null != alreadyExistPo) {
            todayFundNav.setId(alreadyExistPo.getId());
            mapper.updateAccountFundNav(todayFundNav);
        } else {
            mapper.insertAccountFundNav(todayFundNav);
        }
    }

    @Override
    public List<AccountFundNavPO> queryListByNavTime(AccountFundNavPO accountFundNavPO) {
        return mapper.queryListByNavTime(accountFundNavPO);
    }
}
