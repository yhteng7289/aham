package com.pivot.aham.common.core.base;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 实体基类
 *
 * @author addison
 * @since 2018年11月15日
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Accessors(chain = true)
public abstract class BaseModel implements Serializable {

    private Long id;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Integer useEnable;
    @ApiModelProperty(hidden = true)
    private Date createTime;
    @ApiModelProperty(hidden = true)
    private Date updateTime;

    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String keyword;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String orderBy;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<Long> ids;

}
