package com.pivot.aham.api.service.impl;

import org.springframework.stereotype.Component;

/**
 * @program: aham
 * @description:
 * @author: zhang7
 * @create: 2019-06-30 19:31
 **/
@Component
public class SaxoTradingCalculate {

    //S_SAXO = [( X1+X2+X3+Y1+Y2-Z2)+3.99]/price_sell
    //S_SAXO = Integer(XS1+XS2+XS3+YS1+YS2)
    //CS = ( Z2-TCS * [ ZS2 / ( SellX + BuyX ) ] ) / Price_Sell
    //CS = SellX - S_SAXO
//TC = XS1 /(SellX+BuyX) * TCS
    //TC = XS2 / (SellX+BuyX) * TCS
    //RS =(S_SAXO+CS-XS1-XS2)
}
