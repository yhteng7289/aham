package com.pivot.aham.api.service.remote.impl;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.server.dto.ModelRecommendDTO;
import com.pivot.aham.api.server.dto.ModelRecommendResDTO;
import com.pivot.aham.api.server.dto.PortLevelDTO;
import com.pivot.aham.api.server.dto.PortLevelResDTO;
import com.pivot.aham.common.core.util.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.List;

/**
 * Created by luyang.li on 18/12/26.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ModelServiceRemoteServiceImplTest {

    @Resource
    private ModelServiceRemoteServiceImpl modelServiceRemoteServiceImpl;

    @Test
    public void getAllValidRecommend() throws Exception {

    }

    @Test
    public void getValidRecommendByPortfolioId() throws Exception {

    }

    @Test
    public void getModelRecommend() throws Exception {
        ModelRecommendDTO modelRecommendDTO = new ModelRecommendDTO();
        modelRecommendDTO.setModelTime(DateUtils.parseDate("20181220"));
        List<ModelRecommendResDTO> resDTOs = modelServiceRemoteServiceImpl.getModelRecommendByDate(modelRecommendDTO);
        System.out.println(JSON.toJSON(resDTOs));
    }

    @Test
    public void synchroModelRecommend() throws Exception {

    }

    @Test
    public void getPortLevel() throws Exception {
        List<PortLevelResDTO> portLevelDTOs =  modelServiceRemoteServiceImpl.getPortLevel(new PortLevelDTO());
        System.out.println(portLevelDTOs);
    }

    @Test
    public void queryValidModelByPortfolioId() throws Exception {

    }

    @Test
    public void queryAllProductInfo() throws Exception {

    }

    @Test
    public void queryById(){
        modelServiceRemoteServiceImpl.getModelRecommendById(1L);
    }

}