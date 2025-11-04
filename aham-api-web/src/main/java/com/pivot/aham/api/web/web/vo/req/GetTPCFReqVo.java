package com.pivot.aham.api.web.web.vo.req;

import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 请填写类注释
 *
 * @author dexter
 * @since 20/4/2020
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "get TPCF")
public class GetTPCFReqVo extends BaseVo {

    @Valid
    @ApiModelProperty(value = "get TPCF:tpcfReqVo", required = true)
    private TPCFReqVo tpcfReqVo;

    @Data
    @Accessors(chain = true)
    @ApiModel(value = "get TPCF")
    public static class TPCFReqVo {

        @NotNull(message = "date to get TPCF must not be null.")
        @ApiModelProperty(value = "checkDate", required = true)
        private String checkDate;
        private int current;
        private int size;

    }

    public String getDate() {

        return tpcfReqVo.getCheckDate();
    }

    public int getCurrent() {

        return tpcfReqVo.getCurrent();

    }

    public int getSize() {

        return tpcfReqVo.getSize();

    }

}
