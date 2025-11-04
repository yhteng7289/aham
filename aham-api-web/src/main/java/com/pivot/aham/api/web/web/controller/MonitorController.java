//package com.pivot.aham.api.web.web;
//
//import MetriceCounterService;
//import MetriceGuageService;
//import MetriceSummaryService;
//import MetriceTimerService;
//import org.springframework.stereotype.Controller;
//import org.springframework.stereotype.Repository;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Controller
//@RequestMapping("monitor")
//public class MonitorController {
//
//
//    @Resource
//    private MetriceCounterService metriceCounterService;
//    @Resource
//    private MetriceGuageService metriceGuageService;
//    @Resource
//    private MetriceSummaryService metriceSummaryService;
//    @Resource
//    private MetriceTimerService metriceTimerService;
//
//
//    @RequestMapping(value = "test")
//    @ResponseBody
//    public String version(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {
//        metriceCounterService.test();
//        metriceGuageService.test();
//        metriceSummaryService.test();
//        metriceTimerService.test();
//        return "Success";
//    }
//
//
//}
