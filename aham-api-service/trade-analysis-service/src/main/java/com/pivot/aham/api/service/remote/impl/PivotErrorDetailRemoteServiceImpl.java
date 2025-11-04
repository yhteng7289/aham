package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.PivotErrorHandlingDetailDTO;
import com.pivot.aham.api.server.dto.req.ErrorHandlingAccountReqDTO;
import com.pivot.aham.api.server.dto.res.ErrorHandlingAccountResDTO;
import com.pivot.aham.api.server.remoteservice.PivotErrorDetailRemoteService;
import com.pivot.aham.api.service.mapper.model.PivotErrorHandlingDetailPO;
import com.pivot.aham.api.service.mapper.model.PivotErrorHandlingDetailVo;
import com.pivot.aham.api.service.service.PivotErrorHandlingDetailService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.support.email.Email;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.EmailUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;
import com.pivot.aham.common.enums.analysis.ErrorFeeTypeEnum;
import com.pivot.aham.common.enums.analysis.OperateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service(interfaceClass = PivotErrorDetailRemoteService.class)
@Slf4j
public class PivotErrorDetailRemoteServiceImpl implements PivotErrorDetailRemoteService {

    private final static String CONTACT_EMAIL = PropertiesUtil.getString("pivot.error.handling.summary.email");

    private final static ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(2, 20, 1, TimeUnit.SECONDS,
            new ArrayBlockingQueue(20), new ThreadPoolExecutor.DiscardOldestPolicy());

    @Resource
    private PivotErrorHandlingDetailService pivotErrorHandlingDetailService;

    @Override
    public RpcMessage saveErrorHandlingDetail(List<PivotErrorHandlingDetailDTO> pivotErrorHandlingDetailDTOs) {
        List<PivotErrorHandlingDetailPO> insertList = BeanMapperUtils.mapList(pivotErrorHandlingDetailDTOs, PivotErrorHandlingDetailPO.class);
        if (CollectionUtils.isNotEmpty(insertList)) {
            pivotErrorHandlingDetailService.batchInsert(insertList);
        }
        return RpcMessage.success();
    }

    @Override
    public RpcMessage summaryErrorHandlingDetail(Date now) {
        List<PivotErrorHandlingDetailPO> summaryList = Lists.newArrayList();

        PivotErrorHandlingDetailPO queryExchangePO = new PivotErrorHandlingDetailPO();
        queryExchangePO.setType(ErrorFeeTypeEnum.EXCHANGE_FEE);
        queryExchangePO.setStartDate(DateUtils.dayStart(now));
        queryExchangePO.setEndDate(DateUtils.dayEnd(now));
        List<PivotErrorHandlingDetailPO> exchangeFeeList = pivotErrorHandlingDetailService.queryByTypeAndDate(queryExchangePO);
        summaryList.addAll(exchangeFeeList);

        PivotErrorHandlingDetailPO queryExternalChargesPO = new PivotErrorHandlingDetailPO();
        queryExternalChargesPO.setType(ErrorFeeTypeEnum.EXTERNAL_CHARGES);
        queryExternalChargesPO.setStartDate(DateUtils.dayStart(now));
        queryExternalChargesPO.setEndDate(DateUtils.dayEnd(now));
        List<PivotErrorHandlingDetailPO> externalChargesList = pivotErrorHandlingDetailService.queryByTypeAndDate(queryExchangePO);
        summaryList.addAll(externalChargesList);

        PivotErrorHandlingDetailPO queryPerformanceFeeChargesPO = new PivotErrorHandlingDetailPO();
        queryPerformanceFeeChargesPO.setType(ErrorFeeTypeEnum.PERFORMANCE_FEE);
        queryPerformanceFeeChargesPO.setStartDate(DateUtils.dayStart(now));
        queryPerformanceFeeChargesPO.setEndDate(DateUtils.dayEnd(now));
        List<PivotErrorHandlingDetailPO> performanceFeeChargesPOList = pivotErrorHandlingDetailService.queryByTypeAndDate(queryExchangePO);
        summaryList.addAll(performanceFeeChargesPOList);

        PivotErrorHandlingDetailPO queryAccountPO = new PivotErrorHandlingDetailPO();
        queryAccountPO.setType(ErrorFeeTypeEnum.ACCOUNT);
        queryAccountPO.setStartDate(DateUtils.dayStart(now));
        queryAccountPO.setEndDate(DateUtils.dayEnd(now));
        List<PivotErrorHandlingDetailPO> accountList = pivotErrorHandlingDetailService.queryByTypeAndDate(queryAccountPO);
        summaryList.addAll(accountList);

        PivotErrorHandlingDetailPO stampDutyPO = new PivotErrorHandlingDetailPO();
        stampDutyPO.setType(ErrorFeeTypeEnum.STAMP_DUTY);
        stampDutyPO.setStartDate(DateUtils.dayStart(now));
        stampDutyPO.setEndDate(DateUtils.dayEnd(now));
        List<PivotErrorHandlingDetailPO> stampDutyList = pivotErrorHandlingDetailService.queryByTypeAndDate(stampDutyPO);
        summaryList.addAll(stampDutyList);

        PivotErrorHandlingDetailPO totalRechargePO = new PivotErrorHandlingDetailPO();
        totalRechargePO.setOperateType(OperateTypeEnum.RECHARGE);
        BigDecimal totalRechargeMoney = pivotErrorHandlingDetailService.getTotalMoneyByDateAndType(totalRechargePO);
        if (totalRechargeMoney == null) {
            totalRechargeMoney = BigDecimal.ZERO;
        }
        PivotErrorHandlingDetailPO withDraw = new PivotErrorHandlingDetailPO();
        withDraw.setOperateType(OperateTypeEnum.WITHDRAW);
        BigDecimal totalRedeemMoney = pivotErrorHandlingDetailService.getTotalMoneyByDateAndType(withDraw);
        if (totalRedeemMoney == null) {
            totalRedeemMoney = BigDecimal.ZERO;
        }
        summaryList.addAll(summaryList);
        if (CollectionUtils.isNotEmpty(summaryList)) {
            List<PivotErrorHandlingDetailVo> pivotErrorHandlingDetailVoList = buildVo(stampDutyList);

            String currentDate = DateUtils.formatDate(now, "yyyyMMdd");

            String topic = "errorHandlingDetail" + currentDate + "[" + PropertiesUtil.getString("email.env.name") + "]";

            String totalBalance = String.valueOf(totalRechargeMoney.subtract(totalRedeemMoney));

            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("totalBalance", totalBalance);
            templateVariables.put("pivotErrorHandlingDetailVoList", pivotErrorHandlingDetailVoList);

            Email email = new Email().setTemplateName("ErrorHandling")
                    .setTemplateVariables(templateVariables).setSendTo(CONTACT_EMAIL).setTopic(topic);
            EXECUTOR_SERVICE.submit(() -> EmailUtil.sendEmail(email));
        } else {
            log.info("Today: {} errorHandling not found fee deduction !!! Email not sent, record logging", DateUtils.formatDate(now, "yyyyMMdd"));
        }
        return RpcMessage.success();
    }

