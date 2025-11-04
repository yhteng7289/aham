package com.pivot.aham.api.web.web.vo.req;

import com.pivot.aham.api.server.dto.PersonalInfoUploadDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class PersonalInfoUploadReqVo {

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

    public PersonalInfoUploadDTO convertToDto(PersonalInfoUploadReqVo personalInfoUploadReqVo) {
        PersonalInfoUploadDTO personalInfoUploadDTO = new PersonalInfoUploadDTO();
        personalInfoUploadDTO.setTitle(personalInfoUploadReqVo.getTitle())
                .setLastName(personalInfoUploadReqVo.getLastName())
                .setFirstName(personalInfoUploadReqVo.getFirstName())
                .setNationality(personalInfoUploadReqVo.getNationality())
                .setIdNo(personalInfoUploadReqVo.getIdNo())
                .setIsSingGaPorePR(personalInfoUploadReqVo.getIsSingGaPorePR())
                .setIsUploadID(personalInfoUploadReqVo.getIsUploadID())
                .setGender(personalInfoUploadReqVo.getGender())
                .setBirthday(personalInfoUploadReqVo.getBirthday())
                .setEmailAddress(personalInfoUploadReqVo.getEmailAddress())
                .setMobileNum(personalInfoUploadReqVo.getMobileNum())
                .setHomeNum(personalInfoUploadReqVo.getHomeNum())
                .setCountry(personalInfoUploadReqVo.getCountry())
                .setResidentialAddress(personalInfoUploadReqVo.getResidentialAddress())
                .setPostalCode(personalInfoUploadReqVo.getPostalCode())
                .setIsAgree(personalInfoUploadReqVo.getIsAgree());
        return personalInfoUploadDTO;
    }
}
