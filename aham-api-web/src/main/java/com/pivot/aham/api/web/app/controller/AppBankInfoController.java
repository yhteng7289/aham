package com.pivot.aham.api.web.app.controller;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.web.app.dto.reqdto.BankInfoDTO;
import com.pivot.aham.api.web.app.dto.resdto.BankInfoResDTO;
import com.pivot.aham.api.web.app.dto.resdto.UserBankDetailResDTO;
import com.pivot.aham.api.web.app.febase.AppResultCode;
import com.pivot.aham.api.web.app.service.AppService;
import com.pivot.aham.api.web.app.vo.req.BankInfoReqVo;
import com.pivot.aham.api.web.app.vo.res.BankInfoResVo;
import com.pivot.aham.api.web.app.vo.res.CountryBankListResVo;
import com.pivot.aham.api.web.app.vo.res.UserBankDetailResVo;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YYYz
 */
@RestController
@RequestMapping("/api/v1/")
@Api(value = "银行列表信息", description = "银行列表信息接口")
public class AppBankInfoController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppWithdrawController.class);

    @Resource
    private AppService appService;

    @PostMapping("app/getBankList")
    @ApiOperation(value = "银行列表", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message<BankInfoResVo> getBankList(@RequestBody BankInfoReqVo bankInfoReqVo) throws Exception {
        if(!checkLogin(bankInfoReqVo.getClientId())){
            return Message.error(AppResultCode.UNAUTHORIZED.value(),AppResultCode.UNAUTHORIZED.msg());
        }
        LOGGER.info("用户资产,请求参数bankInfoReqVo:{}", JSON.toJSON(bankInfoReqVo));
        BankInfoDTO bankInfoDTO = bankInfoReqVo.convertToDto(bankInfoReqVo);
        BankInfoResDTO bankInfoResDTO = appService.getBankList(bankInfoDTO);
        if (bankInfoResDTO != null) {
            BankInfoResVo bankInfoResVo = new BankInfoResVo();
            bankInfoResVo.setUserBankDetailListVo(convertToVo(bankInfoResDTO.getUserBankDetailVo()))
            .setClientId(bankInfoResDTO.getClientId())
            .setCountryBankList(buildCountryBankList(convertToVo(bankInfoResDTO.getUserBankDetailVo())));
            return Message.success(bankInfoResVo);
        } else {
            return Message.error("请求失败");
        }
    }

    private List<CountryBankListResVo> buildCountryBankList(List<UserBankDetailResVo> userBankDetailResVos) {
        List<CountryBankListResVo> countryBankListResVos = Lists.newArrayList();
        Map<String, List<UserBankDetailResVo>> map = new HashMap<>();
        for (UserBankDetailResVo userBankDetailResVo : userBankDetailResVos) {
            if (map.get(userBankDetailResVo.getCountry()) == null) {
                List<UserBankDetailResVo> list = Lists.newArrayList();
                list.add(userBankDetailResVo);
                map.put(userBankDetailResVo.getCountry(), list);
            } else {
                List<UserBankDetailResVo> userOrdersResVoList = map.get(userBankDetailResVo.getCountry());
                userOrdersResVoList.add(userBankDetailResVo);
            }
        }
        for (String country : map.keySet()) {
            CountryBankListResVo countryBankListResVo = new CountryBankListResVo();
            countryBankListResVo.setCountry(country);
            countryBankListResVo.setBankList(map.get(country));
            countryBankListResVos.add(countryBankListResVo);
        }
        return countryBankListResVos;
    }

    public List<UserBankDetailResVo> convertToVo(List<UserBankDetailResDTO> userBankDetailResDTOList) {
        List<UserBankDetailResVo> userBankDetailResVos = Lists.newArrayList();
        for (UserBankDetailResDTO userBankDetailResDTO : userBankDetailResDTOList) {
            UserBankDetailResVo userBankDetailResVo = new UserBankDetailResVo();
            String country = userBankDetailResDTO.getCountry() == null ? "" : userBankDetailResDTO.getCountry();
            String bankAccNo = userBankDetailResDTO.getBankAcctNo() == null ? "" : userBankDetailResDTO.getBankAcctNo();
            String bankCode = userBankDetailResDTO.getBankCode() == null ? "" : userBankDetailResDTO.getBankCode();;
            String bankAccountName = userBankDetailResDTO.getAccountName() == null ? "" : userBankDetailResDTO.getAccountName();
            String bankName = userBankDetailResDTO.getBankName() == null ? "" : userBankDetailResDTO.getBankName();

            userBankDetailResVo.setCountry(country.trim())
                    .setBankAcctNo(bankAccNo.trim())
                    .setBankCode(bankCode.trim())
                    .setAccountName(bankAccountName.trim())
                    .setBankName(bankName.trim());
            userBankDetailResVos.add(userBankDetailResVo);
        }
        return userBankDetailResVos;
    }
}