    private List<PivotErrorHandlingDetailVo> buildVo(List<PivotErrorHandlingDetailPO> summaryList) {
        List<PivotErrorHandlingDetailVo> voList = Lists.newArrayList();
        for (PivotErrorHandlingDetailPO po : summaryList) {
            PivotErrorHandlingDetailVo vo = new PivotErrorHandlingDetailVo();
            vo.setMoney(po.getMoney());
            vo.setOperateDate(DateUtils.formatDate(po.getOperateDate(), DateUtils.DATE_FORMAT));
            vo.setType(po.getType().getDesc());
            vo.setOperateType(po.getOperateType().getDesc());
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public RpcMessage<BigDecimal> getTotalMoney() {
        return RpcMessage.success(pivotErrorHandlingDetailService.getTotalMoney());
    }

    @Override
    public RpcMessage<Page<ErrorHandlingAccountResDTO>> getErrorHandlingPage(ErrorHandlingAccountReqDTO errorHandlingAccountReqDTO) {
        Page<PivotErrorHandlingDetailPO> rowBounds = new Page<>(errorHandlingAccountReqDTO.getPageNo(), errorHandlingAccountReqDTO.getPageSize());
        Date startCreateTime = errorHandlingAccountReqDTO.getStartCreateTime();
        Date endCreateTime = errorHandlingAccountReqDTO.getEndCreateTime();
        PivotErrorHandlingDetailPO pivotErrorHandlingDetailPO = BeanMapperUtils.map(errorHandlingAccountReqDTO, PivotErrorHandlingDetailPO.class);
        Page<PivotErrorHandlingDetailPO> pivotFeeDetailPOPage = pivotErrorHandlingDetailService.queryPageListByTimeRange(pivotErrorHandlingDetailPO, rowBounds, "create_time", startCreateTime, endCreateTime);
        Page<ErrorHandlingAccountResDTO> errorHandlingAccountResDTOPage = new Page<>();
        errorHandlingAccountResDTOPage = BeanMapperUtils.map(pivotFeeDetailPOPage, errorHandlingAccountResDTOPage.getClass());
        List<PivotErrorHandlingDetailPO> pivotFeeDetailPOList = pivotFeeDetailPOPage.getRecords();
        List<ErrorHandlingAccountResDTO> userInfoResDTOList = BeanMapperUtils.mapList(pivotFeeDetailPOList, ErrorHandlingAccountResDTO.class);
        errorHandlingAccountResDTOPage.setRecords(userInfoResDTOList);
        return RpcMessage.success(errorHandlingAccountResDTOPage);
    }

}
