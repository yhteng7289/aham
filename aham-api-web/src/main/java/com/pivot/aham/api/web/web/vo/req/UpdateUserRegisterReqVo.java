package com.pivot.aham.api.web.web.vo.req;

import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.BankVirtualAccountDTO;
import com.pivot.aham.api.server.dto.UserBaseInfoDTO;
import com.pivot.aham.api.server.dto.UserInfoDTO;
import com.pivot.aham.common.core.base.BaseVo;
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
public class UpdateUserRegisterReqVo extends BaseVo {

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

        @NotNull(message = "用户地址")
        @ApiModelProperty(value = "用户地址", required = true)
        private String address;

        @NotNull(message = "用户手机号")
        @ApiModelProperty(value = "用户手机号", required = true)
        private String mobilenumber;
    }

    public List<UserBaseInfoDTO> convertToDto() {
        List<UserBaseInfoDTO> userBaseInfoDTOs = Lists.newArrayList();
        for (UserBaseInfoVo vo : userBaseInfoVoList) {
            UserInfoDTO userInfoDTO = new UserInfoDTO();
            userInfoDTO.setClientId(vo.getClientId())
                        .setAddress(vo.getAddress())
                        .setMobileNum(vo.getMobilenumber());   

            BankVirtualAccountDTO virtualAccountDTO = new BankVirtualAccountDTO();
            virtualAccountDTO.setClientId(vo.getClientId())
                    .setCashAmount(BigDecimal.ZERO)
                    .setUsedAmount(BigDecimal.ZERO)
                    .setFreezeAmount(BigDecimal.ZERO);

            UserBaseInfoDTO userBaseInfoDTO = new UserBaseInfoDTO();
            userBaseInfoDTO.setUserInfoDTO(userInfoDTO);
            userBaseInfoDTO.setBankVirtualAccountDTO(virtualAccountDTO);
            userBaseInfoDTOs.add(userBaseInfoDTO);
        }
        return userBaseInfoDTOs;
    }

}
