package com.pivot.aham.api.web.web.vo.req;

import com.pivot.aham.api.server.dto.PersonalInfoNotUploadDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class PersonalInfoNotUploadReqVo {
    private String lastName;
    private String firstName;
    private String mobileNum;
    private String firstQuestion;
    private String firstQuestionA;
    private String firstQuestionB;
    private String firstQuestionC;
    private String firstQuestionD;
    private String firstQuestionE;
    private String firstQuestionF;
    private String firstQuestionG;
    private String secondQuestion;
    private String thirdQuestion;
    private String thirdQuestionA;
    private String thirdQuestionB;
    private String thirdQuestionC;
    private String thirdQuestionD;
    private String fourthQuestion;
    private String fifthQuestion;
    private String sixthQuestion;
    private Integer isAgree;
    private String age;
    private String skill;
    private String basis;
    private String horizon;
    private String toleRance;
    private String stability;
    private String portfolioId;
    private List<Integer> questionsAndAnswers;

    public PersonalInfoNotUploadDTO convertToDto(PersonalInfoNotUploadReqVo personalInfoNotUploadReqVo) {
        PersonalInfoNotUploadDTO personalInfoNotUploadDTO = new PersonalInfoNotUploadDTO();
        personalInfoNotUploadDTO.setFirstName(personalInfoNotUploadReqVo.getFirstName())
                .setLastName(personalInfoNotUploadReqVo.getLastName())
                .setMobileNum(personalInfoNotUploadReqVo.getMobileNum())
                .setFirstQuestion(personalInfoNotUploadReqVo.getFirstQuestion())
                .setFirstQuestionA(personalInfoNotUploadReqVo.getFirstQuestionA())
                .setFirstQuestionB(personalInfoNotUploadReqVo.getFirstQuestionB())
                .setFirstQuestionC(personalInfoNotUploadReqVo.getFirstQuestionC())
                .setFirstQuestionD(personalInfoNotUploadReqVo.getFirstQuestionD())
                .setFirstQuestionE(personalInfoNotUploadReqVo.getFirstQuestionE())
                .setFirstQuestionF(personalInfoNotUploadReqVo.getFirstQuestionF())
                .setFirstQuestionG(personalInfoNotUploadReqVo.getFirstQuestionG())
                .setSecondQuestion(personalInfoNotUploadReqVo.getSecondQuestion())
                .setThirdQuestion(personalInfoNotUploadReqVo.getThirdQuestion())
                .setThirdQuestionA(personalInfoNotUploadReqVo.getThirdQuestionA())
                .setThirdQuestionB(personalInfoNotUploadReqVo.getThirdQuestionB())
                .setThirdQuestionC(personalInfoNotUploadReqVo.getThirdQuestionC())
                .setThirdQuestionD(personalInfoNotUploadReqVo.getThirdQuestionD())
                .setFourthQuestion(personalInfoNotUploadReqVo.getFourthQuestion())
                .setFifthQuestion(personalInfoNotUploadReqVo.getFifthQuestion())
                .setSixthQuestion(personalInfoNotUploadReqVo.getSixthQuestion())
                .setIsAgree(personalInfoNotUploadReqVo.getIsAgree())
                .setAge(personalInfoNotUploadReqVo.getAge())
                .setBasis(personalInfoNotUploadReqVo.getBasis())
                .setHorizon(personalInfoNotUploadReqVo.getHorizon())
                .setSkill(personalInfoNotUploadReqVo.getSkill())
                .setStability(personalInfoNotUploadReqVo.getStability())
                .setToleRance(personalInfoNotUploadReqVo.getToleRance())
                .setPortfolioId(personalInfoNotUploadReqVo.getPortfolioId())
                .setQuestionsAndAnswers(personalInfoNotUploadReqVo.getQuestionsAndAnswers());
        return personalInfoNotUploadDTO;
    }
}
