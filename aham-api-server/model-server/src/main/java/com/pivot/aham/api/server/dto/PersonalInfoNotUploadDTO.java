package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class PersonalInfoNotUploadDTO extends BaseDTO {

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

    public String toStringWithSeperator(String seperator) {
        StringBuffer str = new StringBuffer();
        str.append(this.firstQuestion).append(seperator);
        str.append(this.firstQuestionA).append(seperator);
        str.append(this.firstQuestionB).append(seperator);
        str.append(this.firstQuestionC).append(seperator);
        str.append(this.firstQuestionD).append(seperator);
        str.append(this.firstQuestionE).append(seperator);
        str.append(this.firstQuestionF).append(seperator);
        str.append(this.firstQuestionG).append(seperator);
        str.append(this.secondQuestion).append(seperator);
        str.append(this.thirdQuestion).append(seperator);
        str.append(this.thirdQuestionA).append(seperator);
        str.append(this.thirdQuestionB).append(seperator);
        str.append(this.thirdQuestionC).append(seperator);
        str.append(this.thirdQuestionD).append(seperator);
        str.append(this.fourthQuestion).append(seperator);
        str.append(this.fifthQuestion).append(seperator);
        str.append(this.sixthQuestion).append(seperator);
        return str.toString();
    }
}
