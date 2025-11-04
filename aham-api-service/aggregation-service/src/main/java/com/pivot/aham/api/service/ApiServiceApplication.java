package com.pivot.aham.api.service;

import com.alibaba.csp.sentinel.init.InitExecutor;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.util.Collections;


@SpringBootApplication(scanBasePackages = {"com.pivot.aham"})
public class ApiServiceApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        InitExecutor.doInit();
        FlowRule flowRule = new FlowRule();
        flowRule.setResource("TestRemoteService");
        flowRule.setCount(5);
        flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        flowRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        flowRule.setLimitApp("default");
//        flowRule.setControlBehavior()
        FlowRuleManager.loadRules(Collections.singletonList(flowRule));

        SpringApplication.run(ApiServiceApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ApiServiceApplication.class);
    }
}
