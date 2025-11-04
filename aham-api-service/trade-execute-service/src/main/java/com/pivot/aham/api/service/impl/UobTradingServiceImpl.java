package com.pivot.aham.api.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pivot.aham.api.server.dto.UobExchangeCallbackDTO;
import com.pivot.aham.api.server.dto.UobTransferToSaxoCallbackDTO;
import com.pivot.aham.api.server.dto.UobRechargeLogDTO;
import com.pivot.aham.api.server.remoteservice.ExchangeRemoteService;
import com.pivot.aham.api.server.remoteservice.RechargeServiceRemoteService;
import com.pivot.aham.api.service.UobTradingService;
import com.pivot.aham.api.service.client.uob.UobConstants;
import com.pivot.aham.api.service.client.uob.excel.*;
import com.pivot.aham.api.service.mapper.*;
import com.pivot.aham.api.service.mapper.model.*;
import com.pivot.aham.common.enums.*;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.support.cache.RedissonHelper;
import com.pivot.aham.common.core.support.file.excel.ExportExcel;
import com.pivot.aham.common.core.support.file.excel.ImportExcel;
import com.pivot.aham.common.core.support.file.ftp.FTPClientUtil;
import com.pivot.aham.common.core.support.file.ftp.SftpClient;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;
import com.pivot.aham.common.enums.recharge.UobRechargeStatusEnum;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.annotation.Resource;
import org.apache.commons.io.IOUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Created by hao.tong on 2018/12/12.
 */
@Service("uobTradingService")
@Slf4j
public class UobTradingServiceImpl implements UobTradingService {

    private static final BigDecimal MAX_SGD = new BigDecimal(190000);

    @Autowired
    private UobTransferOrderMapper uobTransferOrderMapper;

    @Autowired
    private UobTransferExecutionOrderMapper uobTransferExecutionOrderMapper;

    @Autowired
    private UobTransferOrderRelationMapper uobTransferOrderRelationMapper;

    @Autowired
    private UobExchangeOrderMapper uobExchangeOrderMapper;

    @Autowired
    private SaxoAccountFundingEventMapper saxoAccountFundingEventMapper;

    @Autowired
    private UobRechargeLogMapper uobRechargeLogMapper;

    @Autowired
    private RechargeServiceRemoteService rechargeServiceRemoteService;

    @Autowired
    private ExchangeRemoteService exchangeRemoteService;

    @Resource
    private RedissonHelper redissonHelper;

    @Override
    @Transactional
    public void createTransferExecutionOrderClient() {
        List<UobTransferOrderPO> orderList = uobTransferOrderMapper.getOrderList(
                Lists.newArrayList(UobTransferOrderTypeEnum.TRANSFER_TO_BANK),
                Lists.newArrayList(UobOrderStatusEnum.WAIT_CREATE_ORDER));

        if (!CollectionUtils.isEmpty(orderList)) {
            List<UobTransferOrderPO> orderListMoreThan20W = Lists.newArrayList();
            List<UobTransferOrderPO> orderListEqual20W = Lists.newArrayList();
            List<UobTransferOrderPO> orderListMYR = Lists.newArrayList();

            for (UobTransferOrderPO order : orderList) {
                if (order.getCurrency() == CurrencyEnum.SGD) {
                    if (order.getAmount().compareTo(MAX_SGD) <= 0) {
                        orderListEqual20W.add(order);
                    }

                    if (order.getAmount().compareTo(MAX_SGD) > 0) {
                        orderListMoreThan20W.add(order);
                    }
                }

                if (order.getCurrency() == CurrencyEnum.MYR) {
                    orderListMYR.add(order);
                }
            }

            this.createUobOrderByMore(orderListMoreThan20W);
            this.createUobOrderByEqual(orderListEqual20W);
            this.createUobOrderMYR(orderListMYR);

            for (UobTransferOrderPO order : orderList) {
                uobTransferOrderMapper.updateStatus(order.getId(), UobOrderStatusEnum.WAIT_CONFIRM);
            }
        }
    }

    @Override
    @Transactional
    public void createTransferExecutionOrderSaxo() {
        List<UobTransferOrderPO> orderList = uobTransferOrderMapper.getOrderList(
                Lists.newArrayList(UobTransferOrderTypeEnum.TRANSFER_TO_SAXO),
                Lists.newArrayList(UobOrderStatusEnum.WAIT_CREATE_ORDER));

        if (!CollectionUtils.isEmpty(orderList)) {
            List<UobTransferOrderPO> orderListLessThan20W = Lists.newArrayList();
            List<UobTransferOrderPO> orderListMoreThan20W = Lists.newArrayList();
            List<UobTransferOrderPO> orderListEqual20W = Lists.newArrayList();

            for (UobTransferOrderPO order : orderList) {
                if (order.getAmount().compareTo(MAX_SGD) < 0) {
                    orderListLessThan20W.add(order);
                    continue;
                }

                if (order.getAmount().compareTo(MAX_SGD) > 0) {
                    orderListMoreThan20W.add(order);
                    continue;
                }

                if (order.getAmount().compareTo(MAX_SGD) == 0) {
                    orderListEqual20W.add(order);
                }
            }

            this.createUobOrderByLess(orderListLessThan20W);
            this.createUobOrderByMore(orderListMoreThan20W);
            this.createUobOrderByEqual(orderListEqual20W);

            for (UobTransferOrderPO order : orderList) {
                uobTransferOrderMapper.updateStatus(order.getId(), UobOrderStatusEnum.WAIT_CONFIRM);
            }
        }
    }

    private void createUobOrderByLess(List<UobTransferOrderPO> orderListLessThan20W) {
        BigDecimal amount = BigDecimal.ZERO;
        Set<Long> mergeList = Sets.newHashSet();
        Iterator<UobTransferOrderPO> orderListLessThan20WIt = orderListLessThan20W.listIterator();
        while (orderListLessThan20WIt.hasNext()) {
            UobTransferOrderPO transferOrder = orderListLessThan20WIt.next();

            mergeList.add(transferOrder.getId());
            amount = amount.add(transferOrder.getAmount());

            if (amount.compareTo(MAX_SGD) >= 0 || !orderListLessThan20WIt.hasNext()) {
                boolean initVal = false;
                if (amount.compareTo(MAX_SGD) > 0) {
                    initVal = true;
                    mergeList.remove(transferOrder.getId());
                    amount = amount.subtract(transferOrder.getAmount());
                }

                Long executionOrderId = this.buildExecutionOrder(transferOrder, amount, UobOrderRelationTypeEnum.MERGE);
                for (Long orderId : mergeList) {
                    this.buildRelation(orderId, executionOrderId, UobOrderRelationTypeEnum.MERGE);
                }

                if (initVal) {
                    amount = transferOrder.getAmount();
                    mergeList.add(transferOrder.getId());
                } else {
                    amount = BigDecimal.ZERO;
                    mergeList.clear();
                }
            }
        }
    }

    private void createUobOrderByMore(List<UobTransferOrderPO> orderListMoreThan20W) {
        for (UobTransferOrderPO transferOrder : orderListMoreThan20W) {
            BigDecimal surplusAmount = transferOrder.getAmount();

            while (true) {
                BigDecimal amount = surplusAmount.min(MAX_SGD);

                Long executionOrderId = this.buildExecutionOrder(transferOrder, amount, UobOrderRelationTypeEnum.SPLIT);
                this.buildRelation(transferOrder.getId(), executionOrderId, UobOrderRelationTypeEnum.SPLIT);

                surplusAmount = surplusAmount.subtract(amount);
                if (surplusAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }
            }
        }
    }

    private void createUobOrderByEqual(List<UobTransferOrderPO> orderListEqual20W) {
        for (UobTransferOrderPO transferOrder : orderListEqual20W) {
            Long executionOrderId = this.buildExecutionOrder(transferOrder, transferOrder.getAmount(), UobOrderRelationTypeEnum.EQUAL);
            this.buildRelation(transferOrder.getId(), executionOrderId, UobOrderRelationTypeEnum.EQUAL);
        }
    }

