package com.pivot.aham.api.web.web.vo.res;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "做游戏返回portfolioId")
public class UserTestResVo {
    private String portfolioId;
}
