package com.pivot.aham.api.web.web.vo.req;

import com.pivot.aham.api.server.dto.PortLevelDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * Created by luyang.li on 18/12/6.
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "收益曲线请求对象说明")
public class PortLevelReqVo {
    @ApiModelProperty(value = "模型数据请求参数:收益曲线", required = true)
    @NotBlank(message = "portfolioId 不能为空")
    private String portfolioId;


    public PortLevelDTO convertToDto(PortLevelReqVo portLevelReqVo) {
        PortLevelDTO portLevelDTO = new PortLevelDTO();
        portLevelDTO.setPortfolioId(portLevelReqVo.getPortfolioId());
        return portLevelDTO;
    }
}
