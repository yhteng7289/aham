package com.pivot.aham.api.web.withdrawal;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月11日
 */

import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.server.dto.EtfCallbackDTO;
import com.pivot.aham.api.server.remoteservice.AssetServiceRemoteService;
import com.pivot.aham.api.web.web.vo.req.WithdrawalFromGoalReqVo;
import com.pivot.aham.api.web.web.vo.req.WithdrawalFromVirtalAccountReqVo;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.TransferStatusEnum;
import com.pivot.aham.common.enums.analysis.WithdrawalTargetBankTypeEnum;
import com.pivot.aham.common.enums.analysis.WithdrawalTargetTypeEnum;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月11日
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WithdrawalController {

    @Resource
    private AssetServiceRemoteService assetServiceRemoteService;

    @Test
    public void withdrawalFromVirtualAccount() {
        WithdrawalFromVirtalAccountReqVo withdrawalFromVirtalAccountReqVo = new WithdrawalFromVirtalAccountReqVo();
        withdrawalFromVirtalAccountReqVo.setApplyMoney(new BigDecimal(1000));
        withdrawalFromVirtalAccountReqVo.setBankAccountNo("622003930");
//        withdrawalFromVirtalAccountReqVo.setBankCode("6010");
        withdrawalFromVirtalAccountReqVo.setBankName("大华银行");
        withdrawalFromVirtalAccountReqVo.setClientId("c123456");
        withdrawalFromVirtalAccountReqVo.setSourceAccountType(CurrencyEnum.SGD);
        withdrawalFromVirtalAccountReqVo.setSwift("sw123");
        withdrawalFromVirtalAccountReqVo.setBranch("bx123");
        withdrawalFromVirtalAccountReqVo.setTargetCurrency(CurrencyEnum.SGD);
        withdrawalFromVirtalAccountReqVo.setWithdrawalTargetBankType(WithdrawalTargetBankTypeEnum.OVERSEA);

        System.out.println(JSON.toJSON(withdrawalFromVirtalAccountReqVo));
    }

    @Test
    public void withdrawalFromGoal() {
        WithdrawalFromGoalReqVo withdrawalFromGoalReqVo = new WithdrawalFromGoalReqVo();
        withdrawalFromGoalReqVo.setApplyMoney(new BigDecimal("1000"));
        withdrawalFromGoalReqVo.setBankAccountNo("622003930");
        withdrawalFromGoalReqVo.setBankName("大华银行");
        withdrawalFromGoalReqVo.setClientId("c123456");
        withdrawalFromGoalReqVo.setGoalId("g123");
        withdrawalFromGoalReqVo.setSwift("sw123");
        withdrawalFromGoalReqVo.setBranch("bx123");
        withdrawalFromGoalReqVo.setTargetBankType(WithdrawalTargetBankTypeEnum.OVERSEA);
        withdrawalFromGoalReqVo.setTargetCurrency(CurrencyEnum.SGD);
        withdrawalFromGoalReqVo.setTargetType(WithdrawalTargetTypeEnum.SquirrelCashAccount);

        System.out.println(JSON.toJSON(withdrawalFromGoalReqVo));
    }

    @Test
    public void testEtfConfirm() {
        List<EtfCallbackDTO> params = Lists.newArrayList();
        EtfCallbackDTO buyEtfCallbackDTO = new EtfCallbackDTO();
        buyEtfCallbackDTO.setAccountId(1L);
        buyEtfCallbackDTO.setConfirmMoney(new BigDecimal("450"));
        buyEtfCallbackDTO.setConfirmShare(new BigDecimal("450"));
        buyEtfCallbackDTO.setConfirmTime(DateUtils.now());
        buyEtfCallbackDTO.setProductCode("SHV");
        buyEtfCallbackDTO.setTmpOrderId(1084840540949966849L);
        buyEtfCallbackDTO.setTransCost(new BigDecimal("10"));
        buyEtfCallbackDTO.setTransferStatus(TransferStatusEnum.SUCCESS);

        EtfCallbackDTO buyEtfCallbackDTO1 = new EtfCallbackDTO();
        buyEtfCallbackDTO1.setAccountId(1L);
        buyEtfCallbackDTO1.setConfirmMoney(new BigDecimal("450"));
        buyEtfCallbackDTO1.setConfirmShare(new BigDecimal("450"));
        buyEtfCallbackDTO1.setConfirmTime(DateUtils.now());
        buyEtfCallbackDTO1.setProductCode("EMB");
        buyEtfCallbackDTO1.setTmpOrderId(1084840542984204289L);
        buyEtfCallbackDTO1.setTransCost(new BigDecimal("10"));
        buyEtfCallbackDTO1.setTransferStatus(TransferStatusEnum.SUCCESS);

        params.add(buyEtfCallbackDTO);
        params.add(buyEtfCallbackDTO1);
        assetServiceRemoteService.etfCallBack(params);
    }

}
