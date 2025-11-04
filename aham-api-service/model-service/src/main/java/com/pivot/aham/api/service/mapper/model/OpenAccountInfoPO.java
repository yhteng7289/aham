package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class OpenAccountInfoPO extends BaseModel{
    private String title;
    private String lastName;
    private String firstName;
    private String nationality;
    private String idNo;
    private String isSingGaPorePR;
    private String isUploadID;
    private String gender;
    private String birthday;
    private String emailAddress;
    private String mobileNum;
    private String homeNum;
    private String country;
    private String residentialAddress;
    private String postalCode;
    private Integer status;
}
