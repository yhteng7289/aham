package com.pivot.aham.api.server.remoteservice;

import com.pivot.aham.api.server.dto.*;
import com.pivot.aham.common.core.base.BaseRemoteService;
import java.util.Date;
import java.util.List;


public interface AhamTradingRemoteService extends BaseRemoteService {
    
    ProductInfoResDTO getAhamDailyClosingPrice(ProductInfoResDTO productInfoResDTO); 
    
    void placeAhamNewOrder(String prdCode, String transType, Integer unit, String orderId, Date transDate);
    
    List<OrderConfirmationResDTO> queryOrderConfirmation();
}
