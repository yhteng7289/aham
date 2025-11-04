package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.beust.jcommander.internal.Lists;
import com.pivot.aham.api.server.dto.BankVirtualAccountOrderResDTO;
import com.pivot.aham.api.server.dto.UobExchangeCallbackDTO;
import com.pivot.aham.api.server.dto.UobExchangeDTO;
import com.pivot.aham.api.server.dto.req.ExchangeRateDTO;
import com.pivot.aham.api.server.dto.res.ExchangeRateResDTO;
import com.pivot.aham.api.server.remoteservice.ExchangeRemoteService;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.service.job.impl.WithdrawalUobToBankTransferJobImpl;
import com.pivot.aham.api.service.mapper.model.ExchangeRatePO;
import com.pivot.aham.api.service.service.ExchangeRateService;
import com.pivot.aham.api.service.service.RechargeService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.enums.ExchangeRateTypeEnum;
import com.pivot.aham.common.enums.analysis.VAOrderActionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月25日
 */
@Service(interfaceClass = ExchangeRemoteService.class)
@Slf4j
public class ExchangeRemoteServiceImpl implements ExchangeRemoteService {
    @Resource
    private UserServiceRemoteService userServiceRemoteService;
    @Resource
    private RechargeService rechargeService;
    @Resource
    private WithdrawalUobToBankTransferJobImpl withdrawalUobToBankTransferJob;
    @Resource
    private ExchangeRateService exchangeRateService;

    /**
     * @param uobExchangeCallback
     * @return
     */
    @Override
    public RpcMessage uobExchangeCallBack(UobExchangeCallbackDTO uobExchangeCallback) {
        log.info("UOB的购汇异步回调,请求参数:{}", JSON.toJSON(uobExchangeCallback));
        if (null == uobExchangeCallback) {
            return RpcMessage.error("UOB的购汇异步回调,请求参数为空");
        }
        List<UobExchangeCallbackDTO> transferErrorUsers = Lists.newArrayList();
        //处理购汇订单
        BankVirtualAccountOrderResDTO virtualAccountOrderResDTO = userServiceRemoteService.queryById(uobExchangeCallback.getOrderNo());
        if (null == virtualAccountOrderResDTO) {
            log.error("该购汇单查不到,{}", JSON.toJSON(uobExchangeCallback));
            return RpcMessage.error("该购汇单查不到");
        }
        //充值在UOB中需要转汇的一定是从USD到SGD
        if (virtualAccountOrderResDTO.getActionType() == VAOrderActionTypeEnum.RECHARGE_EXCHANGE) {
            log.info("UOB充值购汇回调，param:{}:", JSON.toJSONString(uobExchangeCallback));
            try {
                UobExchangeDTO uobExchangeDTO = new UobExchangeDTO();
                uobExchangeDTO.setConfirmMoney(uobExchangeCallback.getConfirmMoney());
                uobExchangeDTO.setVirtualAccountOrderId(uobExchangeCallback.getOrderNo());
                userServiceRemoteService.handelUobExchangeCallBack(uobExchangeDTO);
                //统计 汇率
                handelUobExchangeRate(uobExchangeCallback, ExchangeRateTypeEnum.UOB_USD_TO_SGD, virtualAccountOrderResDTO);
            } catch (Exception ex) {
                transferErrorUsers.add(uobExchangeCallback);
                log.error("param:{},UOB充值购汇回调异常:", JSON.toJSON(uobExchangeCallback), ex);
            }
        }
        //提现在UOB中需要转汇的一定是从SGD到USD
        if (virtualAccountOrderResDTO.getActionType() == VAOrderActionTypeEnum.REDEEM_EXCHANGE) {
            log.info("UOB提现购汇回调，param:{}:", JSON.toJSONString(uobExchangeCallback));
            try {
                withdrawalUobToBankTransferJob.handlerExchangeCallBack(uobExchangeCallback);
                //统计 汇率
                handelUobExchangeRate(uobExchangeCallback, ExchangeRateTypeEnum.UOB_SGD_TO_USD, virtualAccountOrderResDTO);
            } catch (Exception ex) {
                transferErrorUsers.add(uobExchangeCallback);
                log.error("param:{},UOB提现购汇回调异常:", JSON.toJSON(uobExchangeCallback), ex);
            }
        }

        if (CollectionUtils.isNotEmpty(transferErrorUsers)) {
            //邮件通知
            ErrorLogAndMailUtil.logError(log, transferErrorUsers);
        }
        log.info("UOB的购汇异步回调,处理完成");
        return RpcMessage.success("UOB的购汇异步回调,处理完成");
    }

