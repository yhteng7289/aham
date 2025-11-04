package com.pivot.aham.api.web.web.vo;

import com.alibaba.fastjson.JSON;
import com.beust.jcommander.internal.Lists;
import com.pivot.aham.api.server.dto.UserSetGoalMoneyDTO;
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
import java.util.stream.Collectors;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月01日
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "用户在goal上分配金额")
public class UserSetGoalMoneyReqVo extends BaseVo {

    @Valid
    @ApiModelProperty(value = "用户在goal上分配金额列表:userSetGoalMoneyVoList", required = true)
    List<UserSetGoalMoneyVo> userSetGoalMoneyVoList;

    public List<UserSetGoalMoneyDTO> convertToDto() {
        List<UserSetGoalMoneyDTO> userSetGoalMoneyDTOs = userSetGoalMoneyVoList.stream().map(item -> {
            UserSetGoalMoneyDTO dto = new UserSetGoalMoneyDTO();
            dto.setGoalId(item.getGoalsId())
                    .setMoney(item.getMoney())
                    .setClientId(item.getClientId())
                    .setCurrencyType(item.getCurrencyType());
            return dto;
        }).collect(Collectors.toList());
        return userSetGoalMoneyDTOs;
    }

    @Data
    @Accessors(chain = true)
    @ApiModel(value = "用户在goal上分配金额,详情")
    public static class UserSetGoalMoneyVo {
        @NotNull(message = "Investment objective cannot be empty")
        @ApiModelProperty(value = "Investment objective: goalsId", required = true)
        private String goalsId;

        @NotNull(message = "clientId cannot be empty")
        @ApiModelProperty(value = "clientId", required = true)
        private String clientId;

        @NotNull(message = "Amount allocated to goal cannot be empty")
        @ApiModelProperty(value = "Allocated amount in goal:money", required = true)
        private BigDecimal money = BigDecimal.ZERO;
        
//        @NotNull(message = "Portfolio Id cannot be empty")
//        @ApiModelProperty(value = "portfolioId", required = true)
//        private String portfolioId;

        //@NotNull(message = "Currency cannot be empty")
        @ApiModelProperty(value = "Currency of credited fund:currencyType", required = true)
        private CurrencyEnum currencyType;

    }

    public static void main(String[] args) {
        UserSetGoalMoneyReqVo vo = new UserSetGoalMoneyReqVo();
        List<UserSetGoalMoneyVo> userSetGoalMoneyVoList = Lists.newArrayList();

        UserSetGoalMoneyVo userSetGoalMoneyVo = new UserSetGoalMoneyVo();
        userSetGoalMoneyVo.setMoney(new BigDecimal("1000"));
        userSetGoalMoneyVo.setClientId("0000001");
        userSetGoalMoneyVo.setCurrencyType(CurrencyEnum.MYR);
        userSetGoalMoneyVo.setGoalsId("00001");

        userSetGoalMoneyVoList.add(userSetGoalMoneyVo);
        vo.setUserSetGoalMoneyVoList(userSetGoalMoneyVoList);
        System.out.println(JSON.toJSON(vo));
    }
}
