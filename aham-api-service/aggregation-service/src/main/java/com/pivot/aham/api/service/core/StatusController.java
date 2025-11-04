package com.pivot.aham.api.service.core;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.pivot.aham.api.service.TradingSupportService;
import com.pivot.aham.api.service.client.saxo.SaxoClient;
import com.pivot.aham.api.service.client.saxo.resp.ExchangeInfoResp;
import com.pivot.aham.api.service.client.saxo.util.PreTestInternet;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.monitor.PrometheusMethodMonitor;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.core.util.HttpclientUtils;
import com.pivot.aham.common.core.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Map;

@Controller
@RequestMapping("Status")
@Slf4j
public class StatusController extends AbstractController {

    @Autowired
    private TradingSupportService tradingSupportService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping(value = "Version")
    @ResponseBody
    @PrometheusMethodMonitor
    public Map version(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.sendError(404, "aaaaaa");
//        throw  new BusinessException("version回滚测试");
        //本机IP
        InetAddress ip = InetAddress.getLocalHost();
        //获取网络接口
        NetworkInterface network = NetworkInterface.getByInetAddress(ip);
        StringBuilder macBuilder = new StringBuilder();
        if (network != null) {
            byte[] mac = network.getHardwareAddress();
            for (byte b : mac) {
                long tmp = 0x000000FF & (long) b;
                macBuilder.append(Long.toHexString(tmp));
                macBuilder.append(":");
            }
        }
        Sequence sequence = new Sequence();
        Map<String, String> info = Maps.newHashMap();
        info.put("mac", macBuilder.toString());
        info.put("timemillis", System.currentTimeMillis() + "");
        info.put("dataCenterId11", sequence.watchDatacenterId() + "");
        info.put("workerId11", sequence.watchWorkerId() + "");
        info.put("env2", PropertiesUtil.getString("env.remark"));
        info.put("datetime", "2019-06-28_01");

        return info;
    }

    @RequestMapping(value = "Mail")
    @ResponseBody
    @PrometheusMethodMonitor
    public String mail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ErrorLogAndMailUtil.logError(log, "测试发邮件啊啊啊啊啊啊，同时也测试测试输出LOG啊啊啊啊啊啊啊啊");
        return "success";
    }

    @RequestMapping(value = "Saxo")
    @ResponseBody
    @PrometheusMethodMonitor
    public String saxo(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ExchangeInfoResp resp = SaxoClient.queryExchangeInfo("NASDAQ");

        return JSON.toJSONString(resp);
    }

    @RequestMapping(value = "ens")
    @ResponseBody
    @PrometheusMethodMonitor
    public String ens(HttpServletRequest request, HttpServletResponse response) throws IOException {

        tradingSupportService.saveAccountFoundingEvent();

        return "success";
    }

    @RequestMapping("/eee")
    @ResponseBody
    public Message eee(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception e) {

        }
        return Message.success();
    }

    @RequestMapping("/saveClosingPrice")
    @ResponseBody
    public Message saveClosingPrice(String dateStr) {
        try {
            tradingSupportService.saveClosingPrice(dateStr);
        } catch (Exception e) {

        }
        return Message.success();
    }

    @RequestMapping("testNetWork")
    @ResponseBody
    public Message testNetWork() throws IOException {
        PreTestInternet.requestGoogle();
        HttpclientUtils.get("https://google.com");
        return Message.success("testNetWork done");
    }

    @RequestMapping("/refreshToken")
    @ResponseBody
    public Message testToken() {
        SaxoClient.refreshToken();
        return Message.success();
    }

}