    private void createUobOrderMYR(List<UobTransferOrderPO> orderListMYR) {
        for (UobTransferOrderPO transferOrder : orderListMYR) {
            Long executionOrderId = this.buildExecutionOrder(transferOrder, transferOrder.getAmount(), UobOrderRelationTypeEnum.EQUAL);
            this.buildRelation(transferOrder.getId(), executionOrderId, UobOrderRelationTypeEnum.EQUAL);
        }
    }

    private Long buildExecutionOrder(UobTransferOrderPO transferOrder, BigDecimal amount, UobOrderRelationTypeEnum relationType) {
        UobTransferExecutionOrderPO uobOrder = new UobTransferExecutionOrderPO();
        uobOrder.setId(Sequence.next());
        uobOrder.setBankName(transferOrder.getBankName());
        uobOrder.setBankAccountNumber(transferOrder.getBankAccountNumber());
        uobOrder.setBankUserName(transferOrder.getBankUserName());
        uobOrder.setBranchCode(transferOrder.getBranchCode());
        uobOrder.setSwiftCode(transferOrder.getSwiftCode());
        uobOrder.setOrderType(transferOrder.getOrderType());
        uobOrder.setOrderStatus(UobExecutionOrderStatusEnum.WAIT_EXECUTE);
        uobOrder.setCurrency(transferOrder.getCurrency());
        uobOrder.setAmount(amount);
        uobOrder.setCostFee(new BigDecimal(0.5));
        uobOrder.setApplyTime(transferOrder.getApplyTime());
        uobOrder.setRelationType(relationType);
        uobOrder.setRemark(transferOrder.getRemark());
        uobTransferExecutionOrderMapper.save(uobOrder);
        return uobOrder.getId();
    }

    private void buildRelation(Long businessOrderId, Long executionOrderId, UobOrderRelationTypeEnum relationType) {
        UobTransferOrderRelationPO relation = new UobTransferOrderRelationPO();
        relation.setBusinessOrderId(businessOrderId);
        relation.setExecutionOrderId(executionOrderId);
        relation.setRelationType(relationType);
        uobTransferOrderRelationMapper.save(relation);

        uobTransferOrderMapper.updateStatus(businessOrderId, UobOrderStatusEnum.WAIT_CONFIRM);
    }

    @Override
    public void executeTransferOrderClient() {
        executeTransferOrder(UobTransferOrderTypeEnum.TRANSFER_TO_BANK);
    }

    @Override
    public void executeTransferOrderSaxo() {
        executeTransferOrder(UobTransferOrderTypeEnum.TRANSFER_TO_SAXO);
    }

