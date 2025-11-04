package com.pivot.aham.common.core.base;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年11月26日
 */
@Data
@Accessors(chain = true)
public class BaseDTO implements Serializable {

    private Long id;
    private Date createTime;
    private Date updateTime;
}
