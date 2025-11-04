package com.pivot.aham.api.service.service.impl;


import com.beust.jcommander.internal.Lists;
import com.pivot.aham.api.server.dto.OrderConfirmationResDTO;
import com.pivot.aham.api.server.dto.ProductInfoResDTO;
import com.pivot.aham.api.service.client.rest.AhamRestClient;
import com.pivot.aham.api.service.client.rest.resp.ProductClosingPriceResp;
import com.pivot.aham.api.service.client.saxo.resp.OrderConfirmationResp;
import com.pivot.aham.api.service.service.AhamTradingService;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;



@Service
@Slf4j
public class AhamTradingServiceImpl implements AhamTradingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AhamTradingServiceImpl.class);


    @Override
    public ProductInfoResDTO getDailyClosingPrice(ProductInfoResDTO productInfoResDTO) {
  
        List<ProductClosingPriceResp> listProductClosePrice =  AhamRestClient.getDailyClosingPrice(productInfoResDTO);
        ProductInfoResDTO prdInfoResDTO = new ProductInfoResDTO();
        if(listProductClosePrice.size() > 0){
            prdInfoResDTO.setProductCode(listProductClosePrice.get(0).getSchemeCode());
            //prdInfoResDTO.setBsnDt(listProductClosePrice.get(0).getNavDate());
            prdInfoResDTO.setClosingPrice(listProductClosePrice.get(0).getNavPrice());
            prdInfoResDTO.setNavDate(listProductClosePrice.get(0).getNavDate());
            
        }
        
    return prdInfoResDTO;
    }
    
    @Override
    public void placeAhamNewOrder(String prdCode, String transType, Integer unit, String orderId, Date transDate) {
  
        AhamRestClient.placeAhamNewOrder(prdCode, transType, unit, orderId, transDate);  
    }
    
    @Override
    public List<OrderConfirmationResDTO> queryOrderConfirmation() {
  
        List<OrderConfirmationResp> orderConfirmationRespList = AhamRestClient.queryOrderConfirmation();
        List<OrderConfirmationResDTO> listOrderConfirmationResDTO = Lists.newArrayList();
        for(OrderConfirmationResp orderConResp : orderConfirmationRespList){
            OrderConfirmationResDTO orderConfirmationResDTO = new OrderConfirmationResDTO();
            //BeanUtils.copyProperties(orderConfirmationResDTO,  orderConResp);
            BeanUtils.copyProperties(orderConResp,  orderConfirmationResDTO);
            listOrderConfirmationResDTO.add(orderConfirmationResDTO);
        }

        return listOrderConfirmationResDTO;
    }

}
