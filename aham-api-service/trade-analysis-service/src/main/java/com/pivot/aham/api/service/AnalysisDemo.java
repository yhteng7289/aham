package com.pivot.aham.api.service;

import java.math.BigDecimal;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月16日
 */
public class AnalysisDemo {

    /**
     * 变量假设
     假设：CashDividend=0
     假设：interest=0
     假设T-1：price=1
     假设T日：price=2
     假设T+1日：price=2
     假设：各种fee=0
     */
    BigDecimal MGT_Fee = BigDecimal.ZERO;
    BigDecimal Cust_Fee = BigDecimal.ZERO;
    BigDecimal Cost_Fee = BigDecimal.ZERO;
    BigDecimal Per_Fee = BigDecimal.ZERO;
    BigDecimal CashDividend = BigDecimal.ZERO;
    BigDecimal USD_Interest = BigDecimal.ZERO;



    /**
     * 交易价格
     */
    BigDecimal etfPriceT = new BigDecimal(1);
    BigDecimal etfPriceTA1 = new BigDecimal(2);
    BigDecimal etfPriceTA2 = new BigDecimal(2);


    /**
     * T日交易之前的资产记录
     */
    BigDecimal Cash_SAXO_USDT = new BigDecimal(30);
    BigDecimal Cash_SAXO_ETF_BuyT = new BigDecimal(170);
    BigDecimal ETCash_SAXO_ETFA_BuyT = new BigDecimal(170);


    /**
     * T日交易之后的资产记录
     */
    BigDecimal InitialValueT=new BigDecimal(200);
    BigDecimal unBuyAmountT = new BigDecimal(5);
    BigDecimal EFT_MAIN_A_shares = new BigDecimal(85);
    BigDecimal EFT_SUB_B_shares = new BigDecimal(85);
    BigDecimal Cash_Residual=Cash_SAXO_ETF_BuyT.subtract(EFT_MAIN_A_shares.multiply(etfPriceT)).subtract((EFT_SUB_B_shares.multiply(etfPriceT)));
    BigDecimal ADJ_Cash_SAXO_USDT=Cash_SAXO_USDT.add(Cash_Residual);

    BigDecimal TotalCashT = ADJ_Cash_SAXO_USDT;
    BigDecimal TotalEquityT = EFT_MAIN_A_shares.multiply(etfPriceT).add(EFT_SUB_B_shares.multiply(etfPriceT));
    BigDecimal ADJ_FundSharesT=InitialValueT;
    BigDecimal ADJ_Fund_AssetT=TotalCashT.add(TotalEquityT);
    BigDecimal FundNavT=ADJ_Fund_AssetT.divide(ADJ_FundSharesT).setScale(6, BigDecimal.ROUND_HALF_UP);


