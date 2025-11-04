package com.pivot.aham.api.service.remote.impl;

import com.pivot.aham.api.server.dto.res.PivotPftAssetReqDTO;
import com.pivot.aham.api.server.dto.res.PivotPftProductReqDTO;
import com.pivot.aham.common.enums.analysis.PftAssetOperateTypeEnum;
import com.pivot.aham.common.enums.analysis.PftAssetSourceEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PivotPftRemoteServiceTest {
    @Resource
    private PivotPftRemoteServiceImpl pivotPftRemoteServiceImpl;

    @Test
    public void updatePftAsset() {
        PivotPftAssetReqDTO pivotPftAssetReqDTO = new PivotPftAssetReqDTO();
        pivotPftAssetReqDTO.setConfirmMoney(new BigDecimal("1"));
        pivotPftAssetReqDTO.setConfirmShare(new BigDecimal("1"));
//        pivotPftAssetReqDTO.setDataVersion(111l);
        pivotPftAssetReqDTO.setExecuteOrderNo(44l);
        pivotPftAssetReqDTO.setPftAssetOperateType(PftAssetOperateTypeEnum.NEEDETFSHARES);
        pivotPftAssetReqDTO.setPftAssetSource(PftAssetSourceEnum.NORMALSELL);
        pivotPftAssetReqDTO.setProductCode("BNDX");
        pivotPftRemoteServiceImpl.updatePftAsset(pivotPftAssetReqDTO);
    }

    @Test
    public void getPftProductAsset() {
        PivotPftProductReqDTO pivotPftAccountReqDTO = new PivotPftProductReqDTO();
        pivotPftAccountReqDTO.setProductCode("ASX");
        pivotPftRemoteServiceImpl.getPftProductAsset(pivotPftAccountReqDTO);
    }

    @Test
    public void getPftAccountAssets() {
        pivotPftRemoteServiceImpl.getPftAccountAssets();
    }

}
