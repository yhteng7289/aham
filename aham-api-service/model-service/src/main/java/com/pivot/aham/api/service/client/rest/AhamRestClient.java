package com.pivot.aham.api.service.client.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.pivot.aham.api.server.dto.ProductInfoResDTO;
import com.pivot.aham.api.service.client.rest.resp.ModelPortfolioDetailResp;
import com.pivot.aham.api.service.client.rest.resp.ModelPortfolioResp;
import com.pivot.aham.api.service.client.rest.resp.ProductClosingPriceResp;
import com.pivot.aham.api.service.client.saxo.resp.OrderConfirmationResp;
import com.pivot.aham.common.core.exception.HttpClientException;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.core.util.HttpResMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class AhamRestClient extends  AhamRestClientBase {

    private static final String modelPortfoliosUrl = "fund/model_portfolios";
    private static final String modelPortfolioDetailUrl = "fund/model_portfolios/detail";
    private static final String dailyClosingPriceUrl = "fund/productnavhistorical";
    private static final String orderConfirmationUrl = "transaction/daas/dailyconfirmation";
    private static final String transactionOrderUrl = "transaction/SubmitModelPortfolioOrder";

    private static String createErrorMsg(String title, Object request, Object response) {
        return title + ", 【request】: " + JSON.toJSON(request) + ", 【response】: " + JSON.toJSON(response);
    }

        //query model portfolio
//    https://connectapiuat.nadia.com.my/fund/model_portfolios


    public static List<ModelPortfolioResp> modelPortfolios() {

        String url = AhamRestConstant.getBaseUrl() + modelPortfoliosUrl;
        HttpResMsg response = executeGet(url, null);
        List<ModelPortfolioResp> modelPortfolioRespList = new ArrayList<>();


        if (response != null && !StringUtils.isEmpty(response.getResponseStr()) && response.isSuccess()) {
            JSONObject modelPortfolios = JSON.parseObject(response.getResponseStr());
            JSONArray jsonArray = modelPortfolios.getJSONArray(AhamRestConstant.ModelPortfolios);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject modelPortfolio = jsonArray.getJSONObject(i);
                ModelPortfolioResp modelPortfolioResp = new ModelPortfolioResp();
                modelPortfolioResp.setId(Integer.parseInt(modelPortfolio.getString(AhamRestConstant.Id)));
                modelPortfolioResp.setName(modelPortfolio.getString(AhamRestConstant.Name));
                modelPortfolioRespList.add(modelPortfolioResp);
            }
            return modelPortfolioRespList;
        }
        ErrorLogAndMailUtil.logErrorForTrade(log, createErrorMsg("Call for model portfolios failed", url, response));
        throw new HttpClientException("Call for model portfolios failed");
    }

    public static List<ModelPortfolioDetailResp> getModelPortfolioDetailById( int id) {

        String url = AhamRestConstant.getBaseUrl() + modelPortfolioDetailUrl;
        Date today = DateUtils.now();
        Map<String, String> params = Maps.newHashMap();
        params.put(AhamRestConstant.IdParam,Integer.toString(id));
        params.put(AhamRestConstant.DateParam,DateUtils.formatDate(today, DateUtils.DATE_FORMAT ));

        HttpResMsg response = executeGet(url, params);
        List<ModelPortfolioDetailResp> modelPortfolioRespList = new ArrayList<>();

        if (response != null && !StringUtils.isEmpty(response.getResponseStr()) && response.isSuccess()) {
            JSONObject modelPortfoliosDetail = JSON.parseObject(response.getResponseStr());
            JSONArray jsonArray = modelPortfoliosDetail.getJSONObject(AhamRestConstant.PortfolioDetails).getJSONArray("items");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject modelPortfolio = jsonArray.getJSONObject(i);
                ModelPortfolioDetailResp modelPortfolioDetailResp = new ModelPortfolioDetailResp();
                modelPortfolioDetailResp.setId(modelPortfolio.getInteger(AhamRestConstant.PortfolioId));
                modelPortfolioDetailResp.setName(modelPortfolio.getString(AhamRestConstant.PortfolioName));
                modelPortfolioDetailResp.setScheme(modelPortfolio.getString(AhamRestConstant.Scheme));
                modelPortfolioDetailResp.setProductCode(modelPortfolio.getString(AhamRestConstant.ProductCode));
                modelPortfolioDetailResp.setWeightage(modelPortfolio.getBigDecimal(AhamRestConstant.Weightage));
                modelPortfolioDetailResp.setValidFrom(modelPortfolio.getString(AhamRestConstant.ValidFrom));
                modelPortfolioDetailResp.setValidTo(modelPortfolio.getString(AhamRestConstant.ValidTo));
                modelPortfolioDetailResp.setScore(modelPortfolio.getBigDecimal(AhamRestConstant.Score));
                modelPortfolioRespList.add(modelPortfolioDetailResp);
            }
            return modelPortfolioRespList;
        }

        ErrorLogAndMailUtil.logErrorForTrade(log, createErrorMsg("Call for model portfolio detail failed", url, response));
        throw new HttpClientException("Call for model portfolio detail failed");
    }
    
    public static List<ProductClosingPriceResp> getDailyClosingPrice(ProductInfoResDTO productInfoResDTO) {

        String url = AhamRestConstant.getBaseUrl() + dailyClosingPriceUrl;
        List<ProductClosingPriceResp> listProductClosingPriceResp = new ArrayList<>();
        boolean isHasPrice = false;
        HttpResMsg response = null;
        int y = -1;
        while(!isHasPrice){
            Date today = DateUtils.addDays(DateUtils.now(), y);//DateUtils.now();
            Map<String, String> params = Maps.newHashMap();
            params.put("pSchemeCode",productInfoResDTO.getProductCode());
            params.put("pFromDate",DateUtils.formatDate(today, DateUtils.DATE_FORMAT ));
            params.put("pToDate",DateUtils.formatDate(today, DateUtils.DATE_FORMAT ));

            response = executeGet(url, params);

            if (response != null && !StringUtils.isEmpty(response.getResponseStr()) && response.isSuccess()) {
                JSONObject productnavhistoricalResult= JSON.parseObject(response.getResponseStr());
                JSONArray jsonArray = productnavhistoricalResult.getJSONArray("productnavhistoricalResult");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject productClosing = jsonArray.getJSONObject(i);
                    ProductClosingPriceResp productClosingPriceResp = new ProductClosingPriceResp();
                    productClosingPriceResp.setFundCode(productClosing.getString(AhamRestConstant.FundCode));
                    productClosingPriceResp.setFundName(productClosing.getString(AhamRestConstant.FundName));
                    productClosingPriceResp.setFundType(productClosing.getString(AhamRestConstant.FundType));
                    productClosingPriceResp.setSchemeCode(productClosing.getString(AhamRestConstant.SchemeCode));
                    productClosingPriceResp.setClassName(productClosing.getString(AhamRestConstant.ClassName));
                    productClosingPriceResp.setCurrency(productClosing.getString(AhamRestConstant.Currency));
                    productClosingPriceResp.setNavDate(productClosing.getDate(AhamRestConstant.NAVDate));
                    productClosingPriceResp.setNavPrice(productClosing.getBigDecimal(AhamRestConstant.NAV));
                    listProductClosingPriceResp.add(productClosingPriceResp);
                    isHasPrice = true;
                }
            }
            y--;
        }
        return listProductClosingPriceResp;

        //ErrorLogAndMailUtil.logErrorForTrade(log, createErrorMsg("Call for getDailyClosingPrice failed", url, response));
        //throw new HttpClientException("Call for getDailyClosingPrice failed");
        
    }
    
    public static void placeAhamNewOrder(String prdCode, String transType, Integer unit, String orderId, Date transDate){
        
        String url = AhamRestConstant.getBaseUrl() + transactionOrderUrl;
        /*Map<String, Object> params = Maps.newHashMap();
        params.put("pTransactionDate",DateUtils.formatDate(transDate, DateUtils.DATE_FORMAT4 ));
        params.put("pScheme",prdCode);
        params.put("pTransType",transType);
        params.put("pUnit",unit.toString());
        params.put("pOrderID",orderId);
        params.put("pAgentID","RZ00000");
*/
        HttpResMsg response = executePostTrade(url, prdCode, transType, unit, orderId, transDate);//executeGet(url, params);
        if (response != null && !StringUtils.isEmpty(response.getResponseStr()) && response.isSuccess()) {
            System.out.println("response success");
        }
    }
    
    public static List<OrderConfirmationResp> queryOrderConfirmation(){
        String url = AhamRestConstant.getBaseUrl() + orderConfirmationUrl;
        Date today = DateUtils.now();
        Map<String, String> params = Maps.newHashMap();
        params.put(AhamRestConstant.PAccountId,Integer.toString(1));
        params.put(AhamRestConstant.PDateFrom,DateUtils.formatDate(today, DateUtils.DATE_FORMAT ));
        params.put(AhamRestConstant.PDateTo,DateUtils.formatDate(today, DateUtils.DATE_FORMAT ));
        params.put(AhamRestConstant.PScheme,DateUtils.formatDate(today, DateUtils.DATE_FORMAT ));
        params.put(AhamRestConstant.PPlan,DateUtils.formatDate(today, DateUtils.DATE_FORMAT ));
        params.put(AhamRestConstant.PTrantype,DateUtils.formatDate(today, DateUtils.DATE_FORMAT ));


        HttpResMsg response = new HttpResMsg();//executeGet(url, params);
        response.setResponseStr("{\"daasconfirmedtransactionResult\": [{\"ReferenceID\": \"RP011900097496\",\"InvestorID\": \"010012345678\",\"Scheme\": \"1BF\",\"Plan\": \"CS\",\"TranType\": \"RD\",\"TranDate\": \"2019-12-10\",\"ValueDate\": \"2019-12-10\",\"Agent\": \"RG01000\",\"Currency\": \"MYR\",\"Amount\": 194.610,\"NAV\": 0.60540000,\"Units\": 321.4500,\"TotalCharge\": 0.000,\"SalesChargePercent\": 0,\"SalesChargeValue\": 0.000}]}");
        response.setStatusCode(200);
        List<OrderConfirmationResp> orderConfirmationRespList = new ArrayList<>();

        if (response != null && !StringUtils.isEmpty(response.getResponseStr()) && response.isSuccess()) {
            JSONObject jObjOrderConfirmationResp = JSON.parseObject(response.getResponseStr());
            JSONArray jsonArray = jObjOrderConfirmationResp.getJSONArray(AhamRestConstant.confirmTransResult);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jObjOrderConfirmRes = jsonArray.getJSONObject(i);
                OrderConfirmationResp orderConfirmationResp = new OrderConfirmationResp();
                orderConfirmationResp.setReferenceID(jObjOrderConfirmRes.getString("ReferenceID"));
                orderConfirmationResp.setInvestorID(jObjOrderConfirmRes.getString("InvestorID"));
                orderConfirmationResp.setScheme(jObjOrderConfirmRes.getString("Scheme"));
                orderConfirmationResp.setPlan(jObjOrderConfirmRes.getString("Plan"));
                orderConfirmationResp.setTranType(jObjOrderConfirmRes.getString("TranType"));
                orderConfirmationResp.setTranDate(jObjOrderConfirmRes.getDate("TranDate"));
                orderConfirmationResp.setValueDate(jObjOrderConfirmRes.getDate("ValueDate"));
                orderConfirmationResp.setAgent(jObjOrderConfirmRes.getString("Agent"));
                orderConfirmationResp.setCurrency(jObjOrderConfirmRes.getString("Currency"));
                orderConfirmationResp.setAmount(jObjOrderConfirmRes.getBigDecimal("Amount"));
                orderConfirmationResp.setNav(jObjOrderConfirmRes.getBigDecimal("NAV"));
                orderConfirmationResp.setUnits(jObjOrderConfirmRes.getBigDecimal("Units"));
                orderConfirmationResp.setTotalCharge(jObjOrderConfirmRes.getBigDecimal("TotalCharge"));
                orderConfirmationResp.setSalesChargePercent(jObjOrderConfirmRes.getBigDecimal("SalesChargePercent"));
                orderConfirmationResp.setSalesChargeValue(jObjOrderConfirmRes.getBigDecimal("SalesChargeValue"));
                orderConfirmationRespList.add(orderConfirmationResp);
            }
            return orderConfirmationRespList;
        }

        ErrorLogAndMailUtil.logErrorForTrade(log, createErrorMsg("Call for model portfolio detail failed", url, response));
        throw new HttpClientException("Call for model portfolio detail failed");
    }

}
