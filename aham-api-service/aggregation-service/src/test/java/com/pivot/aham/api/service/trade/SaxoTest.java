package com.pivot.aham.api.service.trade;

import com.pivot.aham.api.server.dto.req.SaxoTradeReq;
import com.pivot.aham.api.service.EtfOrderService;
import com.pivot.aham.api.service.TradingSupportService;
import com.pivot.aham.api.service.impl.trade.*;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.EtfOrderTypeEnum;
import com.pivot.aham.common.enums.EtfmergeOrderTypeEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by hao.tong on 2018/12/21.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SaxoTest {

    @Autowired
    private EtfOrderService etfOrderService;

//    @Autowired
//    private SaxoTradingService saxoTradingService;

    @Autowired
    private TradingSupportService tradingSupportService;

//    @Autowired
//    private EtfOrderMapper etfOrderMapper;

    @Test
    public void recalculateMock() throws Exception {

//        SaxoClient.requestGoogle();
        //SaxoClient.queryHoldingInstrument(31917);
        //PlaceNewOrderResp resp = SaxoClient.placeSellMarketOrder(31917, 2, false);

//        List<String> orderLogStatus = Lists.newArrayList(OrderActivitiesResp.OrderLogStatus.Fill, OrderActivitiesResp.OrderLogStatus.FinalFill);
//        OrderActivitiesResp resp = SaxoClient.queryOrderActivities("274840592", orderLogStatus);
//
//        System.out.println("asdasd");
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Test
    public void test() throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy h:m:s aa", Locale.ENGLISH);
        System.out.println(dateFormat.format(DateUtils.parseDate("2019/4/10 6:21:38")));
        System.out.println(dateFormat.format(DateUtils.parseDate("2019/4/10 10:15:46")));

//        List<String> idList = Lists.newArrayList("1409924634","1409924478","1409924638","1409924606","1409924474","1409924594","1409924642","1409924610","1409924614","1409924598","1409924590","1409924626","1409924630","1409924646","1409924486","1409924618","1409924602","1409924622","1409924470");
//
//        for (String pid : idList) {
//            PositionDetailResp resp = SaxoClient.queryPositionDetail(pid);
//            System.out.println("pid: " + pid + ", resp: " + JSON.toJSON(resp));
//        }


//        SaxoClient.queryPositionDetail("1409924478");
//        System.out.println("");

//        jdbcTemplate.execute("update t_uob_tra_order set order_status = 3 where id in (1112964842363879425, 1112964843580227585, 1112964844398116865)");
//
//        Email email = new Email();
//        email.setBody("11111111");
//        email.setTopic("222222222");
//        email.setSendTo("hao.tong@pintec.com");
//        email.setSSL(true);
//        EmailUtil.sendEmail(email);

//        SaxoClient.refreshToken();

//        AccountFundingRespV2 accountFundingRespV2 = SaxoClient.queryAccountFundingEventV2(null);
//        System.out.println("");

//        List<String> orderLogStatus = Lists.newArrayList(
//                OrderActivitiesResp.OrderLogStatus.Placed,
//                OrderActivitiesResp.OrderLogStatus.Changed,
//                OrderActivitiesResp.OrderLogStatus.Cancelled
//                );
        //tradingSupportService.saveAccountFoundingEvent();

//        System.out.println("");
        //SaxoClient.placeBuyLimitOrder(311665, 1, new BigDecimal(30));

        //SaxoClient.reviseOrder("269481767", "Market", null, null);

//        tradingSupportService.saveAccountFoundingEvent();
//
//        saxoTradingService.mergeOrder();
//        saxoTradingService.sell();
//        Thread.sleep(1000 * 5);
//        saxoTradingService.tradeConfirm_sell();
//        saxoTradingService.buy();
//        Thread.sleep(1000 * 5);
        //saxoTradingService.tradeConfirm_buy();
        //saxoTradingService.demergeOrder();
    }

    @Autowired
    private MergeOrder mergeOrder;
    @Autowired
    private Trade trade;
    @Autowired
    private Confirm confirm;
    @Autowired
    private Demerge demerge;

    @Test
    public void placeEtfOrder() throws Exception {
        this.placeBuyOrder(1L, "ILF", new BigDecimal(3000));

    }

    @Autowired
    private Recalculate recalculate;
    @Test
    public void trading() throws Exception {
//        mergeOrder.mergeEtfOrderForOrderType(true, false);
//        trade.sellOrBuy();
//        confirm.tradeConfirmSellOrBuy();
//        demerge.demergeOrderSellOrBuy();

        mergeOrder.mergeEtfOrderForOrderType(true, true);
        trade.buy();
        confirm.tradeConfirmBuy();
        demerge.demergeOrder(EtfmergeOrderTypeEnum.BUY);
//        recalculate.recalculate();

    }

//    private void placeSellOrder(long outBusinessId, String etfCode, BigDecimal amount, boolean balance){
//        SaxoTradeReq req = new SaxoTradeReq();
//        req.setOutBusinessId(outBusinessId);
//        req.setEtfCode("BNDX");
//        req.setAccountId(1120216392869711873L);
//        req.setAmount(new BigDecimal(5818.760617));
//        req.setBalanceOrder(false);
//        etfOrderService.createSellOrder(req);
//    }

    private void placeSellOrder(long outBusinessId, String etfCode, BigDecimal amount, boolean balance) {
//        SaxoTradeReq req = new SaxoTradeReq();
//        req.setOutBusinessId(outBusinessId);
//        req.setEtfCode(etfCode);
//        req.setAccountId(outBusinessId);
//        req.setAmount(amount);
//        req.setBalanceOrder(balance);
//        etfOrderService.createSellOrder(req);
    }

    private void placeBuyOrder(long outBusinessId, String etfCode, BigDecimal amount) {
        SaxoTradeReq req = new SaxoTradeReq();
        req.setOutBusinessId(1111L);
        req.setEtfCode("BNDX");
        req.setAccountId(1120216392869711873L);
        req.setAmount(new BigDecimal(5818.760617));
        req.setOrderType(EtfOrderTypeEnum.RSA);
        etfOrderService.createBuyOrder(req);
    }

}
