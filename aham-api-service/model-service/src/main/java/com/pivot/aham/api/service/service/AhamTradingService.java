package com.pivot.aham.api.service.service;

import com.pivot.aham.api.server.dto.OrderConfirmationResDTO;
import com.pivot.aham.api.server.dto.ProductInfoResDTO;
import com.pivot.aham.api.service.client.saxo.resp.OrderConfirmationResp;
import java.util.Date;
import java.util.List;



public interface AhamTradingService {

    ProductInfoResDTO getDailyClosingPrice(ProductInfoResDTO productInfoResDTO);
    
    //void placeAhamNewOrder(Integer uic, Integer share, String orderType);
    void placeAhamNewOrder(String prdCode, String transType, Integer unit, String orderId, Date transDate);
    
    List<OrderConfirmationResDTO> queryOrderConfirmation();
}