    private void executeTransferOrder(UobTransferOrderTypeEnum orderTypeEnum) {
        List<UobTransferExecutionOrderPO> executionOrderList = uobTransferExecutionOrderMapper.getOrderList(orderTypeEnum, Lists.newArrayList(UobExecutionOrderStatusEnum.WAIT_EXECUTE));

        BigDecimal totalAmount = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(executionOrderList)) {
            List<Long> idList = Lists.newArrayList();

            List<TransferExecutionOrderEM> exportList = Lists.newArrayList();
            for (UobTransferExecutionOrderPO executionOrder : executionOrderList) {
                TransferExecutionOrderEM em = new TransferExecutionOrderEM();
                em.setExecutionOrderId(String.valueOf(executionOrder.getId()));
                em.setBankName(executionOrder.getBankName());
                em.setBankAccountNumber(executionOrder.getBankAccountNumber());
                em.setBankUserName(executionOrder.getBankUserName());
                em.setBranchCode(executionOrder.getBranchCode());
                em.setSwiftCode(executionOrder.getSwiftCode());
                em.setCurrency(executionOrder.getCurrency().getCode());
                em.setAmount(executionOrder.getAmount().setScale(2, RoundingMode.DOWN).toString());
                em.setOrderTime(DateUtils.formatDate(executionOrder.getApplyTime(), DateUtils.DATE_TIME_FORMAT));
                if (executionOrder.getOrderType() == UobTransferOrderTypeEnum.TRANSFER_TO_SAXO) {
                    String clientId = PropertiesUtil.getString("saxo.openApi.client.id");
                    em.setRemark("Please input SAXO Account ID: " + clientId + " in message field while transfering fund from UOB to SAXO");
                } else {
                    em.setRemark(executionOrder.getRemark());
                }
                totalAmount.add(executionOrder.getAmount().setScale(2, RoundingMode.DOWN));
                exportList.add(em);
                idList.add(executionOrder.getId());
            }

            ExportExcel exportExcel = new ExportExcel(null, TransferExecutionOrderEM.class);
            exportExcel.setDataList(exportList);

            String fileName;
            String ftpPath;

            if (orderTypeEnum == UobTransferOrderTypeEnum.TRANSFER_TO_SAXO) {
                fileName = DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT2) + "_paymentOrder_saxo.xlsx";
                ftpPath = UobConstants.getPaymentOrderPath() + "saxo/" + fileName;
            } else {
                fileName = DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT2) + "_paymentOrder_client.xlsx";
                ftpPath = UobConstants.getPaymentOrderPath() + "client/" + fileName;
            }

            try (OutputStream outputStream = FTPClientUtil.getFtpOutPutStream(ftpPath)) {
                exportExcel.write(outputStream);
                if (totalAmount.compareTo(BigDecimal.ZERO) > 0 && orderTypeEnum == UobTransferOrderTypeEnum.TRANSFER_TO_SAXO) {
                    try {
                        String url = UobConstants.getTransferUrl() + "?amount=";
                        log.info("url {} ", url);
                        RestTemplate restTemplate = new RestTemplate();
                        String response = restTemplate.postForObject(url, null, String.class);
                        log.info("resposne {} ", response);
                    } catch (Exception e) {

                    }
                }

                if (CollectionUtils.isNotEmpty(idList)) {
                    for (Long id : idList) {
                        uobTransferExecutionOrderMapper.updateStatus(id, UobExecutionOrderStatusEnum.WAIT_CONFIRM);
                    }
                }
            } catch (IOException e) {
                ErrorLogAndMailUtil.logErrorForTrade(log, e);
            } finally {
                exportExcel.dispose();
            }
        }
    }

    @Override
    public void executeExchangeOrderClient() {
        this.executeExchangeOrder(ExchangeOrderTypeEnum.WITHDRAW);
    }

    @Override
    public void executeExchangeOrderSaxo() {
        this.executeExchangeOrder(ExchangeOrderTypeEnum.RECHARGE);
    }

    private void executeExchangeOrder(ExchangeOrderTypeEnum orderType) {
        List<UobExchangeOrderPO> list = uobExchangeOrderMapper.getOrderList(orderType, Lists.newArrayList(UobOrderStatusEnum.WAIT_EXECUTE));
        List<ExchangeExecutionOrderEM> exportList = Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> idList = Lists.newArrayList();

            for (UobExchangeOrderPO executionOrder : list) {
                ExchangeExecutionOrderEM em = new ExchangeExecutionOrderEM();
                em.setExecutionOrderId(String.valueOf(executionOrder.getId()));
                em.setSpendingAmount(executionOrder.getApplyAmount().setScale(2, RoundingMode.DOWN).toString());
                em.setOrderTime(DateUtils.formatDate(executionOrder.getApplyTime(), DateUtils.DATE_TIME_FORMAT));

                if (executionOrder.getExchangeType() == ExchangeTypeEnum.SGD_USD) {
                    em.setFromCurrency(CurrencyEnum.SGD.getCode());
                    em.setToCurrency(CurrencyEnum.MYR.getCode());
                }

                if (executionOrder.getExchangeType() == ExchangeTypeEnum.USD_SGD) {
                    em.setFromCurrency(CurrencyEnum.MYR.getCode());
                    em.setToCurrency(CurrencyEnum.SGD.getCode());
                }
                exportList.add(em);
                idList.add(executionOrder.getId());
            }

            ExportExcel exportExcel = new ExportExcel(null, ExchangeExecutionOrderEM.class);
            exportExcel.setDataList(exportList);

            String fileName;
            String ftpPath;
            if (orderType == ExchangeOrderTypeEnum.RECHARGE) {
                fileName = DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT2) + "_exchangeOrder_saxo.xlsx";
                ftpPath = UobConstants.getExchangeOrderPath() + "/saxo/" + fileName;
            } else {
                fileName = DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT2) + "_exchangeOrder_client.xlsx";
                ftpPath = UobConstants.getExchangeOrderPath() + "/client/" + fileName;
            }

            try (OutputStream outputStream = FTPClientUtil.getFtpOutPutStream(ftpPath)) {
                exportExcel.write(outputStream);
                if (CollectionUtils.isNotEmpty(idList)) {
                    for (Long id : idList) {
                        uobExchangeOrderMapper.updateStatus(id, UobOrderStatusEnum.WAIT_CONFIRM);
                    }
                }
            } catch (IOException e) {
                ErrorLogAndMailUtil.logErrorForTrade(log, e);
            } finally {
                exportExcel.dispose();
            }
        }
    }

    @Override
    public void confirmExecutionOrderSaxo() {
        String nowStr = DateFormatUtils.format(DateUtils.now(), DateUtils.DATE_FORMAT);
        List<SaxoAccountFundingEventPO> list = saxoAccountFundingEventMapper.getUnConfirm(nowStr);
        if (CollectionUtils.isNotEmpty(list)) {
            List<UobTransferExecutionOrderPO> executionOrderList
                    = uobTransferExecutionOrderMapper.getOrderList(UobTransferOrderTypeEnum.TRANSFER_TO_SAXO, Lists.newArrayList(UobExecutionOrderStatusEnum.WAIT_CONFIRM));

            for (SaxoAccountFundingEventPO ens : list) {
                BigDecimal confirmAmount = ens.getAmount();
                Iterator<UobTransferExecutionOrderPO> executionOrderListIt = executionOrderList.listIterator();
                while (executionOrderListIt.hasNext()) {
                    UobTransferExecutionOrderPO executionOrder = executionOrderListIt.next();
                    if (confirmAmount.compareTo(executionOrder.getAmount()) == 0) {
                        this.confirmExecutionOrder(executionOrder, ens);
                        executionOrderListIt.remove();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void confirmTransferOrderClient() {
        String fileName = DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT2) + "_paymentConfirm_client.xlsx";
        String filePath = UobConstants.getPaymentConfirmPath() + "/client/" + fileName;

        InputStream inputStream = FTPClientUtil.getFtpInputStream(filePath);
        if (inputStream == null) {
            return;
        }

        try {
            ImportExcel importExcel = new ImportExcel(fileName, inputStream, 0, 0);
            List<PaymentConfirmEM> list = importExcel.getDataList(PaymentConfirmEM.class);

            for (PaymentConfirmEM em : list) {
                if (!StringUtils.isEmpty(em.getExecutionOrderId())) {
                    try {
                        Long orderId = Long.parseLong(em.getExecutionOrderId());
                        UobTransferExecutionOrderPO executionOrder = uobTransferExecutionOrderMapper.getById(orderId);
                        if (executionOrder != null
                                && executionOrder.getOrderStatus() == UobExecutionOrderStatusEnum.WAIT_CONFIRM
                                && executionOrder.getOrderType() == UobTransferOrderTypeEnum.TRANSFER_TO_BANK) {
                            this.confirmExecutionOrder(executionOrder, null);
                        }
                    } catch (Exception e) {
                        ErrorLogAndMailUtil.logErrorForTrade(log, e);
                    }
                }
            }
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
    }

    @Transactional
    public void confirmExecutionOrder(UobTransferExecutionOrderPO executionOrder, SaxoAccountFundingEventPO ens) {
        BigDecimal cost = executionOrder.getOrderType() == UobTransferOrderTypeEnum.TRANSFER_TO_SAXO
                ? new BigDecimal(0.5) : new BigDecimal(0.2);
        Date confirmTime = ens == null ? DateUtils.now() : ens.getActivityTime();

        uobTransferExecutionOrderMapper.confirm(executionOrder.getId(), UobExecutionOrderStatusEnum.WAIT_REVERSE, cost, confirmTime);
        if (ens != null) {
            saxoAccountFundingEventMapper.confirm(ens.getId());
        }
    }

    @Override
    public void confirmExchangeOrderClient() {
        this.confirmExchangeOrder(ExchangeOrderTypeEnum.WITHDRAW);
    }

    @Override
    public void confirmExchangeOrderSaxo() {
        this.confirmExchangeOrder(ExchangeOrderTypeEnum.RECHARGE);
    }

    private void confirmExchangeOrder(ExchangeOrderTypeEnum orderType) {
        String fileName;
        String filePath;

        if (orderType == ExchangeOrderTypeEnum.RECHARGE) {
            fileName = DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT2) + "_exchangeConfirm_saxo.xlsx";
            filePath = UobConstants.getExchangeConfirmPath() + "/saxo/" + fileName;
        } else {
            fileName = DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT2) + "_exchangeConfirm_client.xlsx";
            filePath = UobConstants.getExchangeConfirmPath() + "/client/" + fileName;
        }

        InputStream inputStream = FTPClientUtil.getFtpInputStream(filePath);
        if (inputStream == null) {
            return;
        }

        try {
            ImportExcel importExcel = new ImportExcel(fileName, inputStream, 0, 0);
            List<ExchangeConfirmEM> list = importExcel.getDataList(ExchangeConfirmEM.class);

            for (ExchangeConfirmEM em : list) {
                if (!StringUtils.isEmpty(em.getExecutionOrderId())) {
                    try {
                        Long orderId = Long.parseLong(em.getExecutionOrderId());

                        UobExchangeOrderPO exchangeOrder = uobExchangeOrderMapper.getById(orderId);
                        if (exchangeOrder != null
                                && exchangeOrder.getOrderStatus() == UobOrderStatusEnum.WAIT_CONFIRM
                                && exchangeOrder.getOrderType() == orderType) {
                            BigDecimal confirmAmount = new BigDecimal(em.getConfirmAmount());
                            uobExchangeOrderMapper.confirm(orderId, UobOrderStatusEnum.WAIT_NOTIFY, confirmAmount, BigDecimal.ZERO, DateUtils.now());
                        }
                    } catch (Exception e) {
                        ErrorLogAndMailUtil.logErrorForTrade(log, e);
                    }
                }
            }
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
    }

    public void confirmTransferBusinessOrderClient() {
        this.confirmTransferBusinessOrder(UobTransferOrderTypeEnum.TRANSFER_TO_BANK);
    }

    public void confirmTransferBusinessOrderSaxo() {
        this.confirmTransferBusinessOrder(UobTransferOrderTypeEnum.TRANSFER_TO_SAXO);
    }

    private void confirmTransferBusinessOrder(UobTransferOrderTypeEnum orderType) {
        List<UobTransferExecutionOrderPO> executionOrderList = uobTransferExecutionOrderMapper.getOrderList(orderType, Lists.newArrayList(UobExecutionOrderStatusEnum.WAIT_REVERSE));

        List<UobTransferExecutionOrderPO> mergeList = Lists.newArrayList();
        List<UobTransferExecutionOrderPO> splitList = Lists.newArrayList();
        for (UobTransferExecutionOrderPO executionOrder : executionOrderList) {
            if (executionOrder.getRelationType() == UobOrderRelationTypeEnum.MERGE) {
                mergeList.add(executionOrder);
            } else {
                splitList.add(executionOrder);
            }
        }

        this.reverseMergeExecutionOrder(mergeList);
        this.reverseSplitExecutionOrder(splitList);
    }

    @Transactional
    public void reverseMergeExecutionOrder(List<UobTransferExecutionOrderPO> mergeList) {
        for (UobTransferExecutionOrderPO executionOrder : mergeList) {
            List<Long> businessOrderIdList
                    = uobTransferOrderRelationMapper.getBusinessOrderIdByExecutionOrderId(Lists.newArrayList(executionOrder.getId()));

            List<UobTransferOrderPO> businessOrderList = uobTransferOrderMapper.getOrderListById(businessOrderIdList);
            for (UobTransferOrderPO businessOrder : businessOrderList) {
                uobTransferOrderMapper.updateStatus(businessOrder.getId(), UobOrderStatusEnum.WAIT_NOTIFY);
            }
            uobTransferExecutionOrderMapper.updateStatus(executionOrder.getId(), UobExecutionOrderStatusEnum.FINISH);
        }
    }

    @Transactional
    public void reverseSplitExecutionOrder(List<UobTransferExecutionOrderPO> splitList) {
        List<Long> businessIdList = Lists.newArrayList();

        for (UobTransferExecutionOrderPO executionOrder : splitList) {
            List<Long> businessOrderIdList
                    = uobTransferOrderRelationMapper.getBusinessOrderIdByExecutionOrderId(Lists.newArrayList(executionOrder.getId()));
            businessIdList.addAll(businessOrderIdList);
        }

        if (CollectionUtils.isNotEmpty(businessIdList)) {
            List<UobTransferOrderPO> businessOrderList = uobTransferOrderMapper.getOrderListById(businessIdList);
            for (UobTransferOrderPO businessOrder : businessOrderList) {
                List<UobTransferOrderRelationPO> relationList = uobTransferOrderRelationMapper.getByBusinessOrderId(businessOrder.getId());
                List<Long> executionOrderIdList = Lists.newArrayList();
                for (UobTransferOrderRelationPO relation : relationList) {
                    executionOrderIdList.add(relation.getExecutionOrderId());
                }

                List<UobTransferExecutionOrderPO> executionOrderList = uobTransferExecutionOrderMapper.getByIdList(executionOrderIdList);

                boolean allSuccess = true;
                for (UobTransferExecutionOrderPO child : executionOrderList) {
                    if (child.getOrderStatus() != UobExecutionOrderStatusEnum.WAIT_REVERSE) {
                        allSuccess = false;
                        break;
                    }
                }

                if (allSuccess) {
                    uobTransferOrderMapper.confirm(businessOrder.getId(), UobOrderStatusEnum.WAIT_NOTIFY, BigDecimal.ZERO, DateUtils.now());
                    for (UobTransferExecutionOrderPO child : executionOrderList) {
                        uobTransferExecutionOrderMapper.updateStatus(child.getId(), UobExecutionOrderStatusEnum.FINISH);
                    }
                }
            }
        }
    }

    public void notifyTransferBusinessOrderClient() {
        this.notifyTransferBusinessOrder(UobTransferOrderTypeEnum.TRANSFER_TO_BANK);
    }

    public void notifyTransferBusinessOrderSaxo() {
        this.notifyTransferBusinessOrder(UobTransferOrderTypeEnum.TRANSFER_TO_SAXO);
    }

    private void notifyTransferBusinessOrder(UobTransferOrderTypeEnum orderType) {
        List<UobTransferOrderPO> transferOrderList
                = uobTransferOrderMapper.getOrderList(Lists.newArrayList(orderType), Lists.newArrayList(UobOrderStatusEnum.WAIT_NOTIFY));

        for (UobTransferOrderPO uobTransferOrderPO : transferOrderList) {
            String key = "";
            try {
                UobTransferToSaxoCallbackDTO callbackDTO = new UobTransferToSaxoCallbackDTO();
                callbackDTO.setOrderNo(uobTransferOrderPO.getOutBusinessId());
                callbackDTO.setTransferStatus(TransferStatusEnum.SUCCESS);
                if (orderType == UobTransferOrderTypeEnum.TRANSFER_TO_SAXO) {
                    try {
                        key = "transferToUSDFromSGD:" + callbackDTO.getOrderNo();
                        redissonHelper.set(key, "0");
                        log.info("key {} ", key);
                    } catch (Exception e) {
                        ErrorLogAndMailUtil.logErrorForTrade(log, "Fail to set cache");
                    }

                    String orderId = "" + callbackDTO.getOrderNo();
                    RpcMessage rpcMessage = rechargeServiceRemoteService.rechargeUobTransferToSaxoCallback(orderId, Lists.newArrayList(callbackDTO));
                    if (rpcMessage.isSuccess()) {
                        uobTransferOrderMapper.updateStatus(uobTransferOrderPO.getId(), UobOrderStatusEnum.FINISH);
                    }
                } else {
                }
            } catch (Exception e) {
                List<UobTransferOrderPO> failOrder = uobTransferOrderMapper.getOrderList(Lists.newArrayList(orderType), Lists.newArrayList(UobOrderStatusEnum.FAILED));
                if (!failOrder.isEmpty()) {
                    ErrorLogAndMailUtil.logErrorForTrade(log, e);
                }

                if (!key.isEmpty() && redissonHelper.exists(key)) {
                    String value = redissonHelper.get(key);
                    if (value.equalsIgnoreCase("1")) {
                        uobTransferOrderMapper.updateStatus(uobTransferOrderPO.getId(), UobOrderStatusEnum.FINISH);
                    }
                } else {
                    uobTransferOrderMapper.updateStatus(uobTransferOrderPO.getId(), UobOrderStatusEnum.FAILED);
                    ErrorLogAndMailUtil.logErrorForTrade(log, e);
                }

                try {
                    redissonHelper.del(key);
                } catch (Exception ex) {
                    log.info("delete cache {} ", key);
                }

            }
        }
    }

    @Override
    public void notifyExchangeOrderClient() {
        this.notifyExchangeBusinessOrder(ExchangeOrderTypeEnum.WITHDRAW);
    }

    @Override
    public void notifyExchangeOrderSaxo() {
        this.notifyExchangeBusinessOrder(ExchangeOrderTypeEnum.RECHARGE);
    }

    private void notifyExchangeBusinessOrder(ExchangeOrderTypeEnum orderType) {
        List<UobExchangeOrderPO> exchangeOrderList = uobExchangeOrderMapper.getOrderList(orderType, Lists.newArrayList(UobOrderStatusEnum.WAIT_NOTIFY));

        for (UobExchangeOrderPO exchangeOrder : exchangeOrderList) {
            try {
                UobExchangeCallbackDTO callbackDTO = new UobExchangeCallbackDTO();
                callbackDTO.setOrderNo(exchangeOrder.getOutBusinessId());
                callbackDTO.setConfirmMoney(exchangeOrder.getConfirmAmount());
                RpcMessage rpcMessage = exchangeRemoteService.uobExchangeCallBack(callbackDTO);
                if (rpcMessage.isSuccess()) {
                    uobExchangeOrderMapper.updateStatus(exchangeOrder.getId(), UobOrderStatusEnum.FINISH);
                }
            } catch (Exception e) {
                ErrorLogAndMailUtil.logErrorForTrade(log, e);
            }
        }
    }

    @Override
    @Transactional
    public void saveRechargeLog() {
        String fileName = DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT2) + "_rechargeLog.xlsx";
        String filePath = UobConstants.getRechargeLogPath() + fileName;

        InputStream inputStream = FTPClientUtil.getFtpInputStream(filePath);
        if (inputStream == null) {
            return;
        }

        try {
            ImportExcel importExcel = new ImportExcel(fileName, inputStream, 0, 0);
            List<RechargeLogConfirmEM> list = importExcel.getDataList(RechargeLogConfirmEM.class);

            List<UobRechargeLogPO> uobRechargeLogPOList = Lists.newArrayList();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy h:m:s aa", Locale.ENGLISH);

            for (RechargeLogConfirmEM em : list) {
                if (!StringUtils.isEmpty(em.getBankOrderNo())) {
                    UobRechargeLogPO rechargeLog = new UobRechargeLogPO();
                    rechargeLog.setBankOrderNo(em.getBankOrderNo());
                    rechargeLog.setClientName(em.getClientName());
                    rechargeLog.setVirtualAccountNo(em.getVirtualAccountNo());
                    rechargeLog.setCurrency(CurrencyEnum.forCode(em.getCurrency()));
                    rechargeLog.setReferenceCode(em.getReferenceCode().trim());
                    rechargeLog.setCashAmount(new BigDecimal(em.getCashAmount()));
                    rechargeLog.setTradeTime(dateFormat.parse(em.getTradeTime()));
                    rechargeLog.setRechargeStatus(UobRechargeStatusEnum.PROCESSING);

                    if (em.getTradeTime() == null) {
                        throw new BusinessException("saveRechargeLog getTradeTime is null");
                    }

                    uobRechargeLogPOList.add(rechargeLog);
                }
            }

            for (UobRechargeLogPO rechargeLog : uobRechargeLogPOList) {
                try {
                    uobRechargeLogMapper.save(rechargeLog);
                } catch (Exception e) {
                    ErrorLogAndMailUtil.logErrorForTrade(log, e);
                }
            }
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
    }

    @Override
    @Transactional
    public RpcMessage<String> insertRechargeLog(List<UobRechargeLogDTO> uobRechargeLogDTO) {

        try {

            List<UobRechargeLogPO> uobRechargeLogPOList = Lists.newArrayList();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy h:m:s aa", Locale.ENGLISH);

            String error = "";
            boolean counter = false;
            StringBuffer stringBuffer = new StringBuffer();
            int error_counter[] = new int[7];
            String error_string[] = new String[7];
            error_string[0] = "saveRechargeLog getBankOrderNo is null";
            error_string[1] = "saveRechargeLog getClientName is null";
            error_string[2] = "saveRechargeLog getVirtualAccountNo is null";
            error_string[3] = "saveRechargeLog getCurrency is null";
            error_string[4] = "saveRechargeLog getReferenceCode is null";
            error_string[5] = "saveRechargeLog getTradeTime is null";
            error_string[6] = "saveRechargeLog getCashAmount is null";

            String template[] = new String[7];
            template[0] = "Bank Order No: ";
            template[1] = "Client Name: ";
            template[2] = "Virtual Account No: ";
            template[3] = "Currrency: ";
            template[4] = "Reference Code: ";
            template[5] = "Cash Amount: ";
            template[6] = "Trade Time: ";

            String back_template[] = new String[7];

            for (UobRechargeLogDTO em : uobRechargeLogDTO) {

                if (!StringUtils.isEmpty(em.getBankOrderNo())) {

                    for (int i = 0; i < 7; i++) {

                        error_counter[i] = 0;

                    }
                    counter = false;

                    back_template[0] = em.getBankOrderNo();
                    back_template[1] = em.getClientName();
                    back_template[2] = em.getVirtualAccountNo();
                    back_template[3] = em.getCurrency();
                    back_template[4] = em.getReferenceCode();
                    back_template[5] = em.getCashAmount();
                    back_template[6] = em.getTradeTime();

                    UobRechargeLogPO rechargeLog = new UobRechargeLogPO();
                    rechargeLog.setBankOrderNo(em.getBankOrderNo());
                    rechargeLog.setClientName(em.getClientName());
                    rechargeLog.setVirtualAccountNo(em.getVirtualAccountNo());
                    rechargeLog.setCurrency(CurrencyEnum.forCode(em.getCurrency()));
                    rechargeLog.setReferenceCode(em.getReferenceCode().trim());
                    rechargeLog.setCashAmount(new BigDecimal(em.getCashAmount()));

//                    rechargeLog.setTradeTime(dateFormat.parse(dateProcessed));
                    rechargeLog.setRechargeStatus(UobRechargeStatusEnum.PROCESSING);

                    if (em.getBankOrderNo() == null) {

                        error_counter[0] = 1;
                        counter = true;

                    }

                    if (em.getVirtualAccountNo() == null) {

                        error_counter[2] = 1;
                        counter = true;

                    }

                    if (em.getTradeTime() == null) {

                        error_counter[6] = 1;
                        counter = true;

                    } else {

                        try {

                            rechargeLog.setTradeTime(dateFormat.parse(em.getTradeTime()));

                            //                        uobRechargeLogPOList.add(rechargeLog);
                        } catch (Exception e) {

                            error_counter[6] = 2;
                            counter = true;

                        }

                    }

                    if (!counter) {

                        uobRechargeLogPOList.add(rechargeLog);
                    }

                    if (counter) {
                        for (int i = 0; i < 7; i++) {

                            if (error_counter[i] == 1) {

                                error = error + template[i] + error_string[i] + "\r\n";

                            } else {

                                error = error + template[i] + back_template[i] + "\r\n";

                            }

                        }

                        error = error + "\r\n\r\n";
                        return RpcMessage.error("Data Got Error.");

                    }

//                    uobRechargeLogPOList.add(rechargeLog);
                }
            }

            for (UobRechargeLogPO rechargeLog : uobRechargeLogPOList) {
                try {
                    uobRechargeLogMapper.save(rechargeLog);
                    return RpcMessage.success("Insert Success");
                } catch (Exception e) {
                    ErrorLogAndMailUtil.logErrorForTrade(log, e);
                    return RpcMessage.error("Fail");
                }
            }
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
            return RpcMessage.error("Fail");
        }

        return RpcMessage.error("Fail");
    }

    // ADDED BY WOOI TATT KHOR 16/AUG/2019
    // READ FILE FROM UOB VA1 = FILE TYPE: VA1_BILLERCODE_DATE.TXT (Update DAAS Deposit)
    @Override
    @Transactional
    public void saveRechargeLogFromUOB() {

        try {
            String fileName = PropertiesUtil.getString("uob.sftp.deposit") + "_"
                    + PropertiesUtil.getString("pivot.account.sgd.number").replace("-", "") + "_"
                    + DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT6) + ".pgp";
            String filePath = "C:\\write\\" + "VA1_3523095739_200417.txt";

            String fileFrmPath = PropertiesUtil.getString("uob.path.out") + fileName;
            String fileToPath = PropertiesUtil.getString("uob.path.loc") + fileName;
            //SFTP to UOB copy file to our server
            SftpClient sftpClient = SftpClient.connectUOB();
            sftpClient.getUOBFile(fileFrmPath, fileToPath);

            //if(!isFileExist(fileToPath)){}
            //Decrpty file
            String fileNameDecrpty = PropertiesUtil.getString("uob.path.loc") + fileName + ".txt";
            String command = "gpg -o " + fileNameDecrpty + " " + fileToPath;
            encrpytAndDecrpytFile(command);

            //FTP file to pivotbase server.
            String filePathInBase = UobConstants.getRechargeLogPath() + fileName + ".txt";
            //String testPath1 = "C:\\write\\UGBO050201O.pgp.txt";
            //String testPath2 ="D:\\ftpreceive\\UGBO050201O.pgp.txt";
            //InputStream inputStream = FTPClientUtil.getFtpInputStream(testPath1);
            InputStream inputStream = new FileInputStream(fileNameDecrpty);
            OutputStream outputStream = FTPClientUtil.getFtpOutPutStream(filePathInBase);
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();

            inputStream = new FileInputStream(filePathInBase);

            if (inputStream == null) {
                return;
            }

            //FTP to readfile content
            /*InputStream inputStream = FTPClientUtil.getFtpInputStream(filePath);
             if (inputStream == null) {
                return;
            }*/
            List<String> lines = FTPClientUtil.readFileContent(filePath);

            List<RechargeLogConfirmEM> list = Lists.newArrayList();
            List<UobRechargeLogPO> uobRechargeLogPOList = Lists.newArrayList();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.ENGLISH);

            for (int i = 0; lines.size() - 1 > i; i++) {
                RechargeLogConfirmEM rechargelogEM = new RechargeLogConfirmEM();
                String tradeDate = lines.get(i).substring(1, 9);
                String tradeTime = lines.get(i).substring(9, 15);
                String vAccount = lines.get(i).substring(15, 49);
                String bankOrderNo = tradeDate + tradeTime + vAccount;
                rechargelogEM.setBankOrderNo(bankOrderNo);
                rechargelogEM.setCashAmount(lines.get(i).substring(100, 117));
                rechargelogEM.setVirtualAccountNo(vAccount);
                rechargelogEM.setClientName(lines.get(i).substring(134, 274));
                rechargelogEM.setCurrency(lines.get(i).substring(97, 100));
                rechargelogEM.setReferenceCode(lines.get(i).substring(464, 504));

                String formatTradeDate = tradeDate.substring(0, 4) + "/" + tradeDate.substring(4, 6) + "/" + tradeDate.substring(6, 8);
                String formatTradeTime = tradeTime.substring(0, 2) + ":" + tradeTime.substring(2, 4) + ":" + tradeDate.substring(4, 6);
                String combineDateTime = formatTradeDate + " " + formatTradeTime;
                rechargelogEM.setTradeTime(combineDateTime);
                rechargelogEM.setCreditDebit(lines.get(i).substring(95, 96));
                rechargelogEM.setTransactionInd(lines.get(i).substring(96, 97));
                list.add(rechargelogEM);

            }

            for (RechargeLogConfirmEM em : list) {
                if (!StringUtils.isEmpty(em.getBankOrderNo()) && em.getCreditDebit().equalsIgnoreCase("C") && em.getTransactionInd().equalsIgnoreCase("T")) {
                    UobRechargeLogPO rechargeLog = new UobRechargeLogPO();
                    rechargeLog.setBankOrderNo(em.getBankOrderNo().trim());
                    rechargeLog.setClientName(em.getClientName().trim());
                    rechargeLog.setVirtualAccountNo(em.getVirtualAccountNo().trim());
                    rechargeLog.setCurrency(CurrencyEnum.forCode(em.getCurrency().trim()));
                    rechargeLog.setReferenceCode(em.getReferenceCode().trim());
                    rechargeLog.setCashAmount(new BigDecimal(em.getCashAmount()));
                    rechargeLog.setTradeTime(dateFormat.parse(em.getTradeTime()));
                    rechargeLog.setRechargeStatus(UobRechargeStatusEnum.PROCESSING);

                    if (em.getTradeTime() == null) {
                        throw new BusinessException("saveRechargeLog getTradeTime is null");
                    }

                    uobRechargeLogPOList.add(rechargeLog);
                }
            }

            for (UobRechargeLogPO rechargeLog : uobRechargeLogPOList) {
                try {
                    uobRechargeLogMapper.save(rechargeLog);
                } catch (Exception e) {
                    ErrorLogAndMailUtil.logErrorForTrade(log, e);
                }
            }

        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
    }

    // GET FROM UOB (UBGO)= File of client payment confirmation
    @Override
    public void confirmTransferOrderClientDirect() {

        try {
            String fileName = PropertiesUtil.getString("uob.sftp.paymentConfirm.toClient")
                    + DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT5) + "O.pgp";
            String filePath = "C:\\write\\UGBO050201O.pgp.txt";

            /*String fileFrmPath = PropertiesUtil.getString("uob.path.out")+fileName;
            String fileToPath = PropertiesUtil.getString("uob.path.loc")+fileName;
            //SFTP to UOB copy file to our server
            SftpClient sftpClient = SftpClient.connectUOB();
            sftpClient.getUOBFile(fileFrmPath, fileToPath);
            //if(!isFileExist(fileToPath)){}
            //Decrpty file
            String fileNameDecrpty = PropertiesUtil.getString("uob.path.loc")+fileName+".txt";
            String command = "gpg -o "+ fileNameDecrpty +" "+ fileToPath;
            encrpytAndDecrpytFile(command);
            //FTP file to pivotbase server.
            String filePathInBase = UobConstants.getPaymentConfirmPath()+fileName+".txt";
            //String testPath1 = "C:\\write\\UGBO050201O.pgp.txt";
            //String testPath2 ="D:\\ftpreceive\\UGBO050201O.pgp.txt";
            //InputStream inputStream = FTPClientUtil.getFtpInputStream(testPath1);
            InputStream inputStream = new FileInputStream(fileNameDecrpty);
            OutputStream outputStream = FTPClientUtil.getFtpOutPutStream(filePathInBase);
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();
            inputStream = new FileInputStream(filePathInBase);
            if (inputStream == null) {
                return;
            }*/
            InputStream inputStream = FTPClientUtil.getFtpInputStream(filePath);

            if (inputStream == null) {
                return;
            }

            List<String> lines = FTPClientUtil.readFileContent(filePath);
            List<PaymentConfirmEM> list = new ArrayList();

            for (int i = 0; lines.size() - 1 > i; i++) {

                PaymentConfirmEM em = new PaymentConfirmEM();
                em.setConfirmCurrency(lines.get(i).substring(186, 189));
                em.setConfirmAmount(lines.get(i).substring(189, 207));
                em.setExecutionOrderId(lines.get(i).substring(207, 242).trim());
                list.add(em);
            }

            for (PaymentConfirmEM em : list) {
                if (!StringUtils.isEmpty(em.getExecutionOrderId())) {
                    try {
                        Long orderId = Long.parseLong(em.getExecutionOrderId());
                        UobTransferExecutionOrderPO executionOrder = uobTransferExecutionOrderMapper.getById(orderId);
                        if (executionOrder != null
                                && executionOrder.getOrderStatus() == UobExecutionOrderStatusEnum.WAIT_CONFIRM
                                && executionOrder.getOrderType() == UobTransferOrderTypeEnum.TRANSFER_TO_BANK) {
                            this.confirmExecutionOrder(executionOrder, null);
                        }
                    } catch (Exception e) {
                        ErrorLogAndMailUtil.logErrorForTrade(log, e);
                    }
                }
            }
        } catch (Exception e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
    }

    private String appendixSix(String value) {

        HashMap<String, String> appendixValueMap = new HashMap<>();
        appendixValueMap.put("[", "1");
        appendixValueMap.put("]", "2");
        appendixValueMap.put("{", "3");
        appendixValueMap.put("}", "4");
        appendixValueMap.put("|", "5");
        appendixValueMap.put("~", "6");
        appendixValueMap.put("*", "7");
        appendixValueMap.put("!", "8");
        appendixValueMap.put("&", "9");
        appendixValueMap.put("`", "10");
        appendixValueMap.put("@", "11");
        appendixValueMap.put("#", "12");
        appendixValueMap.put("$", "13");
        appendixValueMap.put("%", "14");
        appendixValueMap.put("^", "15");
        appendixValueMap.put("_", "16");
        appendixValueMap.put("=", "17");
        appendixValueMap.put("<", "18");
        appendixValueMap.put(">", "19");
        appendixValueMap.put("\\", "20");
        appendixValueMap.put("\"", "21");

        String returnValue = "";

        for (int i = 0; i < value.length(); i++) {
            String isSym = appendixValueMap.get(String.valueOf(value.charAt(i)));

            if (isSym == null || isSym.equalsIgnoreCase("")) {
                returnValue = returnValue.concat(String.valueOf(value.charAt(i)));
            } else {
                returnValue = returnValue.concat(isSym);
            }
        }

        return returnValue;

    }

    private List createFileHeader(String[] valueHeader) {

        List lFileHeaderValue = new ArrayList();

        for (int i = 0; i < valueHeader.length; i++) {
            lFileHeaderValue.add(valueHeader[i]);
        }

        return lFileHeaderValue;
    }

    /**
     * Modify by WooiTatt 20190825 Due to export in TXT file. Direct FTP to UOB
     * instead send back to PIVOT
     */
    @Override
    public void executeTransferOrderToBank(UobTransferOrderTypeEnum orderTypeEnum) {
        log.info("####executeTransferOrderToBank Begin######");
        List<UobTransferExecutionOrderPO> executionOrderList = uobTransferExecutionOrderMapper.getOrderList(orderTypeEnum, Lists.newArrayList(UobExecutionOrderStatusEnum.WAIT_EXECUTE));

        String fileName = PropertiesUtil.getString("uob.sftp.payment.toClient") + DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT5) + "01.txt";
        String ftpPath = UobConstants.getPaymentOrderPath() + "client/" + fileName;
        // Set Column Size Header File
        String originateName = appendixSix(PropertiesUtil.getString("pivot.account.name"));

        String[] headerFormat = {"%-1s", "%-10s", "%-1s", "%-10s", "%-1s", "%-12s", "%-11s", "%-3s", "%-34s", "%-140s",
            "%-8s", "%-8s", "%-140s", "%-16s", "%-10s", "%-210s%n"};

        String[] headerValue = {PropertiesUtil.getString("uob.rec.header.value"), fileName.replace(".txt", ""),
            PropertiesUtil.getString("uob.payment.type.payment"), PropertiesUtil.getString("uob.service.type.normal"),
            PropertiesUtil.getString("uob.process.type.bGiro"), PropertiesUtil.getString("uob.pivot.company.id"),
            PropertiesUtil.getString("uob.swift.code"), PropertiesUtil.getString("saxo.account.currency"),
            PropertiesUtil.getString("pivot.account.sgd.number").replace("-", ""),
            originateName, DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT2),
            DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT2), "",
            "Pivot" + DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT2), "", ""};

        List lsHeaderFormat = Lists.newArrayList();
        lsHeaderFormat = createFileHeader(headerFormat);

        // lsHeaderFormat.add("%-1s");
        // lsHeaderFormat.add("%-10s");
        // lsHeaderFormat.add("%-1s");
        // lsHeaderFormat.add("%-10s");
        // lsHeaderFormat.add("%-1s");
        // lsHeaderFormat.add("%-12s");
        // lsHeaderFormat.add("%-11s");
        // lsHeaderFormat.add("%-3s");
        // lsHeaderFormat.add("%-34s");
        // lsHeaderFormat.add("%-140s");
        // lsHeaderFormat.add("%-8s");
        // lsHeaderFormat.add("%-8s");
        // lsHeaderFormat.add("%-140s");
        // lsHeaderFormat.add("%-16s"); // Bulk Customer Reference
        // lsHeaderFormat.add("%-10s");
        // lsHeaderFormat.add("%-210s%n");
        //Header Value
        List lsValueHeader = Lists.newArrayList();
        lsValueHeader = createFileHeader(headerValue);
        // lsValueHeader.add(PropertiesUtil.getString("uob.rec.header.value"));
        // lsValueHeader.add(fileName.replace(".txt", ""));
        // lsValueHeader.add(PropertiesUtil.getString("uob.payment.type.payment"));
        // lsValueHeader.add(PropertiesUtil.getString("uob.service.type.normal"));
        // lsValueHeader.add(PropertiesUtil.getString("uob.process.type.bGiro"));
        // lsValueHeader.add(PropertiesUtil.getString("uob.pivot.company.id"));
        // lsValueHeader.add(PropertiesUtil.getString("uob.swift.code"));
        // lsValueHeader.add(PropertiesUtil.getString("saxo.account.currency"));
        // lsValueHeader.add(PropertiesUtil.getString("pivot.account.sgd.number").replace("-", ""));
        // String originateName = appendixSix(PropertiesUtil.getString("pivot.account.name"));
        // lsValueHeader.add(originateName); // Originating A/C Name
        // lsValueHeader.add(DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT2));
        // lsValueHeader.add(DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT2));
        // lsValueHeader.add("");
        // lsValueHeader.add("Pivot"+DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT2));
        // lsValueHeader.add("");
        // lsValueHeader.add("");

        //Trailer
        String[] arrayHashHeader = {PropertiesUtil.getString("uob.swift.code"),
            PropertiesUtil.getString("pivot.account.sgd.number").replace("-", ""), originateName};

        BigInteger bIntSum1, bIntSum2, bIntSum3 = new BigInteger("0");
        String strIntSum = "0";
        int[] arrayHashHeaderLength = {11, 34, 140};
        String strIntHeaderSum = String.valueOf(hashAlgorithm(arrayHashHeader, arrayHashHeaderLength));

        BigDecimal bTotalAmount = new BigDecimal("0");
        log.info("####Level 2######");

        if (CollectionUtils.isNotEmpty(executionOrderList)) {
            List<Long> idList = Lists.newArrayList();

            List<TransferExecutionOrderEM> exportList = Lists.newArrayList();
            int count = 1;

            for (UobTransferExecutionOrderPO executionOrder : executionOrderList) {
                int paymentTypeTotal = 0;
                BigDecimal bAmount = new BigDecimal(executionOrder.getAmount().setScale(2, RoundingMode.DOWN).toString());
                int compareBigDec = bAmount.compareTo(new BigDecimal("0"));
                int compareBigDecOver = bAmount.compareTo(new BigDecimal("200000"));

                if (compareBigDec > 0 && compareBigDecOver < 1) {
                    bTotalAmount = bTotalAmount.add(bAmount);
                    TransferExecutionOrderEM em = new TransferExecutionOrderEM();
                    em.setExecutionOrderId(appendixSix(String.valueOf(executionOrder.getId())));
                    em.setBankName(executionOrder.getBankName());
                    em.setBankAccountNumber(executionOrder.getBankAccountNumber());
                    String receiveUserName = appendixSix(executionOrder.getBankUserName());
                    em.setBankUserName(receiveUserName);
                    em.setBranchCode(executionOrder.getBranchCode());
                    em.setSwiftCode(executionOrder.getSwiftCode());
                    em.setCurrency(executionOrder.getCurrency().getCode());
                    String conAmtFormat = convertAmountFormat(executionOrder.getAmount().setScale(2, RoundingMode.DOWN).toString());
                    em.setAmount(conAmtFormat);
                    em.setOrderTime(DateUtils.formatDate(executionOrder.getApplyTime(), DateUtils.DATE_TIME_FORMAT));
                    em.setRemark(executionOrder.getRemark());
                    String[] arrayHashBody = {em.getSwiftCode(), em.getCurrency(), conAmtFormat, PropertiesUtil.getString("pivot.account.purpose.code")};
                    int[] arrayHashBodyLength = {11, 3, 18, 4};

                    paymentTypeTotal += 20 * count;
                    String[] arrayHashBodyAcc = {em.getBankAccountNumber(), receiveUserName};
                    int[] arrayHashBodyAccLength = {34, 140};
                    bIntSum3 = hashAlgorithm(arrayHashBodyAcc, arrayHashBodyAccLength);
                    System.out.println("bIntSum3 >>" + bIntSum3.toString());
                    bIntSum3 = bIntSum3.multiply(new BigInteger(String.valueOf(count)));

                    System.out.println("intSum3 >>" + bIntSum3.toString());

                    bIntSum1 = hashAlgorithm(arrayHashBody, arrayHashBodyLength);
                    bIntSum2 = new BigInteger(String.valueOf(paymentTypeTotal));
                    bIntSum1 = bIntSum1.add(bIntSum2).add(bIntSum3);
                    bIntSum2 = new BigInteger(strIntSum);
                    strIntSum = bIntSum1.add(bIntSum2).toString();
                    exportList.add(em);
                    idList.add(executionOrder.getId());

                    count += 1;
                }
                log.info("####Level 2 End ######" + executionOrder);
            }
            bIntSum1 = new BigInteger(String.valueOf(strIntSum));
            bIntSum2 = new BigInteger(String.valueOf(strIntHeaderSum));
            strIntSum = bIntSum1.add(bIntSum2).toString();
            //System.out.println("Total Header + Details Record >>" + strIntSum);
            log.info("####Total Header + Details Record######" + strIntSum);
            // try (OutputStream outputStream = FTPClientUtil.getFtpOutPutStream(ftpPath)) {
            log.info("####Start Create File######");
            /* try{
                   String pathName = "/home/howey";
                   Path path = Paths.get(pathName);
                    if (!Files.exists(path)) {
                        Files.createDirectories(path);
                        log.info("####Create Directory######" );
                    }else{
                        log.info("####Directory Existing######" );
                    }
                  }catch(IOException ex) {
            
                    log.info("####Create File######" +  ex.getMessage());
                  //  ex.printStackTrace();
                }*/

            // Write Header Line
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("//home//howey//" + fileName))) {
                // Write Header Line
                for (int i = 0; i < lsHeaderFormat.size(); i++) {
                    writer.write(String.format(lsHeaderFormat.get(i).toString(), lsValueHeader.get(i).toString()));
                }
                // Write Body Line
                for (TransferExecutionOrderEM em : exportList) {

                    writer.write(String.format("%-1s", PropertiesUtil.getString("uob.rec.body.value")));
                    writer.write(String.format("%-11s", em.getSwiftCode()));
                    writer.write(String.format("%-34s", em.getBankAccountNumber()));
                    writer.write(String.format("%-140s", em.getBankUserName()));
                    writer.write(String.format("%-3s", em.getCurrency()));
                    writer.write(String.format("%-18s", em.getAmount()));
                    writer.write(String.format("%-35s", em.getExecutionOrderId()));
                    writer.write(String.format("%-35s", ""));
                    writer.write(String.format("%-4s", PropertiesUtil.getString("pivot.account.purpose.code")));
                    writer.write(String.format("%-140s", ""));
                    writer.write(String.format("%-140s", ""));
                    writer.write(String.format("%-16s", ""));
                    writer.write(String.format("%-38s%n", ""));
                }
                String totalRecord = convertIntegerFormat(String.valueOf(exportList.size()), 7);
                String hashFormat = convertIntegerFormat(strIntSum, 16);
                // Write Traile Line
                writer.write(String.format("%-1s%-18s%-7s%-16s%-573s", PropertiesUtil.getString("uob.rec.trailer.value"), convertAmountFormat(bTotalAmount.toString()),
                        totalRecord, hashFormat, ""));

                // writer.write(outputStream);
            } catch (IOException ex) {

                log.info("####Error Catch######" + ex.getMessage());
                //  ex.printStackTrace();
            }
            log.info("####Write File End######");

            try {
                String outFile = "//home//howey//" + fileName + ".pgp";
                // String command ="gpg -o "+outFile+" --encrypt --recipient PGP-ROSUSGRFTS00117A01@uob.com --sign -u johnny.ong@pivotfintech.com --passphrase pivot //home//howey//"+fileName;
                String command = "mkdir /home/testfolder";
                log.info("####ProcessBuilder######" + command);
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                //processBuilder.command("bash", "/", command);
                // processBuilder.command(command);

                Process process = processBuilder.start();
                log.info("####Start######" + process.toString());
                process.destroy();

                Process process1 = Runtime.getRuntime().exec("mkdir //home//howey//test");

                try {
                    process1.waitFor();
                } catch (InterruptedException ex) {
                    log.info("####Error process1######" + ex.getMessage());
                }
                if (process1.exitValue() == 0) {
                    log.info("####Folder Created######" + process1.toString());
                } else {
                    log.info("####Failed Folder Created######" + process1.toString());
                }

                process1.destroy();

            } catch (IOException e) {
                //    ErrorLogAndMailUtil.logErrorForTrade(log, e);
            }
            //try{
            // PGPLib pgp = new PGPLib();
            //pgp.signAndEncryptFile("/home/khor/uob/"+fileName,"johnny.ong@pivotfintech.com","pivot","ROSUSGRFTS00117A01@uob.com",fileName+".pgp",true,true);
            //}catch(NoPublicKeyFoundException p){}

            if (CollectionUtils.isNotEmpty(idList)) {
                for (Long id : idList) {
                    uobTransferExecutionOrderMapper.updateStatus(id, UobExecutionOrderStatusEnum.WAIT_CONFIRM);
                }
            }
            //} catch (IOException e) {
            //    ErrorLogAndMailUtil.logErrorForTrade(log, e);
            //} finally {
            // exportExcel.dispose();

            //}
        }
    }

    /**
     * hashAlgorithm (Value convert in ACSII code) Added by WooiTatt 190825
     */
    private BigInteger hashAlgorithm(String[] value, int[] valueLength) {

        BigInteger bInt1, bInt2, bInt3;
        bInt1 = new BigInteger("0");

        for (int i = 0; i < value.length; i++) {
            int seqNo = 1;
            bInt3 = new BigInteger("0");
            //char[] ch = value[i].toCharArray();

            int spaceLength = valueLength[i] - value[i].length();
            String strSpace = "";
            System.out.println("spaceLength Diff >" + spaceLength);

            if (spaceLength > 0) {
                for (int y = 0; y < spaceLength; y++) {
                    strSpace = strSpace + " ";
                }
            }
            String combineString = value[i] + strSpace;
            System.out.println("combineString:" + combineString + "combineString Length:" + combineString.length());
            char[] ch = combineString.toCharArray();

            for (int a = 0; a < ch.length; a++) {
                int conv = (int) ch[a];
                int total = conv * seqNo;
                //bInt2 = new BigInteger(String.valueOf(total));
                //bInt1 = bInt1.add(bInt2);
                bInt2 = new BigInteger(String.valueOf(total));
                bInt3 = bInt3.add(bInt2);
                seqNo += 1;

                System.out.println("ASCII >>" + conv + ", ASCII * Position >>" + bInt2.toString() + ", Total >>" + bInt1.toString());
            }
            bInt1 = bInt1.add(bInt3);
        }
        System.out.println("Final Return Value >>" + bInt1.toString());
        return bInt1;
    }

    /**
     * convertAmountFormat Added by WooiTatt 190825
     */
    private String convertAmountFormat(String amount) {

        BigDecimal bAmt = new BigDecimal(amount);
        BigDecimal bFormula = new BigDecimal("100");
        BigInteger iAmt = bAmt.multiply(bFormula).toBigInteger();

        String toAmt = String.valueOf(iAmt);
        int amtLength = 18;
        int amtDigit = amtLength - toAmt.length();
        String format = "";

        for (int i = 0; i < amtDigit; i++) {
            format += "0";
        }

        return format + toAmt;
    }

    /**
     * convertIntegerFormat Added by WooiTatt 190825
     */
    private String convertIntegerFormat(String value, int length) {

        BigInteger bAmt = new BigInteger(value);
        String toAmt = String.valueOf(bAmt);

        int amtDigit = length - toAmt.length();
        String format = "";
        for (int i = 0; i < amtDigit; i++) {
            format += "0";
        }

        return format + toAmt;
    }

    private void encrpytAndDecrpytFile(String command) {

        try {
            Runtime rt = Runtime.getRuntime();
            Process process = rt.exec(command);

            InputStream stdIn = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(stdIn);
            BufferedReader br = new BufferedReader(isr);

            String line = null;
            log.info("<OUTPUT>");

            while ((line = br.readLine()) != null) {
                log.info(line);
            }

            log.info("</OUTPUT>");

            process.destroy();
        } catch (IOException e) {
        }
    }

    private boolean isFileExist(String fileDest) {

        File isFileExist = new File(fileDest);

        return isFileExist.exists();
    }

    //Added By WooiTatt
    //Backup UOB file has been decrypted.
    /*public static boolean storeFile(String local, String remote)  {
        
        boolean isFileSuccessTransfer = false;
        try{
            File localFile = new File(local);
            InputStream inputStream = new FileInputStream(localFile);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            isFileSuccessTransfer =  ftpClient.storeFile(remote, inputStream);
            inputStream.close();
            ftpClient.logout();
        }catch(IOException e){}
        
        return isFileSuccessTransfer;
    }*/
}
