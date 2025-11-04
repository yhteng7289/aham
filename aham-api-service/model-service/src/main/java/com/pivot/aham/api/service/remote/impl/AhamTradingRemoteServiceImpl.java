package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.api.server.dto.*;
import com.pivot.aham.api.server.remoteservice.AhamTradingRemoteService;
import com.pivot.aham.api.server.remoteservice.ModelServiceRemoteService;
import com.pivot.aham.api.service.client.saxo.resp.OrderConfirmationResp;
import com.pivot.aham.api.service.service.*;
import lombok.extern.slf4j.Slf4j;


import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Service(interfaceClass = AhamTradingRemoteService.class)
@Slf4j
public class AhamTradingRemoteServiceImpl implements AhamTradingRemoteService {

    private static final BigDecimal HUNDRED = new BigDecimal(100);

    @Resource
    private AhamTradingService ahamTradingService;

    
    @Override
    public ProductInfoResDTO getAhamDailyClosingPrice(ProductInfoResDTO productInfoResDTO) {
        
        ProductInfoResDTO prdInfoResDTO = ahamTradingService.getDailyClosingPrice(productInfoResDTO);
       
        return prdInfoResDTO;
    }
    
    @Override
    //public void placeAhamNewOrder(Integer uic, Integer share, String orderType) {
    public void  placeAhamNewOrder(String prdCode, String transType, Integer unit, String orderId, Date transDate) {
        
        ahamTradingService.placeAhamNewOrder(prdCode, transType, unit, orderId, transDate);
       
        //return prdInfoResDTO;
    }
    
    @Override
    public List<OrderConfirmationResDTO> queryOrderConfirmation() {
        
       List<OrderConfirmationResDTO> listOrderConfirmationResDTO = ahamTradingService.queryOrderConfirmation();
       
        return listOrderConfirmationResDTO;
    }

}
