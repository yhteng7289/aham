package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName(value = "t_user_cust_statement",resultMap = "UserCustStatementRes")
public class UserCustStatementPO  extends BaseModel {
    private String clientId;
    private Date staticDate;
    private String pdfUrl;
}
