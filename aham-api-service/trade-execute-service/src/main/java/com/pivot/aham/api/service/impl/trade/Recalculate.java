package com.pivot.aham.api.service.impl.trade;

import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.req.ReCalBuyEtfInBalReqDTO;
import com.pivot.aham.api.server.remoteservice.AccountReBalanceRemoteService;
import com.pivot.aham.api.service.mapper.DailyClosingPriceMapper;
import com.pivot.aham.api.service.mapper.EtfMergeOrderMapper;
import com.pivot.aham.api.service.mapper.EtfOrderMapper;
import com.pivot.aham.api.service.mapper.model.DailyClosingPricePO;
import com.pivot.aham.api.service.mapper.model.EtfMergeOrderPO;
import com.pivot.aham.api.service.mapper.model.EtfOrderPO;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.enums.EtfOrderStatusEnum;
import com.pivot.aham.common.enums.EtfOrderTypeEnum;
import com.pivot.aham.common.enums.EtfmergeOrderTypeEnum;
import com.pivot.aham.common.enums.TransferStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @program: aham
 * @description:
 * @author: zhang7
 * @create: 2019-07-02 15:40
 *
 */
@Component
@Slf4j
public class Recalculate {

    @Autowired
    private EtfOrderMapper etfOrderMapper;

    @Autowired
    private EtfMergeOrderMapper etfMergeOrderMapper;

    @Resource
    private AccountReBalanceRemoteService accountReBalanceRemoteService;

    @Autowired
    private DailyClosingPriceMapper dailyClosingPriceMapper;

    public void recalculate() {
        List<ReCalBuyEtfInBalReqDTO> reqList = Lists.newArrayList();

        final List<EtfOrderTypeEnum> conditionSell = Lists.newArrayList(EtfOrderTypeEnum.RSA,
                EtfOrderTypeEnum.RSP);

        List<EtfOrderPO> balanceSellOrderList = etfOrderMapper.getListByStatusAndType(EtfOrderStatusEnum.WAIT_NOTIFY, conditionSell);
        for (EtfOrderPO etfOrderPO : balanceSellOrderList) {
            EtfMergeOrderPO etfMergeOrderPO = etfMergeOrderMapper.getById(etfOrderPO.getMergeOrderId());
            BigDecimal price;
            if (etfMergeOrderPO.getOrderType() == EtfmergeOrderTypeEnum.DO_NOTHING) {
                DailyClosingPricePO dailyClosingPricePO = dailyClosingPriceMapper.getLastPrice(etfOrderPO.getProductCode());
                price = dailyClosingPricePO.getPrice();

            } else {
                price = etfOrderPO.getConfirmAmount().add(etfOrderPO.getCostFee()).divide(etfOrderPO.getConfirmShare(), 6, BigDecimal.ROUND_DOWN);
            }
            log.info("etfOrderId:{},productCode:{},price:{}.", etfOrderPO.getId(), etfOrderPO.getProductCode(), price);
            etfOrderMapper.updatePrice(etfOrderPO.getId(), price);

            ReCalBuyEtfInBalReqDTO etfInBalReq = new ReCalBuyEtfInBalReqDTO();
            etfInBalReq.setConfirmPrice(price);
            etfInBalReq.setTmpOrderId(etfOrderPO.getOutBusinessId());
            etfInBalReq.setAccountId(etfOrderPO.getAccountId());
            etfInBalReq.setProductCode(etfOrderPO.getProductCode());
            etfInBalReq.setConfirmMoney(etfOrderPO.getConfirmAmount());
            etfInBalReq.setConfirmShare(etfOrderPO.getConfirmShare());
            etfInBalReq.setTransferStatus(TransferStatusEnum.SUCCESS);
            reqList.add(etfInBalReq);
        }

        if (!CollectionUtils.isEmpty(reqList)) {
            RpcMessage rpcMessage = accountReBalanceRemoteService.reCalBuyEtfInBal(reqList);
            if (rpcMessage.isSuccess()) {
//                mergeOrder.mergeEtfOrderForOrderType(false,false);
                log.info("recalculate 调用成功");
            } else {
                ErrorLogAndMailUtil.logErrorForTrade(log, "recalculate 调用 reCalBuyEtfInBal 失败！");
            }
        }
    }

}
