package com.pivot.aham.api.web.web.vo.req;

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
public class UpdateUserGoalInfoReqVo extends BaseVo {

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

        @NotNull(message = "投资目标名称不能为空")
        @ApiModelProperty(value = "投资目标名称:goalName", required = true)
        private String goalName;
        @NotNull(message = "clientId不能为空")
        @ApiModelProperty(value = "clientId", required = true)
        private String clientId;
    }

    public List<UserGoalInfoDTO> convertToDto() {
        List<UserGoalInfoDTO> userGoalInfoDTOList = userGoalInfoVoList.stream().map(item -> {
            UserGoalInfoDTO dto = new UserGoalInfoDTO();
            dto.setGoalId(item.getGoalsId());
            dto.setClientId(item.getClientId());
            dto.setGoalName(item.getGoalName());
            return dto;
        }).collect(Collectors.toList());
        return userGoalInfoDTOList;
    }

}
