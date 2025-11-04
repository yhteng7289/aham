package com.pivot.aham.api.web.web.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.BankVirtualAccountDTO;
import com.pivot.aham.api.server.dto.UserBaseInfoDTO;
import com.pivot.aham.api.server.dto.UserInfoDTO;
import com.pivot.aham.common.core.base.BaseVo;
import com.pivot.aham.common.enums.CurrencyEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月01日
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "用户注册信息同步")
public class UserRegisterReqVo extends BaseVo {

    @Valid
    @ApiModelProperty(value = "用户注册信息同步列表:userBaseInfoVoList", required = true)
    private List<UserBaseInfoVo> userBaseInfoVoList;

    @Data
    @Accessors(chain = true)
    @ApiModel(value = "用户注册信息同步详情")
    public static class UserBaseInfoVo {

        @NotNull(message = "用户clientId不能为空")
        @ApiModelProperty(value = "clientId", required = true)
        private String clientId;

        @NotNull(message = "用户姓名不能为空")
        @ApiModelProperty(value = "clientName", required = true)
        private String clientName;

        @NotNull(message = "虚拟账户不能为空")
        @ApiModelProperty(value = "UOB账户,virtualAccountNo", required = true)
        private String virtualAccountNo;

        @NotNull(message = "币种不能为空")
        @ApiModelProperty(value = "UOB账户类型1:美金账户,2:新币账户", required = true)
        private CurrencyEnum currencyType;

        //@NotNull(message = "用户地址")
        @ApiModelProperty(value = "用户地址", required = false)
        private String address;

        //@NotNull(message = "用户手机号")
        @JsonProperty(value = "mobilenumber")
        @ApiModelProperty(value = "用户手机号", required = false)
        private String mobilenumber;
    }

    public List<UserBaseInfoDTO> convertToDto() {
        List<UserBaseInfoDTO> userBaseInfoDTOs = Lists.newArrayList();
        for (UserBaseInfoVo vo : userBaseInfoVoList) {
            UserInfoDTO userInfoDTO = new UserInfoDTO();
            userInfoDTO.setClientId(vo.getClientId())
                    .setClientName(vo.getClientName())
                    .setAddress(vo.getAddress())
                    .setMobileNum(vo.getMobilenumber());
            if (vo.getAddress() == null) {
                userInfoDTO.setAddress("");
            }
            if (vo.getMobilenumber() == null) {
                userInfoDTO.setMobileNum("");
            }

            BankVirtualAccountDTO virtualAccountDTO = new BankVirtualAccountDTO();
            virtualAccountDTO.setClientId(vo.getClientId())
                    .setClientName(vo.getClientName())
                    .setCurrency(vo.getCurrencyType())
                    .setCashAmount(BigDecimal.ZERO)
                    .setUsedAmount(BigDecimal.ZERO)
                    .setFreezeAmount(BigDecimal.ZERO)
                    .setVirtualAccountNo(vo.getVirtualAccountNo());

            UserBaseInfoDTO userBaseInfoDTO = new UserBaseInfoDTO();
            userBaseInfoDTO.setUserInfoDTO(userInfoDTO);
            userBaseInfoDTO.setBankVirtualAccountDTO(virtualAccountDTO);
            userBaseInfoDTOs.add(userBaseInfoDTO);
        }
        return userBaseInfoDTOs;
    }

}
