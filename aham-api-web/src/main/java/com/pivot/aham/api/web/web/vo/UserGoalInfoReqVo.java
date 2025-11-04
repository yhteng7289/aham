package com.pivot.aham.api.web.web.vo;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.UserGoalInfoDTO;
import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
public class UserGoalInfoReqVo extends BaseVo {

    @Valid
    @ApiModelProperty(value = "用户goal列表:userGoalInfoList", required = true)
    private List<UserGoalInfoVo> userGoalInfoVoList;

    @Data
    @Accessors(chain = true)
    @ApiModel(value = "用户goal详情")
    public static class UserGoalInfoVo {

        @NotNull(message = "投资目标不能为空")
        @ApiModelProperty(value = "投资目标:goalsId", required = true)
        private String goalsId;

//        @NotNull(message = "投资目标名称不能为空")
        @ApiModelProperty(value = "投资目标名称:goalName", required = false)
        private String goalName;

        @NotNull(message = "方案标识不能为空")
        @ApiModelProperty(value = "方案标识:portfolioId", required = true)
        private String portfolioId;

        @NotNull(message = "referenceCode不能为空")
        @ApiModelProperty(value = "银行转账使用的code:referenceCode", required = true)
        private String referenceCode;

        @NotNull(message = "clientId不能为空")
        @ApiModelProperty(value = "clientId", required = true)
        private String clientId;
    }

    public List<UserGoalInfoDTO> convertToDto() {
        List<UserGoalInfoDTO> userGoalInfoDTOList = userGoalInfoVoList.stream().map(item -> {
            UserGoalInfoDTO dto = new UserGoalInfoDTO();
            dto.setGoalId(item.getGoalsId())
                    .setClientId(item.getClientId())
                    .setGoalName(item.getGoalName())
                    .setPortfolioId(item.getPortfolioId())
                    .setReferenceCode(item.getReferenceCode());

            if (item.getGoalName() == null) {
                dto.setGoalName(item.getGoalsId());
            }

            return dto;
        }).collect(Collectors.toList());
        return userGoalInfoDTOList;
    }

    public static void main(String[] args) {
        List<UserGoalInfoVo> userGoalInfoVoList = Lists.newArrayList();
        UserGoalInfoVo vo = new UserGoalInfoVo();
        vo.setPortfolioId("P1R1A1");
        vo.setClientId("string");
        vo.setGoalsId("Test1001");
        vo.setReferenceCode("test1001");

        userGoalInfoVoList.add(vo);
        System.out.println(JSON.toJSON(userGoalInfoVoList));
    }

}
