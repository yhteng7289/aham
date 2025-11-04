package com.pivot.aham.api.web.web.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pivot.aham.api.web.web.vo.req.GetTPCFReqVo;
import com.pivot.aham.api.web.web.vo.res.TAccountRechargeResVo;
import com.pivot.aham.api.web.web.vo.res.TAccountRedeemResVo;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.monitor.PrometheusMethodMonitor;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.pivot.aham.api.server.remoteservice.TPCFRemoteService;
import com.pivot.aham.common.core.base.Message;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
@RequestMapping("api/Money")
@Slf4j
public class TPCFController extends AbstractController {

    private static ExecutorService executorService = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(20), new ThreadPoolExecutor.DiscardOldestPolicy());
    
    @Resource
    private TPCFRemoteService tpcfRemoteService;

    @RequestMapping(value = "Version")
    @ResponseBody
    @PrometheusMethodMonitor
    public Map version(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        info.put("dataCenterId", sequence.watchDatacenterId() + "");
        info.put("workerId", sequence.watchWorkerId() + "");
        info.put("env1", PropertiesUtil.getString("env.remark"));
        info.put("datetime", "2019-06-28");
        return info;
    }

    @RequestMapping(value = "TPCF")
    @ResponseBody
    @PrometheusMethodMonitor
    public Message<TAccountRechargeResVo> queryTPCF(@RequestBody @Valid GetTPCFReqVo getTPCFReqVo) throws IOException {

        int current = ( getTPCFReqVo.getCurrent() - 1);
        int size = getTPCFReqVo.getSize();
        
        int length = 0;
        int total = 0;
        
        String checkDate = getTPCFReqVo.getDate();
        
        BigDecimal TPCF = BigDecimal.ZERO;
        List<String> clientIdList = Lists.newArrayList();
        List<String> sendClientIdList = Lists.newArrayList();
        
        List<BigDecimal> rechargeAmountList = Lists.newArrayList();
        List<BigDecimal> sendRechargeAmountList = Lists.newArrayList();
        
        TPCF = tpcfRemoteService.getTPCF(checkDate);
        clientIdList = tpcfRemoteService.getRechargeClient(checkDate);
        rechargeAmountList = tpcfRemoteService.getRechargeAmount(checkDate);
        
        total = clientIdList.size();
        
        if(((current * size) + size ) < total){
        
            length = ((current * size) + size );
        
        }else{
        
            length = total;
        
        }
        
        for(int i = (current * size);i<length;i++){
        
            sendClientIdList.add(clientIdList.get(i));
            sendRechargeAmountList.add(rechargeAmountList.get(i));
        
        }
        
        TAccountRechargeResVo tAccountRechargeResVo = new TAccountRechargeResVo();
        tAccountRechargeResVo.setTpcf(TPCF);
        tAccountRechargeResVo.setRechargeAmount(sendRechargeAmountList);
        tAccountRechargeResVo.setRechargeClient(sendClientIdList);
        tAccountRechargeResVo.setTotal(total);
        
        return Message.success(tAccountRechargeResVo);

    }
    
    @RequestMapping(value = "TNCF")
    @ResponseBody
    @PrometheusMethodMonitor
    public Message<TAccountRedeemResVo> queryTNCF(@RequestBody @Valid GetTPCFReqVo getTPCFReqVo) throws IOException {

        int current = ( getTPCFReqVo.getCurrent() - 1);
        int size = getTPCFReqVo.getSize();
        
        int length = 0;
        int total = 0;
        
        String checkDate = getTPCFReqVo.getDate();
        
        BigDecimal TNCF = BigDecimal.ZERO;
        List<String> clientIdList = Lists.newArrayList();
        List<String> sendClientIdList = Lists.newArrayList();
        
        List<BigDecimal> redeemAmountList = Lists.newArrayList();
        List<BigDecimal> sendRedeemAmountList = Lists.newArrayList();
        
        TNCF = tpcfRemoteService.getTNCF(checkDate);
        clientIdList = tpcfRemoteService.getRedeemClient(checkDate);
        redeemAmountList  = tpcfRemoteService.getRedeemAmount(checkDate);
        
        total = clientIdList.size();
        
        if(((current * size) + size ) < total){
        
            length = ((current * size) + size );
        
        }else{
        
            length = total;
        
        }
        
        for(int i = (current * size);i<length;i++){
        
            sendClientIdList.add(clientIdList.get(i));
            sendRedeemAmountList.add(redeemAmountList.get(i));
        
        }
        
        TAccountRedeemResVo tAccountRedeemResVo = new TAccountRedeemResVo();
        
        tAccountRedeemResVo.setTncf(TNCF);
        tAccountRedeemResVo.setRedeemAmount(sendRedeemAmountList);
        tAccountRedeemResVo.setRedeemClient(sendClientIdList);
        tAccountRedeemResVo.setTotal(total);
        
        return Message.success(tAccountRedeemResVo);
    }

    
}