    @Override
    public RpcMessage<ExchangeRateResDTO> getExchangeRate(ExchangeRateDTO exchangeRateDTO) {
        log.info("汇率查询，exchangeRateDTO:{}", JSON.toJSONString(exchangeRateDTO));
        ExchangeRatePO exchangeRateParam = new ExchangeRatePO();
        exchangeRateParam.setExchangeRateType(exchangeRateDTO.getExchangeRateType());
        exchangeRateParam.setRateDate(exchangeRateDTO.getRateDate());
        ExchangeRatePO exchangeRatePO = exchangeRateService.getExchangeRate(exchangeRateParam);
        if (null == exchangeRatePO) {
            throw new BusinessException("不存在的汇率");
        }
        ExchangeRateResDTO resDTO = BeanMapperUtils.map(exchangeRatePO, ExchangeRateResDTO.class);
        log.info("汇率查询，resDTO:{}", JSON.toJSONString(resDTO));
        return RpcMessage.success(resDTO);
    }

    @Override
    public RpcMessage<ExchangeRateResDTO> getLastExchangeRate(ExchangeRateDTO exchangeRateDTO) {
        ExchangeRatePO exchangeRateParam = new ExchangeRatePO();
        exchangeRateParam.setExchangeRateType(exchangeRateDTO.getExchangeRateType());
        ExchangeRatePO exchangeRatePO = exchangeRateService.queryLastExchangeRate(exchangeRateParam);
        ExchangeRateResDTO resDTO = BeanMapperUtils.map(exchangeRatePO, ExchangeRateResDTO.class);
        return RpcMessage.success(resDTO);
    }

    /**
     * 统计汇率
     * 1.充值类型的话一定是从USD转SGD，因为充值的用户之后从USD转成SGD才会转账到SAXO
     * 2.提现的话一定是SGD转USD，因为提现的话钱使用SAXO到UOB的一定是SGD。用户要提现的是USD才会转汇，所以一定是SGD到USD的转汇
     *
     * @param uobExchangeCallback
     * @param exchangeRateType
     * @param virtualAccountOrderResDTO
     */
    private void handelUobExchangeRate(UobExchangeCallbackDTO uobExchangeCallback,
                                       ExchangeRateTypeEnum exchangeRateType,
                                       BankVirtualAccountOrderResDTO virtualAccountOrderResDTO) {
        BigDecimal rate = BigDecimal.ZERO;
        if (ExchangeRateTypeEnum.UOB_SGD_TO_USD == exchangeRateType) {
            rate = virtualAccountOrderResDTO.getCashAmount().divide(uobExchangeCallback.getConfirmMoney(), 6, BigDecimal.ROUND_HALF_UP);
        } else {
            rate = uobExchangeCallback.getConfirmMoney().divide(virtualAccountOrderResDTO.getCashAmount(), 6, BigDecimal.ROUND_HALF_UP);
        }

        ExchangeRatePO exchangeRatePO = new ExchangeRatePO();
        exchangeRatePO.setExchangeRateType(exchangeRateType);
        exchangeRatePO.setRateDate(DateUtils.dayStart(DateUtils.now()));
        exchangeRatePO.setUsdToSgd(rate);
        exchangeRateService.saveDailyExchangeRate(exchangeRatePO);
    }
}
