package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PersonalInfoUploadDTO extends BaseDTO {

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
    private Integer isAgree;

    public String toStringWithSeperator(String seperator) {
        StringBuffer str = new StringBuffer();
        str.append(this.title).append(seperator);
        str.append(this.lastName).append(seperator);
        str.append(this.firstName).append(seperator);
        str.append(this.nationality).append(seperator);
        //防止长度过长，excel打开转换为科学计数法
        str.append(this.idNo + "\t").append(seperator);
        str.append(this.isSingGaPorePR).append(seperator);
        str.append(this.isUploadID).append(seperator);
        str.append(this.gender).append(seperator);
        str.append(this.birthday).append(seperator);
        str.append(this.emailAddress).append(seperator);
        str.append(this.mobileNum + "\t").append(seperator);
        str.append(this.homeNum + "\t").append(seperator);
        str.append(this.country).append(seperator);
        str.append(this.residentialAddress).append(seperator);
        str.append(this.postalCode).append(seperator);
        return str.toString();
    }
}
