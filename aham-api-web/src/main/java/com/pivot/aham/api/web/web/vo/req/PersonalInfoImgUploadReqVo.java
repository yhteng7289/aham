package com.pivot.aham.api.web.web.vo.req;

import com.pivot.aham.api.server.dto.PersonalInfoUploadDTO;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
//@Accessors(chain = true)
public class PersonalInfoImgUploadReqVo {


    private MultipartFile frontImg;
    private MultipartFile backImg;
    private MultipartFile passPortImg;

    public PersonalInfoUploadDTO convertToDto(PersonalInfoImgUploadReqVo personalInfoUploadReqVo) {
        PersonalInfoUploadDTO personalInfoUploadDTO = new PersonalInfoUploadDTO();
//        personalInfoUploadDTO.setFrontImg(personalInfoUploadReqVo.getFrontImg());
//        personalInfoUploadDTO.setBackImg(personalInfoUploadReqVo.getBackImg());
//        personalInfoUploadDTO.setPassPortImg(personalInfoUploadReqVo.getPassPortImg());
        return personalInfoUploadDTO;
    }
}