    /**
     * mock T 日交易
     */
    private void setT(){
        /**
         * T日交易之前的资产记录
         */
        Cash_SAXO_USDT = new BigDecimal(30);
        Cash_SAXO_ETF_BuyT = new BigDecimal(170);
        ETCash_SAXO_ETFA_BuyT = new BigDecimal(170);


        /**
         * 交易之后
         */
        InitialValueT=new BigDecimal(200);
        unBuyAmountT = new BigDecimal(5);
        EFT_MAIN_A_shares = new BigDecimal(85);
        EFT_SUB_B_shares = new BigDecimal(85);
        Cash_Residual=Cash_SAXO_ETF_BuyT.subtract(EFT_MAIN_A_shares.multiply(etfPriceT)).subtract((EFT_SUB_B_shares.multiply(etfPriceT)));
        ADJ_Cash_SAXO_USDT=Cash_SAXO_USDT.add(Cash_Residual);

        TotalCashT = ADJ_Cash_SAXO_USDT;
        TotalEquityT = EFT_MAIN_A_shares.multiply(etfPriceT).add(EFT_SUB_B_shares.multiply(etfPriceT));
        ADJ_FundSharesT=InitialValueT;
        ADJ_Fund_AssetT=TotalCashT.add(TotalEquityT);
        FundNavT=ADJ_Fund_AssetT.divide(ADJ_FundSharesT).setScale(6, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * TPCF(t)=0 and TNCF(t)<0
     */
    private  void onlyTncfTA1(){
        BigDecimal TNCF=new BigDecimal(22);
        BigDecimal CashExcess_SAXO=ADJ_Cash_SAXO_USDT.subtract(ADJ_Fund_AssetT.multiply(new BigDecimal("0.03")));

        BigDecimal CashExcess_SAXO95 = CashExcess_SAXO.multiply(new BigDecimal("0.95"));
        if(TNCF.compareTo(CashExcess_SAXO95)<0) {
            //交易之前
            BigDecimal Cash_SAXO_USDTA1 = Cash_SAXO_USDT;
            BigDecimal unBuyAmountTA1 = unBuyAmountT;
            if(unBuyAmountT.compareTo(TNCF) < 0) {
                unBuyAmountTA1 = BigDecimal.ZERO;
            } else {
                unBuyAmountTA1 = unBuyAmountT.subtract(TNCF);
            }

            //交易之后
            EFT_MAIN_A_shares = new BigDecimal(85);
            EFT_SUB_B_shares = new BigDecimal(85);

            BigDecimal TotalCashTA1=Cash_SAXO_USDTA1;
            BigDecimal TotalEquity=EFT_MAIN_A_shares.multiply(etfPriceTA1).add(EFT_SUB_B_shares.multiply(etfPriceTA1));
            BigDecimal FundSharesTA1= ADJ_FundSharesT;
            BigDecimal Fund_AssetTA1 = TotalCashTA1.add(TotalEquity);
            BigDecimal FundNAVT=FundNavT;
            BigDecimal FundNAVTA1= Fund_AssetTA1.divide( FundSharesTA1).setScale(6,BigDecimal.ROUND_HALF_UP);

            BigDecimal Cash_WihdrawTA1 = TNCF.divide(FundNAVT,6,BigDecimal.ROUND_HALF_UP).multiply(FundNAVTA1);
            BigDecimal ADJ_Cash_SAXO_USDTA1 = Cash_SAXO_USDT.subtract(Cash_WihdrawTA1);

            BigDecimal ADJ_FundSharesTA1 = Fund_AssetTA1.subtract(TNCF).divide(FundNavT,6,BigDecimal.ROUND_HALF_UP);
            BigDecimal ADJ_Fund_AssetTA1 = ADJ_Cash_SAXO_USDTA1.add(TotalEquity);
        }else{
            BigDecimal EV_SUB = EFT_SUB_B_shares.multiply(etfPriceT);

            BigDecimal EV_SUB95 = EV_SUB.multiply(new BigDecimal(0.95));
            if(TNCF.compareTo(EV_SUB95)<0){
                //交易之前
                //此处因为sub中只有一个B产品
                BigDecimal weight_sub=EFT_SUB_B_shares.multiply(etfPriceTA1).divide(EFT_SUB_B_shares.multiply(etfPriceTA1));
                BigDecimal Sell_EFT_SUB_B_shares=TNCF.multiply(weight_sub);

                //交易之后
                BigDecimal Selled_price = new BigDecimal(1);
                BigDecimal Selled_EFT_SUB_B_shares = Sell_EFT_SUB_B_shares;
                BigDecimal Cash_BY_SELL= Selled_EFT_SUB_B_shares.multiply(Selled_price);
                EFT_SUB_B_shares = EFT_SUB_B_shares.subtract(Selled_EFT_SUB_B_shares);
                BigDecimal TotalCashTA1=Cash_SAXO_USDT.subtract(Cash_BY_SELL);
                BigDecimal TotalEquity=EFT_MAIN_A_shares.multiply(etfPriceTA1).add(EFT_SUB_B_shares.multiply(etfPriceTA1));
                BigDecimal FundSharesTA1= ADJ_FundSharesT;
                BigDecimal Fund_AssetTA1 = TotalCashTA1.add(TotalEquity);
                BigDecimal FundNAVT=FundNavT;
                BigDecimal FundNAVTA1= Fund_AssetTA1.divide( FundSharesTA1).setScale(6,BigDecimal.ROUND_HALF_UP);

                BigDecimal Cash_WihdrawTA1 = TNCF.divide(FundNAVT,6,BigDecimal.ROUND_HALF_UP).multiply(FundNAVTA1);
                BigDecimal ADJ_Cash_SAXO_USDTA1 = Cash_SAXO_USDT.subtract(Cash_WihdrawTA1);

                BigDecimal ADJ_FundSharesTA1 = Fund_AssetTA1.subtract(TNCF).divide(FundNavT,6,BigDecimal.ROUND_HALF_UP);
                BigDecimal ADJ_Fund_AssetTA1 = ADJ_Cash_SAXO_USDTA1.add(TotalEquity);

            }else{

            }




        }
























    }






}
