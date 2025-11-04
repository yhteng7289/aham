package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.DateTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 新加坡假日
 *
 * @author addison
 * @since 2018年12月13日
 */
@Data
@Accessors(chain=true)
@TableName(value = "t_singapore_holiday",resultMap = "SingaporeHolidayRes")
public class SingaporeHolidayPO extends BaseModel{
    private DateTypeEnum dateType;
    private Date vaDate;
}
