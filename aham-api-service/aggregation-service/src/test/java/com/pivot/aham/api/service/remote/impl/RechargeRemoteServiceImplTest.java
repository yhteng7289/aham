package com.pivot.aham.api.service.remote.impl;

import com.beust.jcommander.internal.Lists;
import com.pivot.aham.api.server.dto.UobTransferToSaxoCallbackDTO;
import com.pivot.aham.api.server.remoteservice.RechargeServiceRemoteService;
import com.pivot.aham.common.enums.TransferStatusEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by luyang.li on 19/1/7.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RechargeRemoteServiceImplTest {

    @Resource
    private RechargeServiceRemoteService rechargeRemoteService;

    @Test
    public void transferToStockExchangeCallback() throws Exception {
        List<UobTransferToSaxoCallbackDTO> params = Lists.newArrayList();

        UobTransferToSaxoCallbackDTO dto = new UobTransferToSaxoCallbackDTO();
//        dto.setConfirmMoney(new BigDecimal("1000"));
//        dto.setConfirmShare(new BigDecimal("1000"));
        dto.setOrderNo(1082130122171023362L);
        dto.setTransferStatus(TransferStatusEnum.SUCCESS);
        params.add(dto);

        rechargeRemoteService.rechargeUobTransferToSaxoCallback(params);
    }

    @Test
    public void virtualAccountOfflineTransfer() throws Exception {

    }

}