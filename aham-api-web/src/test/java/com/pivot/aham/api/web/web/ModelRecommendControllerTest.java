package com.pivot.aham.api.web.web;

import com.pivot.aham.api.server.dto.ModelRecommendDTO;
import com.pivot.aham.api.server.dto.PortLevelDTO;
import com.pivot.aham.api.server.remoteservice.ModelServiceRemoteService;
import com.pivot.aham.api.server.remoteservice.RechargeServiceRemoteService;
import com.pivot.aham.common.core.util.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * Created by luyang.li on 18/12/28.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ModelRecommendControllerTest {

    @Resource
    private ModelServiceRemoteService modelServiceRemoteService;

    @Test
    public void modelRecommend() throws Exception {
        ModelRecommendDTO modelRecommendDTO = new ModelRecommendDTO();
        modelRecommendDTO.setModelTime(DateUtils.parseDate("20181220"));
        modelServiceRemoteService.getModelRecommendByDate(modelRecommendDTO);
    }

    @Test
    public void modelPortLevel() throws Exception {
        PortLevelDTO portLevelDTO = new PortLevelDTO();
        modelServiceRemoteService.getPortLevel(portLevelDTO);
    }

    @Resource
    private RechargeServiceRemoteService rechargeServiceRemoteService;

    @Test
    public void tradeAnalysis() throws Exception {
        rechargeServiceRemoteService.tradeAnalysisJob( null);
    }

}