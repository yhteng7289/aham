package com.pivot.aham.api.service.impl;

import com.pivot.aham.api.server.dto.req.SaxoTradeReq;
import com.pivot.aham.api.server.dto.resp.SaxoTradeResult;
import com.pivot.aham.api.service.EtfOrderService;
import com.pivot.aham.api.service.mapper.EtfInfoMapper;
import com.pivot.aham.api.service.mapper.EtfOrderMapper;
import com.pivot.aham.api.service.mapper.model.EtfInfoPO;
import com.pivot.aham.api.service.mapper.model.EtfOrderPO;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.EtfOrderStatusEnum;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EtfOrderServiceImpl implements EtfOrderService {

    @Autowired
    private EtfOrderMapper etfOrderMapper;

    @Autowired
    private EtfInfoMapper etfInfoMapper;

    @Override
    public RpcMessage<SaxoTradeResult> createBuyOrder(SaxoTradeReq saxoTradeReq) {
        if (saxoTradeReq == null) {
            log.error("saxoTradeReq == null.");
            return RpcMessage.error("saxoTradeReq = null");
        }

        log.info("createBuyOrder, saxoTradeReq: {} ", saxoTradeReq);

        if (saxoTradeReq.getAmount().compareTo(BigDecimal.ZERO) > 0) {

            EtfInfoPO etfInfoPO = etfInfoMapper.getByCode(saxoTradeReq.getEtfCode());
            if (etfInfoPO == null) {
                log.error("交易执行层不支持此ETF产品:{}.", saxoTradeReq.getEtfCode());
                return RpcMessage.error("交易执行层不支持此ETF产品！");
            }

            EtfOrderPO order = new EtfOrderPO();
            order.setId(Sequence.next());
            order.setAccountId(saxoTradeReq.getAccountId());
            order.setOrderType(saxoTradeReq.getOrderType());
            order.setOrderStatus(EtfOrderStatusEnum.WAIT_MERGE);
            order.setProductCode(saxoTradeReq.getEtfCode());
            order.setApplyTime(DateUtils.now());
            order.setApplyAmount(saxoTradeReq.getAmount());
            order.setOutBusinessId(saxoTradeReq.getOutBusinessId());
            etfOrderMapper.save(order);

            SaxoTradeResult result = new SaxoTradeResult();
            result.setOrderId(order.getId());
            return RpcMessage.success(result);
        } else {
            SaxoTradeResult result = new SaxoTradeResult();
            result.setOrderId(0L);
            return RpcMessage.success(result);
        }
    }

    @Override
    public RpcMessage<SaxoTradeResult> createSellOrder(SaxoTradeReq saxoTradeReq) {
        if (saxoTradeReq == null) {
            log.error("saxoTradeReq == null.");
            return RpcMessage.error("saxoTradeReq = null");
        }

        if (saxoTradeReq.getAmount().compareTo(BigDecimal.ZERO) > 0) {

            EtfInfoPO etfInfoPO = etfInfoMapper.getByCode(saxoTradeReq.getEtfCode());
            if (etfInfoPO == null) {
                log.error("交易执行层不支持此ETF产品:{}.", saxoTradeReq.getEtfCode());
                return RpcMessage.error("交易执行层不支持此ETF产品！");
            }

            EtfOrderPO order = new EtfOrderPO();
            order.setId(Sequence.next());
            order.setAccountId(saxoTradeReq.getAccountId());
            order.setOrderType(saxoTradeReq.getOrderType());
            order.setOrderStatus(EtfOrderStatusEnum.WAIT_MERGE);
            order.setProductCode(saxoTradeReq.getEtfCode());
            order.setApplyTime(DateUtils.now());
            order.setApplyAmount(saxoTradeReq.getAmount());
            order.setOutBusinessId(saxoTradeReq.getOutBusinessId());
            etfOrderMapper.save(order);

            SaxoTradeResult result = new SaxoTradeResult();
            result.setOrderId(order.getId());
            return RpcMessage.success(result);
        } else {
            SaxoTradeResult result = new SaxoTradeResult();
            result.setOrderId(0L);
            return RpcMessage.success(result);
        }
    }
}
